package com.me.mdm.server.enrollment.task;

import org.json.JSONException;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import org.json.JSONObject;
import com.me.mdm.server.notification.WakeUpProcessor;
import java.util.StringTokenizer;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.enrollment.notification.EnrollmentNotificationHandler;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class PostEnrollmentWakeUpTask implements SchedulerExecutionInterface
{
    private Logger logger;
    
    public PostEnrollmentWakeUpTask() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void executeTask(final Properties taskProps) {
        try {
            if (taskProps.containsKey("ENROLLMENT_REQUEST_ID")) {
                final Long enrollmentID = Long.valueOf(taskProps.getProperty("ENROLLMENT_REQUEST_ID"));
                this.logger.log(Level.INFO, "Inside PostEnrollmentWakeUpTask.. for erid.. {0}", enrollmentID);
                final Long resourceID = Long.valueOf(taskProps.getProperty("RESOURCE_ID"));
                final Boolean isSourceEnrollment = Boolean.valueOf(taskProps.getProperty("IS_SOURCE_TOKEN_UPDATE"));
                final Boolean isAboveIOS8 = this.isAboveIOS8(resourceID);
                if (isAboveIOS8 != null && isAboveIOS8 && isSourceEnrollment) {
                    this.rewakeIOSDevice(enrollmentID, 30000L);
                }
                else {
                    this.qualifyPortBlockMessage(enrollmentID);
                }
                EnrollmentNotificationHandler.getInstance().removeNotification(enrollmentID);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception during post wake up : {0}", e);
        }
    }
    
    private Boolean isAboveIOS8(final Long resourceID) {
        try {
            final String OSVersion = (String)DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceID, "OS_VERSION");
            if (OSVersion != null) {
                final StringTokenizer st = new StringTokenizer(OSVersion, ".");
                return st.hasMoreElements() && Integer.parseInt(st.nextElement().toString()) >= 8;
            }
            return null;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while getting device's os version : {0}", ex);
            return null;
        }
    }
    
    private void rewakeIOSDevice(final Long enrollmentID, final Long time) throws Exception {
        WakeUpProcessor.wakeUpAsynchronously(enrollmentID, time);
    }
    
    private void qualifyPortBlockMessage(final Long enrollmentID) throws JSONException {
        final JSONObject inputJSON = new JSONObject();
        inputJSON.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentID);
        final JSONObject json = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestStatusAndErrorCode(inputJSON);
        if (json.has("REQUEST_STATUS") && json.getInt("REQUEST_STATUS") == 0 && json.getInt("ERROR_CODE") == 51201) {
            MDMEnrollmentRequestHandler.getInstance().updateDeviceRequestStatus(enrollmentID, 3, 1);
        }
    }
}
