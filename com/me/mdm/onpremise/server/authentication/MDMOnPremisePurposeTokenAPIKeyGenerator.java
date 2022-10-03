package com.me.mdm.onpremise.server.authentication;

import com.me.mdm.core.auth.APIKey;
import org.json.JSONObject;
import com.me.mdm.core.auth.MDMPurposeAPIKeyGenerator;

public class MDMOnPremisePurposeTokenAPIKeyGenerator extends MDMPurposeAPIKeyGenerator
{
    public APIKey createAPIKey(final JSONObject json) throws Exception {
        final String purposeToken = String.valueOf(json.get("PURPOSE_TOKEN"));
        return new APIKey("encapiKey", purposeToken, APIKey.VERSION_2_0);
    }
    
    public boolean validateAPIKey(final JSONObject json) {
        return super.validateAPIKeyWithReceivedKey(json);
    }
}
