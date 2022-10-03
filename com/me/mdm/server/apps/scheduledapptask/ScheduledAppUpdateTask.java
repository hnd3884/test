package com.me.mdm.server.apps.scheduledapptask;

import java.util.Hashtable;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.me.mdm.api.command.schedule.ScheduleRepositoryHandler;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ScheduledAppUpdateTask implements SchedulerExecutionInterface
{
    private static final Logger LOGGER;
    
    public void executeTask(final Properties taskProps) {
        final String scheduleName = ((Hashtable<K, String>)taskProps).get("scheduleName");
        try {
            final Long scheduleId = ScheduleRepositoryHandler.getInstance().getScheduleID(scheduleName);
            ScheduledAppUpdateTask.LOGGER.log(Level.INFO, "ScheduleAppUpdate Task initiated for scheduleName {0} scheduleId {1}", new Object[] { scheduleName, scheduleId });
            final DataObject dataObject = this.getScheduledAppUpdateObject(scheduleId);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> iterator = dataObject.getRows("AutoAppUpdateConfigToCollection");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long autoUpdateConfId = (Long)row.get("APP_UPDATE_CONF_ID");
                    final Long collectionId = (Long)row.get("COLLECTION_ID");
                    ScheduledAppUpdateTask.LOGGER.log(Level.INFO, "Pushing app updates based on policy {0} collection {1}", new Object[] { autoUpdateConfId, collectionId });
                    final Row configDetailsRow = dataObject.getRow("AutoAppUpdateConfigDetails", new Criteria(new Column("AutoAppUpdateConfigToCollection", "APP_UPDATE_CONF_ID"), (Object)autoUpdateConfId, 0));
                    final Long customerId = (Long)configDetailsRow.get("CUSTOMER_ID");
                    MDMAppMgmtHandler.getInstance().executeScheduledAppUpdate(autoUpdateConfId, collectionId, customerId);
                }
            }
            ScheduledAppUpdateTask.LOGGER.log(Level.INFO, "**********ScheduledAppUpdateTask current time {0} next execution time {1}", new Object[] { System.currentTimeMillis(), ApiFactoryProvider.getSchedulerAPI().getNextExecutionTimeForSchedule(scheduleName) });
            final HashMap taskInfoMap = new HashMap();
            taskInfoMap.put("taskName", "SchedulerTimeUpdate");
            taskInfoMap.put("poolName", "mdmPool");
            taskInfoMap.put("schedulerTime", System.currentTimeMillis() + 180000L);
            final Properties taskProperties = new Properties();
            ((Hashtable<String, String>)taskProperties).put("appScheduleName", scheduleName);
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay("com.me.mdm.server.apps.scheduledapptask.SchedulerNextExecutionTimeUpdateHandler", taskInfoMap, taskProperties);
        }
        catch (final Exception e) {
            ScheduledAppUpdateTask.LOGGER.log(Level.WARNING, "Cannot fetch app update schedule details ", e);
        }
    }
    
    private DataObject getScheduledAppUpdateObject(final Long scheduleId) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppUpdatePolicyCollnToScheduleRepo"));
        selectQuery.addJoin(new Join("AppUpdatePolicyCollnToScheduleRepo", "AutoAppUpdateConfigToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigToCollection", "RecentPubProfileToColln", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigToCollection", "AutoAppUpdateConfigDetails", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        selectQuery.addSelectColumn(new Column("AutoAppUpdateConfigToCollection", "*"));
        selectQuery.addSelectColumn(new Column("AutoAppUpdateConfigDetails", "APP_UPDATE_CONF_ID"));
        selectQuery.addSelectColumn(new Column("AutoAppUpdateConfigDetails", "CUSTOMER_ID"));
        selectQuery.setCriteria(new Criteria(new Column("AppUpdatePolicyCollnToScheduleRepo", "SCHEDULE_ID"), (Object)scheduleId, 0));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            return dataObject;
        }
        catch (final DataAccessException e) {
            ScheduledAppUpdateTask.LOGGER.log(Level.WARNING, "Cannot fetch App update policy for provided scheduled id: {0} due to {1}", new Object[] { scheduleId, e.getMessage() });
            return null;
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMAppMgmtLogger");
    }
}
