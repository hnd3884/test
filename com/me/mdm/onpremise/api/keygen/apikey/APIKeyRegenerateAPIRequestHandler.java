package com.me.mdm.onpremise.api.keygen.apikey;

import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.onpremise.server.authentication.APIKeyUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class APIKeyRegenerateAPIRequestHandler extends ApiRequestHandler
{
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final Long apiKeyID = APIUtil.getResourceID(requestJSON, "api_key_id");
            final JSONObject request = new JSONObject();
            request.put("API_KEY_ID", (Object)apiKeyID);
            request.put("logged_in_user", (Object)APIUtil.getUserID(requestJSON));
            final JSONObject response = APIKeyUtil.getNewInstance().regenerateAPIKey(request);
            final int status_id = response.getInt("status_id");
            final JSONObject responseJSON = new JSONObject();
            switch (status_id) {
                case 100: {
                    responseJSON.put("status", 200);
                    responseJSON.put("RESPONSE", (Object)new APIKeyApiRequestHandler().getAPIKeyDetails(apiKeyID));
                    break;
                }
                case 102: {
                    throw new APIHTTPException("COM0008", new Object[] { apiKeyID });
                }
            }
            return responseJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "exception in APIKeyRevokeAPIRequestHandler.doPost", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
