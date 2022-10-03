package com.me.devicemanagement.onpremise.tools.dbmigration.handler;

import com.adventnet.persistence.Row;
import java.util.logging.Logger;
import com.adventnet.db.migration.handler.AbstractDBMigrationHandler;

public class DCNativeSQLStringTableRowHandler extends AbstractDBMigrationHandler
{
    private static final Logger LOGGER;
    
    public Row preInvokeForInsert(final Row row) throws Exception {
        try {
            final Row dcNativeSQLStringRow = super.preInvokeForInsert(row);
            if (dcNativeSQLStringRow == null) {
                return dcNativeSQLStringRow;
            }
            DCNativeSQLStringTableRowHandler.LOGGER.info(row.toString());
            if (row.get("SQL_COMMAND") != null) {
                row.set("SQL_COMMAND", (Object)null);
                return row;
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(DCNativeSQLStringTableRowHandler.class.getName());
    }
}
