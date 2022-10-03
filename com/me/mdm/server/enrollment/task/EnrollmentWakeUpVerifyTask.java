package com.me.mdm.server.enrollment.task;

import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.me.mdm.server.enrollment.notification.EnrollmentNotificationHandler;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class EnrollmentWakeUpVerifyTask implements SchedulerExecutionInterface
{
    private Logger logger;
    
    public EnrollmentWakeUpVerifyTask() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void executeTask(final Properties taskProps) {
        try {
            final int errorCode = Integer.parseInt(String.valueOf(51201));
            final Long erid = Long.parseLong(taskProps.getProperty("ENROLLMENT_REQUEST_ID"));
            final int status = EnrollmentNotificationHandler.getInstance().getNotificationStatus(erid);
            if (status == 0) {
                MDMEnrollmentRequestHandler.getInstance().updateEnrollmentStatusAndErrorCode(erid, 0, "mdm.enroll.remarks.device_unable_to_reach_apns", errorCode);
            }
        }
        catch (final Exception ex) {
            this.logger.severe("Exception occurred in WakeUpVerifyTask  : " + ex);
        }
    }
}
