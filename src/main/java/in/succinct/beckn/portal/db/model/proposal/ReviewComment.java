package in.succinct.beckn.portal.db.model.proposal;

import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.model.Model;

public interface ReviewComment extends Model {
    @PARTICIPANT
    public Long getGovernedAreaId();
    public void setGovernedAreaId(Long id);
    public GovernedArea getGovernedArea();


    @PARTICIPANT
    public Long getProposalId();
    public void setProposalId(Long id);
    public Proposal getProposal();

    public String getSection();
    public void setSection(String section);

    public String getComment();
    public void setComment(String comment);

}
