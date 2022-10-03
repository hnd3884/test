package com.adventnet.sym.server.mdm.security.safetynet;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.Properties;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueProcessorInterface;

public class SafetyNetQueueActions implements CommonQueueProcessorInterface, SchedulerExecutionInterface
{
    Logger logger;
    
    public SafetyNetQueueActions() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public void processData(final CommonQueueData data) {
        final String taskName = data.getTaskName();
        if (taskName.equalsIgnoreCase("WipeAllDevices")) {
            this.wipeIntegrityFailedDevices(data.getJsonQueueData());
        }
        else if (taskName.equalsIgnoreCase("WipeDevice")) {
            this.wipeIntegrityFailedDevice(data.getJsonQueueData());
        }
    }
    
    public void executeTask(final Properties props) {
        final CommonQueueData tempData = new CommonQueueData();
        try {
            tempData.setJsonQueueData(new JSONObject((String)((Hashtable<K, String>)props).get("jsonParams")));
            tempData.setCustomerId(((Hashtable<K, Long>)props).get("customerId"));
            this.processData(tempData);
        }
        catch (final JSONException exp) {
            this.logger.log(Level.SEVERE, "Cannot fetch JSON from Props", (Throwable)exp);
        }
    }
    
    private void wipeIntegrityFailedDevices(final JSONObject androidSettings) {
        this.logger.log(Level.INFO, "SafetyNetQueueActions: Going to find integrity failed devices and wipe them");
        final String resId = "ResourceID";
        final String reason = "Reason";
        try {
            final boolean wipeIntegrityFailedDevices = androidSettings.optBoolean("WIPE_INTEGRITY_FAILED_DEVICES", true);
            final ArrayList<JSONObject> wipeDeviceAndReason = new ArrayList<JSONObject>();
            final SelectQuery deviceList = (SelectQuery)new SelectQueryImpl(Table.getTable("SafetyNetStatus"));
            deviceList.addSelectColumn(Column.getColumn("SafetyNetStatus", "RESOURCE_ID"));
            final Criteria integrityFailed = new Criteria(Column.getColumn("SafetyNetStatus", "SAFETYNET_BASIC_INTEGRITY"), (Object)false, 0);
            final Criteria safetyNetAttested = new Criteria(Column.getColumn("SafetyNetStatus", "SAFETYNET_AVAILABIITY"), (Object)true, 0);
            final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            deviceList.addJoin(new Join("SafetyNetStatus", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            if (wipeIntegrityFailedDevices) {
                deviceList.setCriteria(integrityFailed.and(safetyNetAttested).and(managedDeviceCriteria));
            }
            final DataObject failedDeviceList = DataAccess.get(deviceList);
            final Iterator iter = failedDeviceList.getRows("SafetyNetStatus");
            while (iter.hasNext()) {
                final Row deviceRow = iter.next();
                final Long resourceId = (Long)deviceRow.get("RESOURCE_ID");
                final JSONObject wipeDetails = new JSONObject();
                wipeDetails.put(resId, (Object)resourceId);
                wipeDetails.put(reason, 1);
                wipeDeviceAndReason.add(wipeDetails);
            }
            if (androidSettings.optBoolean("WIPE_CTS_FAILED_DEVICES", false)) {
                final SelectQuery deviceListCts = (SelectQuery)new SelectQueryImpl(Table.getTable("SafetyNetStatus"));
                deviceListCts.addSelectColumn(Column.getColumn("SafetyNetStatus", "RESOURCE_ID"));
                final Criteria ctsFailed = new Criteria(Column.getColumn("SafetyNetStatus", "SAFETYNET_CTS"), (Object)false, 0);
                deviceListCts.addJoin(new Join("SafetyNetStatus", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                deviceListCts.setCriteria(ctsFailed.and(safetyNetAttested).and(managedDeviceCriteria));
                final DataObject failedCtsList = DataAccess.get(deviceListCts);
                final Iterator iter2 = failedCtsList.getRows("SafetyNetStatus");
                while (iter2.hasNext()) {
                    final Row deviceRow2 = iter2.next();
                    final Long resourceId2 = (Long)deviceRow2.get("RESOURCE_ID");
                    final JSONObject wipeDetails2 = new JSONObject();
                    wipeDetails2.put(resId, (Object)resourceId2);
                    wipeDetails2.put(reason, 2);
                    wipeDeviceAndReason.add(wipeDetails2);
                }
            }
            for (final JSONObject obj : wipeDeviceAndReason) {
                final Long resourceId3 = obj.optLong(resId);
                final int wipeReason = obj.optInt(reason);
                if (wipeReason == 1) {
                    new SafetyNetHandler().handlePostResponseWipe(resourceId3, 1, "mdm.agent.wipe.integrity_failed", "The device failed the basic integrity check and doesn't comply with the organization's requirements for management.");
                }
                else {
                    if (wipeReason != 2) {
                        continue;
                    }
                    new SafetyNetHandler().handlePostResponseWipe(resourceId3, 2, "mdm.agent.wipe.cts_failed", "The device failed the CTS(compatibility check) check and doesn't comply with the organization's requirements for management.");
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "SafetyNetQueueActions: Cannot get the list of integrity failed devices ", exp);
        }
    }
    
    private void wipeIntegrityFailedDevice(final JSONObject deviceDetails) {
        final Long resId = JSONUtil.optLong(deviceDetails, "DeviceWipeResId", 1L);
        final int managedStatus = ManagedDeviceHandler.getInstance().getManagedDeviceStatus(resId);
        if (managedStatus == 2) {
            this.logger.log(Level.INFO, " SafetyNetQueueActions : Managed Device is being wiped for failing safetynet integrity {0}", resId);
            final int reason = deviceDetails.optInt("WipeReason", 1);
            final String reasonForWipeI18 = deviceDetails.optString("WipeReasonI18", "");
            final String reasonForWipeText = deviceDetails.optString("WipeReasonFeedBack", "");
            new SafetyNetHandler().handlePostResponseWipe(resId, reason, reasonForWipeI18, reasonForWipeText);
        }
        else {
            this.logger.log(Level.INFO, " SafetyNetQueueActions : The device is not managed by MDM anymore so no need to perform any actions {0}", resId);
        }
    }
}
