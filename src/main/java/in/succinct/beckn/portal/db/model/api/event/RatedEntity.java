package in.succinct.beckn.portal.db.model.api.event;

import com.venky.core.util.Bucket;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.annotations.column.validations.NumericRange;

public interface RatedEntity {
    @Enumeration("Seller,Product,Buyer")
    @UNIQUE_KEY
    @IS_NULLABLE(false)
    public String getRated();
    public void setRated(String ratingCategory);

    @UNIQUE_KEY
    @IS_NULLABLE(false)
    public String getRatedId();
    public void setRatedId(String ratedObjectId);


    @NumericRange(min = 1,max = 10)
    public Bucket getRating();
    public void setRating(Bucket rating);


}
