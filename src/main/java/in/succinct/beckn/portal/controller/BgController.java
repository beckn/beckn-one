package in.succinct.beckn.portal.controller;

import com.venky.core.string.StringUtil;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.controller.Controller;
import com.venky.swf.controller.annotations.RequireLogin;
import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.integration.api.Call;
import com.venky.swf.integration.api.HttpMethod;
import com.venky.swf.integration.api.InputFormat;
import com.venky.swf.path.Path;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.TaskManager;
import com.venky.swf.routing.Config;
import com.venky.swf.views.BytesView;
import com.venky.swf.views.View;
import in.succinct.beckn.Acknowledgement;
import in.succinct.beckn.Acknowledgement.Status;
import in.succinct.beckn.Circle;
import in.succinct.beckn.Context;
import in.succinct.beckn.Error;
import in.succinct.beckn.Fulfillment;
import in.succinct.beckn.Fulfillment.FulfillmentType;
import in.succinct.beckn.FulfillmentStop;
import in.succinct.beckn.Intent;
import in.succinct.beckn.Location;
import in.succinct.beckn.Request;
import in.succinct.beckn.Response;
import in.succinct.beckn.User;
import in.succinct.beckn.portal.util.DomainMapper;
import in.succinct.beckn.registry.db.model.Subscriber;
import in.succinct.beckn.registry.db.model.onboarding.NetworkDomain;
import in.succinct.beckn.registry.db.model.onboarding.NetworkParticipant;
import in.succinct.beckn.registry.db.model.onboarding.NetworkRole;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BgController extends Controller {
    public BgController(Path path) {
        super(path);
    }
    public View nack(Request request, String realm){
        Acknowledgement nack = new Acknowledgement(Status.NACK);
        return new BytesView(getPath(),
                new Response(request.getContext(),new Acknowledgement(Status.NACK)).toString().getBytes(StandardCharsets.UTF_8),
                MimeType.APPLICATION_JSON,"WWW-Authenticate","Signature realm=\""+realm+"\"",
                "headers=\"(created) (expires) digest\""){
            @Override
            public void write() throws IOException {
                super.write(HttpServletResponse.SC_UNAUTHORIZED);
            }
        };
    }
    public View ack(Request request){
        Acknowledgement ack = new Acknowledgement(Status.ACK);
        return new BytesView(getPath(),new Response(request.getContext(),ack).toString().getBytes(StandardCharsets.UTF_8) , MimeType.APPLICATION_JSON);
    }


    private View act(){
        Request request = null;
        try {
            request = new Request(StringUtil.read(getPath().getInputStream()));
            List<Task> tasks = new ArrayList<>();
            if (!Config.instance().getBooleanProperty("beckn.auth.enabled", false)  ||
                    request.verifySignature("Authorization",getPath().getHeaders())){
                Context context = request.getContext();
                if ("search".equals(request.getContext().getAction())){
                    Subscriber criteria = getCriteria(request);
                    criteria.setType(NetworkRole.SUBSCRIBER_TYPE_BPP);
                    List<Subscriber> subscriberList ;

                    if (!ObjectUtil.isVoid(context.getBppId())){
                        criteria.setSubscriberId(context.getBppId());
                    }

                    subscriberList = Subscriber.lookup(criteria,0);
                    for (Subscriber subscriber : subscriberList){
                        tasks.add(new Search(request,subscriber,getPath().getHeaders()));
                    }
                }else if ("on_search".equals(request.getContext().getAction())){
                    Subscriber criteria = getCriteria(request);
                    criteria.setType(NetworkRole.SUBSCRIBER_TYPE_BAP);
                    if (!ObjectUtil.isVoid(context.getBapId())){
                        criteria.setSubscriberId(context.getBapId());
                    }else  {
                        throw new RuntimeException("BAP not known!");
                    }
                    List<Subscriber> subscriberList = Subscriber.lookup(criteria,0);
                    for (Subscriber subscriber : subscriberList){
                        tasks.add(new OnSearch(request,subscriber,getPath().getHeaders()));
                    }
                }
                TaskManager.instance().executeAsync(tasks,false);
                return ack(request);
            }else {
                return nack(request,request.getContext().getBapId());
            }
        }catch (Exception ex){
            if (request == null){
                throw new RuntimeException();
            }
            Request response  = new Request();
            Error error = new Error();
            response.setContext(request.getContext());
            response.setError(error);
            error.setCode(ex.getMessage());
            StringWriter message = new StringWriter();
            ex.printStackTrace(new PrintWriter(message));
            error.setMessage(message.toString());
            return new BytesView(getPath(),response.toString().getBytes(StandardCharsets.UTF_8),MimeType.APPLICATION_JSON);
        }
    }

    private Circle getDeliveryRegion(Request request) {
        Context context = request.getContext();
        User user = context.getUser();
        List<Location> possibleLocations = new ArrayList<>();
        Intent intent = request.getMessage().getIntent();
        if (intent == null) {
            return null;
        }
        Fulfillment fulfillment = intent.getFulfillment();
        if (fulfillment != null) {
            FulfillmentStop end = fulfillment.getEnd();
            if (end != null) {
                Location location = end.getLocation();
                if (location != null) {
                    possibleLocations.add(location);
                }
            }
        }
        if (user != null){
            Location location = user.getLocation();
            if (location != null) {
                possibleLocations.add(location);
            }
        }

        Circle region = new Circle();
        for (Location location : possibleLocations){
            Circle c = location.getCircle() ;
            if (c != null){
                region.setGps(c.getGps());
                region.setRadius(c.getRadius());
            }
            if (region.getGps() == null){
                region.setGps(location.getGps());
            }
            if  (region.getGps() != null){
                break;
            }
        }
        if (region.getGps() == null){
            return null;
        }else if (region.getRadius() <= 0){
            if (fulfillment.getType() == FulfillmentType.store_pickup){
                region.setRadius(50.0D);
            }else {
                region.setRadius(0.0D);
            }
        }
        return region;
    }

    private Subscriber getCriteria(Request request) {
        Subscriber criteria = Database.getTable(Subscriber.class).newRecord();
        Context context = request.getContext();
        String countryCode = context.get("country");
        String cityCode = context.get("city");
        if (countryCode != null){
            criteria.setCountry(countryCode);
        }
        if (!ObjectUtil.isVoid(cityCode)){
            criteria.setCity(cityCode);
        }
        criteria.setDomain(context.getDomain());
        criteria.setStatus(NetworkRole.SUBSCRIBER_STATUS_SUBSCRIBED);
        Circle deliveryRegion = getDeliveryRegion(request);
        if (deliveryRegion != null ){
            criteria.setLat(deliveryRegion.getGps().getLat());
            criteria.setLng(deliveryRegion.getGps().getLng());
            criteria.setRadius(deliveryRegion.getRadius());
        }

        return criteria;
    }

    @RequireLogin(false)
    public View search() {
        return act();
    }

    @RequireLogin(false)
    public View on_search() {
        return act();
    }
    protected static Map<String, String> getHeaders(Request request) {
        Map<String,String> headers  = new HashMap<>();
        if (Config.instance().getBooleanProperty("beckn.auth.enabled", false)) {

            String subscriberId = getSelfSubscription().getSubscriberId();
            headers.put("X-Gateway-Authorization", request.generateAuthorizationHeader(subscriberId,
                    NetworkRole.find(subscriberId).getNetworkParticipant().getParticipantKeys().get(0).getKeyId()));
        }
        headers.put("Content-Type", MimeType.APPLICATION_JSON.toString());
        headers.put("Accept", MimeType.APPLICATION_JSON.toString());

        return headers;
    }
    private static NetworkRole getSelfSubscription() {
        NetworkParticipant participant = NetworkParticipant.find(Config.instance().getHostName());
        NetworkRole role = Database.getTable(NetworkRole.class).newRecord();
        role.setNetworkParticipantId(participant.getId());
        role.setType(NetworkRole.SUBSCRIBER_TYPE_BG);
        role = Database.getTable(NetworkRole.class).getRefreshed(role);

        if (role.getRawRecord().isNewRecord()){
            return null;
        }else if (!ObjectUtil.equals(role.getStatus(),NetworkRole.SUBSCRIBER_STATUS_SUBSCRIBED)){
            return null;
        }else{
            return role;
        }

    }

    public static class Search implements Task {
        Request originalRequest;
        Subscriber bpp ;
        Map<String,String> headers;
        public Search(Request request, Subscriber bpp, Map<String, String> headers){
            this.originalRequest = request;
            this.bpp = bpp;
            this.headers = headers;
        }

        @Override
        public void execute() {
            Request clone = new Request(originalRequest.toString());

            Call<InputStream> call = new Call<InputStream>().url(bpp.getSubscriberUrl()+ "/"+clone.getContext().getAction()).
                    method(HttpMethod.POST).inputFormat(InputFormat.INPUT_STREAM).
                    input(new ByteArrayInputStream(clone.toString().getBytes(StandardCharsets.UTF_8))).headers(getHeaders(clone));

            if (headers != null && headers.containsKey("Authorization")){
                call.header("Authorization",headers.get("Authorization"));
            }
            call.getResponseAsJson();
        }
    }
    public static class OnSearch implements Task {
        Request originalRequest;
        Subscriber bap ;
        Map<String,String> headers;
        public OnSearch(Request request, Subscriber bap, Map<String, String> headers){
            this.originalRequest = request;
            this.bap = bap;
            this.headers = headers;
        }
        @Override
        public void execute() {
            Request clone = new Request(originalRequest.toString());

            Call<InputStream> call = new Call<InputStream>().url(bap.getSubscriberUrl()+ "/"+clone.getContext().getAction()).
                    method(HttpMethod.POST).inputFormat(InputFormat.INPUT_STREAM).
                    input(new ByteArrayInputStream(clone.toString().getBytes(StandardCharsets.UTF_8))).headers(getHeaders(clone));
            if (this.headers != null && headers.containsKey("Authorization")){
                call.header("Authorization",headers.get("Authorization"));
            }
            call.getResponseAsJson();
        }
    }

}
