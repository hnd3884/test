package com.me.mdm.api.enrollment.apple.apns;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.server.enrollment.EnrollmentFacade;
import com.me.mdm.api.ApiRequestHandler;

public class CheckAPNSConnectionAPIRequestHandler extends ApiRequestHandler
{
    private static EnrollmentFacade enrollmentFacade;
    private static Logger logger;
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject statusJSON = CheckAPNSConnectionAPIRequestHandler.enrollmentFacade.checkAPNSConnection();
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)statusJSON);
            return responseJSON;
        }
        catch (final JSONException e) {
            CheckAPNSConnectionAPIRequestHandler.logger.log(Level.SEVERE, "Exception doGet CheckAPNSConnectionAPIRequestHandler : ", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        CheckAPNSConnectionAPIRequestHandler.enrollmentFacade = new EnrollmentFacade();
        CheckAPNSConnectionAPIRequestHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
