package in.succinct.beckn.portal.db.model;

import com.venky.core.security.Crypt;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.table.ModelImpl;
import in.succinct.beckn.Request;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCXDHPrivateKey;

import java.lang.reflect.Field;
import java.security.PrivateKey;
import java.util.Base64;

public class SubscriberImpl extends ModelImpl<Subscriber> {
    public SubscriberImpl(Subscriber subscriber){
        super(subscriber);
    }
    public SubscriberImpl(){
        super();
    }
    public String getSigningPrivateKey(){
        return getRawPrivateKey("Ed25519",String.format("%s.k1",getProxy().getSubscriberId()));
    }

    public String getEncrPrivateKey(){
        return getRawPrivateKey("X25519",String.format("%s.encrypt.k1",getProxy().getSubscriberId()));
    }

    public String getRawPrivateKey(String  algo ,String keyId) {
        Subscriber subscriber = getProxy();
        String pem = Request.getPrivateKey(subscriber.getSubscriberId(), keyId);
        try {
            if (!ObjectUtil.isVoid(pem)) {
                PrivateKey privateKey = Crypt.getInstance().getPrivateKey(algo, pem);
                if (privateKey instanceof BCEdDSAPrivateKey) {
                    BCEdDSAPrivateKey privateKey1 = (BCEdDSAPrivateKey) privateKey;
                    Field f = privateKey1.getClass().getDeclaredField("eddsaPrivateKey");
                    f.setAccessible(true); //BC Desnot expose this hence this reflection stuff.
                    Ed25519PrivateKeyParameters privateKeyParameters1 = (Ed25519PrivateKeyParameters) f.get(privateKey1);
                    return Base64.getEncoder().encodeToString(privateKeyParameters1.getEncoded());
                } else if (privateKey instanceof BCXDHPrivateKey) {
                    BCXDHPrivateKey privateKey1 = (BCXDHPrivateKey) privateKey;
                    Field f = privateKey1.getClass().getDeclaredField("xdhPrivateKey");
                    f.setAccessible(true); //BC Desnot expose this hence this reflection stuff.
                    X25519PrivateKeyParameters privateKeyParameters1 = (X25519PrivateKeyParameters) f.get(privateKey1);
                    return Base64.getEncoder().encodeToString(privateKeyParameters1.getEncoded());
                } else {
                    throw new RuntimeException("Key not  identifiable!");
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

}
