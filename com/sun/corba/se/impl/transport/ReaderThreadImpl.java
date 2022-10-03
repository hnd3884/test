package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.pept.transport.ReaderThread;

public class ReaderThreadImpl implements ReaderThread, Work
{
    private ORB orb;
    private Connection connection;
    private Selector selector;
    private boolean keepRunning;
    private long enqueueTime;
    
    public ReaderThreadImpl(final ORB orb, final Connection connection, final Selector selector) {
        this.orb = orb;
        this.connection = connection;
        this.selector = selector;
        this.keepRunning = true;
    }
    
    @Override
    public Connection getConnection() {
        return this.connection;
    }
    
    @Override
    public void close() {
        if (this.orb.transportDebugFlag) {
            this.dprint(".close: " + this.connection);
        }
        this.keepRunning = false;
    }
    
    @Override
    public void doWork() {
        try {
            if (this.orb.transportDebugFlag) {
                this.dprint(".doWork: Start ReaderThread: " + this.connection);
            }
            while (this.keepRunning) {
                try {
                    if (this.orb.transportDebugFlag) {
                        this.dprint(".doWork: Start ReaderThread cycle: " + this.connection);
                    }
                    if (this.connection.read()) {
                        return;
                    }
                    if (!this.orb.transportDebugFlag) {
                        continue;
                    }
                    this.dprint(".doWork: End ReaderThread cycle: " + this.connection);
                }
                catch (final Throwable t) {
                    if (this.orb.transportDebugFlag) {
                        this.dprint(".doWork: exception in read: " + this.connection, t);
                    }
                    this.orb.getTransportManager().getSelector(0).unregisterForEvent(this.getConnection().getEventHandler());
                    this.getConnection().close();
                }
            }
        }
        finally {
            if (this.orb.transportDebugFlag) {
                this.dprint(".doWork: Terminated ReaderThread: " + this.connection);
            }
        }
    }
    
    @Override
    public void setEnqueueTime(final long enqueueTime) {
        this.enqueueTime = enqueueTime;
    }
    
    @Override
    public long getEnqueueTime() {
        return this.enqueueTime;
    }
    
    @Override
    public String getName() {
        return "ReaderThread";
    }
    
    private void dprint(final String s) {
        ORBUtility.dprint("ReaderThreadImpl", s);
    }
    
    protected void dprint(final String s, final Throwable t) {
        this.dprint(s);
        t.printStackTrace(System.out);
    }
}
