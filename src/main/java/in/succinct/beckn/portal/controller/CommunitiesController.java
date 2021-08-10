package in.succinct.beckn.portal.controller;

import com.venky.swf.controller.ModelController;
import com.venky.swf.controller.annotations.SingleRecordAction;
import com.venky.swf.db.Database;
import com.venky.swf.path.Path;
import com.venky.swf.controller.TemplatedModelController;
import com.venky.swf.views.RedirectorView;
import com.venky.swf.views.View;
import in.succinct.beckn.portal.db.model.collab.Community;

public class CommunitiesController extends ModelController<Community> {
    public CommunitiesController(Path path) {
        super(path);
    }

    @SingleRecordAction(icon = "fa-handshake")
    public View join(long id){
        Community community = Database.getTable(Community.class).get(id);
        community.join();//Current user can join
        getPath().addInfoMessage("Congratulations! you are now part of " + community.getName());
        return new RedirectorView(getPath(),community.getCommunityPageUrl(),"");
    }
    @SingleRecordAction(icon = "fa-handshake-slash")
    public View leave(long id){
        Community community = Database.getTable(Community.class).get(id);
        community.leave();//Current user can join
        getPath().addInfoMessage("You have left " + community.getName());
        return redirectTo("index");
    }
}
