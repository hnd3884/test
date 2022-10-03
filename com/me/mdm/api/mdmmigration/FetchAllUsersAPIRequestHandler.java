package com.me.mdm.api.mdmmigration;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.mdmmigration.APIServiceDataHandler;
import server.com.me.mdm.server.mdmmigration.MigrationServicesFacade;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class FetchAllUsersAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            apiRequest.urlStartKey = "fetchusers";
            final JSONObject reqJSON = apiRequest.toJSONObject();
            final Long config_id = APIUtil.getResourceID(reqJSON, "fetchuser_id");
            new MigrationServicesFacade().processMigrationRequest(reqJSON, "FETCH_USERS", config_id);
            final Long customer_id = APIUtil.getCustomerID(apiRequest.toJSONObject());
            response.put("RESPONSE", (Object)new APIServiceDataHandler().getMigrationStatus(config_id, customer_id));
            return response;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception while FetchAllUsersAPIRequestHandler", e.getMessage());
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while FetchAllUsersAPIRequestHandler", e2.getMessage());
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
