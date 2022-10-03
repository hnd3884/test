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

public class IndividualComplianceAPIRequestHandler extends ApiRequestHandler
{
    public ComplianceFacade complianceFacade;
    public Logger logger;
    
    public IndividualComplianceAPIRequestHandler() {
        this.complianceFacade = new ComplianceFacade();
        this.logger = Logger.getLogger("MDMDeviceComplianceLogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.complianceFacade.getComplianceProfile(apiRequest.toJSONObject()));
            this.logger.log(Level.FINEST, " -- IndividualComplianceAPIRequestHandler.doGet()  >   reponse:    {0}", responseDetails.toString());
            return responseDetails;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- doGet()    >   Exception   ", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
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
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            final JSONObject requestJSON = apiRequest.toJSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.complianceFacade.modifyComplianceProfile(requestJSON));
            return responseDetails;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- doPut() >   Exception ", (Throwable)e);
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
}
