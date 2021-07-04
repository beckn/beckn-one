package in.succinct.beckn.portal.db.model.collab;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.model.Model;
import in.succinct.beckn.portal.db.model.User;

public interface Member extends Model {
    @UNIQUE_KEY
    @Index
    public Long getUserId();
    public void setUserId(Long id);
    public User getUser();

    @UNIQUE_KEY
    @Index
    public Long getCommunityId();
    public void setCommunityId(Long id);
    public Community getCommunity();

    public boolean isActive();
    public void setActive(boolean active);


}
