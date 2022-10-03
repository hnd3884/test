package com.adventnet.persistence.cache;

import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.AlterTableQuery;
import java.util.ArrayList;
import com.adventnet.db.persistence.metadata.MetaDataChangeEvent;
import com.adventnet.persistence.OperationInfo;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CacheRepositoryImpl implements CacheRepository
{
    private static Logger logger;
    private DataObjectCache dataObjectCache;
    private CacheStatsUtil cacheStats;
    
    public CacheRepositoryImpl() {
        this.dataObjectCache = null;
        this.cacheStats = new CacheStatsUtil();
    }
    
    @Override
    public void initialize(final int maxSize, final boolean useSoftReference) {
        this.dataObjectCache = new DataObjectCache(maxSize, useSoftReference, this.cacheStats);
        if (useSoftReference) {
            final Thread cleanupThread = new Thread(new CleanUpRunnable());
            cleanupThread.setDaemon(true);
            cleanupThread.start();
        }
    }
    
    @Override
    public Object addToCache(final Object refId, final Object cachedData) {
        CacheRepositoryImpl.logger.log(Level.FINER, " Object {0}  added to the cache refId = {0} , cachedData = {1} ", new Object[] { refId, cachedData });
        return this.dataObjectCache.addToCache(refId, cachedData);
    }
    
    @Override
    public Object addToCache(final Object refId, final Object cachedData, final List tablesList) {
        CacheRepositoryImpl.logger.log(Level.FINER, " Object {0}  added to the cache", refId);
        return this.dataObjectCache.addToCache(refId, cachedData, tablesList);
    }
    
    @Override
    public void clearCachedData() {
        this.dataObjectCache.clearAllCachedData();
    }
    
    @Override
    public void clearCachedData(final String dbID) {
        this.dataObjectCache.clearAllCachedData();
    }
    
    @Override
    public String getCacheSummary(final boolean includeTimeSummary, final boolean includeCachedData) {
        return this.dataObjectCache.getCacheSummary(includeTimeSummary, includeCachedData);
    }
    
    @Override
    public void writeCacheSummary(final OutputStream os, final boolean includeTimeSummary, final boolean includeCachedData) {
        try {
            this.dataObjectCache.writeCacheSummary(os, includeTimeSummary, includeCachedData);
        }
        catch (final IOException e) {
            CacheRepositoryImpl.logger.log(Level.SEVERE, " " + e);
        }
    }
    
    @Override
    public boolean getCachingStatus() {
        return this.dataObjectCache.getCacheingStatus();
    }
    
    @Override
    public boolean getCloningStatus() {
        return this.dataObjectCache.getCloneingStatus();
    }
    
    @Override
    public Object getFromCache(final Object refId) {
        return this.dataObjectCache.getFromCache(refId);
    }
    
    @Override
    public Object getFromCache(final Object refId, final List tablesList, final boolean cloneData) {
        return this.dataObjectCache.getFromCache(refId, cloneData);
    }
    
    @Override
    public String getRemovedTableSummary() {
        return this.dataObjectCache.getRemovedTableSummary();
    }
    
    @Override
    public boolean isUseSoftReference() {
        return this.dataObjectCache.isUseSoftReference();
    }
    
    @Override
    public void removeCachedData(final Object refId) {
        this.dataObjectCache.clearCachedData(refId);
    }
    
    @Override
    public List removeCachedData(final List tablesList) {
        return this.dataObjectCache.clearCachedData(tablesList);
    }
    
    @Override
    public void setCachingStatus(final boolean status) {
        this.dataObjectCache.setCacheingStatus(status);
    }
    
    @Override
    public void setCloningStatus(final boolean cloning) {
        this.dataObjectCache.setCloneingStatus(cloning);
    }
    
    @Override
    public void setUseSoftReference(final boolean useSoftReferenceArg) {
        this.dataObjectCache.setUseSoftReference(useSoftReferenceArg);
    }
    
    @Override
    public void changeCacheForChangeInDeployment() {
        this.clearCachedData();
    }
    
    @Override
    public void changeCacheForChangeInTables(final Object notifiedDataObject) {
        List tablesList = null;
        if (notifiedDataObject instanceof OperationInfo) {
            tablesList = ((OperationInfo)notifiedDataObject).getTableNames();
        }
        else if (notifiedDataObject instanceof MetaDataChangeEvent) {
            final MetaDataChangeEvent mdcEvent = (MetaDataChangeEvent)notifiedDataObject;
            final int operationType = mdcEvent.getOperationType();
            tablesList = new ArrayList();
            switch (operationType) {
                case 2: {
                    final AlterTableQuery alterTableQuery = (AlterTableQuery)mdcEvent.getObject();
                    tablesList.add(alterTableQuery.getTableName());
                    break;
                }
                case 3: {
                    final TableDefinition tableDefinition = (TableDefinition)mdcEvent.getObject();
                    tablesList.add(tableDefinition.getTableName());
                    break;
                }
                case 5: {
                    final List tables = (List)mdcEvent.getObject();
                    for (int i = 0; i < tables.size(); ++i) {
                        tablesList.add(tables.get(i).getTableName());
                    }
                    break;
                }
            }
        }
        if (tablesList != null) {
            this.removeCachedData(tablesList);
        }
    }
    
    @Override
    public int getMaxSize() {
        return this.dataObjectCache.getMaximumSize();
    }
    
    @Override
    public void setMaxSize(final int newMaxSize) {
        this.dataObjectCache.setMaximumSize(newMaxSize);
    }
    
    @Override
    public long currentSize() {
        return this.dataObjectCache.getCurrentSize();
    }
    
    @Override
    public long getCount() {
        return this.cacheStats.getCount();
    }
    
    @Override
    public long getMissCount() {
        return this.cacheStats.getMissCount();
    }
    
    @Override
    public long putCount() {
        return this.cacheStats.putCount();
    }
    
    @Override
    public long putMissCount() {
        return this.cacheStats.putMissCount();
    }
    
    @Override
    public long evictionCount() {
        return this.cacheStats.evictionCount();
    }
    
    static {
        CacheRepositoryImpl.logger = Logger.getLogger(CacheRepositoryImpl.class.getName());
    }
    
    private class CleanUpRunnable implements Runnable
    {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(30000L);
                }
                catch (final Exception ex) {}
                CacheRepositoryImpl.this.dataObjectCache.cleanUp();
            }
        }
    }
}
