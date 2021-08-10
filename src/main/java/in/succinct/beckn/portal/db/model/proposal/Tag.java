package in.succinct.beckn.portal.db.model.proposal;

import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.model.Model;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.TaskManager;

import java.util.List;

public interface Tag extends Model {
    @UNIQUE_KEY
    public String getName();
    public void setName(String name);

    List<SubscribedTag> getSubscribedUsers();

    public static void create(String name){
        TaskManager.instance().executeAsync((Task) () -> get(name),false);
    }
    public static Tag get(String name){
        Tag tag = Database.getTable(Tag.class).newRecord();
        tag.setName(name);
        tag = Database.getTable(Tag.class).getRefreshed(tag);
        if (tag.getRawRecord().isNewRecord()) {
            tag.save();
        }
        return tag;
    }

}
