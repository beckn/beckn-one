package in.succinct.beckn.portal.extensions;

import com.venky.swf.db.Database;
import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.beckn.portal.db.model.collab.Community;
import in.succinct.beckn.portal.db.model.collab.Member;

import java.util.List;

public class BeforeSaveMember extends BeforeModelSaveExtension<Member> {
    static{
        registerExtension(new BeforeSaveMember());
    }
    @Override
    public void beforeSave(Member model) {
        if (!model.isActive() && model.getRawRecord().isFieldDirty("ACTIVE")){
            // member is leaving.
            if (!model.isAdmin() && model.getRawRecord().isFieldDirty("ADMIN")){
                Community community = Database.getTable(Community.class).lock(model.getCommunityId());

                List<Member> administrators = new Select().from(Member.class).where(
                        new Expression(getPool(), Conjunction.AND).
                                add(new Expression(getPool(),"COMMUNITY_ID", Operator.EQ,model.getCommunityId())).
                                add(new Expression(getPool(),"ADMIN",Operator.EQ,true)).
                                add(new Expression(getPool(), "USER_ID", Operator.NE,model.getUserId()))).execute(1);

                if (administrators.isEmpty()){
                    throw new RuntimeException("Group must have at least one administrator! You can delete the community if no longer required.");
                }
            }
        }
    }
}
