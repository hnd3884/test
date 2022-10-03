package com.me.mdm.api.mdmmigration;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import server.com.me.mdm.server.mdmmigration.MigrationServicesFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class MigrationSummaryAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            apiRequest.urlStartKey = "summary";
            response.put("RESPONSE", (Object)new MigrationServicesFacade().getMigrationSummary(apiRequest.toJSONObject()));
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception while MigrationSummaryAPIRequestHandler", e.getMessage());
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while MigrationSummaryAPIRequestHandler", e2.getMessage());
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
