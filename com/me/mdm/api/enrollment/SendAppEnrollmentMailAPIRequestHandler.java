package com.me.mdm.api.enrollment;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.server.enrollment.EnrollmentFacade;
import com.me.mdm.api.ApiRequestHandler;

public class SendAppEnrollmentMailAPIRequestHandler extends ApiRequestHandler
{
    private EnrollmentFacade enrollmentFacade;
    private Logger logger;
    
    public SendAppEnrollmentMailAPIRequestHandler() {
        this.enrollmentFacade = new EnrollmentFacade();
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject statusJSON = this.enrollmentFacade.sendAppEnrollmentMail(apiRequest.toJSONObject());
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)statusJSON);
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, " -- doPost()   >   Exception    ", (Throwable)ex);
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
}
