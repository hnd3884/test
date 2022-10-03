package javax.imageio.stream;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;

class MemoryCache
{
    private static final int BUFFER_LENGTH = 8192;
    private ArrayList cache;
    private long cacheStart;
    private long length;
    
    MemoryCache() {
        this.cache = new ArrayList();
        this.cacheStart = 0L;
        this.length = 0L;
    }
    
    private byte[] getCacheBlock(final long n) throws IOException {
        final long n2 = n - this.cacheStart;
        if (n2 > 2147483647L) {
            throw new IOException("Cache addressing limit exceeded!");
        }
        return (byte[])this.cache.get((int)n2);
    }
    
    public long loadFromStream(final InputStream inputStream, final long n) throws IOException {
        if (n < this.length) {
            return n;
        }
        int n2 = (int)(this.length % 8192L);
        byte[] cacheBlock = null;
        long n3 = n - this.length;
        if (n2 != 0) {
            cacheBlock = this.getCacheBlock(this.length / 8192L);
        }
        while (n3 > 0L) {
            if (cacheBlock == null) {
                try {
                    cacheBlock = new byte[8192];
                }
                catch (final OutOfMemoryError outOfMemoryError) {
                    throw new IOException("No memory left for cache!");
                }
                n2 = 0;
            }
            final int read = inputStream.read(cacheBlock, n2, (int)Math.min(n3, 8192 - n2));
            if (read == -1) {
                return this.length;
            }
            if (n2 == 0) {
                this.cache.add(cacheBlock);
            }
            n3 -= read;
            this.length += read;
            n2 += read;
            if (n2 < 8192) {
                continue;
            }
            cacheBlock = null;
        }
        return n;
    }
    
    public void writeToStream(final OutputStream outputStream, final long n, long n2) throws IOException {
        if (n + n2 > this.length) {
            throw new IndexOutOfBoundsException("Argument out of cache");
        }
        if (n < 0L || n2 < 0L) {
            throw new IndexOutOfBoundsException("Negative pos or len");
        }
        if (n2 == 0L) {
            return;
        }
        final long n3 = n / 8192L;
        if (n3 < this.cacheStart) {
            throw new IndexOutOfBoundsException("pos already disposed");
        }
        int n4 = (int)(n % 8192L);
        final long n5 = n3;
        long n6 = n5 + 1L;
        byte[] array = this.getCacheBlock(n5);
        while (n2 > 0L) {
            if (array == null) {
                array = this.getCacheBlock(n6++);
                n4 = 0;
            }
            final int n7 = (int)Math.min(n2, 8192 - n4);
            outputStream.write(array, n4, n7);
            array = null;
            n2 -= n7;
        }
    }
    
    private void pad(final long n) throws IOException {
        for (long n2 = n / 8192L - (this.cacheStart + this.cache.size() - 1L), n3 = 0L; n3 < n2; ++n3) {
            try {
                this.cache.add(new byte[8192]);
            }
            catch (final OutOfMemoryError outOfMemoryError) {
                throw new IOException("No memory left for cache!");
            }
        }
    }
    
    public void write(final byte[] array, int n, int i, long n2) throws IOException {
        if (array == null) {
            throw new NullPointerException("b == null!");
        }
        if (n < 0 || i < 0 || n2 < 0L || n + i > array.length || n + i < 0) {
            throw new IndexOutOfBoundsException();
        }
        final long n3 = n2 + i - 1L;
        if (n3 >= this.length) {
            this.pad(n3);
            this.length = n3 + 1L;
        }
        int n4 = (int)(n2 % 8192L);
        while (i > 0) {
            final byte[] cacheBlock = this.getCacheBlock(n2 / 8192L);
            final int min = Math.min(i, 8192 - n4);
            System.arraycopy(array, n, cacheBlock, n4, min);
            n2 += min;
            n += min;
            i -= min;
            n4 = 0;
        }
    }
    
    public void write(final int n, final long n2) throws IOException {
        if (n2 < 0L) {
            throw new ArrayIndexOutOfBoundsException("pos < 0");
        }
        if (n2 >= this.length) {
            this.pad(n2);
            this.length = n2 + 1L;
        }
        this.getCacheBlock(n2 / 8192L)[(int)(n2 % 8192L)] = (byte)n;
    }
    
    public long getLength() {
        return this.length;
    }
    
    public int read(final long n) throws IOException {
        if (n >= this.length) {
            return -1;
        }
        final byte[] cacheBlock = this.getCacheBlock(n / 8192L);
        if (cacheBlock == null) {
            return -1;
        }
        return cacheBlock[(int)(n % 8192L)] & 0xFF;
    }
    
    public void read(final byte[] array, int n, int i, final long n2) throws IOException {
        if (array == null) {
            throw new NullPointerException("b == null!");
        }
        if (n < 0 || i < 0 || n2 < 0L || n + i > array.length || n + i < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (n2 + i > this.length) {
            throw new IndexOutOfBoundsException();
        }
        long n3 = n2 / 8192L;
        int n4 = (int)n2 % 8192;
        while (i > 0) {
            final int min = Math.min(i, 8192 - n4);
            System.arraycopy(this.getCacheBlock(n3++), n4, array, n, min);
            i -= min;
            n += min;
            n4 = 0;
        }
    }
    
    public void disposeBefore(final long n) {
        final long cacheStart = n / 8192L;
        if (cacheStart < this.cacheStart) {
            throw new IndexOutOfBoundsException("pos already disposed");
        }
        for (long min = Math.min(cacheStart - this.cacheStart, this.cache.size()), n2 = 0L; n2 < min; ++n2) {
            this.cache.remove(0);
        }
        this.cacheStart = cacheStart;
    }
    
    public void reset() {
        this.cache.clear();
        this.cacheStart = 0L;
        this.length = 0L;
    }
}
