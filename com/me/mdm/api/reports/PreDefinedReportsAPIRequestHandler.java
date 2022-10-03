package com.me.mdm.api.reports;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.reports.MDMReportsFacade;
import com.me.mdm.api.ApiRequestHandler;

public class PreDefinedReportsAPIRequestHandler extends ApiRequestHandler
{
    MDMReportsFacade mdmReportsFacade;
    
    public PreDefinedReportsAPIRequestHandler() {
        this.mdmReportsFacade = new MDMReportsFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.mdmReportsFacade.getMDMPreDefinedReports(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.WARNING, "Cannot fetch Predefined report list", ex);
            throw ex;
        }
    }
}
