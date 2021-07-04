package in.succinct.beckn.portal.extensions;

import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import in.succinct.beckn.portal.db.model.collab.MyPost;

public class BeforeSaveMyPost extends BeforeModelSaveExtension<MyPost> {
    static {
        registerExtension(new BeforeSaveMyPost());
    }
    @Override
    public void beforeSave(MyPost model) {
        if (model.getRawRecord().isFieldDirty("UN_READ")){
            if (model.isUnRead() ){
                if (!model.getRawRecord().isNewRecord()) {
                    model.getPost().getReadCount().decrement();
                }
            }else {
                model.getPost().getReadCount().increment();
            }
        }
        if (model.getRawRecord().isFieldDirty("USEFUL")){
            if (model.isUseful()){
                model.getPost().getUsefulCount().increment();
            }else {
                if (!model.getRawRecord().isNewRecord()) {
                    model.getPost().getUsefulCount().decrement();
                }
            }
        }

    }
}
