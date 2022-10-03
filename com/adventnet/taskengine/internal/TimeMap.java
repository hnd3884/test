package com.adventnet.taskengine.internal;

import java.util.logging.Level;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.concurrent.TimeUnit;
import com.adventnet.taskengine.util.PersistenceUtil;
import java.sql.Timestamp;
import com.adventnet.taskengine.TaskExecutionException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.HashMap;
import com.adventnet.taskengine.TaskContext;
import java.util.List;
import java.util.logging.Logger;

public class TimeMap
{
    private Object mutex;
    TimeMapListener listener;
    private static Logger logger;
    private List<TaskContext> executingTasks;
    private HashMap<Long, TaskContext> idVsCtx;
    private HashMap<Long, ScheduledFuture<?>> idVsSf;
    public static int count;
    private String poolName;
    private int instanceId;
    private static HashMap<String, TimeMap> poolNameVsTimeMap;
    
    private TimeMap(final String poolName) {
        this.mutex = null;
        this.listener = null;
        this.executingTasks = new ArrayList<TaskContext>();
        this.idVsCtx = new HashMap<Long, TaskContext>();
        this.idVsSf = new HashMap<Long, ScheduledFuture<?>>();
        this.instanceId = 0;
        this.listener = new TimeMapListener();
        ++TimeMap.count;
        this.mutex = this;
        this.instanceId = TimeMap.count;
        this.setPoolName(poolName);
    }
    
    synchronized void removeExecutingTask(final TaskContext context) throws Exception {
        this.executingTasks.remove(context);
    }
    
    synchronized TaskContext getTaskContextFromExecutingTasks(final Long taskInputInstanceID) throws Exception {
        for (final TaskContext context : this.executingTasks) {
            if (context.getID() == (long)taskInputInstanceID) {
                return context;
            }
        }
        return null;
    }
    
    public void preponeTask(final Long instanceID) throws Exception {
        final TaskContext ctx = new TaskContext(instanceID);
        ctx.setCurrentScheduleTime(System.currentTimeMillis());
        ctx.setScheduleType(6);
        final Long scheduleTime = ctx.getCurrentScheduleTime();
        if (!this.listener.taskAdded(ctx) || ctx.isExecuting()) {
            return;
        }
        synchronized (this.mutex) {
            Long key = ctx.getActualScheduleTime();
            if (key == null) {
                final DataObject taskInputDO = DataAccess.get("Task_Input", new Criteria(Column.getColumn("Task_Input", "INSTANCE_ID"), (Object)instanceID, 0));
                if (taskInputDO.isEmpty()) {
                    throw new TaskExecutionException("No task has been scheduled with this instanceID [" + instanceID + "], hence it cannot be preponed");
                }
                final Row taskRow = taskInputDO.getRow("Task_Input", (Criteria)null);
                key = ((Timestamp)taskRow.get("SCHEDULE_TIME")).getTime();
            }
            ctx.setActualScheduleTime(key);
            final ScheduledThreadPoolExecutor executor = PersistenceUtil.getExecutor(this.poolName);
            final Long delay = scheduleTime - System.currentTimeMillis();
            executor.schedule(new ScheduleExecutor(this.poolName, ctx), delay, TimeUnit.MILLISECONDS);
            ctx.setCurrentScheduleTime(scheduleTime);
            this.executingTasks.add(ctx);
        }
    }
    
    public boolean addToTimeMap(final DataObject taskInput) throws Exception {
        final Row row = taskInput.getFirstRow("Task_Input");
        final Long instanceID = (Long)row.get("INSTANCE_ID");
        final Timestamp ts = (Timestamp)row.get("SCHEDULE_TIME");
        if (ts == null) {
            TimeMap.logger.log(Level.WARNING, "Unable to schedule this taskInput since the SCHEDULE_TIME in this taskInputDO is NULL :: [{0}]", taskInput);
            return false;
        }
        final Long scheduleTime = ts.getTime();
        if (scheduleTime == -1L) {
            TimeMap.logger.log(Level.INFO, "User Thread:Returned false as scheduleTime is -1");
            return false;
        }
        if (row.get("ADMIN_STATUS").equals(4)) {
            TimeMap.logger.log(Level.INFO, "NOT ADDEDTO TIME MAP Adminstatus failed");
            return false;
        }
        final long timeLimit = System.currentTimeMillis() + 900000L;
        return scheduleTime <= timeLimit && this.addToTimeMap(instanceID, scheduleTime);
    }
    
    public boolean addToTimeMap(final Long instanceID, final Long scheduleTime) throws Exception {
        return this.addToTimeMap(instanceID, scheduleTime, null, null);
    }
    
    public boolean addToTimeMap(final Long instanceID, final Long scheduleTime, final TaskContext context) throws Exception {
        return this.addToTimeMap(instanceID, scheduleTime, null, context);
    }
    
    public boolean addToTimeMap(final Long instanceID, final Long scheduleTime, final DataObject taskInput) throws Exception {
        return this.addToTimeMap(instanceID, scheduleTime, taskInput, null);
    }
    
    public boolean addToTimeMap(final Long instanceID, final Long scheduleTime, final DataObject taskInput, TaskContext ctx) throws Exception {
        if (ctx == null) {
            if (instanceID != null) {
                ctx = new TaskContext(instanceID);
            }
            else {
                ctx = new TaskContext();
            }
        }
        TimeMap.logger.log(Level.FINE, "Original Execution Time : {0} InstanceID : {1} ", new Object[] { scheduleTime, ctx.getID() });
        ctx.setCurrentScheduleTime(scheduleTime);
        if (!this.listener.taskAdded(ctx)) {
            return false;
        }
        synchronized (this.mutex) {
            if (this.executingTasks.contains(ctx)) {
                TimeMap.logger.log(Level.INFO, "Returning false as timemap/executingTasks contains {0}", instanceID);
                return false;
            }
            final ScheduledThreadPoolExecutor executor = PersistenceUtil.getExecutor(this.poolName);
            final Long delay = scheduleTime - System.currentTimeMillis();
            TimeMap.logger.log(Level.INFO, "PoolName : {0}  CorePoolSize : {1}  ActiveThreads : {2} ", new Object[] { this.poolName, executor.getCorePoolSize(), executor.getActiveCount() });
            if (executor.getCorePoolSize() == executor.getActiveCount()) {
                TimeMap.logger.log(Level.WARNING, " ThreadPool limit reached for {0} thread pool", new Object[] { this.poolName.toUpperCase() });
            }
            final ScheduledFuture<?> sf = executor.schedule(new ScheduleExecutor(this.poolName, ctx), delay, TimeUnit.MILLISECONDS);
            if (executor.getCorePoolSize() == executor.getActiveCount()) {
                TimeMap.logger.log(Level.FINE, "Waiting Schedules that are in queue After adding task (instanceID : {0}) are : \n {1}", new Object[] { instanceID, this.getWaitingSchedules() });
            }
            this.idVsCtx.put(instanceID, ctx);
            this.idVsSf.put(instanceID, sf);
            ctx.setCurrentScheduleTime(scheduleTime);
            this.executingTasks.add(ctx);
            ctx.setActualScheduleTime(scheduleTime);
            TimeMap.logger.log(Level.FINE, "Added task to scheduleTimeMap for execution : {0}", ctx);
        }
        return true;
    }
    
    public void setTimeMapListener(final TimeMapListener listener) {
        this.listener = listener;
    }
    
    public void view() throws Exception {
        TimeMap.logger.log(Level.INFO, "Executing Tasks:{0}", this.executingTasks.toString());
    }
    
    public int getSchedulesInQueueCount() throws Exception {
        int queueCount = 0;
        synchronized (this.mutex) {
            for (final TaskContext context : this.executingTasks) {
                if (!context.isExecuting()) {
                    ++queueCount;
                }
            }
        }
        return queueCount;
    }
    
    public Long removeTaskFromTimeMap(final Long instanceID) throws Exception {
        synchronized (this.mutex) {
            final TaskContext ctx = this.idVsCtx.get(instanceID);
            if (this.idVsSf.get(instanceID) == null && ctx == null) {
                return null;
            }
            TimeMap.logger.log(Level.INFO, "Trying to remove Task having isntanceID :[" + instanceID + "] whose context object is : [" + ctx + "] and ScheduleFuture is : [" + this.idVsSf.get(instanceID) + "]");
            this.executingTasks.remove(ctx);
            this.idVsCtx.remove(instanceID);
            this.idVsSf.get(instanceID).cancel(false);
            this.idVsSf.remove(instanceID);
            return ctx.getActualScheduleTime();
        }
    }
    
    public String getWaitingSchedules() {
        final List<TaskContext> waitingSchedules = new ArrayList<TaskContext>();
        synchronized (this.mutex) {
            for (final TaskContext context : this.executingTasks) {
                if (!context.isExecuting()) {
                    waitingSchedules.add(context);
                }
            }
        }
        final StringBuilder waitingSchedulesString = new StringBuilder();
        if (waitingSchedules.isEmpty()) {
            return "No waiting schedules.";
        }
        for (final TaskContext context : waitingSchedules) {
            waitingSchedulesString.append("ScheduleExecutor-" + this.poolName + ", id: " + context.getID() + ", ScheduledExecutionTime : " + new Timestamp(context.getActualScheduleTime()).toString() + "\n");
        }
        return waitingSchedulesString.toString();
    }
    
    public void setPoolName(final String name) {
        this.poolName = name;
    }
    
    public String getPoolName() {
        return this.poolName;
    }
    
    @Override
    public String toString() {
        return "TimeMap :: pool name : " + this.poolName + " instance id : " + this.instanceId;
    }
    
    public static TimeMap getInstance(String poolName) {
        if (poolName == null) {
            poolName = "default";
        }
        TimeMap timeMap = TimeMap.poolNameVsTimeMap.get(poolName);
        if (timeMap == null) {
            timeMap = new TimeMap(poolName);
            TimeMap.poolNameVsTimeMap.put(poolName, timeMap);
            TimeMap.logger.log(Level.FINE, "New TimeMap instance created for the poolName :: [{0}]", poolName);
        }
        return timeMap;
    }
    
    static {
        TimeMap.logger = Logger.getLogger(TimeMap.class.getName());
        TimeMap.count = 0;
        TimeMap.poolNameVsTimeMap = new HashMap<String, TimeMap>();
    }
}
