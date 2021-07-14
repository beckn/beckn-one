package in.succinct.beckn.portal.db.model.collab;

import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.model.Model;

import java.util.List;


@MENU("Beckn")
public interface Community extends Model {

    public String getName();
    public void setName(String name);

    public void addAdmin(long id);
    public void join();
    public void leave();

    @IS_VIRTUAL
    public boolean isMember();

    @IS_VIRTUAL
    public boolean isAdmin();

    public  String getCommunityPageUrl();
    public void setCommunityPageUrl(String url);

    public List<Member> getMembers();

    public List<Post> getPosts();

}
