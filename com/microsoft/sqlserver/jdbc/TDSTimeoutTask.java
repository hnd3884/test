package com.microsoft.sqlserver.jdbc;

import java.util.logging.Level;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

class TDSTimeoutTask implements Runnable
{
    private static final AtomicLong COUNTER;
    private final UUID connectionId;
    private final TDSCommand command;
    private final SQLServerConnection sqlServerConnection;
    
    public TDSTimeoutTask(final TDSCommand command, final SQLServerConnection sqlServerConnection) {
        this.connectionId = ((sqlServerConnection == null) ? null : sqlServerConnection.getClientConIdInternal());
        this.command = command;
        this.sqlServerConnection = sqlServerConnection;
    }
    
    @Override
    public final void run() {
        final String name = "mssql-timeout-task-" + TDSTimeoutTask.COUNTER.incrementAndGet() + "-" + this.connectionId;
        final Thread thread = new Thread(this::interrupt, name);
        thread.setDaemon(true);
        thread.start();
    }
    
    protected void interrupt() {
        try {
            if (null == this.command) {
                if (null != this.sqlServerConnection) {
                    this.sqlServerConnection.terminate(3, SQLServerException.getErrString("R_connectionIsClosed"));
                }
            }
            else {
                this.command.interrupt(SQLServerException.getErrString("R_queryTimedOut"));
            }
        }
        catch (final SQLServerException e) {
            assert null != this.command;
            this.command.log(Level.WARNING, "Command could not be timed out. Reason: " + e.getMessage());
        }
    }
    
    static {
        COUNTER = new AtomicLong(0L);
    }
}
