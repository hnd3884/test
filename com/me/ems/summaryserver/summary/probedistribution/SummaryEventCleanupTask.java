package com.me.ems.summaryserver.summary.probedistribution;

import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.deletionfw.DeletionFramework;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Calendar;
import com.me.ems.summaryserver.common.sync.utils.SummarySyncParamsDAOUtil;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class SummaryEventCleanupTask implements SchedulerExecutionInterface
{
    private static Logger logger;
    private static String sourceClass;
    
    @Override
    public void executeTask(final Properties props) {
        try {
            final SummarySyncParamsDAOUtil summarySyncParamsDAOUtil = new SummarySyncParamsDAOUtil();
            final String noOfDays = summarySyncParamsDAOUtil.getSummarySyncParams("PUSH_EVENT_CLEANUP_DAYS");
            if (noOfDays != null && !"".equals(noOfDays) && !"-1".equals(noOfDays)) {
                final int cleanupDays = Integer.parseInt(noOfDays);
                final long startTime = System.currentTimeMillis();
                final Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(startTime);
                cal.add(5, -1 * cleanupDays);
                final long deletionTime = cal.getTime().getTime();
                final Column eventTimeCol = Column.getColumn("PushToProbesEventLog", "PUSH_EVENT_TIME");
                final Criteria criteria = new Criteria(eventTimeCol, (Object)deletionTime, 7);
                final int chunk = Integer.parseInt(summarySyncParamsDAOUtil.getSummarySyncParams("AUDIT_DELETION_CHUNK"));
                DeletionFramework.delete("PushToProbesEventLog", criteria, chunk);
                SyMLogger.info(SummaryEventCleanupTask.logger, SummaryEventCleanupTask.sourceClass, SummaryEventCleanupTask.sourceClass, "PushEventLogs Cleanup Completed! with deletion time : " + deletionTime);
            }
            else {
                SyMLogger.info(SummaryEventCleanupTask.logger, SummaryEventCleanupTask.sourceClass, SummaryEventCleanupTask.sourceClass, "Cleanup not performed! Since PUSH_EVENT_CLEANUP_DAYS = " + noOfDays);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(SummaryEventCleanupTask.logger, SummaryEventCleanupTask.sourceClass, SummaryEventCleanupTask.sourceClass, "Caught exception while SummaryEventCleanupTask :", ex);
        }
    }
    
    static {
        SummaryEventCleanupTask.logger = Logger.getLogger("SummarySyncLogger");
        SummaryEventCleanupTask.sourceClass = "SummaryEventCleanupTask";
    }
}
