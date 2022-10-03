package com.adventnet.taskengine.internal;

import java.util.Hashtable;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.taskengine.backup.DbBackupTask;
import com.adventnet.mfw.message.MessageFilter;
import com.adventnet.mfw.message.Messenger;
import com.adventnet.db.adapter.postgres.PostgresBackupHandler;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.TimerTask;
import com.adventnet.mfw.message.MessageListener;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ArrayBlockingQueue;
import com.adventnet.taskengine.util.PersistenceUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;
import java.util.Timer;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.Map;
import com.adventnet.mfw.service.Service;

public class TaskEngineService implements Service
{
    private static RefreshController refreshController;
    public static UpdateController updateController;
    private static Map<String, ThreadPoolExecutor> threadPoolMap;
    private static Timer timer;
    private static Logger out;
    private static long updatePeriod;
    static Map<String, BlockingQueue<Runnable>> blockingQueueMap;
    BlockingQueue<Runnable> blockingQueue;
    public ThreadPoolExecutor executor;
    
    public TaskEngineService() {
        this.blockingQueue = null;
        this.executor = null;
    }
    
    public void create(final DataObject serviceDO) throws Exception {
        if (Boolean.getBoolean("ignore.scheduler")) {
            TaskEngineService.out.log(Level.SEVERE, "Scheduler has not been initialized since the System Property ignore.scheduler is set to true");
            return;
        }
        TaskEngineService.out.log(Level.FINE, "3.Entering TaskEngineService CreateService.");
        final Iterator properties = serviceDO.get("ServiceProperties", "PROPERTY");
        final Iterator values = serviceDO.get("ServiceProperties", "VALUE");
        final Properties propertyMap = new Properties();
        while (properties.hasNext() && values.hasNext()) {
            propertyMap.setProperty(properties.next().toString(), values.next().toString());
        }
        final String updatePeriodStr = ((Hashtable<K, String>)propertyMap).get("UpdatePeriod");
        if (updatePeriodStr != null && !updatePeriodStr.equals("")) {
            TaskEngineService.updatePeriod = Long.parseLong(updatePeriodStr);
        }
    }
    
    private void initializeThreadPoolMap() throws Exception {
        final Map<String, ThreadPoolExecutor> map = new HashMap<String, ThreadPoolExecutor>();
        final List<String> poolNames = PersistenceUtil.getThreadPoolNames();
        for (final String poolName : poolNames) {
            final Integer size = PersistenceUtil.getThreadPoolSize(poolName);
            if (size <= 0) {
                throw new IllegalArgumentException("PoolName :: [" + poolName + "] PoolSize :: [" + size + "]. The size of a thread pool should be more than 0");
            }
            this.blockingQueue = new ArrayBlockingQueue<Runnable>(size);
            final TEThreadFactory threadFacInstance = new TEThreadFactory(poolName);
            final int coreSize = (size == 1) ? 0 : ((size == 2) ? 1 : 2);
            this.executor = new ThreadPoolExecutor(coreSize, size, 60000L, TimeUnit.MILLISECONDS, this.blockingQueue, threadFacInstance);
            TaskEngineService.out.log(Level.INFO, "Executor created for PoolName : {0}, CoreSize : {1}, MaxSize : {2}", new Object[] { poolName, coreSize, size });
            map.put(poolName, this.executor);
            TaskEngineService.blockingQueueMap.put(poolName, this.blockingQueue);
        }
        TaskEngineService.threadPoolMap = map;
    }
    
    public static ThreadPoolExecutor getExecutor(final String poolName) {
        if (TaskEngineService.threadPoolMap.containsKey(poolName)) {
            return TaskEngineService.threadPoolMap.get(poolName);
        }
        TaskEngineService.out.log(Level.INFO, "No such thread pool exists : {0} ", poolName);
        return null;
    }
    
    public static void setMaxPoolSize(final String poolName, final int newSize) {
        if (newSize > 0 && newSize > getExecutor(poolName).getCorePoolSize()) {
            getExecutor(poolName).setMaximumPoolSize(newSize);
            return;
        }
        TaskEngineService.out.log(Level.INFO, "MaxPoolSize should be greater than corePoolSize :: CorePoolSize : {0} New MaxPoolSize : {1}", new Object[] { getExecutor(poolName).getCorePoolSize(), newSize });
    }
    
    public void start() throws Exception {
        if (System.getProperty("ignore.scheduler", "false").equalsIgnoreCase("true")) {
            TaskEngineService.out.log(Level.SEVERE, "Scheduler has not been started since the System Property ignore.scheduler is set to true");
            return;
        }
        this.initializeThreadPoolMap();
        TaskEngineService.refreshController = this.createRefreshController();
        TaskEngineService.updateController = this.createUpdateController();
        final MessageListener listener = (MessageListener)new MessageListener() {
            public void onMessage(final Object object) {
                final long delay = 10000L;
                TaskEngineService.timer.scheduleAtFixedRate(TaskEngineService.refreshController, delay, 600000L);
                if (!System.getProperty("updatecontroller.disable", "false").equalsIgnoreCase("true")) {
                    TaskEngineService.timer.scheduleAtFixedRate(TaskEngineService.updateController, delay, TaskEngineService.updatePeriod);
                }
                TaskEngineService.out.info("scheduled refreshcontoller and updateController");
            }
        };
        if (PersistenceInitializer.getConfigurationValue("DBName").equals("postgres")) {
            Messenger.subscribe("startupNotification", (MessageListener)new PostgresBackupHandler(), true, (MessageFilter)null);
        }
        Messenger.subscribe("startupNotification", (MessageListener)new DbBackupTask(), true, (MessageFilter)null);
        Messenger.subscribe("startupNotification", listener, true, (MessageFilter)null);
        final Criteria criteria = new Criteria(Column.getColumn("Schedule_Audit", "TASK_EXECUTION_STATUS"), (Object)"PROCESSING", 12, false);
        final DataObject auditDO = DataAccess.get("Schedule_Audit", criteria);
        final Iterator iterator = auditDO.getRows("Schedule_Audit");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String status = (String)row.get(9);
            row.set(9, (Object)status.replaceAll("PROCESSING", "FAILURE"));
            auditDO.updateRow(row);
        }
        DataAccess.update(auditDO);
    }
    
    private RefreshController createRefreshController() throws Exception {
        RefreshController refreshController = null;
        final String className = System.getProperty("RefreshController");
        if (className != null) {
            final Class clazz = PersistenceUtil.getClass(className);
            refreshController = clazz.newInstance();
        }
        else {
            refreshController = new RefreshController();
        }
        return refreshController;
    }
    
    private UpdateController createUpdateController() throws Exception {
        UpdateController updateController = null;
        final String className = System.getProperty("UpdateController");
        if (className != null) {
            final Class clazz = PersistenceUtil.getClass(className);
            updateController = clazz.newInstance();
        }
        else {
            updateController = new UpdateController();
        }
        return updateController;
    }
    
    public static void setBatchUpdatePeriod(final long period) throws Exception {
        TaskEngineService.updatePeriod = period;
    }
    
    public void stop() throws Exception {
        if (System.getProperty("ignore.scheduler", "false").equalsIgnoreCase("true")) {
            TaskEngineService.out.log(Level.SEVERE, "Scheduler has not been stopped since the System Property ignore.scheduler is set to true");
            return;
        }
        this.shutdownNow(TaskEngineService.threadPoolMap);
        TaskEngineService.updateController.batchUpdate();
        TaskEngineService.timer.cancel();
    }
    
    private void shutdownNow(final Map<String, ThreadPoolExecutor> map) throws Exception {
        for (final String key : map.keySet()) {
            map.get(key).shutdownNow();
        }
    }
    
    public void destroy() throws Exception {
    }
    
    public static String getPoolStatus(final String poolName) throws Exception {
        return " PoolName - " + poolName + " :: " + getExecutor(poolName).toString();
    }
    
    static {
        TaskEngineService.timer = new Timer("FetchAndUpdateTimer");
        TaskEngineService.out = Logger.getLogger(TaskEngineService.class.getName());
        TaskEngineService.updatePeriod = 120000L;
        TaskEngineService.blockingQueueMap = new HashMap<String, BlockingQueue<Runnable>>();
    }
    
    class TEThreadFactory implements ThreadFactory
    {
        String poolName;
        int count;
        
        public TEThreadFactory(final String poolName) {
            this.poolName = "";
            this.count = 0;
            this.poolName = poolName;
        }
        
        @Override
        public Thread newThread(final Runnable r) {
            return new Thread(r, this.poolName + "_" + this.count++);
        }
    }
}
