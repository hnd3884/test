package sun.nio.ch;

import sun.misc.Cleaner;
import java.nio.ByteBuffer;

class IOVecWrapper
{
    private static final int BASE_OFFSET = 0;
    private static final int LEN_OFFSET;
    private static final int SIZE_IOVEC;
    private final AllocatedNativeObject vecArray;
    private final int size;
    private final ByteBuffer[] buf;
    private final int[] position;
    private final int[] remaining;
    private final ByteBuffer[] shadow;
    final long address;
    static int addressSize;
    private static final ThreadLocal<IOVecWrapper> cached;
    
    private IOVecWrapper(final int size) {
        this.size = size;
        this.buf = new ByteBuffer[size];
        this.position = new int[size];
        this.remaining = new int[size];
        this.shadow = new ByteBuffer[size];
        this.vecArray = new AllocatedNativeObject(size * IOVecWrapper.SIZE_IOVEC, false);
        this.address = this.vecArray.address();
    }
    
    static IOVecWrapper get(final int n) {
        IOVecWrapper ioVecWrapper = IOVecWrapper.cached.get();
        if (ioVecWrapper != null && ioVecWrapper.size < n) {
            ioVecWrapper.vecArray.free();
            ioVecWrapper = null;
        }
        if (ioVecWrapper == null) {
            ioVecWrapper = new IOVecWrapper(n);
            Cleaner.create(ioVecWrapper, new Deallocator(ioVecWrapper.vecArray));
            IOVecWrapper.cached.set(ioVecWrapper);
        }
        return ioVecWrapper;
    }
    
    void setBuffer(final int n, final ByteBuffer byteBuffer, final int n2, final int n3) {
        this.buf[n] = byteBuffer;
        this.position[n] = n2;
        this.remaining[n] = n3;
    }
    
    void setShadow(final int n, final ByteBuffer byteBuffer) {
        this.shadow[n] = byteBuffer;
    }
    
    ByteBuffer getBuffer(final int n) {
        return this.buf[n];
    }
    
    int getPosition(final int n) {
        return this.position[n];
    }
    
    int getRemaining(final int n) {
        return this.remaining[n];
    }
    
    ByteBuffer getShadow(final int n) {
        return this.shadow[n];
    }
    
    void clearRefs(final int n) {
        this.buf[n] = null;
        this.shadow[n] = null;
    }
    
    void putBase(final int n, final long n2) {
        final int n3 = IOVecWrapper.SIZE_IOVEC * n + 0;
        if (IOVecWrapper.addressSize == 4) {
            this.vecArray.putInt(n3, (int)n2);
        }
        else {
            this.vecArray.putLong(n3, n2);
        }
    }
    
    void putLen(final int n, final long n2) {
        final int n3 = IOVecWrapper.SIZE_IOVEC * n + IOVecWrapper.LEN_OFFSET;
        if (IOVecWrapper.addressSize == 4) {
            this.vecArray.putInt(n3, (int)n2);
        }
        else {
            this.vecArray.putLong(n3, n2);
        }
    }
    
    static {
        cached = new ThreadLocal<IOVecWrapper>();
        IOVecWrapper.addressSize = Util.unsafe().addressSize();
        LEN_OFFSET = IOVecWrapper.addressSize;
        SIZE_IOVEC = (short)(IOVecWrapper.addressSize * 2);
    }
    
    private static class Deallocator implements Runnable
    {
        private final AllocatedNativeObject obj;
        
        Deallocator(final AllocatedNativeObject obj) {
            this.obj = obj;
        }
        
        @Override
        public void run() {
            this.obj.free();
        }
    }
}
