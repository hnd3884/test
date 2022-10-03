package sun.java2d.marlin;

import java.util.Arrays;
import java.lang.ref.WeakReference;

final class DoubleArrayCache implements MarlinConst
{
    final boolean clean;
    private final int bucketCapacity;
    private WeakReference<Bucket[]> refBuckets;
    final ArrayCacheConst.CacheStats stats;
    
    DoubleArrayCache(final boolean clean, final int bucketCapacity) {
        this.refBuckets = null;
        this.clean = clean;
        this.bucketCapacity = bucketCapacity;
        this.stats = (DoubleArrayCache.DO_STATS ? new ArrayCacheConst.CacheStats(getLogPrefix(clean) + "DoubleArrayCache") : null);
    }
    
    Bucket getCacheBucket(final int n) {
        return this.getBuckets()[ArrayCacheConst.getBucket(n)];
    }
    
    private Bucket[] getBuckets() {
        Bucket[] array = (Bucket[])((this.refBuckets != null) ? ((Bucket[])this.refBuckets.get()) : null);
        if (array == null) {
            array = new Bucket[8];
            for (int i = 0; i < 8; ++i) {
                array[i] = new Bucket(this.clean, ArrayCacheConst.ARRAY_SIZES[i], this.bucketCapacity, DoubleArrayCache.DO_STATS ? this.stats.bucketStats[i] : null);
            }
            this.refBuckets = new WeakReference<Bucket[]>(array);
        }
        return array;
    }
    
    Reference createRef(final int n) {
        return new Reference(this, n);
    }
    
    static double[] createArray(final int n) {
        return new double[n];
    }
    
    static void fill(final double[] array, final int n, final int n2, final double n3) {
        Arrays.fill(array, n, n2, n3);
        if (DoubleArrayCache.DO_CHECKS) {
            check(array, n, n2, n3);
        }
    }
    
    static void check(final double[] array, final int n, final int n2, final double n3) {
        if (DoubleArrayCache.DO_CHECKS) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i] != n3) {
                    MarlinUtils.logException("Invalid value at: " + i + " = " + array[i] + " from: " + n + " to: " + n2 + "\n" + Arrays.toString(array), new Throwable());
                    Arrays.fill(array, n3);
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
        final double[] initial;
        private final boolean clean;
        private final DoubleArrayCache cache;
        
        Reference(final DoubleArrayCache cache, final int n) {
            this.cache = cache;
            this.clean = cache.clean;
            this.initial = DoubleArrayCache.createArray(n);
            if (MarlinConst.DO_STATS) {
                final ArrayCacheConst.CacheStats stats = cache.stats;
                stats.totalInitial += n;
            }
        }
        
        double[] getArray(final int n) {
            if (n <= ArrayCacheConst.MAX_ARRAY_SIZE) {
                return this.cache.getCacheBucket(n).getArray();
            }
            if (MarlinConst.DO_STATS) {
                final ArrayCacheConst.CacheStats stats = this.cache.stats;
                ++stats.oversize;
            }
            if (MarlinConst.DO_LOG_OVERSIZE) {
                MarlinUtils.logInfo(DoubleArrayCache.getLogPrefix(this.clean) + "DoubleArrayCache: getArray[oversize]: length=\t" + n);
            }
            return DoubleArrayCache.createArray(n);
        }
        
        double[] widenArray(final double[] array, final int n, final int n2) {
            final int length = array.length;
            if (MarlinConst.DO_CHECKS && length >= n2) {
                return array;
            }
            if (MarlinConst.DO_STATS) {
                final ArrayCacheConst.CacheStats stats = this.cache.stats;
                ++stats.resize;
            }
            final double[] array2 = this.getArray(ArrayCacheConst.getNewSize(n, n2));
            System.arraycopy(array, 0, array2, 0, n);
            this.putArray(array, 0, n);
            if (MarlinConst.DO_LOG_WIDEN_ARRAY) {
                MarlinUtils.logInfo(DoubleArrayCache.getLogPrefix(this.clean) + "DoubleArrayCache: widenArray[" + array2.length + "]: usedSize=\t" + n + "\tlength=\t" + length + "\tneeded length=\t" + n2);
            }
            return array2;
        }
        
        double[] putArray(final double[] array) {
            return this.putArray(array, 0, array.length);
        }
        
        double[] putArray(final double[] array, final int n, final int n2) {
            if (array.length <= ArrayCacheConst.MAX_ARRAY_SIZE) {
                if (this.clean) {
                    if (n2 != 0) {
                        DoubleArrayCache.fill(array, n, n2, 0.0);
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
        private final double[][] arrays;
        private final ArrayCacheConst.BucketStats stats;
        
        Bucket(final boolean clean, final int arraySize, final int n, final ArrayCacheConst.BucketStats stats) {
            this.tail = 0;
            this.arraySize = arraySize;
            this.clean = clean;
            this.stats = stats;
            this.arrays = new double[n][];
        }
        
        double[] getArray() {
            if (MarlinConst.DO_STATS) {
                final ArrayCacheConst.BucketStats stats = this.stats;
                ++stats.getOp;
            }
            if (this.tail != 0) {
                final double[][] arrays = this.arrays;
                final int tail = this.tail - 1;
                this.tail = tail;
                final double[] array = arrays[tail];
                this.arrays[this.tail] = null;
                return array;
            }
            if (MarlinConst.DO_STATS) {
                final ArrayCacheConst.BucketStats stats2 = this.stats;
                ++stats2.createOp;
            }
            return DoubleArrayCache.createArray(this.arraySize);
        }
        
        void putArray(final double[] array) {
            if (MarlinConst.DO_CHECKS && array.length != this.arraySize) {
                MarlinUtils.logInfo(DoubleArrayCache.getLogPrefix(this.clean) + "DoubleArrayCache: bad length = " + array.length);
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
                MarlinUtils.logInfo(DoubleArrayCache.getLogPrefix(this.clean) + "DoubleArrayCache: array capacity exceeded !");
            }
        }
    }
}
