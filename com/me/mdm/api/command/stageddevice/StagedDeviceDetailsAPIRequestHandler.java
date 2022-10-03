package com.me.mdm.api.command.stageddevice;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.enrollment.admin.AdminEnrollmentFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class StagedDeviceDetailsAPIRequestHandler extends ApiRequestHandler
{
    Logger logger;
    
    public StagedDeviceDetailsAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("RESPONSE", (Object)new AdminEnrollmentFacade().getStagedDeviceInfo(apiRequest.toJSONObject()));
            responseJSON.put("status", 200);
            return responseJSON;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception in doGet ", e);
            throw e;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in doGet ", ex);
            throw new APIHTTPException(500, "COM0005", new Object[0]);
        }
    }
}
