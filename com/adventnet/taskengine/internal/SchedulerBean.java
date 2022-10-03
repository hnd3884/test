package com.adventnet.taskengine.internal;

import java.util.Hashtable;
import com.adventnet.taskengine.ScheduleRetryHandler;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.taskengine.Task;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.mfw.service.ServiceUtil;
import java.util.Date;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import java.util.logging.Level;
import com.adventnet.taskengine.util.ScheduleUtil;
import java.util.Iterator;
import java.sql.Timestamp;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.taskengine.TaskContext;
import java.util.Properties;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.taskengine.util.PersistenceUtil;
import java.util.logging.Logger;
import com.adventnet.taskengine.Scheduler;

public class SchedulerBean implements Scheduler
{
    private transient Logger out;
    private static final int AUDIT_START_TIME = 1;
    private static final int AUDIT_END_TIME = 2;
    
    public SchedulerBean() {
        this.out = Logger.getLogger(this.getClass().getName());
    }
    
    @Override
    public void preponeTask(final Long instanceID) throws Exception {
        final DataObject taskInputDO = PersistenceUtil.getTaskInputDO(instanceID);
        final Row taskInputRow = taskInputDO.getFirstRow("Task_Input");
        final String poolName = this.getPoolNameForTaskInput(taskInputRow);
        TimeMap.getInstance(poolName).preponeTask(instanceID);
    }
    
    @Override
    public void executeAsynchronously(final String taskClassName, final long delay, final Properties taskProps, final Row auditRow, final int transactionTime, final String poolName) throws Exception {
        this.executeAsynchronously(taskClassName, delay, taskProps, auditRow, transactionTime, null, poolName);
    }
    
    @Override
    public void executeAsynchronously(final String taskClassName, final long delay, final Properties taskProps, final Row auditRow, final int transactionTime, final Object userObj, String poolName) throws Exception {
        final long scheduleTime = System.currentTimeMillis() + delay;
        final TaskContext context = new TaskContext();
        context.setUserObject(userObj);
        final DataObject data = (DataObject)new WritableDataObject();
        if (auditRow != null) {
            data.addRow(auditRow);
            DataAccess.fillGeneratedValues(data);
            context.setAuditRow(auditRow);
        }
        Row row = new Row("TaskEngine_Task");
        row.set(3, (Object)taskClassName);
        data.addRow(row);
        final Object taskEngID = row.get(1);
        row = new Row("Task_Input");
        row.set("TASK_ID", taskEngID);
        row.set("SCHEDULE_TIME", (Object)new Timestamp(scheduleTime));
        final Object taskInputID = row.get("INSTANCE_ID");
        data.addRow(row);
        if (taskProps != null && taskProps.size() > 0) {
            for (final String key : ((Hashtable<Object, V>)taskProps).keySet()) {
                final String value = taskProps.getProperty(key);
                row = new Row("Default_Task_Input");
                row.set("INSTANCE_ID", taskInputID);
                row.set("VARIABLE_NAME", (Object)key);
                row.set("VARIABLE_VALUE", (Object)value);
                data.addRow(row);
            }
        }
        context.setAsync(true);
        context.setTaskInputDO(data);
        context.setTransactionTime(transactionTime);
        if (poolName == null) {
            poolName = "asynchThreadPool";
        }
        if (PersistenceUtil.getPoolID(poolName) == null) {
            throw new IllegalArgumentException("No such Thread pool exists : " + poolName);
        }
        TimeMap.getInstance(poolName).addToTimeMap(null, scheduleTime, null, context);
    }
    
    @Override
    public void executeAsynchronously(final String taskClassName, final long delay, final Properties taskProps, final Row auditRow, final int transactionTime) throws Exception {
        this.executeAsynchronously(taskClassName, delay, taskProps, auditRow, transactionTime, null, null);
    }
    
    @Override
    public void executeAsynchronously(final String taskClassName, final long delay, final Properties taskProps, final Row auditRow, final int transactionTime, final Object userObj) throws Exception {
        this.executeAsynchronously(taskClassName, delay, taskProps, auditRow, transactionTime, userObj, null);
    }
    
    @Override
    public DataObject scheduleTask(final String scheduleName, final String taskName, final DataObject input) throws Exception {
        final DataObject scheduledTaskDO = PersistenceUtil.getScheduledTaskFromCache(scheduleName, taskName);
        final Row stRow = scheduledTaskDO.getRow("Scheduled_Task");
        if (stRow != null) {
            final int rescheduleMode = (int)stRow.get(4);
            final int offset = (int)stRow.get(3);
            final int transactionTime = (int)stRow.get(7);
            return this.scheduleTask(scheduleName, taskName, input, rescheduleMode, offset, transactionTime);
        }
        return this.scheduleTask(scheduleName, taskName, input, 2, 100, 0);
    }
    
    @Override
    public DataObject scheduleTask(final String scheduleName, final String taskName, final DataObject taskInput, final int rescheduleMode, final int offset) throws Exception {
        final DataObject scheduledTaskDO = PersistenceUtil.getScheduledTaskFromCache(scheduleName, taskName);
        final Row stRow = scheduledTaskDO.getRow("Scheduled_Task");
        if (stRow != null) {
            final int transactionTime = (int)stRow.get(7);
            return this.scheduleTask(scheduleName, taskName, taskInput, rescheduleMode, offset, transactionTime);
        }
        return this.scheduleTask(scheduleName, taskName, taskInput, rescheduleMode, offset, 0);
    }
    
    @Override
    public DataObject scheduleTask(final String scheduleName, final String taskName, final DataObject taskInput, final int rescheduleMode, final int offset, final int transactionTime) throws Exception {
        return this.scheduleTask(scheduleName, taskName, taskInput, rescheduleMode, offset, transactionTime, null);
    }
    
    @Override
    public DataObject scheduleTask(final String scheduleName, final String taskName, DataObject taskInput, final int rescheduleMode, final int offset, final int transactionTime, String poolName) throws Exception {
        if (scheduleName == null) {
            throw new Exception("The Schedule Name is null");
        }
        if (taskName == null) {
            throw new Exception("The Task Name is null");
        }
        if (taskInput == null) {
            throw new Exception("The TaskInput is null");
        }
        final Long scheduleID = PersistenceUtil.getIDByNameForSchedule(scheduleName);
        final Long taskID = PersistenceUtil.getIDByNameForTask(taskName);
        PersistenceUtil.checkAndAddScheduledTask(scheduleID, taskID, rescheduleMode, offset, transactionTime);
        final DataObject schedule = PersistenceUtil.getSchedule(scheduleID, true);
        Long time = ScheduleUtil.calculateNextScheduleTime(schedule, -1L, false);
        if (time == -1L) {
            this.out.log(Level.SEVERE, "TaskInput is not scheduled as time is less than currentTime");
            return taskInput;
        }
        time += PersistenceUtil.getOffsetForScheduledTask(scheduleID, taskID);
        final Row taskInputRow = taskInput.getFirstRow("Task_Input");
        taskInputRow.set("SCHEDULE_ID", (Object)scheduleID);
        taskInputRow.set("TASK_ID", (Object)taskID);
        taskInputRow.set("SCHEDULE_TIME", (Object)new Timestamp(time));
        if (taskInputRow.get("POOL_ID") != null && poolName != null) {
            final long pool_id = (long)taskInputRow.get("POOL_ID");
            final String pName = PersistenceUtil.getPoolName(pool_id);
            if (!poolName.equalsIgnoreCase(pName)) {
                throw new IllegalArgumentException("PoolName mismatched for the specified poolID . It's not advisable to mention both poolID and poolName");
            }
        }
        else if (taskInputRow.get("POOL_ID") != null) {
            final long pool_id = (long)taskInputRow.get("POOL_ID");
            poolName = PersistenceUtil.getPoolName(pool_id);
            if (poolName == null) {
                throw new Exception(pool_id + " doesn't exist in Threadpools.xml ... ");
            }
        }
        else {
            if (poolName == null) {
                poolName = "default";
            }
            if (PersistenceUtil.getPoolID(poolName) == null) {
                throw new Exception(poolName + " doesn't exist in Threadpools.xml ... ");
            }
            taskInputRow.set("POOL_ID", (Object)PersistenceUtil.getPoolID(poolName));
        }
        final String threadPoolName = this.getPoolNameForTaskInput(taskInputRow);
        taskInput.updateRow(taskInputRow);
        taskInput = PersistenceUtil.getPersistence().add(taskInput);
        TimeMap.getInstance(threadPoolName).listener.newTaskAdded(taskInput);
        return taskInput;
    }
    
    @Override
    public void unscheduleTask(final String scheduleName, final String taskName) throws Exception {
        if (taskName == null) {
            throw new Exception("The Task Name is null");
        }
        if (scheduleName == null) {
            throw new Exception("The ScheduleName is null");
        }
        final Long scheduleID = PersistenceUtil.getIDByNameForSchedule(scheduleName);
        final Long taskID = PersistenceUtil.getIDByNameForTask(taskName);
        this.stopTasks(PersistenceUtil.getAllTaskInput(scheduleID, taskID));
        this.unscheduleTask(scheduleID, taskID);
    }
    
    @Override
    public void unscheduleTask(final long scheduleID, final long taskID) throws Exception {
        PersistenceUtil.unscheduleTask(scheduleID, taskID);
    }
    
    @Override
    public void setScheduledTaskSkipStatus(final String scheduleName, final String taskName, final boolean skipStatus) throws Exception {
        final long scheduleID = PersistenceUtil.getIDByNameForSchedule(scheduleName);
        final long taskID = PersistenceUtil.getIDByNameForTask(taskName);
        this.setScheduledTaskSkipStatus(scheduleID, taskID, skipStatus);
    }
    
    @Override
    public void setScheduledTaskSkipStatus(final long scheduleID, final long taskID, final boolean skipStatus) throws Exception {
        final Boolean newSkipStatus = skipStatus;
        final DataObject scheduledTask = PersistenceUtil.getScheduledTask(scheduleID, taskID);
        final Row row = scheduledTask.getFirstRow("Scheduled_Task");
        row.set("SKIP_MISSED_SCHEDULE", (Object)newSkipStatus);
        scheduledTask.updateRow(row);
        PersistenceUtil.getPersistence().update(scheduledTask);
    }
    
    @Override
    public boolean getScheduledTaskSkipStatus(final String scheduleName, final String taskName) throws Exception {
        final DataObject scheduledTask = PersistenceUtil.getScheduledTaskFromCache(scheduleName, taskName);
        final Row tempRow = scheduledTask.getFirstRow("Scheduled_Task");
        return (boolean)tempRow.get("SKIP_MISSED_SCHEDULE");
    }
    
    @Override
    public int getScheduledTaskStatus(final String scheduleName, final String taskName, final String statusName) throws Exception {
        final DataObject scheduledTask = PersistenceUtil.getScheduledTaskFromCache(scheduleName, taskName);
        final Row tempRow = scheduledTask.getFirstRow("Scheduled_Task");
        return (int)tempRow.get(statusName);
    }
    
    @Override
    public void setScheduledTaskAdminStatus(final String scheduleName, final String taskName, final int adminStatus) throws Exception {
        final long scheduleID = PersistenceUtil.getIDByNameForSchedule(scheduleName);
        final long taskID = PersistenceUtil.getIDByNameForTask(taskName);
        this.setScheduledTaskAdminStatus(scheduleID, taskID, adminStatus);
    }
    
    @Override
    public void setScheduledTaskAdminStatus(final long scheduleID, final long taskID, final int adminStatus) throws Exception {
        final List tableNames = new ArrayList();
        tableNames.add("Scheduled_Task");
        tableNames.add("Task_Input");
        Criteria criteria = new Criteria(Column.getColumn("Scheduled_Task", "SCHEDULE_ID"), (Object)scheduleID, 0);
        criteria = criteria.and(Column.getColumn("Scheduled_Task", "TASK_ID"), (Object)taskID, 0);
        final DataObject dataObject = PersistenceUtil.getPersistence().get(tableNames, tableNames, criteria);
        if (!dataObject.isEmpty()) {
            final Row schTaskRow = dataObject.getRow("Scheduled_Task");
            schTaskRow.set(5, (Object)adminStatus);
            dataObject.updateRow(schTaskRow);
            final Iterator iterator = dataObject.getRows("Task_Input");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                this.setTaskInputAdminStatus((long)row.get("INSTANCE_ID"), adminStatus);
            }
            PersistenceUtil.getPersistence().update(dataObject);
        }
    }
    
    @Override
    public int getScheduledTaskAdminStatus(final String scheduleName, final String taskName) throws Exception {
        return this.getScheduledTaskStatus(scheduleName, taskName, "ADMIN_STATUS");
    }
    
    @Override
    public int getTaskInputStatus(final long instanceID, final String statusName) throws Exception {
        final DataObject ti = PersistenceUtil.getTaskInput(instanceID);
        final Row tiRow = ti.getFirstRow("Task_Input");
        return (int)tiRow.get(statusName);
    }
    
    private String getPoolNameForTaskInput(final Row taskInputRow) throws Exception {
        if (taskInputRow != null && taskInputRow.getTableName().equals("Task_Input")) {
            final Long poolID = (Long)taskInputRow.get("POOL_ID");
            return PersistenceUtil.getPoolName(poolID);
        }
        throw new IllegalArgumentException("The row object should be of Task_Input table");
    }
    
    @Override
    public void setTaskInputAdminStatus(final long id, final int adminStatusInt) throws Exception {
        final Long instanceID = id;
        final Integer adminStatus = adminStatusInt;
        final DataObject taskInputDO = (DataObject)PersistenceUtil.getTaskInputDO(instanceID).clone();
        if (taskInputDO.isEmpty()) {
            this.out.log(Level.SEVERE, "Either there is no such Task_Input row found with the INSTANCE_ID :: [{0}]" + id);
        }
        final Row taskInputRow = taskInputDO.getFirstRow("Task_Input");
        final Integer oldAdminStatus = (Integer)taskInputRow.get("ADMIN_STATUS");
        if (adminStatus == oldAdminStatus) {
            this.out.log(Level.INFO, "Since the oldAdminStatus and the newAdminStatus are same the setTaskInputAdminStatus has returned without doing any change for the taskInput :: [{0}], newAdminStatus :: [{1}]", new Object[] { taskInputRow, adminStatus });
        }
        final String poolName = this.getPoolNameForTaskInput(taskInputRow);
        taskInputRow.set("ADMIN_STATUS", (Object)adminStatus);
        final boolean shouldRunNow = this.isEqual(3, adminStatus);
        final boolean wasRunningPreviously = this.isEqual(3, oldAdminStatus);
        if (!wasRunningPreviously && shouldRunNow) {
            final long schTime = ScheduleUtil.calculateNextScheduleTime(taskInputDO, -1L, true);
            if (schTime != -1L) {
                taskInputRow.set("SCHEDULE_TIME", (Object)new Timestamp(schTime));
            }
            taskInputDO.updateRow(taskInputRow);
            PersistenceUtil.getPersistence().update(taskInputDO);
            TimeMap.getInstance(poolName).addToTimeMap(taskInputDO);
        }
        else if (wasRunningPreviously && !shouldRunNow) {
            TimeMap.getInstance(poolName).removeTaskFromTimeMap(instanceID);
            taskInputDO.updateRow(taskInputRow);
            PersistenceUtil.getPersistence().update(taskInputDO);
        }
    }
    
    @Override
    public int getTaskInputAdminStatus(final long instanceID) throws Exception {
        return this.getTaskInputStatus(instanceID, "ADMIN_STATUS");
    }
    
    @Override
    public void unscheduleTaskInput(final long instanceID) throws Exception {
        final DataObject data = PersistenceUtil.getTaskInput(instanceID);
        if (!data.isEmpty()) {
            this.stopTasks(data);
            PersistenceUtil.getPersistence().delete(data.getRow("Task_Input"));
        }
    }
    
    @Override
    public void unscheduleTaskInput(final DataObject taskInputDO) throws Exception {
        if (taskInputDO == null) {
            throw new Exception("The TaskInput is null");
        }
        final Row tiRow = taskInputDO.getRow("Task_Input");
        if (tiRow != null) {
            this.stopTasks(taskInputDO);
            PersistenceUtil.getPersistence().delete(tiRow);
        }
    }
    
    @Override
    public void updateTaskInput(final DataObject taskInput) throws Exception {
        if (taskInput != null && taskInput.getRow("Task_Input") != null) {
            PersistenceUtil.getPersistence().update(taskInput);
            final Row row = taskInput.getRow("Task_Input");
            final Long instanceID = (Long)row.get("INSTANCE_ID");
            final Integer adminStatus = (Integer)row.get("ADMIN_STATUS");
            this.setTaskInputAdminStatus(instanceID, adminStatus);
        }
    }
    
    @Override
    public void setScheduledTaskAuditStatus(final String scheduleName, final String taskName, final boolean auditStatus) throws Exception {
        final long scheduleID = PersistenceUtil.getIDByNameForSchedule(scheduleName);
        final long taskID = PersistenceUtil.getIDByNameForTask(taskName);
        this.setScheduledTaskAuditStatus(scheduleID, taskID, auditStatus);
    }
    
    @Override
    public void setScheduledTaskAuditStatus(final long scheduleID, final long taskID, final boolean auditStatus) throws Exception {
        final Boolean newAuditStatus = auditStatus;
        final DataObject scheduledTask = PersistenceUtil.getScheduledTask(scheduleID, taskID);
        final Row row = scheduledTask.getFirstRow("Scheduled_Task");
        row.set("AUDIT_FLAG", (Object)newAuditStatus);
        scheduledTask.updateRow(row);
        PersistenceUtil.getPersistence().update(scheduledTask);
    }
    
    @Override
    public boolean getScheduledTaskAuditStatus(final String scheduleName, final String taskName) throws Exception {
        final DataObject scheduledTask = PersistenceUtil.getScheduledTaskFromCache(scheduleName, taskName);
        final Row tempRow = scheduledTask.getFirstRow("Scheduled_Task");
        return (boolean)tempRow.get("AUDIT_FLAG");
    }
    
    @Override
    public void setRemoveOnExpiryStatus(final String scheduleName, final String taskName, final boolean expiryStatus) throws Exception {
        final long scheduleID = PersistenceUtil.getIDByNameForSchedule(scheduleName);
        final long taskID = PersistenceUtil.getIDByNameForTask(taskName);
        this.setRemoveOnExpiryStatus(scheduleID, taskID, expiryStatus);
    }
    
    @Override
    public void setRemoveOnExpiryStatus(final long scheduleID, final long tskID, final boolean expiryStatus) throws Exception {
        final Boolean newExpiryStatus = expiryStatus;
        final DataObject scheduledTask = PersistenceUtil.getScheduledTask(scheduleID, tskID);
        final Row row = scheduledTask.getFirstRow("Scheduled_Task");
        row.set("REMOVE_ON_EXPIRY", (Object)newExpiryStatus);
        scheduledTask.updateRow(row);
        PersistenceUtil.getPersistence().update(scheduledTask);
    }
    
    @Override
    public boolean getRemoveOnExpiryStatus(final String scheduleName, final String taskName) throws Exception {
        final DataObject scheduledTask = PersistenceUtil.getScheduledTaskFromCache(scheduleName, taskName);
        final Row tempRow = scheduledTask.getFirstRow("Scheduled_Task");
        return (boolean)tempRow.get("REMOVE_ON_EXPIRY");
    }
    
    @Override
    public long createSchedule(final DataObject sch) throws Exception {
        return PersistenceUtil.addSchedule(sch);
    }
    
    private void stopTasks(final DataObject taskInputsDO) throws Exception {
        final Iterator iterator = taskInputsDO.getRows("Task_Input");
        while (iterator.hasNext()) {
            final Row taskInputRow = iterator.next();
            this.stopTask(taskInputRow);
        }
    }
    
    private void stopTask(final Row taskInputRow) throws Exception {
        final Long instanceID = (Long)taskInputRow.get("INSTANCE_ID");
        final String poolName = this.getPoolNameForTaskInput(taskInputRow);
        final TaskContext context = TimeMap.getInstance(poolName).getTaskContextFromExecutingTasks(instanceID);
        if (context != null) {
            context.setAdminStatus(false);
            context.getTaskInstance().stopTask();
        }
        final Long previousScheduledTime = TimeMap.getInstance(poolName).removeTaskFromTimeMap(instanceID);
        if (previousScheduledTime != null) {
            this.out.log(Level.FINE, "removeSchedule :: TaskInput instanceID :: [{0}] has been removed from TimeMap which has been scheduled to execute at [{1}]", new Object[] { instanceID, new Date(previousScheduledTime) });
        }
    }
    
    @Override
    public void removeSchedule(final long scheduleID) throws Exception {
        final DataObject allTaskInputs = PersistenceUtil.getAllTaskInput(scheduleID);
        this.out.log(Level.FINE, "allTaskInputs :: [{0}]", allTaskInputs);
        this.stopTasks(allTaskInputs);
        PersistenceUtil.removeSchedule(scheduleID);
    }
    
    @Override
    public void updateSchedule(final DataObject schedule) throws Exception {
        final DataObject dob = PersistenceUtil.updateSchedule(schedule);
        this.out.log(Level.FINE, "dob :: [{0}]", dob);
        final Long scheduleId = (Long)dob.getFirstValue("Schedule", "SCHEDULE_ID");
        final Long prevExecTime = -1L;
        final Long timeObj = ScheduleUtil.calculateNextScheduleTime(schedule, prevExecTime, true);
        this.out.log(Level.FINE, "timeObj :: [{0}]", new Date(timeObj));
        final DataObject taskInput = PersistenceUtil.getAllTaskInput(scheduleId);
        this.out.log(Level.FINE, "taskInput :: [{0}]", taskInput);
        this.updateScheduleTimeInTaskInput(taskInput, timeObj);
    }
    
    private void updateScheduleTimeInTaskInput(final DataObject taskInput, final Long scheduleTime) throws Exception {
        final Iterator taskInputIter = taskInput.getRows("Task_Input");
        while (taskInputIter.hasNext()) {
            final Row taskInputRow = taskInputIter.next();
            final List tableList = taskInput.getTableNames();
            final DataObject newTaskInput = (DataObject)taskInput.getDataObject(tableList, taskInputRow).clone();
            final String poolName = this.getPoolNameForTaskInput(taskInputRow);
            final Row newTaskInputRow = newTaskInput.getRow("Task_Input");
            final long time = scheduleTime;
            newTaskInputRow.set("SCHEDULE_TIME", (Object)((time == -1L) ? null : new Timestamp(time)));
            newTaskInput.updateRow(newTaskInputRow);
            final Long previousScheduledTime = TimeMap.getInstance(poolName).removeTaskFromTimeMap((Long)taskInputRow.get("INSTANCE_ID"));
            TimeMap.getInstance(poolName).addToTimeMap(newTaskInput);
            if (previousScheduledTime != null) {
                this.out.log(Level.FINE, "Removed the task from the thread pool :: [{0}] scheduled at :: [{1}] and added to execute at :: [{2}]", new Object[] { poolName, new Date(previousScheduledTime), new Date(scheduleTime) });
            }
            TaskEngineService.updateController.batchUpdate();
            PersistenceUtil.getPersistence().update(newTaskInput);
        }
    }
    
    @Override
    public void stopScheduler() throws Exception {
        final TaskEngineService taskEngineService = (TaskEngineService)ServiceUtil.lookup("TaskEngineService");
        taskEngineService.stop();
    }
    
    @Override
    public List getAllTaskInput(final String scheduleName, final String taskName) throws Exception {
        if (scheduleName == null) {
            throw new Exception("The Schedule Name is null");
        }
        if (taskName == null) {
            throw new Exception("The Task Name is null");
        }
        final long id = PersistenceUtil.getIDByNameForSchedule(scheduleName);
        final long id2 = PersistenceUtil.getIDByNameForTask(taskName);
        return this.getAllTaskInput(id, id2);
    }
    
    @Override
    public List getAllTaskInput(final Long scheduleID, final Long taskID) throws Exception {
        if (scheduleID == null) {
            throw new Exception("The Schedule ID is null");
        }
        if (taskID == null) {
            throw new Exception("The Task ID is null");
        }
        final DataObject taskInput = PersistenceUtil.getAllTaskInput(scheduleID, taskID);
        final List returnList = new ArrayList();
        final Iterator it = taskInput.getRows("Task_Input");
        while (it.hasNext()) {
            final Row tempRow = it.next();
            final List s = new ArrayList();
            s.add("Task_Input");
            s.add("Default_Task_Input");
            final DataObject newObject = taskInput.getDataObject(s, tempRow);
            newObject.updateRow(tempRow);
            returnList.add(newObject);
        }
        return returnList;
    }
    
    @Override
    public DataObject getAllSchedules() throws Exception {
        return PersistenceUtil.getAllSchedules();
    }
    
    @Override
    public void setBatchProperties(final long tolerance, final long period) throws Exception {
        TaskEngineService.setBatchUpdatePeriod(period);
        TaskEngineService.updateController.setToleranceLevel(tolerance);
    }
    
    @Override
    public TaskContext executeTask(final TaskContext taskContext) throws Throwable {
        final Task instance = taskContext.getTaskInstance();
        taskContext.setExecutionStartTime(System.currentTimeMillis());
        final boolean auditFlag = taskContext.getAuditRow() != null;
        if (taskContext.getAuditRow() != null) {
            this.audit(taskContext, 1, "PROCESSING");
        }
        boolean isFailed = false;
        MonitoringScheduler monschedule = null;
        final String config = PersistenceInitializer.getConfigurationValue("MonitoringConfiguration");
        try {
            if (config != null && config.equals("yes")) {
                final String monschedulerbean = "com.zoho.taskengine.internal.MonitoringSchedulerBean";
                monschedule = (MonitoringScheduler)Thread.currentThread().getContextClassLoader().loadClass(monschedulerbean).newInstance();
            }
            else {
                this.out.log(Level.FINE, "Configuration not set. Hence Monitoring cannot be enabled");
            }
        }
        catch (final UnsupportedClassVersionError uscve) {
            this.out.log(Level.INFO, "Monitoring cannot be enable in JDK5");
        }
        if (monschedule != null) {
            monschedule.initializeInstrumentation(taskContext);
        }
        long executionFinishTime = 0L;
        try {
            taskContext.setTaskIsExecuting();
            instance.executeTask(taskContext);
            if (monschedule != null) {
                monschedule.finishInstrumentation(0, null);
            }
            executionFinishTime = System.currentTimeMillis();
        }
        catch (final Throwable th) {
            isFailed = true;
            if (monschedule != null) {
                monschedule.finishInstrumentation(1, th);
            }
            throw th;
        }
        finally {
            taskContext.setExecutionEndTime(System.currentTimeMillis());
            final UpdateQuery updateTask_InputRow = (UpdateQuery)new UpdateQueryImpl("Task_Input");
            updateTask_InputRow.setUpdateColumn("EXECUTION_START_TIME", (Object)new Timestamp(taskContext.getExecutionStartTime()));
            if (executionFinishTime != 0L) {
                updateTask_InputRow.setUpdateColumn("EXECUTION_FINISH_TIME", (Object)new Timestamp(executionFinishTime));
            }
            updateTask_InputRow.setCriteria(new Criteria(Column.getColumn("Task_Input", "INSTANCE_ID"), (Object)taskContext.getID(), 0));
            DataAccess.update(updateTask_InputRow);
            if (auditFlag) {
                this.audit(taskContext, 2, isFailed ? "FAILURE" : "SUCCESS");
            }
        }
        return taskContext;
    }
    
    @Override
    public void reschedule(final TaskContext context, final String execStatus) throws Exception {
        final Long instanceID = context.getID();
        final Row schRow = context.getScheduledTaskRow();
        final Row tiRow = context.getTaskInputRow();
        final DataObject schedule = context.getTaskInputDO();
        final boolean expiryFlag = (boolean)schRow.get(10);
        final Integer scheduleMode = (Integer)schRow.get(4);
        Long scheduleTime = null;
        if (context.getScheduleType() == 6) {
            scheduleTime = context.getActualScheduleTime();
        }
        else if (schedule.getRow("Calendar") != null && schedule.getRow("Calendar_Periodicity") == null) {
            scheduleTime = context.getCurrentScheduleTime();
            if (scheduleTime < 1L) {
                System.out.println("scheduleTime is less than 1 hence it is set as -1");
                scheduleTime = -1L;
            }
        }
        else if (scheduleMode == 1) {
            scheduleTime = context.getExecutionStartTime();
        }
        else if (scheduleMode == 2) {
            scheduleTime = context.getExecutionEndTime();
        }
        else {
            scheduleTime = context.getCurrentScheduleTime();
        }
        Long nextScheduleTime = null;
        long retryScheduleTime = -1L;
        if (execStatus.equalsIgnoreCase("SUCCESS")) {
            if (context.getRetryAttempt() != 0) {
                context.resetRetryAttempt();
                nextScheduleTime = ScheduleUtil.calculateNextScheduleTime(schedule, (scheduleTime == -1L) ? scheduleTime : ((Timestamp)tiRow.get("SCHEDULE_TIME")).getTime(), true, context.getScheduleType());
            }
            else {
                nextScheduleTime = ScheduleUtil.calculateNextScheduleTime(schedule, scheduleTime, true, context.getScheduleType());
            }
            context.setCurrentScheduleTime(scheduleTime);
            context.setScheduleType(5);
            TimeMap.getInstance(context.getPoolName()).removeExecutingTask(context);
        }
        else {
            final String retryHandlerClassName = (String)schRow.get(11);
            if (retryHandlerClassName != null) {
                context.incrementRetryAttempt();
                final ScheduleRetryHandler retryHandler = PersistenceUtil.getClass(retryHandlerClassName).newInstance();
                retryScheduleTime = retryHandler.getNextScheduleTime(context, scheduleTime);
            }
            if (retryScheduleTime == -1L) {
                context.resetRetryAttempt();
                nextScheduleTime = ScheduleUtil.calculateNextScheduleTime(schedule, scheduleTime, true, context.getScheduleType());
                context.setCurrentScheduleTime(scheduleTime);
                context.setScheduleType(5);
            }
            else {
                nextScheduleTime = retryScheduleTime;
            }
        }
        if (context.isAdminStatusEnabled() && nextScheduleTime != -1L && (nextScheduleTime < System.currentTimeMillis() + 900000L || execStatus.equalsIgnoreCase("FAILURE"))) {
            if (context.getRetryAttempt() > 0) {
                this.out.log(Level.INFO, "Task ID :: [{0}], Retry Count :: [{1}], Next Excecution Time :: [{2}] ", new Object[] { context.getTaskID(), context.getRetryAttempt(), nextScheduleTime });
            }
            this.out.log(Level.FINE, "instanceID [{0}] has been added once again in TimeMap for the nextScheduleTime :: [{1}]", new Object[] { instanceID, new Date(nextScheduleTime) });
            if (context.getRetryAttempt() != 0) {
                this.out.log(Level.INFO, "Adding to TimeMap with the same context.");
                TimeMap.getInstance(context.getPoolName()).removeExecutingTask(context);
                TimeMap.getInstance(context.getPoolName()).addToTimeMap(instanceID, nextScheduleTime, context);
            }
            else {
                this.out.log(Level.INFO, "Adding to TimeMap with different context.");
                TimeMap.getInstance(context.getPoolName()).removeExecutingTask(context);
                TimeMap.getInstance(context.getPoolName()).addToTimeMap(instanceID, nextScheduleTime);
            }
        }
        this.out.log(Level.FINE, "instanceID :: [{0}] has been rescheduled at [{1}]", new Object[] { instanceID, new Date(nextScheduleTime) });
        if (context.getRetryAttempt() == 0) {
            TaskEngineService.updateController.addToBatch(nextScheduleTime, instanceID);
            if (expiryFlag && nextScheduleTime == -1L) {
                this.out.log(Level.FINE, "Task_Input instance removed as it got expired ", schRow);
                PersistenceUtil.getPersistence().delete(tiRow);
            }
        }
    }
    
    private boolean isEqual(final Integer num1, final Integer num2) {
        return num1.equals(num2);
    }
    
    @Override
    public void audit(final TaskContext taskContext, final int auditType, final String taskState) throws Exception {
        Row auditRow = taskContext.getAuditRow();
        DataObject auditObject = null;
        auditObject = PersistenceUtil.getPersistence().constructDataObject();
        if (auditType == 1) {
            if (!taskContext.getPoolName().equalsIgnoreCase("asynchThreadPool")) {
                final Long taskID = taskContext.getTaskID();
                auditRow.set(7, (Object)taskID);
                final Long scheduleID = taskContext.getScheduleID();
                auditRow.set("SCHEDULE_ID", (Object)scheduleID);
                final String scheduleName = (String)taskContext.getTaskInputDO().getRow("Schedule").get(2);
                auditRow.set(3, (Object)scheduleName);
                final Long instanceID = taskContext.getID();
                auditRow.set(4, (Object)instanceID);
                final String taskName = (String)taskContext.getTaskRow().get(2);
                auditRow.set(8, (Object)taskName);
            }
            else {
                auditRow.set(3, (Object)"ASYNCHRONOUS TASK");
                auditRow.set(8, (Object)taskContext.getTaskRow().get(3));
            }
            final Long scheduleStartTime = taskContext.getExecutionStartTime();
            auditRow.set(5, (Object)new Timestamp(scheduleStartTime));
            auditRow.set(6, (Object)new Timestamp(0L));
            auditRow.set(11, (Object)new Timestamp(taskContext.getCurrentScheduleTime()));
            if (taskContext.getRetryAttempt() == 0) {
                auditRow.set("TASK_EXECUTION_STATUS", (Object)taskState);
            }
            else {
                auditRow.set("TASK_EXECUTION_STATUS", (Object)("RETRY_" + taskContext.getRetryAttempt() + "_" + taskState));
            }
            auditObject.addRow(auditRow);
        }
        else {
            auditObject.addRow(auditRow);
            ((WritableDataObject)auditObject).clearOperations();
            auditRow.set(6, (Object)new Timestamp(taskContext.getExecutionEndTime()));
            if (taskContext.getRetryAttempt() == 0) {
                auditRow.set(9, (Object)taskState);
            }
            else {
                auditRow.set(9, (Object)("RETRY_" + taskContext.getRetryAttempt() + "_" + taskState));
            }
            auditRow.set(10, (Object)taskContext.getAuditStatusMessage());
            auditObject.updateRow(auditRow);
        }
        final DataObject extdAudit = taskContext.getAuditInfo();
        if (extdAudit != null) {
            List tableNames = extdAudit.getTableNames();
            tableNames = com.adventnet.persistence.PersistenceUtil.sortTables(tableNames);
            for (final String tableName : tableNames) {
                if (tableName.equals("Schedule_Audit")) {
                    continue;
                }
                final Iterator rowIter = extdAudit.getRows(tableName);
                com.adventnet.persistence.PersistenceUtil.addChildRowsIntoDO(auditObject, rowIter);
            }
        }
        this.out.log(Level.FINE, "The updated Audit oject is {0}", new Object[] { auditObject });
        PersistenceUtil.getPurePersistenceLiteNoTrans().update(auditObject);
        auditRow = auditObject.getRow("Schedule_Audit");
        taskContext.setAuditInfo(auditObject);
        if (auditType == 2) {
            taskContext.clearAuditRow();
        }
    }
    
    @Override
    public void updateNextScheduleTime(final UpdateQuery query) throws Exception {
        DataAccess.update(query);
    }
}
