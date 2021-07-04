package in.succinct.beckn.portal.agents;

import com.venky.swf.db.Database;
import com.venky.swf.plugins.background.core.Task;
import com.venky.swf.plugins.background.core.TaskManager;
import in.succinct.beckn.portal.db.model.collab.Member;
import in.succinct.beckn.portal.db.model.collab.MyPost;
import in.succinct.beckn.portal.db.model.collab.Post;

import java.util.ArrayList;
import java.util.List;

public class InboxPost implements Task {
    public InboxPost(){

    }
    long postId = -1;
    public InboxPost(long postId){
        this.postId = postId;
    }
    @Override
    public void execute() {
        Post post = Database.getTable(Post.class).get(postId);
        List<Task> taskList = new ArrayList<>();
        for (Member member : post.getCommunity().getMembers()) {
            taskList.add(new UserInboxPost(post.getId(),member.getUserId()));
        }
        TaskManager.instance().executeAsync(taskList,true);
    }

    public static class UserInboxPost implements Task {
        public UserInboxPost(){

        }
        long userId;
        long postId;
        public UserInboxPost(long postId, long userId){
            this.postId = postId;
            this.userId = userId;
        }

        @Override
        public void execute() {
            MyPost myPost = Database.getTable(MyPost.class).newRecord();
            myPost.setPostId(this.postId);
            myPost.setUserId(this.userId);
            myPost = Database.getTable(MyPost.class).getRefreshed(myPost);
            myPost.save();
        }
    }
}
