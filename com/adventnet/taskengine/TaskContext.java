package com.adventnet.taskengine;

import java.sql.Timestamp;
import java.util.Locale;
import com.adventnet.taskengine.util.PersistenceUtil;
import com.adventnet.persistence.DataAccessException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import java.util.logging.Logger;

public class TaskContext
{
    private static String className;
    private static Logger logger;
    private Long id;
    private Row taskEngineTaskRow;
    private Row schTaskRow;
    private Row taskInputRow;
    private DataObject auditObject;
    private Long execStartTime;
    private Long execEndTime;
    private Long actualScheduleTime;
    private Object env;
    private Long taskID;
    private Long scheduleID;
    private Long scheduleTime;
    private DataObject taskInputDO;
    private static AtomicLong asynchronousTaskID;
    private int scheduleType;
    private Object userObject;
    private String poolName;
    private boolean isAsynch;
    private boolean isEnabled;
    private String taskHandlerClass;
    private Row auditRow;
    private Long auditID;
    private String auditStatusMessage;
    private Task taskInstance;
    private int retryAttempt;
    private int transactionTime;
    private boolean isExecuting;
    private boolean isSuccess;
    
    public TaskContext(final Long id) {
        this.taskID = null;
        this.scheduleID = null;
        this.taskInputDO = null;
        this.scheduleType = 5;
        this.userObject = null;
        this.poolName = null;
        this.isAsynch = false;
        this.isEnabled = true;
        this.auditRow = null;
        this.auditID = null;
        this.auditStatusMessage = null;
        this.taskInstance = null;
        this.retryAttempt = 0;
        this.transactionTime = 0;
        this.isExecuting = false;
        this.isSuccess = false;
        this.id = id;
    }
    
    public TaskContext() {
        this.taskID = null;
        this.scheduleID = null;
        this.taskInputDO = null;
        this.scheduleType = 5;
        this.userObject = null;
        this.poolName = null;
        this.isAsynch = false;
        this.isEnabled = true;
        this.auditRow = null;
        this.auditID = null;
        this.auditStatusMessage = null;
        this.taskInstance = null;
        this.retryAttempt = 0;
        this.transactionTime = 0;
        this.isExecuting = false;
        this.isSuccess = false;
        this.id = TaskContext.asynchronousTaskID.incrementAndGet();
        this.poolName = "asynchThreadPool";
    }
    
    public int getScheduleType() {
        return this.scheduleType;
    }
    
    public void setScheduleType(final int scheduleType) {
        this.scheduleType = scheduleType;
    }
    
    public Long getID() {
        return this.id;
    }
    
    public Long getTaskID() {
        return this.taskID;
    }
    
    public Long getScheduleID() {
        return this.scheduleID;
    }
    
    public Long getCurrentScheduleTime() {
        return this.scheduleTime;
    }
    
    public void setCurrentScheduleTime(final Long scheduleTime) {
        this.scheduleTime = scheduleTime;
    }
    
    public Object getEnv() {
        return this.env;
    }
    
    public void setEnv(final Object obj) {
        this.env = obj;
    }
    
    public void setExecutionStartTime(final Long startTime) {
        this.execStartTime = startTime;
    }
    
    public void setExecutionEndTime(final Long endTime) {
        this.execEndTime = endTime;
    }
    
    public void setAuditInfo(final DataObject auditObject) {
        this.auditObject = auditObject;
    }
    
    public Long getExecutionStartTime() {
        return this.execStartTime;
    }
    
    public Long getExecutionEndTime() {
        return this.execEndTime;
    }
    
    public Row getTaskRow() {
        if (this.taskInputDO == null) {
            this.getTaskInputDO();
        }
        return this.taskEngineTaskRow;
    }
    
    public Row getScheduledTaskRow() {
        if (this.taskInputDO == null) {
            this.getTaskInputDO();
        }
        return this.schTaskRow;
    }
    
    public Iterator getDefaultTaskInputs() {
        try {
            if (this.taskInputDO == null) {
                this.getTaskInputDO();
            }
            return this.taskInputDO.getRows("Default_Task_Input");
        }
        catch (final DataAccessException dae) {
            TaskContext.logger.log(Level.SEVERE, "Exception occurred while TaskContext.getDefaultTaskInputs :: [{0}]", (Throwable)dae);
            return new ArrayList().iterator();
        }
    }
    
    public DataObject getAuditInfo() {
        return this.auditObject;
    }
    
    public void setTaskInputDO(final DataObject taskInput) {
        if (this.poolName.equalsIgnoreCase("asynchThreadPool")) {
            this.taskInputDO = taskInput;
            try {
                this.taskEngineTaskRow = taskInput.getRow("TaskEngine_Task");
                this.taskInputRow = this.taskInputDO.getRow("Task_Input");
                return;
            }
            catch (final DataAccessException dae) {
                throw new IllegalArgumentException("Exception occurred while setting taskInputDO [{0}]", (Throwable)dae);
            }
            throw new RuntimeException("TaskInputDO should be set only for AsynchronousTasks and not for other tasks");
        }
        throw new RuntimeException("TaskInputDO should be set only for AsynchronousTasks and not for other tasks");
    }
    
    public boolean isAdminStatusEnabled() {
        return this.isEnabled;
    }
    
    public void setAdminStatus(final boolean enabled) {
        this.isEnabled = enabled;
    }
    
    public DataObject getTaskInputDO() {
        if (this.taskInputDO == null) {
            if (this.poolName != null) {
                if (this.poolName.equalsIgnoreCase("asynchThreadPool")) {
                    return this.taskInputDO;
                }
            }
            try {
                this.taskInputDO = PersistenceUtil.getTaskInputDO(this.id);
                if (this.taskInputDO.isEmpty()) {
                    this.isEnabled = false;
                    TaskContext.logger.log(Level.FINE, "taskInputDO fetched for the instanceID is null");
                    return null;
                }
                TaskContext.logger.log(Level.FINE, "taskInputDO fetched from PersistenceUtil :: [{0}]", this.taskInputDO);
                this.taskID = (Long)this.taskInputDO.getRow("TaskEngine_Task").get(1);
                this.scheduleID = (Long)this.taskInputDO.getRow("Schedule").get(1);
                this.taskEngineTaskRow = this.taskInputDO.getRow("TaskEngine_Task");
                this.schTaskRow = this.taskInputDO.getRow("Scheduled_Task");
                this.transactionTime = (int)this.schTaskRow.get(7);
                this.taskInputRow = this.taskInputDO.getRow("Task_Input");
                final int schTask_AdminStatus = (int)this.schTaskRow.get(5);
                final int taskInp_AdminStatus = (int)this.taskInputRow.get("ADMIN_STATUS");
                this.isEnabled = (schTask_AdminStatus == 3 && taskInp_AdminStatus == 3);
                final Iterator it = this.taskInputDO.getRows("Default_Task_Input");
                while (it.hasNext()) {
                    final Row row = it.next();
                    final String key = row.get(2).toString().toLowerCase(Locale.ENGLISH);
                    if (key.equals("task.completion.handler")) {
                        this.taskHandlerClass = (String)row.get(4);
                        break;
                    }
                }
            }
            catch (final DataAccessException dae) {
                dae.printStackTrace();
                TaskContext.logger.log(Level.SEVERE, "Exception occurred while fetching the taskInputDO :: [{0}]", (Throwable)dae);
            }
        }
        return this.taskInputDO;
    }
    
    public void setActualScheduleTime(final Long actualScheduleTime) {
        this.actualScheduleTime = actualScheduleTime;
    }
    
    public Long getActualScheduleTime() {
        return this.actualScheduleTime;
    }
    
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof TaskContext) {
            final TaskContext tc = (TaskContext)obj;
            return this.id.equals(tc.id);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "TaskContext: id:" + this.id + " ScheduledExecutionTime: " + new Timestamp(this.actualScheduleTime).toString() + " env: " + this.env;
    }
    
    public void setUserObject(final Object object) {
        this.userObject = object;
    }
    
    public Object getUserObject() {
        return this.userObject;
    }
    
    public void setAuditRow(final Row auditRow) {
        this.auditRow = auditRow;
        this.auditID = (Long)this.auditRow.get(1);
    }
    
    public Row getAuditRow() {
        if (this.auditRow == null && !this.getPoolName().equalsIgnoreCase("asynchThreadPool") && (boolean)this.getScheduledTaskRow().get(6)) {
            this.auditRow = new Row("Schedule_Audit");
        }
        return this.auditRow;
    }
    
    public void clearAuditRow() {
        this.auditRow = null;
    }
    
    public Long getAuditID() {
        return this.auditID;
    }
    
    public void setAuditStatusMessage(final String auditStatusMsg) {
        this.auditStatusMessage = auditStatusMsg;
    }
    
    public String getAuditStatusMessage() {
        return this.auditStatusMessage;
    }
    
    public Task getTaskInstance() throws Exception {
        if (this.taskInstance == null) {
            final String className = (String)this.getTaskInputDO().getRow("TaskEngine_Task").get(3);
            final Class c = PersistenceUtil.getClass(className);
            this.taskInstance = c.newInstance();
        }
        return this.taskInstance;
    }
    
    public void resetRetryAttempt() {
        this.retryAttempt = 0;
    }
    
    public void incrementRetryAttempt() {
        ++this.retryAttempt;
    }
    
    public int getRetryAttempt() {
        return this.retryAttempt;
    }
    
    public Row getTaskInputRow() {
        return this.taskInputRow;
    }
    
    public String getPoolName() {
        if (this.poolName == null) {
            this.getTaskInputDO();
            try {
                final Long poolID = (Long)this.getTaskInputRow().get("POOL_ID");
                this.poolName = PersistenceUtil.getPoolName(poolID);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return this.poolName;
    }
    
    public void setTransactionTime(final int seconds) {
        if (this.isAsynch) {
            this.transactionTime = seconds;
            return;
        }
        throw new IllegalArgumentException("setTransactionTime can be set only for AsynchronousTasks");
    }
    
    public int getTransactionTime() {
        return this.transactionTime;
    }
    
    public void flushConfigurations() {
        this.taskInputDO = null;
    }
    
    public boolean isExecuting() {
        return this.isExecuting;
    }
    
    public void setTaskIsExecuting() {
        this.isExecuting = true;
    }
    
    public boolean isSuccess() {
        return this.isSuccess;
    }
    
    public void setExecutionStatus(final boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
    
    public TaskCompletionHandler getTaskHandlerInstance() throws Exception {
        if (this.taskHandlerClass != null) {
            final Class c = PersistenceUtil.getClass(this.taskHandlerClass);
            return c.newInstance();
        }
        return null;
    }
    
    public void setAsync(final boolean isAsync) {
        this.isAsynch = isAsync;
    }
    
    static {
        TaskContext.className = TaskContext.class.getName();
        TaskContext.logger = Logger.getLogger(TaskContext.className);
        TaskContext.asynchronousTaskID = new AtomicLong(0L);
    }
}
