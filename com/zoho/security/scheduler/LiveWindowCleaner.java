package com.zoho.security.scheduler;

import java.util.Iterator;
import com.adventnet.iam.security.LiveWindowThrottler;
import java.net.SocketTimeoutException;
import com.adventnet.iam.security.SecurityUtil;
import com.adventnet.iam.security.SecurityFrameworkUtil;
import com.zoho.security.cache.RedisCacheAPI;
import java.util.ArrayList;
import com.zoho.jedis.v320.exceptions.JedisException;
import java.util.logging.Level;
import com.zoho.security.cache.CacheConfiguration;
import java.util.List;
import java.util.Timer;
import java.util.logging.Logger;
import java.util.TimerTask;

public class LiveWindowCleaner extends TimerTask
{
    private static final Logger LOGGER;
    private static Timer timer;
    private static boolean scheduled;
    private final String serviceId;
    private final List<CacheConfiguration> cacheConfigurations;
    private final String serviceAppHostSetName;
    
    private LiveWindowCleaner(final String serviceId, final List<CacheConfiguration> cacheConfigurations) {
        this.serviceId = serviceId;
        this.cacheConfigurations = cacheConfigurations;
        this.serviceAppHostSetName = "LW_SAHS_" + serviceId;
    }
    
    public static synchronized ScheduleState schedule(final long delay, final long period, final String serviceId, final List<CacheConfiguration> liveWindowCacheConfigurations) {
        if (LiveWindowCleaner.scheduled) {
            return ScheduleState.SCHEDULED_ALREADY;
        }
        (LiveWindowCleaner.timer = new Timer()).schedule(new LiveWindowCleaner(serviceId, liveWindowCacheConfigurations), delay, period);
        LiveWindowCleaner.LOGGER.log(Level.INFO, "TimerTask: {0}, State: Scheduled, Delay (ms): {1}, Period (ms): {2}", new Object[] { LiveWindowCleaner.class.getName(), delay, period });
        return ScheduleState.SCHEDULED_NOW;
    }
    
    @Override
    public void run() {
        LiveWindowCleaner.LOGGER.log(Level.INFO, "TimerTask: {0}, State: started", new Object[] { LiveWindowCleaner.class.getName() });
        final long startTime = System.currentTimeMillis();
        try {
            this.runTask();
        }
        catch (final JedisException e) {
            LiveWindowCleaner.LOGGER.log(Level.SEVERE, "JedisException occurred while running the {0} TimerTask. Exception: {1}", new Object[] { LiveWindowCleaner.class.getName(), e.getMessage() });
        }
        catch (final Exception e2) {
            LiveWindowCleaner.LOGGER.log(Level.SEVERE, "Exception occurred while running the {0} TimerTask. Exception: {1}", new Object[] { LiveWindowCleaner.class.getName(), e2.getMessage() });
        }
        LiveWindowCleaner.LOGGER.log(Level.INFO, "TimerTask: {0}, State: completed, ProcessingTime (ms): {1}", new Object[] { LiveWindowCleaner.class.getName(), System.currentTimeMillis() - startTime });
    }
    
    private void runTask() throws JedisException {
        final List<String> invalidAppHostIPs = new ArrayList<String>();
        final CacheConfiguration firstCacheConfiguration = this.cacheConfigurations.get(0);
        for (final String appHostIP : RedisCacheAPI.getDataSet(this.serviceAppHostSetName, firstCacheConfiguration)) {
            try {
                final String serviceName = SecurityUtil.convertInputStreamAsString(SecurityFrameworkUtil.getURLConnection("http://" + appHostIP + ":8080" + "/getzsecservicename", null, "GET").getInputStream(), -1L);
                if (this.serviceId.equals(serviceName)) {
                    continue;
                }
                invalidAppHostIPs.add(appHostIP);
            }
            catch (final SocketTimeoutException e) {
                if (e.getMessage().contains("connect timed out")) {
                    invalidAppHostIPs.add(appHostIP);
                }
                LiveWindowCleaner.LOGGER.log(Level.INFO, "SocketTimeoutException occurred while connecting the app host IP: {0}, Exception: {1}", new Object[] { appHostIP, e.getMessage() });
            }
            catch (final Exception e2) {
                LiveWindowCleaner.LOGGER.log(Level.INFO, "Exception occurred while connecting the app host IP: {0}, Exception: {1}", new Object[] { appHostIP, e2.getMessage() });
            }
        }
        long cleanedLWCount = 0L;
        for (final String invalidAppHostIP : invalidAppHostIPs) {
            cleanedLWCount += LiveWindowThrottler.cleanAppServerLiveWindowCountFromAllCache(this.serviceId, invalidAppHostIP, this.cacheConfigurations);
        }
        if (invalidAppHostIPs.size() > 0) {
            final String[] invalidAppHostIPsArray = new String[invalidAppHostIPs.size()];
            int i = 0;
            for (final String invalidAppHostIP2 : invalidAppHostIPs) {
                invalidAppHostIPsArray[i++] = invalidAppHostIP2;
            }
            RedisCacheAPI.removeDataFromSet(this.serviceAppHostSetName, invalidAppHostIPsArray, firstCacheConfiguration);
        }
        LiveWindowCleaner.LOGGER.log(Level.INFO, "CleanedLiveWindowCount: {0}, InvalidAppHostIPs: {1}", new Object[] { cleanedLWCount, invalidAppHostIPs });
    }
    
    static {
        LOGGER = Logger.getLogger(LiveWindowCleaner.class.getName());
    }
    
    private enum ScheduleState
    {
        SCHEDULED_NOW, 
        SCHEDULED_ALREADY;
    }
}
