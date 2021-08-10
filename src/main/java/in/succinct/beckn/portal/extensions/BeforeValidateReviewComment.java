package in.succinct.beckn.portal.extensions;

import com.venky.swf.db.extensions.BeforeModelValidateExtension;
import in.succinct.beckn.portal.db.model.proposal.ReviewComment;

public class BeforeValidateReviewComment extends BeforeModelValidateExtension<ReviewComment> {
    static {
        registerExtension(new BeforeValidateReviewComment());
    }
    @Override
    public void beforeValidate(ReviewComment model) {
        if (model.getReflector().isVoid(model.getGovernedAreaId()) && !model.getReflector().isVoid(model.getProposalId())){
            model.setGovernedAreaId(model.getProposal().getGovernedAreaId());
        }
    }
}
