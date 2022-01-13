package in.succinct.beckn.portal.util;

import com.ondc.client.mqtt.MqttPublisher;
import in.succinct.beckn.Request;

import java.util.Map;

public abstract class BecknMessagePublisher extends MqttPublisher {
    Request request ;
    Map<String,String> requestHeaders ;
    public BecknMessagePublisher(Request request,Map<String,String> requestHeaders){
        this.request = request;
        this.requestHeaders = requestHeaders;
    }

    public Request getRequest() {
        return request;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }



    public abstract String getEventName();
    public abstract void publishAsync();
}

