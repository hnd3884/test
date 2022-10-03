package javax.sql;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Connection;

public interface DataSource
{
    Connection getConnection() throws SQLException;
    
    Connection getConnection(final String p0, final String p1) throws SQLException;
    
    PrintWriter getLogWriter() throws SQLException;
    
    int getLoginTimeout() throws SQLException;
    
    void setLogWriter(final PrintWriter p0) throws SQLException;
    
    void setLoginTimeout(final int p0) throws SQLException;
}
