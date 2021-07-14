package in.succinct.beckn.portal.extensions;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.extensions.ParticipantExtension;
import com.venky.swf.db.model.User;
import in.succinct.beckn.portal.db.model.api.UseCase;
import in.succinct.beckn.portal.db.model.collab.MyPost;

import java.util.Arrays;
import java.util.List;

public class MyPostParticipationExtension extends ParticipantExtension<MyPost> {
    static {
        registerExtension(new MyPostParticipationExtension());
    }
    @Override
    protected List<Long> getAllowedFieldValues(User user, MyPost partiallyFilledModel, String fieldName) {
        if (ObjectUtil.equals( "USER_ID",fieldName)){
            return Arrays.asList(user.getId());
        }
        return null;
    }
}
