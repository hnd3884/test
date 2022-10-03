package com.me.mdm.server.scheduler;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import com.me.mdm.apps.handler.AppsAutoDeployment;
import com.me.mdm.agent.handlers.MacMDMAgentHandler;
import java.util.Collection;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import java.util.Arrays;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.List;
import java.util.ArrayList;
import com.me.mdm.server.notification.NotificationHandler;
import com.me.mdm.server.updates.osupdates.task.OSUpdateScheduleTask;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.HashSet;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class MDMDistributedDailySchedulerTask implements SchedulerExecutionInterface
{
    private HashSet<Long> resourceSet;
    private static final Logger MDMLOGGER;
    private static final Logger MDM_ENROLLMENT;
    
    public MDMDistributedDailySchedulerTask() {
        this.resourceSet = new HashSet<Long>();
    }
    
    public void executeTask(final Properties taskProps) {
        MDMDistributedDailySchedulerTask.MDMLOGGER.log(Level.INFO, "MDM OSUpdate Task Starts");
        final Properties taskproperties = new Properties();
        final List osupdateResource = new OSUpdateScheduleTask().scheduleOSUpdatePolicy(taskproperties);
        this.addResourceForWakeup(osupdateResource);
        this.upgradeAgentForMacDevices();
        MDMDistributedDailySchedulerTask.MDMLOGGER.log(Level.INFO, "MDM OSUpdate Task End");
        MDMDistributedDailySchedulerTask.MDM_ENROLLMENT.log(Level.INFO, "Deprovision wake up task begins");
        try {
            final HashMap deprovisionDeviceIdMap = this.getDeviceResourceMapForDeprovisionedDevices();
            NotificationHandler.getInstance().SendNotification(deprovisionDeviceIdMap.get(1), 1);
            NotificationHandler.getInstance().SendNotification(deprovisionDeviceIdMap.get(2), 2);
            NotificationHandler.getInstance().SendNotification(deprovisionDeviceIdMap.get(3), 3);
        }
        catch (final Exception ex) {
            MDMDistributedDailySchedulerTask.MDM_ENROLLMENT.log(Level.SEVERE, "Exception while waking up deprovisioned devices with wipe pending", ex);
        }
        MDMDistributedDailySchedulerTask.MDM_ENROLLMENT.log(Level.INFO, "Deprovision wake up task ends");
        this.wakeupResources();
        MDMDistributedDailySchedulerTask.MDMLOGGER.log(Level.INFO, "retryinprogress device & rewakeIosDevice task starts..");
        try {
            final Long[] customerDetails = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            if (customerDetails != null) {
                for (int i = 0; i < customerDetails.length; ++i) {
                    final Long customerId = customerDetails[i];
                    NotificationHandler.wakeUpDeviceStruckInProgress(customerId);
                }
            }
        }
        catch (final SyMException e) {
            MDMDistributedDailySchedulerTask.MDMLOGGER.log(Level.SEVERE, "Exception while updating the retryinprogress device", (Throwable)e);
        }
    }
    
    private void upgradeAgentForMacDevices() {
        final Logger logger = Logger.getLogger("MDMConfigLogger");
        try {
            logger.log(Level.INFO, "-- Going to start agent upgrade task for Mac devices --");
            final List customerList = Arrays.asList(CustomerInfoUtil.getInstance().getCustomerIdsFromDB());
            final Iterator it = customerList.listIterator();
            while (it.hasNext()) {
                final Long customerID = it.next();
                final Row row = MDMDBUtil.getFirstRow("IOSAgentSettings", new Object[][] { { "CUSTOMER_ID", customerID } });
                if (row != null) {
                    final Boolean isEnabled = (Boolean)row.get("IS_NATIVE_APP_ENABLE");
                    final Boolean isFeatureEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MacMDMAgent");
                    Criteria criteria = ManagedDeviceHandler.getInstance().getYetToUpgradeMacOSManagedDevicesCriteria();
                    criteria = criteria.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0));
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put("CUSTOMER_ID", (Object)customerID);
                    jsonObject.put("AGENT_TYPE", 2);
                    jsonObject.put("IS_NATIVE_APP_ENABLE", isEnabled && isFeatureEnabled);
                    jsonObject.put("RESOURCE_LIST", (Object)new JSONArray((Collection)ManagedDeviceHandler.getInstance().getDeviceResourceIDs(criteria)));
                    final MacMDMAgentHandler handler = new MacMDMAgentHandler();
                    handler.updateAgentApp(customerID);
                    AppsAutoDeployment.getInstance().handleNativeAgent(jsonObject);
                }
            }
            logger.log(Level.INFO, "-- Agent upgrade task for MacOS completed --");
        }
        catch (final Exception e) {
            logger.log(Level.SEVERE, "-- MacOS Agent upgrade task failed --", e);
        }
    }
    
    private void addResourceForWakeup(final List resourceList) {
        this.resourceSet.addAll((Collection<?>)resourceList);
    }
    
    private void wakeupResources() {
        try {
            MDMDistributedDailySchedulerTask.MDMLOGGER.log(Level.INFO, "Waking up the resources from DistributedDailySchedule:{0}", new Object[] { this.resourceSet });
            NotificationHandler.getInstance().SendNotification(new ArrayList<Long>(this.resourceSet));
        }
        catch (final Exception e) {
            MDMDistributedDailySchedulerTask.MDMLOGGER.log(Level.SEVERE, "Exception in waking up device in distributed daily scheduler", e);
        }
    }
    
    private HashMap getDeviceResourceMapForDeprovisionedDevices() throws Exception {
        final HashMap<Integer, List<Long>> devicemap = new HashMap<Integer, List<Long>>();
        final List<Long> appleList = new ArrayList<Long>();
        final List<Long> androidList = new ArrayList<Long>();
        final List<Long> windowsList = new ArrayList<Long>();
        final List<String> resourceUDIDList = new ArrayList<String>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        selectQuery.addJoin(new Join("ManagedDevice", "DeprovisionHistory", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DeprovisionHistory", "DEPROVISION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DeprovisionHistory", "WIPE_PENDING"), (Object)true, 0).and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new int[] { 9, 11, 10 }, 8)));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator appleItr = dataObject.getRows("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0));
            final Iterator androidItr = dataObject.getRows("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0));
            final Iterator windowsItr = dataObject.getRows("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)3, 0));
            while (appleItr.hasNext()) {
                final Row row = appleItr.next();
                appleList.add((Long)row.get("RESOURCE_ID"));
                resourceUDIDList.add((String)row.get("UDID"));
            }
            while (androidItr.hasNext()) {
                final Row row = androidItr.next();
                androidList.add((Long)row.get("RESOURCE_ID"));
                resourceUDIDList.add((String)row.get("UDID"));
            }
            while (windowsItr.hasNext()) {
                final Row row = windowsItr.next();
                windowsList.add((Long)row.get("RESOURCE_ID"));
                resourceUDIDList.add((String)row.get("UDID"));
            }
        }
        devicemap.put(1, appleList);
        devicemap.put(2, androidList);
        devicemap.put(3, windowsList);
        MDMDistributedDailySchedulerTask.MDM_ENROLLMENT.log(Level.INFO, "Deprovision wipe pending device Ids to be wakedup: {0}", devicemap.toString());
        MDMDistributedDailySchedulerTask.MDM_ENROLLMENT.log(Level.INFO, "Deprovision wipe pending device UDIDs to be wakedup: {0}", resourceUDIDList.toString());
        return devicemap;
    }
    
    static {
        MDMLOGGER = Logger.getLogger("MDMLogger");
        MDM_ENROLLMENT = Logger.getLogger("MDMEnrollment");
    }
}
