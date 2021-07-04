package in.succinct.beckn.portal.db.model.api;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.ui.WATERMARK;
import com.venky.swf.db.model.Model;
import in.succinct.beckn.registry.db.model.Subscriber;

import java.io.Reader;
import java.util.List;

public interface ApiTest extends Model {
    @UNIQUE_KEY
    public Long getUseCaseId();
    public void setUseCaseId(Long id);
    public UseCase getUseCase();

    @UNIQUE_KEY
    public String getTestName();
    public void setTestName(String testName);

    public Long getCalledOnSubscriberId();
    public void setCalledOnSubscriberId(Long id);
    public Subscriber getCalledOnSubscriber();

    public Long getProxySubscriberId();
    public void setProxySubscriberId(Long  id);
    public Subscriber getProxySubscriber();


    @WATERMARK("Enter variable values as json. e.g. {\"var1\" : \"value1\" , \"var2\" : \"value2\"}" )
    public Reader getVariables();
    public void setVariables(Reader payload);


    public ApiCall execute();

    public List<ApiCall> getApiCalls();

}
