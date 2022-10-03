package com.me.ems.onpremise.summaryserver.probe.sync.utils;

import java.util.Iterator;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.JSONArray;
import java.util.logging.Logger;

public class SSDataUpdateUtil
{
    private static SSDataUpdateUtil ssDataUpdateUtil;
    private static Logger out;
    
    public static SSDataUpdateUtil getInstance() {
        if (SSDataUpdateUtil.ssDataUpdateUtil == null) {
            SSDataUpdateUtil.ssDataUpdateUtil = new SSDataUpdateUtil();
        }
        return SSDataUpdateUtil.ssDataUpdateUtil;
    }
    
    public void updateSSData(final JSONArray jsonArray, final String tableName) {
        try {
            final DataObject dbObject = SyMUtil.getPersistence().get(tableName, (Criteria)null);
            final TableDefinition tableDefinition = MetaDataUtil.getTableDefinitionByName(tableName);
            final PrimaryKeyDefinition primaryKeyDefinition = tableDefinition.getPrimaryKey();
            final String column = primaryKeyDefinition.getColumnList().get(0);
            for (final Object object : jsonArray) {
                final JSONObject jsonObject = new JSONObject(object.toString());
                final Criteria criteria = new Criteria(new Column(tableName, column), jsonObject.get(column.toLowerCase()), 0);
                Row row = dbObject.getRow(tableName, criteria);
                boolean skipPk = true;
                if (row == null) {
                    row = new Row(tableName);
                    skipPk = false;
                }
                for (final Object keyStr : jsonObject.keySet()) {
                    final String key = (String)keyStr;
                    if (skipPk && key.equalsIgnoreCase(column)) {
                        continue;
                    }
                    final Object value = jsonObject.get(key);
                    row.set(key.toUpperCase(), value);
                }
                if (skipPk) {
                    dbObject.updateRow(row);
                }
                else {
                    dbObject.addRow(row);
                }
            }
            DataAccess.update(dbObject);
        }
        catch (final Exception e) {
            SSDataUpdateUtil.out.log(Level.INFO, " Exception while updating table data", e);
        }
    }
    
    static {
        SSDataUpdateUtil.out = Logger.getLogger("ProbeSyncLogger");
    }
}
