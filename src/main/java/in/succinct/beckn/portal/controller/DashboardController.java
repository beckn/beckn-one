package in.succinct.beckn.portal.controller;

import com.venky.swf.path.Path;
import com.venky.swf.plugins.templates.controller.TemplatedController;
import com.venky.swf.views.View;

public class DashboardController extends TemplatedController {
    public DashboardController(Path path) {
        super(path);
    }

    @Override
    public String getTemplateDirectory() {
        return super.getTemplateDirectory("dashboard");
    }

    public View index(){
        return html("index");
    }
}
