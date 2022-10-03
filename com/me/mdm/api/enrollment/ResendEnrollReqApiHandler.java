package com.me.mdm.api.enrollment;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import java.util.logging.Logger;
import com.me.mdm.server.enrollment.EnrollmentFacade;
import com.me.mdm.api.ApiRequestHandler;

public class ResendEnrollReqApiHandler extends ApiRequestHandler
{
    EnrollmentFacade enrollmentFacade;
    Logger logger;
    
    public ResendEnrollReqApiHandler() {
        this.enrollmentFacade = MDMRestAPIFactoryProvider.getEnrollmentFacade();
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 202);
            final JSONObject msg_body = apiRequest.toJSONObject().getJSONObject("msg_body");
            this.enrollmentFacade.resendEnrollmentRequests(apiRequest.toJSONObject());
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception occurred in ResendEnrollReqApiHandler", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
