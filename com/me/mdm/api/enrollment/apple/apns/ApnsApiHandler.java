package com.me.mdm.api.enrollment.apple.apns;

import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.ios.apns.APNsCertificateFacade;
import java.util.logging.Level;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ApnsApiHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public ApnsApiHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.logger.log(Level.INFO, "Start modifying apns certificate.. (post request)");
            APNsCertificateFacade.getFacadeInstance().addApnsCertificate(apiRequest.toJSONObject());
            return this.doGet(apiRequest);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, "exception occurred while saving apns certificate", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "exception occurred while saving apns certificate", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            final JSONObject apnsDetails = APNsCertificateFacade.getFacadeInstance().getApnsDetails(apiRequest.toJSONObject());
            response.put("status", 200);
            response.put("RESPONSE", (Object)apnsDetails);
            return response;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting APNS details..", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject apiResponse = new JSONObject();
            if (DMUserHandler.isUserInRole(APIUtil.getLoginID(apiRequest.toJSONObject()), "All_Managed_Mobile_Devices")) {
                final JSONObject response = APNsCertificateFacade.getFacadeInstance().removeAPNsCertificate(apiRequest.toJSONObject());
                apiResponse.put("status", 200);
                apiResponse.put("RESPONSE", (Object)response);
                return apiResponse;
            }
            throw new APIHTTPException("COM0013", new Object[0]);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception while deleting APNS through Api..", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while deleting APNS through Api..", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.logger.log(Level.INFO, "Start modifying apns certificate.. (put request)");
            APNsCertificateFacade.getFacadeInstance().modifyApnsCertificate(apiRequest.toJSONObject());
            return this.doGet(apiRequest);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, "Exception while modifying APNS through Api..", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "exception occurred while modifying apns certificate", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
