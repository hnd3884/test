package com.adventnet.db.migration.handler;

import java.sql.PreparedStatement;
import java.util.Map;
import java.sql.ResultSetMetaData;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import java.util.List;
import java.io.IOException;

public interface NonMickeyTablesMigrationHandler
{
    void setHandlerName(final String p0);
    
    String getHandlerName();
    
    void initialize() throws IOException;
    
    boolean processTable(final String p0);
    
    String getSQLForCreateTable(final String p0);
    
    void preInvokeForCreateTable(final String p0, final String p1);
    
    List<String> getSelectColumns(final String p0) throws SQLException;
    
    String getSQLForSelect(final String p0, final List<String> p1, final boolean p2) throws QueryConstructionException, SQLException;
    
    String getSQLForInsertQuery(final String p0, final List<String> p1) throws QueryConstructionException;
    
    Map<String, Integer> getSQLTypesForInsert(final String p0, final ResultSetMetaData p1) throws SQLException;
    
    void setValueForInsert(final String p0, final PreparedStatement p1, final String p2, final int p3, final int p4, final Map<String, Object> p5) throws SQLException;
    
    String getSQLForCreateIndex(final String p0);
    
    void preInvokeForCreateIndex(final String p0, final String p1);
    
    void postInvokeForCreateIndex(final String p0, final String p1);
    
    String getSQLForCreatePrimaryKey(final String p0);
    
    void preInvokeForCreatePK(final String p0, final String p1);
    
    void postInvokeForCreatePK(final String p0, final String p1);
    
    String getSQLForCreateUniqueKey(final String p0);
    
    void preInvokeForCreateUK(final String p0, final String p1);
    
    void postInvokeForCreateUK(final String p0, final String p1);
    
    String getSQLForCreateForeignKey(final String p0);
    
    void preInvokeForCreateFK(final String p0, final String p1);
    
    void postInvokeForCreateFK(final String p0, final String p1);
    
    void postInvokeForCreateTable(final String p0);
    
    @Deprecated
    boolean revertOnFailure();
}
