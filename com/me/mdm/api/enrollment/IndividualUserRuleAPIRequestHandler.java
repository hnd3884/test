package com.me.mdm.api.enrollment;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.enrollment.admin.AdminEnrollmentFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualUserRuleAPIRequestHandler extends ApiRequestHandler
{
    AdminEnrollmentFacade enrollmentFacade;
    
    public IndividualUserRuleAPIRequestHandler() {
        this.enrollmentFacade = new AdminEnrollmentFacade();
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.enrollmentFacade.updateAssignUserSettings(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.WARNING, "Exception when adding USer assignment rule ", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.WARNING, "Exception when adding USer assignment rule ", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 202);
            this.enrollmentFacade.deleteAssignUserSettings(apiRequest.toJSONObject());
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.WARNING, "Exception when adding USer assignment rule ", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.WARNING, "Exception when adding USer assignment rule ", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.enrollmentFacade.getAssignUserSettings(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.WARNING, "Exception when adding USer assignment rule ", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.WARNING, "Exception when adding USer assignment rule ", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
