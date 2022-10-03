package com.me.ems.onpremise.summaryserver.summary.proberegistration;

import java.util.List;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.DataAccessException;
import java.util.Map;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.WritableDataObject;
import java.util.HashMap;
import java.util.logging.Logger;

public class ProbeUtil
{
    public static Logger logger;
    private static ProbeUtil instance;
    private static HashMap<Long, HashMap> allProbeDetailsCache;
    
    public static HashMap<Long, HashMap> getAllProbeDetailsCache() {
        return ProbeUtil.allProbeDetailsCache;
    }
    
    public static void setAllProbeDetailsCache(final HashMap<Long, HashMap> allProbeDetailsCache) {
        ProbeUtil.allProbeDetailsCache = allProbeDetailsCache;
    }
    
    public static ProbeUtil getInstance() {
        if (ProbeUtil.instance == null) {
            ProbeUtil.instance = new ProbeUtil();
        }
        return ProbeUtil.instance;
    }
    
    public synchronized HashMap getAllProbeDetails() {
        DataObject dataObject = (DataObject)new WritableDataObject();
        try {
            if (ProbeUtil.allProbeDetailsCache.isEmpty()) {
                final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeDetails"));
                final Join join = new Join("ProbeDetails", "ProbeServerInfo", new String[] { "PROBE_ID" }, new String[] { "PROBE_ID" }, 2);
                sq.addJoin(join);
                final Join join_extn = new Join("ProbeDetails", "ProbeDetailsExtn", new String[] { "PROBE_ID" }, new String[] { "PROBE_ID" }, 2);
                sq.addJoin(join_extn);
                sq.addSelectColumn(new Column("ProbeDetails", "*"));
                sq.addSelectColumn(new Column("ProbeServerInfo", "*"));
                sq.addSelectColumn(new Column("ProbeDetailsExtn", "*"));
                dataObject = SyMUtil.getPersistence().get(sq);
                if (!dataObject.isEmpty()) {
                    final Iterator iter = dataObject.getRows("ProbeServerInfo");
                    while (iter.hasNext()) {
                        final HashMap<String, Object> eachProbeDetails = new HashMap<String, Object>();
                        final Row probeRow = iter.next();
                        final Long probeId = (Long)probeRow.get("PROBE_ID");
                        final Row probeExtnRow = dataObject.getRow("ProbeDetailsExtn", new Criteria(new Column("ProbeDetailsExtn", "PROBE_ID"), (Object)probeId, 0));
                        final Row probeDetails = dataObject.getRow("ProbeDetails", new Criteria(new Column("ProbeDetails", "PROBE_ID"), (Object)probeId, 0));
                        eachProbeDetails.put("PROBE_NAME", probeDetails.get("PROBE_NAME"));
                        eachProbeDetails.put("HOST", probeRow.get("HOST"));
                        eachProbeDetails.put("PORT", (int)probeRow.get("PORT"));
                        eachProbeDetails.put("PROTOCOL", probeRow.get("PROTOCOL"));
                        eachProbeDetails.put("IPADDRESS", probeRow.get("IPADDRESS"));
                        eachProbeDetails.put("BUILD_NUMBER", probeRow.get("BUILD_NUMBER"));
                        eachProbeDetails.put("QUEUE_NAME", probeExtnRow.get("QUEUE_NAME"));
                        ProbeUtil.allProbeDetailsCache.put(probeId, eachProbeDetails);
                    }
                }
            }
        }
        catch (final Exception e) {
            ProbeUtil.logger.log(Level.SEVERE, "Exception while getting Probe RELATED Details", e);
        }
        return ProbeUtil.allProbeDetailsCache;
    }
    
    public HashMap getProbeDetail(final Long probeId) {
        HashMap probeDetail = new HashMap();
        try {
            if (ProbeUtil.allProbeDetailsCache.isEmpty()) {
                this.getAllProbeDetails();
            }
            probeDetail = ProbeUtil.allProbeDetailsCache.get(probeId);
        }
        catch (final Exception e) {
            ProbeUtil.logger.log(Level.SEVERE, "Exception while getting Probe specific details", e);
        }
        return probeDetail;
    }
    
    public boolean setProbeInstalled(final Long probeId, final Map probeDetailsMap) {
        try {
            final HashMap probeDetail = new HashMap();
            final String queueName = this.getQueueName();
            probeDetailsMap.put("queueName", queueName);
            this.createProbeServerInfo(probeId, probeDetailsMap);
            this.createProbeDetailsExtn(probeId, probeDetailsMap);
            this.createProbeLiveStatusRow(probeId);
            probeDetail.put("HOST", probeDetailsMap.get("host"));
            probeDetail.put("PROTOCOL", probeDetailsMap.get("protocol"));
            probeDetail.put("IPADDRESS", probeDetailsMap.get("ipAddress"));
            probeDetail.put("PORT", Integer.parseInt(String.valueOf(probeDetailsMap.get("port"))));
            probeDetail.put("QUEUE_NAME", queueName);
            probeDetail.put("PROBE_NAME", this.getProbeName(probeId));
            ProbeUtil.allProbeDetailsCache.put(probeId, probeDetail);
            this.startQueue(queueName, Boolean.FALSE);
            return true;
        }
        catch (final Exception ex) {
            ProbeUtil.logger.log(Level.SEVERE, "Exception while Setting Probe Details", ex);
            return false;
        }
    }
    
    private void invalidateInstallKey(final Long probeId, final Long installedTime) {
        try {
            final Criteria criteria = new Criteria(new Column("ProbeInstallationKeyDetails", "PROBE_ID"), (Object)probeId, 0);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeInstallationKeyDetails"));
            sq.addSelectColumn(new Column("ProbeInstallationKeyDetails", "*"));
            sq.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            final Row probeInstallKeyRow = dataObject.getRow("ProbeInstallationKeyDetails");
            if (probeInstallKeyRow != null) {
                probeInstallKeyRow.set("IS_VALID", (Object)false);
                probeInstallKeyRow.set("EXPIRED_TIME", (Object)installedTime);
                dataObject.updateRow(probeInstallKeyRow);
                SyMUtil.getPersistence().update(dataObject);
            }
        }
        catch (final DataAccessException ex) {
            ProbeUtil.logger.log(Level.SEVERE, "Exception while Invalidating Installation key", (Throwable)ex);
        }
    }
    
    public long generateAutoGenValueForProbe(final Long probeId) {
        long seqGenValue = 1000000000L;
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeUVHRangeDetails"));
            final Column seqGenColumn = new Column("ProbeUVHRangeDetails", "AUTOGEN_START_VALUE").maximum();
            seqGenColumn.setColumnAlias("Max_UVH_Value");
            sq.addSelectColumn(seqGenColumn);
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            final Row row = new Row("ProbeUVHRangeDetails");
            row.set("PROBE_ID", (Object)probeId);
            final long maxUvh = this.getMaxAutoGenValueInTable(sq);
            seqGenValue = maxUvh + 1000000000L;
            row.set("AUTOGEN_START_VALUE", (Object)seqGenValue);
            dataObject.addRow(row);
            SyMUtil.getPersistence().add(dataObject);
        }
        catch (final Exception ex) {
            ProbeUtil.logger.log(Level.SEVERE, "Exception while generating UVH value for probe", ex);
        }
        return seqGenValue;
    }
    
    private long getMaxAutoGenValueInTable(final SelectQuery selectQuery) throws Exception {
        long maxUvh = 0L;
        final RelationalAPI relationalAPI = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        try {
            conn = relationalAPI.getConnection();
            ds = relationalAPI.executeQuery((Query)selectQuery, conn);
            while (ds.next()) {
                final Object value = ds.getValue(1);
                if (value != null) {
                    maxUvh = (long)value;
                }
            }
        }
        catch (final QueryConstructionException ex) {
            ex.printStackTrace();
            throw ex;
        }
        catch (final SQLException ex2) {
            ex2.printStackTrace();
            throw ex2;
        }
        finally {
            try {
                if (ds != null) {
                    ds.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex3) {
                ex3.printStackTrace();
            }
        }
        return maxUvh;
    }
    
    private String getQueueName() {
        String queueName = null;
        final ArrayList queueNames = new ArrayList((Collection<? extends E>)Arrays.asList("push-to-probe-1", "push-to-probe-2", "push-to-probe-3", "push-to-probe-4", "push-to-probe-5", "push-to-probe-6", "push-to-probe-7", "push-to-probe-8", "push-to-probe-9", "push-to-probe-10"));
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeDetailsExtn"));
        sq.addSelectColumn(new Column("ProbeDetailsExtn", "*"));
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ProbeDetailsExtn");
                while (iterator.hasNext()) {
                    final Row probeRow = iterator.next();
                    if (probeRow != null) {
                        final String currentQueue = probeRow.get("QUEUE_NAME").toString();
                        if (!queueNames.contains(currentQueue)) {
                            continue;
                        }
                        queueNames.remove(currentQueue);
                    }
                }
            }
            if (queueNames.size() > 0) {
                queueName = queueNames.get(0).toString();
            }
            else {
                queueName = "Unavailable";
            }
        }
        catch (final Exception e) {
            ProbeUtil.logger.log(Level.SEVERE, "Exception while getting available queues", e);
        }
        return queueName;
    }
    
    public void startQueue(final String queueName, final Boolean startUp) {
        try {
            if (startUp) {
                this.updateQueueCache(queueName);
                this.startAvailableProbeQueues();
            }
            else if (queueName == null || queueName.equalsIgnoreCase("Unavailable")) {
                ProbeUtil.logger.log(Level.INFO, "Queue is not available");
            }
            else {
                DCQueueHandler.getQueue(queueName).start();
                ProbeUtil.logger.log(Level.INFO, "Queue started: " + queueName);
                this.updateQueueCache(queueName);
            }
        }
        catch (final Exception e) {
            ProbeUtil.logger.log(Level.SEVERE, "Exception while Starting Queue: " + queueName, e);
        }
    }
    
    private void startAvailableProbeQueues() {
        try {
            final List configuredQueues = (ArrayList)ApiFactoryProvider.getCacheAccessAPI().getCache("PUSH_TO_PROBES_QUEUES");
            if (configuredQueues != null) {
                for (int index = 0; index < configuredQueues.size(); ++index) {
                    final String queueName = configuredQueues.get(index).toString();
                    DCQueueHandler.getQueue(queueName).start();
                    ProbeUtil.logger.log(Level.INFO, "Queue started: " + queueName);
                }
            }
            else {
                ProbeUtil.logger.log(Level.INFO, "No Queues configured");
            }
        }
        catch (final Exception e) {
            ProbeUtil.logger.log(Level.SEVERE, "Exception while Starting available probe queues: ", e);
        }
    }
    
    private void updateQueueCache(final String startedQueue) {
        List runningQueues = (ArrayList)ApiFactoryProvider.getCacheAccessAPI().getCache("PUSH_TO_PROBES_QUEUES");
        if (runningQueues == null) {
            runningQueues = this.getQueuesFromDB();
        }
        if (startedQueue != null && !startedQueue.equalsIgnoreCase("Unavailable")) {
            runningQueues.add(startedQueue);
            ProbeUtil.logger.log(Level.INFO, "Queue added. Queue value: " + startedQueue);
        }
        else {
            ProbeUtil.logger.log(Level.INFO, "Queue not added. Queue value: " + startedQueue);
        }
        ApiFactoryProvider.getCacheAccessAPI().putCache("PUSH_TO_PROBES_QUEUES", (Object)runningQueues, 2);
        ProbeUtil.logger.log(Level.INFO, "Updated PUSH_TO_PROBES_QUEUES Cache with value : " + runningQueues.toString());
    }
    
    public List getQueuesFromDB() {
        final List configuredQueues = new ArrayList();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeDetails"));
            final Join join = new Join("ProbeDetails", "ProbeServerInfo", new String[] { "PROBE_ID" }, new String[] { "PROBE_ID" }, 2);
            sq.addJoin(join);
            final Join join_extn = new Join("ProbeDetails", "ProbeDetailsExtn", new String[] { "PROBE_ID" }, new String[] { "PROBE_ID" }, 2);
            sq.addJoin(join_extn);
            sq.addSelectColumn(new Column("ProbeDetails", "*"));
            sq.addSelectColumn(new Column("ProbeServerInfo", "*"));
            sq.addSelectColumn(new Column("ProbeDetailsExtn", "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ProbeDetailsExtn");
                while (iterator.hasNext()) {
                    final Row probeRow = iterator.next();
                    final String queueName = probeRow.get("QUEUE_NAME").toString();
                    if (queueName != null && !queueName.equalsIgnoreCase("Unavailable")) {
                        configuredQueues.add(queueName);
                    }
                }
            }
        }
        catch (final Exception e) {
            ProbeUtil.logger.log(Level.SEVERE, "Exception while getting configured queues: ", e);
        }
        return configuredQueues;
    }
    
    private void createProbeServerInfo(final Long probeId, final Map probeDetailsMap) {
        try {
            final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
            final Row probeServerRow = new Row("ProbeServerInfo");
            final Long installedTime = System.currentTimeMillis();
            probeServerRow.set("PROBE_ID", (Object)probeId);
            probeServerRow.set("HOST", probeDetailsMap.get("host"));
            probeServerRow.set("PROTOCOL", probeDetailsMap.get("protocol"));
            if (probeDetailsMap.get("port") != null) {
                probeServerRow.set("PORT", (Object)Integer.parseInt(String.valueOf(probeDetailsMap.get("port"))));
            }
            probeServerRow.set("INSTALLED_TIME", (Object)installedTime);
            probeServerRow.set("TIMEZONE", probeDetailsMap.get("timeZone"));
            probeServerRow.set("BUILD_NUMBER", probeDetailsMap.get("version"));
            probeServerRow.set("IPADDRESS", probeDetailsMap.get("ipAddress"));
            dataObject.addRow(probeServerRow);
            SyMUtil.getPersistence().add(dataObject);
            this.invalidateInstallKey(probeId, installedTime);
        }
        catch (final Exception e) {
            ProbeUtil.logger.log(Level.SEVERE, "Exception while creating probe server info row", e);
        }
    }
    
    private void createProbeDetailsExtn(final Long probeId, final Map probeDetailsMap) {
        try {
            final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
            final Row probeExtnRow = new Row("ProbeDetailsExtn");
            probeExtnRow.set("PROBE_ID", (Object)probeId);
            probeExtnRow.set("FREE_SPACE", probeDetailsMap.get("freeSpace"));
            probeExtnRow.set("TOTAL_SPACE", probeDetailsMap.get("totalSpace"));
            probeExtnRow.set("QUEUE_NAME", probeDetailsMap.get("queueName"));
            dataObject.addRow(probeExtnRow);
            SyMUtil.getPersistence().add(dataObject);
        }
        catch (final Exception e) {
            ProbeUtil.logger.log(Level.SEVERE, "Exception while creating probe detail extn row", e);
        }
    }
    
    private void createProbeLiveStatusRow(final Long probeId) {
        try {
            final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
            final Row probeStatusRow = new Row("ProbeLiveStatus");
            probeStatusRow.set("PROBE_ID", (Object)probeId);
            probeStatusRow.set("STATUS", (Object)2);
            dataObject.addRow(probeStatusRow);
            SyMUtil.getPersistence().add(dataObject);
        }
        catch (final Exception e) {
            ProbeUtil.logger.log(Level.SEVERE, "Exception while creating probe detail extn row", e);
        }
    }
    
    public String getProbeName(final Long probeId) {
        String probeName = null;
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeDetails"));
            sq.addSelectColumn(new Column("ProbeDetails", "*"));
            sq.setCriteria(new Criteria(new Column("ProbeDetails", "PROBE_ID"), (Object)probeId, 0));
            final DataObject dataObject = SyMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Row probeRow = dataObject.getFirstRow("ProbeDetails");
                if (probeRow != null) {
                    probeName = (String)probeRow.get("PROBE_NAME");
                }
            }
        }
        catch (final Exception e) {
            ProbeUtil.logger.log(Level.SEVERE, "Exception while getting PROBE NAME: ", e);
        }
        return probeName;
    }
    
    static {
        ProbeUtil.logger = Logger.getLogger("probeActionsLogger");
        ProbeUtil.instance = null;
        ProbeUtil.allProbeDetailsCache = new HashMap<Long, HashMap>();
    }
}
