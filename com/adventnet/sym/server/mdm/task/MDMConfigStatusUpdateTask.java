package com.adventnet.sym.server.mdm.task;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.List;
import com.me.mdm.api.command.schedule.GroupActionScheduleUtils;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.status.GroupCollectionStatusSummary;
import com.me.mdm.server.status.ManagedUserCollectionStatusSummary;
import com.me.mdm.server.customgroup.MDMCustomGroupUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class MDMConfigStatusUpdateTask implements SchedulerExecutionInterface
{
    private static final Logger LOGGER;
    private long lastCollnStatusComputationTime;
    private static final String MDM_COLLN_STATUS_UPDATE = "MDM_COLLN_STATUS_UPDATE";
    private static final long LATENCY_VAL = 30000L;
    
    public MDMConfigStatusUpdateTask() {
        this.lastCollnStatusComputationTime = -1L;
    }
    
    public void executeTask(final Properties props) {
        MDMConfigStatusUpdateTask.LOGGER.log(Level.FINE, "MDMConfigStatusUpdateTask Status Update Task and properties {0}", props);
        boolean runScheduler = true;
        try {
            CustomerInfoUtil.getInstance();
            if (CustomerInfoUtil.isSAS()) {
                final Boolean isUserPresent = (Boolean)ApiFactoryProvider.getCacheAccessAPI().getCache("USER_PRESENT", 2);
                if (isUserPresent == null) {
                    runScheduler = false;
                }
            }
        }
        catch (final Exception e) {
            MDMConfigStatusUpdateTask.LOGGER.log(Level.SEVERE, "Exception occurred while checking user present", e);
        }
        if (runScheduler) {
            MDMConfigStatusUpdateTask.LOGGER.log(Level.INFO, "Customer Login available... So Running Scheduler");
            final long lastUpdateTime = this.getLastCollnStatusComputationTime();
            try {
                final Long startTime = System.currentTimeMillis();
                final List collectionIDList = MDMCustomGroupUtil.getInstance().getModifiedCollectionIDList(lastUpdateTime);
                ManagedUserCollectionStatusSummary.getInstance().computeAndUpdateManagedUserCollectionStatusSummary(collectionIDList);
                GroupCollectionStatusSummary.getInstance().computeAndUpdateGroupCollectionStatusSummary(collectionIDList);
                final String isStatusUpdateEnabled = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("enableStatusUpdate");
                Boolean run = false;
                if (isStatusUpdateEnabled == null || isStatusUpdateEnabled.equals("true")) {
                    run = true;
                }
                if (run) {
                    final long updateExpireDeviceTime = System.currentTimeMillis();
                    GroupActionScheduleUtils.updateExpiredDeviceStatus();
                    MDMConfigStatusUpdateTask.LOGGER.log(Level.INFO, "Time taken for removing expired devicecommands is {0}", MDMUtil.getCurrentTimeInMillis() - updateExpireDeviceTime);
                }
                final List actionIDList = MDMCustomGroupUtil.getInstance().getModifiedGroupActionIDList(lastUpdateTime);
                if (actionIDList.size() > 0) {
                    final long actionUpdateStarTime = MDMUtil.getCurrentTimeInMillis();
                    GroupCollectionStatusSummary.getInstance().computeAndUpdateManagedUserActionStatusSummary(actionIDList);
                    MDMConfigStatusUpdateTask.LOGGER.log(Level.INFO, "Time taken for Action Status Count Updation {0}", MDMUtil.getCurrentTimeInMillis() - actionUpdateStarTime);
                }
                this.updateMDMCollnStatusComputationTime(startTime);
            }
            catch (final Exception ex) {
                Logger.getLogger(MDMConfigStatusUpdateTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else {
            MDMConfigStatusUpdateTask.LOGGER.log(Level.INFO, "Customer Login not available.. So Scheduler Skipped...");
        }
    }
    
    private void cleanupCollnToResourcesEntries() throws DataAccessException {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("CollnToResources");
        deleteQuery.addJoin(new Join("CollnToResources", "RecentProfileForGroup", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "GROUP_ID" }, 1));
        deleteQuery.addJoin(new Join("CollnToResources", "RecentProfileForMDMResource", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 1));
        deleteQuery.addJoin(new Join("CollnToResources", "RecentProfileForResource", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 1));
        final Criteria groupCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)null, 0);
        final Criteria userCriteria = new Criteria(Column.getColumn("RecentProfileForMDMResource", "RESOURCE_ID"), (Object)null, 0);
        final Criteria deviceCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)null, 0);
        deleteQuery.setCriteria(groupCriteria.and(userCriteria).and(deviceCriteria));
        DataAccess.delete(deleteQuery);
    }
    
    private long getLastCollnStatusComputationTime() {
        if (this.lastCollnStatusComputationTime <= 0L) {
            this.lastCollnStatusComputationTime = SyMUtil.getLastUpdatedTime("MDM_COLLN_STATUS_UPDATE");
            if (this.lastCollnStatusComputationTime <= 0L) {
                MDMConfigStatusUpdateTask.LOGGER.log(Level.INFO, "No MDM_COLLN_STATUS_UPDATE in DB , will add new entry now");
                this.lastCollnStatusComputationTime = SyMUtil.getLastUpdatedTime("STATUS_UPDATE");
                this.updateMDMCollnStatusComputationTime(System.currentTimeMillis());
            }
        }
        return this.lastCollnStatusComputationTime -= 30000L;
    }
    
    private Long updateMDMCollnStatusComputationTime(final Long timetoUpdate) {
        SyMUtil.updateLastUpdatedTime((long)timetoUpdate, "MDM_COLLN_STATUS_UPDATE");
        return timetoUpdate;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMResourceSummaryLog");
    }
}
