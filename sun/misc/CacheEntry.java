package sun.misc;

class CacheEntry extends Ref
{
    int hash;
    Object key;
    CacheEntry next;
    
    @Override
    public Object reconstitute() {
        return null;
    }
}
