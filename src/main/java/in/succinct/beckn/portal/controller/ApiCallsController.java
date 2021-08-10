package in.succinct.beckn.portal.controller;

import com.venky.swf.controller.ModelController;
import com.venky.swf.controller.annotations.SingleRecordAction;
import com.venky.swf.db.Database;
import com.venky.swf.path.Path;
import com.venky.swf.views.View;
import in.succinct.beckn.portal.db.model.api.ApiCall;

public class ApiCallsController extends ModelController<ApiCall> {
    public ApiCallsController(Path path) {
        super(path);
    }

    @SingleRecordAction(icon = "fa-bolt", tooltip = "Execute the test")
    public View execute(long id){
        ApiCall call = Database.getTable(ApiCall.class).lock(id);
        call.execute();
        return back();
    }

}
