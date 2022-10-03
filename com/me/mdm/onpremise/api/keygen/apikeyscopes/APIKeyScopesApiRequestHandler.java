package com.me.mdm.onpremise.api.keygen.apikeyscopes;

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

public class APIKeyScopesApiRequestHandler extends ApiRequestHandler
{
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONArray apiKeyScopeList = this.getAPIKeyScopeList(apiRequest.toJSONObject());
            final JSONObject response = new JSONObject();
            response.put("api_key_scopes", (Object)apiKeyScopeList);
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
    
    private JSONArray getAPIKeyScopeList(final JSONObject request) throws APIHTTPException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("APIKeyScope"));
        selectQuery.addJoin(new Join("APIKeyScope", "AaaUser", new String[] { "CREATED_BY" }, new String[] { "USER_ID" }, "APIKeyScope", "CREATED_BY_USER", 2));
        selectQuery.addJoin(new Join("APIKeyScope", "AaaUser", new String[] { "MODIFIED_BY" }, new String[] { "USER_ID" }, "APIKeyScope", "MODIFIED_BY_USER", 2));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyScope", "SCOPE_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyScope", "CREATED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyScope", "CREATION_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyScope", "MODIFIED_BY"));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyScope", "MODIFIED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyScope", "SCOPE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("APIKeyScope", "DESCRIPTION"));
        selectQuery.addSelectColumn(Column.getColumn("CREATED_BY_USER", "FIRST_NAME", "created_by_user_name"));
        selectQuery.addSelectColumn(Column.getColumn("MODIFIED_BY_USER", "FIRST_NAME", "modified_by_user_name"));
        return MDMUtil.executeSelectQueryAndGetOrgJSONArray(selectQuery);
    }
}
