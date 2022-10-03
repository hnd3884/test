package javax.sql;

import java.sql.Connection;
import java.sql.SQLException;

public interface PooledConnection
{
    void addConnectionEventListener(final ConnectionEventListener p0);
    
    void close() throws SQLException;
    
    Connection getConnection() throws SQLException;
    
    void removeConnectionEventListener(final ConnectionEventListener p0);
}
