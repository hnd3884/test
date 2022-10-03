package sun.tracing.dtrace;

import java.util.HashSet;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

class SystemResource extends WeakReference<Activation>
{
    private long handle;
    private static ReferenceQueue<Activation> referenceQueue;
    static HashSet<SystemResource> resources;
    
    SystemResource(final Activation activation, final long handle) {
        super(activation, SystemResource.referenceQueue);
        this.handle = handle;
        flush();
        SystemResource.resources.add(this);
    }
    
    void dispose() {
        JVM.dispose(this.handle);
        SystemResource.resources.remove(this);
        this.handle = 0L;
    }
    
    static void flush() {
        SystemResource systemResource;
        while ((systemResource = (SystemResource)SystemResource.referenceQueue.poll()) != null) {
            if (systemResource.handle != 0L) {
                systemResource.dispose();
            }
        }
    }
    
    static {
        SystemResource.referenceQueue = (SystemResource.referenceQueue = new ReferenceQueue<Activation>());
        SystemResource.resources = new HashSet<SystemResource>();
    }
}
