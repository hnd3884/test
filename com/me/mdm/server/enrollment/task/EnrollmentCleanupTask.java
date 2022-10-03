package com.me.mdm.server.enrollment.task;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.Hashtable;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.eventlog.EventConstant;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Logger;

public class EnrollmentCleanupTask
{
    public Logger logger;
    
    public EnrollmentCleanupTask() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public void executeTask() {
        try {
            final Long[] customerIDs = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            if (customerIDs != null) {
                for (final Long customerID : customerIDs) {
                    String enrollReqIDs = this.getUnusedSelfEnrollReqIDs(customerID);
                    final String removedErids = this.getUnclearedRemovedDevices(customerID);
                    if (enrollReqIDs.length() != 0 && removedErids.length() != 0) {
                        enrollReqIDs = enrollReqIDs + "," + removedErids;
                    }
                    else if (removedErids.length() != 0) {
                        enrollReqIDs = removedErids;
                    }
                    this.logger.info("Enrollment_Cleanup: Cleaning up self enrollment requests and trashed deives..." + enrollReqIDs);
                    MDMEnrollmentUtil.getInstance().removeDevice(enrollReqIDs, EventConstant.DC_SYSTEM_USER, customerID);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while cleaning up unused self enrollment requests: {0}", ex);
        }
    }
    
    private String getUnusedSelfEnrollReqIDs(final Long customerID) throws Exception {
        final StringBuilder builder = new StringBuilder("");
        final SelectQuery squery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
        squery.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        squery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        squery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        squery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        squery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", "DEVICEENROLLMENTREQUEST.ENROLLMENT_REQUEST_ID"));
        final Hashtable ht = DateTimeUtil.determine_From_To_Times("today");
        final Long yesterday = ht.get("date2") - 86400000L;
        final Criteria requestedTime = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUESTED_TIME"), (Object)yesterday, 6);
        final Criteria enrollStatus = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)new Integer[] { 0, 1 }, 8);
        final Criteria selfEnroll = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)2, 0);
        final Criteria noManagedDevice = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria checkinSuccess = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "UDID"), (Object)null, 0);
        final Criteria enrollmentRequestToDeviceCriteria = new Criteria(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"), (Object)null, 0);
        squery.setCriteria(requestedTime.and(enrollStatus).and(selfEnroll).and(noManagedDevice).and(customerCriteria).and(checkinSuccess).and(enrollmentRequestToDeviceCriteria));
        final DataObject DO = MDMUtil.getReadOnlyPersistence().get(squery);
        if (!DO.isEmpty()) {
            final Iterator<Row> iterator = DO.getRows("DeviceEnrollmentRequest");
            while (iterator.hasNext()) {
                final Row r = iterator.next();
                final Long erid = (Long)r.get("ENROLLMENT_REQUEST_ID");
                if (builder.length() != 0) {
                    builder.append(",");
                }
                builder.append(erid);
            }
        }
        final String eridStr = builder.toString();
        if (!eridStr.trim().equalsIgnoreCase("")) {
            MDMEnrollmentUtil.getInstance().deviceWithoutRequestDebugLog(eridStr.split(","), "enrollmentCleanUpTask");
        }
        return eridStr;
    }
    
    private String getUnclearedRemovedDevices(final Long customerID) throws Exception {
        final StringBuilder builder = new StringBuilder("");
        final Hashtable ht = DateTimeUtil.determine_From_To_Times("today");
        final Long sevenDaysBeforeToday = ht.get("date2") - 604800000L;
        final Long ninetyDaysBeforeToday = ht.get("date2") - 7776000000L;
        final SelectQuery squery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        squery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        squery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        squery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        final Criteria removeDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)7, 0).and(new Criteria(Column.getColumn("ManagedDevice", "UNREGISTERED_TIME"), (Object)sevenDaysBeforeToday, 6));
        final Criteria retireDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)11, 0).and(new Criteria(Column.getColumn("ManagedDevice", "UNREGISTERED_TIME"), (Object)ninetyDaysBeforeToday, 6).and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "OWNED_BY"), (Object)2, 0)));
        final Criteria customerIdCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        squery.setCriteria(customerIdCriteria.and(removeDeviceCriteria.or(retireDeviceCriteria)));
        squery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", "DEVICEENROLLMENTREQUEST.ENROLLMENT_REQUEST_ID"));
        final DataObject DO = MDMUtil.getReadOnlyPersistence().get(squery);
        if (!DO.isEmpty()) {
            final Iterator<Row> iterator = DO.getRows("DeviceEnrollmentRequest");
            while (iterator.hasNext()) {
                final Row r = iterator.next();
                final Long erid = (Long)r.get("ENROLLMENT_REQUEST_ID");
                if (builder.length() != 0) {
                    builder.append(",");
                }
                builder.append(erid);
            }
        }
        return builder.toString();
    }
}
