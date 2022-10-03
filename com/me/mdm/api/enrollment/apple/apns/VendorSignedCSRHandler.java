package com.me.mdm.api.enrollment.apple.apns;

import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.ios.apns.APNsCertificateFacade;
import java.util.logging.Level;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class VendorSignedCSRHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public VendorSignedCSRHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.logger.log(Level.INFO, "Request to Download VendorSignedCSR..");
            APNsCertificateFacade.getFacadeInstance().downloadVendorSignedCsr(apiRequest);
            return null;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "Exception while downloading VendorSignedCSR..", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while downloading VendorSignedCSR..", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject apiResponse = new JSONObject();
            final JSONObject response = APNsCertificateFacade.getFacadeInstance().signCsrHandler(apiRequest);
            apiResponse.put("status", 200);
            apiResponse.put("RESPONSE", (Object)response);
            return apiResponse;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while signing VendorSignedCSR..", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
