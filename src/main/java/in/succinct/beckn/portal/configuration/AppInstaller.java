package in.succinct.beckn.portal.configuration;

import com.venky.core.security.Crypt;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.configuration.Installer;
import com.venky.swf.db.Database;
import com.venky.swf.plugins.collab.db.model.CryptoKey;
import com.venky.swf.routing.Config;
import in.succinct.beckn.Request;
import in.succinct.beckn.portal.util.DomainMapper;
import in.succinct.beckn.registry.db.model.onboarding.NetworkDomain;
import in.succinct.beckn.registry.db.model.onboarding.NetworkParticipant;
import in.succinct.beckn.registry.db.model.onboarding.NetworkRole;
import in.succinct.beckn.registry.db.model.onboarding.ParticipantKey;

import java.security.KeyPair;
import java.sql.Timestamp;

public class AppInstaller implements Installer {

    @Override
    public void install() {
        for (String type : new String[]{NetworkRole.SUBSCRIBER_TYPE_BAP,NetworkRole.SUBSCRIBER_TYPE_BG,NetworkRole.SUBSCRIBER_TYPE_BPP}) {
            for (String domain : new String[]{"nic2004:52110"}) {
                generateBecknKeys(domain, type);
            }
        }
        //updateProviderLocationsMinMaxLatLng();
    }

    public void generatePrivateKeys(){
        String participantId =  Config.instance().getHostName();
        CryptoKey key = CryptoKey.find(participantId+ ".k1",CryptoKey.PURPOSE_SIGNING);
        if (key.getRawRecord().isNewRecord()) {
            key.setAlgorithm(Request.SIGNATURE_ALGO);
            KeyPair pair = Crypt.getInstance().generateKeyPair(Request.SIGNATURE_ALGO, Request.SIGNATURE_ALGO_KEY_LENGTH);
            key.setPrivateKey(Crypt.getInstance().getBase64Encoded(pair.getPrivate()));
            key.setPublicKey(Crypt.getInstance().getBase64Encoded(pair.getPublic()));
            key.save();
        }

        CryptoKey encryptionKey = CryptoKey.find(participantId+ ".k1",CryptoKey.PURPOSE_ENCRYPTION);
        if (encryptionKey.getRawRecord().isNewRecord()) {
            encryptionKey.setAlgorithm(Request.ENCRYPTION_ALGO);
            KeyPair pair = Crypt.getInstance().generateKeyPair(Request.ENCRYPTION_ALGO, Request.ENCRYPTION_ALGO_KEY_LENGTH);
            encryptionKey.setPrivateKey(Crypt.getInstance().getBase64Encoded(pair.getPrivate()));
            encryptionKey.setPublicKey(Crypt.getInstance().getBase64Encoded(pair.getPublic()));
            encryptionKey.save();
        }
    }
    public NetworkParticipant generateParticipant(){
        NetworkParticipant participant = NetworkParticipant.find(Config.instance().getHostName());
        if (participant.getRawRecord().isNewRecord()) {
            participant.save();
        }
        return participant;
    }

    public void generateBecknKeys(String domain, String type) {
        generatePrivateKeys();
        NetworkParticipant participant = generateParticipant();

        String participantId =  participant.getParticipantId();
        CryptoKey signingKey = CryptoKey.find(participantId+".k1",CryptoKey.PURPOSE_SIGNING);
        CryptoKey encryptionKey = CryptoKey.find(participantId+".k1",CryptoKey.PURPOSE_ENCRYPTION);

        String keyId = String.format("%s.%s",participant.getParticipantId(),"k1");

        ParticipantKey participantKey = ParticipantKey.find(keyId);
        if (participantKey.getRawRecord().isNewRecord()){
            participantKey.setNetworkParticipantId(participant.getId());
            participantKey.setSigningPublicKey(signingKey.getPublicKey());
            participantKey.setEncrPublicKey(encryptionKey.getPublicKey());
            participantKey.setValidFrom(new Timestamp(System.currentTimeMillis()));
            participantKey.setValidUntil(new Timestamp(participantKey.getValidFrom().getTime() + (long)(10L * 365.25D * 24L * 60L * 60L * 1000L))) ; //10 years
            participantKey.setVerified(true);
            participantKey.save();
        }

        NetworkDomain networkDomain = NetworkDomain.find(domain);
        networkDomain.setDescription(DomainMapper.getMapping(domain));
        networkDomain.save();

        NetworkRole role = Database.getTable(NetworkRole.class).newRecord();
        role.setNetworkParticipantId(participant.getId());
        role.setType(type);
        if (!ObjectUtil.equals(type,NetworkRole.SUBSCRIBER_TYPE_BG)) {
            role.setNetworkDomainId(networkDomain.getId());
        }
        role.setUrl(Config.instance().getServerBaseUrl() + "/"+ DomainMapper.getMapping(domain).replace('-', '_') + "_"  +type.toLowerCase() );
        role.setStatus(NetworkRole.SUBSCRIBER_STATUS_SUBSCRIBED);
        role = Database.getTable(NetworkRole.class).getRefreshed(role);
        role.save();

    }

}

