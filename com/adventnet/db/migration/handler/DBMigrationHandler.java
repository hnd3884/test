package com.adventnet.db.migration.handler;

import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.ds.query.AlterTableQuery;
import com.adventnet.persistence.Row;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;

public interface DBMigrationHandler
{
    void setHandlerName(final String p0);
    
    String getHandlerName();
    
    boolean processTable(final String p0);
    
    void preInvokeForCreateTable(final String p0) throws Exception;
    
    void preInvokeForFetchdata(final SelectQuery p0) throws Exception;
    
    List<String> getSelectColumns(final SelectQuery p0) throws Exception;
    
    Row preInvokeForInsert(final Row p0) throws Exception;
    
    void postInvokeForCreateTable(final String p0) throws Exception;
    
    Operation handleException(final AlterTableQuery p0, final SQLException p1, final Connection p2) throws Exception;
    
    public enum Operation
    {
        RETRY, 
        STOP_MIGRATION;
    }
}
