package com.me.mdm.core.enrollment;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.core.DeviceEventForQueue;
import java.util.ArrayList;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class AdminEnrollmentPostAssignUserTask extends DCQueueDataProcessor
{
    private static Logger logger;
    
    public void processData(final DCQueueData qData) {
        AdminEnrollmentPostAssignUserTask.logger.log(Level.INFO, "Executing AdminEnrollmentPostAssignUserTask");
        final List<Long> deviceList = new ArrayList<Long>();
        try {
            final ArrayList<DeviceEventForQueue> invokeListenerList = (ArrayList<DeviceEventForQueue>)qData.queueData;
            for (final DeviceEventForQueue deviceEventForQueue : invokeListenerList) {
                final DeviceEvent deviceEvent = new DeviceEvent(deviceEventForQueue.resourceID, deviceEventForQueue.customerID);
                deviceEvent.udid = deviceEventForQueue.udid;
                deviceEvent.enrollmentRequestId = deviceEventForQueue.enrollmentRequestId;
                deviceEvent.platformType = deviceEventForQueue.platformType;
                deviceEvent.resourceJSON = new JSONObject(deviceEventForQueue.resourceJSON);
                if (deviceEvent.resourceJSON != null && ((deviceEvent.resourceJSON.has("UDID") && MDMStringUtils.isEmpty(deviceEvent.resourceJSON.optString("UDID"))) || !deviceEvent.resourceJSON.has("UDID")) && !MDMStringUtils.isEmpty(deviceEvent.udid)) {
                    deviceEvent.resourceJSON.put("UDID", (Object)deviceEvent.udid);
                    AdminEnrollmentPostAssignUserTask.logger.log(Level.INFO, "UDID NotPresent in resourceJSON so appending from object");
                }
                AdminEnrollmentPostAssignUserTask.logger.log(Level.INFO, "AdminEnrollmentPostAssignUserTask resourceJSON : {0}", deviceEvent.resourceJSON);
                ManagedDeviceHandler.getInstance().invokeDeviceListeners(deviceEvent, 1);
                deviceList.add(deviceEvent.resourceID);
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "REMARKS"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceList.toArray(), 8));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ManagedDevice");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final int managedStatus = (int)row.get("MANAGED_STATUS");
                    if (managedStatus == 4) {
                        row.set("REMARKS", (Object)"dc.mdm.profile.ios.remarks.removed_from_device");
                    }
                    else {
                        row.set("REMARKS", (Object)"dc.mdm.db.agent.enroll.agent_enroll_finished");
                    }
                    dataObject.updateRow(row);
                }
            }
            MDMUtil.getPersistenceLite().update(dataObject);
        }
        catch (final Exception e) {
            AdminEnrollmentPostAssignUserTask.logger.log(Level.SEVERE, "Exception in executeTask", e);
        }
        AdminEnrollmentPostAssignUserTask.logger.log(Level.INFO, "Finished Executing AdminEnrollmentPostAssignUserTask");
    }
    
    static {
        AdminEnrollmentPostAssignUserTask.logger = Logger.getLogger("MDMEnrollment");
    }
}
