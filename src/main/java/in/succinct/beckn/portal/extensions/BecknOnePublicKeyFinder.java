package in.succinct.beckn.portal.extensions;

import com.venky.core.util.ObjectHolder;
import com.venky.extension.Extension;
import com.venky.extension.Registry;
import in.succinct.beckn.registry.db.model.onboarding.ParticipantKey;

public class BecknOnePublicKeyFinder implements Extension {
    static {
        Registry.instance().registerExtension("beckn.public.key.get",new BecknOnePublicKeyFinder());
    }
    @Override
    public void invoke(Object... context) {
        String subscriber_id = (String)context[0];
        String uniqueKeyId = (String)context[1];
        ObjectHolder<String> publicKeyHolder = (ObjectHolder<String>) context[2];
        ParticipantKey key = ParticipantKey.find(uniqueKeyId);
        publicKeyHolder.set(key.getSigningPublicKey());
    }
}
