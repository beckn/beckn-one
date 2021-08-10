package in.succinct.beckn.portal.configuration;

import com.venky.core.security.Crypt;
import com.venky.swf.configuration.Installer;
import com.venky.swf.db.Database;
import com.venky.swf.plugins.collab.db.model.CryptoKey;
import com.venky.swf.plugins.collab.db.model.config.Country;
import com.venky.swf.routing.Config;
import com.venky.swf.sql.Conjunction;
import com.venky.swf.sql.Expression;
import com.venky.swf.sql.Operator;
import com.venky.swf.sql.Select;
import in.succinct.beckn.portal.util.DomainMapper;
import in.succinct.beckn.registry.db.model.Subscriber;
import in.succinct.beckn.registry.db.model.SubscriberLocation;

import java.security.KeyPair;
import java.sql.Timestamp;
import java.util.List;

public class AppInstaller implements Installer {

    @Override
    public void install() {
        for (String type : new String[]{"bpp", "bap", "bg"}) {
            for (String domain : new String[]{"nic2004:52110"}) {
                generateBecknKeys(domain, type);
            }
        }
        updateProviderLocationsMinMaxLatLng();
    }

    private void updateProviderLocationsMinMaxLatLng() {
        Select select = new Select().from(SubscriberLocation.class);
        Expression where = new Expression(select.getPool(), Conjunction.AND);
        where.add(new Expression(select.getPool(),"RADIUS" , Operator.GT , 0));
        where.add(new Expression(select.getPool(),"LAT" , Operator.NE ));
        where.add(new Expression(select.getPool(),"LNG" , Operator.NE ));
        Expression minMax = new Expression(select.getPool(),Conjunction.OR);
        where.add(minMax);
        minMax.add(new Expression(select.getPool(),"MIN_LAT" , Operator.EQ ));
        minMax.add(new Expression(select.getPool(),"MIN_LNG" , Operator.EQ ));
        minMax.add(new Expression(select.getPool(),"MAX_LAT" , Operator.EQ ));
        minMax.add(new Expression(select.getPool(),"MAX_LNG" , Operator.EQ ));

        List<SubscriberLocation> facilities = select.where(where).execute();
        for (SubscriberLocation f :facilities){
            f.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            f.save(); //Let before save do the trick.
        }

    }


    public void generateBecknKeys(String domain, String type) {
        String subscriberId =  Config.instance().getHostName() + "." + DomainMapper.getMapping(domain) + "." + type;

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
            subscriber.setSubscriberUrl(Config.instance().getServerBaseUrl() + "/"+ DomainMapper.getMapping(domain).replace('-', '_') + "_"  +type );
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

