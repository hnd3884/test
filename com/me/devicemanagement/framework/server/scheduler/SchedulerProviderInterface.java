package com.me.devicemanagement.framework.server.scheduler;

import java.util.TimeZone;
import java.sql.Timestamp;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;
import java.util.HashMap;

public interface SchedulerProviderInterface
{
    long createScheduler(final HashMap p0);
    
    HashMap getScheduledValues(final String p0);
    
    String getScheduleNameForTask(final Long p0);
    
    Long getSchedulerClassIDForTask(final Long p0);
    
    Long getSchedulerClassID(final String p0);
    
    boolean isSchedulerDisabled(final String p0);
    
    @Deprecated
    void executeAsynchronously(final String p0, final HashMap p1, final Properties p2);
    
    void executeAsynchronously(final String p0, final HashMap p1, final Properties p2, final String p3);
    
    Long getCustomerID(final String p0);
    
    void executeAsynchronousWithDelay(final String p0, final HashMap p1, final Properties p2);
    
    void setScheduledValuesInJson(final String p0, final HttpServletRequest p1, final Boolean p2, final Boolean p3, final Boolean p4);
    
    void setScheduledValuesInJson(final String p0, final HttpServletRequest p1, final Boolean p2, final Boolean p3, final Boolean p4, final Boolean p5);
    
    @Deprecated
    Long createScheduleFromJson(final JSONObject p0, final String p1, final String p2, final String p3, final String p4, final String p5, final String p6, final String p7, final String p8, final String p9, final Long p10, final Long p11);
    
    Long createScheduleFromJson(final JSONObject p0, final String p1, final String p2, final String p3, final String p4, final String p5, final String p6, final String p7, final String p8, final Long p9, final Long p10, final Boolean p11);
    
    @Deprecated
    Long createScheduleFromJson(final JSONObject p0, final String p1, final String p2, final String p3, final String p4, final String p5, final String p6, final String p7, final String p8, final String p9, final Long p10, final Long p11, final Boolean p12);
    
    void setSchedulerState(final boolean p0, final String p1);
    
    void setSchedulerState(final boolean p0, final Long p1);
    
    void deleteScheduleSpecificInput(final Long p0, final String p1);
    
    void addOrUpdateScheduleSpecificInput(final Long p0, final String p1, final String p2);
    
    String getScheduleSpecificInputValue(final Long p0, final String p1);
    
    Long getSchedulerClassIDFromInput(final String p0, final String p1);
    
    Long getNextExecutionTimeForTask(final Long p0);
    
    Long getNextExecutionTimeForSchedule(final String p0);
    
    boolean isScheduleCreated(final String p0);
    
    void removeScheduler(final String p0);
    
    void removeScheduler(final Long p0);
    
    Long getTaskIDForSchedule(final String p0);
    
    Long getTaskIDForSchedule(final Long p0);
    
    Long calculateNextScheduleTime(final String p0, final Long p1);
    
    List getSchedulesForCriteria(final Criteria p0);
    
    Long getPeriodicEndTime(final Long p0);
    
    Long getPeriodicTimePeriod(final Long p0);
    
    boolean getSchedulerState(final Long p0);
    
    void updatePeriodicSchedule(final HashMap p0);
    
    void updateExecutionTime(final String p0, final Timestamp p1);
    
    boolean updateTimezone(final String p0, final TimeZone p1);
    
    TimeZone getTimezone(final String p0);
    
    String getRepeatFrequency(final String p0);
    
    Properties getScheduleSpecificInputs(final Long p0);
    
    List getScheduleNamesForWorkflow(final String p0);
    
    HashMap fetchNextExecTimeForSchedules(final List p0);
    
    Long getOrAddUserIDInTask(final Long p0);
    
    Boolean isSchedulesWaiting(final String p0);
    
    Boolean isAnyActiveThreadInPool(final String p0) throws Exception;
    
    JSONObject getScheduleJsonValues(final String p0, final Boolean p1, final Boolean p2, final Boolean p3, final Boolean p4, final Long p5);
}
