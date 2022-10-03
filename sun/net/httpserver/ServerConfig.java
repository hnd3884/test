package sun.net.httpserver;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

class ServerConfig
{
    private static final int DEFAULT_CLOCK_TICK = 10000;
    private static final long DEFAULT_IDLE_INTERVAL = 30L;
    private static final int DEFAULT_MAX_IDLE_CONNECTIONS = 200;
    private static final long DEFAULT_MAX_REQ_TIME = -1L;
    private static final long DEFAULT_MAX_RSP_TIME = -1L;
    private static final long DEFAULT_TIMER_MILLIS = 1000L;
    private static final int DEFAULT_MAX_REQ_HEADERS = 200;
    private static final long DEFAULT_DRAIN_AMOUNT = 65536L;
    private static int clockTick;
    private static long idleInterval;
    private static long drainAmount;
    private static int maxIdleConnections;
    private static int maxReqHeaders;
    private static long maxReqTime;
    private static long maxRspTime;
    private static long timerMillis;
    private static boolean debug;
    private static boolean noDelay;
    
    static void checkLegacyProperties(final Logger logger) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                if (System.getProperty("sun.net.httpserver.readTimeout") != null) {
                    logger.warning("sun.net.httpserver.readTimeout property is no longer used. Use sun.net.httpserver.maxReqTime instead.");
                }
                if (System.getProperty("sun.net.httpserver.writeTimeout") != null) {
                    logger.warning("sun.net.httpserver.writeTimeout property is no longer used. Use sun.net.httpserver.maxRspTime instead.");
                }
                if (System.getProperty("sun.net.httpserver.selCacheTimeout") != null) {
                    logger.warning("sun.net.httpserver.selCacheTimeout property is no longer used.");
                }
                return null;
            }
        });
    }
    
    static boolean debugEnabled() {
        return ServerConfig.debug;
    }
    
    static long getIdleInterval() {
        return ServerConfig.idleInterval;
    }
    
    static int getClockTick() {
        return ServerConfig.clockTick;
    }
    
    static int getMaxIdleConnections() {
        return ServerConfig.maxIdleConnections;
    }
    
    static long getDrainAmount() {
        return ServerConfig.drainAmount;
    }
    
    static int getMaxReqHeaders() {
        return ServerConfig.maxReqHeaders;
    }
    
    static long getMaxReqTime() {
        return ServerConfig.maxReqTime;
    }
    
    static long getMaxRspTime() {
        return ServerConfig.maxRspTime;
    }
    
    static long getTimerMillis() {
        return ServerConfig.timerMillis;
    }
    
    static boolean noDelay() {
        return ServerConfig.noDelay;
    }
    
    static {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                ServerConfig.idleInterval = Long.getLong("sun.net.httpserver.idleInterval", 30L) * 1000L;
                ServerConfig.clockTick = Integer.getInteger("sun.net.httpserver.clockTick", 10000);
                ServerConfig.maxIdleConnections = Integer.getInteger("sun.net.httpserver.maxIdleConnections", 200);
                ServerConfig.drainAmount = Long.getLong("sun.net.httpserver.drainAmount", 65536L);
                ServerConfig.maxReqHeaders = Integer.getInteger("sun.net.httpserver.maxReqHeaders", 200);
                ServerConfig.maxReqTime = Long.getLong("sun.net.httpserver.maxReqTime", -1L);
                ServerConfig.maxRspTime = Long.getLong("sun.net.httpserver.maxRspTime", -1L);
                ServerConfig.timerMillis = Long.getLong("sun.net.httpserver.timerMillis", 1000L);
                ServerConfig.debug = Boolean.getBoolean("sun.net.httpserver.debug");
                ServerConfig.noDelay = Boolean.getBoolean("sun.net.httpserver.nodelay");
                return null;
            }
        });
    }
}
