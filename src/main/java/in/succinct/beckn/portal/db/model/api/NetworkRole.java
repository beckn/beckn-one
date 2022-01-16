package in.succinct.beckn.portal.db.model.api;

import com.venky.swf.db.annotations.column.relationship.CONNECTED_VIA;

import java.util.List;

public interface NetworkRole extends in.succinct.beckn.registry.db.model.onboarding.NetworkRole {

    @CONNECTED_VIA("CALLED_ON_SUBSCRIBER_ID")
    public List<ApiTest> getTestsAsCalled();

    @CONNECTED_VIA("PROXY_SUBSCRIBER_ID")
    public List<ApiTest> getTestsAsCaller();
}
