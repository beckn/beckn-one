package in.succinct.beckn.portal.extensions;

import com.venky.core.io.StringReader;
import com.venky.swf.db.Database;
import com.venky.swf.db.extensions.BeforeModelSaveExtension;
import com.venky.swf.db.model.io.ModelIOFactory;
import in.succinct.beckn.portal.db.model.proposal.Proposal;
import in.succinct.beckn.portal.db.model.proposal.ProposalChangeLog;
import org.json.simple.JSONObject;

public class BeforeSaveProposal extends BeforeModelSaveExtension<Proposal> {
    static {
        registerExtension(new BeforeSaveProposal());
    }
    @Override
    public void beforeSave(Proposal model) {
        if (!model.getRawRecord().isNewRecord()){
            JSONObject audit = new JSONObject();
            ModelIOFactory.getWriter(Proposal.class,JSONObject.class).write(model,audit, null);
            ProposalChangeLog log = Database.getTable(ProposalChangeLog.class).newRecord();
            log.setProposalId(model.getId());
            log.setPreviousValue(new StringReader(audit.toString())); //Store all Values.!!
            log.save();
        }
    }
}
