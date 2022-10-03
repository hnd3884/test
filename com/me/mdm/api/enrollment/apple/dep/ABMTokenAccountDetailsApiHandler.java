package com.me.mdm.api.enrollment.apple.dep;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.server.adep.ABMSyncTokenFacade;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class ABMTokenAccountDetailsApiHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject accountDetails = ABMSyncTokenFacade.getInstance().getTokenAccountDetails(apiRequest.toJSONObject());
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)accountDetails);
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception while getting ABM Account Details..", e);
            throw e;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while getting ABM Account Details..", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
