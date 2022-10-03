package com.me.mdm.onpremise.api.keygen.integrationservice;

import com.adventnet.sym.server.util.SyMUtil;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.onpremise.server.authentication.IntegrationServiceUtil;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class IntegrationServiceAPIRequestHandler extends ApiRequestHandler
{
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)this.getIntegrationServiceDetails(APIUtil.getResourceID(requestJSON, "integration_service_id")));
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception in doGet....", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            if (!requestJSON.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject body = requestJSON.getJSONObject("msg_body");
            final JSONObject currentData = this.getIntegrationServiceDetails(APIUtil.getResourceID(requestJSON, "integration_service_id"));
            final JSONObject updateData = new JSONObject();
            updateData.put("NAME", (Object)body.optString("integration_service_name", String.valueOf(currentData.get("integration_service_name"))));
            updateData.put("STATUS", body.optInt("status", currentData.getInt("status")));
            updateData.put("logged_in_user", (Object)APIUtil.getUserID(requestJSON));
            updateData.put("SERVICE_ID", (Object)APIUtil.getResourceID(requestJSON, "integration_service_id"));
            final JSONObject updateResponse = IntegrationServiceUtil.getNewInstance().modifyIntegrationService(updateData);
            final int updateStatus = updateResponse.getInt("status_id");
            switch (updateStatus) {
                case 100: {
                    final JSONObject responseJSON = new JSONObject();
                    responseJSON.put("status", 202);
                    return responseJSON;
                }
                case 106: {
                    throw new APIHTTPException("COM0005", new Object[] { "status" });
                }
                case 103: {
                    throw new APIHTTPException("COM0008", new Object[] { APIUtil.getResourceID(requestJSON, "integration_service_id") });
                }
                case 105: {
                    throw new APIHTTPException("COM0010", new Object[] { String.valueOf(body.get("integration_service_name")) });
                }
                default: {
                    this.logger.log(Level.SEVERE, "Invalid response from IntegrationServiceUtil.modifyIntegrationService");
                    throw new APIHTTPException("COM0004", new Object[0]);
                }
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception in doGet....", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final Long integrationServiceId = APIUtil.getResourceID(requestJSON, "integration_service_id");
            final JSONObject requestData = new JSONObject();
            requestData.put("SERVICE_ID", (Object)integrationServiceId);
            final int status = this.deleteIntegrationService(requestData);
            switch (status) {
                case 102: {
                    throw new APIHTTPException("COM0008", new Object[] { integrationServiceId });
                }
                case 100: {
                    final JSONObject responseJSON = new JSONObject();
                    responseJSON.put("status", 204);
                    return responseJSON;
                }
                case 104: {
                    throw new APIHTTPException("COM0015", new Object[] { "service being used" });
                }
                default: {
                    this.logger.log(Level.SEVERE, "Invalid response from IntegrationServiceUtil.deleteIntegrationService");
                    throw new APIHTTPException("COM0004", new Object[0]);
                }
            }
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "Exception in doDelete....", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getIntegrationServiceDetails(final Long integrationServiceID) throws APIHTTPException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("IntegrationService"));
        selectQuery.addJoin(new Join("IntegrationService", "AaaUser", new String[] { "CREATED_BY" }, new String[] { "USER_ID" }, "IntegrationService", "CREATED_BY_USER", 2));
        selectQuery.addJoin(new Join("IntegrationService", "AaaUser", new String[] { "MODIFIED_BY" }, new String[] { "USER_ID" }, "IntegrationService", "MODIFIED_BY_USER", 2));
        selectQuery.addSelectColumn(Column.getColumn("IntegrationService", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("IntegrationService", "CREATED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("IntegrationService", "CREATION_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("IntegrationService", "MODIFIED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("IntegrationService", "MODIFIED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("IntegrationService", "SERVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("IntegrationService", "STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("CREATED_BY_USER", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MODIFIED_BY_USER", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("CREATED_BY_USER", "FIRST_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("MODIFIED_BY_USER", "FIRST_NAME"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("IntegrationService", "SERVICE_ID"), (Object)integrationServiceID, 0));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final JSONObject response = new JSONObject();
                Row row = dataObject.getFirstRow("IntegrationService");
                response.put("integration_service_name", row.get("NAME"));
                response.put("integration_service_id", row.get("SERVICE_ID"));
                response.put("created_by", row.get("CREATED_BY"));
                response.put("modified_by", row.get("MODIFIED_BY"));
                response.put("creation_time", row.get("CREATION_TIME"));
                response.put("modified_time", row.get("MODIFIED_TIME"));
                response.put("status", row.get("STATUS"));
                row = dataObject.getFirstRow("CREATED_BY_USER");
                response.put("created_by_user_name", row.get("FIRST_NAME"));
                row = dataObject.getFirstRow("MODIFIED_BY_USER");
                response.put("modified_by_user_name", row.get("FIRST_NAME"));
                return response;
            }
            throw new APIHTTPException("COM0008", new Object[] { integrationServiceID });
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, "Exception in getIntegrationServiceDetaisl....", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private int deleteIntegrationService(final JSONObject properties) throws Exception {
        final Long serviceId = Long.valueOf(String.valueOf(properties.get("SERVICE_ID")));
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("IntegrationService"));
        selectQuery.addJoin(new Join("IntegrationService", "APIKeyInfo", new String[] { "SERVICE_ID" }, new String[] { "SERVICE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("IntegrationService", "SERVICE_ID").count());
        selectQuery.setCriteria(new Criteria(Column.getColumn("IntegrationService", "SERVICE_ID"), (Object)serviceId, 0));
        final int count = DBUtil.getRecordCount(selectQuery);
        final DataObject existingDO = DataAccess.get("IntegrationService", new Criteria(Column.getColumn("IntegrationService", "SERVICE_ID"), (Object)serviceId, 0));
        if (existingDO.isEmpty()) {
            return 102;
        }
        if (count == 0) {
            SyMUtil.getPersistence().delete(existingDO.getFirstRow("IntegrationService"));
            return 100;
        }
        return 104;
    }
}
