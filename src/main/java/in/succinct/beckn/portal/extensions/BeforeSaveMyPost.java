package in.succinct.beckn.portal.extensions;

import com.venky.core.util.Bucket;
import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import in.succinct.beckn.portal.db.model.collab.MyPost;
import in.succinct.beckn.portal.db.model.collab.Post;

public class BeforeSaveMyPost extends BeforeModelSaveExtension<MyPost> {
    static {
        registerExtension(new BeforeSaveMyPost());
    }
    @Override
    public void beforeSave(MyPost model) {
        Post post = model.getPost();
        Bucket readCount = model.getPost().getReadCount();
        if (readCount == null){
            readCount = new Bucket();
            post.setReadCount(readCount);
        }
        Bucket usefulCount = model.getPost().getUsefulCount();
        if (usefulCount == null){
            usefulCount = new Bucket();
            post.setUsefulCount(usefulCount);
        }
        if (!model.getRawRecord().isNewRecord() && model.getRawRecord().isFieldDirty("UN_READ")){
            if (model.isUnRead() ){
                readCount.decrement();
            }else {
                readCount.increment();
            }
        }
        if (!model.getRawRecord().isNewRecord() && model.getRawRecord().isFieldDirty("USEFUL")){
            if (model.isUseful()){
                usefulCount.increment();
            }else {
                usefulCount.decrement();
            }
        }
        post.save();

    }
}
