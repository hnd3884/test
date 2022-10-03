package com.me.devicemanagement.framework.server.redis;

import org.json.JSONObject;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.me.devicemanagement.framework.server.util.CommonUtils;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;

public class RedisServerUtil
{
    public static final String REDIS_SETTINGS_CONF_FILE;
    public static final String REDIS_CONF_FILE;
    public static final String REDIS_TEMPLATE_CONF_FILE;
    public static final String REDIS_DUMP_FILE_LOC;
    public static final String REDIS_EXE_FILENAME = "dmredis-server.exe";
    public static final String REDIS_EXE;
    public static final String REDIS_DATA_LOAD_TIMEOUT = "redis.data.load.timeout";
    public static final String REDIS_CONNECTIONS_LIMIT = "redis.connection.limit";
    public static final String REDIS_QUEUE_TIMEOUT = "redis.queue.timeout";
    public static final String REDIS_SHUTDOWN_TIMEOUT = "redis.shutdown.timeout";
    public static final String REDIS_PORT = "redis.port";
    public static final String REDIS_PORT_RANGE = "redis.port.range";
    public static final String REDIS_MAX_BULK_SIZE = "max.bulk.size";
    public static final String REDIS_LOG_LEVEL = "redis.log.level";
    public static final String REDIS_MAX_MEMORY = "redis.maxmemory";
    public static final String REDIS_MAX_LENGTH = "redis.file.maxlength";
    public static final String REDIS_MAX_CONNECTION = "redis.maxconnections";
    public static final String DEFAULT_REDIS_PORT = "6379";
    public static final Long DEFAULT_REDIS_SHUTDOWN_TIMEOUT;
    public static final Long DEFAULT_REDIS_DATA_LOAD_TIMEOUT;
    public static final String DEFAULT_REDIS_LOG_LEVEL = "verbose";
    public static final Integer DEFAULT_REDIS_MAX_BULK_SIZE;
    public static final String DEFAULT_REDIS_MAX_MEMORY = "1GB";
    public static final String DEFAULT_REDIS_MAX_CONNECTION = "10000";
    public static final String KEY_PREFIX_SUFFIX = "%";
    public static final String REDIS_HOST = "localhost";
    private static String queueProcessConfig;
    private static String sourceClass;
    public static final String SERVER_IP = "server.ip";
    public static final String LOCAL_HOST = "localhost";
    public static Logger logger;
    public static Logger redisLogger;
    public static int redisInstallationStatus;
    public static long redisPID;
    public static long redisMaxMemory;
    public static int redisMaxFileLength;
    public static boolean isRedisFileWriteEnabled;
    public static int maxBulkSize;
    
    public static String getPasswordFromDBorCache() {
        String password = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("REDIS_AUTHPW");
        try {
            if (password == null || password.equalsIgnoreCase("")) {
                final DataObject credentialObj = SyMUtil.getPersistence().get("RedisCredential", (Criteria)null);
                if (credentialObj != null && !credentialObj.isEmpty()) {
                    password = (String)credentialObj.getFirstValue("RedisCredential", "REDIS_PASSWORD");
                    password = ApiFactoryProvider.getCryptoAPI().decrypt(password, 8);
                }
            }
        }
        catch (final Exception e) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Exception while getting redis authentication password :", e);
        }
        return password;
    }
    
    public static int getRedisPort() {
        int port = Integer.parseInt("6379");
        try {
            final Properties rProps = getRedisServerProperties();
            if (rProps != null) {
                port = Integer.parseInt(rProps.getProperty("redis.port"));
            }
        }
        catch (final Exception e) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Exception while checking redis connection", e);
        }
        return port;
    }
    
    public static int getMaxBulkSize() {
        int maxBulk = RedisServerUtil.DEFAULT_REDIS_MAX_BULK_SIZE;
        try {
            final Properties rProps = getRedisServerProperties();
            if (rProps != null) {
                maxBulk = Integer.parseInt(rProps.getProperty("max.bulk.size"));
            }
        }
        catch (final Exception e) {
            RedisServerUtil.redisLogger.log(Level.SEVERE, "Exception while getting max bulk size", e);
        }
        return maxBulk;
    }
    
    public static Properties getProperties(final String confFileName) {
        final Properties props = new Properties();
        FileInputStream fis = null;
        try {
            if (new File(confFileName).exists()) {
                fis = new FileInputStream(confFileName);
                props.load(fis);
                fis.close();
            }
        }
        catch (final Exception ex) {
            RedisServerUtil.logger.log(Level.SEVERE, "Caught exception: ", ex);
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception ex) {
                RedisServerUtil.logger.log(Level.SEVERE, "Caught exception: ", ex);
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception ex2) {
                RedisServerUtil.logger.log(Level.SEVERE, "Caught exception: ", ex2);
            }
        }
        return props;
    }
    
    public static Properties getRedisServerProperties() {
        Properties redisProps = new Properties();
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String confFile = serverHome + File.separator + RedisServerUtil.REDIS_SETTINGS_CONF_FILE;
            redisProps = getProperties(confFile);
        }
        catch (final Exception e) {
            RedisServerUtil.redisLogger.log(Level.SEVERE, "Caught exception while getting redis properties : " + e);
        }
        return redisProps;
    }
    
    private static long getRedisMaxMemory() {
        final Properties probs = getRedisServerProperties();
        final String maxmemory = getRedisPropertyValue(probs, "redis.maxmemory");
        final long memoryInBytes = CommonUtils.convertReadableSizeToBytes(maxmemory);
        return memoryInBytes;
    }
    
    private static int getMaxFileLength() {
        final Properties probs = getRedisServerProperties();
        final String maxFileLength = getRedisPropertyValue(probs, "redis.file.maxlength");
        return Integer.parseInt(maxFileLength);
    }
    
    private static String getRedisPropertyValue(final Properties props, final String key) {
        if (props.containsKey(key)) {
            return props.getProperty(key);
        }
        return null;
    }
    
    static {
        REDIS_SETTINGS_CONF_FILE = "conf" + File.separator + "redis_settings.conf";
        REDIS_CONF_FILE = "redis" + File.separator + "conf" + File.separator + "redis.windows.conf";
        REDIS_TEMPLATE_CONF_FILE = "redis" + File.separator + "conf" + File.separator + "redis.windows.conf.template";
        REDIS_DUMP_FILE_LOC = "redis" + File.separator + "data" + File.separator + "dump.rdb";
        REDIS_EXE = "redis" + File.separator + "bin" + File.separator + "dmredis-server.exe";
        DEFAULT_REDIS_SHUTDOWN_TIMEOUT = 10000L;
        DEFAULT_REDIS_DATA_LOAD_TIMEOUT = 30000L;
        DEFAULT_REDIS_MAX_BULK_SIZE = 1;
        RedisServerUtil.queueProcessConfig = "queue_process";
        RedisServerUtil.sourceClass = "RedisServerUtil";
        RedisServerUtil.logger = Logger.getLogger(RedisServerUtil.class.getName());
        RedisServerUtil.redisLogger = Logger.getLogger("RedisLogger");
        RedisServerUtil.redisInstallationStatus = -1;
        RedisServerUtil.redisPID = -1L;
        RedisServerUtil.redisMaxMemory = getRedisMaxMemory();
        RedisServerUtil.redisMaxFileLength = getMaxFileLength();
        RedisServerUtil.maxBulkSize = getMaxBulkSize();
        try {
            final JSONObject frameworkConfigurations = FrameworkConfigurations.getFrameworkConfigurations();
            if (frameworkConfigurations.get(RedisServerUtil.queueProcessConfig) != null) {
                RedisServerUtil.isRedisFileWriteEnabled = Boolean.parseBoolean(((JSONObject)frameworkConfigurations.get(RedisServerUtil.queueProcessConfig)).get("enable_file_write_in_Redis").toString());
            }
        }
        catch (final Exception ex) {
            RedisServerUtil.logger.log(Level.WARNING, "Exception while handling framework configuration json ", ex);
        }
    }
}
