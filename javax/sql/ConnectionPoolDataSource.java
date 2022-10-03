package javax.sql;

import java.sql.SQLException;
import java.io.PrintWriter;

public interface ConnectionPoolDataSource
{
    PrintWriter getLogWriter() throws SQLException;
    
    int getLoginTimeout() throws SQLException;
    
    PooledConnection getPooledConnection() throws SQLException;
    
    PooledConnection getPooledConnection(final String p0, final String p1) throws SQLException;
    
    void setLogWriter(final PrintWriter p0) throws SQLException;
    
    void setLoginTimeout(final int p0) throws SQLException;
}
