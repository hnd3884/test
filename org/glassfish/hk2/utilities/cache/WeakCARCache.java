package org.glassfish.hk2.utilities.cache;

public interface WeakCARCache<K, V>
{
    V compute(final K p0);
    
    int getKeySize();
    
    int getValueSize();
    
    int getT1Size();
    
    int getT2Size();
    
    int getB1Size();
    
    int getB2Size();
    
    void clear();
    
    int getMaxSize();
    
    Computable<K, V> getComputable();
    
    boolean remove(final K p0);
    
    void releaseMatching(final CacheKeyFilter<K> p0);
    
    void clearStaleReferences();
    
    int getP();
    
    String dumpAllLists();
    
    double getHitRate();
}
