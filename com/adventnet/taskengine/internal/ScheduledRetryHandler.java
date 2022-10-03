package com.adventnet.taskengine.internal;

import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.taskengine.util.ScheduleUtil;
import java.util.Date;
import java.util.logging.Level;
import com.adventnet.taskengine.util.PersistenceUtil;
import com.adventnet.taskengine.TaskContext;
import java.util.logging.Logger;
import com.adventnet.taskengine.ScheduleRetryHandler;

public class ScheduledRetryHandler implements ScheduleRetryHandler
{
    private static String className;
    private static Logger logger;
    
    @Override
    public long getNextScheduleTime(final TaskContext taskContext, final long scheduleTime) {
        final Row schRow = taskContext.getScheduledTaskRow();
        final Long retrySchedule = (Long)schRow.get("RETRY_SCHEDULE_ID");
        DataObject retryScheduleDob = null;
        try {
            retryScheduleDob = PersistenceUtil.getSchedule(retrySchedule, true);
        }
        catch (final Exception e) {
            e.printStackTrace();
            ScheduledRetryHandler.logger.log(Level.SEVERE, "Exception occurred while fetching the retryScheduleDO, hence returning -1");
            return -1L;
        }
        ScheduledRetryHandler.logger.log(Level.FINE, "scheduleTime :: [{0}]", new Date(scheduleTime));
        final long returnTime = ScheduleUtil.calculateNextScheduleTime(retryScheduleDob, scheduleTime, true, taskContext.getScheduleType());
        ScheduledRetryHandler.logger.log(Level.FINE, "returnTime :: [{0}]", new Date(returnTime));
        return returnTime;
    }
    
    static {
        ScheduledRetryHandler.className = ScheduledRetryHandler.class.getName();
        ScheduledRetryHandler.logger = Logger.getLogger(ScheduledRetryHandler.className);
    }
}
