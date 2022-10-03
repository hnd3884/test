package sun.security.util;

class NullCache<K, V> extends Cache<K, V>
{
    static final Cache<Object, Object> INSTANCE;
    
    private NullCache() {
    }
    
    @Override
    public int size() {
        return 0;
    }
    
    @Override
    public void clear() {
    }
    
    @Override
    public void put(final K k, final V v) {
    }
    
    @Override
    public V get(final Object o) {
        return null;
    }
    
    @Override
    public void remove(final Object o) {
    }
    
    @Override
    public void setCapacity(final int n) {
    }
    
    @Override
    public void setTimeout(final int n) {
    }
    
    @Override
    public void accept(final CacheVisitor<K, V> cacheVisitor) {
    }
    
    static {
        INSTANCE = new NullCache<Object, Object>();
    }
}
