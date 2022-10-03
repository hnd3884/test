package org.apache.tomcat.dbcp.dbcp2.datasources;

import java.sql.SQLException;
import javax.sql.PooledConnection;

interface PooledConnectionManager
{
    void invalidate(final PooledConnection p0) throws SQLException;
    
    void setPassword(final String p0);
    
    void closePool(final String p0) throws SQLException;
}
