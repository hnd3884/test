package org.apache.tomcat.dbcp.pool2.impl;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

public class ThrowableCallStack implements CallStack
{
    private final String messageFormat;
    private final DateFormat dateFormat;
    private volatile Snapshot snapshot;
    
    public ThrowableCallStack(final String messageFormat, final boolean useTimestamp) {
        this.messageFormat = messageFormat;
        this.dateFormat = (useTimestamp ? new SimpleDateFormat(messageFormat) : null);
    }
    
    @Override
    public synchronized boolean printStackTrace(final PrintWriter writer) {
        final Snapshot snapshotRef = this.snapshot;
        if (snapshotRef == null) {
            return false;
        }
        String message;
        if (this.dateFormat == null) {
            message = this.messageFormat;
        }
        else {
            synchronized (this.dateFormat) {
                message = this.dateFormat.format(snapshotRef.timestampMillis);
            }
        }
        writer.println(message);
        snapshotRef.printStackTrace(writer);
        return true;
    }
    
    @Override
    public void fillInStackTrace() {
        this.snapshot = new Snapshot();
    }
    
    @Override
    public void clear() {
        this.snapshot = null;
    }
    
    private static class Snapshot extends Throwable
    {
        private static final long serialVersionUID = 1L;
        private final long timestampMillis;
        
        private Snapshot() {
            this.timestampMillis = System.currentTimeMillis();
        }
    }
}
