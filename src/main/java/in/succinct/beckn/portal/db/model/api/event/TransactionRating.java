package in.succinct.beckn.portal.db.model.api.event;

import com.venky.core.util.Bucket;
import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.validations.IntegerRange;
import com.venky.swf.db.model.Model;

public interface TransactionRating extends Model,RatedEntity {
    @IS_NULLABLE(value = false)
    @UNIQUE_KEY
    public String getTransactionId();
    public void setTransactionId(String transactionId);


    @IntegerRange(min = 1,max = 10)
    @COLUMN_DEF(StandardDefault.ZERO)
    public Bucket getRating();
    public void setRating(Bucket rating);


}
