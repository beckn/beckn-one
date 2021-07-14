package in.succinct.beckn.portal.db.model.collab;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.ui.PROTECTION;
import com.venky.swf.db.annotations.column.ui.PROTECTION.Kind;
import com.venky.swf.db.model.Model;
import in.succinct.beckn.portal.db.model.User;

public interface Member extends Model {
    @UNIQUE_KEY
    @Index
    @PARTICIPANT
    public long getUserId();
    public void setUserId(long id);
    public User getUser();

    @UNIQUE_KEY
    @Index
    @PARTICIPANT
    public Long getCommunityId();
    public void setCommunityId(Long id);
    public Community getCommunity();

    public boolean isActive();
    public void setActive(boolean active);

    @COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
    public boolean isAdmin();
    public void setAdmin(boolean admin);

    @IS_VIRTUAL
    public boolean isLoggedInUserCommunityAdmin();

}
