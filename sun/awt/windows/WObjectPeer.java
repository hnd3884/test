package sun.awt.windows;

import java.util.Iterator;
import java.util.WeakHashMap;
import java.util.Map;

abstract class WObjectPeer
{
    volatile long pData;
    private volatile boolean destroyed;
    volatile Object target;
    private volatile boolean disposed;
    volatile Error createError;
    private final Object stateLock;
    private volatile Map<WObjectPeer, WObjectPeer> childPeers;
    
    WObjectPeer() {
        this.createError = null;
        this.stateLock = new Object();
    }
    
    public static WObjectPeer getPeerForTarget(final Object o) {
        return (WObjectPeer)WToolkit.targetToPeer(o);
    }
    
    public long getData() {
        return this.pData;
    }
    
    public Object getTarget() {
        return this.target;
    }
    
    public final Object getStateLock() {
        return this.stateLock;
    }
    
    protected abstract void disposeImpl();
    
    public final void dispose() {
        int n = 0;
        synchronized (this) {
            if (!this.disposed) {
                n = ((this.disposed = true) ? 1 : 0);
            }
        }
        if (n != 0) {
            if (this.childPeers != null) {
                this.disposeChildPeers();
            }
            this.disposeImpl();
        }
    }
    
    protected final boolean isDisposed() {
        return this.disposed;
    }
    
    private static native void initIDs();
    
    final void addChildPeer(final WObjectPeer wObjectPeer) {
        synchronized (this.getStateLock()) {
            if (this.childPeers == null) {
                this.childPeers = new WeakHashMap<WObjectPeer, WObjectPeer>();
            }
            if (this.isDisposed()) {
                throw new IllegalStateException("Parent peer is disposed");
            }
            this.childPeers.put(wObjectPeer, this);
        }
    }
    
    private void disposeChildPeers() {
        synchronized (this.getStateLock()) {
            for (final WObjectPeer wObjectPeer : this.childPeers.keySet()) {
                if (wObjectPeer != null) {
                    try {
                        wObjectPeer.dispose();
                    }
                    catch (final Exception ex) {}
                }
            }
        }
    }
    
    static {
        initIDs();
    }
}
