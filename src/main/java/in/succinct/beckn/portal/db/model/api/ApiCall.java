package in.succinct.beckn.portal.db.model.api;

import com.venky.swf.db.annotations.column.COLUMN_SIZE;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.pm.PARTICIPANT;
import com.venky.swf.db.model.Model;

public interface ApiCall extends Model {
    @PARTICIPANT
    public Long getCreatorUserId();


    public Long getApiTestId();
    public void setApiTestId(Long id);
    public ApiTest getApiTest();

    @UNIQUE_KEY
    public String getMessageId();
    public void setMessageId(String messageId);


    @COLUMN_SIZE(2048)
    public String getRequestPayLoad();
    public void setRequestPayLoad(String payLoad);

    @COLUMN_SIZE(2048)
    public String getRequestHeaders();
    public void setRequestHeaders(String headers);


    @COLUMN_SIZE(2 * 4096)
    public String getResponsePayload();
    public void setResponsePayload(String payload);

    @COLUMN_SIZE(2048)
    public String getResponseHeaders();
    public void setResponseHeaders(String headers);


    @COLUMN_SIZE(4 * 4096)
    public String getCallBackPayload();
    public void setCallBackPayload(String payLoad);


    @COLUMN_SIZE(2048)
    public String getCallBackHeaders();
    public void setCallBackHeaders(String headers);


    public void execute();
}
