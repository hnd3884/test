package com.microsoft.sqlserver.jdbc;

import java.util.UUID;

class ActivityId
{
    private final UUID id;
    private final Thread thread;
    private long sequence;
    private boolean isSentToServer;
    
    ActivityId(final Thread thread) {
        this.id = UUID.randomUUID();
        this.thread = thread;
        this.sequence = 0L;
        this.isSentToServer = false;
    }
    
    Thread getThread() {
        return this.thread;
    }
    
    UUID getId() {
        return this.id;
    }
    
    long getSequence() {
        return this.sequence;
    }
    
    void increment() {
        if (this.sequence < 4294967295L) {
            ++this.sequence;
        }
        else {
            this.sequence = 0L;
        }
        this.isSentToServer = false;
    }
    
    void setSentFlag() {
        this.isSentToServer = true;
    }
    
    boolean isSentToServer() {
        return this.isSentToServer;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.id.toString());
        sb.append("-");
        sb.append(this.sequence);
        return sb.toString();
    }
}
