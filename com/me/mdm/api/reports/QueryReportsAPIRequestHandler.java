package com.me.mdm.api.reports;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.reports.MDMReportsFacade;
import com.me.mdm.api.ApiRequestHandler;

public class QueryReportsAPIRequestHandler extends ApiRequestHandler
{
    MDMReportsFacade mdmReportsFacade;
    
    public QueryReportsAPIRequestHandler() {
        this.mdmReportsFacade = new MDMReportsFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            final JSONObject requestObject = apiRequest.toJSONObject();
            responseJSON.put("RESPONSE", (Object)this.mdmReportsFacade.getQueryReportList(requestObject));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.WARNING, "Issue on fetching query reports", ex);
            throw ex;
        }
    }
}
