package com.adventnet.taskengine.util;

import com.adventnet.persistence.DataAccess;
import com.adventnet.mfw.bean.BeanUtil;
import java.util.concurrent.ThreadFactory;
import java.util.Locale;
import java.util.Collections;
import java.util.Vector;
import java.util.ArrayList;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.WritableDataObject;
import java.sql.Timestamp;
import com.adventnet.ds.query.Criteria;
import com.adventnet.taskengine.internal.RefreshController;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.adventnet.persistence.Persistence;
import java.util.Hashtable;
import javax.transaction.TransactionManager;
import com.adventnet.persistence.ReadOnlyPersistence;
import java.util.logging.Logger;

public class PersistenceUtil
{
    private static Logger out;
    private static ReadOnlyPersistence cachedPersistence;
    private static TransactionManager transactionManager;
    private static Hashtable offsetTable;
    private static Persistence persistence;
    private static Persistence purePersistenceLite;
    private static Persistence purePersistenceLiteNoTrans;
    private static Hashtable classReference;
    private static Map<String, Long> poolNameVsID;
    private static Map<Long, String> poolIDVsName;
    private static Map<String, Integer> poolNameVsPoolSize;
    private static List<String> threadPoolNames;
    public static HashMap<String, ScheduledThreadPoolExecutor> poolNameVsExecutor;
    
    public static ReadOnlyPersistence getCachedPersistence() {
        return PersistenceUtil.cachedPersistence;
    }
    
    public static Persistence getPersistence() {
        return PersistenceUtil.persistence;
    }
    
    public static Persistence getPurePersistenceLite() {
        return PersistenceUtil.purePersistenceLite;
    }
    
    public static Persistence getPurePersistenceLiteNoTrans() {
        return PersistenceUtil.purePersistenceLiteNoTrans;
    }
    
    public static TransactionManager getTransactionManager() {
        return PersistenceUtil.transactionManager;
    }
    
    public static int getTransactionTimeOut() {
        final String timeOut = PersistenceInitializer.getConfigurationValue("TransactionTimeOut");
        return new Integer(timeOut);
    }
    
    public static void updateSkipSchedules() throws Exception {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("Task_Input"));
        final String[] relation = { "SCHEDULE_ID", "TASK_ID" };
        final Join join = new Join("Task_Input", "Scheduled_Task", relation, relation, 2);
        sq.addJoin(join);
        final Column selectCol = Column.getColumn("Task_Input", "*");
        sq.addSelectColumn(selectCol);
        final Integer status = 3;
        final Long timeLimit = System.currentTimeMillis();
        Criteria criteria = RefreshController.getCriteria("Task_Input", "ADMIN_STATUS", status, 0, null);
        criteria = RefreshController.getCriteria("Scheduled_Task", "ADMIN_STATUS", status, 0, criteria);
        criteria = RefreshController.getCriteria("Task_Input", "SCHEDULE_TIME", new Timestamp(timeLimit), 7, criteria);
        criteria = RefreshController.getCriteria("Task_Input", "SCHEDULE_TIME", null, 1, criteria);
        criteria = RefreshController.getCriteria("Scheduled_Task", "SKIP_MISSED_SCHEDULE", true, 0, criteria);
        sq.setCriteria(criteria);
        final WritableDataObject taskInputs = (WritableDataObject)getCachedPersistence().get(sq);
        for (final DataObject skipDO : taskInputs.getDataObjects()) {
            final Row taskInputRow = skipDO.getFirstRow("Task_Input");
            final Long scheduleID = (Long)taskInputRow.get("SCHEDULE_ID");
            final Long taskID = (Long)taskInputRow.get("TASK_ID");
            final DataObject schedule = getSchedule(scheduleID, true);
            final DataObject scheduledTask = getScheduledTask(scheduleID, taskID);
            if (!scheduledTask.containsTable("Scheduled_Task")) {
                return;
            }
            Long scheduleTime = -1L;
            if (taskInputRow.get("SCHEDULE_TIME") != null && ((Timestamp)taskInputRow.get("SCHEDULE_TIME")).getTime() != 0L) {
                scheduleTime = ((Timestamp)taskInputRow.get("SCHEDULE_TIME")).getTime();
            }
            Long nextScheduleTime = null;
            nextScheduleTime = ScheduleUtil.calculateNextScheduleTime(schedule, scheduleTime, true);
            final long nextSchTime = nextScheduleTime;
            final DataObject clonedSkipDO = (DataObject)skipDO.clone();
            final Row clonedTaskInputRow = clonedSkipDO.getRow("Task_Input", taskInputRow);
            if (nextSchTime != -1L) {
                clonedTaskInputRow.set("SCHEDULE_TIME", (Object)new Timestamp(nextSchTime));
            }
            else {
                clonedTaskInputRow.set("SCHEDULE_TIME", (Object)null);
            }
            clonedSkipDO.updateRow(clonedTaskInputRow);
            PersistenceUtil.persistence.update(clonedSkipDO);
        }
    }
    
    public static Row getScheduledTask_RetryRow(final Long scheduleID, final Long taskID) throws Exception {
        Criteria criteria = new Criteria(Column.getColumn("ScheduledTask_Retry", "SCHEDULE_ID"), (Object)scheduleID, 0);
        criteria = criteria.and(Column.getColumn("ScheduledTask_Retry", "TASK_ID"), (Object)taskID, 0);
        final DataObject retryDO = PersistenceUtil.cachedPersistence.get("ScheduledTask_Retry", criteria);
        return retryDO.getRow("ScheduledTask_Retry");
    }
    
    public static DataObject getSchedule(final Long scheduleID, final boolean deepFetch) throws Exception {
        final Row schRow = new Row("Schedule");
        schRow.set(1, (Object)scheduleID);
        final DataObject schedule = PersistenceUtil.cachedPersistence.getForPersonality("Schedule_Pers", schRow);
        return schedule;
    }
    
    public static DataObject getSchedule(final Long scheduleID) throws DataAccessException {
        final Table tb = Table.getTable("Schedule");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(tb);
        final Column cc = Column.getColumn("Schedule", "*");
        query.addSelectColumn(cc);
        final Column cc2 = Column.getColumn("Schedule", "SCHEDULE_ID");
        final Criteria ct = new Criteria(cc2, (Object)scheduleID, 0);
        query.setCriteria(ct);
        final DataObject sch = PersistenceUtil.cachedPersistence.get(query);
        return sch;
    }
    
    public static DataObject getScheduledTaskFromCache(final String scheduleName, final String taskName) throws Exception {
        final long scheduleID = getIDByNameForSchedule(scheduleName);
        final long taskID = getIDByNameForTask(taskName);
        return getScheduledTaskFromCache(scheduleID, taskID);
    }
    
    public static DataObject getScheduledTaskFromCache(final Long scheduleID, final Long taskID) throws Exception {
        final Table tb = Table.getTable("Scheduled_Task");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(tb);
        final Column cc = Column.getColumn("Scheduled_Task", "*");
        query.addSelectColumn(cc);
        final Column schColumn = Column.getColumn("Scheduled_Task", "SCHEDULE_ID");
        final Criteria ct = new Criteria(schColumn, (Object)scheduleID, 0);
        final Column taskColumn = Column.getColumn("Scheduled_Task", "TASK_ID");
        final Criteria ct2 = new Criteria(taskColumn, (Object)taskID, 0);
        final Criteria ct3 = ct.and(ct2);
        query.setCriteria(ct3);
        final DataObject schTask = PersistenceUtil.cachedPersistence.get(query);
        return schTask;
    }
    
    public static DataObject getScheduledTask(final long scheduleID, final long taskID) throws DataAccessException {
        final Table tb = Table.getTable("Scheduled_Task");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(tb);
        final Column cc = Column.getColumn("Scheduled_Task", "*");
        query.addSelectColumn(cc);
        final Column schColumn = Column.getColumn("Scheduled_Task", "SCHEDULE_ID");
        final Criteria ct = new Criteria(schColumn, (Object)scheduleID, 0);
        final Column taskColumn = Column.getColumn("Scheduled_Task", "TASK_ID");
        final Criteria ct2 = new Criteria(taskColumn, (Object)taskID, 0);
        final Criteria ct3 = ct.and(ct2);
        query.setCriteria(ct3);
        final DataObject schTask = PersistenceUtil.persistence.get(query);
        return schTask;
    }
    
    public static String getNameByIDForSchedule(final Long scheduleID) throws Exception {
        final Table tb = Table.getTable("Schedule");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(tb);
        final Column cc = Column.getColumn("Schedule", "*");
        query.addSelectColumn(cc);
        final Column c = Column.getColumn("Schedule", "SCHEDULE_ID");
        final Criteria ct = new Criteria(c, (Object)scheduleID, 0);
        query.setCriteria(ct);
        final DataObject bv = PersistenceUtil.cachedPersistence.get(query);
        final Row tempRow = bv.getRow("Schedule");
        final String schName = (String)tempRow.get("SCHEDULE_NAME");
        return schName;
    }
    
    public static Row getWorkingHoursByID(final Long WorkingHoursID) throws Exception {
        final Table tb = Table.getTable("Working_Hours");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(tb);
        final Column cc = Column.getColumn("Working_Hours", "*");
        query.addSelectColumn(cc);
        final Column c = Column.getColumn("Working_Hours", "WORKING_HOURS_ID");
        final Criteria ct = new Criteria(c, (Object)WorkingHoursID, 0);
        query.setCriteria(ct);
        final DataObject bv = PersistenceUtil.cachedPersistence.get(query);
        final Row tempRow = bv.getRow("Working_Hours");
        return tempRow;
    }
    
    public static long getIDByNameForTask(final String taskName) throws DataAccessException {
        final Table tb = Table.getTable("TaskEngine_Task");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(tb);
        final Column cc = Column.getColumn("TaskEngine_Task", "TASK_ID");
        query.addSelectColumn(cc);
        final Column c = Column.getColumn("TaskEngine_Task", "TASK_NAME");
        final Criteria ct = new Criteria(c, (Object)taskName, 0);
        query.setCriteria(ct);
        Long idLong = -1L;
        final DataObject bv = PersistenceUtil.cachedPersistence.get(query);
        final Row tempRow = bv.getFirstRow("TaskEngine_Task");
        idLong = (Long)tempRow.get("TASK_ID");
        return idLong;
    }
    
    public static long getIDByNameForSchedule(final String name) throws DataAccessException {
        final Table tb = Table.getTable("Schedule");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(tb);
        final Column cc = Column.getColumn("Schedule", "SCHEDULE_ID");
        query.addSelectColumn(cc);
        final Column c = Column.getColumn("Schedule", "SCHEDULE_NAME");
        final Criteria ct = new Criteria(c, (Object)name, 0);
        query.setCriteria(ct);
        Long idLong = -1L;
        final DataObject bv = PersistenceUtil.cachedPersistence.get(query);
        final Row tempRow = bv.getFirstRow("Schedule");
        idLong = (Long)tempRow.get("SCHEDULE_ID");
        return idLong;
    }
    
    public static DataObject checkAndAddScheduledTask(final Long scheduleID, final Long taskID, final Integer rescheduleMode, final Integer offset) throws Exception {
        return checkAndAddScheduledTask(scheduleID, taskID, rescheduleMode, offset, 0);
    }
    
    public static DataObject checkAndAddScheduledTask(final Long scheduleID, final Long taskID, final Integer rescheduleMode, final Integer offset, final int transactionTime) throws Exception {
        DataObject scheduledTask = getScheduledTaskFromCache(scheduleID, taskID);
        if (!scheduledTask.containsTable("Scheduled_Task")) {
            scheduledTask = PersistenceUtil.persistence.constructDataObject();
            final Row scheduledTaskRow = new Row("Scheduled_Task");
            scheduledTaskRow.set("SCHEDULE_ID", (Object)scheduleID);
            scheduledTaskRow.set("TASK_ID", (Object)taskID);
            scheduledTaskRow.set("SCHEDULE_MODE", (Object)rescheduleMode);
            scheduledTaskRow.set("OFFSET_MS", (Object)offset);
            scheduledTaskRow.set("TRANSACTION_TIME", (Object)transactionTime);
            scheduledTask.addRow(scheduledTaskRow);
            scheduledTask = PersistenceUtil.persistence.add(scheduledTask);
        }
        else {
            final Row scheduledTaskRow = scheduledTask.getRow("Scheduled_Task");
            final int actualRescheduleModule = (int)scheduledTaskRow.get(4);
            final int actualOffset = (int)scheduledTaskRow.get(3);
            final int actualTransTime = (int)scheduledTaskRow.get(7);
            if (actualRescheduleModule != rescheduleMode || actualOffset != offset || actualTransTime != transactionTime) {
                final DataObject clonedScheduledTaskDO = (DataObject)scheduledTask.clone();
                final Row clonedScheduledTaskRow = (Row)scheduledTaskRow.clone();
                clonedScheduledTaskRow.set("SCHEDULE_MODE", (Object)rescheduleMode);
                clonedScheduledTaskRow.set("OFFSET_MS", (Object)offset);
                clonedScheduledTaskRow.set("TRANSACTION_TIME", (Object)transactionTime);
                clonedScheduledTaskDO.updateRow(clonedScheduledTaskRow);
                PersistenceUtil.out.log(Level.FINE, "updated scheduledTask :: " + clonedScheduledTaskDO);
                scheduledTask = PersistenceUtil.persistence.update(clonedScheduledTaskDO);
            }
        }
        return scheduledTask;
    }
    
    public static long getOffsetForScheduledTask(final Long scheduleID, final Long taskID) throws Exception {
        Integer incrementFactor = 1;
        final DataObject scheduledTask = getScheduledTaskFromCache(scheduleID, taskID);
        final int offSet = (int)scheduledTask.getRow("Scheduled_Task").get("OFFSET_MS");
        final String key = scheduleID + ":" + taskID;
        if (PersistenceUtil.offsetTable.containsKey(key)) {
            incrementFactor = PersistenceUtil.offsetTable.get(key);
            PersistenceUtil.offsetTable.put(key, incrementFactor + 1);
        }
        else {
            PersistenceUtil.offsetTable.put(key, incrementFactor);
        }
        return incrementFactor * offSet;
    }
    
    public static DataObject getAllTaskInput(final long scheduleID) throws DataAccessException {
        final Table tb = Table.getTable("Task_Input");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(tb);
        final Column co1 = Column.getColumn("Task_Input", "*");
        query.addSelectColumn(co1);
        final Column co2 = Column.getColumn("Task_Input", "SCHEDULE_ID");
        final Criteria ct1 = new Criteria(co2, (Object)scheduleID, 0);
        query.setCriteria(ct1);
        final DataObject taskInput = PersistenceUtil.cachedPersistence.get(query);
        return taskInput;
    }
    
    public static DataObject getAllTaskInput(final long scheduleID, final long taskID) throws DataAccessException {
        final List<String> tableNames = new ArrayList<String>();
        tableNames.add("Scheduled_Task");
        tableNames.add("Task_Input");
        tableNames.add("Default_Task_Input");
        final List<String> opTableNames = new ArrayList<String>();
        opTableNames.add("Default_Task_Input");
        Criteria criteria = new Criteria(Column.getColumn("Task_Input", "SCHEDULE_ID"), (Object)scheduleID, 0);
        criteria = criteria.and(Column.getColumn("Task_Input", "TASK_ID"), (Object)taskID, 0);
        final DataObject dataObject = PersistenceUtil.cachedPersistence.get((List)tableNames, (List)opTableNames, criteria);
        return dataObject;
    }
    
    public static DataObject getTaskInput(final long instanceID) throws DataAccessException {
        final List tableList = new ArrayList();
        tableList.add("Task_Input");
        tableList.add("Default_Task_Input");
        final List optTableList = new ArrayList();
        optTableList.add("Default_Task_Input");
        final Criteria ct = new Criteria(Column.getColumn("Task_Input", "INSTANCE_ID"), (Object)instanceID, 0);
        return PersistenceUtil.persistence.get(tableList, optTableList, ct);
    }
    
    public static void unscheduleTask(final Long scheduleID, final Long taskID) throws Exception {
        final Row row = new Row("Scheduled_Task");
        row.set("SCHEDULE_ID", (Object)scheduleID);
        row.set("TASK_ID", (Object)taskID);
        PersistenceUtil.persistence.delete(row);
        final String key = scheduleID + ":" + taskID;
        PersistenceUtil.offsetTable.remove(key);
    }
    
    public static long addSchedule(final DataObject sch) throws Exception {
        Long scheduleID = -1L;
        final DataObject dob = PersistenceUtil.persistence.add(sch);
        final Iterator it = dob.getRows("Schedule");
        while (it.hasNext()) {
            final Row tmpRow = it.next();
            scheduleID = (Long)tmpRow.get("SCHEDULE_ID");
        }
        return scheduleID;
    }
    
    public static void removeSchedule(final long scheduleID) throws Exception {
        final Row schRow = new Row("Schedule");
        schRow.set("SCHEDULE_ID", (Object)scheduleID);
        PersistenceUtil.persistence.delete(schRow);
    }
    
    public static DataObject updateSchedule(final DataObject schedule) throws Exception {
        final DataObject dob = PersistenceUtil.persistence.update(schedule);
        return dob;
    }
    
    public static DataObject getAllSchedules() throws DataAccessException {
        final List tableList = new Vector();
        tableList.add("Schedule_Pers");
        final Column co1 = Column.getColumn("Schedule", "SCHEDULE_ID");
        final Criteria ct = new Criteria(co1, (Object)0, 5);
        final DataObject retObject = PersistenceUtil.cachedPersistence.getForPersonalities(tableList, tableList, ct);
        return retObject;
    }
    
    public static Class getClass(final String className) throws Exception {
        Class c = PersistenceUtil.classReference.get(className);
        if (c == null) {
            c = PersistenceUtil.class.getClassLoader().loadClass(className);
            PersistenceUtil.classReference.put(className, c);
        }
        return c;
    }
    
    public static ScheduledThreadPoolExecutor getExecutor(final String poolName) throws Exception {
        ScheduledThreadPoolExecutor executor = PersistenceUtil.poolNameVsExecutor.get(poolName);
        if (executor == null && PersistenceUtil.poolNameVsExecutor.isEmpty()) {
            fillThreadPoolMaps();
            executor = PersistenceUtil.poolNameVsExecutor.get(poolName);
        }
        return executor;
    }
    
    public static List<String> getThreadPoolNames() throws Exception {
        if (PersistenceUtil.threadPoolNames.isEmpty()) {
            fillThreadPoolMaps();
        }
        return Collections.unmodifiableList((List<? extends String>)PersistenceUtil.threadPoolNames);
    }
    
    public static void logThreadPoolDetails() throws Exception {
        for (final String poolName : getThreadPoolNames()) {
            PersistenceUtil.out.log(Level.INFO, "PoolName : " + poolName + "Poolsize : " + getThreadPoolSize(poolName));
        }
    }
    
    public static Long getPoolID(final String poolName) throws Exception {
        if (poolName == null) {
            throw new IllegalArgumentException("");
        }
        final String pName = poolName.toUpperCase(Locale.ENGLISH);
        Long poolID = PersistenceUtil.poolNameVsID.get(pName);
        if (poolID == null && PersistenceUtil.poolNameVsID.isEmpty()) {
            fillThreadPoolMaps();
            poolID = PersistenceUtil.poolNameVsID.get(pName);
        }
        return poolID;
    }
    
    public static String getPoolName(final Long poolID) throws Exception {
        if (poolID == null) {
            return "default";
        }
        String poolName = PersistenceUtil.poolIDVsName.get(poolID);
        if (poolName == null && PersistenceUtil.poolIDVsName.isEmpty()) {
            fillThreadPoolMaps();
            poolName = PersistenceUtil.poolIDVsName.get(poolID);
        }
        return poolName;
    }
    
    public static int getThreadPoolSize(final String poolName) throws Exception {
        Integer poolSize = PersistenceUtil.poolNameVsPoolSize.get(poolName);
        if (poolSize == null && PersistenceUtil.poolNameVsPoolSize.isEmpty()) {
            fillThreadPoolMaps();
            poolSize = PersistenceUtil.poolNameVsPoolSize.get(poolName);
        }
        return poolSize;
    }
    
    private static synchronized void fillThreadPoolMaps() throws Exception {
        final DataObject threadPoolDO = getCachedPersistence().get("ThreadPool", (Criteria)null);
        final Iterator rows = threadPoolDO.getRows("ThreadPool");
        while (rows.hasNext()) {
            final Row row = rows.next();
            final String poolName = (String)row.get(2);
            PersistenceUtil.threadPoolNames.add(poolName);
            PersistenceUtil.poolNameVsID.put(poolName.toUpperCase(Locale.ENGLISH), (Long)row.get(1));
            PersistenceUtil.poolIDVsName.put((Long)row.get(1), poolName);
            PersistenceUtil.poolNameVsPoolSize.put(poolName, (Integer)row.get(3));
            final TEThreadFactory threadFacInstance = new TEThreadFactory(poolName);
            PersistenceUtil.poolNameVsExecutor.put(poolName, new ScheduledThreadPoolExecutor((int)row.get(3), threadFacInstance));
        }
    }
    
    public static DataObject getTaskInputDO(final Long taskInputInstanceID) throws DataAccessException {
        final List<String> tableNames = new ArrayList<String>();
        tableNames.add("TaskEngine_Task");
        tableNames.add("Schedule");
        tableNames.add("Scheduled_Task");
        tableNames.add("Task_Input");
        tableNames.add("Periodic");
        tableNames.add("Calendar");
        tableNames.add("Composite");
        tableNames.add("Calendar_Periodicity");
        tableNames.add("ScheduledTask_Retry");
        tableNames.add("Default_Task_Input");
        final List<String> optionalTableNames = new ArrayList<String>();
        optionalTableNames.add("Periodic");
        optionalTableNames.add("Calendar");
        optionalTableNames.add("Composite");
        optionalTableNames.add("Calendar_Periodicity");
        optionalTableNames.add("ScheduledTask_Retry");
        optionalTableNames.add("Default_Task_Input");
        optionalTableNames.add("Default_Task_Input");
        final Criteria criteria = new Criteria(Column.getColumn("Task_Input", "INSTANCE_ID"), (Object)taskInputInstanceID, 0);
        final DataObject taskInputDO = getCachedPersistence().get((List)tableNames, (List)optionalTableNames, criteria);
        return taskInputDO;
    }
    
    static {
        PersistenceUtil.out = Logger.getLogger(PersistenceUtil.class.getName());
        PersistenceUtil.cachedPersistence = null;
        PersistenceUtil.transactionManager = null;
        PersistenceUtil.offsetTable = new Hashtable();
        PersistenceUtil.persistence = null;
        PersistenceUtil.purePersistenceLite = null;
        PersistenceUtil.purePersistenceLiteNoTrans = null;
        PersistenceUtil.classReference = new Hashtable();
        try {
            PersistenceUtil.cachedPersistence = (ReadOnlyPersistence)BeanUtil.lookup("CachedPersistence");
            PersistenceUtil.persistence = (Persistence)BeanUtil.lookup("Persistence");
            PersistenceUtil.purePersistenceLite = (Persistence)BeanUtil.lookup("PurePersistenceLite");
            PersistenceUtil.purePersistenceLiteNoTrans = (Persistence)BeanUtil.lookup("PurePersistenceLite-NoTrans");
            PersistenceUtil.transactionManager = DataAccess.getTransactionManager();
        }
        catch (final Exception io) {
            io.printStackTrace();
        }
        PersistenceUtil.poolNameVsID = new HashMap<String, Long>();
        PersistenceUtil.poolIDVsName = new HashMap<Long, String>();
        PersistenceUtil.poolNameVsPoolSize = new HashMap<String, Integer>();
        PersistenceUtil.threadPoolNames = new ArrayList<String>();
        PersistenceUtil.poolNameVsExecutor = new HashMap<String, ScheduledThreadPoolExecutor>();
    }
    
    static class TEThreadFactory implements ThreadFactory
    {
        String poolName;
        int count;
        
        public TEThreadFactory(final String poolName) {
            this.poolName = "";
            this.count = 0;
            this.poolName = poolName;
        }
        
        @Override
        public Thread newThread(final Runnable r) {
            return new Thread(r, this.poolName + "_" + this.count++);
        }
    }
}
