package com.adventnet.sym.server.mdm;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.inv.MDMMailNotificationHandler;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class MDMMailNotificationTask implements SchedulerExecutionInterface
{
    private Logger logger;
    
    public MDMMailNotificationTask() {
        this.logger = Logger.getLogger(MDMMailNotificationTask.class.getName());
    }
    
    public void executeTask(final Properties taskProps) {
        try {
            final MDMMailNotificationHandler notificationHandler = MDMMailNotificationHandler.getInstance();
            for (final Long customerId : CustomerInfoUtil.getInstance().getCustomerIdsFromDB()) {
                notificationHandler.sendBlacklistMails(customerId);
                MDMMailNotificationHandler.getInstance().sendBlackListAppDetectMailNotification(customerId);
                MDMMailNotificationHandler.getInstance().sendNewAppDetectMailNotification(customerId);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception in MDMMailNotificationTask", e);
        }
    }
}
