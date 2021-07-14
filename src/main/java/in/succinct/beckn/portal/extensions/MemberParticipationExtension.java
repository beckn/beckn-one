package in.succinct.beckn.portal.extensions;

import com.venky.core.collections.SequenceSet;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.extensions.ParticipantExtension;
import com.venky.swf.db.model.User;
import in.succinct.beckn.portal.db.model.api.UseCase;
import in.succinct.beckn.portal.db.model.collab.Member;

import java.util.Arrays;
import java.util.List;

public class MemberParticipationExtension extends ParticipantExtension<Member> {
    static {
        registerExtension(new MemberParticipationExtension());
    }
    @Override
    protected List<Long> getAllowedFieldValues(User user, Member partiallyFilledModel, String fieldName) {
        if (ObjectUtil.equals( "USER_ID",fieldName)){
            return Arrays.asList(user.getId());
        }else if (ObjectUtil.equals("COMMUNITY_ID",fieldName)){
            List<Long> ret = new SequenceSet<>();
            user.getRawRecord().getAsProxy(in.succinct.beckn.portal.db.model.User.class).getMembers().forEach(m -> ret.add(m.getCommunityId()));
            return ret;
        }
        return null;
    }
}
