package com.sun.jndi.ldap.pool;

final class ConnectionDesc
{
    private static final boolean debug;
    static final byte BUSY = 0;
    static final byte IDLE = 1;
    static final byte EXPIRED = 2;
    private final PooledConnection conn;
    private byte state;
    private long idleSince;
    private long useCount;
    
    ConnectionDesc(final PooledConnection conn) {
        this.state = 1;
        this.useCount = 0L;
        this.conn = conn;
    }
    
    ConnectionDesc(final PooledConnection conn, final boolean b) {
        this.state = 1;
        this.useCount = 0L;
        this.conn = conn;
        if (b) {
            this.state = 0;
            ++this.useCount;
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof ConnectionDesc && ((ConnectionDesc)o).conn == this.conn;
    }
    
    @Override
    public int hashCode() {
        return this.conn.hashCode();
    }
    
    synchronized boolean release() {
        this.d("release()");
        if (this.state == 0) {
            this.state = 1;
            this.idleSince = System.currentTimeMillis();
            return true;
        }
        return false;
    }
    
    synchronized PooledConnection tryUse() {
        this.d("tryUse()");
        if (this.state == 1) {
            this.state = 0;
            ++this.useCount;
            return this.conn;
        }
        return null;
    }
    
    synchronized boolean expire(final long n) {
        if (this.state == 1 && this.idleSince < n) {
            this.d("expire(): expired");
            this.state = 2;
            this.conn.closeConnection();
            return true;
        }
        this.d("expire(): not expired");
        return false;
    }
    
    @Override
    public String toString() {
        return this.conn.toString() + " " + ((this.state == 0) ? "busy" : ((this.state == 1) ? "idle" : "expired"));
    }
    
    int getState() {
        return this.state;
    }
    
    long getUseCount() {
        return this.useCount;
    }
    
    private void d(final String s) {
        if (ConnectionDesc.debug) {
            System.err.println("ConnectionDesc." + s + " " + this.toString());
        }
    }
    
    static {
        debug = Pool.debug;
    }
}
