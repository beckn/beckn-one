package in.succinct.beckn.portal.db.model.collab;

import com.venky.swf.db.model.Model;

import java.util.List;


public interface Community extends Model {
    public String getName();
    public void setName(String name);


    public void join();
    public void leave();


    public  String getCommunityPageUrl();
    public void setCommunityPageUrl(String url);

    public List<Member> getMembers();

}
