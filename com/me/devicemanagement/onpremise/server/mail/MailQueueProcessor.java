package com.me.devicemanagement.onpremise.server.mail;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.MemoryOnlyDCQueueDataProcessor;

public class MailQueueProcessor extends MemoryOnlyDCQueueDataProcessor
{
    private Logger logger;
    
    public MailQueueProcessor() {
        this.logger = Logger.getLogger("MailQueueLog");
    }
    
    public void processData(final DCQueueData qData) {
        try {
            final MailDetails obj = (MailDetails)qData.queueData;
            this.logger.log(Level.INFO, "Mail Queue processing started {0}", obj.getNonSensitiveDataAsString());
            ApiFactoryProvider.getMailSettingAPI().sendMail((MailDetails)qData.queueData);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while send mail ... : {0}", ex);
        }
    }
}
