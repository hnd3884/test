package com.me.mdm.api.enrollment;

import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.server.enrollment.admin.AdminEnrollmentFacade;
import com.me.mdm.api.ApiRequestHandler;

public class NFCAdminAppAPIRequestHandler extends ApiRequestHandler
{
    AdminEnrollmentFacade enrollmentFacade;
    Logger logger;
    
    public NFCAdminAppAPIRequestHandler() {
        this.enrollmentFacade = new AdminEnrollmentFacade();
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 204);
            responseJSON.put("RESPONSE", (Object)this.enrollmentFacade.removeNFCAdminApp(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in doPost ", ex);
            if (ex instanceof SyMException) {
                throw new APIHTTPException("COM0009", new Object[0]);
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
