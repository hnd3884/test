package com.me.mdm.api.reports;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.reports.MDMReportsFacade;
import com.me.mdm.api.ApiRequestHandler;

public class MDMReportParamAPIHandler extends ApiRequestHandler
{
    private MDMReportsFacade mdmReportsFacade;
    
    public MDMReportParamAPIHandler() {
        this.mdmReportsFacade = new MDMReportsFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.mdmReportsFacade.getReportParamValue(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in get /report/:id/params", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
