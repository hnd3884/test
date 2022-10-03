package com.me.mdm.server.notification;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.server.enrollment.ios.MDMProfileInstallationHandler;
import com.me.mdm.server.enrollment.notification.EnrollmentNotificationHandler;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class DeviceToApnsPortBlockDeviceListener extends ManagedDeviceListener
{
    @Override
    public void deviceUnmanaged(final DeviceEvent userEvent) {
        DeviceToApnsPortBlockDeviceListener.mdmlogger.info("Entering DeviceToApnsPortBlockDeviceListener:deviceUnmanaged");
        try {
            if (userEvent.platformType == 1) {
                if ((userEvent.enrollmentRequestId == null || userEvent.customerID == null) && userEvent.udid != null) {
                    final JSONObject json = ManagedDeviceHandler.getInstance().getEnrollmentDetailsForDevice(userEvent.udid);
                    if (json.has("ENROLLMENT_REQUEST_ID")) {
                        userEvent.enrollmentRequestId = json.getLong("ENROLLMENT_REQUEST_ID");
                        userEvent.customerID = json.getLong("CUSTOMER_ID");
                    }
                }
                if (userEvent.enrollmentRequestId != null && userEvent.customerID != null) {
                    final JSONObject enrollmentProps = new JSONObject();
                    enrollmentProps.put("UDID", (Object)"");
                    enrollmentProps.put("REQUEST_STATUS", 1);
                    enrollmentProps.put("REMARKS", (Object)"dc.mdm.enroll.request_created");
                    MDMEnrollmentRequestHandler.getInstance().updateEnrollmentRequestProperties(userEvent.enrollmentRequestId, enrollmentProps);
                    EnrollmentNotificationHandler.getInstance().removeNotification(userEvent.enrollmentRequestId);
                }
                MDMProfileInstallationHandler.getInstance().clearProfileInstallationStatus(userEvent.enrollmentRequestId);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(DeviceToApnsPortBlockDeviceListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        DeviceToApnsPortBlockDeviceListener.mdmlogger.info("Exiting DeviceToApnsPortBlockDeviceListener:deviceUnmanaged");
    }
}
