package javax.transaction.xa;

public interface XAResource
{
    public static final int TMNOFLAGS = 0;
    public static final int TMONEPHASE = 1073741824;
    public static final int TMJOIN = 2097152;
    public static final int TMRESUME = 134217728;
    public static final int TMSUCCESS = 67108864;
    public static final int TMFAIL = 536870912;
    public static final int TMSUSPEND = 33554432;
    public static final int XA_RDONLY = 3;
    public static final int XA_OK = 0;
    public static final int TMSTARTRSCAN = 16777216;
    public static final int TMENDRSCAN = 8388608;
    
    void start(final Xid p0, final int p1) throws XAException;
    
    void end(final Xid p0, final int p1) throws XAException;
    
    int prepare(final Xid p0) throws XAException;
    
    void commit(final Xid p0, final boolean p1) throws XAException;
    
    void rollback(final Xid p0) throws XAException;
    
    void forget(final Xid p0) throws XAException;
    
    Xid[] recover(final int p0) throws XAException;
    
    boolean isSameRM(final XAResource p0) throws XAException;
    
    int getTransactionTimeout() throws XAException;
    
    boolean setTransactionTimeout(final int p0) throws XAException;
}
