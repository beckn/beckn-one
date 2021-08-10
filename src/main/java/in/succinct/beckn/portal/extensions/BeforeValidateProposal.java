package in.succinct.beckn.portal.extensions;

import com.venky.core.collections.SequenceSet;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.Database;
import com.venky.swf.db.extensions.BeforeModelValidateExtension;
import com.venky.swf.db.model.User;
import com.venky.swf.db.table.Table;
import com.venky.swf.plugins.sequence.db.model.SequentialNumber;
import in.succinct.beckn.portal.db.model.proposal.Proposal;
import in.succinct.beckn.portal.db.model.proposal.ReviewComment;
import in.succinct.beckn.portal.db.model.proposal.Tag;
import in.succinct.beckn.portal.db.model.proposal.WorkingGroup;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class BeforeValidateProposal extends BeforeModelValidateExtension<Proposal> {
    static {
        registerExtension(new BeforeValidateProposal());
    }
    @Override
    public void beforeValidate(Proposal model) {
        if (model.getRawRecord().isFieldDirty("STATUS")){
            User currentUser = Database.getInstance().getCurrentUser();
            if (currentUser == null){
                throw new UnsupportedOperationException();
            }
            boolean allowed = false;
            for (WorkingGroup workingGroup :
                    model.getGovernedArea().getWorkingGroups()){
                if (workingGroup.isProposalAdmin() || workingGroup.isAreaDirector()){
                    if (workingGroup.getCommunity().isMember()){
                        allowed =true ;
                        break;
                    }
                }
            }
            if (!allowed){
                throw new UnsupportedOperationException("Only Area Directors or Proposal Admins can change status");
            }
            if (ObjectUtil.equals(model.getStatus(),Proposal.DRAFT)){
                if (ObjectUtil.isVoid(model.getProposalNumber())){
                    model.setProposalNumber("CP-"+SequentialNumber.get("CP").next());
                }
                if (model.getVersionNumber() == null){
                    model.setVersionNumber(-1);
                }
            }
            if (ObjectUtil.equals(model.getStatus(),Proposal.REJECTED)){
                if (ObjectUtil.equals(model.getStatus(),Proposal.REJECTED)) {
                    throw new RuntimeException("Please specify reason for rejection");
                }else {
                    ReviewComment comment = Database.getTable(ReviewComment.class).newRecord();
                    comment.setProposalId(model.getId());
                    comment.setGovernedAreaId(model.getGovernedAreaId());
                    comment.setComment(model.getRejectReason());
                    comment.save();
                }
            }
            if (ObjectUtil.equals(model.getStatus(),Proposal.STANDARD_REQUIRED) || ObjectUtil.equals(model.getStatus(),Proposal.STANDARD_RECOMMENDED)){
                if (ObjectUtil.isVoid(model.getPullRequestNumber())){
                    model.setPullRequestNumber(0);
                }
                model.setPullRequestNumber(model.getPullRequestNumber() + 1);
            }
        }
        if (model.getRawRecord().isFieldDirty("BODY") && ObjectUtil.equals(Proposal.DRAFT,model.getStatus())){
            model.setVersionNumber(model.getVersionNumber() + 1);
        }
        if (model.getRawRecord().isFieldDirty("KEY_WORDS")){
            StringTokenizer tokenizer = new StringTokenizer(model.getKeyWords()," ,");
            Set<String> tags = new HashSet<>();
            while (tokenizer.hasMoreTokens()){
                String tk = tokenizer.nextToken();
                if (!ObjectUtil.isVoid(tk)){
                    tags.add(tk);
                }
            }
            tags.forEach(Tag::create);
        }

    }
}
