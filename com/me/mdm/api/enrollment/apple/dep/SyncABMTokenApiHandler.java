package com.me.mdm.api.enrollment.apple.dep;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.server.adep.ABMSyncTokenFacade;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class SyncABMTokenApiHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject syncDetails = ABMSyncTokenFacade.getInstance().syncToken(apiRequest.toJSONObject());
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)syncDetails);
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in Sync ABM/ASM token..", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in Sync ABM/ASM token..", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject syncDetails = ABMSyncTokenFacade.getInstance().getSyncDetails(apiRequest.toJSONObject());
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)syncDetails);
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in get Sync ABM/ASM token..", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in get Sync ABM/ASM token..", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
