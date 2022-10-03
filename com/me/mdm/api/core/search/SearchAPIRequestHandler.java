package com.me.mdm.api.core.search;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class SearchAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public SearchAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMApiLogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final APISearchHandler searchHandler = new APISearchHandler();
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)searchHandler.searchResource(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "JSON exception in SearchAPIRequestHandler.doGet", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
