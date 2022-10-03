package com.me.mdm.server.datausage.data;

import org.json.JSONException;
import org.json.JSONObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;

public class DataUsageSummary
{
    public Long resourceID;
    public DataEntity dataEntity;
    public DataPeriod dataPeriod;
    public Long lastComputedTime;
    public Double usage;
    public static final String USAGE_KEY = "usage";
    public static final String PERIOD_KEY = "period";
    public static final String ENTITY_KEY = "entity";
    public static final String LAST_COMPUTER_KEY = "last_computed";
    public static final String RESOURCE_ID_KEY = "resource_id";
    
    public DataUsageSummary() {
    }
    
    public DataUsageSummary(final Row row, final Row entityRow, final Row periodRow) {
        this.resourceID = (Long)row.get("RESOURCE_ID");
        this.lastComputedTime = (Long)row.get("LAST_COMPUTED_TIME");
        this.usage = (Double)row.get("USAGE");
        this.dataEntity = new DataEntity(entityRow);
        this.dataPeriod = new DataPeriod(periodRow);
    }
    
    public void addOrUpdateDataUsageSummary(final DataObject dataObject) throws Exception {
        final Row row = dataObject.getRow("DataTrackingSummary", new DataUsageSummaryQuery().getCriteria(this, 0));
        if (row == null) {
            dataObject.addRow(this.getRow(dataObject));
        }
        else {
            row.set("USAGE", (Object)((double)row.get("USAGE") + this.usage));
            row.set("LAST_COMPUTED_TIME", (Object)System.currentTimeMillis());
            dataObject.updateRow(row);
        }
    }
    
    private Row getRow(final DataObject dataObject) throws DataAccessException {
        final Row row = new Row("DataTrackingSummary");
        row.set("RESOURCE_ID", (Object)this.resourceID);
        row.set("USAGE", (Object)this.usage);
        row.set("ENTITY_ID", this.dataEntity.getAndAddUVH(dataObject));
        row.set("PERIOD_ID", this.dataPeriod.getAndAddUVH(dataObject));
        row.set("LAST_COMPUTED_TIME", (Object)System.currentTimeMillis());
        return row;
    }
    
    public JSONObject toJSON() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("usage", (Object)this.usage);
        jsonObject.put("last_computed", (Object)this.lastComputedTime);
        jsonObject.put("resource_id", (Object)this.resourceID);
        jsonObject.put("period", (Object)this.dataPeriod.toJSON());
        jsonObject.put("entity", (Object)this.dataEntity.toJSON());
        return jsonObject;
    }
}
