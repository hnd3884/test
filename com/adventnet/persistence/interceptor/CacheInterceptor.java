package com.adventnet.persistence.interceptor;

import com.adventnet.persistence.DataAccessException;
import java.util.List;
import com.adventnet.persistence.cache.CacheManager;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CacheInterceptor implements PersistenceInterceptor
{
    private static Logger logger;
    PersistenceInterceptor nextPI;
    
    public CacheInterceptor() {
        this.nextPI = null;
        CacheInterceptor.logger.log(Level.FINER, " Inside CacheInterceptor");
    }
    
    @Override
    public String getInterceptorName() {
        return "CacheInterceptor";
    }
    
    @Override
    public void setBeanConfiguration(final DataObject beanDO) {
    }
    
    @Override
    public Object process(final PersistenceRequest pRequest) throws DataAccessException {
        CacheInterceptor.logger.log(Level.FINER, " Processing {0}", pRequest);
        CacheInterceptor.logger.log(Level.FINER, " Processing {0}", pRequest);
        if (!(pRequest instanceof RetrievePersistenceRequest)) {
            return this.nextPI.process(pRequest);
        }
        Object data = null;
        try {
            data = CacheManager.getCacheRepository().getFromCache(((RetrievePersistenceRequest)pRequest).getQuery(), null, true);
        }
        catch (final Exception ex) {
            CacheInterceptor.logger.log(Level.SEVERE, "Exception occured while trying to get cache from the cache repository for {0}", pRequest);
        }
        CacheInterceptor.logger.log(Level.FINER, " The data is cached {0}", new Boolean(data != null));
        if (data != null) {
            return data;
        }
        data = this.nextPI.process(pRequest);
        if (data != null) {
            try {
                data = CacheManager.getCacheRepository().addToCache(((RetrievePersistenceRequest)pRequest).getQuery(), data, null);
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        return data;
    }
    
    @Override
    public void setNextInterceptor(final PersistenceInterceptor nextPI) {
        this.nextPI = nextPI;
    }
    
    @Override
    public void cleanup() {
    }
    
    static {
        CacheInterceptor.logger = Logger.getLogger(CacheInterceptor.class.getName());
    }
}
