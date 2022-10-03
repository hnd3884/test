package com.adventnet.taskengine.internal;

import com.adventnet.db.persistence.metadata.DataDictionary;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.taskengine.util.PersistenceUtil;
import com.adventnet.taskengine.TaskContext;
import java.util.logging.Logger;
import com.adventnet.taskengine.ScheduleRetryHandler;

public class DefaultScheduleRetryHandler implements ScheduleRetryHandler
{
    private static String className;
    private static Logger logger;
    
    @Override
    public long getNextScheduleTime(final TaskContext taskContext, final long scheduleTime) {
        try {
            final Long taskID = taskContext.getTaskID();
            final Long scheduleID = taskContext.getScheduleID();
            final Row retryRow = PersistenceUtil.getScheduledTask_RetryRow(scheduleID, taskID);
            final int retryAttempt = taskContext.getRetryAttempt();
            return calculateNextRetryScheduleTime(retryRow, System.currentTimeMillis(), retryAttempt);
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    private static long getIntervalForNthAttempt(final long retry_time_period, final int retry_factor, final int nthFailureAttempt) {
        final int n = nthFailureAttempt;
        final long a = retry_time_period;
        final int d = retry_factor;
        final long interval = a + (n - 1) * d;
        return interval;
    }
    
    private static long calculateNextRetryScheduleTime(final Row retryRow, final long previousExecutionTime, final int nthFailureAttempt) {
        DefaultScheduleRetryHandler.logger.log(Level.FINE, "retryRow :: [{0}], previousExecutionTime :: [{1}], nthFailureAttempt :: [{2}]", new Object[] { retryRow, new Date(previousExecutionTime), nthFailureAttempt });
        final int rety_count = (int)retryRow.get(3);
        if (nthFailureAttempt > rety_count) {
            DefaultScheduleRetryHandler.logger.log(Level.FINE, "RETRY attempts has been exhausted hence returning -1");
            return -1L;
        }
        final long retry_time_period = (long)retryRow.get(4);
        final String retry_unit_of_time = (String)retryRow.get(5);
        final int retry_factor = (int)retryRow.get(6);
        final long interval = getIntervalForNthAttempt(retry_time_period, retry_factor, nthFailureAttempt);
        DefaultScheduleRetryHandler.logger.log(Level.FINE, "getIntervalForNthAttempt :: [{0}]", interval);
        final Calendar nextExecCal = Calendar.getInstance();
        nextExecCal.setTime(new Date(previousExecutionTime));
        if (retry_unit_of_time.equalsIgnoreCase("minutes")) {
            nextExecCal.setTime(new Date(previousExecutionTime + interval * 60L * 1000L));
        }
        else if (retry_unit_of_time.equalsIgnoreCase("hours")) {
            nextExecCal.setTime(new Date(previousExecutionTime + interval * 60L * 60L * 1000L));
        }
        else if (retry_unit_of_time.equalsIgnoreCase("seconds")) {
            nextExecCal.setTime(new Date(previousExecutionTime + interval * 1000L));
        }
        final long currTime = System.currentTimeMillis();
        if (nextExecCal.getTime().getTime() < currTime) {
            DefaultScheduleRetryHandler.logger.log(Level.WARNING, "RETRY time [{0}] is less than the current time [{1}]", new Object[] { nextExecCal.getTime(), new Date(currTime) });
            return -1L;
        }
        DefaultScheduleRetryHandler.logger.log(Level.FINE, "Returning :: [{0}]", nextExecCal.getTime());
        return nextExecCal.getTime().getTime();
    }
    
    public static void main(final String[] args) throws Exception {
        final DataDictionary dd = com.adventnet.persistence.PersistenceUtil.getDataDictionary(new File("/advent/vinod/test/m5_19/AdventNet/MickeyLite/conf/TaskEngine/data-dictionary.xml").toURL());
        MetaDataUtil.addDataDictionaryConfiguration(dd);
        final Row retryRow = new Row("ScheduledTask_Retry");
        retryRow.set(3, (Object)new Integer(5));
        retryRow.set(4, (Object)new Long(10L));
        retryRow.set(5, (Object)"Seconds");
        retryRow.set(6, (Object)new Integer(args[0]));
        final long previousExecutionTime = System.currentTimeMillis();
        System.out.println("retryRow :: " + retryRow);
        System.out.println("previousExecutionTime :: " + new Date(previousExecutionTime));
        for (int i = 1; i <= 10; ++i) {
            calculateNextRetryScheduleTime(retryRow, previousExecutionTime, i);
            System.out.println("");
        }
    }
    
    static {
        DefaultScheduleRetryHandler.className = DefaultScheduleRetryHandler.class.getName();
        DefaultScheduleRetryHandler.logger = Logger.getLogger(DefaultScheduleRetryHandler.className);
    }
}
