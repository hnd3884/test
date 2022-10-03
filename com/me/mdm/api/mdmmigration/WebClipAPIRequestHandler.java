package com.me.mdm.api.mdmmigration;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import server.com.me.mdm.server.mdmmigration.MigrationServicesFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class WebClipAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            apiRequest.urlStartKey = "serviceconfigs";
            responseJSON.put("RESPONSE", (Object)new MigrationServicesFacade().getWebClipURL(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while WebClipAPIRequestHandler", e2.getMessage());
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
