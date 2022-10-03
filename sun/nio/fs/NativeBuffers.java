package sun.nio.fs;

import sun.misc.Unsafe;

class NativeBuffers
{
    private static final Unsafe unsafe;
    private static final int TEMP_BUF_POOL_SIZE = 3;
    private static ThreadLocal<NativeBuffer[]> threadLocal;
    
    private NativeBuffers() {
    }
    
    static NativeBuffer allocNativeBuffer(int n) {
        if (n < 2048) {
            n = 2048;
        }
        return new NativeBuffer(n);
    }
    
    static NativeBuffer getNativeBufferFromCache(final int n) {
        final NativeBuffer[] array = NativeBuffers.threadLocal.get();
        if (array != null) {
            for (int i = 0; i < 3; ++i) {
                final NativeBuffer nativeBuffer = array[i];
                if (nativeBuffer != null && nativeBuffer.size() >= n) {
                    array[i] = null;
                    return nativeBuffer;
                }
            }
        }
        return null;
    }
    
    static NativeBuffer getNativeBuffer(final int n) {
        final NativeBuffer nativeBufferFromCache = getNativeBufferFromCache(n);
        if (nativeBufferFromCache != null) {
            nativeBufferFromCache.setOwner(null);
            return nativeBufferFromCache;
        }
        return allocNativeBuffer(n);
    }
    
    static void releaseNativeBuffer(final NativeBuffer nativeBuffer) {
        final NativeBuffer[] array = NativeBuffers.threadLocal.get();
        if (array == null) {
            NativeBuffers.threadLocal.set(new NativeBuffer[] { nativeBuffer, null, null });
            return;
        }
        for (int i = 0; i < 3; ++i) {
            if (array[i] == null) {
                array[i] = nativeBuffer;
                return;
            }
        }
        for (int j = 0; j < 3; ++j) {
            final NativeBuffer nativeBuffer2 = array[j];
            if (nativeBuffer2.size() < nativeBuffer.size()) {
                nativeBuffer2.cleaner().clean();
                array[j] = nativeBuffer;
                return;
            }
        }
        nativeBuffer.cleaner().clean();
    }
    
    static void copyCStringToNativeBuffer(final byte[] array, final NativeBuffer nativeBuffer) {
        final long n = Unsafe.ARRAY_BYTE_BASE_OFFSET;
        final long n2 = array.length;
        assert nativeBuffer.size() >= n2 + 1L;
        NativeBuffers.unsafe.copyMemory(array, n, null, nativeBuffer.address(), n2);
        NativeBuffers.unsafe.putByte(nativeBuffer.address() + n2, (byte)0);
    }
    
    static NativeBuffer asNativeBuffer(final byte[] array) {
        final NativeBuffer nativeBuffer = getNativeBuffer(array.length + 1);
        copyCStringToNativeBuffer(array, nativeBuffer);
        return nativeBuffer;
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
        NativeBuffers.threadLocal = new ThreadLocal<NativeBuffer[]>();
    }
}
