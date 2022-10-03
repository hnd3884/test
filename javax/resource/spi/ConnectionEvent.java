package javax.resource.spi;

import java.io.Serializable;
import java.util.EventObject;

public class ConnectionEvent extends EventObject implements Serializable
{
    public static final int CONNECTION_CLOSED = 1;
    public static final int LOCAL_TRANSACTION_STARTED = 2;
    public static final int LOCAL_TRANSACTION_COMMITTED = 3;
    public static final int LOCAL_TRANSACTION_ROLLEDBACK = 4;
    public static final int CONNECTION_ERROR_OCCURRED = 5;
    protected int id;
    private Exception e;
    private Object connectionHandle;
    
    public ConnectionEvent(final ManagedConnection source, final int eid) {
        super(source);
        this.e = null;
        this.connectionHandle = null;
        this.id = eid;
    }
    
    public ConnectionEvent(final ManagedConnection source, final int eid, final Exception exception) {
        super(source);
        this.e = null;
        this.connectionHandle = null;
        this.id = eid;
        this.e = exception;
    }
    
    public int getId() {
        return this.id;
    }
    
    public Exception getException() {
        return this.e;
    }
    
    public void setConnectionHandle(final Object connectionHandle) {
        this.connectionHandle = connectionHandle;
    }
    
    public Object getConnectionHandle() {
        return this.connectionHandle;
    }
}
