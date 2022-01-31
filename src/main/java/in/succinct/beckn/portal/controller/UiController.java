package in.succinct.beckn.portal.controller;

import com.venky.swf.controller.Controller;
import com.venky.swf.controller.annotations.RequireLogin;
import com.venky.swf.path.Path;
import com.venky.swf.views.RedirectorView;
import com.venky.swf.views.TemplateView;
import com.venky.swf.views.View;

import javax.activation.MimetypesFileTypeMap;

public class UiController extends Controller {
    public UiController(Path path) {
        super(path);
    }
    @Override
    @RequireLogin(value = false)
    public View index() {
        return new RedirectorView(getPath(),"dist");
    }
    @RequireLogin(false)
    public View dist(){
        return dist(null);
    }
    @RequireLogin(false)
    public View dist(String path){
        if (path == null || path.lastIndexOf('.') < 0) {
            return new TemplateView(getPath(), getTemplateDirectory() + "/dist", "index.html");
        }else {
            return load("/dist/"+path, MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(path));
        }
    }
}
