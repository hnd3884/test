package com.me.mdm.server.apps.android.afw;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class GoogleAccountEnableTask implements SchedulerExecutionInterface
{
    public void executeTask(final Properties props) {
        final String taskType = ((Hashtable<K, String>)props).get("TASK_TYPE");
        if (taskType != null && taskType.equalsIgnoreCase("StartMissedTaskToEnableState")) {
            this.startMissedTaskToEnableState();
        }
        else if (taskType != null && taskType.equalsIgnoreCase("EnableAccountState")) {
            Integer lastScheduleDelay = Integer.parseInt(props.getProperty("DELAY_PERIOD"));
            final Long resourceId = Long.parseLong(props.getProperty("MANAGED_DEVICE_ID"));
            final Long bsUserId = Long.parseLong(props.getProperty("BS_USER_ID"));
            try {
                new GoogleAccountChangeHandler().enableGooglePlay(resourceId, bsUserId);
            }
            catch (final GoogleJsonResponseException ex) {
                Logger.getLogger("MDMLogger").log(Level.WARNING, (Throwable)ex, () -> "Exception while attempting to enable the state of device " + n + " for user " + n2);
                final GoogleJsonError err = ex.getDetails();
                if (err.getCode() == 404) {
                    if (lastScheduleDelay > 600) {
                        Logger.getLogger("MDMLogger").log(Level.WARNING, (Throwable)ex, () -> " Since the scheduler runs for more than 10 minutes so drop the request silently " + n3 + " for user " + n4);
                    }
                    else {
                        lastScheduleDelay *= 2;
                        Logger.getLogger("MDMLogger").log(Level.WARNING, "Rescheduling it for {0}", lastScheduleDelay);
                        startAccEnableTaskAfterDelay(lastScheduleDelay, props);
                    }
                }
            }
            catch (final Exception ex2) {
                Logger.getLogger(GoogleAccountChangeHandler.class.getName()).log(Level.SEVERE, ex2, () -> "Exception when enabling Google Account for " + properties);
            }
        }
    }
    
    public void startMissedTaskToEnableState() {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BSUsersToManagedDevices"));
            final Join accStateJoin = new Join("BSUsersToManagedDevices", "BSUserToManagedDeviceAccState", new String[] { "BS_USER_TO_DEVICE_ID" }, new String[] { "BS_USER_TO_DEVICE_ID" }, 1);
            selectQuery.addJoin(accStateJoin);
            selectQuery.addSelectColumn(Column.getColumn("BSUsersToManagedDevices", "*"));
            selectQuery.addSelectColumn(Column.getColumn("BSUserToManagedDeviceAccState", "*"));
            final Criteria bsAccStatusCriteria = new Criteria(Column.getColumn("BSUsersToManagedDevices", "ACCOUNT_STATUS"), (Object)1, 0);
            final Criteria bsAccStateNotEnabledCriteria = new Criteria(Column.getColumn("BSUserToManagedDeviceAccState", "ACCOUNT_STATE"), (Object)1, 1);
            final Criteria bsAccStateNullCriteria = new Criteria(Column.getColumn("BSUserToManagedDeviceAccState", "ACCOUNT_STATE"), (Object)null, 0);
            selectQuery.setCriteria(bsAccStatusCriteria.and(bsAccStateNotEnabledCriteria.or(bsAccStateNullCriteria)));
            final DataObject dO = MDMUtil.getPersistence().get(selectQuery);
            if (!dO.isEmpty()) {
                final Iterator iter = dO.getRows("BSUsersToManagedDevices");
                while (iter.hasNext()) {
                    final Row row = iter.next();
                    final Properties prop = new Properties();
                    ((Hashtable<String, Object>)prop).put("MANAGED_DEVICE_ID", row.get("MANAGED_DEVICE_ID"));
                    ((Hashtable<String, Object>)prop).put("BS_STORE_ID", row.get("BS_USER_ID"));
                    startAccEnableTaskAfterDelay(4, prop);
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger(GoogleAccountChangeHandler.class.getName()).log(Level.SEVERE, "Exception when executing missed task enable: {0}", e);
        }
    }
    
    public static void startAccEnableTaskAfterDelay(final int delaySeconds, final Properties taskProps) {
        taskProps.setProperty("TASK_TYPE", "EnableAccountState");
        taskProps.setProperty("DELAY_PERIOD", delaySeconds + "");
        ((Hashtable<String, String>)taskProps).put("DELAY_PERIOD", delaySeconds + "");
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "EnableEMMPolicyForGoogleAccTask" + ((Hashtable<K, Object>)taskProps).get("MANAGED_DEVICE_ID"));
        taskInfoMap.put("schedulerTime", System.currentTimeMillis() + delaySeconds * 1000L);
        taskInfoMap.put("poolName", "mdmPool");
        ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay("com.me.mdm.server.apps.android.afw.GoogleAccountEnableTask", taskInfoMap, taskProps);
    }
}
