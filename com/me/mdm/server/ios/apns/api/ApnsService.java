package com.me.mdm.server.ios.apns.api;

import javax.net.ssl.SSLException;
import java.net.ConnectException;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.ios.apns.APNsWakeUpProcessor;
import java.util.logging.Level;
import com.me.mdm.api.model.BaseAPIModel;
import java.util.logging.Logger;

public class ApnsService
{
    private static Logger logger;
    
    public void testConnection(final BaseAPIModel model) throws Exception {
        try {
            ApnsService.logger.log(Level.INFO, "[Apns] Initiating Apns connection test..");
            APNsWakeUpProcessor.getInstance().testApnsConnection();
            ApnsService.logger.log(Level.INFO, "[Apns] Completed Apns connection test..");
        }
        catch (final Throwable t) {
            Throwable throwable = t.getCause();
            if (throwable == null) {
                ApnsService.logger.log(Level.SEVERE, "[Apns] Exception in test connection: {0}", t.getClass());
                throwable = t;
            }
            String msg = throwable.getMessage();
            msg = ((msg != null) ? msg.toLowerCase() : "null");
            ApnsService.logger.log(Level.SEVERE, "[Apns] Exception in test connection: {0}:{1}", new Object[] { throwable.getClass(), msg });
            if (msg.contains("push certificate cannot be null")) {
                throw new APIHTTPException("APNS205", new Object[0]);
            }
            if (msg.contains("first received frame was not settings")) {
                throw new APIHTTPException("APNS203", new Object[0]);
            }
            if (msg.contains("certificate_expired")) {
                throw new APIHTTPException("APNS207", new Object[0]);
            }
            if (msg.contains("certificate_revoked")) {
                throw new APIHTTPException("APNS206", new Object[0]);
            }
            if (msg.contains("channel closed before http/2 preface completed") || msg.contains("established connection was aborted")) {
                throw new APIHTTPException("APNS201", new Object[0]);
            }
            if (throwable instanceof ConnectException) {
                throw new APIHTTPException("APNS202", new Object[0]);
            }
            if (throwable instanceof SSLException) {
                throw new APIHTTPException("APNS204", new Object[0]);
            }
            throw new APIHTTPException("APNS208", new Object[0]);
        }
    }
    
    static {
        ApnsService.logger = Logger.getLogger("MDMEnrollment");
    }
}
