package com.adventnet.persistence.cache;

import com.adventnet.mfw.ConsoleOut;
import com.adventnet.cp.MultiDSUtil;
import com.adventnet.db.migration.util.DBMigrationUtil;
import com.adventnet.db.persistence.metadata.MetaDataPreChangeEvent;
import com.adventnet.db.persistence.metadata.MetaDataChangeEvent;
import com.adventnet.persistence.OperationInfo;
import java.util.TimerTask;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Date;
import com.adventnet.db.persistence.metadata.MetaDataChangeListener;
import com.adventnet.persistence.DataAccess;
import com.adventnet.mfw.message.MessageFilter;
import com.adventnet.mfw.message.MessageListener;
import com.adventnet.mfw.message.Messenger;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import java.util.Properties;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.Timer;
import java.util.logging.Logger;
import com.adventnet.mfw.service.Service;

public class CacheService implements Service
{
    private static Logger logger;
    private boolean listen;
    private String dataModelTopicName;
    private CacheRepository cacheRepository;
    private String cacheRepositoryClassName;
    private Timer statsMonitor;
    private long statsMonitorPeriod;
    private static Logger statsLogger;
    
    public CacheService() {
        this.listen = true;
        this.dataModelTopicName = "DataModelTopic";
        this.cacheRepositoryClassName = "com.adventnet.persistence.cache.CacheRepositoryImpl";
        this.statsMonitor = null;
        this.statsMonitorPeriod = -1L;
    }
    
    public void create(final DataObject dobj) throws Exception {
        CacheService.logger.log(Level.FINER, "createService called");
        this.cacheRepositoryClassName = PersistenceInitializer.getConfigurationValue("CacheRepository");
        if (this.cacheRepositoryClassName == null) {
            this.cacheRepositoryClassName = "com.adventnet.persistence.cache.CacheRepositoryImpl";
        }
        CacheService.logger.info("CacheRepository " + this.cacheRepositoryClassName);
        this.cacheRepository = (CacheRepository)this.createNewInstance(this.cacheRepositoryClassName);
        final Properties properties = new Properties();
        final Iterator<Row> propRows = dobj.getRows("ServiceProperties");
        while (propRows.hasNext()) {
            final Row propRow = propRows.next();
            properties.put(propRow.get(3), propRow.get(4));
        }
        final boolean useSoftReference = Boolean.parseBoolean(properties.getProperty("SoftRef", "true"));
        final int maxSize = Integer.parseInt(properties.getProperty("MaxSize", "-1"));
        this.cacheRepository.initialize(maxSize, useSoftReference);
        this.cacheRepository.setCloningStatus(false);
        this.cacheRepository.setCachingStatus(true);
        CacheManager.setCacheRepository(this.cacheRepository);
        this.statsMonitorPeriod = Long.parseLong(properties.getProperty("MonitorPeriod", "-1"));
        this.startStatsMonitor();
    }
    
    public void start() throws Exception {
        CacheService.logger.log(Level.FINER, "startService called");
        Messenger.subscribe(this.dataModelTopicName, (MessageListener)new DataModelNotificationListener(), true, (MessageFilter)null);
        Messenger.subscribe("startupNotification", (MessageListener)new MultiWriteListener(), true, (MessageFilter)null);
        DataAccess.registerForMetaDataChanges(new MetaDataListener());
        this.listen = true;
        CacheService.logger.log(Level.INFO, "Listening for Deployment and DataModel notifications");
    }
    
    public void stop() throws Exception {
        CacheService.logger.log(Level.FINER, "stopService called");
        this.listen = false;
    }
    
    public boolean isListeningForDataModelNotification() {
        return this.listen;
    }
    
    public void setListenForDataModelNotification(final boolean listenFlag) {
        this.listen = listenFlag;
    }
    
    public String getDataModelTopicName() {
        return this.dataModelTopicName;
    }
    
    public void setDataModelTopicName(final String dataModelTopicNameArg) {
        this.dataModelTopicName = dataModelTopicNameArg;
    }
    
    public void clearCache() throws Exception {
        this.cacheRepository.clearCachedData();
    }
    
    public String getCacheSummary(final boolean includeTime, final boolean includeCachedData) {
        return this.cacheRepository.getCacheSummary(includeTime, includeCachedData);
    }
    
    public String listRemovedTableSummary() {
        return this.cacheRepository.getRemovedTableSummary();
    }
    
    public void setCacheingStatus(final boolean status) {
        if (this.cacheRepository != null) {
            this.cacheRepository.setCachingStatus(status);
        }
    }
    
    public boolean getCacheingStatus() {
        return this.cacheRepository.getCachingStatus();
    }
    
    public void setCloneingStatus(final boolean status) {
        if (this.cacheRepository != null) {
            this.cacheRepository.setCloningStatus(status);
        }
    }
    
    public boolean getCloneingStatus() {
        return this.cacheRepository.getCloningStatus();
    }
    
    public void setUseSoftReference(final boolean useSoftReferenceArg) {
        if (this.cacheRepository != null) {
            this.cacheRepository.setUseSoftReference(useSoftReferenceArg);
        }
    }
    
    public boolean isUseSoftReference() {
        return this.cacheRepository.isUseSoftReference();
    }
    
    public String printCacheSummaryToFile(final boolean includeTime, final boolean includeCachedData) throws Exception {
        final String data = this.cacheRepository.getCacheSummary(includeTime, includeCachedData);
        final File datFile = new File("SummaryFile" + new Date() + ".log");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileOutputStream(datFile));
            writer.println(data);
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
        return datFile.getAbsolutePath();
    }
    
    public void destroy() throws Exception {
        CacheService.logger.log(Level.FINER, "destroyService called");
    }
    
    public void setCacheRepository(final String cacheRepositoryClassName) {
        this.cacheRepositoryClassName = cacheRepositoryClassName;
    }
    
    private Object createNewInstance(final String className) {
        Object obj = null;
        try {
            final Class c = Thread.currentThread().getContextClassLoader().loadClass(className);
            obj = c.newInstance();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
    
    public String getCacheRepository() {
        return this.cacheRepositoryClassName;
    }
    
    private void startStatsMonitor() {
        if (this.statsMonitorPeriod == -1L) {
            return;
        }
        (this.statsMonitor = new Timer()).schedule(new StatsMonitorTask(this.cacheRepository), this.statsMonitorPeriod, this.statsMonitorPeriod);
    }
    
    private void stopStatsMonitor() {
        if (this.statsMonitor != null) {
            this.statsMonitor.cancel();
        }
    }
    
    public long getStatsMonitorPeriod() {
        return this.statsMonitorPeriod;
    }
    
    public void setStatsMonitorPeriod(final long period) {
        this.stopStatsMonitor();
        this.statsMonitorPeriod = period;
        this.startStatsMonitor();
    }
    
    static {
        CacheService.logger = Logger.getLogger(CacheService.class.getName());
        CacheService.statsLogger = Logger.getLogger("mickey.cache.monitor");
    }
    
    public class DataModelNotificationListener implements MessageListener
    {
        public void onMessage(final Object msgArg) {
            if (!CacheService.this.listen) {
                return;
            }
            try {
                if (msgArg instanceof OperationInfo) {
                    final OperationInfo notifiedDO = (OperationInfo)msgArg;
                    CacheService.this.cacheRepository.changeCacheForChangeInTables(notifiedDO);
                }
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public class MetaDataListener implements MetaDataChangeListener
    {
        @Override
        public void metaDataChanged(final MetaDataChangeEvent mdcEvent) {
            try {
                CacheService.this.cacheRepository.changeCacheForChangeInTables(mdcEvent);
            }
            catch (final Exception ex) {
                CacheService.logger.log(Level.SEVERE, "Exception occured while clearing cache", ex);
            }
        }
        
        @Override
        public void preMetaDataChange(final MetaDataPreChangeEvent preMDCEvent) {
        }
    }
    
    public static class StatsMonitorTask extends TimerTask
    {
        CacheRepository cacheRep;
        
        public StatsMonitorTask(final CacheRepository cacheRep) {
            this.cacheRep = null;
            this.cacheRep = cacheRep;
        }
        
        @Override
        public void run() {
            CacheService.statsLogger.log(Level.INFO, "{0}| {1}| {2}| {3}| {4} | {5}", new Object[] { String.valueOf(this.cacheRep.getCount()), String.valueOf(this.cacheRep.getMissCount()), String.valueOf(this.cacheRep.putCount()), String.valueOf(this.cacheRep.putMissCount()), String.valueOf(this.cacheRep.currentSize()), String.valueOf(this.cacheRep.evictionCount()) });
        }
    }
    
    public class MultiWriteListener implements MessageListener
    {
        public void onMessage(final Object msgArg) {
            if (PersistenceInitializer.getConfigurationValue("EnableMWSR") != null && PersistenceInitializer.getConfigurationValue("EnableMWSR").equals("true")) {
                if (Boolean.getBoolean("development.mode")) {
                    try {
                        DBMigrationUtil.dbMigrationForMWSR();
                    }
                    catch (final Exception e) {
                        CacheService.logger.log(Level.SEVERE, "Exception occured while migrating tables", e);
                        System.exit(-1);
                    }
                    MultiDSUtil.enableMWSR();
                }
                else {
                    CacheService.logger.log(Level.SEVERE, "Development mode should be set while using MWSR");
                    ConsoleOut.println("Development mode should be set while using MWSR");
                    System.exit(1);
                }
            }
        }
    }
}
