package com.me.mdm.onpremise.api.keygen.apikey;

import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.onpremise.server.authentication.APIKeyUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class APIKeyRevokeAPIRequestHandler extends ApiRequestHandler
{
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final Long apiKeyID = APIUtil.getResourceID(requestJSON, "api_key_id");
            final JSONObject request = new JSONObject();
            request.put("API_KEY_ID", (Object)apiKeyID);
            request.put("logged_in_user", (Object)APIUtil.getUserID(requestJSON));
            final Boolean invalidateResponse = APIKeyUtil.getNewInstance().invalidateAPIKey(request);
            if (invalidateResponse) {
                final JSONObject response = new JSONObject();
                response.put("status", 202);
                return response;
            }
            throw new APIHTTPException("COM0008", new Object[] { apiKeyID });
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "exception in APIKeyRevokeAPIRequestHandler.doPost", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
