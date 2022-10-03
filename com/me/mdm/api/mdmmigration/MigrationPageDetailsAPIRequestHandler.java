package com.me.mdm.api.mdmmigration;

import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import server.com.me.mdm.server.mdmmigration.MigrationServicesFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class MigrationPageDetailsAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)new MigrationServicesFacade().getMigrationProceedDetails(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) {
        try {
            new MigrationServicesFacade().updateMigrationProceedDetails(apiRequest.toJSONObject());
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Migration APIRequestHandler exception while MigrationPageDetailsAPIRequestHandler ", (Throwable)e);
            throw new APIHTTPException("COM0009", new Object[] { e });
        }
        catch (final Exception e2) {
            throw new APIHTTPException("SCN0001", new Object[] { e2 });
        }
        return null;
    }
}
