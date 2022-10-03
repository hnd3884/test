package com.me.devicemanagement.framework.server.redis;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.simple.parser.JSONParser;
import java.util.logging.Logger;

public class RedisErrorTracker
{
    protected static final String TOTAL_REDIS_CONNECTION_FAILURES = "TotalRedisConnectionFailures";
    protected static final String TOTAL_REDIS_FAILURES = "TotalRedisFailures";
    protected static final String POOL_INSUFFICIENT = "PoolInsufficient";
    protected static final String REDIS_NOT_RUNNING = "RedisNotRunning";
    protected static final String NON_POOL_FAILURES = "NonPoolFailures";
    protected static final String OTHERS = "Others";
    protected static final String SHUTDOWN_COUNT = "ShutdownCount";
    protected static long totalRedisFailures;
    protected static long poolInsufficient;
    protected static long redisNotRunning;
    protected static long others;
    public static long shutdownCount;
    private static Logger redisLogger;
    
    public static void initRedisErrors() {
        final JSONParser jsonParser = new JSONParser();
        try {
            final Criteria criteria = new Criteria(new Column("RedisHealthParams", "RPARAM_NAME"), (Object)"TotalRedisConnectionFailures", 0);
            final DataObject redisHealthDO = getRedisHealthParamDO(criteria);
            if (redisHealthDO != null && !redisHealthDO.isEmpty()) {
                final Row row = redisHealthDO.getFirstRow("RedisHealthParams");
                if (row != null) {
                    final JSONObject obj = (JSONObject)jsonParser.parse((String)row.get("RPARAM_VALUE"));
                    RedisErrorTracker.totalRedisFailures = (long)obj.get((Object)"TotalRedisFailures");
                    RedisErrorTracker.poolInsufficient = (long)obj.get((Object)"PoolInsufficient");
                    RedisErrorTracker.redisNotRunning = (long)obj.get((Object)"RedisNotRunning");
                    RedisErrorTracker.others = (long)obj.get((Object)"Others");
                    RedisErrorTracker.shutdownCount = (long)obj.get((Object)"ShutdownCount");
                    RedisErrorTracker.totalRedisFailures += RedisErrorTracker.shutdownCount;
                }
            }
        }
        catch (final Exception e) {
            RedisErrorTracker.redisLogger.log(Level.WARNING, "Exception while initializing redis errors", e);
        }
    }
    
    public static DataObject getRedisHealthParamDO(final Criteria criteria) {
        DataObject redisHealthDO = null;
        try {
            redisHealthDO = SyMUtil.getPersistence().get("RedisHealthParams", criteria);
        }
        catch (final Exception e) {
            RedisErrorTracker.redisLogger.log(Level.WARNING, "Exception while fetching redisHealthDO", e);
        }
        return redisHealthDO;
    }
    
    public static String getRedisConnectionsErrors() {
        final JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put((Object)"TotalRedisFailures", (Object)RedisErrorTracker.totalRedisFailures);
            jsonObj.put((Object)"PoolInsufficient", (Object)RedisErrorTracker.poolInsufficient);
            jsonObj.put((Object)"RedisNotRunning", (Object)RedisErrorTracker.redisNotRunning);
            jsonObj.put((Object)"Others", (Object)RedisErrorTracker.others);
            jsonObj.put((Object)"ShutdownCount", (Object)RedisErrorTracker.shutdownCount);
        }
        catch (final Exception ex) {
            RedisErrorTracker.redisLogger.log(Level.WARNING, "Exception while getting connection errors", ex);
        }
        return jsonObj.toString();
    }
    
    static {
        RedisErrorTracker.totalRedisFailures = 0L;
        RedisErrorTracker.poolInsufficient = 0L;
        RedisErrorTracker.redisNotRunning = 0L;
        RedisErrorTracker.others = 0L;
        RedisErrorTracker.shutdownCount = 0L;
        RedisErrorTracker.redisLogger = Logger.getLogger("RedisTrackerLogger");
    }
}
