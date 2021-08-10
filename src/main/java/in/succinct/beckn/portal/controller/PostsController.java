package in.succinct.beckn.portal.controller;

import com.venky.core.string.StringUtil;
import com.venky.swf.controller.ModelController;
import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.path.Path;
import com.venky.swf.controller.TemplatedModelController;
import com.venky.swf.views.BytesView;
import com.venky.swf.views.View;
import in.succinct.beckn.portal.db.model.collab.Post;
import org.pegdown.PegDownProcessor;

import java.nio.charset.StandardCharsets;

public class PostsController extends ModelController<Post> {
    public PostsController(Path path) {
        super(path);
    }

    public View view(long id){
        Post post = Database.getTable(getModelClass()).get(id);
        String html = new PegDownProcessor().markdownToHtml(StringUtil.read(post.getBody()));
        return new BytesView(getPath(),html.getBytes(StandardCharsets.UTF_8), MimeType.TEXT_HTML);
    }

    public View show(long id){
        return view(id);
    }
}
