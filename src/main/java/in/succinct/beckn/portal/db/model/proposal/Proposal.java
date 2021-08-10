package in.succinct.beckn.portal.db.model.proposal;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.COLUMN_NAME;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.model.Model;
import in.succinct.beckn.portal.db.model.User;

import java.io.Reader;
import java.util.List;

public interface Proposal extends Model {
    @IS_VIRTUAL
    @COLUMN_NAME("ID")
    @PARTICIPANT
    public Long getSelfId();
    public void setSelfId(Long id);
    public Proposal getSelf();

    public List<ProposalChangeLog> getChangeLogs();

    @PARTICIPANT
    @IS_NULLABLE(false)
    public Long getGovernedAreaId();
    public void setGovernedAreaId(Long id);
    public GovernedArea getGovernedArea();

    public static final String PROPOSED = "Proposed Standard";
    public static final String DRAFT = "Protocol Draft";
    public static final String STANDARD_REQUIRED = "Protocol Standard Required";
    public static final String STANDARD_RECOMMENDED = "Protocol Standard Recommended";
    public static final String STANDARD_NOT_RECOMMENDED = "Protocol Standard Not-Recommended";
    public static final String REJECTED = "Rejected";



    @Enumeration(PROPOSED + "," +  DRAFT + "," + STANDARD_REQUIRED +"," + STANDARD_RECOMMENDED + ","  + STANDARD_NOT_RECOMMENDED + "," + REJECTED)
    @COLUMN_DEF(value = StandardDefault.SOME_VALUE,args = PROPOSED)
    public String getStatus();
    public void  setStatus(String status);

    public  String getTitle();
    public void setTitle(String title);

    @PARTICIPANT
    @COLUMN_DEF(StandardDefault.CURRENT_USER)
    @IS_NULLABLE(false)
    public Long getAuthorId();
    public void setAuthorId(Long id);
    public User getAuthor();

    public String getKeyWords();
    public void setKeyWords(String tags);

    @IS_VIRTUAL
    public List<Tag> getTags();


    /**
     * Is Markdown body
     * @return markdown body
     */
    public Reader getBody();
    public void setBody(Reader body);

    //Only proposal admins can accept
    public void accept(); //Moves to DRAFT
    public void reject();
    public void resubmit();

    public String getProposalNumber();
    public void setProposalNumber(String proposalNumber);


    public Integer getVersionNumber();
    public void setVersionNumber(Integer versionNumber);

    public Integer getPullRequestNumber();
    public void setPullRequestNumber(Integer pullRequestNumber);

    public List<ReviewComment> getReviewComments();

    @IS_VIRTUAL
    public String getRejectReason();
    public void setRejectReason(String rejectReason);



}
