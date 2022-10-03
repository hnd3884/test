package com.me.mdm.api.mdmmigration;

import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.mdmmigration.APIServiceDataHandler;
import server.com.me.mdm.server.mdmmigration.MigrationServicesFacade;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class MigrationSynchronizeAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject response = new JSONObject();
        response.put("status", 200);
        apiRequest.urlStartKey = "synchronizeall";
        final JSONObject reqJSON = apiRequest.toJSONObject();
        final Long config_id = APIUtil.getResourceID(reqJSON, "synchronizeal_id");
        new MigrationServicesFacade().processMigrationRequest(reqJSON, "FETCH_ALL", config_id);
        final Long customer_id = APIUtil.getCustomerID(apiRequest.toJSONObject());
        response.put("RESPONSE", (Object)new APIServiceDataHandler().getMigrationStatus(config_id, customer_id));
        return response;
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject response = new JSONObject();
        response.put("status", 200);
        apiRequest.urlStartKey = "synchronizeall";
        response.put("RESPONSE", (Object)new MigrationServicesFacade().getMigrationStatus(apiRequest.toJSONObject()));
        return response;
    }
}
