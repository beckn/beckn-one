package in.succinct.beckn.portal.controller;

import com.venky.swf.controller.ModelController;
import com.venky.swf.controller.annotations.SingleRecordAction;
import com.venky.swf.db.Database;
import com.venky.swf.path.Path;
import com.venky.swf.controller.TemplatedModelController;
import com.venky.swf.views.RedirectorView;
import com.venky.swf.views.View;
import in.succinct.beckn.portal.db.model.api.ApiCall;
import in.succinct.beckn.portal.db.model.api.ApiTest;

public class ApiTestsController extends ModelController<ApiTest> {
    public ApiTestsController(Path path) {
        super(path);
    }

    @SingleRecordAction(icon = "fa-bolt", tooltip = "Execute the test")
    public View execute(long id){
        ApiTest apiTest = Database.getTable(ApiTest.class).get(id);
        ApiCall call = apiTest.execute();
        return new RedirectorView(getPath(),getPath().getTarget().replace("execute","show")+"/api_calls", "show/"+call.getId());

    }

}
