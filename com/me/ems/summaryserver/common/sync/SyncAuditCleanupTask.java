package com.me.ems.summaryserver.common.sync;

import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.ems.summaryserver.common.sync.utils.SyncModuleAuditDAOUtil;
import java.util.Calendar;
import com.me.ems.summaryserver.common.sync.utils.SummarySyncParamsDAOUtil;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class SyncAuditCleanupTask implements SchedulerExecutionInterface
{
    private static Logger logger;
    private static String sourceClass;
    
    @Override
    public void executeTask(final Properties props) {
        final SummarySyncParamsDAOUtil summarySyncParamsDAOUtil = new SummarySyncParamsDAOUtil();
        final String noOfDays = summarySyncParamsDAOUtil.getSummarySyncParams("SYNC_AUDIT_CLEANUP_DAYS");
        if (noOfDays != null && !"".equals(noOfDays) && !"-1".equals(noOfDays)) {
            final int cleanupDays = Integer.parseInt(noOfDays);
            final long startTime = System.currentTimeMillis();
            final Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(startTime);
            cal.add(5, -1 * cleanupDays);
            final long deletionTime = cal.getTime().getTime();
            final SyncModuleAuditDAOUtil syncModuleAuditDAOUtil = new SyncModuleAuditDAOUtil();
            final boolean isCleanupCompleted = syncModuleAuditDAOUtil.cleanUpModuleAudit(deletionTime);
            SyMLogger.info(SyncAuditCleanupTask.logger, SyncAuditCleanupTask.sourceClass, SyncAuditCleanupTask.sourceClass, "Audit Cleanup Completed : " + isCleanupCompleted + " with deletion time : " + deletionTime);
        }
        else {
            SyMLogger.info(SyncAuditCleanupTask.logger, SyncAuditCleanupTask.sourceClass, SyncAuditCleanupTask.sourceClass, "Cleanup not performed! Since SYNC_AUDIT_CLEANUP_DAYS = " + noOfDays);
        }
    }
    
    static {
        SyncAuditCleanupTask.logger = Logger.getLogger("ProbeSyncStatusLogger");
        SyncAuditCleanupTask.sourceClass = "SyncAuditCleanupTask";
    }
}
