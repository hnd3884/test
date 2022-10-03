package com.me.mdm.api.core.compliance.distribution;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.compliance.ComplianceFacade;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ComplianceToGroupsDistributionAPIRequestHandler extends ApiRequestHandler
{
    public Logger logger;
    private ComplianceFacade compliance;
    
    public ComplianceToGroupsDistributionAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMDeviceComplianceLogger");
        this.compliance = new ComplianceFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            requestJSON.put("distribution_key", (Object)"associate");
            this.compliance.associateOrDisassociateComplianceToDeviceGroups(requestJSON);
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            final JSONObject successJSON = new JSONObject();
            successJSON.put("success", (Object)"success");
            responseJSON.put("RESPONSE", (Object)successJSON);
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, " -- doPost()   >   Error", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, " -- doPost()   >   Error", ex2);
            throw new APIHTTPException(500, null, new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            requestJSON.put("distribution_key", (Object)"disassociate");
            this.compliance.associateOrDisassociateComplianceToDeviceGroups(requestJSON);
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            final JSONObject successJSON = new JSONObject();
            successJSON.put("success", (Object)"success");
            responseJSON.put("RESPONSE", (Object)successJSON);
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, " -- doDelete()   >   Error", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, " -- doDelete()   >   Error", ex2);
            throw new APIHTTPException(500, null, new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            final JSONObject distributionJSON = this.compliance.getComplianceToGroupsDistributionDetails(requestJSON);
            responseJSON.put("RESPONSE", (Object)distributionJSON);
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, " -- doGet()   >   Error", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
