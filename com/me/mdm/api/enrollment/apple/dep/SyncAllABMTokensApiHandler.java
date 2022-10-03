package com.me.mdm.api.enrollment.apple.dep;

import com.me.mdm.server.adep.DEPEnrollmentUtil;
import org.json.JSONArray;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.server.adep.ABMSyncTokenFacade;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class SyncAllABMTokensApiHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequest.toJSONObject());
            final JSONArray serverSyncDetails = ABMSyncTokenFacade.getInstance().syncAllTokensForCustomer(customerId);
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)new JSONObject().put("appledepservers", (Object)serverSyncDetails));
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in SyncAll ABM/ASM tokens..", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in SyncAll ABM/ASM tokens..", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequest.toJSONObject());
            final JSONObject response = new JSONObject();
            final Long abmServerPendingProfileCreation = DEPEnrollmentUtil.getDEPTokenPendingProfileCreation(customerId);
            if (abmServerPendingProfileCreation != null) {
                response.put("status", 200);
                response.put("RESPONSE", (Object)new JSONObject().put("abmserverpendingsetupcompletion", (Object)abmServerPendingProfileCreation));
                return response;
            }
            final JSONArray serverSyncDetails = ABMSyncTokenFacade.getInstance().getSyncAllDetails(customerId);
            response.put("status", 200);
            response.put("RESPONSE", (Object)new JSONObject().put("appledepservers", (Object)serverSyncDetails));
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in get SyncAll ABM/ASM tokens..", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in get SyncAll ABM/ASM tokens..", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
