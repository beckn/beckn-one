package in.succinct.beckn.portal.controller;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.venky.core.string.StringUtil;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.controller.annotations.RequireLogin;
import com.venky.swf.controller.annotations.SingleRecordAction;
import com.venky.swf.db.Database;
import com.venky.swf.db.annotations.column.ui.mimes.MimeType;
import com.venky.swf.path.Path;
import com.venky.swf.plugins.collab.db.model.CryptoKey;
import com.venky.swf.views.BytesView;
import com.venky.swf.views.View;
import in.succinct.beckn.Request;
import in.succinct.beckn.registry.db.model.Subscriber;
import in.succinct.beckn.registry.db.model.onboarding.NetworkParticipant;
import in.succinct.beckn.registry.db.model.onboarding.NetworkRole;
import in.succinct.beckn.registry.db.model.onboarding.ParticipantKey;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.util.Map;

public class SubscribersController extends in.succinct.beckn.registry.controller.SubscribersController {
    public SubscribersController(Path path) {
        super(path);
    }
    @SuppressWarnings("unchecked")
    @RequireLogin(value = false)
    public View sign(String headerName) throws Exception{
        Map<String,String> headers = getPath().getHeaders();
        String  uniqueKeyId = headers.get("unique_key_id");
        String  subscriberId = headers.get("subscriber_id");
        String payload = StringUtil.read(getPath().getInputStream());
        String header = new Request(payload).generateAuthorizationHeader(subscriberId,uniqueKeyId);
        JSONObject out = new JSONObject();
        out.put("Authorization",header);
        return new BytesView(getPath(),out.toString().getBytes(), MimeType.APPLICATION_JSON);
    }
    @RequireLogin(value = false)
    public View verify(String headerName) throws Exception{
        String payload = StringUtil.read(getPath().getInputStream());
        boolean verified = new Request(payload).verifySignature(headerName,getPath().getHeaders(),true);
        if (!verified){
            throw new RuntimeException("Verification failed!");
        }
        return getIntegrationAdaptor().createStatusResponse(getPath(),null,"Verification Successful!");
    }
    @SingleRecordAction(tooltip = "Generate Keys" , icon = "fa-key")
    public View generateKeys(String subscriberId){
        NetworkRole role = NetworkRole.find(subscriberId);

        if (role == null){
            throw  new RuntimeException("Cannot identify subscriber!");
        }
        NetworkParticipant participant = role.getNetworkParticipant();
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

        participantKey.setEncrPublicKey(encryptKey.getPublicKey());
        participantKey.setSigningPublicKey(signKey.getPublicKey());
        participantKey.setVerified(true);
        participantKey.setValidFrom(new Timestamp(System.currentTimeMillis()));
        participantKey.setValidUntil(new Timestamp(participantKey.getValidFrom().getTime() + (long)(10L * 365.25D * 24L * 60L * 60L * 1000L))) ; //10 years
        participantKey.save();
        if (getIntegrationAdaptor() != null){
            Subscriber subscriber = Subscriber.getSubscriber(participantKey,role,null);
            return getIntegrationAdaptor().createResponse(getPath(),subscriber);
        }else{
            return back();
        }

    }


}
