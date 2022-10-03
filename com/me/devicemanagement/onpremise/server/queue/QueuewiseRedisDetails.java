package com.me.devicemanagement.onpremise.server.queue;

import com.adventnet.ds.query.Column;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import org.json.simple.JSONObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.redis.RedisErrorTracker;
import java.util.logging.Level;
import org.json.simple.parser.JSONParser;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class QueuewiseRedisDetails
{
    private static final String TOTAL_ADDITION = "TotalAddition";
    private static final String TOTAL_FAILURES = "TotalFailures";
    private static final String FIRST_FAILURE_TIME = "FirstFailureTime";
    private static final String LAST_FAILURE_TIME = "LastFailureTime";
    private static final String LAST_SUCCESS_TIME = "LastSuccessTime";
    private static final String RESET_FAILURES = "ResetFailures";
    public static Logger redisLogger;
    public String queueName;
    public AtomicLong totalAddition;
    public AtomicLong totalFailures;
    public AtomicLong firstFailureTime;
    public AtomicLong lastFailureTime;
    public AtomicLong lastSuccessTime;
    public AtomicLong resetFailures;
    
    public QueuewiseRedisDetails() {
        this.queueName = "";
        this.totalAddition = new AtomicLong();
        this.totalFailures = new AtomicLong();
        this.firstFailureTime = new AtomicLong();
        this.lastFailureTime = new AtomicLong();
        this.lastSuccessTime = new AtomicLong();
        this.resetFailures = new AtomicLong();
    }
    
    public void updateResetFailures(final String qName, final int count) {
        this.queueName = qName;
        this.resetFailures.addAndGet(count);
    }
    
    public void updateQueueAdditionDetails(final String qName, final Boolean status) {
        this.queueName = qName;
        this.totalAddition.incrementAndGet();
        if (!status) {
            this.totalFailures.incrementAndGet();
            this.firstFailureTime.compareAndSet(0L, System.currentTimeMillis());
            this.lastFailureTime.set(System.currentTimeMillis());
        }
        else {
            this.lastSuccessTime.set(System.currentTimeMillis());
        }
    }
    
    public static void loadRedisQueueDetails(final HashMap<String, QueuewiseRedisDetails> redisQueueDetails) {
        final JSONParser jsonParser = new JSONParser();
        QueuewiseRedisDetails.redisLogger.log(Level.INFO, "Going to load redis queue details for tracking");
        int i = 0;
        try {
            final DataObject redisHealthDO = RedisErrorTracker.getRedisHealthParamDO((Criteria)null);
            if (redisHealthDO != null && !redisHealthDO.isEmpty()) {
                final Iterator rows = redisHealthDO.getRows("RedisHealthParams");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    final String keyName = (String)row.get("RPARAM_NAME");
                    if (keyName.contains("METrack")) {
                        ++i;
                        final JSONObject obj = (JSONObject)jsonParser.parse((String)row.get("RPARAM_VALUE"));
                        final QueuewiseRedisDetails qDetailsobj = new QueuewiseRedisDetails();
                        qDetailsobj.queueName = (String)obj.get((Object)keyName);
                        qDetailsobj.totalAddition.set((long)obj.get((Object)"TotalAddition"));
                        qDetailsobj.totalFailures.set((long)obj.get((Object)"TotalFailures"));
                        qDetailsobj.firstFailureTime.set((long)obj.get((Object)"FirstFailureTime"));
                        qDetailsobj.lastFailureTime.set((long)obj.get((Object)"LastFailureTime"));
                        qDetailsobj.lastSuccessTime.set((long)obj.get((Object)"LastSuccessTime"));
                        if (obj.containsKey((Object)"ResetFailures")) {
                            qDetailsobj.resetFailures.set((long)obj.get((Object)"ResetFailures"));
                        }
                        redisQueueDetails.put(keyName, qDetailsobj);
                    }
                }
            }
        }
        catch (final Exception e) {
            QueuewiseRedisDetails.redisLogger.log(Level.WARNING, "Exception while loading first time data", e);
        }
        QueuewiseRedisDetails.redisLogger.log(Level.INFO, "Loaded " + i + "redis queue details for tracking");
    }
    
    public void loadFirstTimeData(final String keyName) {
        QueuewiseRedisDetails.redisLogger.log(Level.INFO, "First time load for queue " + keyName);
        final JSONParser jsonParser = new JSONParser();
        try {
            final Criteria criteria = new Criteria(new Column("RedisHealthParams", "RPARAM_NAME"), (Object)keyName, 0);
            final DataObject redisHealthDO = RedisErrorTracker.getRedisHealthParamDO(criteria);
            if (redisHealthDO != null && !redisHealthDO.isEmpty()) {
                final Row row = redisHealthDO.getFirstRow("RedisHealthParams");
                if (row != null) {
                    final JSONObject obj = (JSONObject)jsonParser.parse((String)row.get("RPARAM_VALUE"));
                    this.queueName = keyName;
                    this.totalAddition.set((long)obj.get((Object)"TotalAddition"));
                    this.totalFailures.set((long)obj.get((Object)"TotalFailures"));
                    this.firstFailureTime.set((long)obj.get((Object)"FirstFailureTime"));
                    this.lastFailureTime.set((long)obj.get((Object)"LastFailureTime"));
                    this.lastSuccessTime.set((long)obj.get((Object)"LastSuccessTime"));
                    if (obj.containsKey((Object)"ResetFailures")) {
                        this.resetFailures.set((long)obj.get((Object)"ResetFailures"));
                    }
                }
            }
        }
        catch (final Exception e) {
            QueuewiseRedisDetails.redisLogger.log(Level.WARNING, "Exception while loading first time data", e);
        }
    }
    
    public String getJSONString() {
        final JSONObject obj = new JSONObject();
        try {
            obj.put((Object)"TotalAddition", (Object)this.totalAddition);
            obj.put((Object)"TotalFailures", (Object)this.totalFailures);
            obj.put((Object)"FirstFailureTime", (Object)this.firstFailureTime);
            obj.put((Object)"LastFailureTime", (Object)this.lastFailureTime);
            obj.put((Object)"LastSuccessTime", (Object)this.lastSuccessTime);
            obj.put((Object)"ResetFailures", (Object)this.resetFailures);
        }
        catch (final Exception e) {
            QueuewiseRedisDetails.redisLogger.log(Level.WARNING, "Exception while getting queuewise failure details");
        }
        return obj.toString();
    }
    
    static {
        QueuewiseRedisDetails.redisLogger = Logger.getLogger("RedisLogger");
    }
}
