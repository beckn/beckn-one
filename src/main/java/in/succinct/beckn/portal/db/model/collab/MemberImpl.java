package in.succinct.beckn.portal.db.model.collab;

import com.venky.swf.db.table.ModelImpl;

public class MemberImpl extends ModelImpl<Member> {
    public MemberImpl(Member member){
        super(member);
    }
    public boolean isLoggedInUserCommunityAdmin(){
        Member other = getProxy();
        if (other.getRawRecord().isNewRecord()){
            return false;
        }
        return other.getCommunity().isAdmin();
    }
}
