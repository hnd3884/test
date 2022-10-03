package sun.rmi.transport;

import java.util.HashMap;
import sun.security.action.GetLongAction;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.rmi.runtime.NewThreadAction;
import java.rmi.dgc.VMID;
import java.rmi.server.ObjID;
import java.rmi.server.ExportException;
import sun.rmi.runtime.Log;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import sun.misc.GC;
import java.lang.ref.ReferenceQueue;
import java.util.Map;

public final class ObjectTable
{
    private static final long gcInterval;
    private static final Object tableLock;
    private static final Map<ObjectEndpoint, Target> objTable;
    private static final Map<WeakRef, Target> implTable;
    private static final Object keepAliveLock;
    private static int keepAliveCount;
    private static Thread reaper;
    static final ReferenceQueue<Object> reapQueue;
    private static GC.LatencyRequest gcLatencyRequest;
    
    private ObjectTable() {
    }
    
    static Target getTarget(final ObjectEndpoint objectEndpoint) {
        synchronized (ObjectTable.tableLock) {
            return ObjectTable.objTable.get(objectEndpoint);
        }
    }
    
    public static Target getTarget(final Remote remote) {
        synchronized (ObjectTable.tableLock) {
            return ObjectTable.implTable.get(new WeakRef(remote));
        }
    }
    
    public static Remote getStub(final Remote remote) throws NoSuchObjectException {
        final Target target = getTarget(remote);
        if (target == null) {
            throw new NoSuchObjectException("object not exported");
        }
        return target.getStub();
    }
    
    public static boolean unexportObject(final Remote remote, final boolean b) throws NoSuchObjectException {
        synchronized (ObjectTable.tableLock) {
            final Target target = getTarget(remote);
            if (target == null) {
                throw new NoSuchObjectException("object not exported");
            }
            if (target.unexport(b)) {
                removeTarget(target);
                return true;
            }
            return false;
        }
    }
    
    static void putTarget(final Target target) throws ExportException {
        final ObjectEndpoint objectEndpoint = target.getObjectEndpoint();
        final WeakRef weakImpl = target.getWeakImpl();
        if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
            DGCImpl.dgcLog.log(Log.VERBOSE, "add object " + objectEndpoint);
        }
        synchronized (ObjectTable.tableLock) {
            if (target.getImpl() != null) {
                if (ObjectTable.objTable.containsKey(objectEndpoint)) {
                    throw new ExportException("internal error: ObjID already in use");
                }
                if (ObjectTable.implTable.containsKey(weakImpl)) {
                    throw new ExportException("object already exported");
                }
                ObjectTable.objTable.put(objectEndpoint, target);
                ObjectTable.implTable.put(weakImpl, target);
                if (!target.isPermanent()) {
                    incrementKeepAliveCount();
                }
            }
        }
    }
    
    private static void removeTarget(final Target target) {
        final ObjectEndpoint objectEndpoint = target.getObjectEndpoint();
        final WeakRef weakImpl = target.getWeakImpl();
        if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
            DGCImpl.dgcLog.log(Log.VERBOSE, "remove object " + objectEndpoint);
        }
        ObjectTable.objTable.remove(objectEndpoint);
        ObjectTable.implTable.remove(weakImpl);
        target.markRemoved();
    }
    
    static void referenced(final ObjID objID, final long n, final VMID vmid) {
        synchronized (ObjectTable.tableLock) {
            final Target target = ObjectTable.objTable.get(new ObjectEndpoint(objID, Transport.currentTransport()));
            if (target != null) {
                target.referenced(n, vmid);
            }
        }
    }
    
    static void unreferenced(final ObjID objID, final long n, final VMID vmid, final boolean b) {
        synchronized (ObjectTable.tableLock) {
            final Target target = ObjectTable.objTable.get(new ObjectEndpoint(objID, Transport.currentTransport()));
            if (target != null) {
                target.unreferenced(n, vmid, b);
            }
        }
    }
    
    static void incrementKeepAliveCount() {
        synchronized (ObjectTable.keepAliveLock) {
            ++ObjectTable.keepAliveCount;
            if (ObjectTable.reaper == null) {
                (ObjectTable.reaper = AccessController.doPrivileged((PrivilegedAction<Thread>)new NewThreadAction(new Reaper(), "Reaper", false))).start();
            }
            if (ObjectTable.gcLatencyRequest == null) {
                ObjectTable.gcLatencyRequest = GC.requestLatency(ObjectTable.gcInterval);
            }
        }
    }
    
    static void decrementKeepAliveCount() {
        synchronized (ObjectTable.keepAliveLock) {
            --ObjectTable.keepAliveCount;
            if (ObjectTable.keepAliveCount == 0) {
                if (ObjectTable.reaper == null) {
                    throw new AssertionError();
                }
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        ObjectTable.reaper.interrupt();
                        return null;
                    }
                });
                ObjectTable.reaper = null;
                ObjectTable.gcLatencyRequest.cancel();
                ObjectTable.gcLatencyRequest = null;
            }
        }
    }
    
    static {
        gcInterval = AccessController.doPrivileged((PrivilegedAction<Long>)new GetLongAction("sun.rmi.dgc.server.gcInterval", 3600000L));
        tableLock = new Object();
        objTable = new HashMap<ObjectEndpoint, Target>();
        implTable = new HashMap<WeakRef, Target>();
        keepAliveLock = new Object();
        ObjectTable.keepAliveCount = 0;
        ObjectTable.reaper = null;
        reapQueue = new ReferenceQueue<Object>();
        ObjectTable.gcLatencyRequest = null;
    }
    
    private static class Reaper implements Runnable
    {
        @Override
        public void run() {
            try {
                do {
                    final WeakRef weakRef = (WeakRef)ObjectTable.reapQueue.remove();
                    synchronized (ObjectTable.tableLock) {
                        final Target target = ObjectTable.implTable.get(weakRef);
                        if (target == null) {
                            continue;
                        }
                        if (!target.isEmpty()) {
                            throw new Error("object with known references collected");
                        }
                        if (target.isPermanent()) {
                            throw new Error("permanent object collected");
                        }
                        removeTarget(target);
                    }
                } while (!Thread.interrupted());
            }
            catch (final InterruptedException ex) {}
        }
    }
}
