package in.succinct.beckn.portal.controller;

import com.venky.swf.controller.annotations.RequireLogin;
import com.venky.swf.path.Path;
import com.venky.swf.views.View;

public class UsersController extends com.venky.swf.plugins.collab.controller.UsersController {
    public UsersController(Path path) {
        super(path);
    }
    @RequireLogin(false)
    public View current() {
        if (getSessionUser() == null){
            return blank();
        }else {
            return show(getSessionUser());
        }
    }
}
