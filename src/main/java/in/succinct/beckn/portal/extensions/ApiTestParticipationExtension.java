package in.succinct.beckn.portal.extensions;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.extensions.ParticipantExtension;
import com.venky.swf.db.model.User;
import in.succinct.beckn.portal.db.model.api.ApiTest;

import java.util.Arrays;
import java.util.List;

public class ApiTestParticipationExtension extends ParticipantExtension<ApiTest> {
    static {
        registerExtension(new ApiTestParticipationExtension());
    }
    @Override
    protected List<Long> getAllowedFieldValues(User user, ApiTest partiallyFilledModel, String fieldName) {
        if (ObjectUtil.equals( "CREATOR_USER_ID",fieldName)){
            return Arrays.asList(user.getId());
        }
        return null;
    }
}
