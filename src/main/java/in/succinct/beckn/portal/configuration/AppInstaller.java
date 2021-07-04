package in.succinct.beckn.portal.configuration;

import com.venky.core.security.Crypt;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.configuration.Installer;
import com.venky.swf.db.Database;
import com.venky.swf.plugins.collab.db.model.CryptoKey;
import com.venky.swf.plugins.collab.db.model.config.Country;
import com.venky.swf.routing.Config;
import in.succinct.beckn.registry.db.model.Subscriber;

import java.security.KeyPair;
import java.sql.Timestamp;

import static com.venky.swf.plugins.collab.db.model.config.City.findByCountryAndStateAndName;

public class AppInstaller implements Installer {

    @Override
    public void install() {
        for (String type : new String[]{"bpp", "bap"}) {
            for (String domain : new String[]{"local-retail"}) {
                generateBecknKeys(domain, type);
            }
        }
    }

    public void generateBecknKeys(String domain, String type) {
        String subscriberId =  Config.instance().getHostName() + "." + domain + "." + type;

        CryptoKey key = Database.getTable(CryptoKey.class).newRecord();
        key.setAlias(subscriberId + ".k1");
        key = Database.getTable(CryptoKey.class).getRefreshed(key);
        if (key.getRawRecord().isNewRecord()) {
            KeyPair pair = Crypt.getInstance().generateKeyPair("Ed25519", 256);
            key.setPrivateKey(Crypt.getInstance().getBase64Encoded(pair.getPrivate()));
            key.setPublicKey(Crypt.getInstance().getBase64Encoded(pair.getPublic()));
            key.save();
        }

        CryptoKey encryptionKey = Database.getTable(CryptoKey.class).newRecord();
        encryptionKey.setAlias(subscriberId+ ".encrypt.k1");
        encryptionKey = Database.getTable(CryptoKey.class).getRefreshed(encryptionKey);
        if (encryptionKey.getRawRecord().isNewRecord()) {
            KeyPair pair = Crypt.getInstance().generateKeyPair(Crypt.KEY_ALGO, 2048);
            encryptionKey.setPrivateKey(Crypt.getInstance().getBase64Encoded(pair.getPrivate()));
            encryptionKey.setPublicKey(Crypt.getInstance().getBase64Encoded(pair.getPublic()));
            encryptionKey.save();
        }

        Subscriber subscriber = Database.getTable(Subscriber.class).newRecord();
        subscriber.setSubscriberId(subscriberId);
        subscriber.setType(type);
        subscriber = Database.getTable(Subscriber.class).getRefreshed(subscriber);
        if (subscriber.getRawRecord().isNewRecord()) {
            subscriber.setStatus("SUBSCRIBED");
            subscriber.setSubscriberUrl(Config.instance().getServerBaseUrl() + "/"+ domain.replace('-', '_') + "_"  +type );
            subscriber.setDomain(domain);

            subscriber.setCountryId(Country.findByName("India").getId());

            subscriber.setSigningPublicKey(key.getPublicKey());
            subscriber.setEncrPublicKey(encryptionKey.getPublicKey());
            subscriber.setValidFrom(new Timestamp(System.currentTimeMillis()));
            subscriber.setValidUntil(new Timestamp(subscriber.getValidFrom().getTime() + (long) (10L * 365.25D * 24L * 60L * 60L * 1000L))); //10 years
            subscriber.save();
        }
    }
}

