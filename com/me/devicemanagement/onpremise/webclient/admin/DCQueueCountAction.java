package com.me.devicemanagement.onpremise.webclient.admin;

import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.Row;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;
import com.adventnet.persistence.Persistence;
import com.me.devicemanagement.framework.server.queue.DCQueueConstants;

public class DCQueueCountAction implements DCQueueConstants
{
    Persistence per;
    private static Logger logger;
    
    public long getQueueCount(final String qTabName) {
        long count = 0L;
        try {
            if (!qTabName.equalsIgnoreCase("DCQueueDummyTable")) {
                final Table qSubTable = new Table(qTabName);
                final SelectQuery qsSelect = (SelectQuery)new SelectQueryImpl(qSubTable);
                final Column countCal = new Column((String)null, "*").count();
                countCal.setColumnAlias("QC_COUNT");
                qsSelect.addSelectColumn(countCal);
                count = Long.valueOf(SyMUtil.getPersistence().get(qsSelect).getFirstValue(qTabName, 1).toString());
            }
        }
        catch (final Exception e) {
            DCQueueCountAction.logger.log(Level.WARNING, "getQueueCount Exception", e);
        }
        return count;
    }
    
    public long getProcessTime(final String qTabName, final int minMax) {
        long procTime = 0L;
        String selQuery = "";
        try {
            if (!qTabName.equalsIgnoreCase("DCQueueDummyTable")) {
                final Table qSubTable = new Table(qTabName);
                final SelectQuery qsSelect = (SelectQuery)new SelectQueryImpl(qSubTable);
                final Column postTime = new Column(qTabName, "POST_TIME");
                if (minMax == 0) {
                    final Column procCol = postTime.minimum();
                    procCol.setColumnAlias("DATA_POSTED_TIME_LOW");
                    qsSelect.addSelectColumn(procCol);
                }
                else if (minMax == 1) {
                    final Column procLastCol = postTime.maximum();
                    procLastCol.setColumnAlias("DATA_POSTED_TIME_HIGH");
                    qsSelect.addSelectColumn(procLastCol);
                }
                final Object selQObj = SyMUtil.getPersistence().get(qsSelect).getFirstValue(qTabName, 1);
                if (selQObj != null) {
                    selQuery = selQObj.toString();
                    procTime = Long.valueOf(selQuery);
                }
            }
        }
        catch (final Exception e) {
            DCQueueCountAction.logger.log(Level.WARNING, "getProcessTime Exception", e);
        }
        return procTime;
    }
    
    public void addorUpdateQCountTable(final long qTabId, final long count, final long memoryCount, final long processTime, final long lastDataTime) {
        long countDiff = 0L;
        try {
            this.per = (Persistence)BeanUtil.lookup("Persistence");
            final Row qCountRow = new Row("DCQueueSummary");
            final Criteria criMetaId = new Criteria(new Column("DCQueueSummary", "Q_METADATA_ID"), (Object)qTabId, 0);
            DataObject dIdObj = this.per.get("DCQueueSummary", criMetaId);
            qCountRow.set("Q_COUNT", (Object)count);
            qCountRow.set("Q_METADATA_ID", (Object)qTabId);
            qCountRow.set("Q_SIZE_IN_MEMORY", (Object)memoryCount);
            if (processTime != 0L) {
                qCountRow.set("DATA_POSTED_TIME_LOW", (Object)processTime);
            }
            if (lastDataTime != 0L) {
                qCountRow.set("DATA_POSTED_TIME_HIGH", (Object)lastDataTime);
            }
            countDiff = Math.abs(count - memoryCount);
            qCountRow.set("QUEUE_SIZE_PENDING_IN_DB", (Object)countDiff);
            if (dIdObj.isEmpty()) {
                dIdObj = (DataObject)new WritableDataObject();
                dIdObj.addRow(qCountRow);
                this.per.add(dIdObj);
            }
            else {
                dIdObj.updateRow(qCountRow);
                this.per.update(dIdObj);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            DCQueueCountAction.logger.log(Level.WARNING, "addorUpdateQCountTable Exception" + e);
        }
    }
    
    static {
        DCQueueCountAction.logger = Logger.getLogger("QueueCountLog");
    }
}
