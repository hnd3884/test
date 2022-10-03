package io.netty.handler.ssl;

import java.util.Iterator;
import io.netty.buffer.ByteBufAllocator;
import java.util.concurrent.ConcurrentHashMap;
import javax.net.ssl.X509KeyManager;
import java.util.concurrent.ConcurrentMap;

final class OpenSslCachingKeyMaterialProvider extends OpenSslKeyMaterialProvider
{
    private final int maxCachedEntries;
    private volatile boolean full;
    private final ConcurrentMap<String, OpenSslKeyMaterial> cache;
    
    OpenSslCachingKeyMaterialProvider(final X509KeyManager keyManager, final String password, final int maxCachedEntries) {
        super(keyManager, password);
        this.cache = new ConcurrentHashMap<String, OpenSslKeyMaterial>();
        this.maxCachedEntries = maxCachedEntries;
    }
    
    @Override
    OpenSslKeyMaterial chooseKeyMaterial(final ByteBufAllocator allocator, final String alias) throws Exception {
        OpenSslKeyMaterial material = this.cache.get(alias);
        if (material == null) {
            material = super.chooseKeyMaterial(allocator, alias);
            if (material == null) {
                return null;
            }
            if (this.full) {
                return material;
            }
            if (this.cache.size() > this.maxCachedEntries) {
                this.full = true;
                return material;
            }
            final OpenSslKeyMaterial old = this.cache.putIfAbsent(alias, material);
            if (old != null) {
                material.release();
                material = old;
            }
        }
        return material.retain();
    }
    
    @Override
    void destroy() {
        do {
            final Iterator<OpenSslKeyMaterial> iterator = this.cache.values().iterator();
            while (iterator.hasNext()) {
                iterator.next().release();
                iterator.remove();
            }
        } while (!this.cache.isEmpty());
    }
}
