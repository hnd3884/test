package com.me.mdm.api.core.apps.config;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.apps.android.afw.PlaystoreAppsAutoApprover;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class PFWAutoAppsApproveAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)new PlaystoreAppsAutoApprover().approveNonAFWApps(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in auto approving apps", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
