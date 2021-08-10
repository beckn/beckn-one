package in.succinct.beckn.portal.db.model.proposal;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.model.Model;

import java.io.Reader;

public interface ProposalTemplate extends Model {
    @UNIQUE_KEY
    public Long getGovernedAreaId();
    public void setGovernedAreaId(Long id);
    public GovernedArea getGovernedArea();
    /**
     * Is Markdown body
     * @return markdown body
     */
    public Reader getBody();
    public void setBody(Reader body);


}
