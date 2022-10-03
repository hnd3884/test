package com.me.mdm.api.enrollment;

import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.enrollment.admin.AdminEnrollmentFacade;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class AdminEnrollmentAPIRequestHandler extends ApiRequestHandler
{
    Logger logger;
    AdminEnrollmentFacade enrollmentFacade;
    
    public AdminEnrollmentAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
        this.enrollmentFacade = new AdminEnrollmentFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.enrollmentFacade.getAdminEnrollmentDetails(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getAdminEnrollmentDetails ", ex);
            if (ex instanceof SyMException) {
                throw new APIHTTPException("COM0005", new Object[] { "template_id" });
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
