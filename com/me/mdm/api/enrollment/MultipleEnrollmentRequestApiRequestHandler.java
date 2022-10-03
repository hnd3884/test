package com.me.mdm.api.enrollment;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.enrollment.EnrollmentFacade;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class MultipleEnrollmentRequestApiRequestHandler extends ApiRequestHandler
{
    Logger logger;
    EnrollmentFacade enrollmentFacade;
    
    public MultipleEnrollmentRequestApiRequestHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
        this.enrollmentFacade = MDMRestAPIFactoryProvider.getEnrollmentFacade();
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            this.enrollmentFacade.resendMultipleEnrollmentRequest(apiRequest.toJSONObject());
            responseJSON.put("RESPONSE", (Object)new JSONObject());
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, "Exception in resendMultipleEnrollmentRequest", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in resendMultipleEnrollmentRequest", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            this.enrollmentFacade.removeMultipleRequest(apiRequest.toJSONObject());
            responseJSON.put("RESPONSE", (Object)new JSONObject());
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, "Exception in removeMultipleRequest", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in removeMultipleRequest", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.enrollmentFacade.createEnrollmentRequest(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, "Exception in createEnrollmentRequest", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in createEnrollmentRequest", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.enrollmentFacade.getEnrollmentRequestList(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, "Exception in getEnrollmentRequestList ", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in getEnrollmentRequestList ", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
