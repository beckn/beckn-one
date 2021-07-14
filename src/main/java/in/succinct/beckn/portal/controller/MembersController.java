package in.succinct.beckn.portal.controller;

import com.venky.swf.controller.annotations.SingleRecordAction;
import com.venky.swf.db.Database;
import com.venky.swf.path.Path;
import com.venky.swf.plugins.templates.controller.TemplatedModelController;
import com.venky.swf.views.View;
import in.succinct.beckn.portal.db.model.collab.Member;

public class MembersController extends TemplatedModelController<Member> {
    public MembersController(Path path) {
        super(path);
    }
    @SingleRecordAction(icon = "fa-crown")
    public View crown(long id){
        Member member = Database.getTable(getModelClass()).get(id);
        member.getCommunity().addAdmin(member.getUserId());
        getPath().addInfoMessage("Member " + member.getUser().getLongName() + " is now an administrator for the group");
        return back();
    }
}
