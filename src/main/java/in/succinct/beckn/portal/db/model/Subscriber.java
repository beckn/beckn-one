package in.succinct.beckn.portal.db.model;

import com.venky.swf.db.annotations.column.IS_VIRTUAL;

public interface Subscriber extends in.succinct.beckn.registry.db.model.Subscriber {
    @IS_VIRTUAL
    public String getSigningPrivateKey();

    @IS_VIRTUAL
    public String getEncrPrivateKey();

}
