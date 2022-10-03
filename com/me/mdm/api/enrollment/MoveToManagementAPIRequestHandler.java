package com.me.mdm.api.enrollment;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import java.util.logging.Logger;
import com.me.mdm.server.enrollment.EnrollmentFacade;
import com.me.mdm.api.ApiRequestHandler;

public class MoveToManagementAPIRequestHandler extends ApiRequestHandler
{
    EnrollmentFacade enrollmentFacade;
    Logger logger;
    
    public MoveToManagementAPIRequestHandler() {
        this.enrollmentFacade = MDMRestAPIFactoryProvider.getEnrollmentFacade();
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            this.enrollmentFacade.moveToManagementMultipleDevices(apiRequest.toJSONObject());
            responseJSON.put("RESPONSE", (Object)new JSONObject());
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, "Exception in moveToManagementMultipleDevices", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in moveToManagementMultipleDevices", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
