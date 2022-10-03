package sun.security.pkcs11;

import java.util.IdentityHashMap;
import java.security.Key;
import java.util.Map;
import java.lang.ref.WeakReference;
import sun.security.util.Cache;

final class KeyCache
{
    private final Cache<IdentityWrapper, P11Key> strongCache;
    private WeakReference<Map<Key, P11Key>> cacheReference;
    
    KeyCache() {
        this.strongCache = Cache.newHardMemoryCache(16);
    }
    
    synchronized P11Key get(final Key key) {
        final P11Key p11Key = this.strongCache.get(new IdentityWrapper(key));
        if (p11Key != null) {
            return p11Key;
        }
        final Map map = (this.cacheReference == null) ? null : this.cacheReference.get();
        if (map == null) {
            return null;
        }
        return (P11Key)map.get(key);
    }
    
    synchronized void put(final Key key, final P11Key p11Key) {
        this.strongCache.put(new IdentityWrapper(key), p11Key);
        Map map = (this.cacheReference == null) ? null : this.cacheReference.get();
        if (map == null) {
            map = new IdentityHashMap();
            this.cacheReference = new WeakReference<Map<Key, P11Key>>(map);
        }
        map.put(key, p11Key);
    }
    
    private static final class IdentityWrapper
    {
        final Object obj;
        
        IdentityWrapper(final Object obj) {
            this.obj = obj;
        }
        
        @Override
        public boolean equals(final Object o) {
            return this == o || (o instanceof IdentityWrapper && this.obj == ((IdentityWrapper)o).obj);
        }
        
        @Override
        public int hashCode() {
            return System.identityHashCode(this.obj);
        }
    }
}
