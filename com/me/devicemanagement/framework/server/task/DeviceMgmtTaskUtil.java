package com.me.devicemanagement.framework.server.task;

import java.util.Hashtable;
import java.util.List;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Properties;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DeviceMgmtTaskUtil
{
    private static DeviceMgmtTaskUtil taskUtil;
    private Logger logger;
    
    public DeviceMgmtTaskUtil() {
        this.logger = Logger.getLogger(DeviceMgmtTaskUtil.class.getName());
    }
    
    public static DeviceMgmtTaskUtil getInstance() {
        if (DeviceMgmtTaskUtil.taskUtil == null) {
            DeviceMgmtTaskUtil.taskUtil = new DeviceMgmtTaskUtil();
        }
        return DeviceMgmtTaskUtil.taskUtil;
    }
    
    public DataObject getTaskDetailsDO(final Long taskID) throws DataAccessException {
        final Persistence persistence = SyMUtil.getPersistence();
        final Row taskRow = new Row("TaskDetails");
        taskRow.set("TASK_ID", (Object)taskID);
        final DataObject taskDetailsDO = persistence.get("TaskDetails", taskRow);
        return taskDetailsDO;
    }
    
    public DataObject getTaskDetailsRelDO(final Long taskID) throws DataAccessException {
        final Persistence persistence = SyMUtil.getPersistence();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("TaskDetails"));
        final Join taskCustJoin = new Join("TaskDetails", "TaskToCustomerRel", new String[] { "TASK_ID" }, new String[] { "TASK_ID" }, 1);
        final Join taskUserJoin = new Join("TaskDetails", "TaskToUserRel", new String[] { "TASK_ID" }, new String[] { "TASK_ID" }, 1);
        final Join scheduledTaskDetailsJoin = new Join("TaskDetails", "ScheduledTaskDetails", new String[] { "TASK_ID" }, new String[] { "TASK_ID" }, 1);
        selectQuery.addJoin(taskCustJoin);
        selectQuery.addJoin(taskUserJoin);
        selectQuery.addJoin(scheduledTaskDetailsJoin);
        final Column taskColumns = Column.getColumn("TaskDetails", "*");
        final Column taskCustColumns = Column.getColumn("TaskToCustomerRel", "*");
        final Column taskUserColumns = Column.getColumn("TaskToUserRel", "*");
        final Column scheduledTaskColumns = Column.getColumn("ScheduledTaskDetails", "*");
        selectQuery.addSelectColumn(taskColumns);
        selectQuery.addSelectColumn(taskCustColumns);
        selectQuery.addSelectColumn(taskUserColumns);
        selectQuery.addSelectColumn(scheduledTaskColumns);
        final Column criteriaColumn = Column.getColumn("TaskDetails", "TASK_ID");
        final Criteria criteria = new Criteria(criteriaColumn, (Object)taskID, 0);
        selectQuery.setCriteria(criteria);
        final DataObject taskDetailsDO = persistence.get(selectQuery);
        return taskDetailsDO;
    }
    
    public DataObject updateTaskDetails(final Long taskID, final Properties prop) throws DataAccessException {
        final Persistence persistence = SyMUtil.getPersistence();
        DataObject taskDetailsDO = this.getTaskDetailsDO(taskID);
        final String status = prop.getProperty("STATUS");
        final Long startTime = ((Hashtable<K, Long>)prop).get("STARTTIME");
        final Long completionTime = ((Hashtable<K, Long>)prop).get("COMPLETIONTIME");
        final String remarks = prop.getProperty("REMARKS");
        final Integer counter = ((Hashtable<K, Integer>)prop).get("COUNTER");
        final String taskName = prop.getProperty("TASKNAME");
        final Long modifiedTime = ((Hashtable<K, Long>)prop).get("modifiedTime");
        final Row taskDetailsRow = taskDetailsDO.getFirstRow("TaskDetails");
        if (taskName != null) {
            taskDetailsRow.set("TASKNAME", (Object)taskName);
        }
        if (status != null) {
            taskDetailsRow.set("STATUS", (Object)status);
        }
        if (startTime != null) {
            taskDetailsRow.set("STARTTIME", (Object)startTime);
        }
        if (completionTime != null) {
            taskDetailsRow.set("COMPLETIONTIME", (Object)completionTime);
        }
        if (modifiedTime != null) {
            taskDetailsRow.set("MODIFIEDTIME", (Object)modifiedTime);
        }
        if (remarks != null) {
            taskDetailsRow.set("REMARKS", (Object)remarks);
        }
        if (counter != null) {
            taskDetailsRow.set("COUNTER", (Object)counter);
        }
        if (prop.getProperty("userName") != null) {
            taskDetailsRow.set("OWNER", (Object)prop.getProperty("userName"));
        }
        taskDetailsDO.updateRow(taskDetailsRow);
        taskDetailsDO = persistence.update(taskDetailsDO);
        return taskDetailsDO;
    }
    
    public DataObject getSchTaskDetailsDO(final Long schedulerClassId) throws DataAccessException {
        final Persistence persistence = SyMUtil.getPersistence();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("TaskDetails"));
        final Join taskToScheduleJoin = new Join("TaskDetails", "ScheduledTaskDetails", new String[] { "TASK_ID" }, new String[] { "TASK_ID" }, 2);
        final Join schedulerClassJoin = new Join("ScheduledTaskDetails", "SchedulerClasses", new String[] { "SCHEDULER_CLASS_ID" }, new String[] { "SCHEDULER_CLASS_ID" }, 1);
        final Join taskCustJoin = new Join("TaskDetails", "TaskToCustomerRel", new String[] { "TASK_ID" }, new String[] { "TASK_ID" }, 1);
        final Join taskUserJoin = new Join("TaskDetails", "TaskToUserRel", new String[] { "TASK_ID" }, new String[] { "TASK_ID" }, 1);
        selectQuery.addJoin(taskToScheduleJoin);
        selectQuery.addJoin(schedulerClassJoin);
        selectQuery.addJoin(taskCustJoin);
        selectQuery.addJoin(taskUserJoin);
        final Column taskColumns = Column.getColumn("TaskDetails", "*");
        final Column scheduleColumns = Column.getColumn("ScheduledTaskDetails", "*");
        final Column schedulerClassColumns = Column.getColumn("SchedulerClasses", "*");
        final Column taskCustColumns = Column.getColumn("TaskToCustomerRel", "*");
        final Column taskUserColumns = Column.getColumn("TaskToUserRel", "*");
        selectQuery.addSelectColumn(taskColumns);
        selectQuery.addSelectColumn(scheduleColumns);
        selectQuery.addSelectColumn(taskCustColumns);
        selectQuery.addSelectColumn(taskUserColumns);
        selectQuery.addSelectColumn(schedulerClassColumns);
        final Column criteriaColumn = Column.getColumn("ScheduledTaskDetails", "SCHEDULER_CLASS_ID");
        final Criteria criteria = new Criteria(criteriaColumn, (Object)schedulerClassId, 0);
        selectQuery.setCriteria(criteria);
        final DataObject taskDetailsDO = persistence.get(selectQuery);
        return taskDetailsDO;
    }
    
    public DataObject getSchTaskDetailsDO(final String scheduleName) throws DataAccessException {
        Long schClassId = null;
        final Criteria c = new Criteria(Column.getColumn("SchedulerClasses", "SCHEDULER_NAME"), (Object)scheduleName, 0);
        final DataObject dObject = SyMUtil.getPersistence().get("SchedulerClasses", c);
        if (!dObject.isEmpty()) {
            schClassId = (Long)dObject.getFirstValue("SchedulerClasses", "SCHEDULER_CLASS_ID");
        }
        return (schClassId != null) ? this.getSchTaskDetailsDO(schClassId) : null;
    }
    
    public DataObject createTaskDetailsDO(final Properties prop) throws DataAccessException {
        final Persistence persistence = SyMUtil.getPersistence();
        final DataObject taskDetailsDO = persistence.constructDataObject();
        final Object opType = ((Hashtable<K, Object>)prop).get("operationType");
        String taskName = null;
        if (opType != null && opType instanceof String) {
            taskName = ((Hashtable<K, String>)prop).get("operationType");
        }
        else if (opType != null && opType instanceof Integer) {
            taskName = ((Hashtable<K, Integer>)prop).get("operationType").toString();
        }
        final Row taskDetailsRow = new Row("TaskDetails");
        if (taskName != null) {
            final int operationType = Integer.parseInt(taskName);
            taskName = this.getTaskName(operationType);
            taskDetailsRow.set("TYPE", (Object)new Integer(operationType));
            taskDetailsRow.set("TASKNAME", (Object)taskName);
        }
        else {
            taskDetailsRow.set("TYPE", ((Hashtable<K, Object>)prop).get("operationType"));
            taskDetailsRow.set("TASKNAME", (Object)"--");
        }
        final Long time = new Long(System.currentTimeMillis());
        taskDetailsRow.set("CREATIONTIME", (Object)time);
        taskDetailsRow.set("STARTTIME", (Object)time);
        taskDetailsRow.set("STATUS", (Object)"RUNNING");
        if (prop.getProperty("email") != null) {
            taskDetailsRow.set("EMAIL", (Object)prop.getProperty("email"));
        }
        if (prop.get("COUNTER") != null) {
            taskDetailsRow.set("COUNTER", ((Hashtable<K, Object>)prop).get("COUNTER"));
        }
        if (prop.getProperty("userName") != null) {
            taskDetailsRow.set("OWNER", (Object)prop.getProperty("userName"));
        }
        taskDetailsDO.addRow(taskDetailsRow);
        return persistence.add(taskDetailsDO);
    }
    
    public DataObject createTaskToCustDO(final Properties prop) throws DataAccessException {
        final Persistence persistence = SyMUtil.getPersistence();
        final Long taskId = ((Hashtable<K, Long>)prop).get("taskId");
        final ArrayList custIds = ((Hashtable<K, ArrayList>)prop).get("custIds");
        DataObject taskTocustDO = persistence.get("TaskToCustomerRel", new Criteria(new Column("TaskToCustomerRel", "TASK_ID"), (Object)taskId, 0).and(new Criteria(new Column("TaskToCustomerRel", "CUSTOMER_ID"), (Object)custIds.toArray(new Long[custIds.size()]), 8)));
        if (taskTocustDO.isEmpty()) {
            taskTocustDO = persistence.constructDataObject();
            for (int i = 0; i < custIds.size(); ++i) {
                final Long customerId = custIds.get(i);
                final Row taskToCustRow = new Row("TaskToCustomerRel");
                taskToCustRow.set("TASK_ID", (Object)taskId);
                taskToCustRow.set("CUSTOMER_ID", (Object)customerId);
                taskTocustDO.addRow(taskToCustRow);
            }
            return persistence.add(taskTocustDO);
        }
        return taskTocustDO;
    }
    
    public String getTaskName(final int operationType) {
        if (operationType == 106) {
            return "TrackEvaluationTask";
        }
        return "--";
    }
    
    public Integer getTaskDetails(final Integer operationType, final String status) {
        Integer count = 0;
        try {
            Criteria crit = null;
            if (operationType != 0 && status == null) {
                crit = new Criteria(Column.getColumn("TaskDetails", "TYPE"), (Object)operationType, 0);
            }
            if (status != null && operationType == 0) {
                crit = new Criteria(Column.getColumn("TaskDetails", "STATUS"), (Object)status, 0);
            }
            final SelectQuery sq = this.getTaskDetailsSQ(crit);
            final DataObject taskDO = SyMUtil.getPersistence().get(sq);
            if (!taskDO.isEmpty()) {
                count = taskDO.size("TaskDetails");
            }
            return count;
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception while getting count of operationtype to generate graph" + e.getMessage());
            return null;
        }
    }
    
    public SelectQuery getTaskDetailsSQ(final Criteria criteria) {
        final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("TaskDetails"));
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("TaskDetails", "*"));
        return (SelectQuery)selectQuery;
    }
    
    public SelectQuery getTaskReportSelectQuery(final HashMap map) {
        final String operationType = map.get("operationType");
        final String status = map.get("status");
        Criteria crit = null;
        if (operationType != null && !operationType.equals("")) {
            crit = new Criteria(Column.getColumn("TaskDetails", "TYPE"), (Object)operationType, 0);
        }
        if (status != null && !status.equals("")) {
            crit = new Criteria(Column.getColumn("TaskDetails", "STATUS"), (Object)status, 0);
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("TaskDetails"));
        selectQuery.addSelectColumn(Column.getColumn("TaskDetails", "TASK_ID"));
        selectQuery.addSelectColumn(Column.getColumn("TaskDetails", "TASKNAME"));
        selectQuery.addSelectColumn(Column.getColumn("TaskDetails", "TYPE", "TASK_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("TaskDetails", "CREATIONTIME"));
        selectQuery.addSelectColumn(Column.getColumn("TaskDetails", "STARTTIME"));
        selectQuery.addSelectColumn(Column.getColumn("TaskDetails", "COMPLETIONTIME"));
        selectQuery.addSelectColumn(Column.getColumn("TaskDetails", "STATUS", "TASK_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("TaskDetails", "REMARKS"));
        if (crit != null) {
            selectQuery.setCriteria(crit);
        }
        return selectQuery;
    }
    
    public Long getLastExecutedTaskID(final String taskName) {
        SelectQuery selectQuery = null;
        Persistence persistence = null;
        DataObject dataObject = null;
        Long taskID = null;
        Criteria criteria = null;
        try {
            persistence = SyMUtil.getPersistence();
            criteria = new Criteria(Column.getColumn("TaskDetails", "TASKNAME"), (Object)("*" + taskName + "*"), 2, false);
            final Criteria statusCriteria = new Criteria(Column.getColumn("TaskDetails", "STATUS"), (Object)"COMPLETED", 0);
            criteria = criteria.and(statusCriteria);
            selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("TaskDetails"));
            selectQuery.addSelectColumn(Column.getColumn("TaskDetails", "TASK_ID"));
            selectQuery.setCriteria(criteria);
            selectQuery.addSortColumn(new SortColumn(Column.getColumn("TaskDetails", "STARTTIME"), false));
            selectQuery.setRange(new Range(1, 1));
            dataObject = persistence.get(selectQuery);
            final Row taskRow = dataObject.getRow("TaskDetails");
            if (taskRow != null) {
                taskID = (Long)taskRow.get("TASK_ID");
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return taskID;
    }
    
    public Properties getTaskProperties(final Long taskID) {
        final Properties properties = new Properties();
        try {
            final Criteria crit = new Criteria(Column.getColumn("TaskDetails", "TASK_ID"), (Object)taskID, 0);
            final DataObject taskDO = DataAccess.get("TaskDetails", crit);
            final Row taskDetailsRow = taskDO.getRow("TaskDetails", crit);
            if (taskDetailsRow != null) {
                final List columnNames = taskDetailsRow.getColumns();
                String columnName = null;
                Object columnValue = null;
                for (int i = 0; i < columnNames.size(); ++i) {
                    columnName = columnNames.get(i);
                    columnValue = taskDetailsRow.get(columnName);
                    if (columnValue != null) {
                        ((Hashtable<String, Object>)properties).put(columnName, columnValue);
                    }
                }
            }
            final Long customerId = this.getCustomerIDFromTaskID(taskID);
            if (customerId != null) {
                ((Hashtable<String, Long>)properties).put("CUSTOMER_ID", customerId);
            }
            final Long userId = this.getUserIDFromTaskID(taskID);
            if (userId != null) {
                ((Hashtable<String, Long>)properties).put("USER_ID", userId);
            }
        }
        catch (final Exception excep) {
            excep.printStackTrace();
        }
        return properties;
    }
    
    public Long getCustomerIDFromTaskID(final Long taskID) {
        Long customerID = null;
        try {
            final Criteria crit = new Criteria(Column.getColumn("TaskToCustomerRel", "TASK_ID"), (Object)taskID, 0);
            final DataObject taskDO = DataAccess.get("TaskToCustomerRel", crit);
            final Row taskCustRow = taskDO.getRow("TaskToCustomerRel", crit);
            if (taskCustRow != null) {
                customerID = (Long)taskCustRow.get("CUSTOMER_ID");
            }
        }
        catch (final Exception excep) {
            excep.printStackTrace();
        }
        return customerID;
    }
    
    public Long getUserIDFromTaskID(final Long taskID) {
        Long userID = null;
        try {
            final Criteria crit = new Criteria(Column.getColumn("TaskToUserRel", "TASK_ID"), (Object)taskID, 0);
            final DataObject taskDO = DataAccess.get("TaskToUserRel", crit);
            final Row taskCustRow = taskDO.getRow("TaskToUserRel", crit);
            if (taskCustRow != null) {
                userID = (Long)taskCustRow.get("USER_ID");
            }
        }
        catch (final Exception excep) {
            excep.printStackTrace();
        }
        return userID;
    }
    
    public Long gettaskID(final String taskName) {
        Long taskID = null;
        try {
            final Criteria crit = new Criteria(Column.getColumn("TaskDetails", "TASKNAME"), (Object)taskName, 0);
            final DataObject taskDO = DataAccess.get("TaskDetails", crit);
            final Row taskCustRow = taskDO.getRow("TaskDetails", crit);
            if (taskCustRow != null) {
                taskID = (Long)taskCustRow.get("TASK_ID");
            }
        }
        catch (final Exception excep) {
            excep.printStackTrace();
        }
        return taskID;
    }
    
    public String getEmailId(final Long taskID) {
        String email = null;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("TaskDetails", "TASK_ID"), (Object)taskID, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("TaskDetails", criteria);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("TaskDetails");
                email = (String)row.get("EMAIL");
                this.logger.log(Level.FINE, "Email given for Task is " + email);
            }
            else {
                this.logger.log(Level.FINE, "Task Detail row is empty for the task Task");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Caught exception  while getting email id specified for Task" + ex);
        }
        return email;
    }
    
    public void removeTask(final Long taskID) {
        try {
            final Criteria removeTaskcriteria = new Criteria(Column.getColumn("TaskDetails", "TASK_ID"), (Object)taskID, 0);
            final DataObject removeTaskDO = DataAccess.get("TaskDetails", removeTaskcriteria);
            removeTaskDO.deleteRows("TaskDetails", removeTaskcriteria);
            DataAccess.update(removeTaskDO);
            this.logger.log(Level.WARNING, "Task is removed from TaskDetails List,  Task ID is " + taskID);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Caught exception  while removing the Task from TaskDetails List :: " + ex);
        }
    }
    
    public void addOrUpdateTaskEmailAddr(final Long taskId, final String email) {
        DataObject taskEmailDO = null;
        try {
            taskEmailDO = getTaskEmailDO(taskId);
            if (!taskEmailDO.isEmpty()) {
                final Row childRow = taskEmailDO.getRow("TaskDetails");
                childRow.set("EMAIL", (Object)email);
                taskEmailDO.updateRow(childRow);
                SyMUtil.getPersistence().update(taskEmailDO);
            }
            else {
                this.logger.log(Level.INFO, "No task will be available with task ID  : " + taskId);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static DataObject getTaskEmailDO(final Long taskID) throws Exception {
        final Criteria crit = new Criteria(Column.getColumn("TaskDetails", "TASK_ID"), (Object)taskID, 0);
        final DataObject getTaskEmailDO = SyMUtil.getPersistence().get("TaskDetails", crit);
        return getTaskEmailDO;
    }
    
    public String getTaskNamefromTaskID(final Long taskID) {
        String taskName = null;
        DataObject taskNameDO = null;
        try {
            taskNameDO = this.getTaskDetailsDO(taskID);
            if (!taskNameDO.isEmpty()) {
                final Row taskNameRow = taskNameDO.getRow("TaskDetails");
                taskName = (String)taskNameRow.get("TASKNAME");
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return taskName;
    }
    
    static {
        DeviceMgmtTaskUtil.taskUtil = null;
    }
}
