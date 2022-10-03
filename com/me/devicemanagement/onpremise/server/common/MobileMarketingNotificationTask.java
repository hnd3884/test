package com.me.devicemanagement.onpremise.server.common;

import java.util.Properties;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class MobileMarketingNotificationTask implements SchedulerExecutionInterface
{
    public void executeTask(final Properties props) {
        final MobileMarketingNotification mobileMarketingNotification = new MobileMarketingNotification();
        mobileMarketingNotification.sendNotification();
    }
}
