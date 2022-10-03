package com.me.mdm.api.enrollment;

import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import java.util.logging.Logger;
import com.me.mdm.server.enrollment.EnrollmentFacade;
import com.me.mdm.api.ApiRequestHandler;

public class BulkDeprovisionAPIRequestHandler extends ApiRequestHandler
{
    EnrollmentFacade enrollmentFacade;
    Logger logger;
    
    public BulkDeprovisionAPIRequestHandler() {
        this.enrollmentFacade = MDMRestAPIFactoryProvider.getEnrollmentFacade();
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.enrollmentFacade.deprovisionMultipleDevice(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in deprovisionMultipleDevice ", ex);
            if (ex instanceof SyMException) {
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
            else {
                if (ex instanceof APIHTTPException) {
                    throw (APIHTTPException)ex;
                }
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
    }
}
