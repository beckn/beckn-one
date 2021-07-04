package in.succinct.beckn.portal.db.model.api;

import com.venky.swf.db.annotations.column.UNIQUE_KEY;
import com.venky.swf.db.annotations.column.validations.Enumeration;
import com.venky.swf.db.annotations.model.MENU;
import com.venky.swf.db.model.Model;

import java.util.List;

@MENU("Beckn")
public interface BecknApi extends Model {

    @UNIQUE_KEY
    @Enumeration("bap,bpp,bg,lreg,creg,rreg")
    public String getPlatform();
    public void setPlatform(String platform);

    @UNIQUE_KEY
    public String getName();
    public void setName(String name);

    public List<UseCase> getUseCases();
}
