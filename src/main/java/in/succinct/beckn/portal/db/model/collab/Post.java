package in.succinct.beckn.portal.db.model.collab;

import com.venky.core.util.Bucket;
import com.venky.swf.db.model.Model;
import in.succinct.beckn.portal.db.model.User;

import java.io.Reader;

public interface Post extends Model {
    public Long getCommunityId();
    public void setCommunityId(Long id);
    public Community getCommunity();

    public  String getTitle();
    public void setTitle(String title);

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
