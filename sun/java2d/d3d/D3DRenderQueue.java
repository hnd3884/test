package sun.java2d.d3d;

import sun.java2d.pipe.RenderBuffer;
import sun.java2d.ScreenUpdateManager;
import sun.java2d.pipe.RenderQueue;

public class D3DRenderQueue extends RenderQueue
{
    private static D3DRenderQueue theInstance;
    private static Thread rqThread;
    
    private D3DRenderQueue() {
    }
    
    public static synchronized D3DRenderQueue getInstance() {
        if (D3DRenderQueue.theInstance == null) {
            (D3DRenderQueue.theInstance = new D3DRenderQueue()).flushAndInvokeNow(new Runnable() {
                @Override
                public void run() {
                    D3DRenderQueue.rqThread = Thread.currentThread();
                }
            });
        }
        return D3DRenderQueue.theInstance;
    }
    
    public static void sync() {
        if (D3DRenderQueue.theInstance != null) {
            ((D3DScreenUpdateManager)ScreenUpdateManager.getInstance()).runUpdateNow();
            D3DRenderQueue.theInstance.lock();
            try {
                D3DRenderQueue.theInstance.ensureCapacity(4);
                D3DRenderQueue.theInstance.getBuffer().putInt(76);
                D3DRenderQueue.theInstance.flushNow();
            }
            finally {
                D3DRenderQueue.theInstance.unlock();
            }
        }
    }
    
    public static void restoreDevices() {
        final D3DRenderQueue instance = getInstance();
        instance.lock();
        try {
            instance.ensureCapacity(4);
            instance.getBuffer().putInt(77);
            instance.flushNow();
        }
        finally {
            instance.unlock();
        }
    }
    
    public static boolean isRenderQueueThread() {
        return Thread.currentThread() == D3DRenderQueue.rqThread;
    }
    
    public static void disposeGraphicsConfig(final long n) {
        final D3DRenderQueue instance = getInstance();
        instance.lock();
        try {
            final RenderBuffer buffer = instance.getBuffer();
            instance.ensureCapacityAndAlignment(12, 4);
            buffer.putInt(74);
            buffer.putLong(n);
            instance.flushNow();
        }
        finally {
            instance.unlock();
        }
    }
    
    @Override
    public void flushNow() {
        this.flushBuffer(null);
    }
    
    @Override
    public void flushAndInvokeNow(final Runnable runnable) {
        this.flushBuffer(runnable);
    }
    
    private native void flushBuffer(final long p0, final int p1, final Runnable p2);
    
    private void flushBuffer(final Runnable runnable) {
        final int position = this.buf.position();
        if (position > 0 || runnable != null) {
            this.flushBuffer(this.buf.getAddress(), position, runnable);
        }
        this.buf.clear();
        this.refSet.clear();
    }
}
