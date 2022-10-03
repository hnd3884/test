package sun.java2d.opengl;

import sun.java2d.pipe.RenderBuffer;
import java.security.AccessController;
import sun.misc.ThreadGroupUtils;
import sun.java2d.pipe.RenderQueue;

public class OGLRenderQueue extends RenderQueue
{
    private static OGLRenderQueue theInstance;
    private final QueueFlusher flusher;
    
    private OGLRenderQueue() {
        this.flusher = AccessController.doPrivileged(() -> new QueueFlusher(ThreadGroupUtils.getRootThreadGroup()));
    }
    
    public static synchronized OGLRenderQueue getInstance() {
        if (OGLRenderQueue.theInstance == null) {
            OGLRenderQueue.theInstance = new OGLRenderQueue();
        }
        return OGLRenderQueue.theInstance;
    }
    
    public static void sync() {
        if (OGLRenderQueue.theInstance != null) {
            OGLRenderQueue.theInstance.lock();
            try {
                OGLRenderQueue.theInstance.ensureCapacity(4);
                OGLRenderQueue.theInstance.getBuffer().putInt(76);
                OGLRenderQueue.theInstance.flushNow();
            }
            finally {
                OGLRenderQueue.theInstance.unlock();
            }
        }
    }
    
    public static void disposeGraphicsConfig(final long scratchSurface) {
        final OGLRenderQueue instance = getInstance();
        instance.lock();
        try {
            OGLContext.setScratchSurface(scratchSurface);
            final RenderBuffer buffer = instance.getBuffer();
            instance.ensureCapacityAndAlignment(12, 4);
            buffer.putInt(74);
            buffer.putLong(scratchSurface);
            instance.flushNow();
        }
        finally {
            instance.unlock();
        }
    }
    
    public static boolean isQueueFlusherThread() {
        return Thread.currentThread() == getInstance().flusher;
    }
    
    @Override
    public void flushNow() {
        try {
            this.flusher.flushNow();
        }
        catch (final Exception ex) {
            System.err.println("exception in flushNow:");
            ex.printStackTrace();
        }
    }
    
    @Override
    public void flushAndInvokeNow(final Runnable runnable) {
        try {
            this.flusher.flushAndInvokeNow(runnable);
        }
        catch (final Exception ex) {
            System.err.println("exception in flushAndInvokeNow:");
            ex.printStackTrace();
        }
    }
    
    private native void flushBuffer(final long p0, final int p1);
    
    private void flushBuffer() {
        final int position = this.buf.position();
        if (position > 0) {
            this.flushBuffer(this.buf.getAddress(), position);
        }
        this.buf.clear();
        this.refSet.clear();
    }
    
    private class QueueFlusher extends Thread
    {
        private boolean needsFlush;
        private Runnable task;
        private Error error;
        
        public QueueFlusher(final ThreadGroup threadGroup) {
            super(threadGroup, "Java2D Queue Flusher");
            this.setDaemon(true);
            this.setPriority(10);
            this.start();
        }
        
        public synchronized void flushNow() {
            this.needsFlush = true;
            this.notify();
            while (this.needsFlush) {
                try {
                    this.wait();
                }
                catch (final InterruptedException ex) {}
            }
            if (this.error != null) {
                throw this.error;
            }
        }
        
        public synchronized void flushAndInvokeNow(final Runnable task) {
            this.task = task;
            this.flushNow();
        }
        
        @Override
        public synchronized void run() {
            boolean tryLock = false;
            while (true) {
                if (!this.needsFlush) {
                    try {
                        tryLock = false;
                        this.wait(100L);
                        if (this.needsFlush || !(tryLock = OGLRenderQueue.this.tryLock())) {
                            continue;
                        }
                        if (OGLRenderQueue.this.buf.position() > 0) {
                            this.needsFlush = true;
                        }
                        else {
                            OGLRenderQueue.this.unlock();
                        }
                    }
                    catch (final InterruptedException ex) {}
                }
                else {
                    try {
                        this.error = null;
                        OGLRenderQueue.this.flushBuffer();
                        if (this.task != null) {
                            this.task.run();
                        }
                    }
                    catch (final Error error) {
                        this.error = error;
                    }
                    catch (final Exception ex2) {
                        System.err.println("exception in QueueFlusher:");
                        ex2.printStackTrace();
                    }
                    finally {
                        if (tryLock) {
                            OGLRenderQueue.this.unlock();
                        }
                        this.task = null;
                        this.needsFlush = false;
                        this.notify();
                    }
                }
            }
        }
    }
}
