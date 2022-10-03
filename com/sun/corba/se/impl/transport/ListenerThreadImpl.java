package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.pept.transport.ListenerThread;

public class ListenerThreadImpl implements ListenerThread, Work
{
    private ORB orb;
    private Acceptor acceptor;
    private Selector selector;
    private boolean keepRunning;
    private long enqueueTime;
    
    public ListenerThreadImpl(final ORB orb, final Acceptor acceptor, final Selector selector) {
        this.orb = orb;
        this.acceptor = acceptor;
        this.selector = selector;
        this.keepRunning = true;
    }
    
    @Override
    public Acceptor getAcceptor() {
        return this.acceptor;
    }
    
    @Override
    public void close() {
        if (this.orb.transportDebugFlag) {
            this.dprint(".close: " + this.acceptor);
        }
        this.keepRunning = false;
    }
    
    @Override
    public void doWork() {
        try {
            if (this.orb.transportDebugFlag) {
                this.dprint(".doWork: Start ListenerThread: " + this.acceptor);
            }
            while (this.keepRunning) {
                try {
                    if (this.orb.transportDebugFlag) {
                        this.dprint(".doWork: BEFORE ACCEPT CYCLE: " + this.acceptor);
                    }
                    this.acceptor.accept();
                    if (!this.orb.transportDebugFlag) {
                        continue;
                    }
                    this.dprint(".doWork: AFTER ACCEPT CYCLE: " + this.acceptor);
                }
                catch (final Throwable t) {
                    if (this.orb.transportDebugFlag) {
                        this.dprint(".doWork: Exception in accept: " + this.acceptor, t);
                    }
                    this.orb.getTransportManager().getSelector(0).unregisterForEvent(this.getAcceptor().getEventHandler());
                    this.getAcceptor().close();
                }
            }
        }
        finally {
            if (this.orb.transportDebugFlag) {
                this.dprint(".doWork: Terminated ListenerThread: " + this.acceptor);
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
        return "ListenerThread";
    }
    
    private void dprint(final String s) {
        ORBUtility.dprint("ListenerThreadImpl", s);
    }
    
    private void dprint(final String s, final Throwable t) {
        this.dprint(s);
        t.printStackTrace(System.out);
    }
}
