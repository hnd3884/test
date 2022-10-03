package com.adventnet.taskengine;

import com.adventnet.persistence.Row;
import java.util.Properties;
import com.adventnet.ds.query.UpdateQuery;
import java.util.List;
import com.adventnet.persistence.DataObject;

public interface Scheduler
{
    public static final int SCHEDULE_TIME = 0;
    public static final int EXECUTION_START_TIME = 1;
    public static final int EXECUTION_FINISH_TIME = 2;
    public static final int ENABLE = 3;
    public static final int DISABLE = 4;
    public static final int NORMAL_SCHEDULED_TASK = 5;
    public static final int PREPONED_SCHEDULED_TASK = 6;
    
    DataObject scheduleTask(final String p0, final String p1, final DataObject p2) throws Exception;
    
    DataObject scheduleTask(final String p0, final String p1, final DataObject p2, final int p3, final int p4) throws Exception;
    
    DataObject scheduleTask(final String p0, final String p1, final DataObject p2, final int p3, final int p4, final int p5) throws Exception;
    
    DataObject scheduleTask(final String p0, final String p1, final DataObject p2, final int p3, final int p4, final int p5, final String p6) throws Exception;
    
    void unscheduleTask(final String p0, final String p1) throws Exception;
    
    void unscheduleTask(final long p0, final long p1) throws Exception;
    
    void setScheduledTaskSkipStatus(final String p0, final String p1, final boolean p2) throws Exception;
    
    void setScheduledTaskSkipStatus(final long p0, final long p1, final boolean p2) throws Exception;
    
    boolean getScheduledTaskSkipStatus(final String p0, final String p1) throws Exception;
    
    int getScheduledTaskStatus(final String p0, final String p1, final String p2) throws Exception;
    
    void setScheduledTaskAdminStatus(final String p0, final String p1, final int p2) throws Exception;
    
    void setScheduledTaskAdminStatus(final long p0, final long p1, final int p2) throws Exception;
    
    int getScheduledTaskAdminStatus(final String p0, final String p1) throws Exception;
    
    int getTaskInputStatus(final long p0, final String p1) throws Exception;
    
    void setTaskInputAdminStatus(final long p0, final int p1) throws Exception;
    
    int getTaskInputAdminStatus(final long p0) throws Exception;
    
    void unscheduleTaskInput(final long p0) throws Exception;
    
    void unscheduleTaskInput(final DataObject p0) throws Exception;
    
    void updateTaskInput(final DataObject p0) throws Exception;
    
    void setScheduledTaskAuditStatus(final String p0, final String p1, final boolean p2) throws Exception;
    
    void setScheduledTaskAuditStatus(final long p0, final long p1, final boolean p2) throws Exception;
    
    boolean getScheduledTaskAuditStatus(final String p0, final String p1) throws Exception;
    
    void setRemoveOnExpiryStatus(final String p0, final String p1, final boolean p2) throws Exception;
    
    void setRemoveOnExpiryStatus(final long p0, final long p1, final boolean p2) throws Exception;
    
    boolean getRemoveOnExpiryStatus(final String p0, final String p1) throws Exception;
    
    long createSchedule(final DataObject p0) throws Exception;
    
    void removeSchedule(final long p0) throws Exception;
    
    void updateSchedule(final DataObject p0) throws Exception;
    
    void stopScheduler() throws Exception;
    
    List getAllTaskInput(final String p0, final String p1) throws Exception;
    
    List getAllTaskInput(final Long p0, final Long p1) throws Exception;
    
    DataObject getAllSchedules() throws Exception;
    
    TaskContext executeTask(final TaskContext p0) throws Throwable;
    
    void reschedule(final TaskContext p0, final String p1) throws Exception;
    
    void audit(final TaskContext p0, final int p1, final String p2) throws Exception;
    
    void updateNextScheduleTime(final UpdateQuery p0) throws Exception;
    
    void setBatchProperties(final long p0, final long p1) throws Exception;
    
    void executeAsynchronously(final String p0, final long p1, final Properties p2, final Row p3, final int p4) throws Exception;
    
    void executeAsynchronously(final String p0, final long p1, final Properties p2, final Row p3, final int p4, final String p5) throws Exception;
    
    void executeAsynchronously(final String p0, final long p1, final Properties p2, final Row p3, final int p4, final Object p5) throws Exception;
    
    void executeAsynchronously(final String p0, final long p1, final Properties p2, final Row p3, final int p4, final Object p5, final String p6) throws Exception;
    
    void preponeTask(final Long p0) throws Exception;
}
