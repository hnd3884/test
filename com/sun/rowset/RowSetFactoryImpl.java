package com.sun.rowset;

import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.JoinRowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.FilteredRowSet;
import java.sql.SQLException;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;

public final class RowSetFactoryImpl implements RowSetFactory
{
    @Override
    public CachedRowSet createCachedRowSet() throws SQLException {
        return new CachedRowSetImpl();
    }
    
    @Override
    public FilteredRowSet createFilteredRowSet() throws SQLException {
        return new FilteredRowSetImpl();
    }
    
    @Override
    public JdbcRowSet createJdbcRowSet() throws SQLException {
        return new JdbcRowSetImpl();
    }
    
    @Override
    public JoinRowSet createJoinRowSet() throws SQLException {
        return new JoinRowSetImpl();
    }
    
    @Override
    public WebRowSet createWebRowSet() throws SQLException {
        return new WebRowSetImpl();
    }
}
