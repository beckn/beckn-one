package in.succinct.beckn.portal.controller;

import com.venky.swf.path.Path;
import com.venky.swf.plugins.templates.controller.TemplatedController;
import com.venky.swf.views.View;

public class GlobalController extends TemplatedController {
    public GlobalController(Path path) {
        super(path);
    }

    @Override
    public String getTemplateDirectory() {
        return super.getTemplateDirectory("global");
    }

    public View index(){
        return html("index");
    }
}
