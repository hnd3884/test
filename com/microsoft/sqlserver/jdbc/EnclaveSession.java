package com.microsoft.sqlserver.jdbc;

import java.util.concurrent.atomic.AtomicLong;

class EnclaveSession
{
    private byte[] sessionID;
    private AtomicLong counter;
    private byte[] sessionSecret;
    
    EnclaveSession(final byte[] cs, final byte[] b) {
        this.sessionID = cs;
        this.sessionSecret = b;
        this.counter = new AtomicLong(0L);
    }
    
    byte[] getSessionID() {
        return this.sessionID;
    }
    
    byte[] getSessionSecret() {
        return this.sessionSecret;
    }
    
    synchronized long getCounter() {
        return this.counter.getAndIncrement();
    }
}
