package in.succinct.beckn.portal.extensions;

import com.venky.core.collections.SequenceSet;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.extensions.ParticipantExtension;
import com.venky.swf.db.model.User;
import com.venky.swf.pm.DataSecurityFilter;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.beckn.portal.db.model.proposal.GovernedArea;
import in.succinct.beckn.portal.db.model.proposal.Proposal;
import in.succinct.beckn.portal.db.model.proposal.ReviewComment;
import in.succinct.beckn.portal.db.model.proposal.WorkingGroup;

import java.util.Arrays;
import java.util.List;

public class ReviewCommentParticipationExtension extends ParticipantExtension<ReviewComment> {
    static {
        registerExtension(new ReviewCommentParticipationExtension());
    }
    @Override
    protected List<Long> getAllowedFieldValues(User user, ReviewComment partiallyFilledModel, String fieldName) {
        if (ObjectUtil.equals( "GOVERNED_AREA_ID",fieldName)){
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
        }else if (ObjectUtil.equals("PROPOSAL_ID",fieldName)){
            List<Long> ret = new SequenceSet<>();
            if (partiallyFilledModel.getProposalId() != null){
                if (partiallyFilledModel.isAccessibleBy(user)){
                    ret.add(partiallyFilledModel.getProposalId());
                }
            }else {
                //Can return null too.
                List<Proposal> proposals = new Select().from(Proposal.class).
                        where(new Expression(getReflector().getPool(),"AUTHOR_ID",Operator.EQ,user.getId())).execute();
                ret.addAll(DataSecurityFilter.getIds(proposals));
            }
        }
        return null;
    }
}
