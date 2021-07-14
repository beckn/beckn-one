package in.succinct.beckn.portal.extensions;

import com.venky.core.collections.SequenceSet;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.extensions.ParticipantExtension;
import com.venky.swf.db.model.User;
import in.succinct.beckn.portal.db.model.api.ApiTest;
import in.succinct.beckn.portal.db.model.api.UseCase;

import java.util.Arrays;
import java.util.List;

public class UseCaseParticipationExtension extends ParticipantExtension<UseCase> {
    static {
        registerExtension(new UseCaseParticipationExtension());
    }
    @Override
    protected List<Long> getAllowedFieldValues(User user, UseCase partiallyFilledModel, String fieldName) {
        if (ObjectUtil.equals( "CREATOR_USER_ID",fieldName)){
            return Arrays.asList(user.getId());
        }else if (ObjectUtil.equals("COMMUNITY_ID",fieldName)){
            List<Long> ret = new SequenceSet<>();
            if (partiallyFilledModel.getCommunityId() != null){
                if (partiallyFilledModel.getCommunity().isMember()){
                    ret.add(partiallyFilledModel.getCommunityId());
                }
            }else {
                user.getRawRecord().getAsProxy(in.succinct.beckn.portal.db.model.User.class).getMembers().forEach(m -> {
                    if (m.isActive()){
                        ret.add(m.getCommunityId());
                    }
                });
            }
            ret.add(null);
            return ret;
        }
        return null;
    }
}
