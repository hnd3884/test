package javax.sql;

import java.sql.SQLException;
import java.util.EventObject;

public class ConnectionEvent extends EventObject
{
    private SQLException ex;
    
    public ConnectionEvent(final PooledConnection pooledConnection) {
        super(pooledConnection);
        this.ex = null;
    }
    
    public ConnectionEvent(final PooledConnection pooledConnection, final SQLException e) {
        super(pooledConnection);
        this.ex = null;
        this.ex = e;
    }
    
    public SQLException getSQLException() {
        return this.ex;
    }
}
