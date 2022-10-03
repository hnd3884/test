package com.me.mdm.server.datausage.data;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;

public class DataPeriod
{
    public Long periodID;
    public Long startTime;
    public Long endTime;
    public static final String START_TIME_KEY = "start_time";
    public static final String END_TIME_KEY = "end_time";
    
    public DataPeriod(final Row row) {
        this.periodID = (Long)row.get("PERIOD_ID");
        this.startTime = (Long)row.get("PERIOD_START_TIME");
        this.endTime = (Long)row.get(3);
    }
    
    public DataPeriod() {
    }
    
    @Override
    public boolean equals(final Object obj) {
        final DataPeriod p = (DataPeriod)obj;
        return p.startTime.equals(this.startTime) && p.startTime.equals(this.startTime);
    }
    
    public Object getAndAddUVH(final DataObject dataObject) throws DataAccessException {
        Object retVal = this.periodID;
        if (this.periodID == null) {
            final Criteria startCriteria = new Criteria(Column.getColumn("DataTrackingPeriods", "PERIOD_START_TIME"), (Object)this.startTime, 0);
            final Criteria endCriteria = new Criteria(Column.getColumn("DataTrackingPeriods", "PERIOD_END_TIME"), (Object)this.endTime, 0);
            Row row = dataObject.getRow("DataTrackingPeriods", startCriteria.and(endCriteria));
            if (row == null) {
                row = new Row("DataTrackingPeriods");
                row.set("PERIOD_START_TIME", (Object)this.startTime);
                row.set("PERIOD_END_TIME", (Object)this.endTime);
                dataObject.addRow(row);
            }
            retVal = row.get("PERIOD_ID");
        }
        return retVal;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.startTime == null) ? 0 : this.startTime.hashCode()) + ((this.endTime == null) ? 0 : this.endTime.hashCode());
        return result;
    }
    
    public static DataObject getDataPeriodsFromDB(final List<DataPeriod> timePeriods) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DataTrackingPeriods"));
        selectQuery.addSelectColumn(Column.getColumn("DataTrackingPeriods", "*"));
        selectQuery.setCriteria(new DataPeriodCriteria(timePeriods, 8).getFinalCriteria());
        return MDMUtil.getPersistenceLite().get(selectQuery);
    }
    
    public DataPeriod getBucketPeriod(final List<DataPeriod> dataPeriods) {
        for (final DataPeriod period : dataPeriods) {
            if (period.startTime <= this.startTime && period.endTime >= this.endTime) {
                return period;
            }
        }
        return null;
    }
    
    public JSONObject toJSON() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("start_time", (Object)this.startTime);
        jsonObject.put("end_time", (Object)this.endTime);
        return jsonObject;
    }
}
