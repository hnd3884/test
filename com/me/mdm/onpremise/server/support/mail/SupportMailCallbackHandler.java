package com.me.mdm.onpremise.server.support.mail;

import java.io.IOException;
import org.apache.commons.io.FileUtils;
import java.util.logging.Level;
import java.io.File;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.server.mail.MailCallBackHandlerImpl;

public class SupportMailCallbackHandler extends MailCallBackHandlerImpl
{
    Logger logger;
    
    public SupportMailCallbackHandler() {
        this.logger = Logger.getLogger(SupportMailCallbackHandler.class.getName());
    }
    
    public void handleSuccessfulCompletion(final JSONObject mailSuccessDetails) {
        super.handleSuccessfulCompletion(mailSuccessDetails);
        final File resourceDir = new File(mailSuccessDetails.getString("logFileToDelete"));
        if (resourceDir.getParentFile().exists()) {
            try {
                this.logger.log(Level.INFO, "Mail sent.... deleting device initiated support log file");
                FileUtils.deleteDirectory(resourceDir.getParentFile());
            }
            catch (final IOException e) {
                this.logger.log(Level.SEVERE, "error deleting device initiated support log file", e);
            }
        }
    }
    
    public void handleMailSendingFailure(final JSONObject mailFailureDetails) {
        super.handleMailSendingFailure(mailFailureDetails);
        final File resourceDir = new File(mailFailureDetails.getString("logFileToDelete"));
        if (resourceDir.getParentFile().exists()) {
            try {
                this.logger.log(Level.INFO, "Mail sending failed.... deleting device initiated support log file");
                FileUtils.deleteDirectory(resourceDir.getParentFile());
            }
            catch (final IOException e) {
                this.logger.log(Level.SEVERE, "error deleting device initiated support log file", e);
            }
        }
    }
}
