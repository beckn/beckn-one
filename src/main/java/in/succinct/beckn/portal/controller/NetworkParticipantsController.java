package in.succinct.beckn.portal.controller;

import com.venky.swf.controller.ModelController;
import com.venky.swf.controller.annotations.SingleRecordAction;
import com.venky.swf.db.Database;
import com.venky.swf.path.Path;
import com.venky.swf.plugins.collab.db.model.CryptoKey;
import com.venky.swf.views.RedirectorView;
import com.venky.swf.views.View;
import in.succinct.beckn.Request;
import in.succinct.beckn.registry.db.model.onboarding.NetworkParticipant;
import in.succinct.beckn.registry.db.model.onboarding.ParticipantKey;

import java.sql.Timestamp;

public class NetworkParticipantsController extends ModelController<NetworkParticipant> {
    public NetworkParticipantsController(Path path) {
        super(path);
    }
    @SingleRecordAction(tooltip = "Generate Keys" , icon = "fa-key")
    public View generateKeys(long id){
        NetworkParticipant participant = Database.getTable(NetworkParticipant.class).get(id);

        String participantId = participant.getParticipantId();
        String alias = participantId + ".k"+ (participant.getParticipantKeys().size() + 1);

        CryptoKey signKey = CryptoKey.find(alias,CryptoKey.PURPOSE_SIGNING);
        {
            String[] pair = CryptoKey.generateKeyPair(Request.SIGNATURE_ALGO, Request.SIGNATURE_ALGO_KEY_LENGTH);
            signKey.setPrivateKey(pair[0]);
            signKey.setPublicKey(pair[1]);
            signKey.setAlgorithm(Request.SIGNATURE_ALGO);
            signKey.save();
        }
        CryptoKey encryptKey = CryptoKey.find(alias,CryptoKey.PURPOSE_ENCRYPTION);
        {
            String[] pair = CryptoKey.generateKeyPair(Request.ENCRYPTION_ALGO, Request.ENCRYPTION_ALGO_KEY_LENGTH);
            encryptKey.setPrivateKey(pair[0]);
            encryptKey.setPublicKey(pair[1]);
            encryptKey.setAlgorithm(Request.ENCRYPTION_ALGO);
            encryptKey.save();
        }
        ParticipantKey participantKey = ParticipantKey.find(alias);
        participantKey.setNetworkParticipantId(participant.getId());
        participantKey.setEncrPublicKey(encryptKey.getPublicKey());
        participantKey.setSigningPublicKey(signKey.getPublicKey());
        participantKey.setVerified(true);
        participantKey.setValidFrom(new Timestamp(System.currentTimeMillis()));
        participantKey.setValidUntil(new Timestamp(participantKey.getValidFrom().getTime() + (long)(10L * 365.25D * 24L * 60L * 60L * 1000L))) ; //10 years
        participantKey.save();
        return new RedirectorView(getPath(),"/network_participants",String.format("show/%d",participant.getId()));

    }
}
