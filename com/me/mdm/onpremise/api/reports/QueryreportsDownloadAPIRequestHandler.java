package com.me.mdm.onpremise.api.reports;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class QueryreportsDownloadAPIRequestHandler extends ApiRequestHandler
{
    QueryreportsFacade queryreportsFacade;
    
    public QueryreportsDownloadAPIRequestHandler() {
        this.queryreportsFacade = new QueryreportsFacade();
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.queryreportsFacade.downloadReport(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
