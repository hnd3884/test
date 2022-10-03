package com.adventnet.persistence.cache;

import java.util.LinkedHashMap;
import com.adventnet.ds.query.Table;
import java.io.IOException;
import com.adventnet.persistence.DataObject;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.HashSet;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.lang.ref.SoftReference;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class DataObjectCache
{
    private static Logger logger;
    private static Set<String> tablesNotToBeChecked;
    private String[] divs;
    private boolean useSoftReference;
    private boolean cachingStatus;
    private boolean cloningStatus;
    private CacheStatsUtil cacheStats;
    private volatile Map doCache;
    private LockUtil lockUtil;
    private int maxSize;
    private Map<String, String> cachedTables;
    private ConcurrentHashMap<String, Integer[]> removedTableStats;
    
    public DataObjectCache(final int maxSize, final boolean useSoftReference, final CacheStatsUtil cacheStats) {
        this.divs = new String[] { "\n----------------------------------------------------------------------------------------", "\n========================================================================================" };
        this.useSoftReference = true;
        this.cachingStatus = false;
        this.cloningStatus = true;
        this.cacheStats = null;
        this.doCache = null;
        this.lockUtil = null;
        this.maxSize = -1;
        this.cachedTables = new ConcurrentHashMap<String, String>();
        this.removedTableStats = new ConcurrentHashMap<String, Integer[]>();
        this.maxSize = maxSize;
        this.useSoftReference = useSoftReference;
        this.cacheStats = cacheStats;
        this.initDOCacheMap();
    }
    
    private void initDOCacheMap() {
        if (this.maxSize == -1) {
            this.doCache = new ConcurrentHashMap();
            this.lockUtil = new LockUtil() {
                @Override
                public void acquireReadLock() {
                }
                
                @Override
                public void releaseReadLock() {
                }
                
                @Override
                public void acquireWriteLock() {
                }
                
                @Override
                public void releaseWriteLock() {
                }
            };
        }
        else {
            final ReadWriteLock rwl = new ReentrantReadWriteLock();
            this.doCache = new SyncMap(new LRUMap(this.maxSize), rwl);
            this.lockUtil = new LockUtil() {
                @Override
                public void acquireReadLock() {
                    rwl.readLock().lock();
                }
                
                @Override
                public void releaseReadLock() {
                    rwl.readLock().unlock();
                }
                
                @Override
                public void acquireWriteLock() {
                    rwl.writeLock().lock();
                }
                
                @Override
                public void releaseWriteLock() {
                    rwl.writeLock().unlock();
                }
            };
        }
    }
    
    public Object getFromCache(final Object refId) {
        return this.getFromCache(refId, true);
    }
    
    public Object getFromCache(final Object refId, final boolean cloneData) {
        this.cacheStats.incrGetCount();
        if (!this.cachingStatus) {
            this.cacheStats.incrGetMissCount();
            return null;
        }
        final CachedDOReference cachedRef = this.getCachedData(this.doCache.get(refId));
        if (cachedRef == null) {
            DataObjectCache.logger.log(Level.FINER, " Data not present  for {0}", refId);
            this.cacheStats.incrGetMissCount();
            return null;
        }
        cachedRef.numTimesUsed++;
        if (!cloneData) {
            return cachedRef.cachedData;
        }
        return this.cloneIfNeeded(cachedRef.cachedData);
    }
    
    public Object addToCache(final Object refId, final Object cachedData) {
        return this.addToCache(refId, cachedData, new ArrayList<String>());
    }
    
    public Object addToCache(final Object refId, final Object cachedData, final List<String> tablesList) {
        this.cacheStats.incrPutCount();
        if (!this.cachingStatus) {
            this.cacheStats.incrPutMissCount();
            return cachedData;
        }
        SelectQuery selQuery = null;
        if (refId instanceof SelectQuery) {
            selQuery = (SelectQuery)refId;
        }
        DataObjectCache.logger.log(Level.FINER, " Adding cached data for {0}", refId);
        final CachedDOReference ref = new CachedDOReference(refId, cachedData, selQuery, tablesList);
        final List tableNames = ref.tablesList;
        for (int i = 0, j = tableNames.size(); i < j; ++i) {
            final String tableName = tableNames.get(i);
            if (!DataObjectCache.tablesNotToBeChecked.contains(tableName)) {
                if (this.isTemplate(tableName)) {
                    throw new IllegalArgumentException("Trying to store template table [" + tableName + "] data in cache. Not Supported.");
                }
                this.cachedTables.put(tableName, tableName);
            }
        }
        if (cachedData instanceof WritableDataObject) {
            ((WritableDataObject)cachedData).makeImmutable();
        }
        Object cacheValue = ref;
        if (this.useSoftReference) {
            cacheValue = new SoftReference(ref);
        }
        this.doCache.put(refId, cacheValue);
        return this.cloneIfNeeded(cachedData);
    }
    
    private boolean isTemplate(final String tableName) {
        try {
            final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
            return td != null && td.isTemplate();
        }
        catch (final MetaDataException exp) {
            throw new RuntimeException(exp);
        }
    }
    
    public boolean getCacheingStatus() {
        return this.cachingStatus;
    }
    
    public void setCacheingStatus(final boolean status) {
        final boolean oldstatus = this.cachingStatus;
        this.cachingStatus = status;
        if (oldstatus && !status) {
            this.clearAllCachedData();
        }
    }
    
    public boolean getCloneingStatus() {
        return this.cloningStatus;
    }
    
    public void setCloneingStatus(final boolean status) {
        this.cloningStatus = status;
    }
    
    public boolean isUseSoftReference() {
        return this.useSoftReference;
    }
    
    public void setUseSoftReference(final boolean useSoftReferenceArg) {
        final boolean oldstatus = this.useSoftReference;
        this.useSoftReference = useSoftReferenceArg;
        if (oldstatus != useSoftReferenceArg) {
            this.clearAllCachedData();
        }
    }
    
    public long getCurrentSize() {
        return this.doCache.size();
    }
    
    public int getMaximumSize() {
        return this.maxSize;
    }
    
    public void setMaximumSize(final int new_maxSize) {
        final int old_maxSize = this.maxSize;
        this.maxSize = new_maxSize;
        if (old_maxSize == new_maxSize || (old_maxSize != -1 && new_maxSize > old_maxSize)) {
            return;
        }
        this.clearAllCachedData();
    }
    
    public void clearCachedData(final Object refId) {
        DataObjectCache.logger.log(Level.FINER, "clearCachedData refId {0}", refId);
        if (refId == null) {
            return;
        }
        if (refId instanceof CacheComparator) {
            this.removeFromCache((CacheComparator)refId);
            return;
        }
        this.doCache.remove(refId);
    }
    
    public List clearCachedData(final List tablesList) {
        DataObjectCache.logger.log(Level.FINER, "clearCachedData tablesList {0}", tablesList);
        if (tablesList == null) {
            return null;
        }
        final List removedObjectIds = new ArrayList();
        final Set<String> toBeCleared = new HashSet<String>();
        for (int i = 0, j = tablesList.size(); i < j; ++i) {
            final String remTableName = tablesList.get(i);
            if (this.cachedTables.remove(remTableName) != null) {
                toBeCleared.add(remTableName);
                Integer[] counts = this.removedTableStats.get(remTableName);
                if (counts == null) {
                    counts = new Integer[] { new Integer(1), new Integer(0) };
                    this.removedTableStats.put(remTableName, counts);
                }
                else {
                    counts[0] = new Integer(counts[0] + 1);
                }
            }
        }
        if (toBeCleared.size() > 0) {
            this.lockUtil.acquireWriteLock();
            try {
                DataObjectCache.logger.log(Level.FINER, " To Be Cleared {0}", toBeCleared);
                final Iterator ite = this.doCache.values().iterator();
                while (ite.hasNext()) {
                    final CachedDOReference ref = this.getCachedData(ite.next());
                    if (ref == null) {
                        continue;
                    }
                    for (int size = ref.tablesList.size(), k = 0; k < size; ++k) {
                        final String tableName = ref.tablesList.get(k);
                        if (toBeCleared.contains(tableName)) {
                            DataObjectCache.logger.log(Level.FINER, "DataObject refering to table {0}. Removing DO {1}", new Object[] { tableName, ref.cachedData });
                            final Integer[] counts2 = this.removedTableStats.get(tableName);
                            counts2[1] = new Integer(counts2[1] + 1);
                            removedObjectIds.add(ref.getRefId());
                            ite.remove();
                            break;
                        }
                    }
                }
            }
            finally {
                this.lockUtil.releaseWriteLock();
            }
        }
        return removedObjectIds;
    }
    
    public void writeCacheSummary(final OutputStream os, final boolean includeTimeSummary, final boolean includeCachedData) throws IOException {
        synchronized (this.cachedTables) {
            if (os != null) {
                os.write(this.divs[1].getBytes());
                os.write((" Tables : (" + this.cachedTables.size() + ":").getBytes());
                int tblCount = 0;
                final Iterator ite = this.cachedTables.values().iterator();
                while (ite.hasNext()) {
                    os.write(("\n" + ++tblCount + " : " + ite.next()).getBytes());
                }
                os.write(this.divs[1].getBytes());
                os.write(("\nCache Size " + this.doCache.size()).getBytes());
                int count = 0;
                Iterator ite2 = this.doCache.values().iterator();
                while (ite2.hasNext()) {
                    final CachedDOReference ref = this.getCachedData(ite2.next());
                    if (ref == null) {
                        continue;
                    }
                    os.write(this.divs[0].getBytes());
                    os.write(("\n" + ++count + " : Num Used : " + ref.numTimesUsed).getBytes());
                    if (includeTimeSummary) {
                        long time = System.currentTimeMillis();
                        if (ref.selQuery != null) {
                            for (int i = 0; i < ref.numTimesUsed; ++i) {
                                ref.selQuery.toString();
                            }
                        }
                        final long timeTaken = System.currentTimeMillis() - time;
                        time = System.currentTimeMillis();
                        if (ref.cachedData instanceof DataObject) {
                            final DataObject cachedDo = (DataObject)ref.cachedData;
                            for (int j = 0; j < ref.numTimesUsed; ++j) {
                                cachedDo.clone();
                            }
                        }
                        final long cloneTimeTaken = System.currentTimeMillis() - time;
                        os.write((" : ToString Time : " + timeTaken + " : Clone Time : " + cloneTimeTaken).getBytes());
                    }
                    os.write((" : RefId : " + ref.refId).getBytes());
                }
                os.write(this.divs[1].getBytes());
                if (includeCachedData) {
                    os.write("\n------------------------- DataObject ------------------".getBytes());
                    count = 0;
                    ite2 = this.doCache.values().iterator();
                    while (ite2.hasNext()) {
                        final CachedDOReference ref = this.getCachedData(ite2.next());
                        if (ref == null) {
                            continue;
                        }
                        os.write((++count + " : CachedData : " + ref.cachedData).getBytes());
                    }
                }
            }
            else {
                DataObjectCache.logger.log(Level.SEVERE, " OutputStream is null ");
            }
        }
    }
    
    @Deprecated
    public String getCacheSummary(final boolean includeTimeSummary, final boolean includeCachedData) {
        final StringBuffer strBuf = new StringBuffer();
        strBuf.append(this.divs[1]);
        strBuf.append(" Tables : (" + this.cachedTables.size() + ":");
        int tblCount = 0;
        final Iterator ite = this.cachedTables.values().iterator();
        while (ite.hasNext()) {
            strBuf.append("\n" + ++tblCount + " : " + ite.next());
        }
        strBuf.append(this.divs[1]);
        strBuf.append("\nCache Size " + this.doCache.size());
        this.lockUtil.acquireReadLock();
        try {
            int count = 0;
            Iterator ite2 = this.doCache.values().iterator();
            while (ite2.hasNext()) {
                final CachedDOReference ref = this.getCachedData(ite2.next());
                if (ref == null) {
                    continue;
                }
                strBuf.append(this.divs[0]);
                strBuf.append("\n" + ++count + " : Num Used : " + ref.numTimesUsed);
                if (includeTimeSummary) {
                    long time = System.currentTimeMillis();
                    if (ref.selQuery != null) {
                        for (int i = 0; i < ref.numTimesUsed; ++i) {
                            ref.selQuery.toString();
                        }
                    }
                    final long timeTaken = System.currentTimeMillis() - time;
                    time = System.currentTimeMillis();
                    if (ref.cachedData instanceof DataObject) {
                        final DataObject cachedDo = (DataObject)ref.cachedData;
                        for (int j = 0; j < ref.numTimesUsed; ++j) {
                            cachedDo.clone();
                        }
                    }
                    final long cloneTimeTaken = System.currentTimeMillis() - time;
                    strBuf.append(" : ToString Time : " + timeTaken + " : Clone Time : " + cloneTimeTaken);
                }
                strBuf.append(" : RefId : " + ref.refId);
            }
            strBuf.append(this.divs[1]);
            if (includeCachedData) {
                strBuf.append("\n------------------------- DataObject ------------------");
                count = 0;
                ite2 = this.doCache.values().iterator();
                while (ite2.hasNext()) {
                    final CachedDOReference ref = this.getCachedData(ite2.next());
                    if (ref == null) {
                        continue;
                    }
                    strBuf.append(++count + " : CachedData : " + ref.cachedData);
                }
            }
            return strBuf.toString();
        }
        finally {
            this.lockUtil.releaseReadLock();
        }
    }
    
    public String getRemovedTableSummary() {
        final StringBuffer strBuf = new StringBuffer();
        strBuf.append("\n Table Name - NumTimes Removed - Num Data Objects Removed");
        for (final Map.Entry statsEntry : this.removedTableStats.entrySet()) {
            strBuf.append("\n" + statsEntry.getKey() + " : " + ((Integer[])statsEntry.getValue())[0] + " : " + ((Integer[])statsEntry.getValue())[1]);
        }
        return strBuf.toString();
    }
    
    private List<String> extractTableNamesFromTable(final List tablesList) {
        final ArrayList<String> namesList = new ArrayList<String>(tablesList.size());
        for (int i = 0, j = tablesList.size(); i < j; ++i) {
            namesList.add(tablesList.get(i).getTableName());
        }
        return namesList;
    }
    
    public static ArrayList getTableNames(final List dataObjectList) {
        final ArrayList<String> tableNames = new ArrayList<String>();
        try {
            for (int i = 0, j = dataObjectList.size(); i < j; ++i) {
                final DataObject dob = dataObjectList.get(i);
                final List curTableNames = dob.getTableNames();
                for (int k = 0, l = curTableNames.size(); k < l; ++k) {
                    final String tableName = curTableNames.get(k);
                    if (!tableNames.contains(tableName)) {
                        tableNames.add(tableName);
                    }
                }
            }
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return tableNames;
    }
    
    public void clearAllCachedData() {
        DataObjectCache.logger.log(Level.FINER, " clearing all cachedData ");
        boolean enableCachingStatus = false;
        if (this.cachingStatus) {
            this.cachingStatus = false;
            enableCachingStatus = true;
        }
        this.cachedTables.clear();
        this.removedTableStats.clear();
        this.initDOCacheMap();
        if (enableCachingStatus) {
            this.cachingStatus = true;
        }
    }
    
    private Object cloneIfNeeded(final Object cachedData) {
        return (!this.cloningStatus || !(cachedData instanceof DataObject)) ? cachedData : ((DataObject)cachedData).clone();
    }
    
    public CachedDOReference getCachedData(final Object cachedInfo) {
        if (cachedInfo == null) {
            return null;
        }
        if (cachedInfo instanceof SoftReference) {
            return ((SoftReference)cachedInfo).get();
        }
        return (CachedDOReference)cachedInfo;
    }
    
    public CachedDOReference[] getCachedReferences() {
        final Map temp = this.doCache;
        final ArrayList<CachedDOReference> refArray = new ArrayList<CachedDOReference>(temp.size());
        this.lockUtil.acquireReadLock();
        try {
            final Iterator iterator = temp.values().iterator();
            while (iterator.hasNext()) {
                final CachedDOReference ref = this.getCachedData(iterator.next());
                if (ref != null) {
                    refArray.add(ref);
                }
            }
        }
        finally {
            this.lockUtil.releaseReadLock();
        }
        return refArray.toArray(new CachedDOReference[refArray.size()]);
    }
    
    public void removeCachedReferences(final CachedDOReference[] refArray) {
        for (final CachedDOReference ref : refArray) {
            final Object key = ref.getRefId();
            this.doCache.remove(key);
        }
    }
    
    private void removeFromCache(final CacheComparator criteria) {
        DataObjectCache.logger.log(Level.FINER, " clearing all objects matching criteria matched through CacheComparator");
        try {
            final CachedDOReference[] refArray = this.getCachedReferences();
            final List<CachedDOReference> resultantVector = new ArrayList<CachedDOReference>();
            for (final CachedDOReference reference : refArray) {
                final Object value = reference.getCachedData();
                if (value instanceof CacheComparator) {
                    final CacheComparator configuration = (CacheComparator)value;
                    final boolean result = configuration.compare(criteria);
                    if (result) {
                        resultantVector.add(reference);
                    }
                }
            }
            if (resultantVector.size() > 0) {
                final CachedDOReference[] resultantArray = resultantVector.toArray(new CachedDOReference[resultantVector.size()]);
                this.removeCachedReferences(resultantArray);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void cleanUp() {
        if (this.useSoftReference) {
            final Iterator ite = this.doCache.entrySet().iterator();
            while (ite.hasNext()) {
                final Map.Entry entry = ite.next();
                final CachedDOReference ref = this.getCachedData(entry.getValue());
                if (ref == null) {
                    DataObjectCache.logger.log(Level.FINER, "The reference for {0} has been cleaned up", entry.getKey());
                    ite.remove();
                    this.cacheStats.incrEvictionCount();
                }
            }
        }
    }
    
    static {
        DataObjectCache.logger = Logger.getLogger(DataObjectCache.class.getName());
        (DataObjectCache.tablesNotToBeChecked = new HashSet<String>()).add("ConfigurationRecord");
        DataObjectCache.tablesNotToBeChecked.add("ModuleOwnedCR");
        DataObjectCache.tablesNotToBeChecked.add("DrivenMICR");
        DataObjectCache.tablesNotToBeChecked.add("ConfigurationRecord_PIDX");
    }
    
    private class LRUMap<K, V> extends LinkedHashMap<K, V>
    {
        public LRUMap(final int initialCapacity) {
            super(initialCapacity, 0.75f, true);
        }
        
        @Override
        protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
            final boolean isRemove = this.size() > DataObjectCache.this.maxSize;
            if (isRemove) {
                DataObjectCache.this.cacheStats.incrEvictionCount();
            }
            return isRemove;
        }
    }
    
    public class CachedDOReference
    {
        private Object refId;
        private Object cachedData;
        private SelectQuery selQuery;
        private int numTimesUsed;
        private List<String> tablesList;
        
        CachedDOReference(final Object refIdArg, final Object cachedDataArg, final SelectQuery selQueryArg, final List<String> tablesListArg) {
            this.numTimesUsed = 1;
            this.tablesList = null;
            this.refId = refIdArg;
            this.cachedData = cachedDataArg;
            this.selQuery = selQueryArg;
            this.tablesList = tablesListArg;
            if (this.tablesList == null && this.selQuery != null) {
                this.tablesList = DataObjectCache.this.extractTableNamesFromTable(this.selQuery.getTableList());
            }
            if (this.tablesList == null) {
                this.tablesList = new ArrayList<String>();
            }
        }
        
        public Object getCachedData() {
            return this.cachedData;
        }
        
        public List getTablesList() {
            return this.tablesList;
        }
        
        public Object getRefId() {
            return this.refId;
        }
    }
    
    public interface CacheComparator
    {
        boolean compare(final CacheComparator p0);
    }
    
    private interface LockUtil
    {
        void acquireReadLock();
        
        void releaseReadLock();
        
        void acquireWriteLock();
        
        void releaseWriteLock();
    }
}
