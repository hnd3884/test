package sun.misc;

import java.util.TreeSet;
import java.util.SortedSet;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class GC
{
    private static final long NO_TARGET = Long.MAX_VALUE;
    private static long latencyTarget;
    private static Thread daemon;
    private static Object lock;
    
    private GC() {
    }
    
    public static native long maxObjectInspectionAge();
    
    private static void setLatencyTarget(final long latencyTarget) {
        GC.latencyTarget = latencyTarget;
        if (GC.daemon == null) {
            Daemon.create();
        }
        else {
            GC.lock.notify();
        }
    }
    
    public static LatencyRequest requestLatency(final long n) {
        return new LatencyRequest(n);
    }
    
    public static long currentLatencyTarget() {
        final long latencyTarget = GC.latencyTarget;
        return (latencyTarget == Long.MAX_VALUE) ? 0L : latencyTarget;
    }
    
    static {
        GC.latencyTarget = Long.MAX_VALUE;
        GC.daemon = null;
        GC.lock = new LatencyLock();
    }
    
    private static class LatencyLock
    {
    }
    
    private static class Daemon extends Thread
    {
        @Override
        public void run() {
            while (true) {
                synchronized (GC.lock) {
                    final long access$200 = GC.latencyTarget;
                    if (access$200 == Long.MAX_VALUE) {
                        GC.daemon = null;
                        return;
                    }
                    long maxObjectInspectionAge = GC.maxObjectInspectionAge();
                    if (maxObjectInspectionAge >= access$200) {
                        System.gc();
                        maxObjectInspectionAge = 0L;
                    }
                    try {
                        GC.lock.wait(access$200 - maxObjectInspectionAge);
                    }
                    catch (final InterruptedException ex) {}
                }
            }
        }
        
        private Daemon(final ThreadGroup threadGroup) {
            super(threadGroup, "GC Daemon");
        }
        
        public static void create() {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    ThreadGroup threadGroup2;
                    ThreadGroup threadGroup;
                    for (threadGroup = (threadGroup2 = Thread.currentThread().getThreadGroup()); threadGroup2 != null; threadGroup2 = threadGroup.getParent()) {
                        threadGroup = threadGroup2;
                    }
                    final Daemon daemon = new Daemon(threadGroup);
                    daemon.setDaemon(true);
                    daemon.setPriority(2);
                    daemon.start();
                    GC.daemon = daemon;
                    return null;
                }
            });
        }
    }
    
    public static class LatencyRequest implements Comparable<LatencyRequest>
    {
        private static long counter;
        private static SortedSet<LatencyRequest> requests;
        private long latency;
        private long id;
        
        private static void adjustLatencyIfNeeded() {
            if (LatencyRequest.requests == null || LatencyRequest.requests.isEmpty()) {
                if (GC.latencyTarget != Long.MAX_VALUE) {
                    setLatencyTarget(Long.MAX_VALUE);
                }
            }
            else {
                final LatencyRequest latencyRequest = LatencyRequest.requests.first();
                if (latencyRequest.latency != GC.latencyTarget) {
                    setLatencyTarget(latencyRequest.latency);
                }
            }
        }
        
        private LatencyRequest(final long latency) {
            if (latency <= 0L) {
                throw new IllegalArgumentException("Non-positive latency: " + latency);
            }
            this.latency = latency;
            synchronized (GC.lock) {
                this.id = ++LatencyRequest.counter;
                if (LatencyRequest.requests == null) {
                    LatencyRequest.requests = new TreeSet<LatencyRequest>();
                }
                LatencyRequest.requests.add(this);
                adjustLatencyIfNeeded();
            }
        }
        
        public void cancel() {
            synchronized (GC.lock) {
                if (this.latency == Long.MAX_VALUE) {
                    throw new IllegalStateException("Request already cancelled");
                }
                if (!LatencyRequest.requests.remove(this)) {
                    throw new InternalError("Latency request " + this + " not found");
                }
                if (LatencyRequest.requests.isEmpty()) {
                    LatencyRequest.requests = null;
                }
                this.latency = Long.MAX_VALUE;
                adjustLatencyIfNeeded();
            }
        }
        
        @Override
        public int compareTo(final LatencyRequest latencyRequest) {
            long n = this.latency - latencyRequest.latency;
            if (n == 0L) {
                n = this.id - latencyRequest.id;
            }
            return (n < 0L) ? -1 : (n > 0L);
        }
        
        @Override
        public String toString() {
            return LatencyRequest.class.getName() + "[" + this.latency + "," + this.id + "]";
        }
        
        static {
            LatencyRequest.counter = 0L;
            LatencyRequest.requests = null;
        }
    }
}
