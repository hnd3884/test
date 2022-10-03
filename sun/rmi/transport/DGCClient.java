package sun.rmi.transport;

import java.lang.ref.PhantomReference;
import sun.rmi.runtime.Log;
import java.io.InvalidClassException;
import java.rmi.UnmarshalException;
import java.rmi.dgc.Lease;
import java.util.Iterator;
import java.util.Collection;
import sun.rmi.runtime.NewThreadAction;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import sun.rmi.server.Util;
import sun.rmi.server.UnicastRef;
import java.util.HashSet;
import java.util.HashMap;
import sun.misc.GC;
import java.lang.ref.ReferenceQueue;
import java.util.Set;
import java.util.Map;
import java.rmi.dgc.DGC;
import java.security.PermissionCollection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.Permission;
import java.net.SocketPermission;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetLongAction;
import java.util.List;
import java.security.AccessControlContext;
import java.rmi.server.ObjID;
import java.rmi.dgc.VMID;

final class DGCClient
{
    private static long nextSequenceNum;
    private static VMID vmid;
    private static final long leaseValue;
    private static final long cleanInterval;
    private static final long gcInterval;
    private static final int dirtyFailureRetries = 5;
    private static final int cleanFailureRetries = 5;
    private static final ObjID[] emptyObjIDArray;
    private static final ObjID dgcID;
    private static final AccessControlContext SOCKET_ACC;
    
    private DGCClient() {
    }
    
    static void registerRefs(final Endpoint endpoint, final List<LiveRef> list) {
        while (!EndpointEntry.lookup(endpoint).registerRefs(list)) {}
    }
    
    private static synchronized long getNextSequenceNum() {
        return DGCClient.nextSequenceNum++;
    }
    
    private static long computeRenewTime(final long n, final long n2) {
        return n + n2 / 2L;
    }
    
    static {
        DGCClient.nextSequenceNum = Long.MIN_VALUE;
        DGCClient.vmid = new VMID();
        leaseValue = AccessController.doPrivileged((PrivilegedAction<Long>)new GetLongAction("java.rmi.dgc.leaseValue", 600000L));
        cleanInterval = AccessController.doPrivileged((PrivilegedAction<Long>)new GetLongAction("sun.rmi.dgc.cleanInterval", 180000L));
        gcInterval = AccessController.doPrivileged((PrivilegedAction<Long>)new GetLongAction("sun.rmi.dgc.client.gcInterval", 3600000L));
        emptyObjIDArray = new ObjID[0];
        dgcID = new ObjID(2);
        final Permissions permissions = new Permissions();
        permissions.add(new SocketPermission("*", "connect,resolve"));
        SOCKET_ACC = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, permissions) });
    }
    
    private static class EndpointEntry
    {
        private Endpoint endpoint;
        private DGC dgc;
        private Map<LiveRef, RefEntry> refTable;
        private Set<RefEntry> invalidRefs;
        private boolean removed;
        private long renewTime;
        private long expirationTime;
        private int dirtyFailures;
        private long dirtyFailureStartTime;
        private long dirtyFailureDuration;
        private Thread renewCleanThread;
        private boolean interruptible;
        private ReferenceQueue<LiveRef> refQueue;
        private Set<CleanRequest> pendingCleans;
        private static Map<Endpoint, EndpointEntry> endpointTable;
        private static GC.LatencyRequest gcLatencyRequest;
        
        public static EndpointEntry lookup(final Endpoint endpoint) {
            synchronized (EndpointEntry.endpointTable) {
                EndpointEntry endpointEntry = EndpointEntry.endpointTable.get(endpoint);
                if (endpointEntry == null) {
                    endpointEntry = new EndpointEntry(endpoint);
                    EndpointEntry.endpointTable.put(endpoint, endpointEntry);
                    if (EndpointEntry.gcLatencyRequest == null) {
                        EndpointEntry.gcLatencyRequest = GC.requestLatency(DGCClient.gcInterval);
                    }
                }
                return endpointEntry;
            }
        }
        
        private EndpointEntry(final Endpoint endpoint) {
            this.refTable = new HashMap<LiveRef, RefEntry>(5);
            this.invalidRefs = new HashSet<RefEntry>(5);
            this.removed = false;
            this.renewTime = Long.MAX_VALUE;
            this.expirationTime = Long.MIN_VALUE;
            this.dirtyFailures = 0;
            this.interruptible = false;
            this.refQueue = new ReferenceQueue<LiveRef>();
            this.pendingCleans = new HashSet<CleanRequest>(5);
            this.endpoint = endpoint;
            try {
                this.dgc = (DGC)Util.createProxy(DGCImpl.class, new UnicastRef(new LiveRef(DGCClient.dgcID, endpoint, false)), true);
            }
            catch (final RemoteException ex) {
                throw new Error("internal error creating DGC stub");
            }
            (this.renewCleanThread = AccessController.doPrivileged((PrivilegedAction<Thread>)new NewThreadAction(new RenewCleanThread(), "RenewClean-" + endpoint, true))).start();
        }
        
        public boolean registerRefs(final List<LiveRef> list) {
            assert !Thread.holdsLock(this);
            Set<RefEntry> set = null;
            final long access$300;
            synchronized (this) {
                if (this.removed) {
                    return false;
                }
                for (final LiveRef liveRef : list) {
                    assert liveRef.getEndpoint().equals(this.endpoint);
                    RefEntry refEntry = this.refTable.get(liveRef);
                    if (refEntry == null) {
                        final LiveRef liveRef2 = (LiveRef)liveRef.clone();
                        refEntry = new RefEntry(liveRef2);
                        this.refTable.put(liveRef2, refEntry);
                        if (set == null) {
                            set = new HashSet<RefEntry>(5);
                        }
                        set.add(refEntry);
                    }
                    refEntry.addInstanceToRefSet(liveRef);
                }
                if (set == null) {
                    return true;
                }
                set.addAll(this.invalidRefs);
                this.invalidRefs.clear();
                access$300 = getNextSequenceNum();
            }
            this.makeDirtyCall(set, access$300);
            return true;
        }
        
        private void removeRefEntry(final RefEntry refEntry) {
            assert Thread.holdsLock(this);
            assert !this.removed;
            assert this.refTable.containsKey(refEntry.getRef());
            this.refTable.remove(refEntry.getRef());
            this.invalidRefs.remove(refEntry);
            if (this.refTable.isEmpty()) {
                synchronized (EndpointEntry.endpointTable) {
                    EndpointEntry.endpointTable.remove(this.endpoint);
                    this.endpoint.getOutboundTransport().free(this.endpoint);
                    if (EndpointEntry.endpointTable.isEmpty()) {
                        assert EndpointEntry.gcLatencyRequest != null;
                        EndpointEntry.gcLatencyRequest.cancel();
                        EndpointEntry.gcLatencyRequest = null;
                    }
                    this.removed = true;
                }
            }
        }
        
        private void makeDirtyCall(final Set<RefEntry> set, final long n) {
            assert !Thread.holdsLock(this);
            ObjID[] array;
            if (set != null) {
                array = createObjIDArray(set);
            }
            else {
                array = DGCClient.emptyObjIDArray;
            }
            final long currentTimeMillis = System.currentTimeMillis();
            try {
                final long value = this.dgc.dirty(array, n, new Lease(DGCClient.vmid, DGCClient.leaseValue)).getValue();
                final long access$700 = computeRenewTime(currentTimeMillis, value);
                final long expirationTime = currentTimeMillis + value;
                synchronized (this) {
                    this.dirtyFailures = 0;
                    this.setRenewTime(access$700);
                    this.expirationTime = expirationTime;
                }
            }
            catch (final Exception ex) {
                final long currentTimeMillis2 = System.currentTimeMillis();
                synchronized (this) {
                    ++this.dirtyFailures;
                    if (ex instanceof UnmarshalException && ex.getCause() instanceof InvalidClassException) {
                        DGCImpl.dgcLog.log(Log.BRIEF, "InvalidClassException exception in DGC dirty call", ex);
                        return;
                    }
                    if (this.dirtyFailures == 1) {
                        this.dirtyFailureStartTime = currentTimeMillis;
                        this.dirtyFailureDuration = currentTimeMillis2 - currentTimeMillis;
                        this.setRenewTime(currentTimeMillis2);
                    }
                    else {
                        final int n2 = this.dirtyFailures - 2;
                        if (n2 == 0) {
                            this.dirtyFailureDuration = Math.max(this.dirtyFailureDuration + (currentTimeMillis2 - currentTimeMillis) >> 1, 1000L);
                        }
                        final long renewTime = currentTimeMillis2 + (this.dirtyFailureDuration << n2);
                        if (renewTime < this.expirationTime || this.dirtyFailures < 5 || renewTime < this.dirtyFailureStartTime + DGCClient.leaseValue) {
                            this.setRenewTime(renewTime);
                        }
                        else {
                            this.setRenewTime(Long.MAX_VALUE);
                        }
                    }
                    if (set != null) {
                        this.invalidRefs.addAll(set);
                        final Iterator<RefEntry> iterator = set.iterator();
                        while (iterator.hasNext()) {
                            iterator.next().markDirtyFailed();
                        }
                    }
                    if (this.renewTime >= this.expirationTime) {
                        this.invalidRefs.addAll(this.refTable.values());
                    }
                }
            }
        }
        
        private void setRenewTime(final long n) {
            assert Thread.holdsLock(this);
            if (n < this.renewTime) {
                this.renewTime = n;
                if (this.interruptible) {
                    AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                        @Override
                        public Void run() {
                            EndpointEntry.this.renewCleanThread.interrupt();
                            return null;
                        }
                    });
                }
            }
            else {
                this.renewTime = n;
            }
        }
        
        private void processPhantomRefs(RefEntry.PhantomLiveRef phantomLiveRef) {
            assert Thread.holdsLock(this);
            Set<RefEntry> set = null;
            Set<RefEntry> set2 = null;
            do {
                final RefEntry refEntry = phantomLiveRef.getRefEntry();
                refEntry.removeInstanceFromRefSet(phantomLiveRef);
                if (refEntry.isRefSetEmpty()) {
                    if (refEntry.hasDirtyFailed()) {
                        if (set == null) {
                            set = new HashSet<RefEntry>(5);
                        }
                        set.add(refEntry);
                    }
                    else {
                        if (set2 == null) {
                            set2 = new HashSet<RefEntry>(5);
                        }
                        set2.add(refEntry);
                    }
                    this.removeRefEntry(refEntry);
                }
            } while ((phantomLiveRef = (RefEntry.PhantomLiveRef)this.refQueue.poll()) != null);
            if (set != null) {
                this.pendingCleans.add(new CleanRequest(createObjIDArray(set), getNextSequenceNum(), true));
            }
            if (set2 != null) {
                this.pendingCleans.add(new CleanRequest(createObjIDArray(set2), getNextSequenceNum(), false));
            }
        }
        
        private void makeCleanCalls() {
            assert !Thread.holdsLock(this);
            final Iterator<CleanRequest> iterator = this.pendingCleans.iterator();
            while (iterator.hasNext()) {
                final CleanRequest cleanRequest = iterator.next();
                try {
                    this.dgc.clean(cleanRequest.objIDs, cleanRequest.sequenceNum, DGCClient.vmid, cleanRequest.strong);
                    iterator.remove();
                }
                catch (final Exception ex) {
                    if (++cleanRequest.failures < 5) {
                        continue;
                    }
                    iterator.remove();
                }
            }
        }
        
        private static ObjID[] createObjIDArray(final Set<RefEntry> set) {
            final ObjID[] array = new ObjID[set.size()];
            final Iterator iterator = set.iterator();
            for (int i = 0; i < array.length; ++i) {
                array[i] = ((RefEntry)iterator.next()).getRef().getObjID();
            }
            return array;
        }
        
        static {
            EndpointEntry.endpointTable = new HashMap<Endpoint, EndpointEntry>(5);
            EndpointEntry.gcLatencyRequest = null;
        }
        
        private class RenewCleanThread implements Runnable
        {
            @Override
            public void run() {
                do {
                    RefEntry.PhantomLiveRef phantomLiveRef = null;
                    boolean b = false;
                    Set access$1500 = null;
                    long access$1501 = Long.MIN_VALUE;
                    long n;
                    synchronized (EndpointEntry.this) {
                        n = Math.max(EndpointEntry.this.renewTime - System.currentTimeMillis(), 1L);
                        if (!EndpointEntry.this.pendingCleans.isEmpty()) {
                            n = Math.min(n, DGCClient.cleanInterval);
                        }
                        EndpointEntry.this.interruptible = true;
                    }
                    try {
                        phantomLiveRef = (RefEntry.PhantomLiveRef)EndpointEntry.this.refQueue.remove(n);
                    }
                    catch (final InterruptedException ex) {}
                    synchronized (EndpointEntry.this) {
                        EndpointEntry.this.interruptible = false;
                        Thread.interrupted();
                        if (phantomLiveRef != null) {
                            EndpointEntry.this.processPhantomRefs(phantomLiveRef);
                        }
                        if (System.currentTimeMillis() > EndpointEntry.this.renewTime) {
                            b = true;
                            if (!EndpointEntry.this.invalidRefs.isEmpty()) {
                                access$1500 = EndpointEntry.this.invalidRefs;
                                EndpointEntry.this.invalidRefs = (Set<RefEntry>)new HashSet(5);
                            }
                            access$1501 = getNextSequenceNum();
                        }
                    }
                    AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                        @Override
                        public Void run() {
                            if (b) {
                                EndpointEntry.this.makeDirtyCall(access$1500, access$1501);
                            }
                            if (!EndpointEntry.this.pendingCleans.isEmpty()) {
                                EndpointEntry.this.makeCleanCalls();
                            }
                            return null;
                        }
                    }, DGCClient.SOCKET_ACC);
                } while (!EndpointEntry.this.removed || !EndpointEntry.this.pendingCleans.isEmpty());
            }
        }
        
        private static class CleanRequest
        {
            final ObjID[] objIDs;
            final long sequenceNum;
            final boolean strong;
            int failures;
            
            CleanRequest(final ObjID[] objIDs, final long sequenceNum, final boolean strong) {
                this.failures = 0;
                this.objIDs = objIDs;
                this.sequenceNum = sequenceNum;
                this.strong = strong;
            }
        }
        
        private class RefEntry
        {
            private LiveRef ref;
            private Set<PhantomLiveRef> refSet;
            private boolean dirtyFailed;
            final /* synthetic */ EndpointEntry this$0;
            
            public RefEntry(final LiveRef ref) {
                this.refSet = new HashSet<PhantomLiveRef>(5);
                this.dirtyFailed = false;
                this.ref = ref;
            }
            
            public LiveRef getRef() {
                return this.ref;
            }
            
            public void addInstanceToRefSet(final LiveRef liveRef) {
                assert Thread.holdsLock(EndpointEntry.this);
                assert liveRef.equals(this.ref);
                this.refSet.add(new PhantomLiveRef(liveRef));
            }
            
            public void removeInstanceFromRefSet(final PhantomLiveRef phantomLiveRef) {
                assert Thread.holdsLock(EndpointEntry.this);
                assert this.refSet.contains(phantomLiveRef);
                this.refSet.remove(phantomLiveRef);
            }
            
            public boolean isRefSetEmpty() {
                assert Thread.holdsLock(EndpointEntry.this);
                return this.refSet.size() == 0;
            }
            
            public void markDirtyFailed() {
                assert Thread.holdsLock(EndpointEntry.this);
                this.dirtyFailed = true;
            }
            
            public boolean hasDirtyFailed() {
                assert Thread.holdsLock(EndpointEntry.this);
                return this.dirtyFailed;
            }
            
            private class PhantomLiveRef extends PhantomReference<LiveRef>
            {
                public PhantomLiveRef(final LiveRef liveRef) {
                    super(liveRef, RefEntry.this.this$0.refQueue);
                }
                
                public RefEntry getRefEntry() {
                    return RefEntry.this;
                }
            }
        }
    }
}
