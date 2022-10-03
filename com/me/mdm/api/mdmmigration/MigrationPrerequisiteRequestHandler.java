package com.me.mdm.api.mdmmigration;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.mdmmigration.MigrationAPIRequestHandler;
import com.me.mdm.mdmmigration.MigrationAPIUtilities;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class MigrationPrerequisiteRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            apiRequest.urlStartKey = "prerequisite";
            final JSONObject request = apiRequest.toJSONObject();
            final Long config_id = APIUtil.getResourceID(request, "prerequisit_id");
            final int service_id = new MigrationAPIUtilities().getServiceID(config_id);
            final JSONObject responseJSON = MigrationAPIRequestHandler.getInstance(service_id).fetchMigrationPrerequisite(config_id);
            response.put("RESPONSE", (Object)responseJSON);
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception while MigrationPrerequisiteRequestHandler", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while MigrationPrerequisiteRequestHandler", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
