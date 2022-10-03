package com.me.mdm.onpremise.server.enrollment.mail;

import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.mailmanager.MailProcessor;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import org.json.JSONObject;
import com.me.devicemanagement.onpremise.server.mail.MailCallBackHandlerImpl;

public class EnrollMailCallBackHandler extends MailCallBackHandlerImpl
{
    public void handleSuccessfulCompletion(final JSONObject mailSuccessDetails) {
        try {
            super.handleSuccessfulCompletion(mailSuccessDetails);
            if (mailSuccessDetails.getBoolean("NewEnrollReq")) {
                MDMEnrollmentRequestHandler.getInstance().updateEnrollmentStatusAndErrorCode(Long.valueOf(mailSuccessDetails.getLong("EnrollmentRequestID")), 1, "dc.mdm.enroll.request_sent", -1);
            }
            else {
                MDMEnrollmentRequestHandler.getInstance().updateEnrollmentStatusAndErrorCode(Long.valueOf(mailSuccessDetails.getLong("EnrollmentRequestID")), 1, "mdm.enroll.successfully_resend_enrollment_request", -1);
            }
            MDMEnrollmentRequestHandler.getInstance().updateMailSentStatus();
        }
        catch (final JSONException ex) {
            Logger.getLogger(EnrollMailCallBackHandler.class.getName()).log(Level.SEVERE, "Exception while handling auth failure", (Throwable)ex);
        }
    }
    
    public void handleMailSendingFailure(final JSONObject mailFailureDetails) {
        try {
            super.handleMailSendingFailure(mailFailureDetails);
            if (!ApiFactoryProvider.getDemoUtilAPI().isDemoMode()) {
                MDMEnrollmentRequestHandler.getInstance().updateEnrollFailedStatus(Long.valueOf(mailFailureDetails.getLong("EnrollmentRequestID")), MailProcessor.getInstance().getErrorKeyForErrorCode(Integer.valueOf(mailFailureDetails.getInt("mailErrorCode"))), mailFailureDetails.getInt("mailErrorCode"));
                MDMEnrollmentRequestHandler.getInstance().updateMailSentStatus();
            }
            else {
                Logger.getLogger(EnrollMailCallBackHandler.class.getName()).log(Level.INFO, "hiding  MAIL_SERVER_CONFIGURED_INCORRECTLY message");
                MessageProvider.getInstance().unhideMessage("MAIL_SERVER_CONFIGURED_INCORRECTLY");
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(EnrollMailCallBackHandler.class.getName()).log(Level.SEVERE, "Exception while handling mail sending failure", ex);
        }
    }
}
