package com.me.mdm.api.enrollment;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.server.enrollment.admin.AdminEnrollmentFacade;
import com.me.mdm.api.ApiRequestHandler;

public class ChromeAPIRequestHandler extends ApiRequestHandler
{
    AdminEnrollmentFacade enrollmentFacade;
    Logger logger;
    
    public ChromeAPIRequestHandler() {
        this.enrollmentFacade = new AdminEnrollmentFacade();
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            final JSONObject jsonObject = this.enrollmentFacade.getChromeEnrollDetails(apiRequest.toJSONObject());
            if (jsonObject.getJSONArray("managed_domains").length() > 0) {
                responseJSON.put("status", 200);
                responseJSON.put("RESPONSE", (Object)jsonObject);
            }
            else {
                responseJSON.put("status", 204);
            }
            return responseJSON;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in doGet ", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.enrollmentFacade.addChromeEnrollmentDetails(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in doPost", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.enrollmentFacade.removeChromeIntegration(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            this.logger.log(Level.SEVERE, "Exception in doDelete", ex);
            throw new APIHTTPException("COM0027", new Object[0]);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in doDelete", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
