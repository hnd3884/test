package com.me.mdm.server.datausage.data;

import com.me.mdm.server.datausage.DataUsageConstants;
import org.json.JSONException;
import org.json.JSONObject;
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

public class DataEntity
{
    public Long entityID;
    public Integer type;
    public String identifier;
    public static final String TYPE_KEY = "type";
    public static final String IDENTIFIER_KEY = "identifier";
    
    public DataEntity() {
    }
    
    public DataEntity(final Row row) {
        this.entityID = (Long)row.get("ENTITY_ID");
        this.type = (Integer)row.get("ENTITY_TYPE");
        this.identifier = (String)row.get("ENTITY_IDENTIFIER");
    }
    
    @Override
    public boolean equals(final Object obj) {
        final DataEntity e = (DataEntity)obj;
        return e.identifier.equals(this.identifier) && this.type.equals(e.type);
    }
    
    public Object getAndAddUVH(final DataObject dataObject) throws DataAccessException {
        Object retVal = this.entityID;
        if (this.entityID == null) {
            final Criteria identifierCriteria = new Criteria(Column.getColumn("DataEntity", "ENTITY_IDENTIFIER"), (Object)this.identifier, 0);
            final Criteria typeCriteria = new Criteria(Column.getColumn("DataEntity", "ENTITY_TYPE"), (Object)this.type, 0);
            Row row = dataObject.getRow("DataEntity", identifierCriteria.and(typeCriteria));
            if (row == null) {
                row = new Row("DataEntity");
                row.set("ENTITY_TYPE", (Object)this.type);
                row.set("ENTITY_IDENTIFIER", (Object)this.identifier);
                dataObject.addRow(row);
            }
            retVal = row.get("ENTITY_ID");
        }
        return retVal;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.type == null) ? 0 : this.type.hashCode()) + ((this.identifier == null) ? 0 : this.identifier.hashCode());
        return result;
    }
    
    public static DataObject getDataEntitiesFromDB(final List<DataEntity> dataEntities) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DataEntity"));
        selectQuery.addSelectColumn(Column.getColumn("DataEntity", "*"));
        selectQuery.setCriteria(new DataEntityCriteria(dataEntities, 8).getFinalCriteria());
        return MDMUtil.getPersistenceLite().get(selectQuery);
    }
    
    public JSONObject toJSON() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("identifier", (Object)this.identifier);
        jsonObject.put("type", (Object)this.type);
        return jsonObject;
    }
    
    public static String getIdentifierKey(final String identifier) {
        String val = identifier;
        if (identifier.equalsIgnoreCase("data.device.full")) {
            val = "Device Usage";
        }
        return val;
    }
    
    public static String getTypeKey(final Integer type) {
        String val = type.toString();
        if (type.equals(DataUsageConstants.DataUsages.DataEntities.MOBILE_TYPE)) {
            val = "Mobile Data";
        }
        return val;
    }
}
