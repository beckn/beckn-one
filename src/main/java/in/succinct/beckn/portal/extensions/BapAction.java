package in.succinct.beckn.portal.extensions;

import com.venky.core.collections.IgnoreCaseMap;
import com.venky.core.io.ByteArrayInputStream;
import com.venky.core.string.StringUtil;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.Database;
import com.venky.swf.db.model.io.json.JSONFormatter;
import com.venky.swf.db.model.reflection.ModelReflector;
import com.venky.swf.plugins.background.core.AsyncTaskManager;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.TaskManager;
import com.venky.swf.plugins.background.messaging.MessageAdaptor;
import com.venky.swf.plugins.background.messaging.MessageAdaptor.CloudEventHandler;
import com.venky.swf.plugins.background.messaging.MessageAdaptor.SubscriptionHandle;
import com.venky.swf.plugins.background.messaging.MessageAdaptorFactory;
import com.venky.swf.plugins.beckn.messaging.Topic;
import com.venky.swf.routing.Config;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.beckn.Request;
import in.succinct.beckn.portal.db.model.api.ApiCall;
import in.succinct.beckn.portal.db.model.api.NetworkRole;
import in.succinct.beckn.registry.db.model.onboarding.NetworkParticipant;
import io.cloudevents.CloudEvent;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class BapAction {
    static{
        TaskManager.instance().executeAsync(()-> {
            MessageAdaptor adaptor = MessageAdaptorFactory.getInstance().getDefaultMessageAdaptor();
            NetworkParticipant networkParticipant = NetworkParticipant.find(Config.instance().getHostName());
            Map<String,List<String>> participantActions = new HashMap<String,List<String>>(){{
                // put(NetworkRole.SUBSCRIBER_TYPE_BG,Arrays.asList("search")); No need for bg.!!
                put(NetworkRole.SUBSCRIBER_TYPE_BPP,Arrays.asList("search", "select", "init", "confirm", "track",
                        "cancel", "update", "status", "rating", "support"));
                put(NetworkRole.SUBSCRIBER_TYPE_BAP,Arrays.asList("on_search", "on_select", "on_init", "on_confirm",
                        "on_track", "on_cancel", "on_update", "on_status", "on_rating", "on_support"));
            }};
            networkParticipant.getNetworkRoles().forEach(networkRole -> {
                if (!participantActions.containsKey(networkRole.getType())){
                    return;
                }
                participantActions.get(networkRole.getType()).forEach(action -> {
                    Topic topic = Topic.builder(adaptor).action(action).domain(networkRole.getNetworkDomain().getName()).subscriber_id(networkRole.getSubscriberId()).build();

                    adaptor.getDefaultQueue().subscribe(topic.toString(), new CloudEventHandler() {
                        @Override
                        public void handle(String topic, CloudEvent event, SubscriptionHandle subscriptionHandle) {
                            String payload = StringUtil.read(new ByteArrayInputStream(Objects.requireNonNull(event.getData()).toBytes()));
                            Request request = new Request(payload);
                            Map<String, String> map = new IgnoreCaseMap<>();
                            for (String extensionName : event.getExtensionNames()) {
                                map.put(extensionName, (String) event.getExtension(extensionName));
                            }
                            //Subscription handler doesnot have db connection!
                            AsyncTaskManager.getInstance().addAll(Collections.singletonList(new Action(request, map)));
                        }
                    });
                });
            });
        },false);
    }

    public static class Action implements Task {
        Request request;
        Map<String,String> headers ;
        public Action(Request request, Map<String,String> map){
            this.request = request;
            this.headers = map;
        }

        @Override

        public void execute() {
            if (!Config.instance().getBooleanProperty("beckn.auth.enabled", false)  || request.verifySignature("Authorization",headers)){
                String action  = request.getContext().getAction();
                boolean isCallBack = action.startsWith("on_");

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

                if (!isCallBack){
                    apiCall.setRequestPayLoad(request.toString());
                    JSONObject requestHeaderAsJson = new JSONObject();
                    requestHeaderAsJson.putAll(headers);
                    apiCall.setRequestHeaders(formatter.toString(requestHeaderAsJson));
                }else {
                    JSONObject responseCollection = new JSONObject();
                    JSONObject headersCollection = new JSONObject();

                    if (!apiCall.getReflector().isVoid(apiCall.getCallBackPayload())){
                        responseCollection = (JSONObject) JSONValue.parse(apiCall.getCallBackPayload());
                        headersCollection = (JSONObject) JSONValue.parse(apiCall.getCallBackHeaders());
                    }
                    responseCollection.put(request.getContext().getBppId(),request.getInner());

                    JSONObject responseHeadersAsJson = new JSONObject();
                    responseHeadersAsJson.putAll(headers);

                    headersCollection.put(request.getContext().getBppId(),responseHeadersAsJson);

                    apiCall.setCallBackPayload(formatter.toString(responseCollection));
                    apiCall.setCallBackHeaders(formatter.toString(headersCollection));

                }

                apiCall.save();
            }
        }
    }
}
