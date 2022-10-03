package com.me.mdm.server.ios.apns;

import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.SSLException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import javapns.communication.exceptions.KeystoreException;
import javapns.communication.exceptions.CommunicationException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class APNSCommunicationErrorHandler
{
    static final Logger LOGGER;
    public static final String CERTIFICATE_EXPIRED = "certificate_expired";
    public static final String CERTIFICATE_REVOKED = "certificate_revoked";
    
    public long handleAPNSWakeupFailure(final Throwable ex) {
        String errorMessage = null;
        if (ex != null) {
            APNSCommunicationErrorHandler.LOGGER.log(Level.INFO, "--------Error Response from APNS-----");
            errorMessage = ex.getMessage();
            APNSCommunicationErrorHandler.LOGGER.log(Level.INFO, "Response from APNS : {0}", errorMessage);
        }
        hideAllAPNSMessages();
        if (ex == null) {
            this.handleCommunicationException(null);
            return 12133L;
        }
        if (ex.getMessage().contains("certificate_expired")) {
            handleAPNSCertificateExpiredException();
            return 12140L;
        }
        if (ex.getMessage().contains("certificate_revoked")) {
            handleAPNSCertificateRevokedException();
            return 12139L;
        }
        if (ex instanceof CommunicationException) {
            this.handleCommunicationException(errorMessage);
            return 12138L;
        }
        if (ex instanceof KeystoreException) {
            handleKeystoreException();
            return 12133L;
        }
        if (ex instanceof SyMException) {
            return 12133L;
        }
        if (ex instanceof SSLException) {
            return 12133L;
        }
        if (ex instanceof InterruptedException) {
            return 12138L;
        }
        if (ex instanceof ExecutionException) {
            return 12133L;
        }
        return 2L;
    }
    
    public static void hideAllAPNSMessages() {
        APNSCommunicationErrorHandler.LOGGER.log(Level.INFO, "Going to Close all APNS messages in UI to open up new messages!");
        MessageProvider.getInstance().hideMessage("APNS_ABOUT_TO_EXPIRED");
        MessageProvider.getInstance().hideMessage("APNS_EXPIRED_CLOSABLE");
        MessageProvider.getInstance().hideMessage("APNS_PORT_BLOCKED");
        MessageProvider.getInstance().hideMessage("APNS_CERTIFICATE_REVOKED");
        MessageProvider.getInstance().hideMessage("APNS_EXPIRED_NON_CLOSABLE");
        if (MDMEnrollmentUtil.getInstance().isAPNsConfigured()) {
            MessageProvider.getInstance().hideMessage("APNS_NOT_UPLOADED");
            MessageProvider.getInstance().hideMessage("APNS_NOT_UPLOADED_CLOSABLE");
        }
    }
    
    protected void handleCommunicationException(final String errorMessage) {
    }
    
    protected static void handleKeystoreException() {
        MessageProvider.getInstance().unhideMessage("IOS_WAKEUP_FAILED_CONTACT_SUPPORT");
    }
    
    protected static void handleAPNSCertificateExpiredException() {
        try {
            MessageProvider.getInstance().unhideMessage("APNS_EXPIRED_NON_CLOSABLE");
            final APNsCertificateHandler apnsHandlerInstance = APNsCertificateHandler.getInstance();
            apnsHandlerInstance.addApnsCertErrorDetails(apnsHandlerInstance.getAPNSDetail().getLong("CERTIFICATE_ID"), 1001);
        }
        catch (final Exception ex1) {
            APNSCommunicationErrorHandler.LOGGER.log(Level.SEVERE, "Exception in APNSCommunicationErrorHandler , While handing certificate_expired response from APNS", ex1);
        }
    }
    
    protected static void handleAPNSCertificateRevokedException() {
        MessageProvider.getInstance().unhideMessage("APNS_CERTIFICATE_REVOKED");
        try {
            final APNsCertificateHandler apnsHandlerInstance = APNsCertificateHandler.getInstance();
            apnsHandlerInstance.addApnsCertErrorDetails(apnsHandlerInstance.getAPNSDetail().getLong("CERTIFICATE_ID"), 1002);
            APNSCommunicationErrorHandler.LOGGER.log(Level.INFO, "APNS certificate is revoked ..so updating apnsstate to 'open for renewal'");
        }
        catch (final Exception ex1) {
            APNSCommunicationErrorHandler.LOGGER.log(Level.SEVERE, "Exception in APNSCommunicationErrorHandler , While handing certificate_revoked response from APNS", ex1);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMEnrollment");
    }
}
