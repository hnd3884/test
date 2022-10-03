package com.me.mdm.api.enrollment;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import java.util.logging.Logger;
import com.me.mdm.server.enrollment.EnrollmentFacade;
import com.me.mdm.api.ApiRequestHandler;

public class GetEnrollmentTabCountApiRequestHandler extends ApiRequestHandler
{
    EnrollmentFacade enrollmentFacade;
    Logger logger;
    
    public GetEnrollmentTabCountApiRequestHandler() {
        this.enrollmentFacade = MDMRestAPIFactoryProvider.getEnrollmentFacade();
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.enrollmentFacade.getEnrollTabCount(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, "Exception in getEnrollTabCount    ", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in getEnrollTabCount    ", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
