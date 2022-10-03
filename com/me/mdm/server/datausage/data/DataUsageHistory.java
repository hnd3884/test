package com.me.mdm.server.datausage.data;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;

public class DataUsageHistory
{
    public Long resourceID;
    public DataEntity dataEntity;
    public DataPeriod dataPeriod;
    public Double usage;
    private Double delta;
    public Long reportedTime;
    public Long dataTrackID;
    public Long agentComputedTime;
    
    public Boolean addRowToDO(final DataObject dataObject) throws DataAccessException {
        final Row row = this.getRow(dataObject);
        if (row.hasUVGColInPK()) {
            dataObject.addRow(row);
            return true;
        }
        dataObject.updateRow(row);
        return false;
    }
    
    private Row getRow(final DataObject dataObject) throws DataAccessException {
        Row row = null;
        if (this.dataEntity.entityID != null && this.dataPeriod.periodID != null) {
            row = dataObject.getRow("DataTrackingHistory", new DataUsageHistoryQuery().getCriteria(this, 0));
            if (row != null) {
                this.delta = this.usage - (double)row.get("USAGE");
            }
        }
        if (row == null) {
            row = new Row("DataTrackingHistory");
            row.set("RESOURCE_ID", (Object)this.resourceID);
            row.set("ENTITY_ID", this.dataEntity.getAndAddUVH(dataObject));
            row.set("PERIOD_ID", this.dataPeriod.getAndAddUVH(dataObject));
            this.delta = this.usage;
        }
        row.set("USAGE", (Object)this.usage);
        row.set("REPORTED_TIME", (Object)this.reportedTime);
        row.set("AGENT_COMPUTED_TIME", (Object)this.agentComputedTime);
        return row;
    }
    
    public Double getDelta() {
        return this.delta;
    }
}
