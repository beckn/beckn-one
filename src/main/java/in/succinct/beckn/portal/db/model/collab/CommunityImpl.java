package in.succinct.beckn.portal.db.model.collab;

import com.venky.swf.db.Database;
import com.venky.swf.db.table.ModelImpl;

public class CommunityImpl extends ModelImpl<Community> {
    public CommunityImpl(){
        super();
    }
    public CommunityImpl(Community community){
        super(community);
    }
    public void join(){
        Member member = getMember(Database.getInstance().getCurrentUser());
        if (member != null) {
            member.setActive(true);
            member.save();
        }
    }
    private Member getMember(com.venky.swf.db.model.User user){
        if (user != null){
            Member member = Database.getTable(Member.class).newRecord();
            member.setCommunityId(getProxy().getId());
            member.setUserId(user.getId());
            member = Database.getTable(Member.class).getRefreshed(member);
            return member;
        }
        return null;
    }
    public void leave(){
        Member member = getMember(Database.getInstance().getCurrentUser());
        if (member != null) {
            member.setActive(false);
            member.save();
        }
    }


}
