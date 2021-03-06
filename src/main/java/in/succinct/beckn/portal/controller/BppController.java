package in.succinct.beckn.portal.controller;

import com.venky.core.string.StringUtil;
import com.venky.swf.controller.Controller;
import com.venky.swf.controller.annotations.RequireLogin;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.path.Path;
import com.venky.swf.routing.Config;
import com.venky.swf.views.BytesView;
import com.venky.swf.views.View;
import in.succinct.beckn.Acknowledgement;
import in.succinct.beckn.Acknowledgement.Status;
import in.succinct.beckn.Error;
import in.succinct.beckn.Options;
import in.succinct.beckn.Request;
import in.succinct.beckn.Response;
import in.succinct.beckn.portal.util.BecknActionCallBack;
import org.json.simple.JSONArray;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BppController extends Controller {
    public BppController(Path path) {
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
        //ack.setSignature(Request.generateSignature(request.hash(),request.getPrivateKey(request.getContext().getBppId(),request.getContext().getBppId() +".k1")));
        return new BytesView(getPath(),new Response(request.getContext(),ack).toString().getBytes(StandardCharsets.UTF_8),MimeType.APPLICATION_JSON);
    }

    protected View act(){
        return act(null);
    }


    protected  <T extends BecknActionCallBack> View act(T callback){
        Request request = null;
        try {
            request = new Request(StringUtil.read(getPath().getInputStream()));
            if (!Config.instance().getBooleanProperty("beckn.auth.enabled", false)  || request.verifySignature("Authorization",getPath().getHeaders(),false)){
                if (callback != null){
                    return callback.execute(request,getPath().getHeaders());
                }
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
            error.setMessage(ex.getMessage());
            return new BytesView(getPath(),response.toString().getBytes(StandardCharsets.UTF_8),MimeType.APPLICATION_JSON);
        }
    }
    @RequireLogin(false)
    public View search() {
        return act();
    }
    @RequireLogin(false)
    public View select(){
        return act();
    }
    @RequireLogin(false)
    public View cancel(){
        return act();
    }

    @RequireLogin(false)
    public View init(){
        return act();
    }
    @RequireLogin(false)
    public View confirm(){
        return act();
    }

    @RequireLogin(false)
    public View status(){
        return act();
    }
    @RequireLogin(false)
    public View track(){
        return act();
    }
    @RequireLogin(false)
    public View update(){
        return act();
    }


    @RequireLogin(false)
    public View rating(){
        return act();
    }

    @RequireLogin(false)
    public View support(){
        return act();
    }

    @RequireLogin(false)
    public View get_cancellation_reasons(){
        try {
            Request request = new Request(StringUtil.read(getPath().getInputStream()));
            if (request.verifySignature("Authorization",getPath().getHeaders())){
                Options options = new Options();
                return new BytesView(getPath(),options.toString().getBytes(StandardCharsets.UTF_8),MimeType.APPLICATION_JSON);
            }else {
                return nack(request,request.getContext().getBapId());
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }

    }

    @RequireLogin(false)
    public View get_return_reasons(){
        try {
            Request request = new Request(StringUtil.read(getPath().getInputStream()));
            if (request.verifySignature("Authorization",getPath().getHeaders())){
                Options options = new Options();
                return new BytesView(getPath(),options.toString().getBytes(StandardCharsets.UTF_8),MimeType.APPLICATION_JSON);
            }else {
                return nack(request,request.getContext().getBapId());
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }

    }

    @RequireLogin(false)
    public View get_rating_categories(){
        try {
            Request request = new Request(StringUtil.read(getPath().getInputStream()));
            if (request.verifySignature("Authorization",getPath().getHeaders())){
                return new BytesView(getPath(),new JSONArray().toString().getBytes(StandardCharsets.UTF_8),MimeType.APPLICATION_JSON);
            }else {
                return nack(request,request.getContext().getBapId());
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }

    }

}
