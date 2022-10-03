package javax.sql;

import java.sql.SQLException;
import java.io.PrintWriter;

public interface XADataSource
{
    PrintWriter getLogWriter() throws SQLException;
    
    int getLoginTimeout() throws SQLException;
    
    XAConnection getXAConnection() throws SQLException;
    
    XAConnection getXAConnection(final String p0, final String p1) throws SQLException;
    
    void setLogWriter(final PrintWriter p0) throws SQLException;
    
    void setLoginTimeout(final int p0) throws SQLException;
}
