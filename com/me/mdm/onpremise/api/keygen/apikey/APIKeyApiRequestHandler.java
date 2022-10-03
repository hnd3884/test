package com.me.mdm.onpremise.api.keygen.apikey;

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
import org.json.JSONArray;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.authentication.APIKeyUtil;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class APIKeyApiRequestHandler extends ApiRequestHandler
{
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            if (!requestJSON.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject body = requestJSON.getJSONObject("msg_body");
            if (body.length() == 0) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            JSONArray scopeIDS = null;
            if (body.has("api_scope_ids")) {
                Label_0105: {
                    try {
                        scopeIDS = body.getJSONArray("api_scope_ids");
                        break Label_0105;
                    }
                    catch (final JSONException e) {
                        throw new APIHTTPException("COM0005", new Object[] { "api_scope_ids" });
                    }
                    throw new APIHTTPException("COM0005", new Object[] { "api_scope_ids" });
                }
                final JSONObject request = new JSONObject();
                request.put("API_KEY_ID", (Object)APIUtil.getResourceID(requestJSON, "api_key_id"));
                request.put("is_update", true);
                request.put("logged_in_user", (Object)APIUtil.getUserID(requestJSON));
                if (scopeIDS != null) {
                    request.put("scope_ids", (Object)scopeIDS);
                }
                final boolean updateResponse = APIKeyUtil.getNewInstance().updateApiKeyScopeRelation(request);
                if (updateResponse) {
                    final JSONObject response = new JSONObject();
                    response.put("status", 202);
                    return response;
                }
                throw new APIHTTPException("COM0008", new Object[] { "Invalid api_key_id or api_scope_id" });
            }
            throw new APIHTTPException("COM0005", new Object[] { "api_scope_ids" });
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "exception in APIKeyApiRequestHandler.doPut", (Throwable)e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final JSONObject request = new JSONObject();
            request.put("API_KEY_ID", (Object)APIUtil.getResourceID(requestJSON, "api_key_id"));
            final boolean delResponse = APIKeyUtil.getNewInstance().deleteAPIKey(request);
            if (delResponse) {
                final JSONObject response = new JSONObject();
                response.put("status", 204);
                return response;
            }
            throw new APIHTTPException("COM0008", new Object[] { APIUtil.getResourceID(requestJSON, "api_key_id") });
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception in doGet....", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)this.getAPIKeyDetails(APIUtil.getResourceID(requestJSON, "api_key_id")));
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception in doGet....", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getAPIKeyDetails(final Long apiKeyID) throws APIHTTPException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("APIKeyInfo"));
        selectQuery.addJoin(new Join("APIKeyInfo", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        selectQuery.addJoin(new Join("APIKeyInfo", "AaaUser", new String[] { "CREATED_BY" }, new String[] { "USER_ID" }, "APIKeyInfo", "CREATED_BY_USER", 2));
        selectQuery.addJoin(new Join("APIKeyInfo", "AaaUser", new String[] { "MODIFIED_BY" }, new String[] { "USER_ID" }, "APIKeyInfo", "MODIFIED_BY_USER", 2));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyInfo", "API_KEY_ID"));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyInfo", "CREATED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyInfo", "CREATION_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyInfo", "MODIFIED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyInfo", "MODIFIED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyInfo", "API_KEY"));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyInfo", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUser", "FIRST_NAME", "user_name"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUser", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyInfo", "VALIDITY"));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyInfo", "SERVICE_ID", "integration_service_id"));
        selectQuery.addSelectColumn(Column.getColumn("CREATED_BY_USER", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MODIFIED_BY_USER", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("CREATED_BY_USER", "FIRST_NAME", "created_by_user_name"));
        selectQuery.addSelectColumn(Column.getColumn("MODIFIED_BY_USER", "FIRST_NAME", "modified_by_user_name"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("APIKeyInfo", "API_KEY_ID"), (Object)apiKeyID, 0));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final JSONObject response = new JSONObject();
                Row row = dataObject.getFirstRow("APIKeyInfo");
                response.put("api_key_id", row.get("API_KEY_ID"));
                response.put("api_key", row.get("API_KEY"));
                response.put("integration_service_id", row.get("SERVICE_ID"));
                response.put("created_by", row.get("CREATED_BY"));
                response.put("modified_by", row.get("MODIFIED_BY"));
                response.put("creation_time", row.get("CREATION_TIME"));
                response.put("modified_time", row.get("MODIFIED_TIME"));
                response.put("validity", row.get("VALIDITY"));
                row = dataObject.getFirstRow("AaaUser");
                response.put("user_name", row.get("FIRST_NAME"));
                row = dataObject.getFirstRow("CREATED_BY_USER");
                response.put("created_by_user_name", row.get("FIRST_NAME"));
                row = dataObject.getFirstRow("MODIFIED_BY_USER");
                response.put("modified_by_user_name", row.get("FIRST_NAME"));
                return response;
            }
            throw new APIHTTPException("COM0008", new Object[] { apiKeyID });
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, "Exception in getAPIKeyDetails....", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
