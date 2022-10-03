package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.CORBA.INTERNAL;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchThreadPoolException;
import java.nio.channels.SelectionKey;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.pept.transport.EventHandler;

public abstract class EventHandlerBase implements EventHandler
{
    protected ORB orb;
    protected Work work;
    protected boolean useWorkerThreadForEvent;
    protected boolean useSelectThreadToWait;
    protected SelectionKey selectionKey;
    
    @Override
    public void setUseSelectThreadToWait(final boolean useSelectThreadToWait) {
        this.useSelectThreadToWait = useSelectThreadToWait;
    }
    
    @Override
    public boolean shouldUseSelectThreadToWait() {
        return this.useSelectThreadToWait;
    }
    
    @Override
    public void setSelectionKey(final SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }
    
    @Override
    public SelectionKey getSelectionKey() {
        return this.selectionKey;
    }
    
    @Override
    public void handleEvent() {
        if (this.orb.transportDebugFlag) {
            this.dprint(".handleEvent->: " + this);
        }
        this.getSelectionKey().interestOps(this.getSelectionKey().interestOps() & ~this.getInterestOps());
        if (this.shouldUseWorkerThreadForEvent()) {
            Throwable t = null;
            try {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".handleEvent: addWork to pool: 0");
                }
                this.orb.getThreadPoolManager().getThreadPool(0).getWorkQueue(0).addWork(this.getWork());
            }
            catch (final NoSuchThreadPoolException ex) {
                t = ex;
            }
            catch (final NoSuchWorkQueueException ex2) {
                t = ex2;
            }
            if (t != null) {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".handleEvent: " + t);
                }
                final INTERNAL internal = new INTERNAL("NoSuchThreadPoolException");
                internal.initCause(t);
                throw internal;
            }
        }
        else {
            if (this.orb.transportDebugFlag) {
                this.dprint(".handleEvent: doWork");
            }
            this.getWork().doWork();
        }
        if (this.orb.transportDebugFlag) {
            this.dprint(".handleEvent<-: " + this);
        }
    }
    
    @Override
    public boolean shouldUseWorkerThreadForEvent() {
        return this.useWorkerThreadForEvent;
    }
    
    @Override
    public void setUseWorkerThreadForEvent(final boolean useWorkerThreadForEvent) {
        this.useWorkerThreadForEvent = useWorkerThreadForEvent;
    }
    
    @Override
    public void setWork(final Work work) {
        this.work = work;
    }
    
    @Override
    public Work getWork() {
        return this.work;
    }
    
    private void dprint(final String s) {
        ORBUtility.dprint("EventHandlerBase", s);
    }
}
