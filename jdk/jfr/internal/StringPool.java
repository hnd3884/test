package jdk.jfr.internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import sun.misc.Unsafe;

public final class StringPool
{
    private static final Unsafe unsafe;
    static final int MIN_LIMIT = 16;
    static final int MAX_LIMIT = 128;
    private static final long epochAddress;
    private static final SimpleStringIdPool sp;
    
    public static long addString(final String s) {
        return StringPool.sp.addString(s);
    }
    
    private static boolean getCurrentEpoch() {
        return StringPool.unsafe.getByte(StringPool.epochAddress) == 1;
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
        sp = new SimpleStringIdPool();
        epochAddress = JVM.getJVM().getEpochAddress();
        StringPool.sp.reset();
    }
    
    private static class SimpleStringIdPool
    {
        private final AtomicLong sidIdx;
        private boolean poolEpoch;
        private final ConcurrentHashMap<String, Long> cache;
        private final int MAX_SIZE = 32768;
        private final long MAX_SIZE_UTF16 = 16777216L;
        private long currentSizeUTF16;
        private final String[] preCache;
        private int preCacheOld;
        private static final int preCacheMask = 3;
        
        SimpleStringIdPool() {
            this.sidIdx = new AtomicLong();
            this.preCache = new String[] { "", "", "", "" };
            this.preCacheOld = 0;
            this.cache = new ConcurrentHashMap<String, Long>(32768, 0.75f);
        }
        
        void reset() {
            this.reset(getCurrentEpoch());
        }
        
        private void reset(final boolean poolEpoch) {
            this.cache.clear();
            this.poolEpoch = poolEpoch;
            this.currentSizeUTF16 = 0L;
        }
        
        private long addString(final String s) {
            final boolean access$100 = getCurrentEpoch();
            if (this.poolEpoch == access$100) {
                final Long n = this.cache.get(s);
                if (n != null) {
                    return n;
                }
            }
            else {
                this.reset(access$100);
            }
            if (!this.preCache(s)) {
                return -1L;
            }
            if (this.cache.size() > 32768 || this.currentSizeUTF16 > 16777216L) {
                this.reset(access$100);
            }
            return this.storeString(s);
        }
        
        private long storeString(final String s) {
            final long andIncrement = this.sidIdx.getAndIncrement();
            this.cache.put(s, andIncrement);
            final boolean addStringConstant;
            synchronized (SimpleStringIdPool.class) {
                addStringConstant = JVM.addStringConstant(this.poolEpoch, andIncrement, s);
                this.currentSizeUTF16 += s.length();
            }
            return (addStringConstant == this.poolEpoch) ? andIncrement : -1L;
        }
        
        private boolean preCache(final String s) {
            if (this.preCache[0].equals(s)) {
                return true;
            }
            if (this.preCache[1].equals(s)) {
                return true;
            }
            if (this.preCache[2].equals(s)) {
                return true;
            }
            if (this.preCache[3].equals(s)) {
                return true;
            }
            this.preCacheOld = (this.preCacheOld - 1 & 0x3);
            this.preCache[this.preCacheOld] = s;
            return false;
        }
    }
}
