package com.me.devicemanagement.framework.server.queue;

import java.util.Collection;
import com.adventnet.persistence.DataAccessException;
import java.io.FileReader;
import java.io.File;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.List;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.io.Reader;
import java.lang.reflect.Constructor;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Hashtable;

public class DCQueueHandler implements DCQueueConstants
{
    private static Hashtable qHash;
    private static Logger logger;
    private static String sourceClass;
    private static HashMap helperClassMap;
    private static JSONObject qBulkProcessorJSON;
    
    public static synchronized DCQueue createQueue(final DCQueueMetaData qMetaData) throws Exception {
        final String sourceMethod = "createQueue";
        SyMLogger.info(DCQueueHandler.logger, DCQueueHandler.sourceClass, sourceMethod, "\n-----------------------------CREATING QUEUE START------------------------------");
        SyMLogger.info(DCQueueHandler.logger, DCQueueHandler.sourceClass, sourceMethod, "Creating Queue for given meta data: " + qMetaData);
        DCQueue qObj = null;
        try {
            CustomerInfoUtil.getInstance();
            if (CustomerInfoUtil.isSAS()) {
                ApiFactoryProvider.getCacheAccessAPI().putCache(qMetaData.queueName, qMetaData, 1);
                qObj = getQueueInstance(qMetaData.queueName);
            }
            else {
                qObj = DCQueueHandler.qHash.get(qMetaData.queueName);
                if (qObj != null) {
                    SyMLogger.warning(DCQueueHandler.logger, DCQueueHandler.sourceClass, "DCQueue", "Queue for given name already exists. Going to throw exception for meta data: " + qMetaData);
                    throw new Exception("Cannot create Queue. Already exists with the given name: " + qMetaData.queueName);
                }
                qObj = createQueueInstance(qMetaData);
                DCQueueHandler.qHash.put(qMetaData.queueName, qObj);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(DCQueueHandler.logger, DCQueueHandler.sourceClass, sourceMethod, "Caught exception while creating queue for given QueueMetaData: " + qMetaData, ex);
            throw ex;
        }
        SyMLogger.info(DCQueueHandler.logger, DCQueueHandler.sourceClass, sourceMethod, "\n-----------------------------CREATING QUEUE ENDS------------------------------");
        SyMLogger.debug(DCQueueHandler.logger, DCQueueHandler.sourceClass, sourceMethod, "End of createQueue() with QueueMetaData: {0}", new Object[] { qMetaData });
        return qObj;
    }
    
    private static DCQueue createQueueInstance(final DCQueueMetaData qMetaData) throws Exception {
        DCQueue qObj = null;
        if (qMetaData != null) {
            final Class queueClass = Class.forName(qMetaData.queueClassName);
            final Constructor queueConstructor = queueClass.getDeclaredConstructor(DCQueueMetaData.class);
            qObj = queueConstructor.newInstance(qMetaData);
        }
        return qObj;
    }
    
    private static DCQueue getQueueInstance(final String qName) throws Exception {
        DCQueue qObj = null;
        CustomerInfoUtil.getInstance();
        if (CustomerInfoUtil.isSAS()) {
            DCQueueMetaData qMetaData = DCQueueHandler.qHash.get(qName);
            if (qMetaData == null) {
                qMetaData = createQMetaDataByName(qName);
                DCQueueHandler.qHash.put(qName, qMetaData);
            }
            qObj = createQueueInstance(qMetaData);
        }
        else {
            qObj = DCQueueHandler.qHash.get(qName);
        }
        return qObj;
    }
    
    public static DCQueue getQueue(final String qName) throws Exception {
        DCQueue qObj = null;
        qObj = getQueueInstance(qName);
        return qObj;
    }
    
    public static void addToQueue(final String qName, final DCQueueData qData) throws Exception {
        final DCQueue qObj = getQueueInstance(qName);
        qObj.addToQueue(qData);
    }
    
    public static void addToQueue(final String qName, final DCQueueData qData, final Reader reader) throws Exception {
        final DCQueue qObj = getQueueInstance(qName);
        qObj.addToQueue(qData, reader);
    }
    
    public static void addToQueue(final String qName, final DCQueueData qData, final String qContent) throws Exception {
        final DCQueue qObj = getQueueInstance(qName);
        qObj.addToQueue(qData, qContent);
    }
    
    public static void deleteQueue(final String qName) throws Exception {
        CustomerInfoUtil.getInstance();
        if (CustomerInfoUtil.isSAS()) {
            ApiFactoryProvider.getCacheAccessAPI().removeCache(qName, 1);
        }
        else {
            final DCQueue qObj = DCQueueHandler.qHash.get(qName);
            qObj.shutdownQueue();
            DCQueueHandler.qHash.remove(qName);
        }
    }
    
    public static void createAndStartQueuesFromDB() {
        final String sourceMethod = "createAndStartQueuesFromDB";
        final QueueConfigurations queueConfigurations = QueueConfigurations.getInstance();
        final String qmdTable = "DCQueueMetaData";
        try {
            queueConfigurations.checkQueueConfigurationNeeded();
            final List<String> enabledQueues = queueConfigurations.fetchApplicableQueues();
            queueConfigurations.addEnabledQueue(enabledQueues);
            final DataObject qmdDO = SyMUtil.getPersistence().get(qmdTable, (Criteria)null);
            if (qmdDO.isEmpty()) {
                SyMLogger.log(DCQueueHandler.logger.getName(), Level.INFO, "No queue data is found in the table:  {0}", new Object[] { qmdTable });
                return;
            }
            final Iterator qmdRows = qmdDO.getRows(qmdTable);
            while (qmdRows.hasNext()) {
                final Row qmdRow = qmdRows.next();
                try {
                    final DCQueueMetaData metaData = createQMetaData(qmdRow);
                    final DCQueue queue = createQueue(metaData);
                    if (queue == null) {
                        DCQueueHandler.logger.log(Level.SEVERE, "Dc Queue object is null! Metadata = {0}", metaData);
                    }
                    else {
                        DCQueueHandler.logger.log(Level.FINE, "Autostart: {0} | isQueueConfigurationNeeded: {1}  | queue Name: {2}", new Object[] { metaData.autoStart, queueConfigurations.isQueueConfigurationNeeded(), metaData.queueName });
                        if (metaData.autoStart && queueConfigurations.isQueueConfigurationNeeded()) {
                            if (queueConfigurations.isQueueEnabled(metaData.queueName)) {
                                queue.start();
                                SyMLogger.log(DCQueueHandler.logger.getName(), Level.INFO, "Started queue {0} as it is defined in the product map", new Object[] { metaData.queueName });
                            }
                            else {
                                SyMLogger.log(DCQueueHandler.logger.getName(), Level.INFO, "Not started queue {0} as it is undefined in the product map", new Object[] { metaData.queueName });
                            }
                        }
                        else {
                            if (!metaData.autoStart) {
                                continue;
                            }
                            queue.start();
                            SyMLogger.log(DCQueueHandler.logger.getName(), Level.INFO, "Started queue without product map checks: {0}", new Object[] { metaData.queueName });
                        }
                    }
                }
                catch (final Exception ex) {
                    SyMLogger.error(DCQueueHandler.logger, DCQueueHandler.sourceClass, sourceMethod, "Caught exception while creating queue with the data: " + qmdRow, ex);
                }
            }
            SyMLogger.info(DCQueueHandler.logger, DCQueueHandler.sourceClass, sourceMethod, "Queues creation configured in DB is completed.....");
        }
        catch (final Exception ex2) {
            SyMLogger.error(DCQueueHandler.logger, DCQueueHandler.sourceClass, sourceMethod, "Caught exception while creating queues from DB: ", ex2);
        }
    }
    
    public static DCQueueMetaData createQMetaDataByName(final String queueName) throws Exception {
        final String sourceMethod = "createQMetaDataByName";
        DCQueueMetaData metaData = null;
        final Criteria criteria = new Criteria(new Column("DCQueueMetaData", "QUEUE_NAME"), (Object)queueName, 0, false);
        final DataObject qmdDO = SyMUtil.getPersistence().get("DCQueueMetaData", criteria);
        if (!qmdDO.isEmpty()) {
            final Row qmdRow = qmdDO.getRow("DCQueueMetaData");
            metaData = createQMetaData(qmdRow);
        }
        else {
            SyMLogger.log(DCQueueHandler.logger.getName(), Level.INFO, "No queue data is found in the table:  {0}", new Object[] { queueName });
        }
        return metaData;
    }
    
    private static DCQueueMetaData createQMetaData(final Row queueMetaDataRow) throws Exception {
        final DCQueueMetaData metaData = new DCQueueMetaData();
        metaData.queueName = (String)queueMetaDataRow.get("QUEUE_NAME");
        metaData.queueClassName = (String)queueMetaDataRow.get("QUEUE_CLASS_NAME");
        metaData.queueTableName = (String)queueMetaDataRow.get("QUEUE_TABLE_NAME");
        metaData.processorClassName = (String)queueMetaDataRow.get("DATA_PROCESSOR_CLASS");
        metaData.loggerName = (String)queueMetaDataRow.get("LOGGER_NAME");
        metaData.qMaxSize = (int)queueMetaDataRow.get("MAX_SIZE");
        metaData.qMinSize = (int)queueMetaDataRow.get("MIN_SIZE");
        metaData.processThreadCount = (int)queueMetaDataRow.get("NUM_OF_THREADS");
        metaData.processThreadMaxCount = (int)queueMetaDataRow.get("MAX_NUM_OF_THREADS");
        metaData.keepAliveTimeout = (long)queueMetaDataRow.get("KEEP_ALIVE_TIMEOUT");
        metaData.timeoutAllThreads = (boolean)queueMetaDataRow.get("TIMEOUT_ALL_THREADS");
        metaData.delayBetweenProcessing = (long)queueMetaDataRow.get("SLEEP_BETWEEN_PROCESS");
        metaData.sleepBeweenQueueSizeCheck = (long)queueMetaDataRow.get("QUEUE_SIZE_CHECK_TIMER");
        metaData.autoStart = (boolean)queueMetaDataRow.get("Q_AUTO_START");
        metaData.queueExtnTableName = (String)queueMetaDataRow.get("QUEUE_EXTN_TABLE_NAME");
        metaData.priorityQRefTableName = (String)queueMetaDataRow.get("PRIORITY_Q_REF_TABLE_NAME");
        metaData.retainQDataInMemory = Boolean.valueOf(queueMetaDataRow.get("RETAIN_QDATA_IN_MEMORY").toString());
        modifyQProcessorClass(metaData);
        if (DCQueueHandler.helperClassMap.isEmpty()) {
            DCQueueHandler.helperClassMap = loadqHelperData();
        }
        if (DCQueueHandler.helperClassMap.containsKey(metaData.queueClassName)) {
            metaData.qHelperClassName = DCQueueHandler.helperClassMap.get(metaData.queueClassName);
        }
        else {
            metaData.qHelperClassName = null;
        }
        return metaData;
    }
    
    private static void loadUpdatedProcessingClass() {
        try {
            final LicenseProvider licenseProvider = LicenseProvider.getInstance();
            final String licenseType = licenseProvider.getLicenseType();
            if (licenseType != null && licenseType.equalsIgnoreCase("R")) {
                final String deviceCount = licenseProvider.getNoOfComutersManaged();
                if (deviceCount != null) {
                    final int licensedDeviceCount = Integer.parseInt(deviceCount);
                    final boolean isRedis = Boolean.parseBoolean(SyMUtil.getSyMParameter("enableRedis"));
                    final boolean isFullJsonEnabled = Boolean.parseBoolean(FrameworkConfigurations.getSpecificPropertyIfExists("queue_process", "enable_fullscan_json", (Object)"false").toString());
                    if (licensedDeviceCount < 250 && DCQueueHandler.qBulkProcessorJSON.length() == 0 && isRedis && isFullJsonEnabled) {
                        final String filePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "Queue" + File.separator + "BulkQProcessorClassList.json";
                        if (new File(filePath).exists()) {
                            final FileReader updatesReader = new FileReader(filePath);
                            DCQueueHandler.qBulkProcessorJSON = new JSONObject((Object)updatesReader);
                        }
                        else {
                            DCQueueHandler.logger.log(Level.FINEST, "processor file class does not exists");
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            DCQueueHandler.logger.log(Level.SEVERE, "Error while getting queueProcessor value", e);
        }
    }
    
    private static void modifyQProcessorClass(final DCQueueMetaData dcQueueMetaData) {
        try {
            loadUpdatedProcessingClass();
            if (DCQueueHandler.qBulkProcessorJSON.length() != 0 && DCQueueHandler.qBulkProcessorJSON.has(dcQueueMetaData.queueName)) {
                final JSONObject queueDetailsObj = DCQueueHandler.qBulkProcessorJSON.getJSONObject(dcQueueMetaData.queueName);
                if (queueDetailsObj != null && queueDetailsObj.has("data_processor_class")) {
                    final String processorClass = (String)queueDetailsObj.get("data_processor_class");
                    dcQueueMetaData.processorClassName = processorClass;
                    dcQueueMetaData.isBulkProcessor = true;
                }
            }
        }
        catch (final Exception e) {
            DCQueueHandler.logger.log(Level.SEVERE, "Exception while changing processing class", e);
        }
    }
    
    private static HashMap loadqHelperData() throws DataAccessException {
        final DataObject qmdDO = SyMUtil.getPersistence().get("QueueHelperMetaData", (Criteria)null);
        final HashMap<String, String> hs = new HashMap<String, String>();
        if (qmdDO != null) {
            final Iterator qmdRows = qmdDO.getRows("QueueHelperMetaData");
            while (qmdRows.hasNext()) {
                final Row qmdRow = qmdRows.next();
                hs.put((String)qmdRow.get("QUEUE_CLASS"), (String)qmdRow.get("QUEUE_HELPER"));
            }
        }
        return hs;
    }
    
    public long getMemoryCount(final String qName) {
        long memoryCount = 0L;
        try {
            final DCQueue queueObj = getQueue(qName);
            memoryCount = queueObj.getQueueDataCount(1);
        }
        catch (final Exception e) {
            DCQueueHandler.logger.log(Level.WARNING, "Exception in getMemoryCount: ", e);
        }
        return memoryCount;
    }
    
    public static void addApplicableQueues() {
        final String sourceMethod = "addApplicableQueues";
        final String qmdTable = "DCQueueMetaData";
        final QueueConfigurations queueConfigurations = QueueConfigurations.getInstance();
        try {
            SyMLogger.log(DCQueueHandler.logger.getName(), Level.INFO, "Currently running queues: {0}", new Object[] { queueConfigurations.getEnabledQueues() });
            final List<String> currentApplicableQueues = queueConfigurations.fetchApplicableQueues();
            currentApplicableQueues.removeAll(queueConfigurations.getEnabledQueues());
            SyMLogger.log(DCQueueHandler.logger.getName(), Level.INFO, "Newly applicable queues that are yet to be started: {0}", new Object[] { currentApplicableQueues });
            final DataObject qmdDO = SyMUtil.getPersistence().get(qmdTable, (Criteria)null);
            if (qmdDO.isEmpty()) {
                SyMLogger.log(DCQueueHandler.logger.getName(), Level.INFO, "No queue data is found in the table:  {0}", new Object[] { qmdTable });
                return;
            }
            if (!queueConfigurations.isQueueConfigurationNeeded() || currentApplicableQueues.isEmpty()) {
                DCQueueHandler.logger.log(Level.SEVERE, "is queue configuration needed?: {0} | is current Applicable Queues Empty?: {1} ", new Object[] { queueConfigurations.isQueueConfigurationNeeded(), currentApplicableQueues.isEmpty() });
                return;
            }
            final Iterator qmdRows = qmdDO.getRows(qmdTable);
            while (qmdRows.hasNext()) {
                final Row qmdRow = qmdRows.next();
                final DCQueueMetaData metaData = createQMetaData(qmdRow);
                final DCQueue queue = DCQueueHandler.qHash.get(metaData.queueName);
                if (metaData.autoStart) {
                    if (queue != null && currentApplicableQueues.contains(metaData.queueName)) {
                        queue.start();
                        SyMLogger.log(DCQueueHandler.logger.getName(), Level.INFO, "Started queue {0} as it is defined in the product map", new Object[] { metaData.queueName });
                    }
                    else {
                        SyMLogger.log(DCQueueHandler.logger.getName(), Level.INFO, "Not started queue {0} as it is undefined in the product map", new Object[] { metaData.queueName });
                    }
                }
            }
            queueConfigurations.addEnabledQueue(currentApplicableQueues);
        }
        catch (final Exception ex) {
            SyMLogger.error(DCQueueHandler.logger, DCQueueHandler.sourceClass, sourceMethod, "Exception in addApplicableQueues: ", ex);
        }
    }
    
    static {
        DCQueueHandler.qHash = new Hashtable();
        DCQueueHandler.logger = Logger.getLogger("DCQueueLogger");
        DCQueueHandler.sourceClass = "DCQueueHandler";
        DCQueueHandler.helperClassMap = new HashMap();
        DCQueueHandler.qBulkProcessorJSON = new JSONObject();
    }
}
