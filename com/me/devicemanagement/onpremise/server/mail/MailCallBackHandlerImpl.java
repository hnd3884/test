package com.me.devicemanagement.onpremise.server.mail;

import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.ds.query.UpdateQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.mailmanager.MailCallBackHandler;

public class MailCallBackHandlerImpl implements MailCallBackHandler
{
    public void handleSuccessfulCompletion(final JSONObject mailSuccessDetails) {
        try {
            this.hideMailServerIncorrectlyConfiguredMessage();
            this.updateErrorCodeInTable(-1);
        }
        catch (final Exception ex) {
            Logger.getLogger(MailCallBackHandlerImpl.class.getName()).log(Level.SEVERE, "Exception while handling mail sending success action {0}", ex);
        }
    }
    
    public void handleMailSendingFailure(final JSONObject mailFailureDetails) {
        try {
            final Integer mailErrorCode = mailFailureDetails.getInt("mailErrorCode");
            this.updateErrorCodeInTable(mailFailureDetails.getInt("mailErrorCode"));
            switch (mailErrorCode) {
                case 40010:
                case 40011: {
                    this.hideMailServerIncorrectlyConfiguredMessage();
                    break;
                }
                default: {
                    this.unhideMailServerIncorrectlyConfiguredMessage();
                    break;
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(MailCallBackHandlerImpl.class.getName()).log(Level.SEVERE, "Exception while handling mail sending failure action {0}", ex);
        }
    }
    
    private void updateErrorCodeInTable(final Integer errorCode) throws Exception {
        final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("SmtpConfiguration");
        query.setUpdateColumn("PREVIOUS_ERROR_CODE", (Object)errorCode);
        SyMUtil.getPersistence().update(query);
    }
    
    private void hideMailServerIncorrectlyConfiguredMessage() {
        MessageProvider.getInstance().hideMessage("MAIL_SERVER_CONFIGURED_INCORRECTLY");
        SyMUtil.deleteSyMParameter("MAIL_CONFIG_ERROR");
    }
    
    private void unhideMailServerIncorrectlyConfiguredMessage() {
        MessageProvider.getInstance().unhideMessage("MAIL_SERVER_CONFIGURED_INCORRECTLY");
    }
}
