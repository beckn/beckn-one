package in.succinct.beckn.portal.db.model.collab;

import com.venky.core.util.Bucket;
import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.model.Model;
import in.succinct.beckn.portal.db.model.User;

import java.io.Reader;

public interface Post extends Model {
    @PARTICIPANT
    public Long getCommunityId();
    public void setCommunityId(Long id);
    public Community getCommunity();

    public  String getTitle();
    public void setTitle(String title);

    @PARTICIPANT
    @COLUMN_DEF(StandardDefault.CURRENT_USER)
    @IS_NULLABLE(false)
    public Long getAuthorId();
    public void setAuthorId(Long id);
    public User getAuthor();

    /**
     * Is Markdown body
     * @return markdown body
     */
    public Reader getBody();
    public void setBody(Reader body);

    Bucket getReadCount();
    public void setReadCount(Bucket readCount);

    Bucket getUsefulCount();
    public void setUsefulCount(Bucket readCount);

}
