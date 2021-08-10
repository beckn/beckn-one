package in.succinct.beckn.portal.db.model.proposal;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.model.Model;

import java.util.List;

@MENU("Beckn")
public interface GovernedArea extends Model {
    @UNIQUE_KEY
    public String getName();
    public void setName(String name);

    public List<WorkingGroup> getWorkingGroups();

    public List<Proposal> getProposals();

    public List<ProposalTemplate> getProposalTemplates();
}
