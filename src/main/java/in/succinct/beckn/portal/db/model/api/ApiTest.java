package in.succinct.beckn.portal.db.model.api;

import com.venky.swf.db.annotations.column.COLUMN_DEF;
import com.venky.swf.db.annotations.column.IS_NULLABLE;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.defaulting.StandardDefault;
import com.venky.swf.db.annotations.column.indexing.Index;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.annotations.column.ui.WATERMARK;
import com.venky.swf.db.model.Model;
import in.succinct.beckn.registry.db.model.Subscriber;
import in.succinct.beckn.registry.db.model.onboarding.NetworkRole;

import java.io.Reader;
import java.util.List;

public interface ApiTest extends Model {

    @PARTICIPANT
    public Long getCreatorUserId();
    
    @UNIQUE_KEY
    @Index
    @IS_NULLABLE(false)
    public Long getUseCaseId();
    public void setUseCaseId(Long id);
    public UseCase getUseCase();

    @UNIQUE_KEY
    @Index
    public String getTestName();
    public void setTestName(String testName);


    @Index
    public Long getCalledOnSubscriberId();
    public void setCalledOnSubscriberId(Long id);
    public NetworkRole getCalledOnSubscriber();

    @Index
    public Long getProxySubscriberId();
    public void setProxySubscriberId(Long  id);
    public NetworkRole getProxySubscriber();


    @WATERMARK("Enter variable values as json. e.g. {\"var1\" : \"value1\" , \"var2\" : \"value2\"}" )
    @Index
    public Reader getVariables();
    public void setVariables(Reader payload);

    @COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
    @Index
    public Boolean isSignatureNeeded();
    public void setSignatureNeeded(Boolean signatureNeeded);

    @COLUMN_DEF(StandardDefault.BOOLEAN_FALSE)
    public Boolean isPushedViaMessageQ();
    public void setPushedViaMessageQ(Boolean pushedViaMessageQ);


    public ApiCall execute();

    public List<ApiCall> getApiCalls();

}
