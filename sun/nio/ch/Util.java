package sun.nio.ch;

import sun.security.action.GetPropertyAction;
import sun.misc.VM;
import java.lang.reflect.InvocationTargetException;
import java.nio.MappedByteBuffer;
import java.io.FileDescriptor;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Constructor;
import sun.misc.Unsafe;

public class Util
{
    private static final int TEMP_BUF_POOL_SIZE;
    private static final long MAX_CACHED_BUFFER_SIZE;
    private static ThreadLocal<BufferCache> bufferCache;
    private static Unsafe unsafe;
    private static int pageSize;
    private static volatile Constructor<?> directByteBufferConstructor;
    private static volatile Constructor<?> directByteBufferRConstructor;
    private static volatile String bugLevel;
    
    private static long getMaxCachedBufferSize() {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("jdk.nio.maxCachedBufferSize");
            }
        });
        if (s != null) {
            try {
                final long long1 = Long.parseLong(s);
                if (long1 >= 0L) {
                    return long1;
                }
            }
            catch (final NumberFormatException ex) {}
        }
        return Long.MAX_VALUE;
    }
    
    private static boolean isBufferTooLarge(final int n) {
        return n > Util.MAX_CACHED_BUFFER_SIZE;
    }
    
    private static boolean isBufferTooLarge(final ByteBuffer byteBuffer) {
        return isBufferTooLarge(byteBuffer.capacity());
    }
    
    public static ByteBuffer getTemporaryDirectBuffer(final int n) {
        if (isBufferTooLarge(n)) {
            return ByteBuffer.allocateDirect(n);
        }
        final BufferCache bufferCache = Util.bufferCache.get();
        final ByteBuffer value = bufferCache.get(n);
        if (value != null) {
            return value;
        }
        if (!bufferCache.isEmpty()) {
            free(bufferCache.removeFirst());
        }
        return ByteBuffer.allocateDirect(n);
    }
    
    public static void releaseTemporaryDirectBuffer(final ByteBuffer byteBuffer) {
        offerFirstTemporaryDirectBuffer(byteBuffer);
    }
    
    static void offerFirstTemporaryDirectBuffer(final ByteBuffer byteBuffer) {
        if (isBufferTooLarge(byteBuffer)) {
            free(byteBuffer);
            return;
        }
        assert byteBuffer != null;
        if (!Util.bufferCache.get().offerFirst(byteBuffer)) {
            free(byteBuffer);
        }
    }
    
    static void offerLastTemporaryDirectBuffer(final ByteBuffer byteBuffer) {
        if (isBufferTooLarge(byteBuffer)) {
            free(byteBuffer);
            return;
        }
        assert byteBuffer != null;
        if (!Util.bufferCache.get().offerLast(byteBuffer)) {
            free(byteBuffer);
        }
    }
    
    private static void free(final ByteBuffer byteBuffer) {
        ((DirectBuffer)byteBuffer).cleaner().clean();
    }
    
    static ByteBuffer[] subsequence(final ByteBuffer[] array, final int n, final int n2) {
        if (n == 0 && n2 == array.length) {
            return array;
        }
        final ByteBuffer[] array2 = new ByteBuffer[n2];
        for (int i = 0; i < n2; ++i) {
            array2[i] = array[n + i];
        }
        return array2;
    }
    
    static <E> Set<E> ungrowableSet(final Set<E> set) {
        return new Set<E>() {
            @Override
            public int size() {
                return set.size();
            }
            
            @Override
            public boolean isEmpty() {
                return set.isEmpty();
            }
            
            @Override
            public boolean contains(final Object o) {
                return set.contains(o);
            }
            
            @Override
            public Object[] toArray() {
                return set.toArray();
            }
            
            @Override
            public <T> T[] toArray(final T[] array) {
                return set.toArray(array);
            }
            
            @Override
            public String toString() {
                return set.toString();
            }
            
            @Override
            public Iterator<E> iterator() {
                return set.iterator();
            }
            
            @Override
            public boolean equals(final Object o) {
                return set.equals(o);
            }
            
            @Override
            public int hashCode() {
                return set.hashCode();
            }
            
            @Override
            public void clear() {
                set.clear();
            }
            
            @Override
            public boolean remove(final Object o) {
                return set.remove(o);
            }
            
            @Override
            public boolean containsAll(final Collection<?> collection) {
                return set.containsAll(collection);
            }
            
            @Override
            public boolean removeAll(final Collection<?> collection) {
                return set.removeAll(collection);
            }
            
            @Override
            public boolean retainAll(final Collection<?> collection) {
                return set.retainAll(collection);
            }
            
            @Override
            public boolean add(final E e) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean addAll(final Collection<? extends E> collection) {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    private static byte _get(final long n) {
        return Util.unsafe.getByte(n);
    }
    
    private static void _put(final long n, final byte b) {
        Util.unsafe.putByte(n, b);
    }
    
    static void erase(final ByteBuffer byteBuffer) {
        Util.unsafe.setMemory(((DirectBuffer)byteBuffer).address(), byteBuffer.capacity(), (byte)0);
    }
    
    static Unsafe unsafe() {
        return Util.unsafe;
    }
    
    static int pageSize() {
        if (Util.pageSize == -1) {
            Util.pageSize = unsafe().pageSize();
        }
        return Util.pageSize;
    }
    
    private static void initDBBConstructor() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                try {
                    final Constructor<?> declaredConstructor = Class.forName("java.nio.DirectByteBuffer").getDeclaredConstructor(Integer.TYPE, Long.TYPE, FileDescriptor.class, Runnable.class);
                    declaredConstructor.setAccessible(true);
                    Util.directByteBufferConstructor = declaredConstructor;
                }
                catch (final ClassNotFoundException | NoSuchMethodException | IllegalArgumentException | ClassCastException ex) {
                    throw new InternalError((Throwable)ex);
                }
                return null;
            }
        });
    }
    
    static MappedByteBuffer newMappedByteBuffer(final int n, final long n2, final FileDescriptor fileDescriptor, final Runnable runnable) {
        if (Util.directByteBufferConstructor == null) {
            initDBBConstructor();
        }
        MappedByteBuffer mappedByteBuffer;
        try {
            mappedByteBuffer = (MappedByteBuffer)Util.directByteBufferConstructor.newInstance(new Integer(n), new Long(n2), fileDescriptor, runnable);
        }
        catch (final InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new InternalError((Throwable)ex);
        }
        return mappedByteBuffer;
    }
    
    private static void initDBBRConstructor() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                try {
                    final Constructor<?> declaredConstructor = Class.forName("java.nio.DirectByteBufferR").getDeclaredConstructor(Integer.TYPE, Long.TYPE, FileDescriptor.class, Runnable.class);
                    declaredConstructor.setAccessible(true);
                    Util.directByteBufferRConstructor = declaredConstructor;
                }
                catch (final ClassNotFoundException | NoSuchMethodException | IllegalArgumentException | ClassCastException ex) {
                    throw new InternalError((Throwable)ex);
                }
                return null;
            }
        });
    }
    
    static MappedByteBuffer newMappedByteBufferR(final int n, final long n2, final FileDescriptor fileDescriptor, final Runnable runnable) {
        if (Util.directByteBufferRConstructor == null) {
            initDBBRConstructor();
        }
        MappedByteBuffer mappedByteBuffer;
        try {
            mappedByteBuffer = (MappedByteBuffer)Util.directByteBufferRConstructor.newInstance(new Integer(n), new Long(n2), fileDescriptor, runnable);
        }
        catch (final InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new InternalError((Throwable)ex);
        }
        return mappedByteBuffer;
    }
    
    static boolean atBugLevel(final String s) {
        if (Util.bugLevel == null) {
            if (!VM.isBooted()) {
                return false;
            }
            final String s2 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.nio.ch.bugLevel"));
            Util.bugLevel = ((s2 != null) ? s2 : "");
        }
        return Util.bugLevel.equals(s);
    }
    
    static {
        TEMP_BUF_POOL_SIZE = IOUtil.IOV_MAX;
        MAX_CACHED_BUFFER_SIZE = getMaxCachedBufferSize();
        Util.bufferCache = new ThreadLocal<BufferCache>() {
            @Override
            protected BufferCache initialValue() {
                return new BufferCache();
            }
        };
        Util.unsafe = Unsafe.getUnsafe();
        Util.pageSize = -1;
        Util.directByteBufferConstructor = null;
        Util.directByteBufferRConstructor = null;
        Util.bugLevel = null;
    }
    
    private static class BufferCache
    {
        private ByteBuffer[] buffers;
        private int count;
        private int start;
        
        private int next(final int n) {
            return (n + 1) % Util.TEMP_BUF_POOL_SIZE;
        }
        
        BufferCache() {
            this.buffers = new ByteBuffer[Util.TEMP_BUF_POOL_SIZE];
        }
        
        ByteBuffer get(final int n) {
            assert !isBufferTooLarge(n);
            if (this.count == 0) {
                return null;
            }
            final ByteBuffer[] buffers = this.buffers;
            ByteBuffer byteBuffer = buffers[this.start];
            if (byteBuffer.capacity() < n) {
                byteBuffer = null;
                int n2 = this.start;
                while ((n2 = this.next(n2)) != this.start) {
                    final ByteBuffer byteBuffer2 = buffers[n2];
                    if (byteBuffer2 == null) {
                        break;
                    }
                    if (byteBuffer2.capacity() >= n) {
                        byteBuffer = byteBuffer2;
                        break;
                    }
                }
                if (byteBuffer == null) {
                    return null;
                }
                buffers[n2] = buffers[this.start];
            }
            buffers[this.start] = null;
            this.start = this.next(this.start);
            --this.count;
            byteBuffer.rewind();
            byteBuffer.limit(n);
            return byteBuffer;
        }
        
        boolean offerFirst(final ByteBuffer byteBuffer) {
            assert !isBufferTooLarge(byteBuffer);
            if (this.count >= Util.TEMP_BUF_POOL_SIZE) {
                return false;
            }
            this.start = (this.start + Util.TEMP_BUF_POOL_SIZE - 1) % Util.TEMP_BUF_POOL_SIZE;
            this.buffers[this.start] = byteBuffer;
            ++this.count;
            return true;
        }
        
        boolean offerLast(final ByteBuffer byteBuffer) {
            assert !isBufferTooLarge(byteBuffer);
            if (this.count >= Util.TEMP_BUF_POOL_SIZE) {
                return false;
            }
            this.buffers[(this.start + this.count) % Util.TEMP_BUF_POOL_SIZE] = byteBuffer;
            ++this.count;
            return true;
        }
        
        boolean isEmpty() {
            return this.count == 0;
        }
        
        ByteBuffer removeFirst() {
            assert this.count > 0;
            final ByteBuffer byteBuffer = this.buffers[this.start];
            this.buffers[this.start] = null;
            this.start = this.next(this.start);
            --this.count;
            return byteBuffer;
        }
    }
}
