package com.me.mdm.api.enrollment;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.enrollment.EnrollmentFacade;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ReEnrollDeviceAPIRequestHandler extends ApiRequestHandler
{
    Logger logger;
    EnrollmentFacade enrollmentFacade;
    
    public ReEnrollDeviceAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
        this.enrollmentFacade = MDMRestAPIFactoryProvider.getEnrollmentFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            this.enrollmentFacade.assignDepProfile(apiRequest.toJSONObject());
            responseJSON.put("RESPONSE", (Object)new JSONObject());
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, "Exception in assignDepProfile ", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in assignDepProfile ", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
