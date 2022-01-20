package in.succinct.beckn.portal.db.model.api;

import com.venky.core.collections.IgnoreCaseMap;
import com.venky.core.string.StringUtil;
import com.venky.core.util.ObjectHolder;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.db.model.io.json.JSONFormatter;
import com.venky.swf.db.table.ModelImpl;
import com.venky.swf.integration.api.Call;
import com.venky.swf.integration.api.HttpMethod;
import com.venky.swf.integration.api.InputFormat;
import com.venky.swf.plugins.background.messaging.MessageAdaptor;
import com.venky.swf.plugins.background.messaging.MessageAdaptor.CloudEventHandler;
import com.venky.swf.plugins.background.messaging.MessageAdaptor.SubscriptionHandle;
import com.venky.swf.plugins.background.messaging.MessageAdaptorFactory;
import com.venky.swf.routing.Config;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import com.venky.swf.util.ToWords;
import freemarker.cache.NullCacheStorage;
import freemarker.core.ArithmeticEngine;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import in.succinct.beckn.Context;
import in.succinct.beckn.Message;
import in.succinct.beckn.Request;
import in.succinct.beckn.portal.util.DomainMapper;
import in.succinct.beckn.registry.db.model.onboarding.NetworkDomain;
import in.succinct.beckn.registry.db.model.onboarding.NetworkParticipant;
import in.succinct.beckn.registry.db.model.onboarding.NetworkRole;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("unchecked")
public class ApiTestImpl extends ModelImpl<ApiTest> {
    public ApiTestImpl(ApiTest test){
        super(test);
    }
    public ApiCall execute(){

        Context context = getContext();
        Message message = getMessage();

        Request request = new Request();
        request.setContext(context);
        request.setMessage(message);

        ApiTest test = getProxy();
        UseCase useCase = test.getUseCase();
        BecknApi api = useCase.getBecknApi();



        NetworkRole calledOn = test.getCalledOnSubscriber();
        NetworkRole caller = test.getProxySubscriber();
        if (caller == null){
            caller = getSelfSubscription(test.getUseCase().getNetworkDomain().getName(),ObjectUtil.equals(calledOn.getType(), NetworkRole.SUBSCRIBER_TYPE_BAP)?
                    NetworkRole.SUBSCRIBER_TYPE_BPP : NetworkRole.SUBSCRIBER_TYPE_BAP);
        }



        Map<String,String> headers  = new HashMap<>();
        assert caller != null;
        if (Config.instance().getBooleanProperty("beckn.auth.enabled", false) && test.isSignatureNeeded() ) {
            headers.put("Authorization", request.generateAuthorizationHeader(caller.getSubscriberId(),
                    caller.getNetworkParticipant().getParticipantKeys().get(0).getKeyId()));
        }
        headers.put("Accept", MimeType.APPLICATION_JSON.toString());
        headers.put("Content-Type",MimeType.APPLICATION_JSON.toString());

        ApiCall apiCall = Database.getTable(ApiCall.class).newRecord();
        apiCall.setApiTestId(test.getId());
        apiCall.setRequestHeaders(headers.toString());
        apiCall.setRequestPayLoad(request.getInner().toString());

        if (!test.isPushedViaMessageQ()){
            JSONObject responseHeaders = new JSONObject();
            Call<JSONObject> call = new Call<>();
            call.method(HttpMethod.POST).
                    url(calledOn.getUrl() +"/" + api.getName()).
                    input(request.getInner()).inputFormat(InputFormat.JSON).headers(headers);
            JSONAware response = call.getResponseAsJson();
            if (response == null ){
                response = (JSONAware) JSONValue.parse(new InputStreamReader(call.getErrorStream()));
            }
            responseHeaders.putAll(call.getResponseHeaders());
            JSONFormatter formatter = new JSONFormatter();
            if (response != null){
                apiCall.setResponseHeaders(responseHeaders.toString());
                apiCall.setResponsePayload(formatter.toString(response));
            }
        }else {
            pushToQueue(request,headers);
        }
        apiCall.setMessageId(context.getMessageId());
        apiCall.save();

        return apiCall;
    }

    private void pushToQueue(Request request, Map<String,String> headers) {
        ApiTest test = getProxy();
        NetworkRole calledOnSubscriber = test.getCalledOnSubscriber();

        Map<String,String> authParams = request.extractAuthorizationParams("Authorization",headers);
        Context context = request.getContext();

        String src = authParams.get("subscriber_id");
        String srcUri = null;
        if (ObjectUtil.equals(src,context.getBapId())){
            srcUri = context.getBapUri();
        }else if (ObjectUtil.equals(src,context.getBppId())){
            srcUri = context.getBppUri();
        }else {
            throw new RuntimeException("Cannot identify source of the message");
        }



        final CloudEventBuilder builder = CloudEventBuilder.v1().withId(context.getMessageId()) // this can be
                .withType(context.getAction()) // type of event
                .withSource(URI.create(srcUri)) // event source
                .withDataContentType("application/json")
                .withData(request.toString().getBytes(StandardCharsets.UTF_8));

        headers.forEach((k,v)-> {
            String key = ((String)k).toLowerCase();
            if (key.matches("[a-z,0-9]*")) {
                builder.withExtension(key, (String)v);
            }
        });


        final CloudEvent event = builder.build();

        MessageAdaptor adaptor = MessageAdaptorFactory.getInstance().getDefaultMessageAdaptor();
        StringBuilder topic = new StringBuilder();
        topic.append("ROOT").                                                                                                       // ROOT
                append(adaptor.getSeparatorToken()).append(context.getCountry()).                                                   // Country
                append(adaptor.getSeparatorToken()).append(context.getCity()).   // City
                append(adaptor.getSeparatorToken()).append(context.getDomain()).                                    // Domain
                append(adaptor.getSeparatorToken()).append(context.getAction()).                           // Action
                append(adaptor.getSeparatorToken()).append("search".equals(context.getAction()) ? "all" :   // Subscriber
                                                            (context.getAction().startsWith("on_") ? context.getBapId() : context.getBppId()).
                                                                    replaceAll(adaptor.getSeparatorToken(), "_separator_")).
                append(adaptor.getSeparatorToken()).append(context.getTransactionId()).   // Transaction
                append(adaptor.getSeparatorToken()).append(context.getMessageId());   // Message


        adaptor.getDefaultQueue().publish(topic.toString(),event);
    }

    private Message getMessage() {
        ApiTest test = getProxy();
        UseCase useCase = test.getUseCase();
        Reader templateJson = useCase.getTemplateJson();
        String messageTemplate = StringUtil.read(templateJson) ;
        String messagePayload ;
        try {
            StringWriter writer = new StringWriter();
            new Template("message",messageTemplate,getConfiguration()).process(
                    (JSONObject)JSONValue.parse(test.getVariables()),writer);
            messagePayload = writer.toString();
        }catch (Exception ex){
           throw new RuntimeException(ex);
        }

        return new Message(messagePayload);
    }

    private static final Configuration cfg = null ;
    static {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setLocalizedLookup(false);
        cfg.setWrapUncheckedExceptions(true);
        ArithmeticEngine engine = ArithmeticEngine.BIGDECIMAL_ENGINE;
        engine.setMinScale(2);
        engine.setMaxScale(2);
        cfg.setArithmeticEngine(engine);
        cfg.setCacheStorage(new NullCacheStorage()); //
        cfg.setSharedVariable("to_words",new ToWords());
    }
    private Configuration getConfiguration(){
        return cfg;
    }

    private Context getContext() {
        ApiTest test = getProxy();
        UseCase useCase = test.getUseCase();
        BecknApi api = useCase.getBecknApi();

        Context context = new Context();
        context.setAction(api.getName());
        context.setDomain(useCase.getNetworkDomain().getName());
        context.setTtl(60);

        NetworkRole bap = null;
        NetworkRole bpp = null ;
        NetworkRole bg = null ;
        JSONObject variables = (JSONObject)JSONValue.parse(test.getVariables());

        for (Object key : variables.keySet()){
            if (key.toString().startsWith("context.")){
                String contextKey = key.toString().replace("context.","");
                Object value = variables.get(key);
                if (value == null){
                    context.getInner().remove(contextKey);
                }else {
                    context.set(contextKey, String.valueOf(value));
                }

            }
        }
        String apiUsuallycalledOn = "";
        if (ObjectUtil.equals(api.getPlatform(),NetworkRole.SUBSCRIBER_TYPE_BPP)){
            bpp = test.getCalledOnSubscriber();
            apiUsuallycalledOn = NetworkRole.SUBSCRIBER_TYPE_BPP;
        }else if (ObjectUtil.equals(api.getPlatform(),NetworkRole.SUBSCRIBER_TYPE_BAP)){
            bap = test.getCalledOnSubscriber();
            apiUsuallycalledOn = NetworkRole.SUBSCRIBER_TYPE_BAP;

        }else if (ObjectUtil.equals(api.getPlatform(),NetworkRole.SUBSCRIBER_TYPE_BG)){
            bg = test.getCalledOnSubscriber();
            List<BecknApi> apis  = new Select().from(BecknApi.class).where(new Expression(getPool(), Conjunction.AND).add(
                    new Expression(getPool(),"PLATFORM" , Operator.NE, NetworkRole.SUBSCRIBER_TYPE_BG)).add(
                            new Expression(getPool(),"NAME",Operator.EQ,api.getName()) ) ).execute();
            apiUsuallycalledOn = apis.get(0).getPlatform();
        }
        if (test.getProxySubscriberId() != null){
            NetworkRole s = test.getProxySubscriber();
            if (bap == null && ObjectUtil.equals(s.getType(), NetworkRole.SUBSCRIBER_TYPE_BAP)){
                bap = s;
            }
            if (bpp == null && ObjectUtil.equals(s.getType(),NetworkRole.SUBSCRIBER_TYPE_BPP)){
                bpp = s;
            }
        }else {
            if (bap == null){
                bap = getSelfSubscription(useCase.getNetworkDomain().getName(),NetworkRole.SUBSCRIBER_TYPE_BAP);
            }
            if (bpp == null){
                bpp = getSelfSubscription(useCase.getNetworkDomain().getName(),NetworkRole.SUBSCRIBER_TYPE_BPP);
            }
        }


        if (bap != null && context.getBapId() == null) {
            context.setBapId(bap.getSubscriberId());
            context.setBapUri(bap.getUrl());
        }
        if (bpp != null && context.getBppId() == null && bg == null) {
            context.setBppId(bpp.getSubscriberId());
            context.setBppUri(bpp.getUrl());
        }
        if (ObjectUtil.isVoid(context.getBppId()) && bg == null){
            throw new RuntimeException("Cannot determine participants in the interaction");
        }else if (ObjectUtil.isVoid(context.getBapId()) && bg == null ){
            throw new RuntimeException("Cannot determine participants in the interaction");
        }
        context.setTimestamp(new Date());
        context.setCoreVersion("0.9.1");

        String messageId = UUID.randomUUID().toString(); //SequentialNumber.get("BECKN_MESSAGE_ID").next();
        context.setMessageId(messageId);
        if (ObjectUtil.isVoid(context.getTransactionId())) {
            context.setTransactionId(messageId);
        }
        /*
        String transactionId  = SequentialNumber.get("BECKN_TRANSACTION_ID").next();
        context.setTransactionId(transactionId);

         */

        return context;
    }



    private NetworkRole getSelfSubscription(String realDomain, String type) {
        NetworkParticipant participant = Database.getTable(NetworkParticipant.class).newRecord();
        participant.setParticipantId(Config.instance().getHostName());
        participant = Database.getTable(NetworkParticipant.class).getRefreshed(participant);


        String domain = DomainMapper.getMapping(realDomain);
        NetworkRole role = Database.getTable(NetworkRole.class).newRecord();
        role.setNetworkParticipantId(participant.getId());
        role.setNetworkDomainId(NetworkDomain.find(realDomain).getId());
        role.setType(type);
        role = Database.getTable(NetworkRole.class).getRefreshed(role);

        if (role.getRawRecord().isNewRecord()){
            return null;
        }else if (!ObjectUtil.equals(role.getStatus(),NetworkRole.SUBSCRIBER_STATUS_SUBSCRIBED)){
            return null;
        }else{
            return role;
        }

    }



}
