package com.me.mdm.api.enrollment.apple.apns;

import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.ios.apns.APNsCertificateFacade;
import java.util.logging.Level;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class CSRHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public CSRHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.logger.log(Level.INFO, "Request to Download ManualSignCSR..");
            APNsCertificateFacade.getFacadeInstance().downloadManualCsr(apiRequest);
            return null;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.WARNING, "Exception while downloading ManualSignCSR..", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Exception while downloading ManualSignCSR..", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
