package in.succinct.beckn.portal.extensions;

import com.venky.core.util.Bucket;
import com.venky.swf.db.Database;
import com.venky.swf.db.extensions.AfterModelCreateExtension;
import in.succinct.beckn.portal.db.model.api.event.Rating;
import in.succinct.beckn.portal.db.model.api.event.TransactionRating;

public class AfterCreateTransactionRating extends AfterModelCreateExtension<TransactionRating> {
    static {
        registerExtension(new AfterCreateTransactionRating());
    }
    @Override
    public void afterCreate(TransactionRating model) {
        Rating finalRating = Database.getTable(Rating.class).newRecord();
        finalRating.setRated(model.getRated());
        finalRating.setRatedId(model.getRatedId());
        finalRating = Database.getTable(Rating.class).getRefreshed(finalRating);
        if (finalRating.getRating() == null) {
            finalRating.setRating(new Bucket());
        }
        if (finalRating.getOrderCount() == null){
            finalRating.setOrderCount(new Bucket());
        }
        if (finalRating.getTotalRating() == null){
            finalRating.setTotalRating(new Bucket());
        }

        finalRating.getTotalRating().increment(model.getRating().doubleValue());
        finalRating.getOrderCount().increment(1);
        finalRating.setRating(new Bucket(finalRating.getTotalRating().doubleValue()/finalRating.getOrderCount().doubleValue()));
        finalRating.save();
    }
}
