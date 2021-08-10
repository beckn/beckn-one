package in.succinct.beckn.portal.db.model.proposal;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.model.Model;
import in.succinct.beckn.portal.db.model.collab.Community;

public interface WorkingGroup extends Model {
    @UNIQUE_KEY
    @IS_NULLABLE(false)
    public Long getGovernedAreaId();
    public void setGovernedAreaId(Long id);
    public GovernedArea getGovernedArea();

    @UNIQUE_KEY
    @IS_NULLABLE(false)
    public String getName();
    public void setName(String name);

    @IS_NULLABLE(false)
    public Long getCommunityId();
    public void setCommunityId(Long id);
    public Community getCommunity();

    @COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
    public boolean isAreaDirector();
    public void setAreaDirector(boolean areaDirector);

    @COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
    public boolean isProposalAdmin();
    public void setProposalAdmin(boolean proposalReviewer);

    @COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
    public boolean isProposalContributor();
    public void setProposalContributor(boolean proposalContributor);




}
