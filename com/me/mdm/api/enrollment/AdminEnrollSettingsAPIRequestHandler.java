package com.me.mdm.api.enrollment;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.enrollment.admin.AdminEnrollmentFacade;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class AdminEnrollSettingsAPIRequestHandler extends ApiRequestHandler
{
    Logger logger;
    AdminEnrollmentFacade enrollmentFacade;
    
    public AdminEnrollSettingsAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
        this.enrollmentFacade = new AdminEnrollmentFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.enrollmentFacade.saveAdminEnrollSettings(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, "Exception in saveAdminEnrollSettings  ", ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in saveAdminEnrollSettings  ", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
