package in.succinct.beckn.portal.controller;

import com.venky.core.string.StringUtil;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.controller.Controller;
import com.venky.swf.controller.annotations.RequireLogin;
import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.db.model.io.json.JSONFormatter;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.path.Path;
import com.venky.swf.controller.TemplatedController;
import com.venky.swf.routing.Config;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import com.venky.swf.views.BytesView;
import com.venky.swf.views.View;
import in.succinct.beckn.Acknowledgement;
import in.succinct.beckn.Acknowledgement.Status;
import in.succinct.beckn.Error;
import in.succinct.beckn.Request;
import in.succinct.beckn.Response;
import in.succinct.beckn.portal.db.model.api.ApiCall;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class BapController extends Controller {
    public BapController(Path path) {
        super(path);
    }
    public View nack(Request request, String realm){
        Acknowledgement nack = new Acknowledgement(Status.NACK);
        //nack.setSignature(Request.generateSignature(request.hash(),request.getPrivateKey(request.getContext().getBppId(),request.getContext().getBppId() +".k1")));

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
        //ack.setSignature(Request.generateSignature(request.hash(),request.getPrivateKey(request.getContext().getBapId(),request.getContext().getBapId() +".k1")));
        return new BytesView(getPath(),new Response(request.getContext(),ack).toString().getBytes(StandardCharsets.UTF_8));
    }


    private View act(){
        Request request =null ;
        try {
            request = new Request(StringUtil.read(getPath().getInputStream()));
            String messageId = request.getContext().getMessageId();
            if (ObjectUtil.isVoid(messageId)){
                messageId = UUID.randomUUID().toString();
            }
            JSONFormatter formatter = new JSONFormatter();
            List<ApiCall> apiCalls = new Select(true,true).from(ApiCall.class).where(new Expression(ModelReflector.instance(ApiCall.class).getPool(),"MESSAGE_ID", Operator.EQ,messageId)).execute();
            ApiCall apiCall =null;
            if (apiCalls.isEmpty()){
                apiCall = Database.getTable(ApiCall.class).newRecord();
                apiCall.setMessageId(messageId);
            }else {
                apiCall  = apiCalls.get(0);
            }

            if (!Config.instance().getBooleanProperty("beckn.auth.enabled", false)  ||
                    request.verifySignature("Authorization",getPath().getHeaders(),apiCall.getApiTest().isSignatureNeeded())){

                JSONObject responseCollection = new JSONObject();
                JSONObject headersCollection = new JSONObject();

                if (!apiCall.getReflector().isVoid(apiCall.getCallBackPayload())){
                    responseCollection = (JSONObject) JSONValue.parse(apiCall.getCallBackPayload());
                    headersCollection = (JSONObject) JSONValue.parse(apiCall.getCallBackHeaders());
                }

                responseCollection.put(request.getContext().getBppId(),request.getInner());
                JSONObject header = new JSONObject();
                header.putAll(getPath().getHeaders());
                headersCollection.put(request.getContext().getBppId(),header);

                apiCall.setCallBackPayload(formatter.toString(responseCollection));
                apiCall.setCallBackHeaders(formatter.toString(headersCollection));
                apiCall.save();
                return ack(request);
            }else {
                return nack(request,request.getContext().getBapId());
            }
        }catch (Exception ex){
            if (request == null){
                throw new RuntimeException(ex);
            }
            Request response  = new Request();
            Error error = new Error();
            response.setContext(request.getContext());
            response.setError(error);
            error.setCode(ex.getMessage());
            error.setMessage(ex.getMessage());
            return new BytesView(getPath(),response.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    @RequireLogin(false)
    public View on_search(){
        return act();
    }
    @RequireLogin(false)
    public View on_select(){
        return act();
    }
    @RequireLogin(false)
    public View on_init(){
        return act();
    }

    @RequireLogin(false)
    public View on_confirm(){
        return act();
    }

    @RequireLogin(false)
    public View on_status(){
        return act();
    }
    @RequireLogin(false)
    public View on_track(){
        return act();
    }
    @RequireLogin(false)
    public View on_cancel(){
        return act();
    }
    @RequireLogin(false)
    public View on_update(){
        return act();
    }


    @RequireLogin(false)
    public View on_rating(){
        return act();
    }

    @RequireLogin(false)
    public View on_support(){
        return act();
    }
}
