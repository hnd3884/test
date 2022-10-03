package sun.java2d.marlin;

import java.util.Arrays;

public final class ArrayCacheConst implements MarlinConst
{
    static final int BUCKETS = 8;
    static final int MIN_ARRAY_SIZE = 4096;
    static final int MAX_ARRAY_SIZE;
    static final int THRESHOLD_SMALL_ARRAY_SIZE = 4194304;
    static final int THRESHOLD_ARRAY_SIZE;
    static final long THRESHOLD_HUGE_ARRAY_SIZE;
    static final int[] ARRAY_SIZES;
    
    private ArrayCacheConst() {
    }
    
    static int getBucket(final int n) {
        for (int i = 0; i < ArrayCacheConst.ARRAY_SIZES.length; ++i) {
            if (n <= ArrayCacheConst.ARRAY_SIZES[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static int getNewSize(final int n, final int n2) {
        if (n2 < 0) {
            throw new ArrayIndexOutOfBoundsException("array exceeds maximum capacity !");
        }
        assert n >= 0;
        int n3;
        if (n > ArrayCacheConst.THRESHOLD_ARRAY_SIZE) {
            n3 = n + (n >> 1);
        }
        else {
            n3 = n << 1;
        }
        if (n3 < n2) {
            n3 = (n2 >> 12) + 1 << 12;
        }
        if (n3 < 0) {
            n3 = Integer.MAX_VALUE;
        }
        return n3;
    }
    
    public static long getNewLargeSize(final long n, final long n2) {
        if (n2 >> 31 != 0L) {
            throw new ArrayIndexOutOfBoundsException("array exceeds maximum capacity !");
        }
        assert n >= 0L;
        long n3;
        if (n > ArrayCacheConst.THRESHOLD_HUGE_ARRAY_SIZE) {
            n3 = n + (n >> 2);
        }
        else if (n > ArrayCacheConst.THRESHOLD_ARRAY_SIZE) {
            n3 = n + (n >> 1);
        }
        else if (n > 4194304L) {
            n3 = n << 1;
        }
        else {
            n3 = n << 2;
        }
        if (n3 < n2) {
            n3 = (n2 >> 12) + 1L << 12;
        }
        if (n3 > 2147483647L) {
            n3 = 2147483647L;
        }
        return n3;
    }
    
    static {
        ARRAY_SIZES = new int[8];
        int n = 4096;
        int n2 = 2;
        for (int i = 0; i < 8; ++i, n <<= n2) {
            ArrayCacheConst.ARRAY_SIZES[i] = n;
            if (ArrayCacheConst.DO_TRACE) {
                MarlinUtils.logInfo("arraySize[" + i + "]: " + n);
            }
            if (n >= 4194304) {
                n2 = 1;
            }
        }
        MAX_ARRAY_SIZE = n >> n2;
        if (ArrayCacheConst.MAX_ARRAY_SIZE <= 0) {
            throw new IllegalStateException("Invalid max array size !");
        }
        THRESHOLD_ARRAY_SIZE = 16777216;
        THRESHOLD_HUGE_ARRAY_SIZE = 50331648L;
        if (ArrayCacheConst.DO_STATS) {
            MarlinUtils.logInfo("ArrayCache.BUCKETS        = 8");
            MarlinUtils.logInfo("ArrayCache.MIN_ARRAY_SIZE = 4096");
            MarlinUtils.logInfo("ArrayCache.MAX_ARRAY_SIZE = " + ArrayCacheConst.MAX_ARRAY_SIZE);
            MarlinUtils.logInfo("ArrayCache.ARRAY_SIZES = " + Arrays.toString(ArrayCacheConst.ARRAY_SIZES));
            MarlinUtils.logInfo("ArrayCache.THRESHOLD_ARRAY_SIZE = " + ArrayCacheConst.THRESHOLD_ARRAY_SIZE);
            MarlinUtils.logInfo("ArrayCache.THRESHOLD_HUGE_ARRAY_SIZE = " + ArrayCacheConst.THRESHOLD_HUGE_ARRAY_SIZE);
        }
    }
    
    static final class CacheStats
    {
        final String name;
        final BucketStats[] bucketStats;
        int resize;
        int oversize;
        long totalInitial;
        
        CacheStats(final String name) {
            this.resize = 0;
            this.oversize = 0;
            this.totalInitial = 0L;
            this.name = name;
            this.bucketStats = new BucketStats[8];
            for (int i = 0; i < 8; ++i) {
                this.bucketStats[i] = new BucketStats();
            }
        }
        
        void reset() {
            this.resize = 0;
            this.oversize = 0;
            for (int i = 0; i < 8; ++i) {
                this.bucketStats[i].reset();
            }
        }
        
        long dumpStats() {
            long n = 0L;
            if (MarlinConst.DO_STATS) {
                for (int i = 0; i < 8; ++i) {
                    final BucketStats bucketStats = this.bucketStats[i];
                    if (bucketStats.maxSize != 0) {
                        n += this.getByteFactor() * (bucketStats.maxSize * ArrayCacheConst.ARRAY_SIZES[i]);
                    }
                }
                if (this.totalInitial != 0L || n != 0L || this.resize != 0 || this.oversize != 0) {
                    MarlinUtils.logInfo(this.name + ": resize: " + this.resize + " - oversize: " + this.oversize + " - initial: " + this.getTotalInitialBytes() + " bytes (" + this.totalInitial + " elements) - cache: " + n + " bytes");
                }
                if (n != 0L) {
                    MarlinUtils.logInfo(this.name + ": usage stats:");
                    for (int j = 0; j < 8; ++j) {
                        final BucketStats bucketStats2 = this.bucketStats[j];
                        if (bucketStats2.getOp != 0) {
                            MarlinUtils.logInfo("  Bucket[" + ArrayCacheConst.ARRAY_SIZES[j] + "]: get: " + bucketStats2.getOp + " - put: " + bucketStats2.returnOp + " - create: " + bucketStats2.createOp + " :: max size: " + bucketStats2.maxSize);
                        }
                    }
                }
            }
            return n;
        }
        
        private int getByteFactor() {
            int n = 1;
            if (this.name.contains("Int") || this.name.contains("Float")) {
                n = 4;
            }
            else if (this.name.contains("Double")) {
                n = 8;
            }
            return n;
        }
        
        long getTotalInitialBytes() {
            return this.getByteFactor() * this.totalInitial;
        }
    }
    
    static final class BucketStats
    {
        int getOp;
        int createOp;
        int returnOp;
        int maxSize;
        
        BucketStats() {
            this.getOp = 0;
            this.createOp = 0;
            this.returnOp = 0;
            this.maxSize = 0;
        }
        
        void reset() {
            this.getOp = 0;
            this.createOp = 0;
            this.returnOp = 0;
            this.maxSize = 0;
        }
        
        void updateMaxSize(final int maxSize) {
            if (maxSize > this.maxSize) {
                this.maxSize = maxSize;
            }
        }
    }
}
