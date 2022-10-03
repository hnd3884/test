package com.adventnet.sym.server.mdm.task;

import com.me.mdm.server.enrollment.ios.IOSUpgradeMobileConfigCommandHandler;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class IOSMobileConfigUpgradeTask implements SchedulerExecutionInterface
{
    private final Logger logger;
    
    public IOSMobileConfigUpgradeTask() {
        this.logger = Logger.getLogger("MDMIosEnrollmentClientCertificateLogger");
    }
    
    public void executeTask(final Properties props) {
        try {
            this.logger.log(Level.INFO, "IOSMobileConfigUpgradeTask: Getting devices eligible for upgrade mobile config.");
            IOSUpgradeMobileConfigCommandHandler.getInstance().addIosUpgradeMobileConfigCommand(null, false, true);
            this.logger.log(Level.INFO, "IOSMobileConfigUpgradeTask: Task Completed.");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "IOSMobileConfigUpgradeTask: Exception while adding upgrade mobile config command to ios devices: ", e);
        }
    }
}
