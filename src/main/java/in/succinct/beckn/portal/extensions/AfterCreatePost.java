package in.succinct.beckn.portal.extensions;

import com.venky.swf.db.extensions.AfterModelCreateExtension;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.beckn.portal.agents.InboxPost;
import in.succinct.beckn.portal.db.model.collab.Post;

public class AfterCreatePost extends AfterModelCreateExtension<Post> {
    static {
        registerExtension(new AfterCreatePost());
    }
    @Override
    public void afterCreate(Post model) {
        TaskManager.instance().executeAsync(new InboxPost(model.getId()),true);
    }
}
