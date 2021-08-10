package in.succinct.beckn.portal.db.model.api;

import com.venky.core.string.StringUtil;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.db.model.io.json.JSONFormatter;
import com.venky.swf.db.table.ModelImpl;
import com.venky.swf.integration.api.Call;
import com.venky.swf.integration.api.HttpMethod;
import com.venky.swf.integration.api.InputFormat;
import com.venky.swf.plugins.templates.util.templates.TemplateEngine;
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
import in.succinct.beckn.registry.db.model.Subscriber;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
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



        Subscriber calledOn = test.getCalledOnSubscriber();
        Subscriber caller = test.getProxySubscriber();
        if (caller == null){
            caller = getSelfSubscription(test.getUseCase().getDomain(),ObjectUtil.equals(calledOn.getType(),Subscriber.SUBSCRIBER_TYPE_BAP)? Subscriber.SUBSCRIBER_TYPE_BPP : Subscriber.SUBSCRIBER_TYPE_BAP);
        }



        JSONObject headers  = new JSONObject();
        assert caller != null;
        if (Config.instance().getBooleanProperty("beckn.auth.enabled", false)) {
            headers.put("Authorization", request.generateAuthorizationHeader(caller.getSubscriberId(),
                    String.format("%s.k1", caller.getSubscriberId())));
        }
        headers.put("Accept", MimeType.APPLICATION_JSON.toString());
        headers.put("Content-Type",MimeType.APPLICATION_JSON.toString());

        JSONObject responseHeaders = new JSONObject();

        Call<JSONObject> call = new Call<>();
        call.method(HttpMethod.POST).
                url(calledOn.getSubscriberUrl() +"/" + api.getName()).
                input(request.getInner()).inputFormat(InputFormat.JSON).headers(headers);
        JSONAware response = call.getResponseAsJson();
        if (response == null ){
            response = (JSONAware) JSONValue.parse(new InputStreamReader(call.getErrorStream()));
        }

        JSONFormatter formatter = new JSONFormatter();
        ApiCall apiCall = Database.getTable(ApiCall.class).newRecord();
        apiCall.setApiTestId(test.getId());
        apiCall.setRequestHeaders(headers.toString());
        apiCall.setRequestPayLoad(request.getInner().toString());
        if (response != null){
            responseHeaders.putAll(call.getResponseHeaders());
            apiCall.setResponseHeaders(responseHeaders.toString());
            apiCall.setResponsePayload(formatter.toString(response));
        }
        apiCall.setMessageId(context.getMessageId());
        apiCall.save();

        return apiCall;
    }

    private Message getMessage() {
        ApiTest test = getProxy();
        UseCase useCase = test.getUseCase();
        Reader templateJson = useCase.getTemplateJson();
        String messageTemplate = StringUtil.read(templateJson) ;
        String messagePayload ;
        try {
            StringWriter writer = new StringWriter();
            TemplateEngine.getInstance().publish(new Template("message",messageTemplate,getConfiguration()),
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
        context.setDomain(useCase.getDomain());

        Subscriber bap = null;
        Subscriber bpp = null ;
        Subscriber bg = null ;
        JSONObject variables = (JSONObject)JSONValue.parse(test.getVariables());

        for (Object key : variables.keySet()){
            if (key.toString().startsWith("context.")){
                String contextKey = key.toString().replace("context.","");
                Object value = variables.get(key);
                context.set(contextKey,String.valueOf(value));
            }
        }
        String apiUsuallycalledOn = "";
        if (ObjectUtil.equals(api.getPlatform(),Subscriber.SUBSCRIBER_TYPE_BPP)){
            bpp = test.getCalledOnSubscriber();
            apiUsuallycalledOn = Subscriber.SUBSCRIBER_TYPE_BPP;
        }else if (ObjectUtil.equals(api.getPlatform(),Subscriber.SUBSCRIBER_TYPE_BAP)){
            bap = test.getCalledOnSubscriber();
            apiUsuallycalledOn = Subscriber.SUBSCRIBER_TYPE_BAP;

        }else if (ObjectUtil.equals(api.getPlatform(),Subscriber.SUBSCRIBER_TYPE_BG)){
            bg = test.getCalledOnSubscriber();
            List<BecknApi> apis  = new Select().from(BecknApi.class).where(new Expression(getPool(), Conjunction.AND).add(
                    new Expression(getPool(),"PLATFORM" , Operator.NE, Subscriber.SUBSCRIBER_TYPE_BG)).add(
                            new Expression(getPool(),"NAME",Operator.EQ,api.getName()) ) ).execute();
            apiUsuallycalledOn = apis.get(0).getPlatform();
        }
        if (test.getProxySubscriberId() != null){
            Subscriber s = test.getProxySubscriber();
            if (bap == null && ObjectUtil.equals(s.getType(), Subscriber.SUBSCRIBER_TYPE_BAP)){
                bap = s;
            }
            if (bpp == null && ObjectUtil.equals(s.getType(),Subscriber.SUBSCRIBER_TYPE_BPP)){
                bpp = s;
            }
        }else {
            if (bap == null){
                bap = getSelfSubscription(useCase.getDomain(),Subscriber.SUBSCRIBER_TYPE_BAP);
            }
            if (bpp == null){
                bpp = getSelfSubscription(useCase.getDomain(),Subscriber.SUBSCRIBER_TYPE_BPP);
            }
        }


        if (bap != null && context.getBapId() == null) {
            context.setBapId(bap.getSubscriberId());
            context.setBapUri(bap.getSubscriberUrl());
        }
        if (bpp != null && context.getBppId() == null && bg == null) {
            context.setBppId(bpp.getSubscriberId());
            context.setBppUri(bpp.getSubscriberUrl());
        }
        if (ObjectUtil.isVoid(context.getBppId()) && bg == null){
            throw new RuntimeException("Cannot determine participants in the interaction");
        }else if (ObjectUtil.isVoid(context.getBapId()) && bg == null ){
            throw new RuntimeException("Cannot determine participants in the interaction");
        }
        context.setTimestamp(new Date());
        context.setTtl(60);
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



    private Subscriber getSelfSubscription(String domain, String type) {
        Subscriber criteria = Database.getTable(Subscriber.class).newRecord();
        criteria.setSubscriberId(Config.instance().getHostName() + "." + domain +"."+ type);
        criteria.setDomain(domain);
        criteria.setType(type);
        criteria.setStatus(Subscriber.SUBSCRIBER_STATUS_SUBSCRIBED);

        List<Subscriber> subscribers = Subscriber.lookup(criteria,10);
        if (!subscribers.isEmpty()){
            return subscribers.get(0);
        }

        return null;
    }



}
