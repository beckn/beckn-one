package in.succinct.beckn.portal.db.model.proposal;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.model.Model;
import in.succinct.beckn.portal.db.model.User;

public interface SubscribedTag extends Model {
    @UNIQUE_KEY
    public Long getTagId();
    public void setTagId(Long id);
    public Tag getTag();

    @UNIQUE_KEY
    public Long getUserId();
    public void setUserId(Long id);
    public User getUser();

}
