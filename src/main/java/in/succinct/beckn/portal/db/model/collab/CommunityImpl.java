package in.succinct.beckn.portal.db.model.collab;

import com.venky.swf.db.Database;
import com.venky.swf.db.table.ModelImpl;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;

import java.util.List;

public class CommunityImpl extends ModelImpl<Community> {
    public CommunityImpl(){
        super();
    }
    public CommunityImpl(Community community){
        super(community);
    }
    public void join(){
        if (!getProxy().isSelfManaged()){
            throw new RuntimeException("Please contact admin for this group");
        }

        Member member = getMember(Database.getInstance().getCurrentUser());
        if (member != null) {
            member.setActive(true);
            member.setAdmin(false);
            member.save();
        }
    }
    private boolean hasMembers() {
        Community community = getProxy();
        if (community.getRawRecord().isNewRecord()) {
            return false;
        }
        Select select = new Select("ID").from(Member.class);
        List<Member> members = select.where(new Expression(select.getPool(), Conjunction.AND).
                add(new Expression(select.getPool(), "COMMUNITY_ID", Operator.EQ, community.getId()))).execute(1);
        return !members.isEmpty();
    }

    public void addAdmin(long id){
        Community community = getProxy();
        if (community.getRawRecord().isNewRecord()){
            throw new RuntimeException("No such community");
        }
        Member me = getMember(Database.getInstance().getCurrentUser());
        if (me  == null){
            throw new RuntimeException("Not Logged in.");
        }

        Member memberPromoted = null;
        if (me.getRawRecord().isNewRecord()){
            if (id == me.getUserId() && !hasMembers()){
                //Trying to promote myself
                memberPromoted = me;
            }
        }else if (me.isAdmin() && me.isActive() && !me.isDirty() && me.getUserId() != id) {
            memberPromoted = getMember(id);
        }
        if (!memberPromoted.isAdmin()){
            memberPromoted.setAdmin(true);
            memberPromoted.setActive(true);
            memberPromoted.save();
        }
    }
    private Member getMember(com.venky.swf.db.model.User user){
        if (user == null){
            return null;
        }
        return getMember(user.getId());
    }
    private Member getMember(long userId){
        Member member = Database.getTable(Member.class).newRecord();
        member.setCommunityId(getProxy().getId());
        member.setUserId(userId);
        member = Database.getTable(Member.class).getRefreshed(member);
        return member;
    }
    public void leave(){
        Member member = getMember(Database.getInstance().getCurrentUser());
        if (member != null) {
            member.setActive(false);
            member.setAdmin(false);
            member.save();
        }
    }

    public boolean isMember() {
        return isMember(Database.getInstance().getCurrentUser());
    }
    public boolean isMember(com.venky.swf.db.model.User user) {
        Community community = getProxy();
        if (community.getRawRecord().isNewRecord()){
            return false;
        }
        Member member = getMember(user);
        return member!=null && !member.getRawRecord().isNewRecord() && member.isActive();
    }
    public boolean isAdmin(){
        Community community = getProxy();
        if (community.getRawRecord().isNewRecord()){
            return false;
        }
        Member member = getMember(Database.getInstance().getCurrentUser());
        if (member == null){
            return false;
        }
        return member.isAdmin();
    }




}
