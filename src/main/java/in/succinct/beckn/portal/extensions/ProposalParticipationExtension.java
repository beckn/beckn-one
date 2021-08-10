package in.succinct.beckn.portal.extensions;

import com.venky.core.collections.SequenceSet;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.DATA_TYPE;
import com.venky.swf.db.extensions.ParticipantExtension;
import com.venky.swf.db.model.User;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.beckn.portal.db.model.collab.Post;
import in.succinct.beckn.portal.db.model.proposal.GovernedArea;
import in.succinct.beckn.portal.db.model.proposal.Proposal;
import in.succinct.beckn.portal.db.model.proposal.SubscribedTag;
import in.succinct.beckn.portal.db.model.proposal.Tag;
import in.succinct.beckn.portal.db.model.proposal.WorkingGroup;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProposalParticipationExtension extends ParticipantExtension<Proposal> {
    static {
        registerExtension(new ProposalParticipationExtension());
    }
    @Override
    protected List<Long> getAllowedFieldValues(User user, Proposal partiallyFilledModel, String fieldName) {
        if (ObjectUtil.equals( "AUTHOR_ID",fieldName)){
            return Arrays.asList(user.getId());
        }else if (ObjectUtil.equals( "GOVERNED_AREA_ID",fieldName)){
            List<Long> ret = new SequenceSet<>();
            if (!partiallyFilledModel.getReflector().isVoid(partiallyFilledModel.getGovernedAreaId())){
                GovernedArea area = partiallyFilledModel.getGovernedArea();
                for (WorkingGroup workingGroup : area.getWorkingGroups()) {
                    if (workingGroup.getCommunity().isMember(user)){
                        ret.add(area.getId());
                        break;
                    }
                }
            }else {
                List<Long> communityIds = new SequenceSet<>();
                user.getRawRecord().getAsProxy(in.succinct.beckn.portal.db.model.User.class).getMembers().forEach(m -> {
                    if (m.isActive()){
                        communityIds.add(m.getCommunityId());
                    }
                });
                Select select = new Select().from(WorkingGroup.class);
                select.where(new Expression(select.getPool(),"COMMUNITY_ID", Operator.IN, communityIds.toArray()));
                List<WorkingGroup> workingGroups = select.execute();
                for (WorkingGroup group : workingGroups){
                    ret.add(group.getGovernedAreaId());
                }
            }
            return ret;
        }else if ("SELF_ID".equals(fieldName)){
            List<Long> ret = new SequenceSet<>();
            if (partiallyFilledModel.getId() > 0 ){
                Set<Long> tagIds = new HashSet<>();
                partiallyFilledModel.getTags().forEach((t)->tagIds.add(t.getId()));
                Select select= new Select().from(SubscribedTag.class);
                List<SubscribedTag> subscribedTags = select.where(new Expression(select.getPool(), Conjunction.AND).
                        add(new Expression(select.getPool(),"USER_ID",Operator.EQ, user.getId())).
                        add(new Expression(select.getPool(),"TAG_ID",Operator.IN,tagIds.toArray()))).execute(1);
                if (!subscribedTags.isEmpty()){
                    ret.add(partiallyFilledModel.getId());
                }
            }
            return ret;
        }
        return null;
    }
}
