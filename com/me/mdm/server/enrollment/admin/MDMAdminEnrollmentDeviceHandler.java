package com.me.mdm.server.enrollment.admin;

import java.util.List;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.me.mdm.core.enrollment.DeviceForEnrollmentHandler;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.enrollment.MDMEnrollmentDeviceHandler;

public class MDMAdminEnrollmentDeviceHandler extends MDMEnrollmentDeviceHandler
{
    public Logger logger;
    String sourceClass;
    
    public MDMAdminEnrollmentDeviceHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
        this.sourceClass = "MDMAdminEnrollmentDeviceHandler";
    }
    
    @Override
    public JSONObject enrollDevice(final JSONObject deviceJSON) throws SyMException {
        try {
            Long deviceForEnrollmentId = null;
            Boolean dfeAdded = Boolean.FALSE;
            final Long reqId = deviceJSON.getLong("ENROLLMENT_REQUEST_ID");
            final int managedStatus = deviceJSON.getInt("MANAGED_STATUS");
            if (managedStatus == 2) {
                final DeviceForEnrollmentHandler deviceForEnrollmentHandler = new DeviceForEnrollmentHandler();
                final JSONObject deviceDetailsJSON = new JSONObject();
                deviceDetailsJSON.putOpt("UDID", (Object)deviceJSON.optString("UDID", "--"));
                deviceDetailsJSON.putOpt("SerialNumber", (Object)deviceJSON.optString("SERIAL_NUMBER", "--"));
                deviceDetailsJSON.putOpt("IMEI", (Object)deviceJSON.optString("IMEI", "--"));
                deviceDetailsJSON.putOpt("EASID", (Object)deviceJSON.optString("EAS_DEVICE_IDENTIFIER", "--"));
                deviceDetailsJSON.putOpt("GENERIC_ID", (Object)deviceJSON.optString("GENERIC_IDENTIFIER", "--"));
                deviceForEnrollmentId = deviceForEnrollmentHandler.getDeviceForEnrollmentId(deviceDetailsJSON);
                if (deviceForEnrollmentId != null) {
                    final Long deviceEnrollmentRequestID = deviceForEnrollmentHandler.getAssociatedEnrollmentRequestid(deviceForEnrollmentId);
                    if (deviceEnrollmentRequestID == null) {
                        new DeviceForEnrollmentHandler().addOrUpdateRequestForDevice(deviceForEnrollmentId, reqId);
                    }
                    final Long managedUserId = deviceForEnrollmentHandler.getAssociatedUserid(deviceForEnrollmentId);
                    if (managedUserId != null) {
                        ManagedUserHandler.getInstance().changeUser(managedUserId, new Long[] { reqId });
                    }
                    final List<Long> groupId = deviceForEnrollmentHandler.getAssociatedGroupId(deviceForEnrollmentId);
                    if (groupId != null && !groupId.isEmpty()) {
                        MDMEnrollmentRequestHandler.getInstance().addEnrollmentToGroupEntries(reqId, groupId);
                    }
                }
                else if (deviceJSON.has("SERIAL_NUMBER") || deviceJSON.has("IMEI")) {
                    final JSONObject dfeJSON = new JSONObject();
                    dfeJSON.put("CustomerId", deviceJSON.getLong("CUSTOMER_ID"));
                    dfeJSON.put("SerialNumber", (Object)deviceJSON.optString("SERIAL_NUMBER"));
                    dfeJSON.put("IMEI", (Object)deviceJSON.optString("IMEI"));
                    dfeJSON.put("EASID", (Object)deviceJSON.optString("EAS_DEVICE_IDENTIFIER"));
                    dfeJSON.put("UDID", (Object)deviceJSON.optString("UDID"));
                    if (deviceJSON.has("MANAGED_USER_ID")) {
                        dfeJSON.put("MANAGED_USER_ID", deviceJSON.getLong("MANAGED_USER_ID"));
                    }
                    final String templateToken = new EnrollmentTemplateHandler().getTemplateTokenForEnrollmentRequest(reqId);
                    dfeJSON.put("TEMPLATE_TOKEN", (Object)templateToken);
                    deviceForEnrollmentId = new DeviceForEnrollmentHandler().addDeviceForEnrollment(dfeJSON, new EnrollmentTemplateHandler().getEnrollmentTemplateTypeForErid(reqId));
                    if (deviceForEnrollmentId != null) {
                        dfeAdded = Boolean.TRUE;
                        new DeviceForEnrollmentHandler().addOrUpdateRequestForDevice(deviceForEnrollmentId, reqId);
                    }
                }
                if (!EnrollmentTemplateHandler.isUserAssignmentCompleted(reqId)) {
                    deviceJSON.put("MANAGED_STATUS", 5);
                }
            }
            if (dfeAdded && deviceForEnrollmentId != null) {
                final Long managedUserId2 = new DeviceForEnrollmentHandler().getAssociatedUserid(deviceForEnrollmentId);
                if (managedUserId2 != null) {
                    ManagedUserHandler.getInstance().changeUser(managedUserId2, new Long[] { reqId });
                    deviceJSON.put("MANAGED_STATUS", 2);
                }
            }
            final JSONObject response = super.enrollDevice(deviceJSON);
            return response;
        }
        catch (final JSONException ex) {
            throw new SyMException(-1, "Manadatory fields missing", (Throwable)ex);
        }
        catch (final DataAccessException ex2) {
            throw new SyMException(-1, "Internal Server Error", (Throwable)ex2);
        }
        catch (final Exception ex3) {
            throw new SyMException(-1, "Internal Server Error", (Throwable)ex3);
        }
    }
}
