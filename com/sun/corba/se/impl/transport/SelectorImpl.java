package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchThreadPoolException;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.ClosedSelectorException;
import java.io.IOException;
import java.util.Iterator;
import com.sun.corba.se.pept.transport.ReaderThread;
import com.sun.corba.se.pept.transport.ListenerThread;
import java.nio.channels.SelectionKey;
import com.sun.corba.se.pept.transport.EventHandler;
import java.util.Collections;
import java.util.ArrayList;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.pept.transport.Selector;

class SelectorImpl extends Thread implements Selector
{
    private ORB orb;
    private java.nio.channels.Selector selector;
    private long timeout;
    private List deferredRegistrations;
    private List interestOpsList;
    private HashMap listenerThreads;
    private Map readerThreads;
    private boolean selectorStarted;
    private volatile boolean closed;
    private ORBUtilSystemException wrapper;
    
    public SelectorImpl(final ORB orb) {
        this.orb = orb;
        this.selector = null;
        this.selectorStarted = false;
        this.timeout = 60000L;
        this.deferredRegistrations = new ArrayList();
        this.interestOpsList = new ArrayList();
        this.listenerThreads = new HashMap();
        this.readerThreads = Collections.synchronizedMap(new HashMap<Object, Object>());
        this.closed = false;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.transport");
    }
    
    @Override
    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }
    
    @Override
    public long getTimeout() {
        return this.timeout;
    }
    
    @Override
    public void registerInterestOps(final EventHandler eventHandler) {
        if (this.orb.transportDebugFlag) {
            this.dprint(".registerInterestOps:-> " + eventHandler);
        }
        final SelectionKey selectionKey = eventHandler.getSelectionKey();
        if (selectionKey.isValid()) {
            final SelectionKeyAndOp selectionKeyAndOp = new SelectionKeyAndOp(selectionKey, eventHandler.getInterestOps());
            synchronized (this.interestOpsList) {
                this.interestOpsList.add(selectionKeyAndOp);
            }
            try {
                if (this.selector != null) {
                    this.selector.wakeup();
                }
            }
            catch (final Throwable t) {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".registerInterestOps: selector.wakeup: ", t);
                }
            }
        }
        else {
            this.wrapper.selectionKeyInvalid(eventHandler.toString());
            if (this.orb.transportDebugFlag) {
                this.dprint(".registerInterestOps: EventHandler SelectionKey not valid " + eventHandler);
            }
        }
        if (this.orb.transportDebugFlag) {
            this.dprint(".registerInterestOps:<- ");
        }
    }
    
    @Override
    public void registerForEvent(final EventHandler eventHandler) {
        if (this.orb.transportDebugFlag) {
            this.dprint(".registerForEvent: " + eventHandler);
        }
        if (this.isClosed()) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".registerForEvent: closed: " + eventHandler);
            }
            return;
        }
        if (eventHandler.shouldUseSelectThreadToWait()) {
            synchronized (this.deferredRegistrations) {
                this.deferredRegistrations.add(eventHandler);
            }
            if (!this.selectorStarted) {
                this.startSelector();
            }
            this.selector.wakeup();
            return;
        }
        switch (eventHandler.getInterestOps()) {
            case 16: {
                this.createListenerThread(eventHandler);
                break;
            }
            case 1: {
                this.createReaderThread(eventHandler);
                break;
            }
            default: {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".registerForEvent: default: " + eventHandler);
                }
                throw new RuntimeException("SelectorImpl.registerForEvent: unknown interest ops");
            }
        }
    }
    
    @Override
    public void unregisterForEvent(final EventHandler eventHandler) {
        if (this.orb.transportDebugFlag) {
            this.dprint(".unregisterForEvent: " + eventHandler);
        }
        if (this.isClosed()) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".unregisterForEvent: closed: " + eventHandler);
            }
            return;
        }
        if (eventHandler.shouldUseSelectThreadToWait()) {
            final SelectionKey selectionKey;
            synchronized (this.deferredRegistrations) {
                selectionKey = eventHandler.getSelectionKey();
            }
            if (selectionKey != null) {
                selectionKey.cancel();
            }
            if (this.selector != null) {
                this.selector.wakeup();
            }
            return;
        }
        switch (eventHandler.getInterestOps()) {
            case 16: {
                this.destroyListenerThread(eventHandler);
                break;
            }
            case 1: {
                this.destroyReaderThread(eventHandler);
                break;
            }
            default: {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".unregisterForEvent: default: " + eventHandler);
                }
                throw new RuntimeException("SelectorImpl.uregisterForEvent: unknown interest ops");
            }
        }
    }
    
    @Override
    public void close() {
        if (this.orb.transportDebugFlag) {
            this.dprint(".close");
        }
        if (this.isClosed()) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".close: already closed");
            }
            return;
        }
        this.setClosed(true);
        final Iterator iterator = this.listenerThreads.values().iterator();
        while (iterator.hasNext()) {
            ((ListenerThread)iterator.next()).close();
        }
        final Iterator iterator2 = this.readerThreads.values().iterator();
        while (iterator2.hasNext()) {
            ((ReaderThread)iterator2.next()).close();
        }
        this.clearDeferredRegistrations();
        try {
            if (this.selector != null) {
                this.selector.wakeup();
            }
        }
        catch (final Throwable t) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".close: selector.wakeup: ", t);
            }
        }
    }
    
    @Override
    public void run() {
        this.setName("SelectorThread");
        while (!this.closed) {
            try {
                int select = 0;
                if (this.timeout == 0L && this.orb.transportDebugFlag) {
                    this.dprint(".run: Beginning of selection cycle");
                }
                this.handleDeferredRegistrations();
                this.enableInterestOps();
                try {
                    select = this.selector.select(this.timeout);
                }
                catch (final IOException ex) {
                    if (this.orb.transportDebugFlag) {
                        this.dprint(".run: selector.select: ", ex);
                    }
                }
                catch (final ClosedSelectorException ex2) {
                    if (this.orb.transportDebugFlag) {
                        this.dprint(".run: selector.select: ", ex2);
                    }
                    break;
                }
                if (this.closed) {
                    break;
                }
                final Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();
                if (this.orb.transportDebugFlag && iterator.hasNext()) {
                    this.dprint(".run: n = " + select);
                }
                while (iterator.hasNext()) {
                    final SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    final EventHandler eventHandler = (EventHandler)selectionKey.attachment();
                    try {
                        eventHandler.handleEvent();
                    }
                    catch (final Throwable t) {
                        if (!this.orb.transportDebugFlag) {
                            continue;
                        }
                        this.dprint(".run: eventHandler.handleEvent", t);
                    }
                }
                if (this.timeout != 0L || !this.orb.transportDebugFlag) {
                    continue;
                }
                this.dprint(".run: End of selection cycle");
            }
            catch (final Throwable t2) {
                if (!this.orb.transportDebugFlag) {
                    continue;
                }
                this.dprint(".run: ignoring", t2);
            }
        }
        try {
            if (this.selector != null) {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".run: selector.close ");
                }
                this.selector.close();
            }
        }
        catch (final Throwable t3) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".run: selector.close: ", t3);
            }
        }
    }
    
    private void clearDeferredRegistrations() {
        synchronized (this.deferredRegistrations) {
            final int size = this.deferredRegistrations.size();
            if (this.orb.transportDebugFlag) {
                this.dprint(".clearDeferredRegistrations:deferred list size == " + size);
            }
            for (int i = 0; i < size; ++i) {
                final EventHandler eventHandler = this.deferredRegistrations.get(i);
                if (this.orb.transportDebugFlag) {
                    this.dprint(".clearDeferredRegistrations: " + eventHandler);
                }
                final SelectableChannel channel = eventHandler.getChannel();
                try {
                    if (this.orb.transportDebugFlag) {
                        this.dprint(".clearDeferredRegistrations:close channel == " + channel);
                        this.dprint(".clearDeferredRegistrations:close channel class == " + channel.getClass().getName());
                    }
                    channel.close();
                    final SelectionKey selectionKey = eventHandler.getSelectionKey();
                    if (selectionKey != null) {
                        selectionKey.cancel();
                        selectionKey.attach(null);
                    }
                }
                catch (final IOException ex) {
                    if (this.orb.transportDebugFlag) {
                        this.dprint(".clearDeferredRegistrations: ", ex);
                    }
                }
            }
            this.deferredRegistrations.clear();
        }
    }
    
    private synchronized boolean isClosed() {
        return this.closed;
    }
    
    private synchronized void setClosed(final boolean closed) {
        this.closed = closed;
    }
    
    private void startSelector() {
        try {
            this.selector = java.nio.channels.Selector.open();
        }
        catch (final IOException ex) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".startSelector: Selector.open: IOException: ", ex);
            }
            final RuntimeException ex2 = new RuntimeException(".startSelector: Selector.open exception");
            ex2.initCause(ex);
            throw ex2;
        }
        this.setDaemon(true);
        this.start();
        this.selectorStarted = true;
        if (this.orb.transportDebugFlag) {
            this.dprint(".startSelector: selector.start completed.");
        }
    }
    
    private void handleDeferredRegistrations() {
        synchronized (this.deferredRegistrations) {
            for (int size = this.deferredRegistrations.size(), i = 0; i < size; ++i) {
                final EventHandler eventHandler = this.deferredRegistrations.get(i);
                if (this.orb.transportDebugFlag) {
                    this.dprint(".handleDeferredRegistrations: " + eventHandler);
                }
                final SelectableChannel channel = eventHandler.getChannel();
                SelectionKey register = null;
                try {
                    register = channel.register(this.selector, eventHandler.getInterestOps(), eventHandler);
                }
                catch (final ClosedChannelException ex) {
                    if (this.orb.transportDebugFlag) {
                        this.dprint(".handleDeferredRegistrations: ", ex);
                    }
                }
                eventHandler.setSelectionKey(register);
            }
            this.deferredRegistrations.clear();
        }
    }
    
    private void enableInterestOps() {
        synchronized (this.interestOpsList) {
            final int size = this.interestOpsList.size();
            if (size > 0) {
                if (this.orb.transportDebugFlag) {
                    this.dprint(".enableInterestOps:->");
                }
                for (int i = 0; i < size; ++i) {
                    final SelectionKeyAndOp selectionKeyAndOp = this.interestOpsList.get(i);
                    final SelectionKey selectionKey = selectionKeyAndOp.selectionKey;
                    if (selectionKey.isValid()) {
                        if (this.orb.transportDebugFlag) {
                            this.dprint(".enableInterestOps: " + selectionKeyAndOp);
                        }
                        selectionKey.interestOps(selectionKey.interestOps() | selectionKeyAndOp.keyOp);
                    }
                }
                this.interestOpsList.clear();
                if (this.orb.transportDebugFlag) {
                    this.dprint(".enableInterestOps:<-");
                }
            }
        }
    }
    
    private void createListenerThread(final EventHandler eventHandler) {
        if (this.orb.transportDebugFlag) {
            this.dprint(".createListenerThread: " + eventHandler);
        }
        final ListenerThreadImpl listenerThreadImpl = new ListenerThreadImpl(this.orb, eventHandler.getAcceptor(), this);
        this.listenerThreads.put(eventHandler, listenerThreadImpl);
        Throwable t = null;
        try {
            this.orb.getThreadPoolManager().getThreadPool(0).getWorkQueue(0).addWork(listenerThreadImpl);
        }
        catch (final NoSuchThreadPoolException ex) {
            t = ex;
        }
        catch (final NoSuchWorkQueueException ex2) {
            t = ex2;
        }
        if (t != null) {
            final RuntimeException ex3 = new RuntimeException(t.toString());
            ex3.initCause(t);
            throw ex3;
        }
    }
    
    private void destroyListenerThread(final EventHandler eventHandler) {
        if (this.orb.transportDebugFlag) {
            this.dprint(".destroyListenerThread: " + eventHandler);
        }
        final ListenerThread listenerThread = this.listenerThreads.get(eventHandler);
        if (listenerThread == null) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".destroyListenerThread: cannot find ListenerThread - ignoring.");
            }
            return;
        }
        this.listenerThreads.remove(eventHandler);
        listenerThread.close();
    }
    
    private void createReaderThread(final EventHandler eventHandler) {
        if (this.orb.transportDebugFlag) {
            this.dprint(".createReaderThread: " + eventHandler);
        }
        final ReaderThreadImpl readerThreadImpl = new ReaderThreadImpl(this.orb, eventHandler.getConnection(), this);
        this.readerThreads.put(eventHandler, readerThreadImpl);
        Throwable t = null;
        try {
            this.orb.getThreadPoolManager().getThreadPool(0).getWorkQueue(0).addWork(readerThreadImpl);
        }
        catch (final NoSuchThreadPoolException ex) {
            t = ex;
        }
        catch (final NoSuchWorkQueueException ex2) {
            t = ex2;
        }
        if (t != null) {
            final RuntimeException ex3 = new RuntimeException(t.toString());
            ex3.initCause(t);
            throw ex3;
        }
    }
    
    private void destroyReaderThread(final EventHandler eventHandler) {
        if (this.orb.transportDebugFlag) {
            this.dprint(".destroyReaderThread: " + eventHandler);
        }
        final ReaderThread readerThread = this.readerThreads.get(eventHandler);
        if (readerThread == null) {
            if (this.orb.transportDebugFlag) {
                this.dprint(".destroyReaderThread: cannot find ReaderThread - ignoring.");
            }
            return;
        }
        this.readerThreads.remove(eventHandler);
        readerThread.close();
    }
    
    private void dprint(final String s) {
        ORBUtility.dprint("SelectorImpl", s);
    }
    
    protected void dprint(final String s, final Throwable t) {
        this.dprint(s);
        t.printStackTrace(System.out);
    }
    
    private class SelectionKeyAndOp
    {
        public int keyOp;
        public SelectionKey selectionKey;
        
        public SelectionKeyAndOp(final SelectionKey selectionKey, final int keyOp) {
            this.selectionKey = selectionKey;
            this.keyOp = keyOp;
        }
    }
}
