package com.me.ems.summaryserver.probe.sync;

import java.util.Iterator;
import java.util.List;
import com.me.ems.summaryserver.common.probeadministration.ProbeDetailsAPI;
import com.me.ems.summaryserver.probe.sync.utils.SyncUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.ems.summaryserver.common.sync.utils.SyncModuleMetaDAOUtil;
import com.me.ems.summaryserver.common.sync.utils.SummarySyncParamsDAOUtil;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class SyncMaintenanceTask implements SchedulerExecutionInterface
{
    private Logger logger;
    private String sourceClass;
    
    public SyncMaintenanceTask() {
        this.logger = Logger.getLogger("ProbeSyncStatusLogger");
        this.sourceClass = "SyncMaintenanceTask";
    }
    
    @Override
    public void executeTask(final Properties props) {
        try {
            SyMLogger.info(this.logger, this.sourceClass, this.sourceClass, "####### SYNC MAINTENANCE TASK STARTS #######");
            final ProbeDetailsAPI probeDetailsAPI = ProbeMgmtFactoryProvider.getProbeDetailsAPI();
            final int summaryServerStatus = probeDetailsAPI.getSummaryServerLiveStatus();
            final SummarySyncParamsDAOUtil summarySyncParamsDAOUtil = new SummarySyncParamsDAOUtil();
            summarySyncParamsDAOUtil.updateSummarySyncParams("ENABLE_SPLIT_SYNC", "true");
            final SyncModuleMetaDAOUtil syncModuleMetaDAOUtil = new SyncModuleMetaDAOUtil();
            final List<Long> moduleIDs = syncModuleMetaDAOUtil.getAllModuleIDs();
            for (final long moduleID : moduleIDs) {
                final String syncState = ApiFactoryProvider.getRedisHashMap().get(moduleID + "_SYNC", 2);
                SyMLogger.info(this.logger, this.sourceClass, this.sourceClass, "Sync State for " + moduleID + " : " + syncState);
                if (syncState != null) {
                    final String s = syncState;
                    switch (s) {
                        case "DISABLED_ON_SS_DOWN": {
                            if (summaryServerStatus == 1) {
                                SyncUtil.getInstance().checkAndEnableSyncScheduler(moduleID, "ENABLE_ON_SS_LIVE");
                                continue;
                            }
                            continue;
                        }
                        default: {
                            SyncUtil.getInstance().checkAndEnableSyncScheduler(moduleID, "ENABLE_ON_MAINTENANCE_RETRY");
                            continue;
                        }
                    }
                }
                else {
                    SyncUtil.getInstance().checkAndEnableSyncScheduler(moduleID, "ENABLE_ON_MAINTENANCE_RETRY");
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, this.sourceClass, "Exception while enabling sync scheduler", e);
        }
        SyMLogger.info(this.logger, this.sourceClass, this.sourceClass, "####### SYNC MAINTENANCE TASK ENDS #######");
    }
}
