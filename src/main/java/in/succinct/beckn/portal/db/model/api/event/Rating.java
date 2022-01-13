package in.succinct.beckn.portal.db.model.api.event;

import com.venky.core.util.Bucket;
import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.model.Model;


public interface Rating extends Model,RatedEntity {

    public Bucket getTotalRating();
    public void setTotalRating(Bucket totalRating);

    public Bucket getOrderCount();
    public void setOrderCount(Bucket count);
}
