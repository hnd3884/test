package sun.nio.fs;

import sun.misc.Cleaner;
import sun.misc.Unsafe;

class NativeBuffer
{
    private static final Unsafe unsafe;
    private final long address;
    private final int size;
    private final Cleaner cleaner;
    private Object owner;
    
    NativeBuffer(final int size) {
        this.address = NativeBuffer.unsafe.allocateMemory(size);
        this.size = size;
        this.cleaner = Cleaner.create(this, new Deallocator(this.address));
    }
    
    void release() {
        NativeBuffers.releaseNativeBuffer(this);
    }
    
    long address() {
        return this.address;
    }
    
    int size() {
        return this.size;
    }
    
    Cleaner cleaner() {
        return this.cleaner;
    }
    
    void setOwner(final Object owner) {
        this.owner = owner;
    }
    
    Object owner() {
        return this.owner;
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
    }
    
    private static class Deallocator implements Runnable
    {
        private final long address;
        
        Deallocator(final long address) {
            this.address = address;
        }
        
        @Override
        public void run() {
            NativeBuffer.unsafe.freeMemory(this.address);
        }
    }
}
