package com.adventnet.taskengine.internal;

import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import java.util.Map;
import java.util.Iterator;
import java.sql.Timestamp;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.taskengine.util.PersistenceUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.TimerTask;

public class RefreshController extends TimerTask
{
    private static Logger logger;
    private static final int REFRESH_PERIOD = 15;
    private static boolean isScheduleSkipped;
    
    @Override
    public void run() {
        try {
            RefreshController.logger.log(Level.FINE, "RefreshController thread Started");
            this.refresh();
        }
        catch (final Exception excp) {
            RefreshController.logger.log(Level.SEVERE, "Exception from  RefreshController :", excp);
        }
    }
    
    public void refresh() throws Exception {
        if (!RefreshController.isScheduleSkipped) {
            PersistenceUtil.updateSkipSchedules();
            RefreshController.isScheduleSkipped = true;
            RefreshController.logger.fine("ScheduleSkipped ");
        }
        final WritableDataObject taskInputs = (WritableDataObject)PersistenceUtil.getPersistence().get(this.getQuery());
        RefreshController.logger.log(Level.FINE, "Adding tasks from Task_Input table to TimeMap, DataObject fetched : {0}", taskInputs);
        final Iterator taskInputItr = taskInputs.getRows("Task_Input");
        while (taskInputItr.hasNext()) {
            final Row tiRow = taskInputItr.next();
            final Long instanceID = (Long)tiRow.get("INSTANCE_ID");
            final Long scheduleTime = new Long(((Timestamp)tiRow.get("SCHEDULE_TIME")).getTime());
            final Map batchMap = TaskEngineService.updateController.getMap();
            final Long timeInBatchMap = batchMap.get(instanceID);
            final Long poolID = (Long)tiRow.get("POOL_ID");
            final String poolname = PersistenceUtil.getPoolName(poolID);
            final Long reqTime = (timeInBatchMap == null) ? scheduleTime : timeInBatchMap;
            if (reqTime != -1L) {
                final TimeMap timeMap = TimeMap.getInstance(poolname);
                timeMap.addToTimeMap(instanceID, reqTime);
                RefreshController.logger.log(Level.FINE, "Added task to TimeMap, InstanceId: {0}", instanceID);
            }
        }
    }
    
    public static Criteria getCriteria(final String table, final String column, final Object value, final int comparator, final Criteria andCriteria) {
        final Column newColumn = Column.getColumn(table, column);
        final Criteria criteria = new Criteria(newColumn, value, comparator);
        if (andCriteria == null) {
            return criteria;
        }
        return andCriteria.and(criteria);
    }
    
    private SelectQuery getQuery() throws Exception {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("Task_Input"));
        final String[] relation = { "SCHEDULE_ID", "TASK_ID" };
        final Join join = new Join("Task_Input", "Scheduled_Task", relation, relation, 2);
        sq.addJoin(join);
        final Column instanceIDCol = Column.getColumn("Task_Input", "INSTANCE_ID");
        sq.addSelectColumn(instanceIDCol);
        final Column schTimeCol = Column.getColumn("Task_Input", "SCHEDULE_TIME");
        sq.addSelectColumn(schTimeCol);
        final Column poolIdCol = Column.getColumn("Task_Input", "POOL_ID");
        sq.addSelectColumn(poolIdCol);
        final Integer status = 3;
        final Long timeLimit = new Long(System.currentTimeMillis() + 900000L);
        Criteria criteria = null;
        criteria = getCriteria("Task_Input", "ADMIN_STATUS", status, 0, criteria);
        criteria = getCriteria("Task_Input", "SCHEDULE_TIME", new Timestamp(timeLimit), 7, criteria);
        criteria = getCriteria("Scheduled_Task", "ADMIN_STATUS", status, 0, criteria);
        criteria = getCriteria("Task_Input", "SCHEDULE_TIME", null, 1, criteria);
        sq.setCriteria(criteria);
        return sq;
    }
    
    static {
        RefreshController.logger = Logger.getLogger(RefreshController.class.getName());
        RefreshController.isScheduleSkipped = false;
    }
}
