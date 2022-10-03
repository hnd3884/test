package sun.java2d.marlin;

import java.util.Arrays;
import java.lang.ref.WeakReference;

final class ByteArrayCache implements MarlinConst
{
    final boolean clean;
    private final int bucketCapacity;
    private WeakReference<Bucket[]> refBuckets;
    final ArrayCacheConst.CacheStats stats;
    
    ByteArrayCache(final boolean clean, final int bucketCapacity) {
        this.refBuckets = null;
        this.clean = clean;
        this.bucketCapacity = bucketCapacity;
        this.stats = (ByteArrayCache.DO_STATS ? new ArrayCacheConst.CacheStats(getLogPrefix(clean) + "ByteArrayCache") : null);
    }
    
    Bucket getCacheBucket(final int n) {
        return this.getBuckets()[ArrayCacheConst.getBucket(n)];
    }
    
    private Bucket[] getBuckets() {
        Bucket[] array = (Bucket[])((this.refBuckets != null) ? ((Bucket[])this.refBuckets.get()) : null);
        if (array == null) {
            array = new Bucket[8];
            for (int i = 0; i < 8; ++i) {
                array[i] = new Bucket(this.clean, ArrayCacheConst.ARRAY_SIZES[i], this.bucketCapacity, ByteArrayCache.DO_STATS ? this.stats.bucketStats[i] : null);
            }
            this.refBuckets = new WeakReference<Bucket[]>(array);
        }
        return array;
    }
    
    Reference createRef(final int n) {
        return new Reference(this, n);
    }
    
    static byte[] createArray(final int n) {
        return new byte[n];
    }
    
    static void fill(final byte[] array, final int n, final int n2, final byte b) {
        Arrays.fill(array, n, n2, b);
        if (ByteArrayCache.DO_CHECKS) {
            check(array, n, n2, b);
        }
    }
    
    static void check(final byte[] array, final int n, final int n2, final byte b) {
        if (ByteArrayCache.DO_CHECKS) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i] != b) {
                    MarlinUtils.logException("Invalid value at: " + i + " = " + array[i] + " from: " + n + " to: " + n2 + "\n" + Arrays.toString(array), new Throwable());
                    Arrays.fill(array, b);
                    return;
                }
            }
        }
    }
    
    static String getLogPrefix(final boolean b) {
        return b ? "Clean" : "Dirty";
    }
    
    static final class Reference
    {
        final byte[] initial;
        private final boolean clean;
        private final ByteArrayCache cache;
        
        Reference(final ByteArrayCache cache, final int n) {
            this.cache = cache;
            this.clean = cache.clean;
            this.initial = ByteArrayCache.createArray(n);
            if (MarlinConst.DO_STATS) {
                final ArrayCacheConst.CacheStats stats = cache.stats;
                stats.totalInitial += n;
            }
        }
        
        byte[] getArray(final int n) {
            if (n <= ArrayCacheConst.MAX_ARRAY_SIZE) {
                return this.cache.getCacheBucket(n).getArray();
            }
            if (MarlinConst.DO_STATS) {
                final ArrayCacheConst.CacheStats stats = this.cache.stats;
                ++stats.oversize;
            }
            if (MarlinConst.DO_LOG_OVERSIZE) {
                MarlinUtils.logInfo(ByteArrayCache.getLogPrefix(this.clean) + "ByteArrayCache: getArray[oversize]: length=\t" + n);
            }
            return ByteArrayCache.createArray(n);
        }
        
        byte[] widenArray(final byte[] array, final int n, final int n2) {
            final int length = array.length;
            if (MarlinConst.DO_CHECKS && length >= n2) {
                return array;
            }
            if (MarlinConst.DO_STATS) {
                final ArrayCacheConst.CacheStats stats = this.cache.stats;
                ++stats.resize;
            }
            final byte[] array2 = this.getArray(ArrayCacheConst.getNewSize(n, n2));
            System.arraycopy(array, 0, array2, 0, n);
            this.putArray(array, 0, n);
            if (MarlinConst.DO_LOG_WIDEN_ARRAY) {
                MarlinUtils.logInfo(ByteArrayCache.getLogPrefix(this.clean) + "ByteArrayCache: widenArray[" + array2.length + "]: usedSize=\t" + n + "\tlength=\t" + length + "\tneeded length=\t" + n2);
            }
            return array2;
        }
        
        byte[] putArray(final byte[] array) {
            return this.putArray(array, 0, array.length);
        }
        
        byte[] putArray(final byte[] array, final int n, final int n2) {
            if (array.length <= ArrayCacheConst.MAX_ARRAY_SIZE) {
                if (this.clean) {
                    if (n2 != 0) {
                        ByteArrayCache.fill(array, n, n2, (byte)0);
                    }
                }
                if (array != this.initial) {
                    this.cache.getCacheBucket(array.length).putArray(array);
                }
            }
            return this.initial;
        }
    }
    
    static final class Bucket
    {
        private int tail;
        private final int arraySize;
        private final boolean clean;
        private final byte[][] arrays;
        private final ArrayCacheConst.BucketStats stats;
        
        Bucket(final boolean clean, final int arraySize, final int n, final ArrayCacheConst.BucketStats stats) {
            this.tail = 0;
            this.arraySize = arraySize;
            this.clean = clean;
            this.stats = stats;
            this.arrays = new byte[n][];
        }
        
        byte[] getArray() {
            if (MarlinConst.DO_STATS) {
                final ArrayCacheConst.BucketStats stats = this.stats;
                ++stats.getOp;
            }
            if (this.tail != 0) {
                final byte[][] arrays = this.arrays;
                final int tail = this.tail - 1;
                this.tail = tail;
                final byte[] array = arrays[tail];
                this.arrays[this.tail] = null;
                return array;
            }
            if (MarlinConst.DO_STATS) {
                final ArrayCacheConst.BucketStats stats2 = this.stats;
                ++stats2.createOp;
            }
            return ByteArrayCache.createArray(this.arraySize);
        }
        
        void putArray(final byte[] array) {
            if (MarlinConst.DO_CHECKS && array.length != this.arraySize) {
                MarlinUtils.logInfo(ByteArrayCache.getLogPrefix(this.clean) + "ByteArrayCache: bad length = " + array.length);
                return;
            }
            if (MarlinConst.DO_STATS) {
                final ArrayCacheConst.BucketStats stats = this.stats;
                ++stats.returnOp;
            }
            if (this.arrays.length > this.tail) {
                this.arrays[this.tail++] = array;
                if (MarlinConst.DO_STATS) {
                    this.stats.updateMaxSize(this.tail);
                }
            }
            else if (MarlinConst.DO_CHECKS) {
                MarlinUtils.logInfo(ByteArrayCache.getLogPrefix(this.clean) + "ByteArrayCache: array capacity exceeded !");
            }
        }
    }
}
