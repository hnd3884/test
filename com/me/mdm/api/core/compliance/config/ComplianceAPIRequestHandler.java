package com.me.mdm.api.core.compliance.config;

import com.me.mdm.http.HttpException;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.server.compliance.ComplianceFacade;
import com.me.mdm.api.ApiRequestHandler;

public class ComplianceAPIRequestHandler extends ApiRequestHandler
{
    public ComplianceFacade complianceFacade;
    public Logger logger;
    
    public ComplianceAPIRequestHandler() {
        this.complianceFacade = new ComplianceFacade();
        this.logger = Logger.getLogger("MDMDeviceComplianceLogger");
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            JSONObject complianceJSON = new JSONObject();
            complianceJSON = this.complianceFacade.addComplianceProfile(apiRequest.toJSONObject());
            responseJSON.put("RESPONSE", (Object)complianceJSON);
            this.logger.log(Level.FINEST, " -- ComplianceAPIRequestHandler    >   responseJSON    {0}", responseJSON.toString());
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, " -- doPost()   >   Exception    ", (Throwable)ex);
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.complianceFacade.getComplianceProfiles(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            JSONObject responseJSON = new JSONObject();
            responseJSON = this.complianceFacade.removeComplianceProfile(apiRequest.toJSONObject());
            final JSONObject doDeleteResponseJSON = new JSONObject();
            doDeleteResponseJSON.put("status", 204);
            return doDeleteResponseJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- doDelete()    >   Exception   ", (Throwable)e);
            throw new HttpException(400, null);
        }
    }
}
