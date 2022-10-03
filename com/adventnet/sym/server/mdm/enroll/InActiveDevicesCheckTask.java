package com.adventnet.sym.server.mdm.enroll;

import com.adventnet.ds.query.Join;
import java.util.Collection;
import java.util.Arrays;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DeleteQuery;
import java.util.HashMap;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.mdm.server.profiles.ios.IOSRemoveMangedProfileHandler;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import com.me.mdm.server.ios.apns.APNsCertificateHandler;
import java.util.List;
import java.util.ArrayList;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.enrollment.task.InactiveDevicePolicyTask;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class InActiveDevicesCheckTask implements SchedulerExecutionInterface
{
    private Logger logger;
    private static long day;
    
    public InActiveDevicesCheckTask() {
        this.logger = Logger.getLogger(InActiveDevicesCheckTask.class.getName());
    }
    
    public void executeTask(final Properties taskProps) {
        this.logger.log(Level.INFO, "Wakeup All iOS device to update last contact time");
        try {
            this.resendUpgradeAgentCommand();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
        }
        try {
            new InactiveDevicePolicyTask().executeTask();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in InActiveDevicesCheckTask calling InactiveDevicePolicyTask :{0}", ex);
        }
        final Criteria enrollSuccess = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria contactNotWithinADay = this.getCriteriaForAgentContact(1, 7);
        final Criteria contactAboveThreshold = this.getCriteriaForAgentContact(250, 5);
        final Criteria notifyCriteria = enrollSuccess.and(contactNotWithinADay).and(contactAboveThreshold);
        final Criteria removedDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)7, 0);
        final HashMap devicePlatformMap = this.getDeviceResourceIDBasedOnAgentContact(notifyCriteria.or(removedDeviceCriteria));
        try {
            NotificationHandler.getInstance().SendNotification(devicePlatformMap.get(1), 1);
            NotificationHandler.getInstance().SendNotification(devicePlatformMap.get(2), 2);
            NotificationHandler.getInstance().SendNotification(devicePlatformMap.get(3), 3);
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, null, ex2);
        }
        try {
            APNsCertificateHandler.getInstance().validateAPNSCertificateExpiry();
            WpAppSettingsHandler.getInstance().validateCertExpiry(true);
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while checking apns expiry ", ex2);
        }
        final List windows81AboveDevices = ManagedDeviceHandler.getInstance().getWindows81AboveWNSAllowedDeviceResourceIDs();
        final List windows81AboveDevicesWithAgent = ManagedDeviceHandler.getInstance().getWindows81AboveWNSAllowedAgentInstalledResourceIDs();
        try {
            DeviceCommandRepository.getInstance().addChannelUriCommand(windows81AboveDevices);
            DeviceCommandRepository.getInstance().addNativeAppChannelUriCommand(windows81AboveDevicesWithAgent);
        }
        catch (final Exception ex3) {
            this.logger.log(Level.SEVERE, "Exception while adding GetChannelUri command ", ex3);
        }
        final ManagedDeviceHandler managedDeviceHandler = ManagedDeviceHandler.getInstance();
        final List<Long> inactiveDevices = managedDeviceHandler.getInActiveDevicesByCommand(new Integer(2), 30, "ReregisterNotificationToken");
        this.logger.log(Level.INFO, "In-Active Android devcies for {0} days. Device resourceID :{1}", new Object[] { 30, inactiveDevices });
        if (!inactiveDevices.isEmpty()) {
            final JSONObject details = new JSONObject();
            try {
                details.put("MANAGED_STATUS", 4);
                final String sRemarks = "dc.mdm.profile.remarks.removed_from_device";
                details.put("REMARKS", (Object)sRemarks);
                managedDeviceHandler.updateManagedDeviceDetails(inactiveDevices, details);
            }
            catch (final Exception ex4) {
                this.logger.log(Level.WARNING, "Exception occurred while constructing managed device details data", ex4);
            }
        }
        new IOSRemoveMangedProfileHandler().updateRemovePayloadParams();
        try {
            DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("DeviceAppNotification");
            final Criteria criteria = new Criteria(new Column("DeviceAppNotification", "STATUS"), (Object)3, 0);
            final Criteria criteria2 = new Criteria(new Column("DeviceAppNotification", "ADDED_TIME"), (Object)(System.currentTimeMillis() - InActiveDevicesCheckTask.day * 2L), 6);
            deleteQuery.setCriteria(criteria.or(criteria2));
            MDMUtil.getPersistence().delete(deleteQuery);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppNotificationMessageInfo"));
            selectQuery.addSelectColumn(new Column("AppNotificationMessageInfo", "MESSAGE_ID"));
            final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceAppNotification"));
            subQuery.addSelectColumn(new Column("DeviceAppNotification", "MESSAGE_ID"));
            selectQuery.setCriteria(new Criteria(new Column("AppNotificationMessageInfo", "MESSAGE_ID"), (Object)new DerivedColumn("subQuery", subQuery), 9));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final List<Long> messageIds = new ArrayList<Long>();
                final Iterator iterator = dataObject.getRows("AppNotificationMessageInfo");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    messageIds.add(Long.parseLong(row.get("MESSAGE_ID").toString()));
                }
                deleteQuery = (DeleteQuery)new DeleteQueryImpl("AppNotificationMessageInfo");
                deleteQuery.setCriteria(new Criteria(new Column("AppNotificationMessageInfo", "MESSAGE_ID"), (Object)messageIds, 8));
                MDMUtil.getPersistence().delete(deleteQuery);
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Error while cleaning up junk message rows.", (Throwable)e);
        }
    }
    
    private Criteria getCriteriaForAgentContact(final int daysCount, final int queryConstant) {
        return new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)(System.currentTimeMillis() - InActiveDevicesCheckTask.day * daysCount), queryConstant);
    }
    
    private void resendUpgradeAgentCommand() throws Exception {
        final List resList = ManagedDeviceHandler.getInstance().getYetToUpgradeAndroidManagedDevices();
        if (!resList.isEmpty()) {
            DeviceCommandRepository.getInstance().addAgentUpgradeCommand(resList, 1);
        }
        final List iosList = ManagedDeviceHandler.getInstance().getYetToUpgradeIOSManagedDevices();
        if (!iosList.isEmpty()) {
            DeviceCommandRepository.getInstance().addAgentUpgradeCommand(iosList, 2);
        }
        final List windowsList = ManagedDeviceHandler.getInstance().getYetToUpgradeWindowsManagedDevices();
        if (!windowsList.isEmpty()) {
            DeviceCommandRepository.getInstance().addAgentUpgradeCommand(windowsList, 2);
        }
    }
    
    public HashMap getDeviceResourceIDBasedOnAgentContact(final Criteria criteria) {
        final HashMap<Integer, ArrayList<Long>> resourceIDMap = new HashMap<Integer, ArrayList<Long>>();
        final ArrayList<Long> iosList = new ArrayList<Long>();
        final ArrayList<Long> androidList = new ArrayList<Long>();
        final ArrayList<Long> winList = new ArrayList<Long>();
        final long currentTime = System.currentTimeMillis();
        final ArrayList<Integer> daysListForWakeUp = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 7, 10, 15, 23, 36, 57, 91, 146, 235));
        final ArrayList<String> resourceUDIDList = new ArrayList<String>();
        try {
            final SelectQuery recentlyInactiveDeviceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            recentlyInactiveDeviceQuery.addJoin(new Join("ManagedDevice", "AgentContact", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            recentlyInactiveDeviceQuery.addSelectColumn(Column.getColumn("ManagedDevice", "*"));
            recentlyInactiveDeviceQuery.addSelectColumn(Column.getColumn("AgentContact", "*"));
            recentlyInactiveDeviceQuery.setCriteria(criteria);
            final DataObject recentlyInactiveDeviceDO = MDMUtil.getPersistence().get(recentlyInactiveDeviceQuery);
            final Iterator recentlyInactiveDeviceIterator = recentlyInactiveDeviceDO.getRows("ManagedDevice");
            while (recentlyInactiveDeviceIterator.hasNext()) {
                final Row managedDeviceRow = recentlyInactiveDeviceIterator.next();
                final Integer platform = (Integer)managedDeviceRow.get("PLATFORM_TYPE");
                final Long resourceId = (Long)managedDeviceRow.get("RESOURCE_ID");
                final Row agentContactRow = recentlyInactiveDeviceDO.getRow("AgentContact", new Criteria(Column.getColumn("AgentContact", "RESOURCE_ID"), (Object)resourceId, 0));
                long deviceContactTime = -1L;
                if (agentContactRow != null) {
                    deviceContactTime = (long)agentContactRow.get("LAST_CONTACT_TIME");
                }
                final int daySinceWakeUp = (int)Math.floor((double)((currentTime - deviceContactTime) / InActiveDevicesCheckTask.day));
                if (daysListForWakeUp.contains(daySinceWakeUp)) {
                    this.logger.log(Level.FINE, "Days Since last Wakeup : {0} for device id : {1}", new Object[] { daySinceWakeUp, resourceId });
                    switch (platform) {
                        case 1: {
                            iosList.add(resourceId);
                            break;
                        }
                        case 2: {
                            androidList.add(resourceId);
                            break;
                        }
                        case 3: {
                            winList.add(resourceId);
                            break;
                        }
                    }
                    resourceUDIDList.add((String)managedDeviceRow.get("UDID"));
                }
            }
            resourceIDMap.put(1, iosList);
            resourceIDMap.put(2, androidList);
            resourceIDMap.put(3, winList);
            this.logger.log(Level.FINE, "ManagedDeviceHandler: Recently Inactive device Ids: {0}", resourceIDMap.toString());
            this.logger.log(Level.FINE, "ManagedDeviceHandler: Recently Inactive device UDIDs: {0}", resourceUDIDList.toString());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "ManagedDeviceHandler: Error in Getting fetching inactive device list", e);
        }
        return resourceIDMap;
    }
    
    static {
        InActiveDevicesCheckTask.day = 86400000L;
    }
}
