package sun.security.util;

import java.util.Map;
import java.util.Arrays;

public abstract class Cache<K, V>
{
    protected Cache() {
    }
    
    public abstract int size();
    
    public abstract void clear();
    
    public abstract void put(final K p0, final V p1);
    
    public abstract V get(final Object p0);
    
    public abstract void remove(final Object p0);
    
    public abstract void setCapacity(final int p0);
    
    public abstract void setTimeout(final int p0);
    
    public abstract void accept(final CacheVisitor<K, V> p0);
    
    public static <K, V> Cache<K, V> newSoftMemoryCache(final int n) {
        return new MemoryCache<K, V>(true, n);
    }
    
    public static <K, V> Cache<K, V> newSoftMemoryCache(final int n, final int n2) {
        return new MemoryCache<K, V>(true, n, n2);
    }
    
    public static <K, V> Cache<K, V> newHardMemoryCache(final int n) {
        return new MemoryCache<K, V>(false, n);
    }
    
    public static <K, V> Cache<K, V> newNullCache() {
        return (Cache<K, V>)NullCache.INSTANCE;
    }
    
    public static <K, V> Cache<K, V> newHardMemoryCache(final int n, final int n2) {
        return new MemoryCache<K, V>(false, n, n2);
    }
    
    public static class EqualByteArray
    {
        private final byte[] b;
        private volatile int hash;
        
        public EqualByteArray(final byte[] b) {
            this.b = b;
        }
        
        @Override
        public int hashCode() {
            int hash = this.hash;
            if (hash == 0) {
                hash = this.b.length + 1;
                for (int i = 0; i < this.b.length; ++i) {
                    hash += (this.b[i] & 0xFF) * 37;
                }
                this.hash = hash;
            }
            return hash;
        }
        
        @Override
        public boolean equals(final Object o) {
            return this == o || (o instanceof EqualByteArray && Arrays.equals(this.b, ((EqualByteArray)o).b));
        }
    }
    
    public interface CacheVisitor<K, V>
    {
        void visit(final Map<K, V> p0);
    }
}
