package in.succinct.beckn.portal.extensions;

import com.bigchaindb.model.GenericCallback;
import com.ondc.client.ledger.DistributedLedger;
import com.ondc.client.mqtt.MqttClient.MqttCallback;
import com.ondc.client.mqtt.MqttSubscriber;
import com.venky.core.util.Bucket;
import com.venky.swf.db.Database;
import com.venky.swf.db.Transaction;
import com.venky.swf.plugins.collab.db.model.CryptoKey;
import com.venky.swf.routing.Config;
import in.succinct.beckn.Context;
import in.succinct.beckn.Rating;
import in.succinct.beckn.Request;
import in.succinct.beckn.portal.db.model.api.event.TransactionRating;
import io.cloudevents.CloudEvent;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;
import java.util.logging.Level;

public class RatingConsumer extends MqttSubscriber {
    static {
        registerRatingConsumer(new RatingConsumer());
    }
    /*
        topic.append("ROOT").
            append("/").append(context.getCountry()).
            append("/").append(context.getCity()).
            append("/").append(rating.getRatingCategory()).
            append("/").append(rating.getId()).
            append("/").append(getEventName());

     */
    public static void registerRatingConsumer(RatingConsumer consumer){
        consumer.subscribeAsync("ROOT/#", new MqttCallback() {
            @Override
            public void receive(String topic, CloudEvent event) {
                String src = (String)event.getExtension("subscriber_id");
                String keyId = String.format("%s.%s",Config.instance().getHostName(),"k1");
                Database db  = null;
                Transaction txn = null;
                try {
                    db = Database.getInstance();
                    txn = db.getCurrentTransaction();
                    collate(event);
                    txn.commit();
                }catch (Exception ex){
                    if (txn != null){
                        txn.rollback(ex);
                    }
                    Config.instance().getLogger(getClass().getName()).log(Level.WARNING,"BOMB", ex);
                }finally {
                    if (db != null){
                        db.close();
                    }
                    //consumer.subscribeAsync(topic,this);
                }



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

        });
    }

}
