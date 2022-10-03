package com.me.ems.onpremise.security.breachnotification.core;

import com.me.ems.framework.security.breachnotification.core.BreachNotificationAPI;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class BreachNotificationScheduler implements SchedulerExecutionInterface
{
    private static Logger logger;
    
    public void executeTask(final Properties properties) {
        BreachNotificationScheduler.logger.log(Level.INFO, "-------------------BreachNotificationScheduler-Started---------------");
        try {
            final BreachNotificationAPI breachNotificationAPI = ApiFactoryProvider.getBreachNotificationAPI();
            if (breachNotificationAPI.schedulerUpdate()) {
                BreachNotificationScheduler.logger.log(Level.INFO, "Security Notification Success");
            }
            else {
                BreachNotificationScheduler.logger.log(Level.INFO, "Security Notification Failure");
            }
        }
        catch (final Exception exp) {
            BreachNotificationScheduler.logger.log(Level.SEVERE, "---BreachNotificationScheduler - Exception---", exp);
        }
        finally {
            BreachNotificationScheduler.logger.log(Level.INFO, "-------------------BreachNotificationScheduler-Ended---------------");
        }
    }
    
    static {
        BreachNotificationScheduler.logger = Logger.getLogger("SecurityLogger");
    }
}
