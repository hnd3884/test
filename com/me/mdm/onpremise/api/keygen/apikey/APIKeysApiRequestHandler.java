package com.me.mdm.onpremise.api.keygen.apikey;

import com.me.devicemanagement.onpremise.server.authentication.APIKeyUtil;
import com.me.mdm.api.APIUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONArray;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class APIKeysApiRequestHandler extends ApiRequestHandler
{
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONArray apiKeys = this.getAPIKeys(apiRequest.toJSONObject());
            final JSONObject response = new JSONObject();
            response.put("api_keys", (Object)apiKeys);
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
    
    private JSONArray getAPIKeys(final JSONObject request) throws APIHTTPException {
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
        selectQuery.addSelectColumn(Column.getColumn("APIKeyInfo", "VALIDITY"));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyInfo", "SERVICE_ID", "integration_service_id"));
        selectQuery.addSelectColumn(Column.getColumn("CREATED_BY_USER", "FIRST_NAME", "created_by_user_name"));
        selectQuery.addSelectColumn(Column.getColumn("MODIFIED_BY_USER", "FIRST_NAME", "modified_by_user_name"));
        return MDMUtil.executeSelectQueryAndGetOrgJSONArray(selectQuery);
    }
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            if (!requestJSON.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject body = requestJSON.getJSONObject("msg_body");
            final Long serviceID = JSONUtil.optLongForUVH(body, "integration_service_id", Long.valueOf(-1L));
            if (serviceID == -1L) {
                throw new APIHTTPException("COM0005", new Object[] { "integration_service_id" });
            }
            final Long userID = JSONUtil.optLongForUVH(body, "user_id", Long.valueOf(-1L));
            if (userID == -1L) {
                throw new APIHTTPException("COM0005", new Object[] { "user_id" });
            }
            JSONArray scopeIDS = null;
            if (body.has("api_scope_ids")) {
                Label_0174: {
                    try {
                        scopeIDS = body.getJSONArray("api_scope_ids");
                        break Label_0174;
                    }
                    catch (final JSONException e) {
                        throw new APIHTTPException("COM0005", new Object[] { "api_scope_ids" });
                    }
                    throw new APIHTTPException("COM0005", new Object[] { "api_scope_ids" });
                }
                final Long validity = JSONUtil.optLongForUVH(body, "validity", Long.valueOf(System.currentTimeMillis() + 15552000000L));
                final JSONObject request = new JSONObject();
                request.put("SERVICE_ID", (Object)serviceID);
                request.put("scope_ids", (Object)scopeIDS);
                request.put("USER_ID", (Object)userID);
                request.put("VALIDITY", (Object)validity);
                request.put("logged_in_user", (Object)APIUtil.getUserID(requestJSON));
                final JSONObject response = APIKeyUtil.getNewInstance().createAPIKey(request);
                final int status_id = response.getInt("status_id");
                final JSONObject responseJSON = new JSONObject();
                switch (status_id) {
                    case 100: {
                        responseJSON.put("status", 200);
                        responseJSON.put("RESPONSE", (Object)new APIKeyApiRequestHandler().getAPIKeyDetails(JSONUtil.optLongForUVH(response, "API_KEY_ID", Long.valueOf(-1L))));
                        break;
                    }
                    case 104: {
                        throw new APIHTTPException("COM0005", new Object[] { "integration_service_id" });
                    }
                    case 105: {
                        throw new APIHTTPException("COM0005", new Object[] { "api_scope_ids" });
                    }
                    case 101: {
                        throw new APIHTTPException("COM0010", new Object[] { "api key" });
                    }
                }
                return responseJSON;
            }
            throw new APIHTTPException("COM0005", new Object[] { "api_scope_ids" });
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "Exception occurred in APIKeysApiRequestHandler.doPost", (Throwable)e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
