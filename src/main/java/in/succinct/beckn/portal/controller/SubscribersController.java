package in.succinct.beckn.portal.controller;

import com.venky.core.string.StringUtil;
import com.venky.swf.controller.annotations.RequireLogin;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.path.Path;
import com.venky.swf.views.BytesView;
import com.venky.swf.views.View;
import in.succinct.beckn.Request;
import org.json.simple.JSONObject;

import java.util.Map;

public class SubscribersController extends in.succinct.beckn.registry.controller.SubscribersController {
    public SubscribersController(Path path) {
        super(path);
    }
    @SuppressWarnings("unchecked")
    @RequireLogin(value = false)
    public View sign(String headerName) throws Exception{
        Map<String,String> headers = getPath().getHeaders();
        String  uniqueKeyId = headers.get("unique_key_id");
        String  subscriberId = headers.get("subscriber_id");
        String payload = StringUtil.read(getPath().getInputStream());
        String header = new Request(payload).generateAuthorizationHeader(subscriberId,uniqueKeyId);
        JSONObject out = new JSONObject();
        out.put("Authorization",header);
        return new BytesView(getPath(),out.toString().getBytes(), MimeType.APPLICATION_JSON);
    }
    @RequireLogin(value = false)
    public View verify(String headerName) throws Exception{
        String payload = StringUtil.read(getPath().getInputStream());
        boolean verified = new Request(payload).verifySignature(headerName,getPath().getHeaders(),true);
        if (!verified){
            throw new RuntimeException("Verification failed!");
        }
        return getIntegrationAdaptor().createStatusResponse(getPath(),null,"Verification Successful!");
    }

}
