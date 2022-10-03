package com.me.devicemanagement.framework.server.scheduler;

import java.util.Properties;
import com.me.devicemanagement.framework.server.util.EMSServerUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.me.devicemanagement.framework.server.task.DeviceMgmtTaskUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class SchedulerUtil
{
    private Logger logger;
    private static SchedulerUtil schedule;
    
    public SchedulerUtil() {
        this.logger = Logger.getLogger(SchedulerUtil.class.getName());
    }
    
    public void deleteScheduleSpecificInput(final Long schedulerClassID, final String paramName) {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("SchedulerClasses"));
            sq.addJoin(new Join("SchedulerClasses", "SchedulerInputs", new String[] { "SCHEDULER_CLASS_ID" }, new String[] { "SCHEDULER_INPUT_ID" }, 2));
            Criteria crit = new Criteria(Column.getColumn("SchedulerClasses", "SCHEDULER_CLASS_ID"), (Object)schedulerClassID, 0);
            crit = crit.and(new Criteria(Column.getColumn("SchedulerInputs", "KEY"), (Object)paramName, 0));
            sq.setCriteria(crit);
            sq.addSelectColumn(Column.getColumn("SchedulerInputs", "SCHEDULER_INPUT_ID"));
            final DataObject dobj = SyMUtil.getPersistence().get(sq);
            if (!dobj.isEmpty()) {
                final Row r = dobj.getFirstRow("SchedulerInputs");
                SyMUtil.getPersistence().delete(r);
            }
        }
        catch (final Exception e) {
            Logger.getLogger(SchedulerUtil.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    private void addScheduleSpecificInput(final Long schedulerClassID, final String paramName, final String paramValue) {
        try {
            final Criteria crit = new Criteria(Column.getColumn("SchedulerClasses", "SCHEDULER_CLASS_ID"), (Object)schedulerClassID, 0);
            final DataObject dobj = SyMUtil.getPersistence().get("SchedulerClasses", crit);
            if (!dobj.isEmpty()) {
                final DataObject schInputDO = SyMUtil.getPersistence().constructDataObject();
                final Row newRow = new Row("SchedulerInputs");
                newRow.set("KEY", (Object)paramName);
                newRow.set("VALUE", (Object)paramValue);
                newRow.set("SCHEDULER_INPUT_ID", dobj.getFirstValue("SchedulerClasses", "SCHEDULER_CLASS_ID"));
                schInputDO.addRow(newRow);
                SyMUtil.getPersistence().add(schInputDO);
            }
        }
        catch (final Exception e) {
            Logger.getLogger(SchedulerUtil.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void addOrUpdateScheduleSpecificInput(final Long schedulerClassID, final String paramName, final String paramValue) {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("SchedulerClasses"));
            sq.addJoin(new Join("SchedulerClasses", "SchedulerInputs", new String[] { "SCHEDULER_CLASS_ID" }, new String[] { "SCHEDULER_INPUT_ID" }, 2));
            Criteria crit = new Criteria(Column.getColumn("SchedulerInputs", "KEY"), (Object)paramName, 0);
            crit = crit.and(new Criteria(Column.getColumn("SchedulerClasses", "SCHEDULER_CLASS_ID"), (Object)schedulerClassID, 0));
            sq.setCriteria(crit);
            sq.addSelectColumn(Column.getColumn("SchedulerInputs", "SCHEDULER_INPUT_ID"));
            sq.addSelectColumn(Column.getColumn("SchedulerInputs", "KEY"));
            sq.addSelectColumn(Column.getColumn("SchedulerInputs", "VALUE"));
            final DataObject dobj = SyMUtil.getPersistence().get(sq);
            if (!dobj.isEmpty()) {
                final Row r = dobj.getFirstRow("SchedulerInputs");
                r.set("VALUE", (Object)paramValue);
                dobj.updateRow(r);
                SyMUtil.getPersistence().update(dobj);
            }
            else {
                this.addScheduleSpecificInput(schedulerClassID, paramName, paramValue);
            }
        }
        catch (final Exception e) {
            Logger.getLogger(SchedulerUtil.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public String getScheduleSpecificInputValue(final Long schedulerClassID, final String paramName) {
        try {
            Criteria crit = new Criteria(Column.getColumn("SchedulerInputs", "KEY"), (Object)paramName, 0);
            crit = crit.and(new Criteria(Column.getColumn("SchedulerInputs", "SCHEDULER_INPUT_ID"), (Object)schedulerClassID, 0));
            final DataObject dobj = SyMUtil.getPersistence().get("SchedulerInputs", crit);
            if (!dobj.isEmpty()) {
                return (String)dobj.getFirstValue("SchedulerInputs", "VALUE");
            }
        }
        catch (final Exception e) {
            Logger.getLogger(SchedulerUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }
    
    public Long getSchedulerClassIDFromInput(final String paramName, final String paramValue) {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("SchedulerClasses"));
            sq.addJoin(new Join("SchedulerClasses", "SchedulerInputs", new String[] { "SCHEDULER_CLASS_ID" }, new String[] { "SCHEDULER_INPUT_ID" }, 2));
            Criteria crit = new Criteria(Column.getColumn("SchedulerInputs", "KEY"), (Object)paramName, 0);
            if (DBUtil.getActiveDBName().equalsIgnoreCase("mssql")) {
                crit = crit.and(new Criteria(Column.getColumn("SchedulerInputs", "VALUE"), (Object)paramValue, 2));
            }
            else {
                crit = crit.and(new Criteria(Column.getColumn("SchedulerInputs", "VALUE"), (Object)paramValue, 0));
            }
            sq.setCriteria(crit);
            sq.addSelectColumn(Column.getColumn("SchedulerClasses", "SCHEDULER_CLASS_ID"));
            final DataObject dobj = SyMUtil.getPersistence().get(sq);
            if (!dobj.isEmpty()) {
                return (Long)dobj.getFirstValue("SchedulerClasses", "SCHEDULER_CLASS_ID");
            }
        }
        catch (final Exception e) {
            Logger.getLogger(SchedulerUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }
    
    public Long getCustomerID(final String scheduleName) {
        Long customerID = null;
        try {
            final DataObject taskDetailsDO = DeviceMgmtTaskUtil.getInstance().getSchTaskDetailsDO(scheduleName);
            if (taskDetailsDO.containsTable("TaskToCustomerRel")) {
                final Row taskToCustRelRow = taskDetailsDO.getRow("TaskToCustomerRel");
                if (taskToCustRelRow != null) {
                    customerID = (Long)taskToCustRelRow.get("CUSTOMER_ID");
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger(SchedulerUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return customerID;
    }
    
    public Long getSchedulerClassID(final String scheduleName) {
        try {
            final Criteria crit = new Criteria(Column.getColumn("SchedulerClasses", "SCHEDULER_NAME"), (Object)scheduleName, 0);
            final DataObject dobj = SyMUtil.getPersistence().get("SchedulerClasses", crit);
            if (!dobj.isEmpty()) {
                return (Long)dobj.getFirstValue("SchedulerClasses", "SCHEDULER_CLASS_ID");
            }
        }
        catch (final Exception e) {
            Logger.getLogger(SchedulerUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }
    
    public Long getTaskIDForSchedule(final String scheduleName) {
        try {
            final Long schClassID = this.getSchedulerClassID(scheduleName);
            return this.getTaskIDForSchedule(schClassID);
        }
        catch (final Exception e) {
            Logger.getLogger(SchedulerUtil.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }
    
    public Long getTaskIDForSchedule(final Long schedulerClassID) {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("SchedulerClasses"));
            sq.addJoin(new Join("SchedulerClasses", "ScheduledTaskDetails", new String[] { "SCHEDULER_CLASS_ID" }, new String[] { "SCHEDULER_CLASS_ID" }, 2));
            sq.setCriteria(new Criteria(Column.getColumn("SchedulerClasses", "SCHEDULER_CLASS_ID"), (Object)schedulerClassID, 0));
            sq.addSelectColumn(Column.getColumn("ScheduledTaskDetails", "TASK_ID"));
            final DataObject dobj = SyMUtil.getPersistence().get(sq);
            if (!dobj.isEmpty()) {
                return (Long)dobj.getFirstValue("ScheduledTaskDetails", "TASK_ID");
            }
        }
        catch (final Exception e) {
            Logger.getLogger(SchedulerUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }
    
    public List getSchedulesForCriteria(final Criteria schedulerCriteria) {
        final List scheduleNameList = new ArrayList();
        try {
            final DataObject dobj = SyMUtil.getPersistence().get("SchedulerClasses", schedulerCriteria);
            if (!dobj.isEmpty()) {
                final Iterator<Row> iterator = dobj.getRows("SchedulerClasses");
                while (iterator.hasNext()) {
                    final Row r = iterator.next();
                    scheduleNameList.add(r.get("SCHEDULER_NAME"));
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger(SchedulerUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return scheduleNameList;
    }
    
    public Long addSchedulerInTable(final String schedulerName, final String className, final String workflowName, final HashMap schedulerValues) {
        Long schedulerClassID = 0L;
        try {
            final Column col = Column.getColumn("SchedulerClasses", "SCHEDULER_NAME");
            final Criteria criteria = new Criteria(col, (Object)schedulerName, 0);
            final DataObject dobj = SyMUtil.getPersistence().get("SchedulerClasses", criteria);
            if (dobj.isEmpty()) {
                final Row rowDep = new Row("SchedulerClasses");
                rowDep.set("SCHEDULER_NAME", (Object)schedulerName);
                rowDep.set("REPETITION_NAME", (Object)schedulerName);
                rowDep.set("CLASS_NAME", (Object)className);
                if (workflowName != null) {
                    rowDep.set("WORKFLOW_NAME", (Object)workflowName);
                }
                Integer serverTypeBitwise = 0;
                if (schedulerValues.containsKey("applicableServerTypes")) {
                    final ArrayList applicableServerTypes = schedulerValues.get("applicableServerTypes");
                    serverTypeBitwise = (int)(long)EMSServerUtil.getBitwiseValueForServerTypes(applicableServerTypes);
                }
                rowDep.set("SERVER_TYPE", (Object)serverTypeBitwise);
                final DataObject d = SyMUtil.getPersistence().constructDataObject();
                d.addRow(rowDep);
                SyMUtil.getPersistence().add(d);
                schedulerClassID = (Long)rowDep.get("SCHEDULER_CLASS_ID");
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(SchedulerUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return schedulerClassID;
    }
    
    public void removeSchedulerClasses(final String schedName) {
        try {
            final Column col1 = Column.getColumn("SchedulerClasses", "SCHEDULER_NAME");
            final Criteria schedCriteria = new Criteria(col1, (Object)schedName, 0);
            SyMUtil.getPersistence().delete(schedCriteria);
        }
        catch (final Exception e) {
            Logger.getLogger(SchedulerUtil.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public String getRepeatFrequency(final String scheduleName) {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("SchedulerClasses"));
            sq.addJoin(new Join("SchedulerClasses", "ScheduledTaskDetails", new String[] { "SCHEDULER_CLASS_ID" }, new String[] { "SCHEDULER_CLASS_ID" }, 2));
            sq.setCriteria(new Criteria(Column.getColumn("SchedulerClasses", "SCHEDULER_NAME"), (Object)scheduleName, 0));
            sq.addSelectColumn(Column.getColumn("ScheduledTaskDetails", "TASK_ID"));
            sq.addSelectColumn(Column.getColumn("ScheduledTaskDetails", "REPEAT_FREQUENCY"));
            final DataObject dobj = SyMUtil.getPersistence().get(sq);
            if (!dobj.isEmpty()) {
                return (String)dobj.getFirstValue("ScheduledTaskDetails", "REPEAT_FREQUENCY");
            }
        }
        catch (final Exception e) {
            Logger.getLogger(SchedulerUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }
    
    public Properties getScheduleSpecificInputs(final Long schedulerClassID) {
        final Properties props = new Properties();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("SchedulerClasses"));
            sq.addJoin(new Join("SchedulerClasses", "SchedulerInputs", new String[] { "SCHEDULER_CLASS_ID" }, new String[] { "SCHEDULER_INPUT_ID" }, 2));
            final Criteria crit = new Criteria(Column.getColumn("SchedulerClasses", "SCHEDULER_CLASS_ID"), (Object)schedulerClassID, 0);
            sq.setCriteria(crit);
            sq.addSelectColumn(Column.getColumn("SchedulerInputs", "SCHEDULER_INPUT_ID"));
            sq.addSelectColumn(Column.getColumn("SchedulerInputs", "KEY"));
            sq.addSelectColumn(Column.getColumn("SchedulerInputs", "VALUE"));
            final DataObject dobj = SyMUtil.getPersistence().get(sq);
            if (!dobj.isEmpty()) {
                final Iterator rows = dobj.getRows("SchedulerInputs");
                while (rows.hasNext()) {
                    final Row r = rows.next();
                    props.put(r.get("KEY"), r.get("VALUE"));
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger(SchedulerUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return props;
    }
    
    public List getScheduleNamesForWorkflow(final String workflowName) {
        final List scheduleList = new ArrayList();
        try {
            final Criteria crit = new Criteria(Column.getColumn("SchedulerClasses", "WORKFLOW_NAME"), (Object)workflowName, 0);
            final DataObject dobj = SyMUtil.getPersistence().get("SchedulerClasses", crit);
            if (!dobj.isEmpty()) {
                final Iterator rows = dobj.getRows("SchedulerClasses");
                while (rows.hasNext()) {
                    final Row r = rows.next();
                    scheduleList.add(r.get("SCHEDULER_NAME"));
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger(SchedulerUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return scheduleList;
    }
    
    public String getScheduleNameForTask(final Long taskID) {
        String scheduleName = null;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ScheduledTaskDetails"));
            query.addJoin(new Join("ScheduledTaskDetails", "SchedulerClasses", new String[] { "SCHEDULER_CLASS_ID" }, new String[] { "SCHEDULER_CLASS_ID" }, 2));
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria criteria = new Criteria(Column.getColumn("ScheduledTaskDetails", "TASK_ID"), (Object)taskID, 0);
            query.setCriteria(criteria);
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            if (!dobj.isEmpty()) {
                scheduleName = (String)dobj.getFirstValue("SchedulerClasses", "SCHEDULER_NAME");
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(SchedulerUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return scheduleName;
    }
    
    public Long getSchedulerClassIDForTask(final Long taskID) {
        Long schedulerClassID = null;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ScheduledTaskDetails"));
            query.addJoin(new Join("ScheduledTaskDetails", "SchedulerClasses", new String[] { "SCHEDULER_CLASS_ID" }, new String[] { "SCHEDULER_CLASS_ID" }, 2));
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final Criteria criteria = new Criteria(Column.getColumn("ScheduledTaskDetails", "TASK_ID"), (Object)taskID, 0);
            query.setCriteria(criteria);
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            if (!dobj.isEmpty()) {
                schedulerClassID = (Long)dobj.getFirstValue("SchedulerClasses", "SCHEDULER_CLASS_ID");
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(SchedulerUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return schedulerClassID;
    }
    
    public boolean isScheduleCreated(final String scheduleName) {
        try {
            final Criteria crit = new Criteria(Column.getColumn("SchedulerClasses", "SCHEDULER_NAME"), (Object)scheduleName, 0);
            final DataObject dobj = SyMUtil.getPersistence().get("SchedulerClasses", crit);
            return !dobj.isEmpty();
        }
        catch (final Exception e) {
            Logger.getLogger(SchedulerUtil.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }
    
    public boolean qualifySchedulerCreationBasedOnServerType(final HashMap schedulerValues) {
        if (schedulerValues.containsKey("applicableServerTypes")) {
            final ArrayList applicableServerTypes = schedulerValues.get("applicableServerTypes");
            return EMSServerUtil.isApplicableForCurrentProduct(applicableServerTypes);
        }
        return true;
    }
    
    static {
        SchedulerUtil.schedule = null;
    }
}
