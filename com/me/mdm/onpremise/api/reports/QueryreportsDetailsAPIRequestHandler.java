package com.me.mdm.onpremise.api.reports;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class QueryreportsDetailsAPIRequestHandler extends ApiRequestHandler
{
    QueryreportsFacade queryreportsFacade;
    
    public QueryreportsDetailsAPIRequestHandler() {
        this.queryreportsFacade = new QueryreportsFacade();
    }
    
    public JSONObject doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.queryreportsFacade.reportNameCheck(apiRequest));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "error while getting query report details...", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
