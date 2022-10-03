package com.me.devicemanagement.onpremise.server.redis;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RedisErrorTracker extends com.me.devicemanagement.framework.server.redis.RedisErrorTracker
{
    private static Logger redisLogger;
    
    public static synchronized void logRedisErrors(final Exception e) {
        try {
            final Boolean redisErrorShutdown = Boolean.valueOf(System.getProperty("RedisErrorShutdown"));
            if (!redisErrorShutdown) {
                ++RedisErrorTracker.totalRedisFailures;
                final String initMsg = e.getMessage();
                final Throwable cause = e.getCause();
                String message = initMsg;
                if (cause != null) {
                    message = e.getCause().getMessage();
                }
                RedisErrorTracker.redisLogger.log(Level.WARNING, "Stack trace causing exception", e);
                if (initMsg != null && message != null) {
                    if (initMsg.equalsIgnoreCase("Could not get a resource from the pool")) {
                        if (message.equalsIgnoreCase("timeout waiting for idle object")) {
                            ++RedisErrorTracker.poolInsufficient;
                        }
                        else if (message.equalsIgnoreCase("java.net.ConnectException: Connection refused: connect")) {
                            ++RedisErrorTracker.redisNotRunning;
                        }
                        else {
                            ++RedisErrorTracker.others;
                        }
                    }
                    else {
                        ++RedisErrorTracker.others;
                    }
                    final String state = RedisServerUtil.checkAndKillRedis.getState().name();
                    RedisErrorTracker.redisLogger.log(Level.INFO, "Thread State :" + state);
                    if (state.equalsIgnoreCase("NEW")) {
                        RedisErrorTracker.redisLogger.log(Level.INFO, "First Time starting this thread");
                        RedisServerUtil.checkAndKillRedis.start();
                    }
                    else if (state.equalsIgnoreCase("TERMINATED")) {
                        RedisErrorTracker.redisLogger.log(Level.INFO, "Previous thread has been terminated once :" + state);
                        (RedisServerUtil.checkAndKillRedis = new RedisServerUtil.CheckAndKillRedis()).start();
                    }
                    RedisErrorTracker.redisLogger.log(Level.INFO, "Errors :" + getRedisConnectionsErrors());
                }
            }
        }
        catch (final Exception ex) {
            RedisErrorTracker.redisLogger.log(Level.WARNING, "Exception while logging redis errors", ex);
        }
    }
    
    static {
        RedisErrorTracker.redisLogger = Logger.getLogger("RedisTrackerLogger");
    }
}
