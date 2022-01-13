package in.succinct.beckn.portal.extensions;

import com.venky.swf.db.Database;
import com.venky.swf.db.extensions.BeforeModelUpdateExtension;
import in.succinct.beckn.portal.db.model.api.event.Rating;
import in.succinct.beckn.portal.db.model.api.event.TransactionRating;

public class BeforeUpdateTransactionRating extends BeforeModelUpdateExtension<TransactionRating> {
    static {
        registerExtension(new BeforeUpdateTransactionRating());
    }
    @Override
    public void beforeUpdate(TransactionRating model) {
        Rating finalRating = Database.getTable(Rating.class).newRecord();
        finalRating.setRated(model.getRated());
        finalRating.setRatedId(model.getRatedId());
        finalRating = Database.getTable(Rating.class).getRefreshed(finalRating);
        if (finalRating.getRawRecord().isNewRecord()){
            return;
        }

        double oldRating = finalRating.getReflector().getJdbcTypeHelper().getTypeRef(double.class).getTypeConverter().
                valueOf(model.getRawRecord().getOldValue("RATING"));
        finalRating.getRating().decrement(oldRating);
        finalRating.getRating().increment(model.getRating().doubleValue());


    }
}
