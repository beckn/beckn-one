package in.succinct.beckn.portal.db.model.api;

import com.venky.swf.db.table.ModelImpl;

public class BecknApiImpl extends ModelImpl<BecknApi> {
    public BecknApiImpl(BecknApi api){
        super(api);
    }

    public String getUniqueName(){
        if (getProxy().getRawRecord().isNewRecord()){
            return null;
        }
        BecknApi api = getProxy();
        StringBuilder name = new StringBuilder();
        name.append(api.getPlatform()).append("-");
        name.append(api.getName());
        return name.toString();
    }
}
