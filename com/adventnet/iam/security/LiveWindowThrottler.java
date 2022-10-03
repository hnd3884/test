package com.adventnet.iam.security;

import java.util.Map;
import java.util.Iterator;
import com.zoho.jedis.v320.exceptions.JedisException;
import java.util.logging.Level;
import java.util.List;
import com.zoho.security.cache.RedisCacheAPI;
import com.zoho.security.cache.RedisLuaScript;
import com.zoho.security.cache.CacheConfiguration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class LiveWindowThrottler
{
    private static final Logger LOGGER;
    
    public static boolean accessEnter(final String throttleKey, final LiveWindowThrottleRule liveThrottleRule) {
        InMemCacheAccessInfo.LIVE_WINDOW_CACHE.putIfAbsent(throttleKey, new AtomicInteger(0));
        return InMemCacheAccessInfo.LIVE_WINDOW_CACHE.get(throttleKey).incrementAndGet() <= liveThrottleRule.getThreshold();
    }
    
    public static boolean accessEnter(final String serviceID, final String appServerID, final String throttleKey, final LiveWindowThrottleRule liveThrottleRule, final CacheConfiguration cacheConfiguration) {
        final String serviceLWMapName = "LW_SM_" + serviceID;
        final String appServerLWMapName = "LW_SAM_" + serviceID + "_" + appServerID;
        return (long)RedisCacheAPI.evalSHA(RedisLuaScript.LIVE_WINDOW_ACCESS_ENTER_HANDLER_SCRIPT, 3, new String[] { serviceLWMapName, appServerLWMapName, throttleKey }, cacheConfiguration) <= liveThrottleRule.getThreshold();
    }
    
    public static void accessExit(final String throttleKey) {
        InMemCacheAccessInfo.LIVE_WINDOW_CACHE.get(throttleKey).decrementAndGet();
    }
    
    public static void accessExit(final String serviceID, final String appServerID, final String throttleKey, final CacheConfiguration cacheConfiguration) {
        final String serviceLWMapName = "LW_SM_" + serviceID;
        final String appServerLWMapName = "LW_SAM_" + serviceID + "_" + appServerID;
        RedisCacheAPI.evalSHA(RedisLuaScript.LIVE_WINDOW_ACCESS_EXIT_HANDLER_SCRIPT, 3, new String[] { serviceLWMapName, appServerLWMapName, throttleKey }, cacheConfiguration);
    }
    
    public static void init(final String serviceID, final String appServerID, final List<CacheConfiguration> cacheConfigurations) throws JedisException {
        final long startTime = System.currentTimeMillis();
        long cleanedLWCount = 0L;
        registerAppServer(serviceID, appServerID, cacheConfigurations.get(0));
        cleanedLWCount = cleanAppServerLiveWindowCountFromAllCache(serviceID, appServerID, cacheConfigurations);
        LiveWindowThrottler.LOGGER.log(Level.INFO, "Live window initialization completed. ServiceID: {0}, AppServerID: {1}, CleanedLiveWindowCount: {2}, ProcessingTime (ms): {3}", new Object[] { serviceID, appServerID, cleanedLWCount, System.currentTimeMillis() - startTime });
    }
    
    private static void registerAppServer(final String serviceID, final String appServerID, final CacheConfiguration cacheConfiguration) throws JedisException {
        final String serviceAppRegistrationSetName = "LW_SAHS_" + serviceID;
        RedisCacheAPI.addDataIntoSet(serviceAppRegistrationSetName, new String[] { appServerID }, cacheConfiguration);
    }
    
    public static long cleanAppServerLiveWindowCountFromAllCache(final String serviceID, final String appServerID, final List<CacheConfiguration> cacheConfigurations) throws JedisException {
        long cleanedLiveWindowCount = 0L;
        for (final CacheConfiguration cacheConfiguration : cacheConfigurations) {
            cleanedLiveWindowCount += cleanLiveWindowCount(serviceID, appServerID, cacheConfiguration);
        }
        return cleanedLiveWindowCount;
    }
    
    private static long cleanLiveWindowCount(final String serviceID, final String appServerID, final CacheConfiguration cacheConfiguration) throws JedisException {
        final String serviceLWMapName = "LW_SM_" + serviceID;
        final String appServerLWMapName = "LW_SAM_" + serviceID + "_" + appServerID;
        final Map<String, String> appServerLWMap = RedisCacheAPI.getDataMap(appServerLWMapName, cacheConfiguration);
        if (appServerLWMap.isEmpty()) {
            return 0L;
        }
        long cleanedLWCount = 0L;
        for (final String throttlesKey : appServerLWMap.keySet()) {
            final long count = Long.parseLong(appServerLWMap.get(throttlesKey));
            RedisCacheAPI.evalSHA(RedisLuaScript.LIVE_WINDOW_APP_SERVER_COUNT_CLEANER_SCRIPT, 3, new String[] { serviceLWMapName, throttlesKey, String.valueOf(count - count - count) }, cacheConfiguration);
            cleanedLWCount += count;
        }
        RedisCacheAPI.removeData(appServerLWMapName, cacheConfiguration);
        return cleanedLWCount;
    }
    
    static {
        LOGGER = Logger.getLogger(LiveWindowThrottler.class.getName());
    }
}
