package com.me.devicemanagement.onpremise.server.scheduler;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.task.DeviceMgmtTaskUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Calendar;
import java.util.TimeZone;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.me.devicemanagement.framework.webclient.audit.EventViewerTRAction;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.sql.Timestamp;
import com.me.devicemanagement.framework.server.util.Utils;
import com.adventnet.taskengine.util.CalendarRowConfig;
import com.adventnet.persistence.DataAccessException;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.logging.Logger;

class DMSchedulerUtil
{
    private Logger logger;
    private static DMSchedulerUtil dmschedulerutil;
    public static final String WORKFLOW_NAME = "OnPremiseTask";
    public static final String TASKENGINE_DEFAULT_CLASSNAME = "com.me.devicemanagement.onpremise.server.scheduler.SchedulerExecutionTask";
    
    DMSchedulerUtil() {
        this.logger = Logger.getLogger(DMSchedulerUtil.class.getName());
    }
    
    public static DMSchedulerUtil getInstance() {
        if (DMSchedulerUtil.dmschedulerutil == null) {
            DMSchedulerUtil.dmschedulerutil = new DMSchedulerUtil();
        }
        return DMSchedulerUtil.dmschedulerutil;
    }
    
    public Long createSchedulerTask(final String taskName, final String description, final String workflowName, final Boolean skipMissedSche, final Boolean schedStatus) {
        return this.createSchedulerTask(taskName, description, workflowName, skipMissedSche, schedStatus, -1);
    }
    
    public Long createSchedulerTask(final String taskName, final String description, final String workflowName, final Boolean skipMissedSche, final Boolean schedStatus, final int transactionTime) {
        Long scheduleID = 0L;
        DataObject schDo = null;
        try {
            schDo = SyMUtil.getPersistence().constructDataObject();
            Row schRow = new Row("Schedule");
            schRow.set("SCHEDULE_NAME", (Object)taskName);
            if (description != null) {
                schRow.set("DESCRIPTION", (Object)description);
            }
            schDo.addRow(schRow);
            final Row taskRow = new Row("Scheduled_Task");
            schRow = schDo.getRow("Schedule");
            taskRow.set("ADMIN_STATUS", (Object)3);
            if (!schedStatus) {
                taskRow.set("ADMIN_STATUS", (Object)4);
            }
            taskRow.set("AUDIT_FLAG", (Object)true);
            taskRow.set("SCHEDULE_ID", schRow.get("SCHEDULE_ID"));
            if (skipMissedSche) {
                taskRow.set("SKIP_MISSED_SCHEDULE", (Object)Boolean.TRUE);
            }
            Long taskID = this.getTaskIDByTaskName(workflowName);
            if (taskID == null) {
                taskID = this.getTaskIDByTaskName("OnPremiseTask");
            }
            taskRow.set("TASK_ID", (Object)taskID);
            taskRow.set("TRANSACTION_TIME", (Object)transactionTime);
            schDo.addRow(taskRow);
            SyMUtil.getPersistence().add(schDo);
            scheduleID = (Long)schRow.get("SCHEDULE_ID");
            this.logger.log(Level.INFO, "New Scheduler created with ID :  {0} \t Scheduler Name :{1}, Scheduler Description :{2},Task ID :{3}", new Object[] { schRow.get("SCHEDULE_ID"), schRow.get("SCHEDULE_NAME"), schRow.get("DESCRIPTION"), taskRow.get("TASK_ID") });
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return scheduleID;
    }
    
    public Long createScheduleTaskDetails(final Long scheduleID, final boolean isScheduled, final HashMap mapValues) throws DataAccessException {
        final DataObject taskDetailsDO = SyMUtil.getPersistence().constructDataObject();
        final Row taskDetailsRow = new Row("TaskDetails");
        final String operationType = mapValues.get("operationType");
        if (operationType != null) {
            taskDetailsRow.set("TYPE", (Object)new Integer(operationType));
        }
        final String owner = mapValues.get("owner");
        if (owner != null) {
            taskDetailsRow.set("OWNER", (Object)owner);
        }
        final String taskName = mapValues.get("taskName");
        taskDetailsRow.set("TASKNAME", (Object)taskName);
        taskDetailsRow.set("REMARKS", (Object)"Scheduled Task");
        final Long time = new Long(System.currentTimeMillis());
        taskDetailsRow.set("CREATIONTIME", (Object)time);
        taskDetailsRow.set("REMARKS", (Object)"Scheduled Task");
        if (mapValues.get("description") != null) {
            taskDetailsRow.set("DESCRIPTION", mapValues.get("description"));
        }
        taskDetailsDO.addRow(taskDetailsRow);
        if (isScheduled) {
            final String repeatFreq = mapValues.get("schType");
            final String schedulerName = mapValues.get("schedulerName");
            final Row pmScheduledTaskDetailsRow = new Row("ScheduledTaskDetails");
            pmScheduledTaskDetailsRow.set("TASK_ID", taskDetailsRow.get("TASK_ID"));
            pmScheduledTaskDetailsRow.set("SCHEDULE_ID", (Object)scheduleID);
            pmScheduledTaskDetailsRow.set("SCHEDULER_CLASS_ID", (Object)this.getSchedulerClassID(schedulerName));
            pmScheduledTaskDetailsRow.set("REPEAT_FREQUENCY", (Object)repeatFreq);
            taskDetailsDO.addRow(pmScheduledTaskDetailsRow);
            taskDetailsRow.set("STATUS", (Object)"SCHEDULED");
        }
        else {
            taskDetailsRow.set("STARTTIME", (Object)time);
            taskDetailsRow.set("STATUS", (Object)"RUNNING");
        }
        if (mapValues.get("email") != null) {
            taskDetailsRow.set("EMAIL", (Object)mapValues.get("email"));
        }
        if (mapValues.get("COUNTER") != null) {
            taskDetailsRow.set("COUNTER", (Object)mapValues.get("COUNTER"));
        }
        final String schedulerName2 = mapValues.get("schedulerName");
        final Long startTime = this.executionTime(schedulerName2);
        if (startTime != 0L) {
            taskDetailsRow.set("STARTTIME", (Object)startTime);
        }
        if (mapValues.get("customerID") != null) {
            final Row taskCustRow = new Row("TaskToCustomerRel");
            taskCustRow.set("TASK_ID", taskDetailsRow.get("TASK_ID"));
            taskCustRow.set("CUSTOMER_ID", (Object)mapValues.get("customerID"));
            taskDetailsDO.addRow(taskCustRow);
        }
        if (mapValues.get("userID") != null) {
            final Row taskCustRow = new Row("TaskToUserRel");
            taskCustRow.set("TASK_ID", taskDetailsRow.get("TASK_ID"));
            taskCustRow.set("USER_ID", (Object)mapValues.get("userID"));
            taskCustRow.set("LAST_MODIFIED_BY", (Object)mapValues.get("userID"));
            taskDetailsDO.addRow(taskCustRow);
        }
        try {
            SyMUtil.getPersistence().add(taskDetailsDO);
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception while creating ScheduleTaskDetails" + e);
        }
        return (Long)taskDetailsDO.getFirstRow("TaskDetails").get("TASK_ID");
    }
    
    public CalendarRowConfig createOneTimeScheduler(final int hours, final int minutes, final int seconds, final String date, final String timezone) {
        final CalendarRowConfig calRowConf = new CalendarRowConfig();
        calRowConf.setScheduleType(CalendarRowConfig.ScheduleType.NONE);
        if (date != null) {
            final int day = Integer.parseInt(Utils.getDayOfDate(date));
            final int month = Integer.parseInt(Utils.getMonthOfDate(date));
            final int year = Integer.parseInt(Utils.getYearOfDate(date));
            calRowConf.setStartDate(day, month, year);
        }
        if (timezone != null) {
            calRowConf.setTimeZone(timezone);
        }
        calRowConf.setExecutionTime(hours, minutes, seconds);
        return calRowConf;
    }
    
    public CalendarRowConfig createDailyCalendarConfig(final int hours, final int minutes, final int seconds, final String date, final int skipFrequency, final String timezone) {
        final CalendarRowConfig calRowConf = new CalendarRowConfig();
        calRowConf.setScheduleType(CalendarRowConfig.ScheduleType.DAILY);
        if (date != null) {
            final int day = Integer.parseInt(Utils.getDayOfDate(date));
            final int month = Integer.parseInt(Utils.getMonthOfDate(date));
            final int year = Integer.parseInt(Utils.getYearOfDate(date));
            calRowConf.setStartDate(day, month, year);
        }
        if (skipFrequency > 0) {
            calRowConf.setSkipFrequency(skipFrequency);
        }
        calRowConf.setExecutionTime(hours, minutes, seconds);
        if (timezone != null) {
            calRowConf.setTimeZone(timezone);
        }
        return calRowConf;
    }
    
    public CalendarRowConfig createWeeklyCalendarConfig(final int hours, final int minutes, final int seconds, final String date, final String daysOfWeek, final String timeZone) {
        final CalendarRowConfig calRowConf = new CalendarRowConfig();
        calRowConf.setScheduleType(CalendarRowConfig.ScheduleType.WEEKLY);
        if (date != null) {
            final int day = Integer.parseInt(Utils.getDayOfDate(date));
            final int month = Integer.parseInt(Utils.getMonthOfDate(date));
            final int year = Integer.parseInt(Utils.getYearOfDate(date));
            calRowConf.setStartDate(day, month, year);
        }
        if (timeZone != null) {
            calRowConf.setTimeZone(timeZone);
        }
        calRowConf.setDaysOfWeek(this.getIntArrayFromString(daysOfWeek));
        calRowConf.setExecutionTime(hours, minutes, seconds);
        return calRowConf;
    }
    
    private CalendarRowConfig createMonthlyCalendarRepetition(final int hours, final int minutes, final int seconds, final int[] weeksOfMonth, final int[] daysOfWeeks, final Object object) {
        final CalendarRowConfig calRowConf = new CalendarRowConfig();
        calRowConf.setScheduleType(CalendarRowConfig.ScheduleType.MONTHLY);
        calRowConf.setExecutionTime(hours, minutes, seconds);
        calRowConf.setWeeksOfMonth(weeksOfMonth);
        calRowConf.setDaysOfWeek(daysOfWeeks);
        return calRowConf;
    }
    
    public CalendarRowConfig createYearlyCalendarRepetition(final String monthlyPerform, final int hours, final int minutes, final int seconds, final String months, final String daysOfWeek, final String numOfWeek, final String daysOfMonth, final String timeZone) {
        final CalendarRowConfig calRowConf = new CalendarRowConfig();
        calRowConf.setScheduleType(CalendarRowConfig.ScheduleType.YEARLY);
        calRowConf.setExecutionTime(hours, minutes, seconds);
        calRowConf.setMonths(this.getIntArrayFromString(months));
        if (monthlyPerform.equals("WeekDay")) {
            calRowConf.setWeeksOfMonth(this.getIntArrayFromString(numOfWeek));
            calRowConf.setDaysOfWeek(this.getIntArrayFromString(daysOfWeek));
        }
        else if (monthlyPerform.equals("Day")) {
            calRowConf.setDates(this.getIntArrayFromString(daysOfMonth));
        }
        if (timeZone != null) {
            calRowConf.setTimeZone(timeZone);
        }
        return calRowConf;
    }
    
    public DataObject createCalendarScheduler(final DataObject schedulerDO, final Long scheduleID, final CalendarRowConfig calConf) {
        try {
            final Row calendarRow = calConf.toCalendarRow();
            calendarRow.set("SCHEDULE_ID", (Object)scheduleID);
            schedulerDO.addRow(calendarRow);
            this.logger.log(Level.INFO, "Calendar scheduler created with Schedule ID: " + calendarRow.get("SCHEDULE_ID") + "\t" + calendarRow.get("TIME_OF_DAY"));
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return schedulerDO;
    }
    
    public DataObject createPeriodicScheduler(final DataObject schedulerDO, final Long scheduleID, final String timePeriod, final String unitOfTime, final Timestamp endDate, final Timestamp startDate) {
        try {
            final Row PeriodicRow = new Row("Periodic");
            PeriodicRow.set("SCHEDULE_ID", (Object)scheduleID);
            PeriodicRow.set("TIME_PERIOD", (Object)Long.parseLong(timePeriod));
            PeriodicRow.set("UNIT_OF_TIME", (Object)unitOfTime);
            if (startDate != null) {
                PeriodicRow.set("START_DATE", (Object)startDate);
            }
            if (endDate != null) {
                PeriodicRow.set("END_DATE", (Object)endDate);
            }
            schedulerDO.addRow(PeriodicRow);
            this.logger.log(Level.INFO, "Periodic scheduler created with Schedule ID: " + PeriodicRow.get("SCHEDULE_ID") + "\t with" + PeriodicRow.get("TIME_PERIOD") + PeriodicRow.get("UNIT_OF_TIME"));
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return schedulerDO;
    }
    
    private Long getTaskIDByTaskName(final String taskName) {
        Long taskID = null;
        try {
            final Criteria crit = new Criteria(Column.getColumn("TaskEngine_Task", "TASK_NAME"), (Object)taskName, 0, false);
            final DataObject instanceDO = SyMUtil.getPersistence().get("TaskEngine_Task", crit);
            if (!instanceDO.isEmpty()) {
                taskID = (Long)instanceDO.getFirstValue("TaskEngine_Task", "TASK_ID");
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return taskID;
    }
    
    public HashMap getScheduledValues(final String schedulerName) {
        final HashMap hash = new HashMap();
        try {
            Long schedulerID = null;
            final Column col = Column.getColumn("Schedule", "SCHEDULE_NAME");
            final Criteria criteria = new Criteria(col, (Object)schedulerName, 0);
            final DataObject scheduleDO = SyMUtil.getPersistence().get("Schedule", criteria);
            if (scheduleDO.isEmpty()) {
                this.logger.log(Level.INFO, "ScheduleID for Scheduler Name : " + schedulerName + " is not available");
            }
            else {
                schedulerID = (Long)scheduleDO.getFirstValue("Schedule", "SCHEDULE_ID");
            }
            if (schedulerID != null) {
                final Row calendarRow = DBUtil.getRowFromDB("Calendar", "SCHEDULE_ID", (Object)schedulerID);
                final CalendarRowConfig calRow = new CalendarRowConfig(calendarRow);
                final CalendarRowConfig.ScheduleType scheduleType = calRow.getScheduleType();
                final String scheduleTypeStr = scheduleType.name().toString();
                final CalendarRowConfig.Time1 executionTime = calRow.getExecutionTime();
                hash.put("exeHours", executionTime.hours());
                hash.put("exeMinutes", executionTime.minutes());
                hash.put("exeSeconds", executionTime.seconds());
                if (scheduleTypeStr.equalsIgnoreCase("None") || scheduleTypeStr.equalsIgnoreCase("Daily")) {
                    final CalendarRowConfig.Date1 startDate = calRow.getStartDate();
                    if (startDate != null) {
                        hash.put("startYear", startDate.year());
                        hash.put("startMonth", startDate.month());
                        hash.put("startDate", startDate.date());
                    }
                    else {
                        final String currentDateStr = DateTimeUtil.getCurrentTimeInUserTimeZone(EventViewerTRAction.dateFormat);
                        final String[] dateStr = currentDateStr.split("-");
                        hash.put("startYear", Integer.parseInt(dateStr[0]));
                        hash.put("startMonth", Integer.parseInt(dateStr[1]) - 1);
                        hash.put("startDate", Integer.parseInt(dateStr[2]));
                    }
                    if (scheduleTypeStr.equalsIgnoreCase("None")) {
                        hash.put("schedType", "Once");
                    }
                    else if (scheduleTypeStr.equalsIgnoreCase("Daily")) {
                        hash.put("schedType", "Daily");
                        final int skipFrequency = calRow.getSkipFrequency();
                        if (skipFrequency > 0) {
                            hash.put("dailyIntervalType", "alternativeDays");
                        }
                        else {
                            hash.put("dailyIntervalType", "everyDay");
                        }
                    }
                }
                else if (scheduleTypeStr.equalsIgnoreCase("Weekly")) {
                    String actSchedulerType = (String)DBUtil.getValueFromDB("ScheduledTaskDetails", "SCHEDULE_ID", (Object)schedulerID, "REPEAT_FREQUENCY");
                    if (actSchedulerType == null) {
                        actSchedulerType = "";
                    }
                    if (actSchedulerType.equalsIgnoreCase("Daily")) {
                        final CalendarRowConfig.Date1 startDate2 = calRow.getStartDate();
                        hash.put("startYear", startDate2.year());
                        hash.put("startMonth", startDate2.month());
                        hash.put("startDate", startDate2.date());
                        hash.put("dailyIntervalType", "weekDays");
                        hash.put("schedType", "Daily");
                    }
                    else if (actSchedulerType.equalsIgnoreCase("Weekly") || actSchedulerType.equalsIgnoreCase("")) {
                        hash.put("schedType", "Weekly");
                        final int[] daysOfWeek = calRow.getDaysOfWeek();
                        hash.put("daysOfWeek", daysOfWeek);
                    }
                }
                else if (scheduleTypeStr.equalsIgnoreCase("Yearly")) {
                    hash.put("schedType", "Monthly");
                    final int[] months = calRow.getMonths();
                    hash.put("months", months);
                    int datelen = 0;
                    if (calRow.getDates() != null) {
                        datelen = calRow.getDates().length;
                        int[] dates = new int[datelen];
                        dates = calRow.getDates();
                        hash.put("monthlyPerform", "Day");
                        hash.put("dates", dates[0]);
                    }
                    else {
                        hash.put("monthlyPerform", "WeekDay");
                        final int[] daysOfWeek2 = calRow.getDaysOfWeek();
                        hash.put("monthlyWeekDay", daysOfWeek2[0]);
                        hash.put("monthlyWeekNum", calRow.getWeeksOfMonth());
                    }
                }
                final TimeZone taskTimeZone = calRow.getTimeZone();
                if (!this.isTimeZoneDifferent(taskTimeZone)) {
                    hash.put("timeZoneDiff", true);
                    hash.put("taskTimeZone", taskTimeZone.getID());
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.INFO, "Exception while retrieving rows from calendar  ");
        }
        catch (final Exception ex) {
            Logger.getLogger(DMSchedulerUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hash;
    }
    
    private long getStartDateInMillis(final int day, final int month, final int year) {
        final Calendar dateCal = Calendar.getInstance();
        dateCal.set(year, month, day);
        dateCal.set(11, 0);
        dateCal.set(12, 0);
        dateCal.set(13, 0);
        dateCal.set(14, 0);
        final long longDate = dateCal.getTimeInMillis();
        return longDate;
    }
    
    private Long executionTime(final String scheduleName) throws DataAccessException {
        Long execTime = 0L;
        final Criteria crit = new Criteria(Column.getColumn("Schedule", "SCHEDULE_NAME"), (Object)scheduleName, 0);
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("Task_Input"));
        final Join join = new Join("Task_Input", "Schedule", new String[] { "SCHEDULE_ID" }, new String[] { "SCHEDULE_ID" }, 2);
        sq.addJoin(join);
        sq.setCriteria(crit);
        sq.addSelectColumn(Column.getColumn("Task_Input", "INSTANCE_ID"));
        sq.addSelectColumn(Column.getColumn("Task_Input", "SCHEDULE_TIME"));
        final DataObject instanceDO = SyMUtil.getPersistence().get(sq);
        if (!instanceDO.isEmpty()) {
            final Timestamp nextScheduledTime = (Timestamp)instanceDO.getFirstValue("Task_Input", "SCHEDULE_TIME");
            if (nextScheduledTime != null) {
                execTime = nextScheduledTime.getTime();
            }
        }
        return execTime;
    }
    
    public Long updateSchedulerTaskDetails(final Long scheduleID, final Long schedulerClassId, final HashMap schedulerValues) {
        Long taskId = null;
        try {
            final String schedulerName = schedulerValues.get("schedulerName");
            final DataObject scheduledTask = DeviceMgmtTaskUtil.getInstance().getSchTaskDetailsDO(schedulerClassId);
            if (scheduledTask.isEmpty()) {
                taskId = this.createScheduleTaskDetails(scheduleID, true, schedulerValues);
            }
            else {
                final Row taskDetailsRow = scheduledTask.getFirstRow("TaskDetails");
                final Row scheduledTaskDetailsRow = scheduledTask.getFirstRow("ScheduledTaskDetails");
                Row taskUserDetailsRow = null;
                if (scheduledTask.getTableNames().contains("TaskToUserRel")) {
                    taskUserDetailsRow = scheduledTask.getFirstRow("TaskToUserRel");
                }
                if (taskDetailsRow != null) {
                    taskId = (Long)taskDetailsRow.get("TASK_ID");
                    final Long completionTime = schedulerValues.get("COMPLETIONTIME");
                    final String remarks = schedulerValues.get("remarks");
                    final Integer counter = schedulerValues.get("COUNTER");
                    final String taskName = schedulerValues.get("taskName");
                    final String email = schedulerValues.get("email");
                    final String operationType = schedulerValues.get("operationType");
                    final String owner = schedulerValues.get("owner");
                    if (operationType != null) {
                        taskDetailsRow.set("TYPE", (Object)new Integer(operationType));
                    }
                    taskDetailsRow.set("STATUS", (Object)"SCHEDULED");
                    if (owner != null) {
                        taskDetailsRow.set("OWNER", (Object)owner);
                    }
                    if (taskName != null) {
                        taskDetailsRow.set("TASKNAME", (Object)taskName);
                    }
                    if (remarks != null) {
                        taskDetailsRow.set("REMARKS", (Object)remarks);
                    }
                    if (counter != null) {
                        taskDetailsRow.set("COUNTER", (Object)counter);
                    }
                    if (email != null) {
                        taskDetailsRow.set("EMAIL", (Object)email);
                    }
                    if (schedulerValues.get("description") != null) {
                        taskDetailsRow.set("DESCRIPTION", schedulerValues.get("description"));
                    }
                    final Long startTime = this.executionTime(schedulerName);
                    if (startTime != 0L) {
                        taskDetailsRow.set("STARTTIME", (Object)startTime);
                    }
                    if (completionTime != null) {
                        taskDetailsRow.set("COMPLETIONTIME", (Object)completionTime);
                    }
                    final String date = schedulerValues.get("date");
                    if (date != null) {
                        final int day = Integer.parseInt(Utils.getDayOfDate(date));
                        final int month = Integer.parseInt(Utils.getMonthOfDate(date));
                        final int year = Integer.parseInt(Utils.getYearOfDate(date));
                        final long startdate = this.getStartDateInMillis(day, month, year);
                        taskDetailsRow.set("STARTDATE", (Object)startdate);
                    }
                    final Long time = new Long(System.currentTimeMillis());
                    taskDetailsRow.set("MODIFIEDTIME", (Object)time);
                }
                if (schedulerValues.get("userID") != null) {
                    if (taskUserDetailsRow != null) {
                        taskUserDetailsRow.set("LAST_MODIFIED_BY", (Object)schedulerValues.get("userID"));
                    }
                    else {
                        taskUserDetailsRow = new Row("TaskToUserRel");
                        taskUserDetailsRow.set("TASK_ID", (Object)taskId);
                        taskUserDetailsRow.set("USER_ID", (Object)schedulerValues.get("userID"));
                        taskUserDetailsRow.set("LAST_MODIFIED_BY", (Object)schedulerValues.get("userID"));
                    }
                }
                if (scheduledTaskDetailsRow != null) {
                    final String schType = schedulerValues.get("schType");
                    if (schType != null) {
                        scheduledTaskDetailsRow.set("REPEAT_FREQUENCY", (Object)schType);
                    }
                    scheduledTaskDetailsRow.set("SCHEDULE_ID", (Object)scheduleID);
                    scheduledTaskDetailsRow.set("SCHEDULER_CLASS_ID", (Object)schedulerClassId);
                    scheduledTask.updateRow(taskDetailsRow);
                    scheduledTask.updateRow(scheduledTaskDetailsRow);
                    if (taskUserDetailsRow != null) {
                        if (scheduledTask.getTableNames().contains("TaskToUserRel")) {
                            scheduledTask.updateRow(taskUserDetailsRow);
                        }
                        else {
                            scheduledTask.addRow(taskUserDetailsRow);
                        }
                    }
                    SyMUtil.getPersistence().update(scheduledTask);
                }
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(DMSchedulerUtil.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        catch (final Exception ex2) {
            Logger.getLogger(DMSchedulerUtil.class.getName()).log(Level.SEVERE, null, ex2);
        }
        return taskId;
    }
    
    private Long getSchedulerClassID(final String scheduleName) {
        try {
            final Criteria crit = new Criteria(Column.getColumn("SchedulerClasses", "SCHEDULER_NAME"), (Object)scheduleName, 0);
            final DataObject dobj = SyMUtil.getPersistence().get("SchedulerClasses", crit);
            if (!dobj.isEmpty()) {
                return (Long)dobj.getFirstValue("SchedulerClasses", "SCHEDULER_CLASS_ID");
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private int[] getIntArrayFromString(final String numberString) {
        final String[] numberArray = numberString.split(",");
        final int[] result = new int[numberArray.length];
        for (int i = 0; i < numberArray.length; ++i) {
            result[i] = new Integer(numberArray[i].trim());
        }
        return result;
    }
    
    private boolean isTimeZoneDifferent(final TimeZone taskTimeZone) {
        final String currentTimeZoneID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZoneID();
        final String taskTimeZoneID = taskTimeZone.getID();
        return currentTimeZoneID.equals(taskTimeZoneID);
    }
    
    static {
        DMSchedulerUtil.dmschedulerutil = null;
    }
}
