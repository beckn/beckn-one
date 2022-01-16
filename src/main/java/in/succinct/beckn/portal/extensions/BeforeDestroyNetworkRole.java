package in.succinct.beckn.portal.extensions;

import com.venky.swf.db.extensions.BeforeModelDestroyExtension;
import com.venky.swf.db.model.Model;
import in.succinct.beckn.portal.db.model.api.NetworkRole;

public class BeforeDestroyNetworkRole extends BeforeModelDestroyExtension<NetworkRole> {
    static {
        registerExtension(new BeforeDestroyNetworkRole());
    }
    @Override
    public void beforeDestroy(NetworkRole model) {
        model.getTestsAsCaller().forEach(Model::destroy);
        model.getTestsAsCalled().forEach(Model::destroy);
    }
}
