package com.adventnet.sym.server.mdm.task;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class AndroidAdminEnrollCompletionTask implements SchedulerExecutionInterface
{
    public void executeTask(final Properties taskProps) {
        final String devicesPresent = MDMUtil.getSyMParameter("UnEnrolledButAssignedNFCDevicesPresent");
        if (devicesPresent != null && !devicesPresent.isEmpty() && Boolean.parseBoolean(devicesPresent)) {
            try {
                final SelectQuery squery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
                squery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
                squery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToUser", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
                squery.addJoin(new Join("DeviceEnrollmentToRequest", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
                squery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
                squery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                squery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
                squery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"));
                squery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "IMEI"));
                squery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "SERIAL_NUMBER"));
                squery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "EAS_DEVICE_IDENTIFIER"));
                squery.addSelectColumn(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_DEVICE_ID"));
                squery.addSelectColumn(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_REQUEST_ID", "DEVICEENROLLMENTTOREQUEST.ENROLLMENT_REQUEST_ID"));
                squery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID", "ENROLLMENTREQUESTTODEVICE.ENROLLMENT_REQUEST_ID"));
                squery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"));
                squery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
                squery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
                final Criteria devicePresent = new Criteria(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
                final Criteria userAssigned = new Criteria(Column.getColumn("DeviceEnrollmentToUser", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
                final Criteria managed = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
                squery.setCriteria(devicePresent.and(userAssigned).and(managed));
                final DataObject dobj = MDMUtil.getPersistence().get(squery);
                if (!dobj.isEmpty()) {
                    final Iterator<Row> deviceForEnrollmentIterator = dobj.getRows("DeviceForEnrollment");
                    while (deviceForEnrollmentIterator.hasNext()) {
                        final Row deviceForEnrollmentRow = deviceForEnrollmentIterator.next();
                        final String serialNumber = (String)deviceForEnrollmentRow.get("SERIAL_NUMBER");
                        final String imei = (String)deviceForEnrollmentRow.get("IMEI");
                        final String easID = (String)deviceForEnrollmentRow.get("EAS_DEVICE_IDENTIFIER");
                        final Long customerID = (Long)deviceForEnrollmentRow.get("CUSTOMER_ID");
                        final Row deviceEnrollmentToRequestRow = dobj.getRow("DeviceEnrollmentToRequest", new Criteria(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_DEVICE_ID"), deviceForEnrollmentRow.get("ENROLLMENT_DEVICE_ID"), 0));
                        final Long enrollmentRequestID = (Long)deviceEnrollmentToRequestRow.get("ENROLLMENT_REQUEST_ID");
                        final Row enrollReqToDeviceRow = dobj.getRow("EnrollmentRequestToDevice", new Criteria(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"), deviceEnrollmentToRequestRow.get("ENROLLMENT_REQUEST_ID"), 0));
                        final Row managedDeviceRow = dobj.getRow("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), enrollReqToDeviceRow.get("MANAGED_DEVICE_ID"), 0));
                        final Long managedDeviceID = (Long)managedDeviceRow.get("RESOURCE_ID");
                        final Integer platformType = (Integer)managedDeviceRow.get("PLATFORM_TYPE");
                        final DeviceEvent deviceEvent = new DeviceEvent(managedDeviceID, customerID);
                        deviceEvent.udid = ManagedDeviceHandler.getInstance().getUDIDFromResourceID(managedDeviceID);
                        deviceEvent.enrollmentRequestId = enrollmentRequestID;
                        deviceEvent.platformType = platformType;
                        final JSONObject resourceJSON = new JSONObject();
                        resourceJSON.put("SERIAL_NUMBER", (Object)serialNumber);
                        resourceJSON.put("IMEI", (Object)imei);
                        resourceJSON.put("EAS_DEVICE_IDENTIFIER", (Object)easID);
                        deviceEvent.resourceJSON = resourceJSON;
                        ManagedDeviceHandler.getInstance().invokeDeviceListeners(deviceEvent, 1);
                    }
                }
            }
            catch (final Exception ex) {
                Logger.getLogger(AndroidAdminEnrollCompletionTask.class.getName()).log(Level.SEVERE, null, ex);
            }
            MDMUtil.deleteSyMParameter("UnEnrolledButAssignedNFCDevicesPresent");
        }
    }
}
