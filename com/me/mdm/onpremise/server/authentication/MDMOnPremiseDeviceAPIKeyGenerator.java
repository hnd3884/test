package com.me.mdm.onpremise.server.authentication;

import java.util.Map;
import java.util.HashMap;
import com.me.mdm.core.auth.APIKey;
import org.json.JSONObject;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;

public class MDMOnPremiseDeviceAPIKeyGenerator extends MDMDeviceAPIKeyGenerator
{
    public APIKey createAPIKey(final JSONObject json) throws Exception {
        final String deviceToken = String.valueOf(json.get("DEVICE_TOKEN"));
        return new APIKey("encapiKey", deviceToken, APIKey.VERSION_2_0);
    }
    
    public HashMap fetchAPIKeyDetails(final int version, final HashMap parameterValueMap) {
        final HashMap map = new HashMap();
        if (version == APIKey.VERSION_2_0) {
            if (parameterValueMap.containsKey("encapiKey")) {
                return super.fetchAPIKeyDetails(version, parameterValueMap);
            }
            if (parameterValueMap.containsKey("cid")) {
                final String[] parts = parameterValueMap.get("cid").split("encapiKey=");
                if (parts.length == 2) {
                    map.put("encapiKey", parts[1]);
                }
                else {
                    map.put("encapiKey", null);
                }
            }
            else {
                map.put("encapiKey", null);
            }
        }
        return map;
    }
    
    public APIKey getAPIKeyFromMap(final Map requestMap) {
        if (requestMap.containsKey("encapiKey")) {
            return new APIKey("encapiKey", (String)requestMap.get("encapiKey"), APIKey.VERSION_2_0);
        }
        if (requestMap.containsKey("cid")) {
            final String[] parts = requestMap.get("cid").split("encapiKey=");
            if (parts.length == 2) {
                return new APIKey("encapiKey", parts[1], APIKey.VERSION_2_0);
            }
        }
        return new APIKey("", "", APIKey.VERSION_1_0);
    }
}
