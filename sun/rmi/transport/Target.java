package sun.rmi.transport;

import java.lang.ref.Reference;
import java.rmi.NoSuchObjectException;
import java.util.Enumeration;
import java.security.PrivilegedAction;
import sun.rmi.runtime.NewThreadAction;
import java.rmi.server.Unreferenced;
import sun.rmi.runtime.Log;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.util.Hashtable;
import java.rmi.dgc.VMID;
import java.util.Vector;
import java.rmi.Remote;
import sun.rmi.server.Dispatcher;
import java.rmi.server.ObjID;

public final class Target
{
    private final ObjID id;
    private final boolean permanent;
    private final WeakRef weakImpl;
    private volatile Dispatcher disp;
    private final Remote stub;
    private final Vector<VMID> refSet;
    private final Hashtable<VMID, SequenceEntry> sequenceTable;
    private final AccessControlContext acc;
    private final ClassLoader ccl;
    private int callCount;
    private boolean removed;
    private volatile Transport exportedTransport;
    private static int nextThreadNum;
    
    public Target(final Remote remote, final Dispatcher disp, final Remote stub, final ObjID id, final boolean permanent) {
        this.refSet = new Vector<VMID>();
        this.sequenceTable = new Hashtable<VMID, SequenceEntry>(5);
        this.callCount = 0;
        this.removed = false;
        this.exportedTransport = null;
        this.weakImpl = new WeakRef(remote, ObjectTable.reapQueue);
        this.disp = disp;
        this.stub = stub;
        this.id = id;
        this.acc = AccessController.getContext();
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        final ClassLoader classLoader = remote.getClass().getClassLoader();
        if (checkLoaderAncestry(contextClassLoader, classLoader)) {
            this.ccl = contextClassLoader;
        }
        else {
            this.ccl = classLoader;
        }
        this.permanent = permanent;
        if (permanent) {
            this.pinImpl();
        }
    }
    
    private static boolean checkLoaderAncestry(final ClassLoader classLoader, final ClassLoader classLoader2) {
        if (classLoader2 == null) {
            return true;
        }
        if (classLoader == null) {
            return false;
        }
        for (ClassLoader parent = classLoader; parent != null; parent = parent.getParent()) {
            if (parent == classLoader2) {
                return true;
            }
        }
        return false;
    }
    
    public Remote getStub() {
        return this.stub;
    }
    
    ObjectEndpoint getObjectEndpoint() {
        return new ObjectEndpoint(this.id, this.exportedTransport);
    }
    
    WeakRef getWeakImpl() {
        return this.weakImpl;
    }
    
    Dispatcher getDispatcher() {
        return this.disp;
    }
    
    AccessControlContext getAccessControlContext() {
        return this.acc;
    }
    
    ClassLoader getContextClassLoader() {
        return this.ccl;
    }
    
    Remote getImpl() {
        return ((Reference<Remote>)this.weakImpl).get();
    }
    
    boolean isPermanent() {
        return this.permanent;
    }
    
    synchronized void pinImpl() {
        this.weakImpl.pin();
    }
    
    synchronized void unpinImpl() {
        if (!this.permanent && this.refSet.isEmpty()) {
            this.weakImpl.unpin();
        }
    }
    
    void setExportedTransport(final Transport exportedTransport) {
        if (this.exportedTransport == null) {
            this.exportedTransport = exportedTransport;
        }
    }
    
    synchronized void referenced(final long n, final VMID vmid) {
        final SequenceEntry sequenceEntry = this.sequenceTable.get(vmid);
        if (sequenceEntry == null) {
            this.sequenceTable.put(vmid, new SequenceEntry(n));
        }
        else {
            if (sequenceEntry.sequenceNum >= n) {
                return;
            }
            sequenceEntry.update(n);
        }
        if (!this.refSet.contains(vmid)) {
            this.pinImpl();
            if (this.getImpl() == null) {
                return;
            }
            if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
                DGCImpl.dgcLog.log(Log.VERBOSE, "add to dirty set: " + vmid);
            }
            this.refSet.addElement(vmid);
            DGCImpl.getDGCImpl().registerTarget(vmid, this);
        }
    }
    
    synchronized void unreferenced(final long n, final VMID vmid, final boolean b) {
        final SequenceEntry sequenceEntry = this.sequenceTable.get(vmid);
        if (sequenceEntry == null || sequenceEntry.sequenceNum > n) {
            return;
        }
        if (b) {
            sequenceEntry.retain(n);
        }
        else if (!sequenceEntry.keep) {
            this.sequenceTable.remove(vmid);
        }
        if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
            DGCImpl.dgcLog.log(Log.VERBOSE, "remove from dirty set: " + vmid);
        }
        this.refSetRemove(vmid);
    }
    
    private synchronized void refSetRemove(final VMID vmid) {
        DGCImpl.getDGCImpl().unregisterTarget(vmid, this);
        if (this.refSet.removeElement(vmid) && this.refSet.isEmpty()) {
            if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
                DGCImpl.dgcLog.log(Log.VERBOSE, "reference set is empty: target = " + this);
            }
            final Remote impl = this.getImpl();
            if (impl instanceof Unreferenced) {
                final Unreferenced unreferenced = (Unreferenced)impl;
                AccessController.doPrivileged((PrivilegedAction<Thread>)new NewThreadAction(() -> {
                    Thread.currentThread().setContextClassLoader(this.ccl);
                    AccessController.doPrivileged(() -> {
                        unreferenced2.unreferenced();
                        return null;
                    }, this.acc);
                    return;
                }, "Unreferenced-" + Target.nextThreadNum++, false, true)).start();
            }
            this.unpinImpl();
        }
    }
    
    synchronized boolean unexport(final boolean b) {
        if (b || this.callCount == 0 || this.disp == null) {
            this.disp = null;
            this.unpinImpl();
            final DGCImpl dgcImpl = DGCImpl.getDGCImpl();
            final Enumeration<VMID> elements = this.refSet.elements();
            while (elements.hasMoreElements()) {
                dgcImpl.unregisterTarget(elements.nextElement(), this);
            }
            return true;
        }
        return false;
    }
    
    synchronized void markRemoved() {
        if (this.removed) {
            throw new AssertionError();
        }
        this.removed = true;
        if (!this.permanent && this.callCount == 0) {
            ObjectTable.decrementKeepAliveCount();
        }
        if (this.exportedTransport != null) {
            this.exportedTransport.targetUnexported();
        }
    }
    
    synchronized void incrementCallCount() throws NoSuchObjectException {
        if (this.disp != null) {
            ++this.callCount;
            return;
        }
        throw new NoSuchObjectException("object not accepting new calls");
    }
    
    synchronized void decrementCallCount() {
        final int callCount = this.callCount - 1;
        this.callCount = callCount;
        if (callCount < 0) {
            throw new Error("internal error: call count less than zero");
        }
        if (!this.permanent && this.removed && this.callCount == 0) {
            ObjectTable.decrementKeepAliveCount();
        }
    }
    
    boolean isEmpty() {
        return this.refSet.isEmpty();
    }
    
    public synchronized void vmidDead(final VMID vmid) {
        if (DGCImpl.dgcLog.isLoggable(Log.BRIEF)) {
            DGCImpl.dgcLog.log(Log.BRIEF, "removing endpoint " + vmid + " from reference set");
        }
        this.sequenceTable.remove(vmid);
        this.refSetRemove(vmid);
    }
    
    static {
        Target.nextThreadNum = 0;
    }
}
