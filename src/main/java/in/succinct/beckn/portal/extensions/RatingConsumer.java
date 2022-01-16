package in.succinct.beckn.portal.extensions;


import com.venky.core.util.Bucket;
import com.venky.swf.db.Database;
import com.venky.swf.plugins.background.core.TaskManager;
import com.venky.swf.plugins.background.messaging.MessageAdaptor.CloudEventHandler;
import com.venky.swf.plugins.background.messaging.MessageAdaptorFactory;
import in.succinct.beckn.Context;
import in.succinct.beckn.Rating;
import in.succinct.beckn.Request;
import in.succinct.beckn.portal.db.model.api.event.TransactionRating;
import io.cloudevents.CloudEvent;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class RatingConsumer {
    static {
        registerRatingConsumer(new RatingConsumer());
    }

    public static void registerRatingConsumer(RatingConsumer consumer){
        //Let this happen after all extensions are registered or else the Message adaptors will not be registered
        TaskManager.instance().executeAsync(()-> MessageAdaptorFactory.getInstance().getDefaultMessageAdaptor().subscribe("ROOT/#", new CloudEventHandler() {
            @Override
            public void handle(String topic, CloudEvent event) {
                TaskManager.instance().executeAsync(()-> collate(event),false);
            }
            public void collate(CloudEvent event){
                JSONObject rateRequest = (JSONObject) JSONValue.parse(new InputStreamReader(new ByteArrayInputStream(Objects.requireNonNull(event.getData()).toBytes())));
                Request request = new Request(rateRequest);
                Rating rating = request.getMessage().getRating();
                Context context = request.getContext();
                TransactionRating tr = Database.getTable(TransactionRating.class).newRecord();
                tr.setTransactionId(context.getTransactionId());
                tr.setRated(rating.getRatingCategory());
                tr.setRatedId(rating.getId());
                tr.setRating(new Bucket(rating.getValue()));
                TransactionRating tr1 = Database.getTable(TransactionRating.class).getRefreshed(tr);
                tr1.save();
            }
        }),false);
    }

}
