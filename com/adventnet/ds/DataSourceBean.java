package com.adventnet.ds;

import com.adventnet.ds.query.QueryConstructionException;
import java.util.Collection;
import java.sql.SQLException;

public interface DataSourceBean
{
     <T extends ResultSetHandler> T executeQuery(final Class<T> p0, final String p1, final Object... p2) throws SQLException;
    
    int executeUpdate(final String p0, final Object... p1) throws SQLException;
    
    int[] executeBatch(final String p0, final Collection<Object[]> p1) throws SQLException;
    
    int executeDeleteSQL(final String p0, final Object... p1) throws SQLException, QueryConstructionException;
}
