package com.me.devicemanagement.onpremise.server.sql;

import java.util.Map;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.sql.ValidateDCNativeSQL;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.persistence.Row;
import org.xml.sax.Attributes;
import java.util.logging.Logger;
import com.adventnet.persistence.xml.DefaultRowTransformer;

public class DCNativeSQLTableRowTransformer extends DefaultRowTransformer
{
    private static final Logger OUT;
    
    public Row createRow(final String tname, final Attributes atts) {
        String attr_dbName = atts.getValue("sqlfor");
        final String dbName = PersistenceInitializer.getConfigurationValue("DBName");
        DCNativeSQLTableRowTransformer.OUT.log(Level.FINER, "createRow invoked for db {0} Persistence dbname is {1}", new Object[] { attr_dbName, dbName });
        if (attr_dbName == null) {
            throw new IllegalArgumentException("sqlfor attribute is mandatory.");
        }
        attr_dbName = attr_dbName.toLowerCase();
        attr_dbName = attr_dbName.replaceAll(" ", "");
        final ArrayList arrDBNames = SyMUtil.getInstance().splitToArrayList((CharSequence)attr_dbName, ",");
        ValidateDCNativeSQL.getInstance().validateSQLFORAttribute(arrDBNames);
        if (arrDBNames.contains("common") || arrDBNames.contains(dbName)) {
            final Row row = new Row(tname);
            DCNativeSQLTableRowTransformer.OUT.log(Level.FINER, "Returning the row {0}", row);
            return row;
        }
        return null;
    }
    
    public void setDisplayNames(final String string, final Map map) {
    }
    
    public void setColumnNames(final String string, final Map map) {
    }
    
    static {
        OUT = Logger.getLogger(DCNativeSQLTableRowTransformer.class.getName());
    }
}
