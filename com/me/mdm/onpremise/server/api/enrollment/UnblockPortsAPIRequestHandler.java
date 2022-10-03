package com.me.mdm.onpremise.server.api.enrollment;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.onpremise.server.enrollment.EnrollmentFacadeOnPremise;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class UnblockPortsAPIRequestHandler extends ApiRequestHandler
{
    private static Logger logger;
    private static EnrollmentFacadeOnPremise enrollmentFacadeOnPremise;
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject statusJSON = UnblockPortsAPIRequestHandler.enrollmentFacadeOnPremise.unblockPorts();
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)statusJSON);
            return responseJSON;
        }
        catch (final JSONException e) {
            UnblockPortsAPIRequestHandler.logger.log(Level.SEVERE, "Exception doPost UnblockPortsAPIRequestHandler : ", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        UnblockPortsAPIRequestHandler.logger = Logger.getLogger("MDMEnrollment");
        UnblockPortsAPIRequestHandler.enrollmentFacadeOnPremise = new EnrollmentFacadeOnPremise();
    }
}
