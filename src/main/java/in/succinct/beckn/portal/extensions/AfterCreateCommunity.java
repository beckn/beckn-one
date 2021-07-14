package in.succinct.beckn.portal.extensions;

import com.venky.swf.db.extensions.AfterModelCreateExtension;
import in.succinct.beckn.portal.db.model.collab.Community;

public class AfterCreateCommunity extends AfterModelCreateExtension<Community> {
    static {
        registerExtension(new AfterCreateCommunity());
    }
    @Override
    public void afterCreate(Community model) {
        model.addAdmin(model.getCreatorUserId());
    }
}
