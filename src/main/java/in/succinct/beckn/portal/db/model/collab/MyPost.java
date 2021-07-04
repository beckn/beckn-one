package in.succinct.beckn.portal.db.model.collab;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.model.Model;
import in.succinct.beckn.portal.db.model.User;

public interface MyPost extends Model {
    @UNIQUE_KEY
    public Long getPostId();
    public void setPostId(Long id);
    public Post getPost();

    @UNIQUE_KEY
    public Long getUserId();
    public void setUserId(Long id);
    public User getUser();

    @Index
    public boolean isUnRead();
    public void setUnRead(boolean read);

    @Index
    public  boolean isUseful();
    public void setUseful(boolean useful);



}
