package com.me.idps.core.util;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.idps.core.factory.IdpsFactoryProvider;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.idps.core.factory.TransactionExecutionImpl;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.client.components.web.TransformerContext;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import org.json.simple.JSONArray;
import com.adventnet.ds.query.UpdateQuery;
import com.me.devicemanagement.framework.server.api.DemoUtilAPI;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.sql.Connection;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.admin.DomainHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.SoMADUtil;
import com.me.idps.core.crud.DomainDataPopulator;
import org.json.simple.parser.JSONParser;
import org.apache.commons.lang3.mutable.MutableInt;
import java.text.MessageFormat;
import java.util.Map;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.Random;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.io.File;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import java.util.Set;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.me.idps.core.sync.asynch.DirectorySequenceAsynchImpl;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import org.json.JSONObject;
import java.util.Properties;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import java.util.concurrent.TimeUnit;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;

public class DirectoryUtil
{
    private static DirectoryUtil directoryUtil;
    
    public static DirectoryUtil getInstance() {
        if (DirectoryUtil.directoryUtil == null) {
            DirectoryUtil.directoryUtil = new DirectoryUtil();
        }
        return DirectoryUtil.directoryUtil;
    }
    
    public String longdateToString(final Long timeInMillis) {
        if (timeInMillis != null) {
            return DateTimeUtil.longdateToString((long)timeInMillis, "EEE, d MMM yyyy HH:mm:ss.SSS zzz");
        }
        return "";
    }
    
    public String formatDurationMS(final long timeInMillis) {
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis);
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) - TimeUnit.MINUTES.toSeconds(minutes);
        final long millis = timeInMillis - TimeUnit.MINUTES.toMillis(minutes) - TimeUnit.SECONDS.toMillis(seconds);
        final StringBuilder sb = new StringBuilder();
        if (minutes > 0L) {
            sb.append(minutes);
            sb.append(" M: ");
        }
        if (seconds >= 0L) {
            sb.append(seconds);
            if (millis > 0L) {
                String ms = String.valueOf(millis / 1000.0f);
                ms = ms.substring(ms.indexOf("."));
                sb.append(ms);
            }
            sb.append(" S");
        }
        return sb.toString();
    }
    
    public Object extractValue(final Row row, final String columnName, Object value) {
        String extractedValue = null;
        if (row != null && !SyMUtil.isStringEmpty(columnName)) {
            extractedValue = String.valueOf(row.get(columnName));
        }
        if (!SyMUtil.isStringEmpty(extractedValue)) {
            if (Long.class.isInstance(value)) {
                value = Long.valueOf(extractedValue);
            }
            else if (String.class.isInstance(value)) {
                value = String.valueOf(extractedValue);
            }
            else if (Boolean.class.isInstance(value)) {
                value = Boolean.valueOf(extractedValue);
            }
            else if (Integer.class.isInstance(value)) {
                value = Integer.valueOf(extractedValue);
            }
        }
        return value;
    }
    
    public String getSyncTypeValueInString(final Integer syncType) {
        if (syncType != null) {
            switch (syncType) {
                case 1: {
                    return "Full Sync";
                }
                case 2: {
                    return "Diff Sync";
                }
            }
        }
        return "undefined sync";
    }
    
    public String getSyncStatusInString(final Object syncStatus) throws Exception {
        if (syncStatus == null) {
            return "null";
        }
        final int syncStatusInt = Integer.parseInt(String.valueOf(syncStatus));
        switch (syncStatusInt) {
            case 951: {
                return I18N.getMsg("dir.status.queued", new Object[0]);
            }
            case 901: {
                return I18N.getMsg("dir.status.failed", new Object[0]);
            }
            case 921: {
                return I18N.getMsg("dir.status.succeeded", new Object[0]);
            }
            case 931: {
                return I18N.getMsg("dir.status.collated", new Object[0]);
            }
            case 911: {
                return I18N.getMsg("dir.status.suspended", new Object[0]);
            }
            case 941: {
                return I18N.getMsg("dir.status.in.progress", new Object[0]);
            }
            default: {
                return String.valueOf(syncStatusInt);
            }
        }
    }
    
    private boolean syncDomain(final Properties dmDomainProps, final Boolean doFullSync, final JSONObject postSyncTaskOpsDetails) {
        if (dmDomainProps != null) {
            final Long domainID = ((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID");
            final String dmDomainName = dmDomainProps.getProperty("NAME");
            final Long customerID = ((Hashtable<K, Long>)dmDomainProps).get("CUSTOMER_ID");
            final Integer clientID = ((Hashtable<K, Integer>)dmDomainProps).get("CLIENT_ID");
            try {
                IDPSlogger.ASYNCH.log(Level.INFO, "received sync request for {0} {1} {2} {3}", new String[] { String.valueOf(domainID), dmDomainName, String.valueOf(customerID), doFullSync ? "full" : "diff" });
                final boolean syncAllowable = DirectorySequenceAsynchImpl.getInstance().checkDomainSyncReady(((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID"), dmDomainName, true);
                IDPSlogger.ASYNCH.log(Level.INFO, "sync request {0} for {1} {2} {3} {4}", new String[] { syncAllowable ? "granted" : "not granted", String.valueOf(domainID), dmDomainName, String.valueOf(customerID), doFullSync ? "full" : "diff" });
                if (syncAllowable) {
                    final org.json.simple.JSONObject qData = new org.json.simple.JSONObject();
                    qData.put((Object)"doFullSync", (Object)doFullSync);
                    qData.put((Object)"TASK_TYPE", (Object)"SYNC_NOW");
                    qData.put((Object)"LAST_SYNC_INITIATED", (Object)System.currentTimeMillis());
                    if (postSyncTaskOpsDetails != null) {
                        final String postSyncOpsDetailsFilePath = this.writeDataIntoFileForProcessingLater(postSyncTaskOpsDetails.toString().getBytes());
                        qData.put((Object)"POST_SYNC_DETAILS", (Object)postSyncOpsDetailsFilePath);
                    }
                    this.addTaskToQueue("adProc-task", dmDomainProps, qData);
                    return true;
                }
            }
            catch (final Exception e) {
                IDPSlogger.ERR.log(Level.SEVERE, "exception in initating sync task", e);
                if (domainID != null) {
                    DirectorySyncErrorHandler.getInstance().handleError(domainID, customerID, dmDomainName, clientID, e, null);
                }
            }
        }
        return false;
    }
    
    public void syncDomain(final String dmDomainName, final Long customerID, final Boolean doFullSync, final JSONObject postSyncTaskOpsDetails) {
        if (!SyMUtil.isStringEmpty(dmDomainName) && customerID != null) {
            final Properties dmDomainProps = DMDomainDataHandler.getInstance().getDomainProps(dmDomainName, customerID);
            this.syncDomain(dmDomainProps, doFullSync, postSyncTaskOpsDetails);
        }
    }
    
    public boolean syncDomain(final Properties dmDomainProps, final Boolean doFullSync) {
        return this.syncDomain(dmDomainProps, doFullSync, null);
    }
    
    public void syncDomain(final String domainName, final Long customerID, final Integer domainClientID, final Boolean doFullSync) {
        final List<Properties> domainsList = DMDomainDataHandler.getInstance().getAllDMManagedProps(customerID);
        for (final Properties dmDomainProps : domainsList) {
            final Long dmCustomerID = ((Hashtable<K, Long>)dmDomainProps).get("CUSTOMER_ID");
            final String dmDomainName = dmDomainProps.getProperty("NAME");
            final Integer dmDomainClient = ((Hashtable<K, Integer>)dmDomainProps).get("CLIENT_ID");
            if (dmDomainName.equalsIgnoreCase(domainName) && dmCustomerID.equals(customerID)) {
                if ((domainClientID != null && domainClientID != null && domainClientID.equals(dmDomainClient)) || domainClientID == null) {
                    this.syncDomain(dmDomainProps, doFullSync, null);
                }
                else {
                    IDPSlogger.ASYNCH.log(Level.WARNING, "as {0} and {1} are not matching, not taking this sync request forward", new Object[] { domainClientID, dmDomainClient });
                }
            }
        }
    }
    
    @Deprecated
    public void syncDomain(final String dmDomainName, final Long customerID, final Boolean doFullSync) {
        this.syncDomain(dmDomainName, customerID, null, doFullSync);
    }
    
    public void syncAllDomains(final Long customerID, final boolean doFullSync) {
        final List<Properties> domainsList = DMDomainDataHandler.getInstance().getAllDMManagedProps(customerID);
        for (final Properties dmDomainProps : domainsList) {
            final Long dmDomainID = ((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID");
            final Integer domainType = ((Hashtable<K, Integer>)dmDomainProps).get("CLIENT_ID");
            final boolean procceedWithSyncRequest = this.canProceedWithSyncRequest(dmDomainID, domainType);
            if (procceedWithSyncRequest) {
                getInstance().syncDomain(dmDomainProps, doFullSync);
            }
        }
    }
    
    private boolean canProceedWithSyncRequest(final Long dmDomainID, final Integer domainType) {
        boolean procceedWithSyncRequest = true;
        try {
            IDPSlogger.SYNC.log(Level.INFO, "canProceedWithSyncRequest domainType {0}", new Object[] { domainType });
            if (domainType != null && domainType == 201) {
                procceedWithSyncRequest = Boolean.valueOf(SyMUtil.getSyMParameter("isOnpremiseADIntegrated"));
                IDPSlogger.SYNC.log(Level.INFO, "isOnpremiseADIntegrated {0}", new Object[] { procceedWithSyncRequest });
                if (!procceedWithSyncRequest) {
                    procceedWithSyncRequest |= IdpsUtil.isFeatureAvailable("DO_DAILY_ZD_SYNC");
                    IDPSlogger.SYNC.log(Level.INFO, "DO_DAILY_ZD_SYNC {0}", new Object[] { procceedWithSyncRequest });
                    if (!procceedWithSyncRequest) {
                        final Long lastSuccessfullSync = (Long)DMDomainSyncDetailsDataHandler.getInstance().getDMdomainSyncDetail(dmDomainID, "LAST_SUCCESSFUL_SYNC");
                        IDPSlogger.SYNC.log(Level.INFO, "lastSuccessfullSync {0}", new Object[] { lastSuccessfullSync });
                        if (lastSuccessfullSync == null || lastSuccessfullSync <= 0L) {
                            procceedWithSyncRequest = true;
                        }
                        else {
                            final long zdSyncThreshold = 28L;
                            final long curTime = System.currentTimeMillis();
                            final long timeElapsedSinceLastDuration = curTime - lastSuccessfullSync;
                            final long days = TimeUnit.MILLISECONDS.toDays(curTime - lastSuccessfullSync);
                            IDPSlogger.SYNC.log(Level.INFO, "{0} - {1} = {2} : {3} | {4}", new Object[] { curTime, lastSuccessfullSync, timeElapsedSinceLastDuration, days, zdSyncThreshold });
                            if (days > zdSyncThreshold) {
                                procceedWithSyncRequest = true;
                            }
                        }
                    }
                }
                IDPSlogger.SYNC.log(Level.INFO, "procceedWithSyncRequest {0}", new Object[] { procceedWithSyncRequest });
            }
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            procceedWithSyncRequest = true;
        }
        return procceedWithSyncRequest;
    }
    
    public void executeAsynchronousWithDelay(final String taskName, final Long schedulerTime, final org.json.simple.JSONObject taskDetails) {
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", taskName);
        taskInfoMap.put("schedulerTime", schedulerTime);
        taskInfoMap.put("poolName", "asynchThreadPool");
        final Properties taskProps = new Properties();
        if (taskDetails != null) {
            final Set keys = taskDetails.keySet();
            final Iterator itr = keys.iterator();
            while (itr != null && itr.hasNext()) {
                final String key = itr.next();
                ((Hashtable<String, Object>)taskProps).put(key, taskDetails.get((Object)key));
            }
        }
        ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay("com.me.idps.core.sync.schedule.DirectoryTask", taskInfoMap, taskProps);
    }
    
    private DCQueue getQueueByName(final String queueName) throws Exception {
        return DCQueueHandler.getQueue(queueName);
    }
    
    public String getQueueFolderPath(final String queueName) throws Exception {
        final String queueFolderPath = DCMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "dc-queue" + File.separator + queueName + File.separator;
        return queueFolderPath;
    }
    
    private String getFileName(final Long currentMillisTime, final int dataLen) {
        final SecureRandom random1 = new SecureRandom();
        final String randomSuffix1 = new BigInteger(32, random1).toString(16);
        final SecureRandom random2 = new SecureRandom();
        final String randomSuffix2 = new BigInteger(32, random2).toString(16);
        return Thread.currentThread().getId() + "_" + randomSuffix1 + "_" + currentMillisTime + "_" + dataLen + "_" + randomSuffix2 + ".dat";
    }
    
    private String getFileName(final int dataLen) {
        return this.getFileName(System.currentTimeMillis(), dataLen);
    }
    
    public void addTaskToQueue(final String queueName, final Properties dmDomainProps, final org.json.simple.JSONObject qData) throws Exception {
        final int len = qData.toString().getBytes().length;
        final Long currentTime = System.currentTimeMillis();
        final DCQueueData queueData = new DCQueueData();
        queueData.postTime = currentTime;
        queueData.fileName = this.getFileName(currentTime, len);
        if (dmDomainProps != null) {
            dmDomainProps.remove("CRD_USERNAME");
            dmDomainProps.remove("CRD_PASSWORD");
            final org.json.simple.JSONObject jsObject = IdpsJSONutil.convertPropertiesToJSONObject(dmDomainProps);
            qData.putAll((Map)jsObject);
            qData.remove((Object)"CRD_PASSWORD");
            qData.remove((Object)"CRD_USERNAME");
            qData.remove((Object)"CREDENTIAL_ID");
        }
        qData.put((Object)"QUEUE_NAME", (Object)queueName);
        final String qDataStr = qData.toString();
        final DCQueue queue = this.getQueueByName(queueName);
        if (queue != null) {
            queue.addToQueue(queueData, qDataStr);
            IDPSlogger.QUEUE.log(Level.INFO, "queueName:{0},domainId:{1},task:{2},producedQTID:{3}", new Object[] { queueName, String.valueOf(qData.get((Object)"DOMAIN_ID")), String.valueOf(qData.get((Object)"TASK_TYPE")), String.valueOf(queueData.fileName) });
            return;
        }
        final String exceptionMsg = MessageFormat.format("could not fetch queue {0} ", String.valueOf(queueName));
        IDPSlogger.ERR.log(Level.SEVERE, exceptionMsg);
        throw new Exception(exceptionMsg);
    }
    
    public String writeDataIntoFileForProcessingLater(final byte[] data) throws Exception {
        final String filePath = this.getQueueFolderPath("adAsync-task") + this.getFileName(data.length);
        ApiFactoryProvider.getFileAccessAPI().writeFile(filePath, data, false);
        return filePath;
    }
    
    public Object readFile(final String rawDataFilePath, final MutableInt fileReadTimeTaken, final MutableInt fileSize) throws Exception {
        final long readStart = System.currentTimeMillis();
        final String rawData = ApiFactoryProvider.getFileAccessAPI().readFileIntoString(rawDataFilePath);
        final Object obj = new JSONParser().parse(rawData);
        final long readEnd = System.currentTimeMillis();
        fileSize.setValue(String.valueOf(obj).getBytes().length);
        fileReadTimeTaken.setValue((int)Integer.valueOf(String.valueOf(readEnd - readStart)));
        return obj;
    }
    
    public Object readFile(final String rawDataFilePath) throws Exception {
        return this.readFile(rawDataFilePath, new MutableInt(0), new MutableInt(0));
    }
    
    public void deleteDomain(Properties domainProperties) {
        final String domainName = domainProperties.getProperty("NAME");
        final int domainType = ((Hashtable<K, Integer>)domainProperties).get("CLIENT_ID");
        final Long customerID = ((Hashtable<K, Long>)domainProperties).get("CUSTOMER_ID");
        final String adDomainName = domainProperties.getProperty("AD_DOMAIN_NAME");
        try {
            final Long dmDomainID = ((Hashtable<K, Long>)domainProperties).get("DOMAIN_ID");
            this.safeDeleteDirTables(dmDomainID);
            final HashMap deleteDomainDetails = new HashMap();
            deleteDomainDetails.put("CLIENT_ID", domainType);
            deleteDomainDetails.put("CUSTOMER_ID", customerID);
            deleteDomainDetails.put("DOMAINNAME", domainName);
            deleteDomainDetails.put("AD_DOMAIN_NAME", adDomainName);
            DomainDataPopulator.getInstance().deleteDomain(deleteDomainDetails);
            Long resourceID = null;
            if (domainProperties.containsKey("RESOURCE_ID")) {
                resourceID = ((Hashtable<K, Long>)domainProperties).get("RESOURCE_ID");
            }
            else if (domainName != null) {
                domainProperties = SoMADUtil.getInstance().getManagedDomainInfo(domainName);
                if (domainProperties != null && domainProperties.containsKey("RESOURCE_ID")) {
                    try {
                        resourceID = Long.valueOf(String.valueOf(((Hashtable<K, Object>)domainProperties).get("RESOURCE_ID")));
                    }
                    catch (final Exception ex) {
                        resourceID = null;
                        IDPSlogger.SOM.log(Level.WARNING, null, ex);
                    }
                }
                if (resourceID == null) {
                    try {
                        final Criteria customerCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
                        final Criteria resourceTypeCri = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)5, 0);
                        final Criteria domainNameCri = new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)domainName, 0, false);
                        final DataObject dobj = SyMUtil.getPersistence().get("Resource", customerCri.and(resourceTypeCri.and(domainNameCri)));
                        final Row row = dobj.getRow("Resource");
                        if (row != null) {
                            resourceID = (Long)row.get("RESOURCE_ID");
                        }
                    }
                    catch (final Exception ex) {
                        IDPSlogger.SOM.log(Level.SEVERE, null, ex);
                    }
                }
            }
            if (resourceID != null) {
                DomainHandler.getInstance().deleteDomainDetails(domainName, resourceID);
            }
        }
        catch (final DataAccessException | SyMException ex2) {
            IDPSlogger.SOM.log(Level.SEVERE, null, ex2);
        }
    }
    
    private void safeDelDirTable(final Connection connection, final String tableName, final String domainIDcol, final Long dmDomainID) throws Exception {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl(tableName);
        deleteQuery.setCriteria(new Criteria(Column.getColumn(tableName, domainIDcol), (Object)dmDomainID, 0));
        DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
    }
    
    private void safeDelDirTempTable(final Connection connection, final String tableName, final String tempIdcol, final Criteria dirSyncDetailsCri) throws Exception {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl(tableName);
        if (!SyMUtil.isStringEmpty(tempIdcol)) {
            deleteQuery.addJoin(new Join(tableName, "DirObjTmp", new String[] { tempIdcol }, new String[] { "TEMP_ID" }, 2));
        }
        deleteQuery.addJoin(new Join("DirObjTmp", "DirectorySyncDetails", new String[] { "SYNC_TOKEN_ID" }, new String[] { "SYNC_TOKEN_ID" }, 2));
        deleteQuery.setCriteria(dirSyncDetailsCri);
        DirectoryQueryutil.getInstance().executeDeleteQuery(connection, deleteQuery, false);
    }
    
    private void safeDeleteDirTables(final Long dmDomainID) {
        if (dmDomainID != null) {
            Connection connection = null;
            final Criteria dirSyncDetailsCri = new Criteria(Column.getColumn("DirectorySyncDetails", "DM_DOMAIN_ID"), (Object)dmDomainID, 0);
            try {
                connection = RelationalAPI.getInstance().getConnection();
                this.safeDelDirTempTable(connection, "DirObjTmpDuplAttr", "DUPLICATED_MAX_TEMP_ID", dirSyncDetailsCri);
                this.safeDelDirTempTable(connection, "DirObjTmpDuplVal", "DUPLICATED_MAX_TEMP_ID", dirSyncDetailsCri);
                this.safeDelDirTempTable(connection, "DirObjTmpRegIntVal", "TEMP_ID", dirSyncDetailsCri);
                this.safeDelDirTempTable(connection, "DirObjTmpRegStrVal", "TEMP_ID", dirSyncDetailsCri);
                this.safeDelDirTempTable(connection, "DirObjTmpArrStrVal", "TEMP_ID", dirSyncDetailsCri);
                this.safeDelDirTempTable(connection, "DirObjTmpDupl", "DUPLICATED_MAX_TEMP_ID", dirSyncDetailsCri);
                this.safeDelDirTempTable(connection, "DirObjTmp", null, dirSyncDetailsCri);
                this.safeDelDirTable(connection, "DirectorySyncDetails", "DM_DOMAIN_ID", dmDomainID);
                this.safeDelDirTable(connection, "DirObjArrLngVal", "DM_DOMAIN_ID", dmDomainID);
                this.safeDelDirTable(connection, "DirObjRegStrVal", "DM_DOMAIN_ID", dmDomainID);
                this.safeDelDirTable(connection, "DirObjRegIntVal", "DM_DOMAIN_ID", dmDomainID);
                this.safeDelDirTable(connection, "DirResRel", "DM_DOMAIN_ID", dmDomainID);
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, null, ex);
                if (connection != null) {
                    try {
                        connection.close();
                    }
                    catch (final Exception ex) {
                        IDPSlogger.ERR.log(Level.SEVERE, null, ex);
                    }
                }
            }
            finally {
                if (connection != null) {
                    try {
                        connection.close();
                    }
                    catch (final Exception ex2) {
                        IDPSlogger.ERR.log(Level.SEVERE, null, ex2);
                    }
                }
            }
        }
    }
    
    public void updateCredentialstatus(final Long dmDomainID, boolean isADreachable) {
        final DemoUtilAPI demoUtilAPI = ApiFactoryProvider.getDemoUtilAPI();
        if (demoUtilAPI != null && demoUtilAPI.isDemoMode()) {
            isADreachable = true;
        }
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DMManagedDomainCredentialRel");
        updateQuery.setCriteria(new Criteria(Column.getColumn("DMManagedDomainCredentialRel", "DOMAIN_ID"), (Object)dmDomainID, 0));
        updateQuery.setUpdateColumn("VALIDATION_STATUS", (Object)isADreachable);
        try {
            DirectoryQueryutil.getInstance().executeUpdateQuery(updateQuery, false);
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.INFO, null, ex);
        }
        DMDomainDataHandler.getInstance().hideOrShowDomainPwdChangedMsg();
    }
    
    public void deleteFile(final String filePath) {
        new FileDeleter(filePath).run();
    }
    
    public Object readAndDeleteFile(final String rawDataFilePath, final MutableInt fileReadTimeTaken, final MutableInt fileDeleteTimeTaken, final MutableInt fileSize) throws Exception {
        try {
            final Object retObj = this.readFile(rawDataFilePath, fileReadTimeTaken, fileSize);
            return retObj;
        }
        finally {
            final Long fileDeleteStart = System.currentTimeMillis();
            this.deleteFile(rawDataFilePath);
            final Long fileDeleteEnd = System.currentTimeMillis();
            if (fileDeleteTimeTaken != null) {
                fileDeleteTimeTaken.setValue((int)Integer.valueOf(String.valueOf(fileDeleteEnd - fileDeleteStart)));
            }
        }
    }
    
    public Object readAndDeleteFile(final String rawDataFilePath) throws Exception {
        return this.readAndDeleteFile(rawDataFilePath, new MutableInt(0), new MutableInt(0), new MutableInt(0));
    }
    
    private JSONArray getDirObjAttrVal(final Long resourceId, final Long attrID) throws DataAccessException {
        final JSONArray resultAr = new JSONArray();
        final Criteria cri = new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)attrID, 0).and(new Criteria(Column.getColumn("DirObjRegStrVal", "RESOURCE_ID"), (Object)resourceId, 0));
        final SelectQuery query = DirectoryQueryutil.getInstance().getDirObjAttrQuery(cri);
        query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "VALUE"));
        query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "OBJ_ID"));
        query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "ATTR_ID"));
        query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "ADDED_AT"));
        query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "RESOURCE_ID"));
        final DataObject dobj = SyMUtil.getPersistenceLite().get(query);
        if (dobj != null && !dobj.isEmpty()) {
            final Iterator valueItr = dobj.getRows("DirObjRegStrVal", new Criteria(Column.getColumn("DirObjRegStrVal", "RESOURCE_ID"), (Object)resourceId, 0));
            while (valueItr != null && valueItr.hasNext()) {
                resultAr.add(valueItr.next().get("VALUE"));
            }
        }
        return resultAr;
    }
    
    public String getFirstDirObjAttrValue(final Long resourceId, final Long attrID) throws DataAccessException {
        final JSONArray jsonArray = this.getDirObjAttrVal(resourceId, attrID);
        if (jsonArray != null && !jsonArray.isEmpty()) {
            return String.valueOf(jsonArray.get(0));
        }
        return null;
    }
    
    public SelectQuery getObjectAttriutesQuery(final Criteria criteria) {
        final SelectQuery query = DirectoryQueryutil.getInstance().getDirObjAttrQuery(criteria);
        query.addJoin(new Join("DirObjRegStrVal", "DirResRel", new String[] { "OBJ_ID" }, new String[] { "OBJ_ID" }, 2));
        query.addJoin(new Join("DirResRel", "DMDomain", new String[] { "DM_DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 2));
        query.addJoin(new Join("DirResRel", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("DirResRel", "DirObjRegIntVal", new String[] { "OBJ_ID" }, new String[] { "OBJ_ID" }, 1));
        query.addSelectColumn(Column.getColumn("DirResRel", "GUID"));
        query.addSelectColumn(Column.getColumn("DirResRel", "OBJ_ID"));
        query.addSelectColumn(Column.getColumn("DirResRel", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("DirResRel", "DM_DOMAIN_ID"));
        query.addSelectColumn(Column.getColumn("DirResRel", "DIR_RESOURCE_TYPE"));
        query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "VALUE"));
        query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "OBJ_ID"));
        query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "ATTR_ID"));
        query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "ADDED_AT"));
        query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("DirObjRegIntVal", "VALUE"));
        query.addSelectColumn(Column.getColumn("DirObjRegIntVal", "OBJ_ID"));
        query.addSelectColumn(Column.getColumn("DirObjRegIntVal", "ATTR_ID"));
        query.addSelectColumn(Column.getColumn("DirObjRegIntVal", "ADDED_AT"));
        query.addSelectColumn(Column.getColumn("DirObjRegIntVal", "RESOURCE_ID"));
        return query;
    }
    
    private Properties popupateObjAttr(final Properties objectAttributes, final DataObject dobj, final String tableName, final Criteria objIDcri) throws DataAccessException {
        final String valCol = DirectoryQueryutil.getValCol(tableName);
        final String attrIDcol = DirectoryQueryutil.getAttrIDcol(tableName);
        final Iterator iterator = dobj.getRows(tableName, objIDcri);
        while (iterator != null && iterator.hasNext()) {
            Object val = "";
            Long attrID = null;
            int attrType = -1;
            final Row dirObjValRow = iterator.next();
            if (dirObjValRow != null) {
                val = dirObjValRow.get(valCol);
                attrID = (Long)dirObjValRow.get(attrIDcol);
                final String attrKey = DirectoryAttributeConstants.getAttrKey(attrID);
                attrType = DirectoryAttributeConstants.getAttrType(attrID);
                if (attrType != 1) {
                    continue;
                }
                ((Hashtable<String, Object>)objectAttributes).put(attrKey, val);
            }
        }
        return objectAttributes;
    }
    
    public Properties getObjectAttributes(final Criteria criteria) throws DataAccessException {
        final SelectQuery query = this.getObjectAttriutesQuery(criteria);
        final Properties objectAttributes = this.getObjectAttributes(query);
        return objectAttributes;
    }
    
    public Properties getMemberRelDetails(Criteria criteria, final Long attrID) throws Exception {
        criteria = criteria.and(new Criteria(Column.getColumn("DirObjArrLngVal", "ATTR_ID"), (Object)attrID, 0));
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirObjArrLngVal"));
        selectQuery.addJoin(new Join("DirObjArrLngVal", "DirResRel", new String[] { "VALUE_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("DirResRel", "GUID"));
        final JSONArray jsonArray = IdpsUtil.executeSelectQuery(selectQuery);
        final List<String> memberRel = new ArrayList<String>();
        for (int i = 0; jsonArray != null && i < jsonArray.size(); ++i) {
            final org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject)jsonArray.get(i);
            final String memberRelGUID = (String)jsonObject.get((Object)"GUID");
            memberRel.add(memberRelGUID);
        }
        final Properties properties = new Properties();
        ((Hashtable<String, List<String>>)properties).put(DirectoryAttributeConstants.getAttrKey(attrID), memberRel);
        return properties;
    }
    
    public Properties getObjectAttributes(final SelectQuery query) throws DataAccessException {
        Properties objectAttributes = null;
        final DataObject dobj = SyMUtil.getPersistenceLite().get(query);
        if (dobj != null && !dobj.isEmpty() && dobj.containsTable("DirResRel")) {
            objectAttributes = new Properties();
            final Row dirResRelRow = dobj.getRow("DirResRel");
            final Long objID = (Long)dirResRelRow.get("OBJ_ID");
            ((Hashtable<String, Long>)objectAttributes).put("OBJ_ID", objID);
            ((Hashtable<String, Object>)objectAttributes).put("GUID", dirResRelRow.get("GUID"));
            ((Hashtable<String, Object>)objectAttributes).put("RESOURCE_ID", dirResRelRow.get("RESOURCE_ID"));
            ((Hashtable<String, Object>)objectAttributes).put("DM_DOMAIN_ID", dirResRelRow.get("DM_DOMAIN_ID"));
            ((Hashtable<String, Object>)objectAttributes).put("DIR_RESOURCE_TYPE", dirResRelRow.get("DIR_RESOURCE_TYPE"));
            objectAttributes = this.popupateObjAttr(objectAttributes, dobj, "DirObjRegStrVal", new Criteria(Column.getColumn("DirObjRegStrVal", "OBJ_ID"), (Object)objID, 0));
            objectAttributes = this.popupateObjAttr(objectAttributes, dobj, "DirObjRegIntVal", new Criteria(Column.getColumn("DirObjRegIntVal", "OBJ_ID"), (Object)objID, 0));
        }
        return objectAttributes;
    }
    
    public int getDirObjStatus(final TransformerContext tableContext) {
        try {
            Integer status = null;
            try {
                status = IdpsUtil.getInstance().getIntVal(String.valueOf(tableContext.getAssociatedPropertyValue("DIROBJREGINTVAL_STATUS_VALUE")));
            }
            catch (final Exception ex2) {}
            if (status != null) {
                return status;
            }
            final Long dirResID = (Long)tableContext.getAssociatedPropertyValue("DirResRel.RESOURCE_ID");
            final String domainName = (String)tableContext.getAssociatedPropertyValue("Resource.DOMAIN_NETBIOS_NAME");
            if (!"MDM".equalsIgnoreCase(domainName) && dirResID == null) {
                return 5;
            }
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, "exception in querying status of obj", ex);
        }
        return 1;
    }
    
    public String getDomainName(final Long resourceID, final String domainName) {
        String retDomainName = null;
        if (!SyMUtil.isStringEmpty(domainName) && domainName.equalsIgnoreCase("Zoho Directory")) {
            try {
                retDomainName = getInstance().getFirstDirObjAttrValue(resourceID, 116L);
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, "exception in getting domain name value from directory tables", ex);
            }
        }
        if (SyMUtil.isStringEmpty(retDomainName)) {
            retDomainName = domainName;
        }
        if (SyMUtil.isStringEmpty(retDomainName)) {
            try {
                retDomainName = (String)DBUtil.getValueFromDB("Resource", "RESOURCE_ID", (Object)resourceID, "DOMAIN_NETBIOS_NAME");
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, "exception in getting domain name value from resource table", ex);
                retDomainName = "";
            }
        }
        return retDomainName;
    }
    
    public org.json.simple.JSONObject getNewTaskDetails(final String domainName, final Integer dmDomainClientID, final Long dmDomainID, final Long customerID) {
        final org.json.simple.JSONObject taskDetails = new org.json.simple.JSONObject();
        taskDetails.put((Object)"NAME", (Object)domainName);
        taskDetails.put((Object)"DOMAIN_ID", (Object)dmDomainID);
        taskDetails.put((Object)"CUSTOMER_ID", (Object)customerID);
        taskDetails.put((Object)"CLIENT_ID", (Object)dmDomainClientID);
        return taskDetails;
    }
    
    public org.json.simple.JSONObject getNewTaskDetails(final org.json.simple.JSONObject qData) {
        final String domainName = (String)qData.get((Object)"NAME");
        final Long dmDomainID = (Long)qData.get((Object)"DOMAIN_ID");
        final Long customerID = (Long)qData.get((Object)"CUSTOMER_ID");
        final Integer dmDomainClientID = (Integer)qData.get((Object)"CLIENT_ID");
        final org.json.simple.JSONObject taskDetails = this.getNewTaskDetails(domainName, dmDomainClientID, dmDomainID, customerID);
        if (qData.containsKey((Object)"FILE_WRITE_SIZE")) {
            taskDetails.put((Object)"FILE_WRITE_SIZE", qData.get((Object)"FILE_WRITE_SIZE"));
        }
        if (qData.containsKey((Object)"FILE_WRITE_TIME_TAKEN")) {
            taskDetails.put((Object)"FILE_WRITE_TIME_TAKEN", qData.get((Object)"FILE_WRITE_TIME_TAKEN"));
        }
        return taskDetails;
    }
    
    public Long getAttrID(final String key) throws DataAccessException {
        final String[] tokens = key.split(":");
        final String actualKey = tokens[0];
        if (!SyMUtil.isStringEmpty(actualKey)) {
            return DirectoryAttributeConstants.getAttrID(actualKey);
        }
        return null;
    }
    
    public Object convertObj(final Object inputObj, Object returnObj) {
        org.json.simple.JSONObject returnJS = null;
        Properties returnProps = null;
        Set<Map.Entry<Object, Object>> entries = null;
        if (inputObj != null && returnObj != null) {
            if (inputObj instanceof Properties && returnObj instanceof org.json.simple.JSONObject) {
                final Properties inputProps = (Properties)inputObj;
                returnJS = (org.json.simple.JSONObject)returnObj;
                entries = inputProps.entrySet();
            }
            else {
                if (!(inputObj instanceof org.json.simple.JSONObject) || !(returnObj instanceof Properties)) {
                    return returnObj;
                }
                final org.json.simple.JSONObject inputJS = (org.json.simple.JSONObject)inputObj;
                returnProps = (Properties)returnObj;
                entries = inputJS.entrySet();
            }
            if (entries != null && !entries.isEmpty()) {
                for (final Map.Entry<Object, Object> entry : entries) {
                    final Object key = entry.getKey();
                    final String keyStr = String.valueOf(key);
                    if (!SyMUtil.isStringEmpty(keyStr)) {
                        if (returnObj instanceof org.json.simple.JSONObject) {
                            returnJS.put(key, entry.getValue());
                        }
                        else {
                            if (!(returnObj instanceof Properties)) {
                                continue;
                            }
                            returnProps.put(key, entry.getValue());
                        }
                    }
                }
            }
            if (returnObj instanceof org.json.simple.JSONObject) {
                returnObj = returnJS;
            }
            else if (returnObj instanceof Properties) {
                returnObj = returnProps;
            }
        }
        return returnObj;
    }
    
    public org.json.simple.JSONObject getQdataFromSchedulerProps(final Properties properties) {
        final org.json.simple.JSONObject qData = (org.json.simple.JSONObject)this.convertObj(properties, new org.json.simple.JSONObject());
        if (qData.containsKey((Object)"TASK_TYPE")) {
            qData.put((Object)"SCHEDULER_TASK", qData.get((Object)"TASK_TYPE"));
        }
        qData.put((Object)"TASK_TYPE", (Object)"SCHEDULER_TASK");
        return qData;
    }
    
    public boolean clearActiveTransactionsIfAnyWithoutException() {
        try {
            final boolean closedTransaction = TransactionExecutionImpl.getInstance().clearActiveTransactionsIfAny();
            return closedTransaction;
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public Long[] removeActiveADGroupFromList(final Long[] resourceIds) throws DataAccessException {
        final List<Long> filteredResIDlist = new ArrayList<Long>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroup"));
        selectQuery.addJoin(new Join("CustomGroup", "DirObjRegIntVal", new Criteria(Column.getColumn("DirObjRegIntVal", "ATTR_ID"), (Object)118L, 0).and(new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)Column.getColumn("DirObjRegIntVal", "RESOURCE_ID"), 0)), 1));
        selectQuery.setCriteria(new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)resourceIds, 8).and(new Criteria(Column.getColumn("DirObjRegIntVal", "VALUE"), (Object)null, 0).or(new Criteria(Column.getColumn("DirObjRegIntVal", "VALUE"), (Object)new Integer[] { 2, 4, 5 }, 8, false))));
        selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
        final DataObject dataObject = IdpsUtil.getPersistenceLite().get(selectQuery);
        if (dataObject != null && !dataObject.isEmpty() && dataObject.containsTable("CustomGroup")) {
            final Iterator itr = dataObject.getRows("CustomGroup");
            while (itr != null && itr.hasNext()) {
                final Row row = itr.next();
                filteredResIDlist.add((Long)row.get("RESOURCE_ID"));
            }
        }
        return filteredResIDlist.toArray(new Long[filteredResIDlist.size()]);
    }
    
    private String getDomainCollKey(final Long dmDomainID, final String collIDstr) {
        return "DirectorySyncDetails_VALIDITY_" + String.valueOf(dmDomainID) + "_" + collIDstr;
    }
    
    public boolean canExecQuery(final Long dmDomainID, final Long collationID) {
        return Boolean.valueOf(String.valueOf(ApiFactoryProvider.getCacheAccessAPI().getCache(this.getDomainCollKey(dmDomainID, String.valueOf(collationID)), 2)));
    }
    
    public void updateGlobalCollStatus(final Long dmDomainID, final String collationID, final boolean enable) {
        final String key = this.getDomainCollKey(dmDomainID, collationID);
        IDPSlogger.DBO.log(Level.INFO, "{0} collation key {1}", new Object[] { key, enable });
        if (enable) {
            ApiFactoryProvider.getCacheAccessAPI().putCache(key, (Object)Boolean.TRUE, 2);
        }
        else {
            ApiFactoryProvider.getCacheAccessAPI().removeCache(key, 2);
        }
    }
    
    private void hideDirectoryMsg() {
        MessageProvider.getInstance().hideMessage("IDP_DIR_ADD");
    }
    
    private void showDirectoryMsg() {
        MessageProvider.getInstance().unhideMessage("IDP_DIR_ADD");
    }
    
    public void hideOrShowDirectoryMsg() {
        int totalRelevantDirAdded = 0;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirectoryMetrics"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DirectoryMetrics", "KEY"), (Object)new String[] { "AZURE_COUNT", "OP_COUNT", "OKTA_COUNT" }, 8, false));
        selectQuery.addSelectColumn(Column.getColumn("DirectoryMetrics", "VALUE"));
        final JSONArray jsonArray = IdpsUtil.executeSelectQuery(selectQuery);
        if (jsonArray != null && !jsonArray.isEmpty()) {
            for (int i = 0; i < jsonArray.size(); ++i) {
                final org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject)jsonArray.get(i);
                try {
                    final int curVal = Integer.valueOf(String.valueOf(jsonObject.get((Object)"VALUE")));
                    totalRelevantDirAdded += curVal;
                }
                catch (final Exception ex) {}
            }
        }
        if (totalRelevantDirAdded > 0) {
            this.hideDirectoryMsg();
        }
        else {
            this.showDirectoryMsg();
        }
    }
    
    public String getDbOpMetricKey(final Long dmDomainID, final Long collationID, final String block, final int opType) {
        return this.getKey(dmDomainID, collationID, "DirectorySyncDetails_" + block + "_" + opType);
    }
    
    public int getCurrentDBOpsMetric(final Long dmDomainID, final Long collationID, final String block, final int opType, final boolean remove) {
        int curValue = 0;
        final String key = getInstance().getDbOpMetricKey(dmDomainID, collationID, block, opType);
        try {
            final Object obj = ApiFactoryProvider.getCacheAccessAPI().getCache(key, 2);
            if (obj != null) {
                curValue = Integer.valueOf(String.valueOf(obj));
            }
        }
        catch (final Exception ex) {}
        if (remove) {
            try {
                ApiFactoryProvider.getCacheAccessAPI().removeCache(key, 2);
            }
            catch (final Exception ex2) {}
        }
        return curValue;
    }
    
    public Logger getLoggerForQueue(final String queueName) {
        switch (queueName) {
            case "adProc-task": {
                return IDPSlogger.SYNC;
            }
            case "adAsync-task":
            case "adTemp-task":
            case "adRetreiver-task": {
                return IDPSlogger.ASYNCH;
            }
            case "adCoreDB-task": {
                return IDPSlogger.DBO;
            }
            default: {
                return IDPSlogger.ASYNCH;
            }
        }
    }
    
    private int extractValFromJS(final org.json.simple.JSONObject qData, final String key) {
        int val = 0;
        try {
            val = Integer.valueOf(String.valueOf(qData.get((Object)key)));
        }
        catch (final Exception ex) {}
        return val;
    }
    
    public int extractValFromCache(final Long dmDomainID, final Long syncTokenID, final String key, final boolean remove) {
        final String keyStr = this.getKey(dmDomainID, syncTokenID, key);
        int val = 0;
        try {
            val = Integer.valueOf(String.valueOf(ApiFactoryProvider.getCacheAccessAPI().getCache(keyStr, 2)));
        }
        catch (final Exception ex) {}
        finally {
            if (remove) {
                ApiFactoryProvider.getCacheAccessAPI().removeCache(keyStr, 2);
            }
        }
        return val;
    }
    
    public String getKey(final Long dmDomainID, final Long syncTokenID, final String key) {
        return dmDomainID + "_" + syncTokenID + "_" + key;
    }
    
    public void setFileIOstats(final Long dmDomainID, final Long syncTokenID, final String key, final int val) {
        ApiFactoryProvider.getCacheAccessAPI().putCache(this.getKey(dmDomainID, syncTokenID, key), (Object)val, 2, 21600);
    }
    
    public void updateFileIOstats(final Long dmDomainID, final Long syncTokenID, final org.json.simple.JSONObject qData) {
        int totalWritSize = this.extractValFromCache(dmDomainID, syncTokenID, "FILE_WRITE_SIZE", false);
        int totalFileWritten = this.extractValFromCache(dmDomainID, syncTokenID, "FILE_WRITE_NUM", false);
        int totalWriteTimeTaken = this.extractValFromCache(dmDomainID, syncTokenID, "FILE_WRITE_TIME_TAKEN", false);
        final int totalFileRead = this.extractValFromCache(dmDomainID, syncTokenID, "FILE_READ_NUM", false);
        final int totalReadSize = this.extractValFromCache(dmDomainID, syncTokenID, "FILE_READ_SIZE", false);
        final int totalReadTimeTaken = this.extractValFromCache(dmDomainID, syncTokenID, "FILE_READ_TIME_TAKEN", false);
        final int totalFileDel = this.extractValFromCache(dmDomainID, syncTokenID, "FILE_DELETE_NUM", false);
        final int totalDelSize = this.extractValFromCache(dmDomainID, syncTokenID, "FILE_DELETE_SIZE", false);
        final int totalDelTimeTaken = this.extractValFromCache(dmDomainID, syncTokenID, "FILE_DELETE_TIME_TAKEN", false);
        int curWriteSize = 0;
        int curWriteTimeTaken = 0;
        final int curReadSize = this.extractValFromJS(qData, "FILE_READ_SIZE");
        final int curDelSize = this.extractValFromJS(qData, "FILE_DELETE_SIZE");
        final int curReadTimeTaken = this.extractValFromJS(qData, "FILE_READ_TIME_TAKEN");
        final int curDelTimeTaken = this.extractValFromJS(qData, "FILE_DELETE_TIME_TAKEN");
        final JSONArray fileSizeWrite = (JSONArray)qData.get((Object)"FILE_WRITE_SIZE");
        final JSONArray fileWriteTime = (JSONArray)qData.get((Object)"FILE_WRITE_TIME_TAKEN");
        if (fileSizeWrite != null) {
            final int curFilesWritten = fileWriteTime.size();
            for (int i = 0; i < curFilesWritten; ++i) {
                curWriteSize += Integer.valueOf(String.valueOf(fileSizeWrite.get(i)));
                curWriteTimeTaken += Integer.valueOf(String.valueOf(fileWriteTime.get(i)));
            }
            totalWritSize += curWriteSize;
            totalFileWritten += curFilesWritten;
            totalWriteTimeTaken += curWriteTimeTaken;
        }
        this.setFileIOstats(dmDomainID, syncTokenID, "FILE_WRITE_NUM", totalFileWritten);
        this.setFileIOstats(dmDomainID, syncTokenID, "FILE_WRITE_SIZE", totalWritSize);
        this.setFileIOstats(dmDomainID, syncTokenID, "FILE_WRITE_TIME_TAKEN", totalWriteTimeTaken);
        this.setFileIOstats(dmDomainID, syncTokenID, "FILE_READ_NUM", totalFileRead + 2);
        this.setFileIOstats(dmDomainID, syncTokenID, "FILE_READ_SIZE", totalReadSize + 2 * curReadSize);
        this.setFileIOstats(dmDomainID, syncTokenID, "FILE_READ_TIME_TAKEN", totalReadTimeTaken + 2 * curReadTimeTaken);
        this.setFileIOstats(dmDomainID, syncTokenID, "FILE_DELETE_NUM", totalFileDel + 2);
        this.setFileIOstats(dmDomainID, syncTokenID, "FILE_DELETE_SIZE", totalDelSize + 2 * curDelSize);
        this.setFileIOstats(dmDomainID, syncTokenID, "FILE_DELETE_TIME_TAKEN", totalDelTimeTaken + 2 * curDelTimeTaken);
    }
    
    public boolean isManualVAdisabled(final boolean fromDB) {
        return IdpsFactoryProvider.getIdpsProdEnvAPI().isManualVAdisabled(fromDB);
    }
    
    public void initiateResetHandling() {
        final Properties props = new Properties();
        final org.json.simple.JSONObject qData = new org.json.simple.JSONObject();
        qData.put((Object)"TASK_TYPE", (Object)"RESET");
        try {
            getInstance().addTaskToQueue("adAsync-task", props, qData);
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
        }
    }
    
    public void handleUpgrade() throws Exception {
        final org.json.simple.JSONObject qData = new org.json.simple.JSONObject();
        qData.put((Object)"TASK_TYPE", (Object)"HANDLE_UPGRADE");
        this.addTaskToQueue("adAsync-task", null, qData);
    }
    
    public boolean isZDOPIntegratedWithApiOrUserIntegration() {
        boolean isZDOPIntegratedWithApiOrUserIntegration = false;
        try {
            isZDOPIntegratedWithApiOrUserIntegration = Boolean.valueOf(SyMUtil.getSyMParameter("isOnpremiseADIntegrated"));
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, "exception in retrieving opad user interaction integration status", ex);
        }
        return isZDOPIntegratedWithApiOrUserIntegration;
    }
    
    public int getZDopCount(final long customerID) {
        int zdOPcount = 0;
        try {
            zdOPcount = Integer.valueOf(String.valueOf(ApiFactoryProvider.getCacheAccessAPI().getCache(customerID + "_" + "OP_COUNT", 2)));
        }
        catch (final Exception ex) {
            try {
                final String zdOPcountStr = String.valueOf(DBUtil.getValueFromDB("DirectoryMetrics", "KEY", (Object)"OP_COUNT", "VALUE"));
                if (SyMUtil.isStringEmpty(zdOPcountStr)) {
                    zdOPcount = 0;
                }
                else {
                    zdOPcount = Integer.valueOf(zdOPcountStr);
                }
            }
            catch (final Exception ex2) {
                zdOPcount = 0;
            }
        }
        return zdOPcount;
    }
    
    public boolean isZDexplicit(final long customerID) {
        final int zdOPcount = this.getZDopCount(customerID);
        return zdOPcount > 0 || this.isZDOPIntegratedWithApiOrUserIntegration();
    }
    
    public void updateDirectoryMetrics() {
        try {
            final Long[] custIDs = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            for (int i = 0; custIDs != null && i < custIDs.length; ++i) {
                final Long customerID = custIDs[i];
                if (customerID != null) {
                    try {
                        final org.json.simple.JSONObject qData = new org.json.simple.JSONObject();
                        qData.put((Object)"CUSTOMER_ID", (Object)customerID);
                        qData.put((Object)"TASK_TYPE", (Object)"UPDATE_METRICS");
                        getInstance().addTaskToQueue("adProc-task", null, qData);
                    }
                    catch (final Exception ex) {
                        IDPSlogger.ERR.log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        catch (final Exception ex2) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex2);
        }
    }
    
    static {
        DirectoryUtil.directoryUtil = null;
    }
    
    private class FileDeleter implements Runnable
    {
        String filePath;
        
        private FileDeleter(final String filePath) {
            this.filePath = filePath;
        }
        
        @Override
        public void run() {
            try {
                if (!SyMUtil.isStringEmpty(this.filePath)) {
                    ApiFactoryProvider.getFileAccessAPI().deleteFile(this.filePath);
                }
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.WARNING, null, ex);
            }
        }
    }
}
