package in.succinct.beckn.portal.db.model.proposal;

import com.venky.core.string.StringUtil;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.table.ModelImpl;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class ProposalImpl extends ModelImpl<Proposal> {
    public ProposalImpl(){
        super();
    }
    public ProposalImpl(Proposal proposal){
        super(proposal);
    }
    public void accept(){
        Proposal proposal = getProxy();
        proposal.setStatus(Proposal.DRAFT);
        proposal.save();
    }
    public void reject(){
        Proposal proposal = getProxy();
        proposal.setStatus(Proposal.REJECTED);
        proposal.save();
    }
    public void resubmit(){
        Proposal proposal = getProxy();
        proposal.setStatus(Proposal.PROPOSED);
        proposal.save();
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    String rejectReason;

    public List<Tag> getTags(){
        StringTokenizer tokenizer = new StringTokenizer(StringUtil.valueOf(getProxy().getKeyWords()));
        Set<String> tokens = new HashSet<>();
        while (tokenizer.hasMoreTokens()){
            String tk = tokenizer.nextToken();
            if (!ObjectUtil.isVoid(tk)){
                tokens.add(tk);
            }
        }
        List<Tag> tags = new ArrayList<>();
        tokens.forEach(name->tags.add(Tag.get(name)));
        return tags;
    }
}
