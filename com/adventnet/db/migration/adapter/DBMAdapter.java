package com.adventnet.db.migration.adapter;

import com.adventnet.db.util.CreateSchema;
import com.adventnet.db.persistence.metadata.TableDefinition;
import javax.sql.DataSource;
import java.util.List;
import com.adventnet.db.migration.handler.DBMigrationHandlerFactory;

public interface DBMAdapter
{
    void initialize(final DBMigrationHandlerFactory p0) throws Exception;
    
    void migrateTablesExcept(final List<String> p0) throws Exception;
    
    void migrateTables(final List<String> p0) throws Exception;
    
    void migrateDataBase(final DataSource p0, final DataSource p1) throws Exception;
    
    void createMickeyTable(final String p0, final TableDefinition p1) throws Exception;
    
    void createMickeyTableConstraints(final String p0, final TableDefinition p1) throws Exception;
    
    void createNonMickeyTable(final String p0);
    
    void createNonMickeyTableConstraints(final String p0);
    
    List<String> createSchemaConfTable(final CreateSchema p0) throws Exception;
    
    void createSchemaConfTableConstraints(final String p0, final CreateSchema p1);
    
    void createSchemaConfTableConstraints(final CreateSchema p0) throws Exception;
    
    boolean isSkippedTable(final String p0);
    
    void waitForTaskCompletion() throws InterruptedException;
    
    void shutdownWorkerPool(final boolean p0) throws Exception;
}
