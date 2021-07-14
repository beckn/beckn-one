package in.succinct.beckn.portal.db.model.api;

import com.venky.swf.db.annotations.column.IS_VIRTUAL;
import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.annotations.model.HAS_DESCRIPTION_FIELD;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.model.Model;

import java.util.List;

@MENU("Beckn")
@HAS_DESCRIPTION_FIELD("UNIQUE_NAME")
public interface BecknApi extends Model {
    @IS_VIRTUAL
    public String getUniqueName();

    @UNIQUE_KEY
    @Enumeration("bap,bpp,bg,lreg,creg,rreg")
    public String getPlatform();
    public void setPlatform(String platform);

    @UNIQUE_KEY
    public String getName();
    public void setName(String name);

    public List<UseCase> getUseCases();
}
