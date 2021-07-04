package in.succinct.beckn.portal.extensions;

import com.venky.core.util.ObjectHolder;
import com.venky.core.util.ObjectUtil;
import com.venky.extension.Extension;
import com.venky.extension.Registry;
import com.venky.swf.db.Database;
import in.succinct.beckn.registry.db.model.Subscriber;

import java.util.List;
import java.util.Optional;

public class BecknOnePublicKeyFinder implements Extension {
    static {
        Registry.instance().registerExtension("beckn.public.key.get",new BecknOnePublicKeyFinder());
    }
    @Override
    public void invoke(Object... context) {
        String subscriber_id = (String)context[0];
        String uniqueKeyId = (String)context[1];
        ObjectHolder<String> publicKeyHolder = (ObjectHolder<String>) context[2];

        Subscriber criteria = Database.getTable(Subscriber.class).newRecord();
        criteria.setSubscriberId(subscriber_id);

        List<Subscriber> subscriberList = Subscriber.lookup(criteria,1);
        Optional<Subscriber> subscriber = subscriberList.stream().filter(s-> ObjectUtil.equals(s.getStatus(),Subscriber.SUBSCRIBER_STATUS_SUBSCRIBED)).findAny();
        if (subscriber.isPresent()){
            publicKeyHolder.set(subscriber.get().getSigningPublicKey());
        }
    }
}
