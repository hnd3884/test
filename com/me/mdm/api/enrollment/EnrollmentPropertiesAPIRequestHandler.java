package com.me.mdm.api.enrollment;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.enrollment.EnrollmentFacade;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class EnrollmentPropertiesAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    private EnrollmentFacade enrollmentFacade;
    
    public EnrollmentPropertiesAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
        this.enrollmentFacade = new EnrollmentFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = this.enrollmentFacade.getEnrollmentProperties(apiRequest.toJSONObject());
            final JSONObject statusJSON = new JSONObject();
            statusJSON.put("status", 200);
            statusJSON.put("RESPONSE", (Object)responseJSON);
            return statusJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception doGet(): ", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
