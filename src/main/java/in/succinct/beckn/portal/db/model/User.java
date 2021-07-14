package in.succinct.beckn.portal.db.model;

import com.venky.swf.db.annotations.column.relationship.CONNECTED_VIA;
import in.succinct.beckn.portal.db.model.collab.Member;

import java.util.List;

/**
 * May need calendars later. Lets see.
 */
public interface User extends com.venky.swf.plugins.collab.db.model.user.User {
    @CONNECTED_VIA("USER_ID")
    public List<Member> getMembers();
}
