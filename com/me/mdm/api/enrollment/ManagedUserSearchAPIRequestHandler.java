package com.me.mdm.api.enrollment;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.server.enrollment.EnrollmentFacade;
import com.me.mdm.api.ApiRequestHandler;

public class ManagedUserSearchAPIRequestHandler extends ApiRequestHandler
{
    EnrollmentFacade enrollmentFacade;
    Logger logger;
    
    public ManagedUserSearchAPIRequestHandler() {
        this.enrollmentFacade = new EnrollmentFacade();
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.enrollmentFacade.getUsers(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in doGet", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
