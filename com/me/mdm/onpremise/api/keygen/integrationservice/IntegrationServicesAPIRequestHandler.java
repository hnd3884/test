package com.me.mdm.onpremise.api.keygen.integrationservice;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.authentication.IntegrationServiceUtil;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class IntegrationServicesAPIRequestHandler extends ApiRequestHandler
{
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            if (!requestJSON.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final String serviceName = requestJSON.getJSONObject("msg_body").optString("integration_service_name", "");
            if (serviceName.length() == 0) {
                throw new APIHTTPException("COM0005", new Object[] { "integration_service_name" });
            }
            final JSONObject request = new JSONObject();
            request.put("NAME", (Object)serviceName);
            request.put("logged_in_user", (Object)APIUtil.getUserID(requestJSON));
            final JSONObject response = IntegrationServiceUtil.getNewInstance().createIntegrationService(request);
            final int status_id = response.getInt("status_id");
            final JSONObject responseJSON = new JSONObject();
            switch (status_id) {
                case 100: {
                    responseJSON.put("status", 200);
                    responseJSON.put("RESPONSE", (Object)new IntegrationServiceAPIRequestHandler().getIntegrationServiceDetails(response.getLong("SERVICE_ID")));
                    break;
                }
                case 103: {
                    throw new APIHTTPException("COM0010", new Object[] { serviceName });
                }
            }
            return responseJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in IntegrationServicesAPIRequestHandler", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONArray integrationServices = this.getIntegrationServicesList(apiRequest.toJSONObject());
            final JSONObject response = new JSONObject();
            response.put("integration_services", (Object)integrationServices);
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)response);
            return responseJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception in doGet...", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private JSONArray getIntegrationServicesList(final JSONObject request) throws APIHTTPException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("IntegrationService"));
        selectQuery.addJoin(new Join("IntegrationService", "AaaUser", new String[] { "CREATED_BY" }, new String[] { "USER_ID" }, "IntegrationService", "CREATED_BY_USER", 2));
        selectQuery.addJoin(new Join("IntegrationService", "AaaUser", new String[] { "MODIFIED_BY" }, new String[] { "USER_ID" }, "IntegrationService", "MODIFIED_BY_USER", 2));
        final Long userID = APIUtil.getUserID(request);
        selectQuery.addSelectColumn(Column.getColumn("IntegrationService", "NAME", "integration_service_name"));
        selectQuery.addSelectColumn(Column.getColumn("IntegrationService", "CREATED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("IntegrationService", "CREATION_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("IntegrationService", "MODIFIED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("IntegrationService", "MODIFIED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("IntegrationService", "SERVICE_ID", "integration_service_id"));
        selectQuery.addSelectColumn(Column.getColumn("IntegrationService", "STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("CREATED_BY_USER", "FIRST_NAME", "created_by_user_name"));
        selectQuery.addSelectColumn(Column.getColumn("MODIFIED_BY_USER", "FIRST_NAME", "modified_by_user_name"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("IntegrationService", "CREATED_BY"), (Object)userID, 0));
        return MDMUtil.executeSelectQueryAndGetOrgJSONArray(selectQuery);
    }
}
