package in.succinct.beckn.portal.controller;

import com.venky.core.string.StringUtil;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.controller.annotations.RequireLogin;
import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.db.model.io.json.JSONFormatter;
import com.venky.swf.path.Path;
import com.venky.swf.plugins.templates.controller.TemplatedController;
import com.venky.swf.views.BytesView;
import com.venky.swf.views.View;
import in.succinct.beckn.Acknowledgement;
import in.succinct.beckn.Acknowledgement.Status;
import in.succinct.beckn.Error;
import in.succinct.beckn.Request;
import in.succinct.beckn.Response;
import in.succinct.beckn.portal.db.model.api.ApiCall;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class LocalRetailBapController extends TemplatedController {
    public LocalRetailBapController(Path path) {
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
        ack.setSignature(Request.generateSignature(request.hash(),request.getPrivateKey(request.getContext().getBapId(),request.getContext().getBapId() +".k1")));
        return new BytesView(getPath(),new Response(request.getContext(),ack).toString().getBytes(StandardCharsets.UTF_8));
    }


    private View act(){
        Request request =null ;
        try {
            request = new Request(StringUtil.read(getPath().getInputStream()));
            if (request.verifySignature("Authorization",getPath().getHeaders())){
                String messageId = request.getContext().getMessageId();
                if (ObjectUtil.isVoid(messageId)){
                    messageId = UUID.randomUUID().toString();
                }
                JSONFormatter formatter = new JSONFormatter();
                ApiCall apiCall = Database.getTable(ApiCall.class).newRecord();
                apiCall.setMessageId(messageId);
                apiCall = Database.getTable(ApiCall.class).getRefreshed(apiCall);
                apiCall.setCallBackPayload(formatter.toString(request.getInner()));
                apiCall.setCallBackHeaders(getPath().getHeaders().toString());
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
