package com.me.devicemanagement.onpremise.server.scheduler;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.taskengine.internal.UpdateController;
import com.adventnet.taskengine.internal.TaskEngineService;
import java.util.List;
import com.adventnet.taskengine.util.ScheduleUtil;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.scheduler.SchedulerInfo;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import com.adventnet.taskengine.internal.TimeMap;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.taskengine.TaskContext;
import com.adventnet.taskengine.util.PersistenceUtil;
import com.me.devicemanagement.framework.server.scheduler.TaskInfo;
import java.util.Properties;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.taskengine.util.CalendarRowConfig;
import java.util.TimeZone;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.Utils;
import java.sql.Timestamp;
import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.taskengine.Scheduler;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerProviderInterface;
import com.me.devicemanagement.framework.server.scheduler.SchedulerUtil;

public class SchedulerProviderImpl extends SchedulerUtil implements SchedulerProviderInterface
{
    private Logger logger;
    public static final String TASKENGINE_DEFAULT_CLASSNAME = "com.me.devicemanagement.onpremise.server.scheduler.SchedulerExecutionTask";
    
    public SchedulerProviderImpl() {
        this.logger = Logger.getLogger(SchedulerProviderImpl.class.getName());
    }
    
    public long createScheduler(final HashMap schedulerValues) {
        Long scheduleID = null;
        Long taskID = 0L;
        final DMSchedulerUtil enpScheduler = new DMSchedulerUtil();
        try {
            final String workflowName = schedulerValues.get("workflowName");
            final Long customerID = schedulerValues.get("customerID");
            String schedulerName = schedulerValues.get("schedulerName");
            final String description = schedulerValues.get("description");
            final String className = schedulerValues.get("className");
            String newscheduleName = null;
            if (!this.qualifySchedulerCreationBasedOnServerType(schedulerValues)) {
                return -1L;
            }
            this.createTaskEngineTask(workflowName, null);
            if (schedulerValues.get("existingTaskId") != null) {
                newscheduleName = schedulerName;
                final String existingSchedule = this.getScheduleNameForTask(new Long(schedulerValues.get("existingTaskId").toString()));
                scheduleID = this.getScheduleID(existingSchedule);
                if (existingSchedule != null) {
                    schedulerName = existingSchedule;
                }
            }
            else {
                scheduleID = this.getScheduleID(schedulerName);
            }
            if (scheduleID == null) {
                this.logger.log(Level.INFO, "Scheduler is new One and Name :{0} for the customer ID : {1} ", new Object[] { schedulerName, customerID });
                boolean skipMissedSche = Boolean.FALSE;
                if (schedulerValues.get("skip_missed_schedule") != null && schedulerValues.get("skip_missed_schedule").toString().equalsIgnoreCase("true")) {
                    skipMissedSche = Boolean.TRUE;
                }
                boolean schedStatus = Boolean.TRUE;
                if (schedulerValues.get("schedStatus") != null) {
                    schedStatus = schedulerValues.get("schedStatus");
                }
                int transactionTime = -1;
                if (schedulerValues.get("transaction_time") != null) {
                    transactionTime = schedulerValues.get("transaction_time");
                }
                scheduleID = enpScheduler.createSchedulerTask(schedulerName, description, workflowName, skipMissedSche, schedStatus, transactionTime);
                super.addSchedulerInTable(schedulerName, className, workflowName, schedulerValues);
                DataObject schedulerDo = SyMUtil.getPersistence().constructDataObject();
                schedulerDo = this.addRepetitionData(schedulerDo, scheduleID, schedulerValues);
                SyMUtil.getPersistence().add(schedulerDo);
                final DataObject taskInputDO = this.getTaskInputDO(workflowName);
                final Scheduler sch = (Scheduler)BeanUtil.lookup("Scheduler");
                sch.scheduleTask(schedulerName, workflowName, taskInputDO);
                taskID = enpScheduler.createScheduleTaskDetails(scheduleID, true, schedulerValues);
                this.logger.log(Level.INFO, "Scheduler is created for {0} and its schedule ID is {1} and its taskID is {2}", new Object[] { schedulerName, scheduleID, taskID });
            }
            else {
                this.logger.log(Level.INFO, "Scheduler Already Exists.So we need to update Scheduler Information");
                this.removeCalendarScheduler(scheduleID);
                this.removePeriodicScheduler(scheduleID);
                final Persistence p = SyMUtil.getPersistence();
                DataObject scheduleDO = p.getForPersonality("Schedule_Pers", new Criteria(new Column("Schedule", "SCHEDULE_NAME"), (Object)schedulerName, 0));
                scheduleDO = this.addRepetitionData(scheduleDO, scheduleID, schedulerValues);
                final Long schedClassId = (Long)DBUtil.getValueFromDB("SchedulerClasses", "SCHEDULER_NAME", (Object)schedulerName, "SCHEDULER_CLASS_ID");
                if (newscheduleName != null && !newscheduleName.equalsIgnoreCase(schedulerName)) {
                    this.logger.log(Level.INFO, "Scheduler Name is getting updated to " + newscheduleName);
                    scheduleDO = this.updateSchedulerName(scheduleDO, schedulerName, newscheduleName);
                }
                final Scheduler scheduler = (Scheduler)BeanUtil.lookup("Scheduler");
                scheduler.updateSchedule(scheduleDO);
                taskID = enpScheduler.updateSchedulerTaskDetails(scheduleID, schedClassId, schedulerValues);
                this.logger.log(Level.INFO, "Scheduler is updated for {0} and its schedule ID {1} , with taskID {2}", new Object[] { schedulerName, scheduleID, taskID });
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(SchedulerProviderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return taskID;
    }
    
    public DataObject addRepetitionData(DataObject schedulerDO, final Long scheduleID, final HashMap schedulerValues) {
        final String schType = schedulerValues.get("schType");
        final DMSchedulerUtil dmSchedulerUtil = DMSchedulerUtil.getInstance();
        if (schType != null && schType.equalsIgnoreCase("Hourly")) {
            final String timePeriod = schedulerValues.get("timePeriod");
            final String unitOfTime = schedulerValues.get("unitOfTime");
            final Timestamp endDate = schedulerValues.get("endDate");
            final Timestamp startDate = schedulerValues.get("startDate");
            schedulerDO = dmSchedulerUtil.createPeriodicScheduler(schedulerDO, scheduleID, timePeriod, unitOfTime, endDate, startDate);
            return schedulerDO;
        }
        final String time = schedulerValues.get("time");
        final int hours = Utils.getHourOfTime(time);
        final int minutes = Utils.getMinuteOfTime(time);
        final int seconds = Utils.getSecondOfTime(time);
        String timeZoneID = null;
        timeZoneID = schedulerValues.get("timezone");
        if (timeZoneID == null) {
            final TimeZone timeZone = ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZone();
            timeZoneID = timeZone.getID();
        }
        CalendarRowConfig calConf = null;
        if (schType.equalsIgnoreCase("Once")) {
            final String date = schedulerValues.get("date");
            calConf = dmSchedulerUtil.createOneTimeScheduler(hours, minutes, seconds, date, timeZoneID);
        }
        else if (schType.equalsIgnoreCase("daily")) {
            final String date = schedulerValues.get("date");
            final String dailyIntervalType = schedulerValues.get("dailyIntervalType");
            if (dailyIntervalType.equals("everyDay") || dailyIntervalType.equals("alternativeDays")) {
                int skipFrequency = 0;
                if (dailyIntervalType.equals("alternativeDays")) {
                    skipFrequency = 1;
                }
                calConf = dmSchedulerUtil.createDailyCalendarConfig(hours, minutes, seconds, date, skipFrequency, timeZoneID);
            }
            else if (dailyIntervalType.equals("weekDays")) {
                final String daysOfWeek = "2, 3, 4, 5, 6";
                calConf = dmSchedulerUtil.createWeeklyCalendarConfig(hours, minutes, seconds, date, daysOfWeek, timeZoneID);
            }
        }
        else if (schType.equalsIgnoreCase("weekly")) {
            final String daysOfWeek2 = schedulerValues.get("daysOfWeek");
            final String date2 = schedulerValues.get("date");
            calConf = dmSchedulerUtil.createWeeklyCalendarConfig(hours, minutes, seconds, date2, daysOfWeek2, timeZoneID);
        }
        else {
            if (!schType.equalsIgnoreCase("monthly")) {
                return null;
            }
            final String months = schedulerValues.get("months");
            final String monthlyPerform = schedulerValues.get("monthlyPerform");
            String dayOfWeek = null;
            String dayOfMonth = null;
            String numberInWeek = null;
            if (monthlyPerform.equals("WeekDay")) {
                dayOfWeek = schedulerValues.get("dayOfWeek");
                numberInWeek = schedulerValues.get("numOfWeek");
            }
            else {
                dayOfMonth = schedulerValues.get("dayOfMonth");
            }
            calConf = dmSchedulerUtil.createYearlyCalendarRepetition(monthlyPerform, hours, minutes, seconds, months, dayOfWeek, numberInWeek, dayOfMonth, timeZoneID);
        }
        schedulerDO = dmSchedulerUtil.createCalendarScheduler(schedulerDO, scheduleID, calConf);
        this.logger.log(Level.INFO, "Scheduler Created!!");
        return schedulerDO;
    }
    
    private DataObject updateSchedulerName(final DataObject scheduleDO, final String existingSchName, final String newScheduleName) {
        try {
            if (!scheduleDO.isEmpty()) {
                final Row schRow = scheduleDO.getFirstRow("Schedule");
                schRow.set("SCHEDULE_NAME", (Object)newScheduleName);
                scheduleDO.updateRow(schRow);
            }
            final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("SchedulerClasses");
            uq.setCriteria(new Criteria(Column.getColumn("SchedulerClasses", "SCHEDULER_NAME"), (Object)existingSchName, 0));
            uq.setUpdateColumn("SCHEDULER_NAME", (Object)newScheduleName);
            SyMUtil.getPersistence().update(uq);
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
        return scheduleDO;
    }
    
    private void removeCalendarScheduler(final Long schedule_id) {
        try {
            final Column col1 = Column.getColumn("Calendar", "SCHEDULE_ID");
            final Criteria calendarCriteria = new Criteria(col1, (Object)schedule_id, 0);
            SyMUtil.getPersistence().delete(calendarCriteria);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private void removePeriodicScheduler(final Long schedule_id) {
        try {
            final Column col1 = Column.getColumn("Periodic", "SCHEDULE_ID");
            final Criteria periodicCriteria = new Criteria(col1, (Object)schedule_id, 0);
            SyMUtil.getPersistence().delete(periodicCriteria);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private DataObject getTaskInputDO(final String workflowName) throws Exception {
        final Persistence persistence = SyMUtil.getPersistence();
        final DataObject taskInputDO = persistence.constructDataObject();
        final Long taskID = this.getIDForWorkflowName(workflowName);
        final Row newTaskRow = new Row("Task_Input");
        newTaskRow.set("TASK_ID", (Object)taskID);
        taskInputDO.addRow(newTaskRow);
        return taskInputDO;
    }
    
    private Long getIDForWorkflowName(final String workflowName) {
        Long taskID = null;
        try {
            final Criteria crit = new Criteria(Column.getColumn("TaskEngine_Task", "TASK_NAME"), (Object)workflowName, 0, false);
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
        final HashMap schedulerValues = DMSchedulerUtil.getInstance().getScheduledValues(schedulerName);
        return schedulerValues;
    }
    
    public String getScheduleNameForTask(final Long taskID) {
        return super.getScheduleNameForTask(taskID);
    }
    
    public Long getSchedulerClassIDForTask(final Long taskID) {
        return super.getSchedulerClassIDForTask(taskID);
    }
    
    public Long getSchedulerClassID(final String scheduleName) {
        return super.getSchedulerClassID(scheduleName);
    }
    
    public boolean isSchedulerDisabled(final String schedulerName) {
        boolean isSchedulerDisabled = true;
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("Scheduled_Task"));
            final Join join = new Join("Scheduled_Task", "Schedule", new String[] { "SCHEDULE_ID" }, new String[] { "SCHEDULE_ID" }, 2);
            final Join join2 = new Join("Schedule", "SchedulerClasses", new String[] { "SCHEDULE_NAME" }, new String[] { "SCHEDULER_NAME" }, 2);
            sq.addSelectColumn(Column.getColumn("Scheduled_Task", "SCHEDULE_ID"));
            sq.addSelectColumn(Column.getColumn("Scheduled_Task", "TASK_ID"));
            sq.addSelectColumn(Column.getColumn("Scheduled_Task", "ADMIN_STATUS"));
            sq.addJoin(join);
            sq.addJoin(join2);
            final Criteria crit = new Criteria(Column.getColumn("SchedulerClasses", "SCHEDULER_NAME"), (Object)schedulerName, 0);
            sq.setCriteria(crit);
            final DataObject instanceDO = SyMUtil.getPersistence().get(sq);
            if (!instanceDO.isEmpty()) {
                final Integer operational_status = (Integer)instanceDO.getFirstValue("Scheduled_Task", "ADMIN_STATUS");
                if (operational_status != null && operational_status == 3) {
                    isSchedulerDisabled = false;
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return isSchedulerDisabled;
    }
    
    public void executeAsynchronously(final String className, final HashMap taskInfoMap, final Properties properties, final String poolName) {
        taskInfoMap.put("poolName", poolName);
        this.executeAsynchronously(className, taskInfoMap, properties);
    }
    
    public void executeAsynchronously(final String className, final HashMap taskInfoMap, final Properties properties) {
        final TaskInfo taskinfo = new TaskInfo();
        taskinfo.className = "com.me.devicemanagement.onpremise.server.scheduler.SchedulerExecutionTask";
        taskinfo.scheduleTime = taskInfoMap.get("schedulerTime");
        if (taskInfoMap.containsKey("transactionTime") && taskInfoMap.get("transactionTime") != null) {
            taskinfo.transactionTime = taskInfoMap.get("transactionTime");
        }
        if (taskInfoMap.containsKey("taskCompletionHandler") && taskInfoMap.get("taskCompletionHandler") != null) {
            taskinfo.taskCompletionHandler = taskInfoMap.get("taskCompletionHandler");
        }
        if (taskInfoMap.get("poolName") != null) {
            taskinfo.poolName = taskInfoMap.get("poolName");
        }
        ((Hashtable<String, String>)properties).put("actual_className", className);
        taskinfo.userProps = properties;
        try {
            this.executeAsynchronously(taskinfo);
        }
        catch (final Exception ex) {
            Logger.getLogger(SchedulerProviderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void executeAsynchronously(final TaskInfo tInfo) throws Exception {
        final Scheduler scheduler = (Scheduler)BeanUtil.lookup("Scheduler");
        Long poolID = null;
        this.logger.log(Level.INFO, "Going to invoke thread" + tInfo.className);
        if (tInfo.poolName != null) {
            poolID = PersistenceUtil.getPoolID(tInfo.poolName);
        }
        final boolean isPoolLimitReached = this.isThreadPoolLimitReached(tInfo.poolName);
        if (isPoolLimitReached) {
            this.executeAsyncTask(tInfo);
            return;
        }
        final Properties taskProps = new Properties();
        if (tInfo.taskCompletionHandler != null) {
            ((Hashtable<String, String>)taskProps).put("task.completion.handler", tInfo.taskCompletionHandler);
        }
        if (poolID == null) {
            Long delay = 10L;
            if (tInfo.scheduleTime != null) {
                delay = tInfo.scheduleTime - System.currentTimeMillis();
            }
            scheduler.executeAsynchronously(tInfo.className, (long)delay, taskProps, (Row)null, (tInfo.transactionTime == null) ? -1 : ((int)tInfo.transactionTime), (Object)tInfo);
        }
        else {
            long scheduleTime = 0L;
            if (tInfo.scheduleTime == null) {
                scheduleTime = System.currentTimeMillis() + 10L;
            }
            else {
                scheduleTime = tInfo.scheduleTime;
            }
            final TaskContext context = new TaskContext();
            context.setAsync(true);
            context.setUserObject((Object)tInfo);
            final DataObject data = (DataObject)new WritableDataObject();
            Row row = new Row("TaskEngine_Task");
            row.set(3, (Object)tInfo.className);
            data.addRow(row);
            final Object taskEngID = row.get(1);
            row = new Row("Task_Input");
            row.set("TASK_ID", taskEngID);
            row.set("SCHEDULE_TIME", (Object)new Timestamp(scheduleTime));
            row.set("POOL_ID", (Object)poolID);
            final Object taskInputID = row.get(1);
            data.addRow(row);
            if (taskProps != null && taskProps.size() > 0) {
                for (final String key : ((Hashtable<Object, V>)taskProps).keySet()) {
                    final String value = taskProps.getProperty(key);
                    row = new Row("Default_Task_Input");
                    row.set(1, taskInputID);
                    row.set(2, (Object)key);
                    row.set(4, (Object)value);
                    data.addRow(row);
                }
            }
            context.setTaskInputDO(data);
            context.setTransactionTime((tInfo.transactionTime == null) ? -1 : ((int)tInfo.transactionTime));
            TimeMap.getInstance(tInfo.poolName).addToTimeMap((Long)null, Long.valueOf(scheduleTime), (DataObject)null, context);
        }
    }
    
    public Long getCustomerID(final String scheduleName) {
        return super.getCustomerID(scheduleName);
    }
    
    public Long getTaskIDForSchedule(final String scheduleName) {
        return super.getTaskIDForSchedule(scheduleName);
    }
    
    public Long getTaskIDForSchedule(final Long schedulerClassID) {
        return super.getTaskIDForSchedule(schedulerClassID);
    }
    
    public void executeAsynchronousWithDelay(final String className, final HashMap taskInfoMap, final Properties properties) {
        this.executeAsynchronously(className, taskInfoMap, properties);
    }
    
    @Deprecated
    public void setScheduledValuesInJson(final String scheduleName, final HttpServletRequest request, final Boolean isOnceScheduleReq, final Boolean removeScheduleReq, final Boolean schedulerDisabled) {
        this.setScheduledValuesInJson(scheduleName, request, isOnceScheduleReq, removeScheduleReq, schedulerDisabled, false);
    }
    
    public JSONObject getScheduledValuesInJson(final String scheduleName, final Boolean isOnceScheduleReq, final Boolean removeScheduleReq, final Boolean schedulerDisabled, final Long date_time) {
        return this.getScheduleJsonValues(scheduleName, isOnceScheduleReq, removeScheduleReq, schedulerDisabled, false, date_time);
    }
    
    @Deprecated
    public void setScheduledValuesInJson(final String scheduleName, final HttpServletRequest request, final Boolean isOnceScheduleReq, final Boolean removeScheduleReq, final Boolean schedulerDisabled, final Boolean emailReqInSched) {
        final Long datetime = new Long(System.currentTimeMillis());
        try {
            final JSONObject schedulerObj = this.getScheduleJsonValues(scheduleName, isOnceScheduleReq, removeScheduleReq, schedulerDisabled, emailReqInSched, datetime);
            request.setAttribute("schedulerObj", (Object)schedulerObj);
            request.setAttribute("currentTime", (Object)datetime);
            final String dateString = DateTimeUtil.getCurrentTimeInUserTimeZone("MMMM dd,YYYY HH:mm:ss");
            request.setAttribute("currentDateTime", (Object)dateString);
            request.setAttribute("currentTimeZone", (Object)DateTimeUtil.getCurrentTimeZone());
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
    }
    
    public JSONObject getScheduleJsonValues(final String scheduleName, final Boolean isOnceScheduleReq, final Boolean removeScheduleReq, final Boolean schedulerDisabled, final Boolean emailReqInScheduler, final Long date_time) {
        final JSONObject schedulerObj = new JSONObject();
        String scheduleTypeExtraText = "";
        String startTimeText = "";
        String startTimeExtraText = "";
        try {
            final SchedulerInfo scheduler = new SchedulerInfo();
            schedulerObj.put("isOnceScheduleReq", (Object)isOnceScheduleReq);
            schedulerObj.put("removeScheduleReq", (Object)removeScheduleReq);
            schedulerObj.put("schedulerDisabled", (Object)schedulerDisabled);
            schedulerObj.put("emailReqInSched", (Object)emailReqInScheduler);
            if (scheduleName != null) {
                final HashMap schedule = ApiFactoryProvider.getSchedulerAPI().getScheduledValues(scheduleName);
                final int schedulerSize = schedule.size();
                if (schedulerSize > 0) {
                    if (!schedule.containsKey("timeZoneDiff")) {
                        schedulerObj.put("taskTimeZone", (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZoneID());
                    }
                    else {
                        schedulerObj.put("timeZoneDiff", schedule.get("timeZoneDiff"));
                        schedulerObj.put("taskTimeZone", schedule.get("taskTimeZone"));
                    }
                    final String schType = schedule.get("schedType");
                    final int hours = schedule.get("exeHours");
                    final int minutes = schedule.get("exeMinutes");
                    final int seconds = schedule.get("exeSeconds");
                    String updateTime = null;
                    String hourStr = null;
                    if (hours < 10) {
                        hourStr = "0" + hours;
                    }
                    else {
                        hourStr = hours + "";
                    }
                    if (minutes < 10) {
                        updateTime = hourStr + ":0" + minutes;
                    }
                    else {
                        updateTime = hourStr + ":" + minutes;
                    }
                    String dateOfExec = "";
                    if (schType.equalsIgnoreCase("Once") || schType.equalsIgnoreCase("Daily")) {
                        final int day = schedule.get("startDate");
                        final int month = schedule.get("startMonth");
                        final int year = schedule.get("startYear");
                        dateOfExec = ((month < 9) ? dateOfExec.concat("0" + (month + 1) + "/") : dateOfExec.concat("" + (month + 1) + "/"));
                        dateOfExec = ((day < 10) ? dateOfExec.concat("0" + day + "/") : dateOfExec.concat("" + day + "/"));
                        dateOfExec = dateOfExec.concat("" + year);
                        if (schType.equalsIgnoreCase("Once")) {
                            scheduleTypeExtraText = "Once";
                            schedulerObj.put("onceTime", (Object)(dateOfExec + ", " + updateTime));
                        }
                        else {
                            schedulerObj.put("dailyTime", (Object)(dateOfExec + ", " + updateTime));
                            final String intervalType = schedule.get("dailyIntervalType");
                            schedulerObj.put("dailyIntervalType", (Object)intervalType);
                            if (intervalType.equals("weekDays")) {
                                scheduleTypeExtraText = I18N.getMsg("dc.common.scheduler.on_week_days", new Object[0]);
                            }
                            else if (intervalType.equals("alternativeDays")) {
                                scheduleTypeExtraText = I18N.getMsg("dc.common.scheduler.alternative_days", new Object[0]);
                            }
                            else {
                                scheduleTypeExtraText = I18N.getMsg("dc.common.EVERYDAY", new Object[0]);
                            }
                        }
                    }
                    else if (schType.equalsIgnoreCase("Weekly")) {
                        schedulerObj.put("weeklyTime", (Object)updateTime);
                        int[] days = new int[7];
                        days = schedule.get("daysOfWeek");
                        String weekDays = "";
                        for (int j = 0; j < days.length; ++j) {
                            weekDays = weekDays + days[j] + ",";
                            weekDays = weekDays.trim();
                        }
                        if (weekDays.charAt(weekDays.length() - 1) == ',') {
                            weekDays = weekDays.substring(0, weekDays.length() - 1);
                        }
                        schedulerObj.put("daysOfWeek", (Object)weekDays);
                        scheduleTypeExtraText = scheduler.getWeekDaysString(weekDays);
                    }
                    else {
                        schedulerObj.put("monthlyTime", (Object)updateTime);
                        final int[] months = schedule.get("months");
                        String monthList = "";
                        for (int j = 0; j < months.length; ++j) {
                            monthList = monthList + months[j] + ",";
                            monthList = monthList.trim();
                        }
                        if (monthList.charAt(monthList.length() - 1) == ',') {
                            monthList = monthList.substring(0, monthList.length() - 1);
                        }
                        schedulerObj.put("monthsList", (Object)monthList);
                        scheduleTypeExtraText = scheduler.getMonthString(monthList);
                        final String monthlyPerform = schedule.get("monthlyPerform");
                        schedulerObj.put("monthlyPerform", (Object)monthlyPerform);
                        startTimeExtraText = I18N.getMsg("dc.common.scheduler.during", new Object[0]) + " :";
                        if (monthlyPerform.equals("WeekDay")) {
                            final String monthlyWeekDay = schedule.get("monthlyWeekDay") + "";
                            final int[] monthlyWeek = schedule.get("monthlyWeekNum");
                            String monthlyWeekNum = "";
                            for (int k = 0; k < monthlyWeek.length; ++k) {
                                monthlyWeekNum = monthlyWeekNum + monthlyWeek[k] + ",";
                                monthlyWeekNum = monthlyWeekNum.trim();
                            }
                            if (monthlyWeekNum.charAt(monthlyWeekNum.length() - 1) == ',') {
                                monthlyWeekNum = monthlyWeekNum.substring(0, monthlyWeekNum.length() - 1);
                            }
                            schedulerObj.put("monthlyWeekDay", (Object)monthlyWeekDay);
                            schedulerObj.put("monthlyWeekNum", (Object)monthlyWeekNum);
                            startTimeExtraText = startTimeExtraText + " " + scheduler.getWeekNumString(monthlyWeekNum) + " " + scheduler.getDaysString(monthlyWeekDay) + " of selected Months";
                        }
                        else {
                            final String monthlyDate = schedule.get("dates") + "";
                            schedulerObj.put("monthlyDay", (Object)monthlyDate);
                            final Object extraText = startTimeExtraText + " " + scheduler.getDateString(monthlyDate);
                            startTimeExtraText = I18N.getMsg("dc.common.java.selected_months", new Object[] { extraText });
                        }
                    }
                    schedulerObj.put("scheduleType", (Object)schType);
                    schedulerObj.put("scheduleTypeExtraText", (Object)scheduleTypeExtraText);
                    schedulerObj.put("startTimeExtraText", (Object)startTimeExtraText);
                    if (!schType.equals("")) {
                        if (schType.equals("Once")) {
                            startTimeText = String.valueOf(schedulerObj.get("onceTime"));
                        }
                        else if (schType.equals("Daily")) {
                            startTimeText = String.valueOf(schedulerObj.get("dailyTime"));
                        }
                        else if (schType.equals("Weekly")) {
                            startTimeText = String.valueOf(schedulerObj.get("weeklyTime"));
                        }
                        else {
                            startTimeText = String.valueOf(schedulerObj.get("monthlyTime"));
                        }
                        final Long nextRunTime = this.getNextExecutionTimeForSchedule(scheduleName);
                        if (nextRunTime == null || nextRunTime == -1L) {
                            schedulerObj.put("nextRunTime", (Object)"--");
                        }
                        else if (nextRunTime < date_time) {
                            schedulerObj.put("nextRunTime", (Object)I18N.getMsg("dc.common.Disabled", new Object[0]));
                        }
                        else {
                            schedulerObj.put("nextRunTime", (Object)Utils.getTime(nextRunTime));
                        }
                        schedulerObj.put("startTimeText", (Object)startTimeText);
                    }
                }
                else {
                    schedulerObj.put("schedulerDisabled", (Object)Boolean.TRUE);
                }
            }
            else {
                schedulerObj.put("schedulerDisabled", (Object)Boolean.TRUE);
                schedulerObj.put("taskTimeZone", (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZoneID());
            }
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
        return schedulerObj;
    }
    
    @Deprecated
    public Long createScheduleFromJson(final JSONObject schedJson, final String operationType, final String taskName, final String scheduleName, final String workEngineId, final String workflowName, final String className, final String description, final String email, final String owner, final Long customerId, final Long existingTaskId) {
        return this.createScheduleFromJson(schedJson, operationType, taskName, scheduleName, workflowName, className, description, email, owner, customerId, existingTaskId, Boolean.FALSE);
    }
    
    public Long createScheduleFromJson(final JSONObject schedJson, final String operationType, final String taskName, final String scheduleName, final String workflowName, final String className, final String description, final String email, final String owner, final Long customerId, final Long existingTaskId, final Boolean skipMissedSchedule) {
        Long taskId = null;
        try {
            final HashMap schedulerProps = new HashMap();
            schedulerProps.put("operationType", operationType);
            schedulerProps.put("workflowName", workflowName);
            schedulerProps.put("schedulerName", scheduleName);
            schedulerProps.put("taskName", taskName);
            final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            schedulerProps.put("userID", userId);
            schedulerProps.put("className", className);
            if (schedJson.has("taskTimeZone")) {
                schedulerProps.put("timezone", schedJson.getString("taskTimeZone"));
            }
            schedulerProps.put("skip_missed_schedule", skipMissedSchedule);
            if (email != null) {
                schedulerProps.put("email", email);
            }
            if (customerId != null) {
                schedulerProps.put("customerID", customerId);
            }
            if (description != null) {
                schedulerProps.put("description", description);
            }
            if (owner != null) {
                schedulerProps.put("owner", owner);
            }
            if (existingTaskId != null) {
                schedulerProps.put("existingTaskId", existingTaskId);
            }
            final String schType = String.valueOf(schedJson.get("scheduleType"));
            schedulerProps.put("schType", schType);
            if (schType.equals("Once")) {
                final String onceTime = String.valueOf(schedJson.get("onceTime"));
                final String format = "MM/dd/yyyy HH:mm:ss";
                String time;
                String date;
                long scheduledtime;
                if (onceTime.contains(",")) {
                    final String[] dateTime = onceTime.split(", ");
                    time = dateTime[1] + ":" + "00";
                    date = dateTime[0];
                    scheduledtime = DateTimeUtil.dateInLonginUserTimeZone(date + " " + time, format);
                }
                else {
                    date = DateTimeUtil.getMonthFromString(onceTime, format);
                    time = DateTimeUtil.getTimeFromString(onceTime, format);
                    scheduledtime = DateTimeUtil.dateInLong(onceTime, format);
                }
                long currentTime = System.currentTimeMillis();
                if (currentTime > scheduledtime) {
                    this.logger.log(Level.INFO, "Scheduled Time ::" + onceTime + " is less than current time.So adding 1 minute to current time");
                    currentTime += 60000L;
                    final String currentTimeStr = Utils.longdateToString(currentTime, format);
                    final String[] dateTime = currentTimeStr.split(" ");
                    time = dateTime[1];
                    date = dateTime[0];
                }
                schedulerProps.put("time", time);
                schedulerProps.put("date", date);
            }
            else if (schType.equals("Daily")) {
                final String dailyTime = String.valueOf(schedJson.get("dailyTime"));
                final String[] dateTime2 = dailyTime.split(", ");
                final String time2 = dateTime2[1] + ":00";
                final String date2 = dateTime2[0];
                final String dailyIntervalType = String.valueOf(schedJson.get("dailyIntervalType"));
                schedulerProps.put("time", time2);
                schedulerProps.put("date", date2);
                schedulerProps.put("dailyIntervalType", dailyIntervalType);
            }
            else if (schType.equals("Weekly")) {
                final String time3 = schedJson.get("weeklyTime") + ":00";
                final String daysOfWeek = String.valueOf(schedJson.get("daysOfWeek"));
                schedulerProps.put("time", time3);
                schedulerProps.put("daysOfWeek", daysOfWeek);
            }
            else if (schType.equals("Monthly")) {
                final String time3 = schedJson.get("monthlyTime") + ":00";
                final String monthsList = String.valueOf(schedJson.get("monthsList"));
                final String monthlyPerform = String.valueOf(schedJson.get("monthlyPerform"));
                String dayOfWeek = null;
                String dayOfMonth = null;
                String monthlyWeekNum = null;
                if (monthlyPerform.equals("WeekDay")) {
                    dayOfWeek = String.valueOf(schedJson.get("monthlyWeekDay"));
                    schedulerProps.put("dayOfWeek", dayOfWeek);
                    monthlyWeekNum = String.valueOf(schedJson.get("monthlyWeekNum"));
                    schedulerProps.put("numOfWeek", monthlyWeekNum);
                }
                else if (monthlyPerform.equals("Day")) {
                    dayOfMonth = String.valueOf(schedJson.get("monthlyDay"));
                    schedulerProps.put("dayOfMonth", dayOfMonth);
                }
                schedulerProps.put("time", time3);
                schedulerProps.put("months", monthsList);
                schedulerProps.put("monthlyPerform", monthlyPerform);
            }
            taskId = ApiFactoryProvider.getSchedulerAPI().createScheduler(schedulerProps);
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
        return taskId;
    }
    
    @Deprecated
    public Long createScheduleFromJson(final JSONObject schedJson, final String operationType, final String taskName, final String scheduleName, final String workEngineId, final String workflowName, final String className, final String description, final String email, final String owner, final Long customerId, final Long existingTaskId, final Boolean skipMissedSchedule) {
        return this.createScheduleFromJson(schedJson, operationType, taskName, scheduleName, workflowName, className, description, email, owner, customerId, existingTaskId, skipMissedSchedule);
    }
    
    public void setSchedulerState(final boolean status, final String schedulerName) {
        try {
            final Long schedulerClassID = super.getSchedulerClassID(schedulerName);
            this.setSchedulerState(status, schedulerClassID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception  while enabling workflow details:- SchedulerName: " + schedulerName, e);
        }
    }
    
    public void setSchedulerState(final boolean status, final Long schedulerClassID) {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("SchedulerClasses"));
            sq.addJoin(new Join("SchedulerClasses", "Schedule", new String[] { "SCHEDULER_NAME" }, new String[] { "SCHEDULE_NAME" }, 2));
            sq.addJoin(new Join("Schedule", "Scheduled_Task", new String[] { "SCHEDULE_ID" }, new String[] { "SCHEDULE_ID" }, 2));
            sq.setCriteria(new Criteria(Column.getColumn("SchedulerClasses", "SCHEDULER_CLASS_ID"), (Object)schedulerClassID, 0));
            sq.addSelectColumn(Column.getColumn("Scheduled_Task", "TASK_ID"));
            sq.addSelectColumn(Column.getColumn("Scheduled_Task", "SCHEDULE_ID"));
            final DataObject dobj = SyMUtil.getPersistence().get(sq);
            if (!dobj.isEmpty()) {
                final Long scheduleID = (Long)dobj.getFirstValue("Scheduled_Task", "SCHEDULE_ID");
                final Long taskD = (Long)dobj.getFirstValue("Scheduled_Task", "TASK_ID");
                int admin_status = 0;
                if (status) {
                    admin_status = 3;
                }
                else {
                    admin_status = 4;
                }
                if (scheduleID != null && taskD != null) {
                    final Scheduler sch = (Scheduler)BeanUtil.lookup("Scheduler");
                    sch.setScheduledTaskAdminStatus((long)scheduleID, (long)taskD, admin_status);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception  while enabling workflow details:- SchedulerClassID: " + schedulerClassID, e);
        }
    }
    
    private Long getScheduleID(final String schedulerName) {
        Long scheduleID = null;
        try {
            DataObject scheduleDO = null;
            final Column col = Column.getColumn("Schedule", "SCHEDULE_NAME");
            final Criteria criteria = new Criteria(col, (Object)schedulerName, 0);
            scheduleDO = SyMUtil.getPersistence().get("Schedule", criteria);
            if (scheduleDO.isEmpty()) {
                this.logger.log(Level.INFO, "ScheduleID for Scheduler Name : " + schedulerName + " is not available");
            }
            else {
                scheduleID = (Long)scheduleDO.getFirstValue("Schedule", "SCHEDULE_ID");
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return scheduleID;
    }
    
    public void deleteScheduleSpecificInput(final Long schedulerClassID, final String paramName) {
        super.deleteScheduleSpecificInput(schedulerClassID, paramName);
    }
    
    public void addOrUpdateScheduleSpecificInput(final Long schedulerClassID, final String paramName, final String paramValue) {
        super.addOrUpdateScheduleSpecificInput(schedulerClassID, paramName, paramValue);
    }
    
    public String getScheduleSpecificInputValue(final Long schedulerClassID, final String paramName) {
        return super.getScheduleSpecificInputValue(schedulerClassID, paramName);
    }
    
    public Long getSchedulerClassIDFromInput(final String paramName, final String paramValue) {
        return super.getSchedulerClassIDFromInput(paramName, paramValue);
    }
    
    private boolean createTaskEngineTask(final String workflowName, String className) {
        try {
            if (className == null || className.equals("")) {
                className = "com.me.devicemanagement.onpremise.server.scheduler.SchedulerExecutionTask";
            }
            final Criteria crit = new Criteria(Column.getColumn("TaskEngine_Task", "TASK_NAME"), (Object)workflowName, 0);
            DataObject taskDo = SyMUtil.getPersistence().get("TaskEngine_Task", crit);
            if (taskDo.isEmpty()) {
                taskDo = SyMUtil.getPersistence().constructDataObject();
                final Row taskRow = new Row("TaskEngine_Task");
                taskRow.set("TASK_NAME", (Object)workflowName);
                taskRow.set("CLASS_NAME", (Object)className);
                taskDo.addRow(taskRow);
                SyMUtil.getPersistence().add(taskDo);
                return true;
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public Long getNextExecutionTimeForTask(final Long taskID) {
        try {
            final String scheduleName = this.getScheduleNameForTask(taskID);
            return this.getNextExecutionTimeForSchedule(scheduleName);
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Long getNextExecutionTimeForSchedule(final String scheduleName) {
        Long nextExecTime = null;
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("Task_Input"));
            Join join = new Join("Task_Input", "Schedule", new String[] { "SCHEDULE_ID" }, new String[] { "SCHEDULE_ID" }, 1);
            sq.addJoin(join);
            join = new Join("Schedule", "SchedulerClasses", new String[] { "SCHEDULE_NAME" }, new String[] { "SCHEDULER_NAME" }, 2);
            sq.addJoin(join);
            final Criteria crit = new Criteria(Column.getColumn("SchedulerClasses", "SCHEDULER_NAME"), (Object)scheduleName, 0);
            sq.setCriteria(crit);
            sq.addSelectColumn(Column.getColumn("Task_Input", "INSTANCE_ID"));
            sq.addSelectColumn(Column.getColumn("Task_Input", "SCHEDULE_TIME"));
            final DataObject dobj = SyMUtil.getPersistence().get(sq);
            if (!dobj.isEmpty()) {
                final Timestamp nextScheduledTime = (Timestamp)dobj.getFirstValue("Task_Input", "SCHEDULE_TIME");
                if (nextScheduledTime != null) {
                    nextExecTime = nextScheduledTime.getTime();
                }
                else {
                    nextExecTime = -1L;
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return nextExecTime;
    }
    
    public boolean isScheduleCreated(final String scheduleName) {
        return super.isScheduleCreated(scheduleName);
    }
    
    public void removeScheduler(final String scheduleName) {
        try {
            if (scheduleName != null) {
                final Long scheduleID = (Long)DBUtil.getValueFromDB("Schedule", "SCHEDULE_NAME", (Object)scheduleName, "SCHEDULE_ID");
                if (scheduleID != null) {
                    final Scheduler sch = (Scheduler)BeanUtil.lookup("Scheduler");
                    sch.removeSchedule((long)scheduleID);
                    Criteria cri = new Criteria(new Column("ScheduledTaskDetails", "SCHEDULE_ID"), (Object)scheduleID, 0);
                    SyMUtil.getPersistence().delete(cri);
                    cri = new Criteria(new Column("SchedulerClasses", "SCHEDULER_NAME"), (Object)scheduleName, 0);
                    SyMUtil.getPersistence().delete(cri);
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void removeScheduler(final Long schedulerClassID) {
        try {
            final String scheduleName = (String)DBUtil.getValueFromDB("SchedulerClasses", "SCHEDULER_CLASS_ID", (Object)schedulerClassID, "SCHEDULER_NAME");
            if (scheduleName != null) {
                this.removeScheduler(scheduleName);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public Long calculateNextScheduleTime(final String scheduleName, final Long prevExecTime) {
        try {
            final Long scheduleID = this.getScheduleID(scheduleName);
            final DataObject schedule = PersistenceUtil.getSchedule(scheduleID, true);
            final Long time = ScheduleUtil.calculateNextScheduleTime(schedule, prevExecTime, true);
            return time;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return -1L;
        }
    }
    
    public List getSchedulesForCriteria(final Criteria schedulerCriteria) {
        return super.getSchedulesForCriteria(schedulerCriteria);
    }
    
    public Long getPeriodicEndTime(final Long schedulerClassID) {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("SchedulerClasses"));
            sq.addJoin(new Join("SchedulerClasses", "Schedule", new String[] { "SCHEDULER_NAME" }, new String[] { "SCHEDULE_NAME" }, 2));
            sq.addJoin(new Join("Schedule", "Periodic", new String[] { "SCHEDULE_ID" }, new String[] { "SCHEDULE_ID" }, 2));
            final Criteria crit = new Criteria(Column.getColumn("SchedulerClasses", "SCHEDULER_CLASS_ID"), (Object)schedulerClassID, 0);
            sq.setCriteria(crit);
            sq.addSelectColumn(Column.getColumn("Periodic", "SCHEDULE_ID"));
            sq.addSelectColumn(Column.getColumn("Periodic", "END_DATE"));
            final DataObject dobj = SyMUtil.getPersistence().get(sq);
            if (!dobj.isEmpty()) {
                final Row r = dobj.getFirstRow("Periodic");
                final Timestamp endTime = (Timestamp)r.get("END_DATE");
                return endTime.getTime();
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return null.getTime();
    }
    
    public Long getPeriodicTimePeriod(final Long taskId) {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("ScheduledTaskDetails"));
            sq.addJoin(new Join("ScheduledTaskDetails", "Schedule", new String[] { "SCHEDULE_ID" }, new String[] { "SCHEDULE_ID" }, 2));
            sq.addJoin(new Join("Schedule", "Periodic", new String[] { "SCHEDULE_ID" }, new String[] { "SCHEDULE_ID" }, 2));
            final Criteria crit = new Criteria(Column.getColumn("ScheduledTaskDetails", "TASK_ID"), (Object)taskId, 0);
            sq.setCriteria(crit);
            sq.addSelectColumn(Column.getColumn("Periodic", "SCHEDULE_ID"));
            sq.addSelectColumn(Column.getColumn("Periodic", "TIME_PERIOD"));
            sq.addSelectColumn(Column.getColumn("Periodic", "UNIT_OF_TIME"));
            final DataObject dobj = SyMUtil.getPersistence().get(sq);
            if (!dobj.isEmpty()) {
                final Row r = dobj.getFirstRow("Periodic");
                Long timePeriod = (Long)r.get("TIME_PERIOD");
                final String unitOfTime = (String)r.get("UNIT_OF_TIME");
                if (unitOfTime.equalsIgnoreCase("minutes")) {
                    timePeriod *= 60L;
                }
                else if (unitOfTime.equalsIgnoreCase("hours")) {
                    timePeriod = timePeriod * 60L * 60L;
                }
                return timePeriod;
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean getSchedulerState(final Long schedulerClassID) {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("SchedulerClasses"));
            sq.addJoin(new Join("SchedulerClasses", "Schedule", new String[] { "SCHEDULER_NAME" }, new String[] { "SCHEDULE_NAME" }, 2));
            sq.addJoin(new Join("Schedule", "Scheduled_Task", new String[] { "SCHEDULE_ID" }, new String[] { "SCHEDULE_ID" }, 2));
            final Criteria crit = new Criteria(Column.getColumn("SchedulerClasses", "SCHEDULER_CLASS_ID"), (Object)schedulerClassID, 0);
            sq.setCriteria(crit);
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dobj = SyMUtil.getPersistence().get(sq);
            if (!dobj.isEmpty()) {
                final int status = (int)dobj.getFirstValue("Scheduled_Task", "ADMIN_STATUS");
                if (status == 3) {
                    return true;
                }
                if (status == 4) {
                    return false;
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void updatePeriodicSchedule(final HashMap props) {
        try {
            final String scheduleName = props.get("SCHEDULE_NAME");
            final Long scheduleID = (Long)DBUtil.getValueFromDB("Schedule", "SCHEDULE_NAME", (Object)scheduleName, "SCHEDULE_ID");
            final Long timePeriod = props.get("TIME_PERIOD");
            final String unitOfTime = props.get("UNIT_OF_TIME");
            final Timestamp startDate = props.get("START_DATE");
            if (scheduleID != null) {
                final Criteria crit = new Criteria(new Column("Periodic", "SCHEDULE_ID"), (Object)scheduleID, 0);
                final DataObject dobj = SyMUtil.getPersistence().get("Periodic", crit);
                if (!dobj.isEmpty()) {
                    final Row r = dobj.getFirstRow("Periodic");
                    if (timePeriod != null) {
                        r.set("TIME_PERIOD", (Object)timePeriod);
                    }
                    if (unitOfTime != null && !unitOfTime.equals("")) {
                        r.set("UNIT_OF_TIME", (Object)unitOfTime);
                    }
                    if (startDate != null) {
                        r.set("START_DATE", (Object)startDate);
                    }
                    dobj.updateRow(r);
                    SyMUtil.getPersistence().update(dobj);
                }
                final DataObject scheduleDO = SyMUtil.getPersistence().getForPersonality("Schedule_Pers", new Criteria(Column.getColumn("Schedule", "SCHEDULE_NAME"), (Object)scheduleName, 0));
                final Scheduler s = (Scheduler)BeanUtil.lookup("Scheduler");
                if (TaskEngineService.updateController == null) {
                    TaskEngineService.updateController = new UpdateController();
                }
                s.updateSchedule(scheduleDO);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void updateExecutionTime(final String scheduleName, final Timestamp time) {
        try {
            final Long scheduleID = this.getScheduleID(scheduleName);
            final Criteria crit = new Criteria(Column.getColumn("Task_Input", "SCHEDULE_ID"), (Object)scheduleID, 0);
            final DataObject dobj = SyMUtil.getPersistence().get("Task_Input", crit);
            if (!dobj.isEmpty()) {
                final Row r = dobj.getFirstRow("Task_Input");
                r.set("SCHEDULE_TIME", (Object)time);
                dobj.updateRow(r);
                SyMUtil.getPersistence().update(dobj);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean updateTimezone(final String scheduleName, final TimeZone timeZone) {
        try {
            final Persistence persistence = SyMUtil.getPersistence();
            final DataObject scheduleDO = persistence.getForPersonality("Schedule_Pers", new Criteria(new Column("Schedule", "SCHEDULE_NAME"), (Object)scheduleName, 0));
            final Long scheduleId = (Long)scheduleDO.getValue("Schedule", "SCHEDULE_ID", (Criteria)null);
            scheduleDO.set("Calendar", "TZ", (Object)timeZone.getID());
            final Scheduler scheduler = (Scheduler)BeanUtil.lookup("Scheduler");
            scheduler.updateSchedule(scheduleDO);
            final Long schedulerClassId = this.getSchedulerClassID(scheduleName);
            final HashMap schedulerValues = new HashMap();
            schedulerValues.put("schedulerName", scheduleName);
            new DMSchedulerUtil().updateSchedulerTaskDetails(scheduleId, schedulerClassId, schedulerValues);
            return true;
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public TimeZone getTimezone(final String scheduleName) {
        TimeZone timezone = TimeZone.getDefault();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Calendar"));
        selectQuery.addJoin(new Join("Calendar", "Schedule", new String[] { "SCHEDULE_ID" }, new String[] { "SCHEDULE_ID" }, 2));
        final Criteria criteria = new Criteria(Column.getColumn("Schedule", "SCHEDULE_NAME"), (Object)scheduleName, 0);
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(new Column("Calendar", "SCHEDULE_ID"));
        selectQuery.addSelectColumn(new Column("Calendar", "TZ"));
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("Calendar");
                final String tz = (String)row.get("TZ");
                if (tz != null) {
                    timezone = TimeZone.getTimeZone(tz);
                }
            }
        }
        catch (final DataAccessException daex) {
            daex.printStackTrace();
        }
        return timezone;
    }
    
    public String getRepeatFrequency(final String scheduleName) {
        return super.getRepeatFrequency(scheduleName);
    }
    
    public Properties getScheduleSpecificInputs(final Long schedulerClassID) {
        return super.getScheduleSpecificInputs(schedulerClassID);
    }
    
    public List getScheduleNamesForWorkflow(final String workflowName) {
        return super.getScheduleNamesForWorkflow(workflowName);
    }
    
    public HashMap fetchNextExecTimeForSchedules(final List scheduleList) {
        HashMap map = null;
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("Task_Input"));
            Join join = new Join("Task_Input", "Schedule", new String[] { "SCHEDULE_ID" }, new String[] { "SCHEDULE_ID" }, 1);
            sq.addJoin(join);
            join = new Join("Schedule", "SchedulerClasses", new String[] { "SCHEDULE_NAME" }, new String[] { "SCHEDULER_NAME" }, 2);
            sq.addJoin(join);
            final Criteria crit = new Criteria(Column.getColumn("SchedulerClasses", "SCHEDULER_CLASS_ID"), (Object)scheduleList.toArray(), 8);
            sq.setCriteria(crit);
            sq.addSelectColumn(Column.getColumn("Task_Input", "INSTANCE_ID"));
            sq.addSelectColumn(Column.getColumn("Task_Input", "SCHEDULE_TIME"));
            sq.addSelectColumn(Column.getColumn("Task_Input", "SCHEDULE_ID"));
            sq.addSelectColumn(Column.getColumn("Schedule", "SCHEDULE_ID"));
            sq.addSelectColumn(Column.getColumn("Schedule", "SCHEDULE_NAME"));
            sq.addSelectColumn(Column.getColumn("SchedulerClasses", "SCHEDULER_CLASS_ID"));
            sq.addSelectColumn(Column.getColumn("SchedulerClasses", "SCHEDULER_NAME"));
            final DataObject dobj = SyMUtil.getPersistence().get(sq);
            if (!dobj.isEmpty()) {
                map = new HashMap();
                for (final Object schClassID : scheduleList) {
                    Criteria criteria = new Criteria(Column.getColumn("SchedulerClasses", "SCHEDULER_CLASS_ID"), (Object)schClassID, 0);
                    final Row schClassRow = dobj.getRow("SchedulerClasses", criteria);
                    final String schName = (String)schClassRow.get("SCHEDULER_NAME");
                    criteria = new Criteria(Column.getColumn("Schedule", "SCHEDULE_NAME"), (Object)schName, 0);
                    final Row schRow = dobj.getRow("Schedule", criteria);
                    final Long scheduleID = (Long)schRow.get("SCHEDULE_ID");
                    criteria = new Criteria(Column.getColumn("Task_Input", "SCHEDULE_ID"), (Object)scheduleID, 0);
                    final Row taskInputRow = dobj.getRow("Task_Input", criteria);
                    final Timestamp nextExecTime = (Timestamp)taskInputRow.get("SCHEDULE_TIME");
                    if (nextExecTime != null) {
                        map.put(schClassID, nextExecTime.getTime());
                    }
                    else {
                        map.put(schClassID, -1L);
                    }
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return map;
    }
    
    public Long getOrAddUserIDInTask(final Long taskID) {
        final SelectQuery userQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaUser"));
        final Criteria criteria1 = new Criteria(Column.getColumn("TaskDetails", "TASK_ID"), (Object)taskID, 0);
        userQuery.setCriteria(criteria1);
        userQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        final Join userJoin = new Join("AaaUser", "TaskDetails", new String[] { "FIRST_NAME" }, new String[] { "OWNER" }, 2);
        final Join logJoin = new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
        userQuery.addJoin(userJoin);
        userQuery.addJoin(logJoin);
        Long user_id = null;
        try {
            final DataObject dataObject1 = SyMUtil.getPersistence().get(userQuery);
            if (!dataObject1.isEmpty()) {
                final Row useridRow = dataObject1.getRow("AaaUser");
                user_id = (Long)useridRow.get("USER_ID");
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(SchedulerProviderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return user_id;
    }
    
    public Boolean isSchedulesWaiting(final String threadPoolName) {
        boolean isSchedulesWaiting = true;
        final String waitingSchedules = TimeMap.getInstance(threadPoolName).getWaitingSchedules();
        if (waitingSchedules == null || waitingSchedules.startsWith("[]")) {
            isSchedulesWaiting = false;
        }
        return isSchedulesWaiting;
    }
    
    private boolean isThreadPoolLimitReached(String threadPoolName) throws Exception {
        boolean poolLimitReached = false;
        if (threadPoolName == null) {
            threadPoolName = "asynchThreadPool";
        }
        final ScheduledThreadPoolExecutor executor = PersistenceUtil.getExecutor(threadPoolName);
        final int activeThreadcount = executor.getActiveCount();
        final int poolSize = executor.getCorePoolSize();
        this.logger.log(Level.INFO, "###Thread pool " + threadPoolName + " Info: " + executor.toString());
        if (poolSize > 0 && poolSize == activeThreadcount) {
            this.logger.log(Level.INFO, "###Thread pool: " + threadPoolName + " limit reached");
            this.logger.log(Level.INFO, "###Pool size: " + poolSize + "###Active thread count:" + activeThreadcount);
            poolLimitReached = true;
            METrackerUtil.incrementMETrackParams("THREADPOOL_" + threadPoolName);
        }
        return poolLimitReached && (threadPoolName.equals("somCommonPool") || threadPoolName.equals("configPool") || threadPoolName.equals("patchPool")) && poolLimitReached;
    }
    
    public Boolean isAnyActiveThreadInPool(final String threadPoolName) throws Exception {
        boolean activeThread = false;
        final ScheduledThreadPoolExecutor executor = PersistenceUtil.getExecutor(threadPoolName);
        final int activeThreadcount = executor.getActiveCount();
        if (activeThreadcount > 0) {
            activeThread = true;
        }
        return activeThread;
    }
    
    private void executeAsyncTask(final TaskInfo taskInfo) throws Exception {
        String threadPoolName = taskInfo.poolName;
        if (threadPoolName == null) {
            threadPoolName = "asynchThreadPool";
        }
        this.logger.log(Level.INFO, "Thread pool size = active thread ==> invoking thread outside threadPool");
        final Thread taskThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10L);
                    final Properties props = taskInfo.userProps;
                    final String className = props.getProperty("actual_className");
                    SchedulerProviderImpl.this.invokeMethodInClass(className, props);
                }
                catch (final InterruptedException e) {
                    SchedulerProviderImpl.this.logger.log(Level.WARNING, "Interrupted Exception occured : " + e);
                }
            }
        });
        taskThread.start();
    }
    
    private void invokeMethodInClass(final String classname, final Properties props) {
        this.logger.log(Level.FINE, "Class to get Invoked is : {0}", new Object[] { classname });
        try {
            final SchedulerExecutionInterface schedulerInterface = (SchedulerExecutionInterface)Class.forName(classname).newInstance();
            schedulerInterface.executeTask(props);
        }
        catch (final ClassNotFoundException ex) {
            Logger.getLogger(SchedulerExecutionTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception occured while invoking executeTask Method in class", e);
        }
    }
}
