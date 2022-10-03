package sun.rmi.transport;

import java.util.HashSet;
import java.util.Set;
import java.rmi.RemoteException;
import java.security.AccessControlContext;
import java.security.PermissionCollection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import sun.rmi.server.Dispatcher;
import java.security.Permission;
import java.net.SocketPermission;
import java.security.Permissions;
import java.rmi.Remote;
import java.rmi.server.RemoteRef;
import sun.rmi.server.Util;
import sun.rmi.server.UnicastRef;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.runtime.RuntimeUtil;
import sun.security.action.GetLongAction;
import java.rmi.server.LogStream;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.rmi.server.UID;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.RemoteServer;
import java.rmi.dgc.Lease;
import java.rmi.server.ObjID;
import java.util.HashMap;
import java.security.Security;
import sun.misc.ObjectInputFilter;
import java.util.concurrent.Future;
import java.rmi.dgc.VMID;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import sun.rmi.runtime.Log;
import java.rmi.dgc.DGC;

final class DGCImpl implements DGC
{
    static final Log dgcLog;
    private static final long leaseValue;
    private static final long leaseCheckInterval;
    private static final ScheduledExecutorService scheduler;
    private static DGCImpl dgc;
    private Map<VMID, LeaseInfo> leaseTable;
    private Future<?> checker;
    private static final String DGC_FILTER_PROPNAME = "sun.rmi.transport.dgcFilter";
    private static int DGC_MAX_DEPTH;
    private static int DGC_MAX_ARRAY_SIZE;
    private static final ObjectInputFilter dgcFilter;
    
    static DGCImpl getDGCImpl() {
        return DGCImpl.dgc;
    }
    
    private static ObjectInputFilter initDgcFilter() {
        Object filter = null;
        String s = System.getProperty("sun.rmi.transport.dgcFilter");
        if (s == null) {
            s = Security.getProperty("sun.rmi.transport.dgcFilter");
        }
        if (s != null) {
            filter = ObjectInputFilter.Config.createFilter(s);
            if (DGCImpl.dgcLog.isLoggable(Log.BRIEF)) {
                DGCImpl.dgcLog.log(Log.BRIEF, "dgcFilter = " + filter);
            }
        }
        return (ObjectInputFilter)filter;
    }
    
    private DGCImpl() {
        this.leaseTable = new HashMap<VMID, LeaseInfo>();
        this.checker = null;
    }
    
    @Override
    public Lease dirty(final ObjID[] array, final long n, Lease lease) {
        VMID vmid = lease.getVMID();
        final long leaseValue = DGCImpl.leaseValue;
        if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
            DGCImpl.dgcLog.log(Log.VERBOSE, "vmid = " + vmid);
        }
        if (vmid == null) {
            vmid = new VMID();
            if (DGCImpl.dgcLog.isLoggable(Log.BRIEF)) {
                String clientHost;
                try {
                    clientHost = RemoteServer.getClientHost();
                }
                catch (final ServerNotActiveException ex) {
                    clientHost = "<unknown host>";
                }
                DGCImpl.dgcLog.log(Log.BRIEF, " assigning vmid " + vmid + " to client " + clientHost);
            }
        }
        lease = new Lease(vmid, leaseValue);
        synchronized (this.leaseTable) {
            final LeaseInfo leaseInfo = this.leaseTable.get(vmid);
            if (leaseInfo == null) {
                this.leaseTable.put(vmid, new LeaseInfo(vmid, leaseValue));
                if (this.checker == null) {
                    this.checker = DGCImpl.scheduler.scheduleWithFixedDelay(new Runnable() {
                        @Override
                        public void run() {
                            DGCImpl.this.checkLeases();
                        }
                    }, DGCImpl.leaseCheckInterval, DGCImpl.leaseCheckInterval, TimeUnit.MILLISECONDS);
                }
            }
            else {
                leaseInfo.renew(leaseValue);
            }
        }
        for (final ObjID objID : array) {
            if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
                DGCImpl.dgcLog.log(Log.VERBOSE, "id = " + objID + ", vmid = " + vmid + ", duration = " + leaseValue);
            }
            ObjectTable.referenced(objID, n, vmid);
        }
        return lease;
    }
    
    @Override
    public void clean(final ObjID[] array, final long n, final VMID vmid, final boolean b) {
        for (final ObjID objID : array) {
            if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
                DGCImpl.dgcLog.log(Log.VERBOSE, "id = " + objID + ", vmid = " + vmid + ", strong = " + b);
            }
            ObjectTable.unreferenced(objID, n, vmid, b);
        }
    }
    
    void registerTarget(final VMID vmid, final Target target) {
        synchronized (this.leaseTable) {
            final LeaseInfo leaseInfo = this.leaseTable.get(vmid);
            if (leaseInfo == null) {
                target.vmidDead(vmid);
            }
            else {
                leaseInfo.notifySet.add(target);
            }
        }
    }
    
    void unregisterTarget(final VMID vmid, final Target target) {
        synchronized (this.leaseTable) {
            final LeaseInfo leaseInfo = this.leaseTable.get(vmid);
            if (leaseInfo != null) {
                leaseInfo.notifySet.remove(target);
            }
        }
    }
    
    private void checkLeases() {
        final long currentTimeMillis = System.currentTimeMillis();
        final ArrayList list = new ArrayList();
        synchronized (this.leaseTable) {
            final Iterator<LeaseInfo> iterator = this.leaseTable.values().iterator();
            while (iterator.hasNext()) {
                final LeaseInfo leaseInfo = iterator.next();
                if (leaseInfo.expired(currentTimeMillis)) {
                    list.add(leaseInfo);
                    iterator.remove();
                }
            }
            if (this.leaseTable.isEmpty()) {
                this.checker.cancel(false);
                this.checker = null;
            }
        }
        for (final LeaseInfo leaseInfo2 : list) {
            final Iterator<Target> iterator3 = leaseInfo2.notifySet.iterator();
            while (iterator3.hasNext()) {
                iterator3.next().vmidDead(leaseInfo2.vmid);
            }
        }
    }
    
    private static ObjectInputFilter.Status checkInput(final ObjectInputFilter.FilterInfo filterInfo) {
        if (DGCImpl.dgcFilter != null) {
            final ObjectInputFilter.Status checkInput = DGCImpl.dgcFilter.checkInput(filterInfo);
            if (checkInput != ObjectInputFilter.Status.UNDECIDED) {
                return checkInput;
            }
        }
        if (filterInfo.depth() > DGCImpl.DGC_MAX_DEPTH) {
            return ObjectInputFilter.Status.REJECTED;
        }
        Class<?> clazz = filterInfo.serialClass();
        if (clazz == null) {
            return ObjectInputFilter.Status.UNDECIDED;
        }
        while (clazz.isArray()) {
            if (filterInfo.arrayLength() >= 0L && filterInfo.arrayLength() > DGCImpl.DGC_MAX_ARRAY_SIZE) {
                return ObjectInputFilter.Status.REJECTED;
            }
            clazz = clazz.getComponentType();
        }
        if (clazz.isPrimitive()) {
            return ObjectInputFilter.Status.ALLOWED;
        }
        return (clazz == ObjID.class || clazz == UID.class || clazz == VMID.class || clazz == Lease.class) ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.REJECTED;
    }
    
    static {
        dgcLog = Log.getLog("sun.rmi.dgc", "dgc", LogStream.parseLevel(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.rmi.dgc.logLevel"))));
        leaseValue = AccessController.doPrivileged((PrivilegedAction<Long>)new GetLongAction("java.rmi.dgc.leaseValue", 600000L));
        leaseCheckInterval = AccessController.doPrivileged((PrivilegedAction<Long>)new GetLongAction("sun.rmi.dgc.checkInterval", DGCImpl.leaseValue / 2L));
        scheduler = AccessController.doPrivileged((PrivilegedAction<RuntimeUtil>)new RuntimeUtil.GetInstanceAction()).getScheduler();
        DGCImpl.DGC_MAX_DEPTH = 5;
        DGCImpl.DGC_MAX_ARRAY_SIZE = 10000;
        dgcFilter = AccessController.doPrivileged(DGCImpl::initDgcFilter);
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                try {
                    Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
                    try {
                        DGCImpl.dgc = new DGCImpl(null);
                        final ObjID objID = new ObjID(2);
                        final LiveRef liveRef = new LiveRef(objID, 0);
                        final UnicastServerRef unicastServerRef = new UnicastServerRef(liveRef, filterInfo -> checkInput(filterInfo));
                        final Remote proxy = Util.createProxy(DGCImpl.class, new UnicastRef(liveRef), true);
                        unicastServerRef.setSkeleton(DGCImpl.dgc);
                        final Permissions permissions = new Permissions();
                        permissions.add(new SocketPermission("*", "accept,resolve"));
                        ObjectTable.putTarget(AccessController.doPrivileged((PrivilegedAction<Target>)new PrivilegedAction<Target>() {
                            @Override
                            public Target run() {
                                return new Target(DGCImpl.dgc, unicastServerRef, proxy, objID, true);
                            }
                        }, new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, permissions) })));
                    }
                    catch (final RemoteException ex) {
                        throw new Error("exception initializing server-side DGC", ex);
                    }
                }
                finally {
                    Thread.currentThread().setContextClassLoader(contextClassLoader);
                }
                return null;
            }
        });
    }
    
    private static class LeaseInfo
    {
        VMID vmid;
        long expiration;
        Set<Target> notifySet;
        
        LeaseInfo(final VMID vmid, final long n) {
            this.notifySet = new HashSet<Target>();
            this.vmid = vmid;
            this.expiration = System.currentTimeMillis() + n;
        }
        
        synchronized void renew(final long n) {
            final long expiration = System.currentTimeMillis() + n;
            if (expiration > this.expiration) {
                this.expiration = expiration;
            }
        }
        
        boolean expired(final long n) {
            if (this.expiration < n) {
                if (DGCImpl.dgcLog.isLoggable(Log.BRIEF)) {
                    DGCImpl.dgcLog.log(Log.BRIEF, this.vmid.toString());
                }
                return true;
            }
            return false;
        }
    }
}
