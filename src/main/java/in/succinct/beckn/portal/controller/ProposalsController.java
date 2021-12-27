package in.succinct.beckn.portal.controller;

import com.venky.core.string.StringUtil;
import com.venky.swf.controller.ModelController;
import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.path.Path;
import com.venky.swf.views.BytesView;
import com.venky.swf.views.View;
import in.succinct.beckn.portal.db.model.proposal.Proposal;
import in.succinct.beckn.portal.db.model.proposal.ProposalTemplate;
import com.venky.swf.plugins.wiki.util.PegDownProcessor;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ProposalsController extends ModelController<Proposal> {
    public ProposalsController(Path path) {
        super(path);
    }

    public View view(long id){
        Proposal post = Database.getTable(getModelClass()).get(id);
        String html = new PegDownProcessor().markdownToHtml(StringUtil.read(post.getBody()));
        return new BytesView(getPath(),html.getBytes(StandardCharsets.UTF_8), MimeType.TEXT_HTML);
    }

    public View show(long id){
        return view(id);
    }

    public View accept(long id){
        Proposal proposal = Database.getTable(Proposal.class).get(id);
        proposal.accept();
        if (getIntegrationAdaptor() == null){
            return back();
        }else {
            return getIntegrationAdaptor().createStatusResponse(getPath(),null,"Accepted");
        }
    }

    @Override
    protected void defaultFields(Proposal record){
        super.defaultFields(record);
        if (record.getGovernedAreaId() != null && record.getRawRecord().isNewRecord()){
            List<ProposalTemplate> proposalTemplates = record.getGovernedArea().getProposalTemplates();
            if (!proposalTemplates.isEmpty()){
                ProposalTemplate template = proposalTemplates.get(0);
                record.setBody(template.getBody());
            }
        }
    }
}
