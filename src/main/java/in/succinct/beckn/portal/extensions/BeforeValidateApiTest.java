package in.succinct.beckn.portal.extensions;

import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.extensions.BeforeModelValidateExtension;
import in.succinct.beckn.portal.db.model.api.ApiTest;

public class BeforeValidateApiTest extends BeforeModelValidateExtension<ApiTest> {
    static {
        registerExtension(new BeforeValidateApiTest());
    }
    @Override
    public void beforeValidate(ApiTest model) {
        if (model.getCalledOnSubscriber() == null){
            throw new RuntimeException("Specify the subscriber to be called");
        }

        if (!ObjectUtil.equals(model.getUseCase().getBecknApi().getPlatform(),model.getCalledOnSubscriber().getType())){
            throw new RuntimeException("Api must be called on a " + model.getUseCase().getBecknApi().getPlatform());
        }
    }
}
