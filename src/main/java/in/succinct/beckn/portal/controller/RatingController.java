package in.succinct.beckn.portal.controller;

import com.venky.core.util.Bucket;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.controller.annotations.RequireLogin;
import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.path.Path;
import com.venky.swf.plugins.background.messaging.MessageAdaptor;
import com.venky.swf.plugins.background.messaging.MessageAdaptorFactory;
import com.venky.swf.plugins.beckn.messaging.Topic;
import com.venky.swf.views.BytesView;
import com.venky.swf.views.View;
import in.succinct.beckn.Context;
import in.succinct.beckn.Rating;
import in.succinct.beckn.Request;
import in.succinct.beckn.portal.util.BecknMessagePublisher;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RatingController extends BppController{


    public RatingController(Path path) {
        super(path);
    }

    @RequireLogin(false)
    public View submit_rating(){
        return act((request, requestHeaders) -> {
            new RatingPublisher(request,requestHeaders).publishAsync();
            return ack(request);
        });

    }

    @RequireLogin(false)
    public View rating_summary(){
        return act((request,requestHeaders)->{
            Rating rating = request.getMessage().getRating();
            in.succinct.beckn.portal.db.model.api.event.Rating dbRating = Database.getTable(in.succinct.beckn.portal.db.model.api.event.Rating.class).newRecord();
            dbRating.setRated(rating.getRatingCategory());
            dbRating.setRatedId(rating.getId());
            dbRating = Database.getTable(in.succinct.beckn.portal.db.model.api.event.Rating.class).getRefreshed(dbRating);
            if (dbRating.getRawRecord().isNewRecord()){
                dbRating.setRating(new Bucket(1));
                dbRating.setOrderCount(new Bucket(1));

            }
            rating.setValue((int)Math.round(dbRating.getRating().doubleValue()/dbRating.getOrderCount().doubleValue()));
            return new BytesView(getPath(),rating.getInner().toString().getBytes(StandardCharsets.UTF_8), MimeType.APPLICATION_JSON);
        });
    }
    public static class RatingPublisher extends BecknMessagePublisher {

        public RatingPublisher(Request request, Map<String,String> requestHeaders) {
            super(request,requestHeaders);
        }

        @Override
        public void publishAsync() {
            Context context = getRequest().getContext();
            Rating rating = getRequest().getMessage().getRating();
            Map<String,String> authParams = getRequest().extractAuthorizationParams("Authorization",getRequestHeaders());
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
                    //.withDataSchema(URI.create("http://beckn.org/schemas/confirm.json")) // "Identifies the schema that data
                    // adheres to."
                    .withDataContentType("application/json")
                    .withData(getRequest().toString().getBytes(StandardCharsets.UTF_8));

            getRequestHeaders().forEach((k,v)-> {
                String key = k.toLowerCase();
                if (key.matches("[a-z,0-9]*")) {
                    builder.withExtension(k.toLowerCase(), v);
                }
            });
            authParams.forEach((k,v)-> {
                String key = k.toLowerCase();
                if (key.matches("[a-z,0-9]*")) {
                    builder.withExtension(k.toLowerCase(), v);
                }
            });

            final CloudEvent event = builder.build();

            MessageAdaptor adaptor = MessageAdaptorFactory.getInstance().getDefaultMessageAdaptor();
            Topic topic = Topic.builder(adaptor).
                    country(context.getCountry()).
                    city(context.getCity()).
                    domain(context.getDomain()).
                    action(context.getAction()).
                    subscriber_id("all").
                    transaction_id(context.getTransactionId()).
                    message_id(context.getMessageId()).build();

            adaptor.getDefaultQueue().publish(topic.toString(),event);
        }
    }


}
