package in.succinct.beckn.portal.util;

import com.ondc.client.mqtt.MqttPublisher;
import com.venky.swf.views.View;
import in.succinct.beckn.Request;

import java.util.Map;

public interface BecknActionCallBack  {
    public View execute(Request request, Map<String,String> requestHeaders);
}

