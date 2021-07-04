package in.succinct.beckn.portal.controller;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.controller.annotations.SingleRecordAction;
import com.venky.swf.db.Database;
import com.venky.swf.path.Path;
import com.venky.swf.plugins.collab.db.model.CryptoKey;
import com.venky.swf.views.View;
import in.succinct.beckn.Request;
import in.succinct.beckn.registry.db.model.Subscriber;

public class SubscribersController extends in.succinct.beckn.registry.controller.SubscribersController {
    public SubscribersController(Path path) {
        super(path);
    }

    @SingleRecordAction(tooltip = "Generate Keys" , icon = "fa-key")
    public View generateKeys(long id){
        Subscriber subscriber = Database.getTable(Subscriber.class).get(id);
        if (subscriber == null){
            throw  new RuntimeException("Cannot identify subscriber!");
        }
        CryptoKey signKey = Database.getTable(CryptoKey.class).newRecord();
        {
            String[] pair = CryptoKey.generateKeyPair(Request.SIGNATURE_ALGO, Request.SIGNATURE_ALGO_KEY_LENGTH);
            signKey.setPrivateKey(pair[0]);
            signKey.setPublicKey(pair[1]);
            signKey.setAlias(subscriber.getSubscriberId() + ".k1");
            signKey = Database.getTable(CryptoKey.class).getRefreshed(signKey);
            signKey.save();
        }
        CryptoKey encryptKey = Database.getTable(CryptoKey.class).newRecord();
        {
            String[] pair = CryptoKey.generateKeyPair(Request.ENCRYPTION_ALGO, Request.ENCRYPTION_ALGO_KEY_LENGTH);
            encryptKey.setPrivateKey(pair[0]);
            encryptKey.setPublicKey(pair[1]);
            encryptKey.setAlias(subscriber.getSubscriberId() + ".encrypt.k1");
            encryptKey = Database.getTable(CryptoKey.class).getRefreshed(encryptKey);
            encryptKey.save();
        }
        subscriber.setEncrPublicKey(encryptKey.getPublicKey());
        subscriber.setSigningPublicKey(signKey.getPublicKey());
        if (!ObjectUtil.equals(subscriber.getStatus(),"SUBSCRIBED")){
            subscriber.setStatus("SUBSCRIBED");
        }
        subscriber.save();
        if (getIntegrationAdaptor() != null){
            return getIntegrationAdaptor().createResponse(getPath(),subscriber);
        }else{
            return back();
        }

    }

}
