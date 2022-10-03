package com.me.mdm.api.core.apps.config;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.apps.android.afw.layoutmgmt.StoreLayoutManager;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class PlaystoreLayoutMigrationAPIHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)new StoreLayoutManager().getStoreLayoutMigrationNotification(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getting Playstore layout migration status ", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject responseJSON = new JSONObject();
        try {
            new StoreLayoutManager().updateStoreLayoutMigrationNotification(apiRequest.toJSONObject());
            responseJSON.put("status", 202);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "Issue on updating playstore layout migration", e);
            throw new APIHTTPException("COM0004", new Object[] { e });
        }
        return responseJSON;
    }
}
