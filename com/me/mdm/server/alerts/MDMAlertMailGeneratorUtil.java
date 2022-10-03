package com.me.mdm.server.alerts;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.alerts.AlertMailGeneratorUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.logging.Logger;

public class MDMAlertMailGeneratorUtil
{
    Logger logger;
    private String sourceClass;
    
    public MDMAlertMailGeneratorUtil() {
        this.sourceClass = "AlertMailGeneratorUtil";
        this.logger = Logger.getLogger(this.sourceClass);
    }
    
    public MDMAlertMailGeneratorUtil(final Logger log) {
        this.sourceClass = "AlertMailGeneratorUtil";
        this.logger = log;
    }
    
    public void sendMail(final Long alertType, final String module, final Long customerId, final Properties prop) {
        if (ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured()) {
            final AlertMailGeneratorUtil mailGenerator = new AlertMailGeneratorUtil(this.logger);
            mailGenerator.sendMail(alertType, module, customerId, prop);
        }
        else {
            Logger.getLogger("MailQueueLog").log(Level.INFO, "Mail Server not configured | Alert Tpye:{0}Module:{1}Customer ID:{2}Properties{3}", new Object[] { alertType, module, customerId, prop });
        }
    }
}
