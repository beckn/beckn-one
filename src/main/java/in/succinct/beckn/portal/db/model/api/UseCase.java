package in.succinct.beckn.portal.db.model.api;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.ui.WATERMARK;
import com.venky.swf.db.model.Model;

import java.io.Reader;
import java.util.List;

public interface UseCase extends Model {
    @UNIQUE_KEY
    public Long getBecknApiId();
    public void setBecknApiId(Long id);
    public BecknApi getBecknApi();

    @UNIQUE_KEY
    public String getDomain();
    public void setDomain(String domain);

    @UNIQUE_KEY
    @WATERMARK("Give a name to this use case")
    public String getName();
    public void setName(String name);


    public String getDescription();
    public void setDescription(String description);


    public Reader getTemplateJson();
    public void setTemplateJson(Reader reader);

    List<ApiTest> getTests();
}
