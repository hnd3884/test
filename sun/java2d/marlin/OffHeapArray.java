package sun.java2d.marlin;

import java.lang.ref.PhantomReference;
import java.security.AccessController;
import sun.misc.ThreadGroupUtils;
import java.util.Vector;
import java.lang.ref.ReferenceQueue;
import sun.misc.Unsafe;

final class OffHeapArray
{
    static final Unsafe UNSAFE;
    static final int SIZE_INT;
    private static final ReferenceQueue<Object> rdrQueue;
    private static final Vector<OffHeapReference> refList;
    long address;
    long length;
    int used;
    
    OffHeapArray(final Object o, final long length) {
        this.address = OffHeapArray.UNSAFE.allocateMemory(length);
        this.length = length;
        this.used = 0;
        if (MarlinConst.LOG_UNSAFE_MALLOC) {
            MarlinUtils.logInfo(System.currentTimeMillis() + ": OffHeapArray.allocateMemory = " + length + " to addr = " + this.address);
        }
        OffHeapArray.refList.add(new OffHeapReference(o, this));
    }
    
    void resize(final long length) {
        this.address = OffHeapArray.UNSAFE.reallocateMemory(this.address, length);
        this.length = length;
        if (MarlinConst.LOG_UNSAFE_MALLOC) {
            MarlinUtils.logInfo(System.currentTimeMillis() + ": OffHeapArray.reallocateMemory = " + length + " to addr = " + this.address);
        }
    }
    
    void free() {
        OffHeapArray.UNSAFE.freeMemory(this.address);
        if (MarlinConst.LOG_UNSAFE_MALLOC) {
            MarlinUtils.logInfo(System.currentTimeMillis() + ": OffHeapEdgeArray.free = " + this.length + " at addr = " + this.address);
        }
        this.address = 0L;
    }
    
    void fill(final byte b) {
        OffHeapArray.UNSAFE.setMemory(this.address, this.length, b);
    }
    
    static {
        rdrQueue = new ReferenceQueue<Object>();
        refList = new Vector<OffHeapReference>(32);
        UNSAFE = Unsafe.getUnsafe();
        SIZE_INT = Unsafe.ARRAY_INT_INDEX_SCALE;
        AccessController.doPrivileged(() -> {
            ThreadGroupUtils.getRootThreadGroup();
            final ThreadGroup threadGroup;
            new Thread(threadGroup, new OffHeapDisposer(), "MarlinRenderer Disposer");
            final Thread thread2;
            thread2.setContextClassLoader(null);
            thread2.setDaemon(true);
            thread2.setPriority(10);
            thread2.start();
            return null;
        });
    }
    
    static final class OffHeapReference extends PhantomReference<Object>
    {
        private final OffHeapArray array;
        
        OffHeapReference(final Object o, final OffHeapArray array) {
            super(o, OffHeapArray.rdrQueue);
            this.array = array;
        }
        
        void dispose() {
            this.array.free();
        }
    }
    
    static final class OffHeapDisposer implements Runnable
    {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    final OffHeapReference offHeapReference = (OffHeapReference)OffHeapArray.rdrQueue.remove();
                    offHeapReference.dispose();
                    OffHeapArray.refList.remove(offHeapReference);
                }
                catch (final InterruptedException ex) {
                    MarlinUtils.logException("OffHeapDisposer interrupted:", ex);
                }
            }
        }
    }
}
