package com.me.mdm.api.enrollment;

import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import java.util.logging.Logger;
import com.me.mdm.server.enrollment.EnrollmentFacade;
import com.me.mdm.api.ApiRequestHandler;

public class DeprovisionAPIRequestHandler extends ApiRequestHandler
{
    EnrollmentFacade enrollmentFacade;
    Logger logger;
    
    public DeprovisionAPIRequestHandler() {
        this.enrollmentFacade = MDMRestAPIFactoryProvider.getEnrollmentFacade();
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.enrollmentFacade.getWipeStatus(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getWipeStatus", ex);
            if (ex instanceof SyMException) {
                throw new APIHTTPException("WIP0004", new Object[0]);
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.enrollmentFacade.deprovisionDevice(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in deprovisionDevice", ex);
            if (!(ex instanceof SyMException)) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            final int errorcode = ((SyMException)ex).getErrorCode();
            if (errorcode == 13001) {
                throw new APIHTTPException("WIP0002", new Object[0]);
            }
            if (errorcode == 1001) {
                throw new APIHTTPException("WIP0001", new Object[0]);
            }
            if (errorcode == 13003) {
                throw new APIHTTPException("WIP0003", new Object[0]);
            }
            return new JSONObject();
        }
    }
}
