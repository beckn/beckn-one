package in.succinct.beckn.portal.db.model.api;

import com.venky.swf.db.Database;
import com.venky.swf.db.model.io.json.JSONFormatter;
import com.venky.swf.db.table.ModelImpl;
import com.venky.swf.integration.api.Call;
import com.venky.swf.integration.api.HttpMethod;
import com.venky.swf.integration.api.InputFormat;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ApiCallImpl extends ModelImpl<ApiCall> {

    public ApiCallImpl(ApiCall proxy){
        super(proxy);
    }
    public void execute() {
        ApiCall apiCall = getProxy();
        JSONObject headers = (JSONObject) JSONValue.parse(apiCall.getRequestHeaders());

        //apiCall.getRequestPayLoad();

        JSONObject responseHeaders = new JSONObject();

        Call<?> call = new Call<InputStream>().
                method(HttpMethod.POST).
                url(apiCall.getApiTest().getCalledOnSubscriber().getSubscriberUrl() +"/" + apiCall.getApiTest().getUseCase().getBecknApi().getName()).
                input(new ByteArrayInputStream(apiCall.getRequestPayLoad().getBytes(StandardCharsets.UTF_8))).
                inputFormat(InputFormat.INPUT_STREAM).headers(headers);

        JSONAware response = call.getResponseAsJson();

        JSONFormatter formatter = new JSONFormatter();
        responseHeaders.putAll(call.getResponseHeaders());
        apiCall.setResponseHeaders(responseHeaders.toString());
        apiCall.setResponsePayload(formatter.toString(response));
        apiCall.save();

    }
}
