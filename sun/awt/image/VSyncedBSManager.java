package sun.awt.image;

import java.lang.ref.WeakReference;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.awt.image.BufferStrategy;

public abstract class VSyncedBSManager
{
    private static VSyncedBSManager theInstance;
    private static final boolean vSyncLimit;
    
    private static VSyncedBSManager getInstance(final boolean b) {
        if (VSyncedBSManager.theInstance == null && b) {
            VSyncedBSManager.theInstance = (VSyncedBSManager.vSyncLimit ? new SingleVSyncedBSMgr() : new NoLimitVSyncBSMgr());
        }
        return VSyncedBSManager.theInstance;
    }
    
    abstract boolean checkAllowed(final BufferStrategy p0);
    
    abstract void relinquishVsync(final BufferStrategy p0);
    
    public static boolean vsyncAllowed(final BufferStrategy bufferStrategy) {
        return getInstance(true).checkAllowed(bufferStrategy);
    }
    
    public static synchronized void releaseVsync(final BufferStrategy bufferStrategy) {
        final VSyncedBSManager instance = getInstance(false);
        if (instance != null) {
            instance.relinquishVsync(bufferStrategy);
        }
    }
    
    static {
        vSyncLimit = Boolean.valueOf(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.java2d.vsynclimit", "true")));
    }
    
    private static final class NoLimitVSyncBSMgr extends VSyncedBSManager
    {
        @Override
        boolean checkAllowed(final BufferStrategy bufferStrategy) {
            return true;
        }
        
        @Override
        void relinquishVsync(final BufferStrategy bufferStrategy) {
        }
    }
    
    private static final class SingleVSyncedBSMgr extends VSyncedBSManager
    {
        private WeakReference<BufferStrategy> strategy;
        
        public synchronized boolean checkAllowed(final BufferStrategy bufferStrategy) {
            if (this.strategy != null) {
                final BufferStrategy bufferStrategy2 = this.strategy.get();
                if (bufferStrategy2 != null) {
                    return bufferStrategy2 == bufferStrategy;
                }
            }
            this.strategy = new WeakReference<BufferStrategy>(bufferStrategy);
            return true;
        }
        
        public synchronized void relinquishVsync(final BufferStrategy bufferStrategy) {
            if (this.strategy != null && this.strategy.get() == bufferStrategy) {
                this.strategy.clear();
                this.strategy = null;
            }
        }
    }
}
