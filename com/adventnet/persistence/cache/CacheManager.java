package com.adventnet.persistence.cache;

public class CacheManager
{
    private static CacheRepository cacheRepo;
    
    public static void setCacheRepository(final CacheRepository cacheRepository) {
        CacheManager.cacheRepo = cacheRepository;
    }
    
    public static CacheRepository getCacheRepository() {
        if (CacheManager.cacheRepo == null) {
            initCacheRepository();
        }
        return CacheManager.cacheRepo;
    }
    
    private static void initCacheRepository() {
        final String cacheRepositoryClassName = "com.adventnet.persistence.cache.CacheRepositoryImpl";
        Object cacheRepository = null;
        try {
            final Class c = Thread.currentThread().getContextClassLoader().loadClass(cacheRepositoryClassName);
            cacheRepository = c.newInstance();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        setCacheRepository((CacheRepository)cacheRepository);
    }
}
