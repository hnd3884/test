package com.adventnet.client.components.xml;

import java.util.Map;
import java.util.logging.Level;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.db.migration.util.DBMigrationUtil;
import com.adventnet.persistence.Row;
import org.xml.sax.Attributes;
import java.util.logging.Logger;
import com.adventnet.persistence.xml.XmlRowTransformer;

public class ACSQLTableRowTransformer implements XmlRowTransformer
{
    private static final Logger OUT;
    
    public Row createRow(final String tname, final Attributes atts) {
        final String attr_dbName = atts.getValue("sqlfor");
        final String dbName = DBMigrationUtil.isDBMigrationRunning() ? DBMigrationUtil.getDestDBType().toString() : PersistenceInitializer.getConfigurationValue("DBName");
        ACSQLTableRowTransformer.OUT.log(Level.FINER, "createRow invoked for db {0} Persistence dbname is {1}", new Object[] { attr_dbName, dbName });
        if (attr_dbName == null || dbName.equalsIgnoreCase(attr_dbName)) {
            final Row row = new Row(tname);
            ACSQLTableRowTransformer.OUT.log(Level.FINER, "Returning the row {0}", row);
            return row;
        }
        return null;
    }
    
    public void setDisplayNames(final String tableName, final Map columnNameVsValue) {
    }
    
    public void setColumnNames(final String tableName, final Map nodeNameVsValue) {
    }
    
    static {
        OUT = Logger.getLogger(ACSQLTableRowTransformer.class.getName());
    }
}
