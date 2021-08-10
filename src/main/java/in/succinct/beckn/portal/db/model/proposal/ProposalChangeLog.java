package in.succinct.beckn.portal.db.model.proposal;

import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.model.Model;

import java.io.Reader;

public interface ProposalChangeLog extends Model {
    @IS_NULLABLE(false)
    public Long getProposalId();
    public void setProposalId(Long id);
    public Proposal getProposal();

    public Reader getPreviousValue();
    public void setPreviousValue(Reader reader);

}
