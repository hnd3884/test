package com.me.mdm.onpremise.api.settings;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class DBPostgresOptimizationAPIRequestHandler extends ApiRequestHandler
{
    DBPostgresOptimizationFacade dbPostgresOptimizationFacade;
    
    public DBPostgresOptimizationAPIRequestHandler() {
        this.dbPostgresOptimizationFacade = new DBPostgresOptimizationFacade();
    }
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.dbPostgresOptimizationFacade.saveDBPostgresOptimizationDetails(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.dbPostgresOptimizationFacade.getDBPostgresOptimizationDetails());
            return responseJSON;
        }
        catch (final Exception ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
