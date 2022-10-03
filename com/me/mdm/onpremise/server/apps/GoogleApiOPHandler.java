package com.me.mdm.onpremise.server.apps;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Properties;
import com.me.mdm.onpremise.server.time.ServerTimeValidationTask;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.message.MDMMessageUtil;
import java.util.logging.Logger;
import com.me.mdm.server.factory.GoogleApiProductBasedHandler;

public class GoogleApiOPHandler implements GoogleApiProductBasedHandler
{
    public Logger logger;
    
    public GoogleApiOPHandler() {
        this.logger = Logger.getLogger("MDMBStoreLogger");
    }
    
    public void handleServerTimeMismatch(final Exception ex, final Long customerId) {
        try {
            final Boolean isServerMismatchMessageOpen = new MDMMessageUtil().IsMsgOpen("SERVER_TIME_MISMATCH");
            if (!isServerMismatchMessageOpen) {
                this.logger.log(Level.INFO, "Validate SERVER TIME MISMATCH for customer {0}", customerId);
                new ServerTimeValidationTask().executeTask(new Properties());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred in validateAndOpenServerTimeMismatchMessage", e);
        }
    }
    
    public String getValueFromPropertiesFile(final String key) throws Exception {
        final String valueInPropertiesFile = MDMUtil.getInstance().getMDMApplicationProperties().getProperty(key);
        return valueInPropertiesFile;
    }
}
