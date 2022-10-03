package com.me.mdm.server.updates;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;

public class ManagedUpdatesDataHandler
{
    protected Long addNewManagedUpdate(final JSONObject dataJson) throws Exception {
        final DataObject dO = MDMUtil.getPersistence().constructDataObject();
        final Row row = new Row("ManagedUpdates");
        row.set("UPDATE_NAME", (Object)String.valueOf(dataJson.get("UPDATE_NAME")));
        row.set("UPDATE_DESCRIPTION", (Object)String.valueOf(dataJson.get("UPDATE_DESCRIPTION")));
        row.set("UPDATE_TYPE", (Object)dataJson.getInt("UPDATE_TYPE"));
        row.set("UPDATE_PLATFORM", (Object)dataJson.getInt("UPDATE_PLATFORM"));
        row.set("ADDED_AT", (Object)dataJson.getLong("ADDED_AT"));
        dO.addRow(row);
        MDMUtil.getPersistence().update(dO);
        return (Long)dO.getFirstRow("ManagedUpdates").get("UPDATE_ID");
    }
    
    protected void modifyManagedUpdate(final JSONObject dataJson) throws Exception {
        final Long updateID = dataJson.getLong("UPDATE_ID");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedUpdates"));
        final Criteria criteria = new Criteria(Column.getColumn("ManagedUpdates", "UPDATE_ID"), (Object)updateID, 0);
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        final DataObject dO = MDMUtil.getPersistence().get(selectQuery);
        final Row row = dO.getFirstRow("ManagedUpdates");
        if (dataJson.has("UPDATE_NAME")) {
            row.set("UPDATE_NAME", (Object)String.valueOf(dataJson.get("UPDATE_NAME")));
        }
        if (dataJson.has("UPDATE_DESCRIPTION")) {
            row.set("UPDATE_DESCRIPTION", (Object)String.valueOf(dataJson.get("UPDATE_DESCRIPTION")));
        }
        if (dataJson.has("UPDATE_TYPE")) {
            row.set("UPDATE_TYPE", (Object)dataJson.getInt("UPDATE_TYPE"));
        }
        dO.updateRow(row);
        MDMUtil.getPersistence().update(dO);
    }
}
