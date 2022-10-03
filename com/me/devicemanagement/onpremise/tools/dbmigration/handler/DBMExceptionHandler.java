package com.me.devicemanagement.onpremise.tools.dbmigration.handler;

import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.ds.query.AlterOperation;
import java.util.logging.Level;
import com.adventnet.db.migration.handler.DBMigrationHandler;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.ds.query.AlterTableQuery;
import java.util.logging.Logger;
import com.adventnet.db.migration.handler.AbstractDBMigrationHandler;

public class DBMExceptionHandler extends AbstractDBMigrationHandler
{
    Logger logger;
    
    public DBMExceptionHandler() {
        this.logger = Logger.getLogger(DBMExceptionHandler.class.getName());
    }
    
    public DBMigrationHandler.Operation handleException(final AlterTableQuery alterQuery, final SQLException sqle, final Connection dstConnection) throws Exception {
        this.logger.warning("Exception received &&&&&&&&&&&&&&&&&&&&&&&&&&&&& ");
        if (alterQuery.getOperationType() == 6) {
            this.logger.log(Level.INFO, "Handling exception in ADD FOREIGN KEY constraint");
            final AlterOperation alterOperation = alterQuery.getAlterOperations().get(0);
            final int affectedRows = this.cleanupAbandonedChilds((ForeignKeyDefinition)alterOperation.getAlterObject());
            this.logger.log(Level.INFO, "Number of rows affected is : " + affectedRows);
            this.logger.warning("No of rows affected...");
            return DBMigrationHandler.Operation.RETRY;
        }
        return super.handleException(alterQuery, sqle, dstConnection);
    }
}
