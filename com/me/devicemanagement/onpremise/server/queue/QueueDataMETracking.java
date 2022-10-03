package com.me.devicemanagement.onpremise.server.queue;

import java.util.Hashtable;
import java.util.LinkedList;
import org.apache.commons.io.FileUtils;
import java.sql.Connection;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.SelectQuery;
import java.sql.SQLException;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.devicemanagement.framework.server.queue.RedisQueueUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.redis.RedisErrorTracker;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.Map;
import org.json.JSONObject;
import java.util.logging.Logger;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.HashMap;

public class QueueDataMETracking
{
    private static final Integer MEDIUM_THRESHOLD;
    private static final Integer HIGH_THRESHOLD;
    private static final String METRACK_KEY = "METrack:";
    private static QueueDataMETracking queueDataMETracking;
    private static long lastUpdateTimetoDB;
    private static long updateForEvery;
    private static long nextUpdateTime;
    private static long thresholdTime;
    public static HashMap<String, HashMap<String, AtomicLong>> qAdditionTimeDetails;
    private static Queue qPostingTimeData;
    private static Logger logger;
    private static HashMap redisQueueDetails;
    public static final String QUEUE_ME_TRACK_DATA = "queue_me_tracking_data";
    private static final String CURRENTVAL = "_current";
    private static final String MAXVAL = "_Max";
    private static final String ME_TRACK_JSON_FILE_NAME = "ServerQueueSize.json";
    private static HashMap<String, AtomicLong> queueTrackingMap;
    
    private QueueDataMETracking() {
    }
    
    public static QueueDataMETracking getInstance() {
        if (QueueDataMETracking.queueDataMETracking == null) {
            QueueDataMETracking.queueDataMETracking = new QueueDataMETracking();
        }
        return QueueDataMETracking.queueDataMETracking;
    }
    
    static void updateCount(final String queueName, final Long currentMaxTime, final Long currentFileTime, final Long currentDBTime) {
        if (!QueueDataMETracking.qAdditionTimeDetails.containsKey(queueName)) {
            synchronized (QueueDataMETracking.class) {
                QueueDataMETracking.qAdditionTimeDetails.put(queueName, new HashMap<String, AtomicLong>());
                QueueDataMETracking.qAdditionTimeDetails.get(queueName).put("TotalTime", new AtomicLong());
                QueueDataMETracking.qAdditionTimeDetails.get(queueName).put("FileTime", new AtomicLong());
                QueueDataMETracking.qAdditionTimeDetails.get(queueName).put("DBTime", new AtomicLong());
                QueueDataMETracking.qAdditionTimeDetails.get(queueName).put("Count", new AtomicLong());
                QueueDataMETracking.qAdditionTimeDetails.get(queueName).put("qPostingTimeExceedsCountBase", new AtomicLong());
                QueueDataMETracking.qAdditionTimeDetails.get(queueName).put("qPostingTimeExceedsCountMedium", new AtomicLong());
                QueueDataMETracking.qAdditionTimeDetails.get(queueName).put("qPostingTimeExceedsCountHigh", new AtomicLong());
            }
        }
        ((AtomicLong)QueueDataMETracking.qAdditionTimeDetails.get(queueName).get("TotalTime")).addAndGet(currentMaxTime);
        ((AtomicLong)QueueDataMETracking.qAdditionTimeDetails.get(queueName).get("FileTime")).addAndGet(currentFileTime);
        ((AtomicLong)QueueDataMETracking.qAdditionTimeDetails.get(queueName).get("DBTime")).addAndGet(currentDBTime);
        ((AtomicLong)QueueDataMETracking.qAdditionTimeDetails.get(queueName).get("Count")).incrementAndGet();
        if (currentMaxTime >= QueueDataMETracking.HIGH_THRESHOLD) {
            ((AtomicLong)QueueDataMETracking.qAdditionTimeDetails.get(queueName).get("qPostingTimeExceedsCountHigh")).incrementAndGet();
        }
        else if (currentMaxTime >= QueueDataMETracking.MEDIUM_THRESHOLD) {
            ((AtomicLong)QueueDataMETracking.qAdditionTimeDetails.get(queueName).get("qPostingTimeExceedsCountMedium")).incrementAndGet();
        }
        else {
            ((AtomicLong)QueueDataMETracking.qAdditionTimeDetails.get(queueName).get("qPostingTimeExceedsCountBase")).incrementAndGet();
        }
    }
    
    private static void resetData() {
        QueueDataMETracking.qAdditionTimeDetails = new HashMap<String, HashMap<String, AtomicLong>>();
    }
    
    public static void copyQDetails() {
        try {
            final int ndays = 3;
            final JSONObject qDataJson = new JSONObject();
            for (final String queueName : QueueDataMETracking.qAdditionTimeDetails.keySet()) {
                if (((AtomicLong)QueueDataMETracking.qAdditionTimeDetails.get(queueName).get("TotalTime")).get() / ((AtomicLong)QueueDataMETracking.qAdditionTimeDetails.get(queueName).get("Count")).get() >= QueueDataMETracking.thresholdTime) {
                    qDataJson.put(queueName, (Map)QueueDataMETracking.qAdditionTimeDetails.get(queueName));
                }
            }
            QueueDataMETracking.qPostingTimeData.add(qDataJson);
            if (QueueDataMETracking.qPostingTimeData.size() > ndays) {
                QueueDataMETracking.qPostingTimeData.remove();
            }
            resetData();
        }
        catch (final Exception e) {
            QueueDataMETracking.logger.log(Level.SEVERE, "Error while maintaining queue addition time details :", e);
        }
    }
    
    public static Queue getQueueDataDetails() {
        return QueueDataMETracking.qPostingTimeData;
    }
    
    public static void loadFirstTimeQDetails() {
        QueuewiseRedisDetails.loadRedisQueueDetails(QueueDataMETracking.redisQueueDetails);
    }
    
    private static QueuewiseRedisDetails getQueueObject(final String qName) {
        final String keyName = "METrack:" + qName;
        QueuewiseRedisDetails obj = QueueDataMETracking.redisQueueDetails.get(keyName);
        if (obj == null) {
            obj = new QueuewiseRedisDetails();
            obj.loadFirstTimeData(keyName);
        }
        return obj;
    }
    
    static void updateRedisQueueDetails(final String qName, final boolean status) {
        final String keyName = "METrack:" + qName;
        final QueuewiseRedisDetails obj = getQueueObject(qName);
        obj.updateQueueAdditionDetails(qName, status);
        QueueDataMETracking.redisQueueDetails.put(keyName, obj);
        checkAndUpdateDB();
        QueueDataMETracking.logger.log(Level.FINE, "DATA : " + getRedisQueueDetailsJSON());
    }
    
    static void updateResetFailureDetails(final String qName, final int count) {
        final String keyName = "METrack:" + qName;
        final QueuewiseRedisDetails obj = getQueueObject(qName);
        obj.updateResetFailures(qName, count);
        QueueDataMETracking.redisQueueDetails.put(keyName, obj);
        checkAndUpdateDB();
        QueueDataMETracking.logger.log(Level.FINE, "DATA : " + getRedisQueueDetailsJSON());
    }
    
    public static String getRedisQueueDetailsJSON() {
        final JSONObject redisDetails = new JSONObject();
        try {
            for (final Map.Entry pair : QueueDataMETracking.redisQueueDetails.entrySet()) {
                final QueuewiseRedisDetails obj = pair.getValue();
                redisDetails.put((String)pair.getKey(), (Object)obj.getJSONString());
            }
        }
        catch (final Exception e) {
            QueueDataMETracking.logger.log(Level.WARNING, "Exception while getting queuewise redis error ", e);
        }
        return redisDetails.toString();
    }
    
    public static synchronized void updateDB() {
        if (Boolean.parseBoolean(SyMUtil.getSyMParameter("enableRedis"))) {
            try {
                QueueDataMETracking.logger.log(Level.INFO, "Start of Redis Health Params to DB");
                final DataObject redisHealthDO = RedisErrorTracker.getRedisHealthParamDO((Criteria)null);
                if (redisHealthDO == null || redisHealthDO.isEmpty()) {
                    final Iterator<Map.Entry<String, QueuewiseRedisDetails>> it = QueueDataMETracking.redisQueueDetails.entrySet().iterator();
                    while (it.hasNext()) {
                        final Row row = new Row("RedisHealthParams");
                        final Map.Entry pair = it.next();
                        final QueuewiseRedisDetails obj = pair.getValue();
                        row.set("RPARAM_NAME", pair.getKey());
                        row.set("RPARAM_VALUE", (Object)obj.getJSONString());
                        redisHealthDO.addRow(row);
                    }
                    final Row row = new Row("RedisHealthParams");
                    row.set("RPARAM_NAME", (Object)"TotalRedisConnectionFailures");
                    row.set("RPARAM_VALUE", (Object)RedisErrorTracker.getRedisConnectionsErrors());
                    redisHealthDO.addRow(row);
                }
                else {
                    final Iterator<Map.Entry<String, QueuewiseRedisDetails>> it = QueueDataMETracking.redisQueueDetails.entrySet().iterator();
                    Criteria criteria = null;
                    while (it.hasNext()) {
                        final Map.Entry pair = it.next();
                        criteria = new Criteria(new Column("RedisHealthParams", "RPARAM_NAME"), pair.getKey(), 0);
                        Row row2 = redisHealthDO.getRow("RedisHealthParams", criteria);
                        final QueuewiseRedisDetails obj2 = pair.getValue();
                        if (row2 == null) {
                            row2 = new Row("RedisHealthParams");
                            row2.set("RPARAM_NAME", pair.getKey());
                            row2.set("RPARAM_VALUE", (Object)obj2.getJSONString());
                            redisHealthDO.addRow(row2);
                        }
                        else {
                            row2.set("RPARAM_VALUE", (Object)obj2.getJSONString());
                            redisHealthDO.updateRow(row2);
                        }
                    }
                    criteria = new Criteria(new Column("RedisHealthParams", "RPARAM_NAME"), (Object)"TotalRedisConnectionFailures", 0);
                    Row row3 = redisHealthDO.getRow("RedisHealthParams", criteria);
                    if (row3 == null) {
                        row3 = new Row("RedisHealthParams");
                        row3.set("RPARAM_NAME", (Object)"TotalRedisConnectionFailures");
                        row3.set("RPARAM_VALUE", (Object)RedisErrorTracker.getRedisConnectionsErrors());
                        redisHealthDO.addRow(row3);
                    }
                    else {
                        row3.set("RPARAM_VALUE", (Object)RedisErrorTracker.getRedisConnectionsErrors());
                        redisHealthDO.updateRow(row3);
                    }
                }
                SyMUtil.getPersistence().update(redisHealthDO);
                QueueDataMETracking.logger.log(Level.INFO, "Queuewise Redis Errors currently in memory:" + getRedisQueueDetailsJSON());
                QueueDataMETracking.logger.log(Level.INFO, "Redis Server Errors currently in memory:" + RedisErrorTracker.getRedisConnectionsErrors());
            }
            catch (final Exception e) {
                QueueDataMETracking.logger.log(Level.INFO, "Exception while update error details to DB", e);
            }
            QueueDataMETracking.logger.log(Level.INFO, "End of Redis Health Params to DB");
        }
        QueueDataMETracking.lastUpdateTimetoDB = System.currentTimeMillis();
        QueueDataMETracking.nextUpdateTime = QueueDataMETracking.lastUpdateTimetoDB + QueueDataMETracking.updateForEvery;
        addBlockedQueueMETrack();
        storeMeTrackDataInFile();
    }
    
    public static void updateBackupFailureInDB() {
        try {
            final DataObject redisHealthDO = RedisErrorTracker.getRedisHealthParamDO((Criteria)null);
            if (redisHealthDO != null && !redisHealthDO.isEmpty()) {
                Criteria criteria = null;
                criteria = new Criteria(new Column("RedisHealthParams", "RPARAM_NAME"), (Object)"TotalRedisBackupFailure", 0);
                Row row = redisHealthDO.getRow("RedisHealthParams", criteria);
                if (row != null) {
                    int noOfBackUpFailiure = Integer.parseInt(row.get("RPARAM_VALUE").toString());
                    ++noOfBackUpFailiure;
                    row.set("RPARAM_VALUE", (Object)noOfBackUpFailiure);
                    redisHealthDO.updateRow(row);
                }
                else {
                    row = new Row("RedisHealthParams");
                    row.set("RPARAM_NAME", (Object)"TotalRedisBackupFailure");
                    row.set("RPARAM_VALUE", (Object)"1");
                    redisHealthDO.addRow(row);
                }
                SyMUtil.getPersistence().update(redisHealthDO);
            }
        }
        catch (final Exception e) {
            QueueDataMETracking.logger.log(Level.WARNING, "Exception while updating backup failure details in db", e);
        }
    }
    
    public static void checkAndUpdateDB() {
        final long currentTime = System.currentTimeMillis();
        if (QueueDataMETracking.lastUpdateTimetoDB == 0L || currentTime >= QueueDataMETracking.nextUpdateTime) {
            updateDB();
        }
    }
    
    private static void storeMeTrackDataInFile() {
        final File jsonfile = new File(System.getProperty("server.home") + File.separator + "logs" + File.separator + "ServerQueueSize.json");
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(jsonfile);
            fileWriter.write(getTrackingSummaryJson());
            fileWriter.flush();
        }
        catch (final Exception e) {
            QueueDataMETracking.logger.log(Level.WARNING, "Error storeMeTrackDataInFile", e);
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            }
            catch (final IOException e2) {
                QueueDataMETracking.logger.log(Level.WARNING, "Error storeMeTrackDataInFile", e2);
            }
        }
        finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            }
            catch (final IOException e3) {
                QueueDataMETracking.logger.log(Level.WARNING, "Error storeMeTrackDataInFile", e3);
            }
        }
    }
    
    public static String getQueueSizeMeTrackData() {
        final File jsonfile = new File(System.getProperty("server.home") + File.separator + "logs" + File.separator + "ServerQueueSize.json");
        BufferedReader reader = null;
        String line = null;
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(jsonfile));
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        }
        catch (final Exception e) {
            QueueDataMETracking.logger.log(Level.WARNING, "Error while getQueueSizeMeTrackData", e);
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (final IOException e2) {
                QueueDataMETracking.logger.log(Level.WARNING, "Error while getQueueSizeMeTrackData", e2);
            }
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (final IOException e3) {
                QueueDataMETracking.logger.log(Level.WARNING, "Error while getQueueSizeMeTrackData", e3);
            }
        }
        return null;
    }
    
    public static void initializeTrackingMap() {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DCQueueMetaData"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator iterator = dataObject.getRows("DCQueueMetaData");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                if (row != null) {
                    final String qName = (String)row.get("QUEUE_NAME");
                    final String qTable = (String)row.get("QUEUE_TABLE_NAME");
                    if (Boolean.parseBoolean(SyMUtil.getSyMParameter("enableRedis"))) {
                        final long val = ((Hashtable<K, Long>)RedisQueueUtil.getRedisQueueDetails(qName)).get("totalCount");
                        QueueDataMETracking.queueTrackingMap.put(qName + "_current", new AtomicLong(val));
                        QueueDataMETracking.queueTrackingMap.put(qName + "_Max", new AtomicLong(val));
                    }
                    else {
                        DataSet ds = null;
                        Connection conn = null;
                        try {
                            if (qTable.equalsIgnoreCase("dcqueuedummytable")) {}
                            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable(qTable));
                            final Column countCal = new Column((String)null, "*").count();
                            sq.addSelectColumn(countCal);
                            conn = RelationalAPI.getInstance().getConnection();
                            ds = RelationalAPI.getInstance().executeQuery((Query)sq, conn);
                            while (ds.next()) {
                                QueueDataMETracking.queueTrackingMap.put(qName + "_current", new AtomicLong(ds.getAsLong(1)));
                                QueueDataMETracking.queueTrackingMap.put(qName + "_Max", new AtomicLong(ds.getAsLong(1)));
                            }
                        }
                        finally {
                            try {
                                if (conn != null) {
                                    conn.close();
                                }
                                if (ds != null) {
                                    ds.close();
                                }
                            }
                            catch (final SQLException e) {
                                QueueDataMETracking.logger.log(Level.WARNING, "Error while initilaize TrackingMap", e);
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception e2) {
            QueueDataMETracking.logger.log(Level.WARNING, "Error while initilaize TrackingMap", e2);
        }
    }
    
    public static void incrementTrackingMap(final String qName) {
        try {
            synchronized (QueueDataMETracking.class) {
                if (!QueueDataMETracking.queueTrackingMap.containsKey(qName + "_current") || !QueueDataMETracking.queueTrackingMap.containsKey(qName + "_Max")) {
                    QueueDataMETracking.queueTrackingMap.put(qName + "_current", new AtomicLong());
                    QueueDataMETracking.queueTrackingMap.put(qName + "_Max", new AtomicLong());
                }
            }
            QueueDataMETracking.queueTrackingMap.get(qName + "_Max").getAndAccumulate(QueueDataMETracking.queueTrackingMap.get(qName + "_current").incrementAndGet(), Math::max);
        }
        catch (final Exception e) {
            QueueDataMETracking.logger.log(Level.SEVERE, "Error while incrementTrackingMap", e);
        }
    }
    
    public static void decrementTrackingMap(final String qName) {
        try {
            synchronized (QueueDataMETracking.class) {
                if (!QueueDataMETracking.queueTrackingMap.containsKey(qName + "_current") || !QueueDataMETracking.queueTrackingMap.containsKey(qName + "_Max")) {
                    QueueDataMETracking.queueTrackingMap.put(qName + "_current", new AtomicLong());
                    QueueDataMETracking.queueTrackingMap.put(qName + "_Max", new AtomicLong());
                }
            }
            QueueDataMETracking.queueTrackingMap.get(qName + "_current").decrementAndGet();
        }
        catch (final Exception e) {
            QueueDataMETracking.logger.log(Level.SEVERE, "Error while decrementTrackingMap", e);
        }
    }
    
    public static String getTrackingSummaryJson() {
        return String.valueOf(new JSONObject((Map)QueueDataMETracking.queueTrackingMap));
    }
    
    private static void getAdditionThresholdTime() {
        final File jsonFile = new File(System.getProperty("server.home") + File.separator + "conf" + File.separator + "DeviceManagementFramework" + File.separator + "configurations" + File.separator + "framework_settings_dc_1.json");
        try {
            final JSONObject settingsjson = new JSONObject(FileUtils.readFileToString(jsonFile, "UTF-8"));
            if (settingsjson.keySet().contains("queue_process") && settingsjson.getJSONObject("queue_process").keySet().contains("average_addition_time_threshold_metrack")) {
                QueueDataMETracking.thresholdTime = settingsjson.getJSONObject("queue_process").getLong("average_addition_time_threshold_metrack");
            }
            else {
                QueueDataMETracking.logger.log(Level.SEVERE, "The required key for thresholdTime is not present in " + jsonFile.getAbsolutePath());
            }
        }
        catch (final Exception e) {
            QueueDataMETracking.logger.log(Level.SEVERE, "Error while reading [" + jsonFile.getAbsolutePath() + "] file for getting queue AdditionThresholdTime", e);
        }
        finally {
            if (QueueDataMETracking.thresholdTime == 0L) {
                QueueDataMETracking.thresholdTime = 3L;
                QueueDataMETracking.logger.log(Level.INFO, "Since thresholdTime is not set from file, default value of 3 seconds is set.");
            }
        }
    }
    
    public static void addBlockedQueueMETrack() {
        final Map<String, Integer> blockedQueueMap = DefaultDCQueue.getQueueBlockedSummary();
        for (final Map.Entry<String, Integer> entry : blockedQueueMap.entrySet()) {
            final String queueName = entry.getKey();
            final Integer blockedCount = entry.getValue();
            QueueDataMETracking.queueTrackingMap.put(queueName + "_blocked", new AtomicLong(blockedCount));
        }
    }
    
    static {
        MEDIUM_THRESHOLD = 15;
        HIGH_THRESHOLD = 25;
        QueueDataMETracking.queueDataMETracking = null;
        QueueDataMETracking.lastUpdateTimetoDB = 0L;
        QueueDataMETracking.updateForEvery = 300000L;
        QueueDataMETracking.nextUpdateTime = 0L + QueueDataMETracking.updateForEvery;
        QueueDataMETracking.thresholdTime = 0L;
        QueueDataMETracking.qAdditionTimeDetails = new HashMap<String, HashMap<String, AtomicLong>>();
        QueueDataMETracking.qPostingTimeData = new LinkedList();
        QueueDataMETracking.logger = Logger.getLogger("RedisTrackerLogger");
        QueueDataMETracking.redisQueueDetails = new HashMap();
        QueueDataMETracking.queueTrackingMap = new HashMap<String, AtomicLong>();
        getAdditionThresholdTime();
    }
}
