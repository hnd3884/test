package com.me.devicemanagement.onpremise.server.scheduler;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Persistence;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import javax.xml.transform.Transformer;
import org.w3c.dom.Document;
import java.io.InputStream;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.nio.charset.Charset;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import com.me.devicemanagement.framework.utils.XMLUtils;
import com.adventnet.persistence.DataAccess;
import java.io.StringWriter;
import com.adventnet.persistence.DataObject;
import java.util.LinkedHashMap;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;

public class SchedulerTuningUtil
{
    private static SchedulerTuningUtil schedulerTuningUtilHandler;
    public static Logger scheduleUtillogger;
    public static final String THREAD_POOL_LMT_FILE;
    public static final String THREAD_POOL_FILENAME;
    public static final String THREAD_POOL_USER_FILENAME;
    public static final String THREAD_POOL_OLD_LMT = "threadpoolOldLMT";
    public static final String THREAD_POOL_USER_OLD_LMT = "threadpoolUsersOldLMT";
    public static final String THREAD_POOL_CURRENT_LMT = "threadpoolCurrentLMT";
    public static final String THREAD_POOL_USER_CURRENT_LMT = "threadpoolUsersCurrentLMT";
    public static final String THREAD_POOL_NODE_NAME = "ThreadPool";
    public static final String THREAD_POOL_NAME = "THREADPOOL_NAME";
    public static final String THREAD_POOL_SIZE = "THREADPOOL_SIZE";
    
    public static SchedulerTuningUtil getInstance() {
        if (SchedulerTuningUtil.schedulerTuningUtilHandler == null) {
            SchedulerTuningUtil.schedulerTuningUtilHandler = new SchedulerTuningUtil();
        }
        return SchedulerTuningUtil.schedulerTuningUtilHandler;
    }
    
    public boolean isThreadPoolFilesChanged() {
        boolean isFilechanged = false;
        final Properties threadPoolLatestModified = new Properties();
        try {
            final Properties threadPoolFileoldLMTProps = this.getThreadPoolFilesOldLMTProperty();
            final Properties threadPoolFileCurrentLMTProps = this.getThreadPoolFilesCurrentLMTProperty();
            if (threadPoolFileoldLMTProps != null && threadPoolFileCurrentLMTProps != null) {
                final Long threadPoolOldLMT = Long.valueOf(threadPoolFileoldLMTProps.getProperty("threadpoolOldLMT", "0"));
                final Long threadPoolUsersFileOldLMT = Long.valueOf(threadPoolFileoldLMTProps.getProperty("threadpoolUsersOldLMT", "0"));
                final Long threadPoolCurrentLMT = Long.valueOf(threadPoolFileCurrentLMTProps.getProperty("threadpoolCurrentLMT", "0"));
                final Long threadPoolUsersFileCurrentLMT = Long.valueOf(threadPoolFileCurrentLMTProps.getProperty("threadpoolUsersCurrentLMT", "0"));
                SchedulerTuningUtil.scheduleUtillogger.log(Level.INFO, "Old modified time of " + SchedulerTuningUtil.THREAD_POOL_FILENAME + "  from file system: " + threadPoolOldLMT);
                SchedulerTuningUtil.scheduleUtillogger.log(Level.INFO, "Current modified time of " + SchedulerTuningUtil.THREAD_POOL_FILENAME + "  from file system: " + threadPoolCurrentLMT);
                SchedulerTuningUtil.scheduleUtillogger.log(Level.INFO, "Old modified time of " + SchedulerTuningUtil.THREAD_POOL_USER_FILENAME + "  from file system: " + threadPoolUsersFileOldLMT);
                SchedulerTuningUtil.scheduleUtillogger.log(Level.INFO, "Current modified time of " + SchedulerTuningUtil.THREAD_POOL_USER_FILENAME + "  from file system: " + threadPoolUsersFileCurrentLMT);
                if (threadPoolOldLMT > 0L || threadPoolUsersFileOldLMT > 0L) {
                    if (threadPoolCurrentLMT > threadPoolOldLMT || threadPoolUsersFileCurrentLMT > threadPoolUsersFileOldLMT || threadPoolUsersFileCurrentLMT == -1L || threadPoolCurrentLMT == -1L) {
                        isFilechanged = true;
                        final Long threadPoolModifiedTime = (threadPoolCurrentLMT > threadPoolOldLMT) ? threadPoolCurrentLMT : threadPoolOldLMT;
                        final Long threadPoolUsersModifiedTime = (threadPoolUsersFileCurrentLMT > threadPoolUsersFileOldLMT) ? threadPoolUsersFileCurrentLMT : threadPoolUsersFileOldLMT;
                        SchedulerTuningUtil.scheduleUtillogger.log(Level.INFO, "ThreadPool.xml ModifiedTime: " + threadPoolModifiedTime);
                        SchedulerTuningUtil.scheduleUtillogger.log(Level.INFO, "ThreadPoolUsers.xml ModifiedTime: " + threadPoolUsersModifiedTime);
                        threadPoolLatestModified.setProperty("threadpoolOldLMT", String.valueOf(threadPoolModifiedTime));
                        threadPoolLatestModified.setProperty("threadpoolUsersOldLMT", String.valueOf(threadPoolUsersModifiedTime));
                        this.saveThreadPoolFileLastModifiedTimeProps(threadPoolLatestModified);
                    }
                    else {
                        SchedulerTuningUtil.scheduleUtillogger.log(Level.INFO, "ThreadPool.xml and ThreadPoolUsers.xml, Last modified time is not greater than the Previous Modified Time, so no Changes in the files");
                    }
                }
                else {
                    isFilechanged = true;
                    SchedulerTuningUtil.scheduleUtillogger.log(Level.INFO, "ThreadPool.xml and ThreadPoolUsers.xml, Previous modified time is null. This might be the first server startup or first startup after restore...");
                    threadPoolLatestModified.setProperty("threadpoolOldLMT", String.valueOf(threadPoolCurrentLMT));
                    threadPoolLatestModified.setProperty("threadpoolUsersOldLMT", String.valueOf(threadPoolUsersFileCurrentLMT));
                    this.saveThreadPoolFileLastModifiedTimeProps(threadPoolLatestModified);
                }
            }
        }
        catch (final Exception ex) {
            SchedulerTuningUtil.scheduleUtillogger.log(Level.WARNING, "Exception occurred while checking the " + SchedulerTuningUtil.THREAD_POOL_FILENAME + " and  " + SchedulerTuningUtil.THREAD_POOL_USER_FILENAME + " is modified or not.", ex);
        }
        return isFilechanged;
    }
    
    public Properties getThreadPoolFilesOldLMTProperty() {
        Properties threadPoolLMTProps = new Properties();
        try {
            final String fname = System.getProperty("server.home") + File.separator + SchedulerTuningUtil.THREAD_POOL_LMT_FILE;
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(fname)) {
                threadPoolLMTProps = StartupUtil.getProperties(fname);
            }
            else {
                SchedulerTuningUtil.scheduleUtillogger.log(Level.INFO, "sc.modtime File does not exist ");
            }
        }
        catch (final Exception ex) {
            SchedulerTuningUtil.scheduleUtillogger.log(Level.WARNING, "Caught exception while retrieving threadpool.xml & threadPoolUsers.conf file previous modified time from ts.modtime", ex);
        }
        return threadPoolLMTProps;
    }
    
    public Properties getThreadPoolFilesCurrentLMTProperty() {
        long threadPoollastModTime = -1L;
        long threadPoolUserlastModTime = -1L;
        String threadPoolsFileName = null;
        String threadPoolsUserFileName = null;
        final Properties threadPoolCurrentLMTProps = new Properties();
        try {
            final String serverHome = System.getProperty("server.home");
            threadPoolsFileName = serverHome + File.separator + SchedulerTuningUtil.THREAD_POOL_FILENAME;
            threadPoolsUserFileName = serverHome + File.separator + SchedulerTuningUtil.THREAD_POOL_USER_FILENAME;
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(threadPoolsFileName)) {
                threadPoollastModTime = ApiFactoryProvider.getFileAccessAPI().lastModified(threadPoolsFileName);
            }
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(threadPoolsUserFileName)) {
                threadPoolUserlastModTime = ApiFactoryProvider.getFileAccessAPI().lastModified(threadPoolsUserFileName);
            }
            threadPoolCurrentLMTProps.setProperty("threadpoolCurrentLMT", String.valueOf(threadPoollastModTime));
            threadPoolCurrentLMTProps.setProperty("threadpoolUsersCurrentLMT", String.valueOf(threadPoolUserlastModTime));
        }
        catch (final Exception ex) {
            SchedulerTuningUtil.scheduleUtillogger.log(Level.WARNING, "Caught error while retrieviing last modified time of file: " + threadPoolsFileName + " and  " + threadPoolsUserFileName, ex);
        }
        return threadPoolCurrentLMTProps;
    }
    
    public void saveThreadPoolFileLastModifiedTimeProps(final Properties threadPoolFileCurrentLMTProps) {
        try {
            final String fname = System.getProperty("server.home") + File.separator + SchedulerTuningUtil.THREAD_POOL_LMT_FILE;
            if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(fname)) {
                SchedulerTuningUtil.scheduleUtillogger.log(Level.INFO, "sc.modtime File does not exist, so a new file is created: " + fname);
                ApiFactoryProvider.getFileAccessAPI().createNewFile(fname);
            }
            StartupUtil.storeProperties(threadPoolFileCurrentLMTProps, fname);
        }
        catch (final Exception ex) {
            SchedulerTuningUtil.scheduleUtillogger.log(Level.WARNING, "Caught exception while saving sc.modtime last modified time.", ex);
        }
    }
    
    public LinkedHashMap getThreadPoolBaseHMap() {
        LinkedHashMap threadPoolHMap = new LinkedHashMap();
        try {
            final DataObject threadPoolBaseDO = this.getThreadPoolBaseDO();
            threadPoolHMap = this.getThreadPoolHMapFromDO(threadPoolBaseDO);
        }
        catch (final Exception ex) {
            SchedulerTuningUtil.scheduleUtillogger.log(Level.WARNING, "Caught exception while getting Properties from threadpools.xml ", ex);
        }
        return threadPoolHMap;
    }
    
    public DataObject getThreadPoolBaseDO() {
        final StringWriter stringWr = new StringWriter();
        try {
            final String threadPoolfilename = System.getProperty("server.home") + File.separator + SchedulerTuningUtil.THREAD_POOL_FILENAME;
            DataObject threadPoolDO = DataAccess.constructDataObject();
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(threadPoolfilename)) {
                final InputStream inputStream = ApiFactoryProvider.getFileAccessAPI().readFile(threadPoolfilename);
                final Document doc = XMLUtils.getDocumentBuilderInstance().parse(inputStream);
                final Transformer serializer = XMLUtils.getTransformerInstance();
                serializer.transform(new DOMSource(doc), new StreamResult(stringWr));
                final String threadPoolstrXML = stringWr.toString();
                final byte[] threadPoolsXMLbyteData = threadPoolstrXML.getBytes(Charset.forName("UTF-8"));
                threadPoolDO = SyMUtil.convertXMLToDO(threadPoolsXMLbyteData);
                SchedulerTuningUtil.scheduleUtillogger.log(Level.INFO, " ThreadPool.xml File Data Object" + threadPoolDO);
            }
            else {
                SchedulerTuningUtil.scheduleUtillogger.log(Level.INFO, SchedulerTuningUtil.THREAD_POOL_FILENAME + " ,does not exist");
            }
            return threadPoolDO;
        }
        catch (final Exception ex) {
            SchedulerTuningUtil.scheduleUtillogger.log(Level.WARNING, "Caught exception while getting Properties from threadpools.xml ", ex);
            return null;
        }
    }
    
    public LinkedHashMap getThreadPoolHMapFromDO(final DataObject threadPoolDo) {
        final LinkedHashMap threadPoolPropsFromDO = new LinkedHashMap();
        try {
            if (!threadPoolDo.isEmpty()) {
                final Iterator rows = threadPoolDo.getRows("ThreadPool");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    final String threadPoolName = (String)row.get("THREADPOOL_NAME");
                    final String threadPoolSize = String.valueOf(row.get("THREADPOOL_SIZE"));
                    threadPoolPropsFromDO.put(threadPoolName, threadPoolSize);
                    SchedulerTuningUtil.scheduleUtillogger.log(Level.INFO, threadPoolName + " : " + threadPoolSize);
                }
            }
            else {
                SchedulerTuningUtil.scheduleUtillogger.log(Level.INFO, "DataObject of Threadpools.xml is Empty ");
            }
        }
        catch (final Exception ex) {
            SchedulerTuningUtil.scheduleUtillogger.log(Level.WARNING, "Caught exception while getting Properties from threadpools.xml after converting it to DataObject ", ex);
        }
        return threadPoolPropsFromDO;
    }
    
    public Properties getThreadPoolUserProps() {
        Properties threadPoolUserProps = new Properties();
        try {
            final String threadPoolUserfilename = System.getProperty("server.home") + File.separator + SchedulerTuningUtil.THREAD_POOL_USER_FILENAME;
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(threadPoolUserfilename)) {
                threadPoolUserProps = StartupUtil.getProperties(threadPoolUserfilename);
            }
            else {
                SchedulerTuningUtil.scheduleUtillogger.log(Level.INFO, threadPoolUserfilename + " ,does not exist ");
            }
        }
        catch (final Exception ex) {
            SchedulerTuningUtil.scheduleUtillogger.log(Level.WARNING, "Caught exception while getting Properties from threadpools.xml after converting it to DataObject ", ex);
        }
        return threadPoolUserProps;
    }
    
    public Boolean updateThreadPoolTable(final LinkedHashMap threadPoolNewMap) {
        Boolean dbRowUpdated = false;
        try {
            final Persistence persistence = SyMUtil.getPersistence();
            DataObject threadPoolDataDO = SyMUtil.getPersistence().constructDataObject();
            final SelectQuery selQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ThreadPool"));
            selQuery.addSelectColumn(Column.getColumn("ThreadPool", "*"));
            selQuery.setCriteria((Criteria)null);
            threadPoolDataDO = persistence.get(selQuery);
            if (!threadPoolDataDO.isEmpty()) {
                final Iterator rows = threadPoolDataDO.getRows("ThreadPool");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    final String threadPoolName = (String)row.get("THREADPOOL_NAME");
                    final Integer threadPoolDBSize = Integer.valueOf(String.valueOf(row.get("THREADPOOL_SIZE")));
                    final Integer threadPoolNewSize = Integer.valueOf(String.valueOf(threadPoolNewMap.get(threadPoolName)).trim());
                    SchedulerTuningUtil.scheduleUtillogger.log(Level.INFO, threadPoolName + " : (Database Size) " + threadPoolDBSize + " and (ThreadPools-User.conf Size) " + threadPoolNewSize);
                    if (threadPoolNewSize.compareTo(threadPoolDBSize) != 0) {
                        row.set("THREADPOOL_SIZE", (Object)threadPoolNewSize);
                        threadPoolDataDO.updateRow(row);
                        dbRowUpdated = true;
                    }
                }
                if (dbRowUpdated) {
                    persistence.update(threadPoolDataDO);
                }
            }
        }
        catch (final Exception ex) {
            SchedulerTuningUtil.scheduleUtillogger.log(Level.INFO, "updateThreadPoolTable method to update ThreadPool Data object is failed due to", ex);
            return false;
        }
        return dbRowUpdated;
    }
    
    public int getThreadPoolSize(final String threadPoolName) {
        int threadPoolSize = 0;
        try {
            final Column threadPoolNameCol = Column.getColumn("ThreadPool", "THREADPOOL_NAME");
            final Criteria criteria = new Criteria(threadPoolNameCol, (Object)threadPoolName, 0);
            final DataObject threadPoolDo = SyMUtil.getPersistence().get("ThreadPool", criteria);
            Row threadPoolRow = null;
            if (!threadPoolDo.isEmpty()) {
                threadPoolRow = threadPoolDo.getRow("ThreadPool");
                threadPoolSize = (int)threadPoolRow.get("THREADPOOL_SIZE");
            }
        }
        catch (final Exception ex) {
            SchedulerTuningUtil.scheduleUtillogger.log(Level.SEVERE, "Exception while getting the thread pool size", ex);
        }
        return threadPoolSize;
    }
    
    static {
        SchedulerTuningUtil.schedulerTuningUtilHandler = null;
        SchedulerTuningUtil.scheduleUtillogger = Logger.getLogger(SchedulerTuningUtil.class.getName());
        THREAD_POOL_LMT_FILE = "conf" + File.separator + "sc.modtime";
        THREAD_POOL_FILENAME = "conf" + File.separator + "TaskEngine" + File.separator + "threadpools.xml";
        THREAD_POOL_USER_FILENAME = "conf" + File.separator + "threadpools-user.conf";
    }
}
