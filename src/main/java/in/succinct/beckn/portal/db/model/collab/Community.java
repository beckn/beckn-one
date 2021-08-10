package in.succinct.beckn.portal.db.model.collab;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.COLUMN_SIZE;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.relationship.CONNECTED_VIA;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.model.Model;
import in.succinct.beckn.portal.db.model.proposal.GovernedArea;
import in.succinct.beckn.portal.db.model.proposal.ProposalTemplate;

import java.util.List;


@MENU("Beckn")
public interface Community extends Model {
    @UNIQUE_KEY
    public String getName();
    public void setName(String name);

    public void addAdmin(long id);
    public void join();
    public void leave();

    @COLUMN_DEF(StandardDefault.BOOLEAN_TRUE)
    public boolean isSelfManaged();
    public void setSelfManaged(boolean selfManaged);

    public boolean isMember(com.venky.swf.db.model.User user);

    @IS_VIRTUAL
    public boolean isMember();

    @IS_VIRTUAL
    public boolean isAdmin();

    public  String getCommunityPageUrl();
    public void setCommunityPageUrl(String url);

    public List<Member> getMembers();

    public List<Post> getPosts();


}
