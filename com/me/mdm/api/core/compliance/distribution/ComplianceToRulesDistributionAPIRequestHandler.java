package com.me.mdm.api.core.compliance.distribution;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.compliance.ComplianceFacade;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ComplianceToRulesDistributionAPIRequestHandler extends ApiRequestHandler
{
    public Logger logger;
    private ComplianceFacade compliance;
    
    public ComplianceToRulesDistributionAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMDeviceComplianceLogger");
        this.compliance = null;
        this.compliance = new ComplianceFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            final JSONObject distributionJSON = this.compliance.getComplianceRulesDistributionDetails(requestJSON);
            responseJSON.put("RESPONSE", (Object)distributionJSON);
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, " -- doGet()   >   Error", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
