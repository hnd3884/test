package com.me.mdm.api.mdmmigration;

import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import server.com.me.mdm.server.mdmmigration.MigrationServicesFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class MigrationServicesAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)new MigrationServicesFacade().getAPIServices());
            return responseJSON;
        }
        catch (final JSONException | DataAccessException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
