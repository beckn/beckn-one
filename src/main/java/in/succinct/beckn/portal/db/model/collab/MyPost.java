package in.succinct.beckn.portal.db.model.collab;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.model.Model;
import in.succinct.beckn.portal.db.model.User;

public interface MyPost extends Model {
    @UNIQUE_KEY
    public Long getPostId();
    public void setPostId(Long id);
    public Post getPost();

    @UNIQUE_KEY
    @PARTICIPANT
    public Long getUserId();
    public void setUserId(Long id);
    public User getUser();

    @Index
    @COLUMN_DEF(StandardDefault.BOOLEAN_TRUE)
    public boolean isUnRead();
    public void setUnRead(boolean read);

    @Index
    @COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
    public  boolean isUseful();
    public void setUseful(boolean useful);



}
