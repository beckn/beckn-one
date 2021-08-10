package in.succinct.beckn.portal.controller;

import com.venky.swf.controller.Controller;
import com.venky.swf.controller.annotations.RequireLogin;
import com.venky.swf.path.Path;
import com.venky.swf.controller.TemplatedController;
import com.venky.swf.plugins.templates.util.templates.TemplateEngine;
import com.venky.swf.views.View;

public class DashboardController extends Controller {
    public DashboardController(Path path) {
        super(path);
    }

    @Override
    public String getTemplateDirectory() {
        return super.getTemplateDirectory("dashboard");
    }

    @RequireLogin
    public View index(){
        if (TemplateEngine.getInstance(getTemplateDirectory()).exists("/html/index.html")){
            return html("index");
        }else {
            return super.dashboard();
        }
    }
}
