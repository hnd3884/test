package com.me.mdm.api.enrollment;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.server.enrollment.EnrollmentFacade;
import com.me.mdm.api.ApiRequestHandler;

public class InactiveDevicePolicyAPIRequestHandler extends ApiRequestHandler
{
    EnrollmentFacade enrollmentFacade;
    private static Logger mdmEnrollmentLogger;
    
    public InactiveDevicePolicyAPIRequestHandler() {
        this.enrollmentFacade = new EnrollmentFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.enrollmentFacade.getInactiveDevicePolicyDetails(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                if (((APIHTTPException)ex).toJSONObject().optString("error_code").equals("ENR00114")) {
                    InactiveDevicePolicyAPIRequestHandler.mdmEnrollmentLogger.log(Level.WARNING, "Inactive Policy is not configured");
                }
                else {
                    InactiveDevicePolicyAPIRequestHandler.mdmEnrollmentLogger.log(Level.SEVERE, "Exception in doGet of InactiveDevicePolicyAPIRequestHandler", ex);
                }
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 204);
            this.enrollmentFacade.saveInactiveDevicePolicySettings(apiRequest.toJSONObject());
            return responseJSON;
        }
        catch (final Exception ex) {
            InactiveDevicePolicyAPIRequestHandler.mdmEnrollmentLogger.log(Level.SEVERE, "Exception in doPut of InactiveDevicePolicyAPIRequestHandler", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 204);
            this.enrollmentFacade.deleteInactiveDevicePolicy(apiRequest.toJSONObject());
            return responseJSON;
        }
        catch (final Exception ex) {
            InactiveDevicePolicyAPIRequestHandler.mdmEnrollmentLogger.log(Level.SEVERE, "Exception in doDelete of InactiveDevicePolicyAPIRequestHandler", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        InactiveDevicePolicyAPIRequestHandler.mdmEnrollmentLogger = Logger.getLogger("MDMEnrollment");
    }
}
