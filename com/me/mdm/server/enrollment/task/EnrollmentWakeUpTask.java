package com.me.mdm.server.enrollment.task;

import com.adventnet.sym.server.mdm.ios.APNSImpl;
import java.util.HashMap;
import com.me.mdm.server.notification.PushNotificationHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import org.json.JSONObject;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class EnrollmentWakeUpTask implements SchedulerExecutionInterface
{
    public Logger logger;
    
    public EnrollmentWakeUpTask() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void executeTask(final Properties taskProps) {
        try {
            final Long erid = Long.parseLong(taskProps.getProperty("ENROLLMENT_REQUEST_ID"));
            this.logger.log(Level.INFO, "Inside EnrollmentWakeUpTask.. for erid.. {0}", erid);
            final JSONObject json = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestProperties(erid);
            if (json != null && json.length() != 0) {
                this.wakeUpDeviceNow(erid, 1);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while executing Enrollment Wakeup  task {0}", ex);
        }
    }
    
    private void wakeUpDeviceNow(final Long erid, final int notificationType) {
        try {
            final JSONObject enrollProperties = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestProperties(erid);
            final String udid = String.valueOf(enrollProperties.get("DeviceEnrollmentRequest.UDID"));
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            String deviceToken = null;
            JSONObject map;
            if (enrollProperties.has("ManagedDevice.RESOURCE_ID")) {
                map = PushNotificationHandler.getInstance().getNotificationDetails(enrollProperties.getLong("ManagedDevice.RESOURCE_ID"), notificationType);
                deviceToken = (String)map.get("NOTIFICATION_TOKEN_ENCRYPTED");
            }
            else {
                map = PushNotificationHandler.getInstance().getiOSEnrollmentTempData(udid);
                deviceToken = (String)map.get("DEVICE_TOKEN");
            }
            if (map.length() == 0) {
                throw new Exception("Comm Details are not available for device : " + udid);
            }
            final HashMap additionalProps = new HashMap();
            additionalProps.put("ENROLLMENT_REQUEST_ID", erid);
            additionalProps.put("IS_SOURCE_TOKEN_UPDATE", false);
            additionalProps.put("RESOURCE_ID", resourceID);
            APNSImpl.getInstance().wakeUpDeviceWithERID(deviceToken, (String)map.get("PUSH_MAGIC"), (String)map.get("TOPIC"), additionalProps);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "EnrollmentWakeUpTask: Exception in wakeUpDeviceNow ({0}): {1}", new Object[] { erid, ex });
        }
    }
}
