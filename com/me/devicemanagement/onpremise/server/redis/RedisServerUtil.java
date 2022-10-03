package com.me.devicemanagement.onpremise.server.redis;

import java.util.Hashtable;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.taskengine.Scheduler;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.FileUtil;
import org.apache.commons.io.FileUtils;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.net.ServerSocket;
import com.adventnet.tools.prevalent.ConsoleOut;
import com.me.devicemanagement.framework.server.redis.RedisErrorTracker;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import java.util.Random;
import org.apache.commons.lang.RandomStringUtils;
import java.security.SecureRandom;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.queue.RedisQueueUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Scanner;
import java.io.InputStream;
import java.util.Arrays;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.me.devicemanagement.onpremise.server.queue.QueueDataMETracking;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisConnectionException;
import java.util.logging.Level;
import java.io.File;

public class RedisServerUtil extends com.me.devicemanagement.framework.server.redis.RedisServerUtil
{
    private static String sourceClass;
    private static final String REDIS_BACKUP_DATE_FORMAT = "MMM-dd-yyyy-HH-mm";
    private static final String REDIS_BACKUP_ENABLED = "redis.periodic.backup.enabled";
    private static final String REDIS_BACKUP_LOCATION = "redis.periodic.backup.location";
    private static final String REDIS_NUMBER_OF_BACKUPS = "redis.periodic.numberofbackups";
    private static final String REDIS_BACKUP_INTERVAL = "redis.periodic.backup.interval";
    private static final String REDIS_SOURCE_DIRECTORY = "redis.source.Directory";
    private static final String REDIS_AUTO_RESTORE_ENABLED = "redis.auto.restore.enabled";
    private static final String REDIS_RESTORE_BACKUP_LASTINDEX = "redis.restore.backup.lastindex";
    public static CheckAndKillRedis checkAndKillRedis;
    
    public static void initRedisFolders() {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String redisHome = serverHome + File.separator + "redis";
            final String dataDirPath = redisHome + File.separator + "data";
            final String logsDirPath = redisHome + File.separator + "logs";
            final File dataDir = new File(dataDirPath);
            if (!dataDir.exists()) {
                dataDir.mkdir();
            }
            final File logDir = new File(logsDirPath);
            if (!logDir.exists()) {
                logDir.mkdir();
            }
        }
        catch (final Exception e) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Exception while initializing redis folders", e);
        }
    }
    
    private static boolean waitAndCheckConnectionUntilTimeOut(final long timeOut, final long split) {
        final long startTime = System.currentTimeMillis();
        final boolean isJedisRunning = false;
        try {
            for (long i = 0L; i < timeOut; i += split, ++i) {
                if (checkRedisConnection()) {
                    final long totalTime = (System.currentTimeMillis() - startTime) / 1000L;
                    RedisServerUtil.redisLogger.log(Level.INFO, "Received connection from redis after " + totalTime + " secs");
                    return true;
                }
                Thread.sleep(split);
            }
        }
        catch (final Exception e) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Exception while checking redis connection", e);
        }
        return isJedisRunning;
    }
    
    public static boolean checkRedisConnection() {
        boolean isJedisRunning = false;
        Jedis jedis = null;
        try {
            jedis = getJedisConnection();
            final String result = jedis.ping();
            if (result.equalsIgnoreCase("PONG")) {
                isJedisRunning = true;
            }
        }
        catch (final JedisConnectionException e) {
            RedisServerUtil.redisLogger.log(Level.INFO, "Jedis Process is not running");
            try {
                final boolean isRedisRunning = isProcessRunning("dmredis-server.exe");
                RedisServerUtil.redisLogger.log(Level.WARNING, "Redis Server Running (checking task list ) :" + isRedisRunning);
            }
            catch (final Exception ex) {
                RedisServerUtil.redisLogger.log(Level.WARNING, "Jedis Connection Exception while checking redis process from task list", ex);
            }
        }
        catch (final JedisDataException e2) {
            if (e2.getMessage().equalsIgnoreCase("LOADING Redis is loading the dataset in memory")) {
                RedisServerUtil.redisLogger.log(Level.INFO, "Jedis is loading data into memory");
            }
            else {
                RedisServerUtil.redisLogger.log(Level.WARNING, "Jedis Data Exception while checking redis connection", (Throwable)e2);
            }
        }
        catch (final Exception e3) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Exception while checking redis connection", e3);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return isJedisRunning;
    }
    
    private static Jedis getJedisConnection() throws Exception {
        Jedis jedis;
        try {
            final int port = getRedisPort();
            jedis = new Jedis("localhost", port);
            final boolean isRedisSecure = Boolean.parseBoolean(System.getProperty("isRedisSecure"));
            if (isRedisSecure) {
                final String redisPW = getPasswordFromDBorCache();
                if (redisPW != null && !redisPW.equalsIgnoreCase("")) {
                    final String status = jedis.auth(redisPW);
                    RedisServerUtil.redisLogger.log(Level.INFO, "Redis Connection Authentication Status :" + status);
                }
            }
        }
        catch (final Exception e) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Exception while getting Redis Connection :", e);
            throw e;
        }
        return jedis;
    }
    
    public static void shutdownServer() {
        RedisServerUtil.redisLogger.log(Level.INFO, "Get Client List before shutting down");
        Jedis jedis = null;
        QueueDataMETracking.updateDB();
        try {
            jedis = getJedisConnection();
            if (jedis.ping().equalsIgnoreCase("PONG")) {
                System.setProperty("isRedisShutdownTriggered", "true");
                Thread.sleep(3000L);
                final String clientList = jedis.clientList();
                RedisServerUtil.redisLogger.log(Level.INFO, "CLIENT LIST BEFORE SHUTDOWN  :" + clientList);
                final String stat = jedis.shutdown();
                RedisServerUtil.redisLogger.log(Level.INFO, "REDIS SEVER IS SUCCESSFULLY SHUTDOWN ");
                RedisServerUtil.redisLogger.log(Level.FINE, "REDIS SEVER SHUTDOWN FLAG IS SET");
            }
        }
        catch (final Exception e) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Exception while shutting down redis server", e);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    
    public static boolean isProcessRunning(final String processName) throws IOException, InterruptedException {
        final ProcessBuilder processBuilder = new ProcessBuilder(new String[] { "tasklist.exe" });
        final Process process = processBuilder.start();
        final BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String result = "";
        for (String str = buf.readLine(); str != null; str = buf.readLine()) {
            result = result + str + "\n";
        }
        final String[] split;
        final String[] tasks = split = result.split("\n");
        for (final String task : split) {
            if (task.contains(processName) && (RedisServerUtil.redisPID == -1L || task.contains(String.valueOf(RedisServerUtil.redisPID)))) {
                RedisServerUtil.logger.log(Level.FINE, "Process is Running ");
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
    
    public static String executeCommand(final String... commandWithArgs) {
        RedisServerUtil.redisLogger.log(Level.INFO, "----------------------- In Execute command ----------------------------");
        String output = "";
        BufferedReader commandOutput = null;
        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(commandWithArgs);
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final File file = new File(serverHome + File.separator + "bin");
            processBuilder.directory(file);
            RedisServerUtil.redisLogger.log(Level.INFO, "COMMAND: {0}", processBuilder.command());
            processBuilder.redirectErrorStream(true);
            final Process process = processBuilder.start();
            commandOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s = "";
            while ((s = commandOutput.readLine()) != null) {
                RedisServerUtil.redisLogger.log(Level.INFO, s);
                output += s;
            }
            final int exitValue = process.waitFor();
            RedisServerUtil.redisLogger.log(Level.INFO, "EXIT VALUE :: {0}", exitValue);
        }
        catch (final IOException ioe) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "IOException while executing command " + Arrays.asList(commandWithArgs), ioe);
        }
        catch (final InterruptedException ie) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "IOException while executing command " + Arrays.asList(commandWithArgs), ie);
        }
        finally {
            try {
                if (commandOutput != null) {
                    commandOutput.close();
                }
            }
            catch (final Exception exp) {
                RedisServerUtil.redisLogger.log(Level.WARNING, "Exception : ", exp);
            }
        }
        RedisServerUtil.redisLogger.log(Level.INFO, "---------------------- End of Execute command -------------------------");
        return output;
    }
    
    private static String toString(final InputStream inputStream) {
        final Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
        final String string = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        return string;
    }
    
    private static boolean isRedisEnabled() {
        boolean isRedisEnabled = false;
        try {
            final Properties productSettingsProperties = SyMUtil.getProductSettingsProperties();
            if (productSettingsProperties != null && productSettingsProperties.containsKey("enableRedis")) {
                isRedisEnabled = Boolean.parseBoolean(productSettingsProperties.getProperty("enableRedis"));
            }
        }
        catch (final Exception e) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Exception while checking if redis is enabled", e);
        }
        return isRedisEnabled;
    }
    
    public static int getRedisServerRequiredStatus() {
        int status = -1;
        String statusStr = "Redis Server Not Required";
        final boolean currentRedisSettings = isRedisEnabled();
        final boolean previousRedisSettings = Boolean.parseBoolean(SyMUtil.getSyMParameter("enableRedis"));
        if (currentRedisSettings != previousRedisSettings) {
            if (currentRedisSettings) {
                status = 3;
                statusStr = "Redis Server Install Handling Required";
            }
            else {
                status = 4;
                statusStr = "Redis Server Revert Handling Required";
            }
            SyMUtil.updateSyMParameter("enableRedis", String.valueOf(currentRedisSettings));
        }
        else if (currentRedisSettings) {
            status = 1;
            statusStr = "Redis Server Required";
        }
        else {
            status = 2;
        }
        RedisServerUtil.redisLogger.log(Level.INFO, "Redis Server Installation status :" + statusStr);
        return status;
    }
    
    public static void installationHandling() {
        RedisQueueUtil.migrateFromDBToRedis();
    }
    
    public static void revertHandling() {
        RedisQueueUtil.migrateFromRedisToDB();
    }
    
    public static void shutdownRedis() {
        final boolean redisSettings = Boolean.parseBoolean(SyMUtil.getSyMParameter("enableRedis"));
        if (redisSettings) {
            ApiFactoryProvider.getRedisQueueAPI().destroyQueuePool();
            shutdownServer();
        }
    }
    
    public static void configureRedisServer() {
        int redisStatus = getRedisServerRequiredStatus();
        if (redisStatus != 2) {
            initializeRedisConfigurations();
            startServer();
            if (RedisServerUtil.redisInstallationStatus == 5) {
                redisStatus = RedisServerUtil.redisInstallationStatus;
                RedisServerUtil.redisLogger.log(Level.INFO, "Redis was not installed successfuly, hence disabling redis settings.");
                RedisServerUtil.redisLogger.log(Level.INFO, "Redis Server Installation status : Redis Initial Installation Failed");
            }
            if (redisStatus == 3) {
                installationHandling();
            }
            if (redisStatus == 4) {
                revertHandling();
                shutdownServer();
            }
        }
    }
    
    public static void configureQueue() {
        try {
            ApiFactoryProvider.getRedisQueueAPI().initQueuePool();
        }
        catch (final Exception e) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Exception while configuring redis: ", e);
        }
    }
    
    public static long getRedisDataLoadTimeout() {
        long timeout = RedisServerUtil.DEFAULT_REDIS_DATA_LOAD_TIMEOUT;
        try {
            final Properties rProps = getRedisServerProperties();
            if (rProps != null) {
                timeout = Long.parseLong(rProps.getProperty("redis.data.load.timeout"));
            }
        }
        catch (final Exception e) {
            RedisServerUtil.logger.log(Level.WARNING, "Exception while getting redis port ", e);
        }
        return timeout;
    }
    
    public static long getRedisTimeoutForShutdown() {
        long timeout = RedisServerUtil.DEFAULT_REDIS_SHUTDOWN_TIMEOUT;
        try {
            final Properties rProps = getRedisServerProperties();
            if (rProps != null) {
                timeout = Long.parseLong(rProps.getProperty("redis.shutdown.timeout"));
            }
        }
        catch (final Exception e) {
            RedisServerUtil.logger.log(Level.WARNING, "Exception while getting redis port ", e);
        }
        return timeout;
    }
    
    private static String generateRedisAuthPW() {
        String password = "";
        try {
            password = RandomStringUtils.random(20, 0, 0, true, true, (char[])null, (Random)new SecureRandom());
            final String encryptPW = ApiFactoryProvider.getCryptoAPI().encrypt(password, Integer.valueOf(8));
            final DataObject redisCredentialDO = DataAccess.get("RedisCredential", (Criteria)null);
            if (redisCredentialDO == null || redisCredentialDO.isEmpty()) {
                final Row row = new Row("RedisCredential");
                row.set("REDIS_PASSWORD", (Object)encryptPW);
                row.set("ENC_TYPE", (Object)8);
                redisCredentialDO.addRow(row);
                RedisServerUtil.redisLogger.log(Level.INFO, "Redis Password Successfully Added to DB");
            }
            else {
                final Row row = redisCredentialDO.getFirstRow("RedisCredential");
                row.set("REDIS_PASSWORD", (Object)encryptPW);
                redisCredentialDO.updateRow(row);
                RedisServerUtil.redisLogger.log(Level.INFO, "Redis Password Successfully Updated to DB");
            }
            SyMUtil.getPersistence().update(redisCredentialDO);
        }
        catch (final Exception e) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Exception while generating redis password :", e);
        }
        return password;
    }
    
    private static String getPasswordForAuth() {
        String password = "";
        try {
            password = getPasswordFromDBorCache();
            if (password == null || password.equalsIgnoreCase("")) {
                password = generateRedisAuthPW();
            }
        }
        catch (final Exception e) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Exception while getting password for redis :", e);
        }
        return password;
    }
    
    private static void setRedisPID() {
        Jedis jedis = null;
        try {
            jedis = getJedisConnection();
            final String serverInfo = jedis.info("Server");
            final String[] split;
            final String[] serverInfoList = split = serverInfo.split("\n");
            for (final String info : split) {
                if (info.contains("process_id")) {
                    final String[] processID = info.split(":");
                    RedisServerUtil.redisPID = Long.parseLong(processID[1].trim());
                }
            }
        }
        catch (final Exception e) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Exception while finding Process ID of Redis :", e);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    
    private static void setAuthenticationToRedis() {
        Jedis jedis = null;
        try {
            final String password = getPasswordForAuth();
            RedisServerUtil.redisLogger.log(Level.INFO, "Redis Server is going to set password for secure access");
            if (password != null && !password.equalsIgnoreCase("")) {
                jedis = getJedisConnection();
                final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
                final String dumpFile = serverHome + File.separator + RedisServerUtil.REDIS_DUMP_FILE_LOC;
                if (!new File(dumpFile).exists()) {
                    jedis.bgsave();
                }
                final String status = jedis.configSet("requirepass", password);
                if (status.equalsIgnoreCase("OK")) {
                    System.setProperty("isRedisSecure", "true");
                    ApiFactoryProvider.getCacheAccessAPI().putCache("REDIS_AUTHPW", (Object)password);
                    RedisServerUtil.redisLogger.log(Level.INFO, "Redis Server is set with password for secure access");
                }
                else {
                    RedisServerUtil.redisLogger.log(Level.INFO, "Cannot set password to redis server");
                }
            }
        }
        catch (final Exception e) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Cannot set password to redis server", e);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    
    public static void checkAndShutdownIfAlreadyRunning() {
        try {
            boolean isServerRunning = isProcessRunning("dmredis-server.exe");
            RedisServerUtil.redisLogger.log(Level.INFO, "Is Redis Server already running :" + isServerRunning);
            if (isServerRunning && checkRedisConnection()) {
                shutdownServer();
                RedisServerUtil.redisLogger.log(Level.INFO, "Server status after shutting down :" + isProcessRunning("dmredis-server.exe"));
            }
            isServerRunning = isProcessRunning("dmredis-server.exe");
            if (isServerRunning) {
                executeCommand("Taskkill.exe", "/IM", "dmredis-server.exe", "/F");
            }
        }
        catch (final Exception e) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Exception while starting redis server", e);
        }
    }
    
    public static void revertToDB() {
        try {
            final String productSettingsFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "productSettings.conf";
            final Properties productProps = SyMUtil.getProductSettingsProperties();
            if (productProps != null) {
                productProps.setProperty("enableRedis", "false");
                FileAccessUtil.storeProperties(productProps, productSettingsFile, false);
                SyMUtil.updateSyMParameter("enableRedis", "false");
            }
        }
        catch (final Exception e) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Exception while resetting redis flag to false", e);
        }
    }
    
    public static void startServer() {
        boolean isJedisRunning = false;
        try {
            final StartRedisProcess redisServer = new StartRedisProcess();
            redisServer.start();
            final long dataLoadtimeout = getRedisDataLoadTimeout();
            isJedisRunning = waitAndCheckConnectionUntilTimeOut(dataLoadtimeout, 5000L);
            RedisServerUtil.redisLogger.log(Level.INFO, "Check Connection after starting server :" + isJedisRunning);
        }
        catch (final Exception e) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Exception while starting redis server", e);
        }
        finally {
            RedisServerUtil.redisLogger.log(Level.INFO, "Inside Finally block : " + isJedisRunning);
            final String installationStatus = SyMUtil.getServerParameter("redisInstallationStatus");
            if (isJedisRunning) {
                setAuthenticationToRedis();
                if (installationStatus == null || installationStatus.equalsIgnoreCase("false")) {
                    SyMUtil.updateServerParameter("redisInstallationStatus", "true");
                }
                setRedisPID();
                RedisErrorTracker.initRedisErrors();
                QueueDataMETracking.loadFirstTimeQDetails();
            }
            else if (installationStatus != null && installationStatus.equalsIgnoreCase("true")) {
                try {
                    final String redisBackupEnabled = getRedisPropertyValue("redis.periodic.backup.enabled");
                    final Boolean isRedisBackupEnabled = redisBackupEnabled != null && Boolean.valueOf(redisBackupEnabled);
                    if (isRedisBackupEnabled) {
                        final String isAutoRestoreRedisBackupEnabled = getRedisPropertyValue("redis.auto.restore.enabled");
                        final Boolean isAutoRestoreEnabled = (isAutoRestoreRedisBackupEnabled == null) ? isRedisBackupEnabled : Boolean.valueOf(isAutoRestoreRedisBackupEnabled);
                        if (isAutoRestoreEnabled) {
                            final String redisDataDirectory = getRedisPropertyValue("redis.source.Directory");
                            File file;
                            if (redisDataDirectory == null) {
                                final StringBuilder sb;
                                file = new File(sb.append(System.getProperty("server.home")).append(File.separator).append("redis").append(File.separator).append("data").toString());
                                sb = new StringBuilder();
                            }
                            else {
                                final StringBuilder sb2;
                                file = new File(sb2.append(System.getProperty("server.home")).append(File.separator).append(redisDataDirectory).toString());
                                sb2 = new StringBuilder();
                            }
                            final File dataDir = file;
                            if (!isValidData(dataDir.getCanonicalPath())) {
                                restoreDataFromRedisBackup();
                            }
                            else {
                                RedisServerUtil.redisLogger.log(Level.INFO, " Seems Setup was not corrupted, so can't proceed the restore ");
                            }
                        }
                    }
                }
                catch (final IOException ioe) {
                    RedisServerUtil.redisLogger.log(Level.WARNING, "Unable to restore the redis backup. Exception : ", ioe);
                }
                RedisServerUtil.redisLogger.log(Level.INFO, "Going to restart server since redis was not started");
                RedisServerUtil.logger.log(Level.INFO, "Going to restart server since redis was not started");
                ConsoleOut.println("Going to restart the server. Reason :: Redis server not started.");
                ConsoleOut.println("Restart the SERVER Completely");
            }
            else {
                RedisServerUtil.redisLogger.log(Level.INFO, "Disable Redis and revert to DB ");
                SyMUtil.updateServerParameter("redisInstallationStatus", "false");
                RedisServerUtil.redisInstallationStatus = 5;
                revertToDB();
            }
        }
    }
    
    public static void initializeRedisConfigurations() {
        initRedisFolders();
        checkAndShutdownIfAlreadyRunning();
        checkAndSetRedisPort();
        generateRedisPropsFile();
    }
    
    public static boolean isPortFree(final int portNum) {
        if (portNum < 0) {
            return false;
        }
        try {
            final ServerSocket sock = new ServerSocket(portNum);
            sock.close();
            return true;
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    public static void findAndReplaceStrings(final String sourceFileName, final String destFileName, final Properties findReplPair, final String keyPrefixSuffix, final boolean isQuoteReplacement) throws Exception {
        FileReader freader = null;
        FileWriter fwriter = null;
        try {
            RedisServerUtil.logger.log(Level.INFO, "Invoked  findAndReplaceStringInFile with source fileName: " + sourceFileName + " dest fileName: " + destFileName + " input strings: " + findReplPair);
            final File sourceFile = new File(sourceFileName);
            if (!sourceFile.exists()) {
                RedisServerUtil.logger.log(Level.WARNING, "Source File does not exist. " + sourceFileName);
                throw new FileNotFoundException("File does not exist: " + sourceFileName);
            }
            freader = new FileReader(sourceFileName);
            int read = 0;
            final char[] chBuf = new char[500];
            final StringBuilder strBuilder = new StringBuilder();
            while ((read = freader.read(chBuf)) > -1) {
                strBuilder.append(chBuf, 0, read);
            }
            freader.close();
            String finalStr = strBuilder.toString();
            final Enumeration prkeys = findReplPair.propertyNames();
            while (prkeys.hasMoreElements()) {
                String findStr = prkeys.nextElement();
                final String replaceStr = findReplPair.getProperty(findStr);
                findStr = keyPrefixSuffix + findStr + keyPrefixSuffix;
                if (isQuoteReplacement) {
                    finalStr = finalStr.replace(findStr, replaceStr);
                }
                else {
                    finalStr = finalStr.replaceAll(findStr, replaceStr);
                }
                RedisServerUtil.logger.log(Level.INFO, "Invoked  findAndReplaceStrings findStr: " + findStr + " replaceStr: " + replaceStr);
            }
            fwriter = new FileWriter(destFileName, false);
            fwriter.write(finalStr, 0, finalStr.length());
        }
        catch (final Exception ex) {
            RedisServerUtil.logger.log(Level.SEVERE, "Caught exception in findAndReplaceStrings() source fileName: " + sourceFileName + " dest fileName: " + destFileName + " input strings: " + findReplPair + " exception: ", ex);
            throw ex;
        }
        finally {
            if (freader != null) {
                freader.close();
            }
            if (fwriter != null) {
                fwriter.close();
            }
        }
    }
    
    private static boolean checkAndSetRedisPort() {
        try {
            final Properties redisProps = getRedisServerProperties();
            final String redisPortStr = redisProps.getProperty("redis.port");
            if (redisPortStr == null) {
                return false;
            }
            final int redisPort = Integer.parseInt(redisPortStr);
            if (isPortFree(redisPort)) {
                return true;
            }
            final String redisPortRangeStr = redisProps.getProperty("redis.port.range");
            RedisServerUtil.logger.log(Level.INFO, "Redis Port " + redisPort + " is not free. Going to find a free port from the given range: " + redisPortRangeStr);
            if (redisPortRangeStr == null) {
                RedisServerUtil.logger.log(Level.WARNING, "Unable to find free redis port. redis.port.range is empty.");
                return false;
            }
            final String[] redisPortArr = redisPortRangeStr.split("-");
            if (redisPortArr.length < 2) {
                RedisServerUtil.logger.log(Level.WARNING, "Unable to find free redis port. redis.port.range is not in correct format.");
                return false;
            }
            int startPort = Integer.parseInt(redisPortArr[0]);
            int endPort = Integer.parseInt(redisPortArr[1]);
            boolean portFound = false;
            if (startPort > endPort) {
                final int tmp = startPort;
                startPort = endPort;
                endPort = tmp;
            }
            for (int s = startPort; s <= endPort; ++s) {
                RedisServerUtil.logger.log(Level.INFO, "Checking port (for Redis) whether it is free: " + s);
                if (isPortFree(s)) {
                    RedisServerUtil.logger.log(Level.INFO, "Port (for Redis): " + s + " is found free. Going to set this port as Redis port.");
                    final Properties props = new Properties();
                    props.setProperty("redis.port", String.valueOf(s));
                    storeProperRedisProperties(props);
                    portFound = true;
                    break;
                }
            }
            if (!portFound) {
                RedisServerUtil.logger.log(Level.WARNING, "Unable to find a free port for redis from given range. It will attempt to use the same port configured already.");
                return false;
            }
        }
        catch (final Exception ex) {
            RedisServerUtil.logger.log(Level.WARNING, "Caught exception while checking whether ajp port is free.", ex);
            return false;
        }
        return true;
    }
    
    public static void storeProperRedisProperties(final Properties rProps) {
        final Properties redisProps = new Properties();
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String confFile = serverHome + File.separator + RedisServerUtil.REDIS_SETTINGS_CONF_FILE;
            storeProperties(rProps, confFile, null);
        }
        catch (final Exception e) {
            RedisServerUtil.redisLogger.log(Level.SEVERE, "Exception while storing redis properties: ", e);
        }
    }
    
    public static Properties setDefaultRedisServerProps() {
        final Properties redisProps = new Properties();
        try {
            ((Hashtable<String, String>)redisProps).put("redis.port", "6379");
            ((Hashtable<String, String>)redisProps).put("redis.log.level", "verbose");
        }
        catch (final Exception e) {
            RedisServerUtil.logger.log(Level.WARNING, "Exception while modifying Redis-Server props");
        }
        return redisProps;
    }
    
    public static void storeProperties(final Properties newprops, final String confFileName, final String comments) {
        final Properties props = new Properties();
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            if (new File(confFileName).exists()) {
                fis = new FileInputStream(confFileName);
                props.load(fis);
                fis.close();
            }
            final Enumeration keys = newprops.propertyNames();
            while (keys.hasMoreElements()) {
                final String key = keys.nextElement();
                props.setProperty(key, newprops.getProperty(key));
            }
            fos = new FileOutputStream(confFileName);
            props.store(fos, comments);
            fos.close();
        }
        catch (final Exception ex) {
            RedisServerUtil.logger.log(Level.SEVERE, "Caught exception: " + ex);
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception ex) {
                RedisServerUtil.logger.log(Level.SEVERE, "Caught exception: " + ex);
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception ex2) {
                RedisServerUtil.logger.log(Level.SEVERE, "Caught exception: " + ex2);
            }
        }
    }
    
    public static void generateRedisPropsFile() {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String redisConfFileName = serverHome + File.separator + RedisServerUtil.REDIS_CONF_FILE;
            final String redisTemplateFileName = serverHome + File.separator + RedisServerUtil.REDIS_TEMPLATE_CONF_FILE;
            final Properties redisProps = getRedisServerProperties();
            findAndReplaceStrings(redisTemplateFileName, redisConfFileName, redisProps, "%", true);
        }
        catch (final Exception e) {
            RedisServerUtil.logger.log(Level.WARNING, "Exception while generating redis conf", e);
        }
    }
    
    public static void backupRedisData() {
        final String folderName = DateTimeUtil.longdateToString(System.currentTimeMillis(), "MMM-dd-yyyy-HH-mm");
        final File destDir = new File(System.getProperty("server.home") + File.separator + getRedisPropertyValue("redis.periodic.backup.location") + File.separator + folderName);
        final String redisDataDirectory = getRedisPropertyValue("redis.source.Directory");
        final File sourceDir = (redisDataDirectory == null) ? new File(System.getProperty("server.home") + File.separator + "redis" + File.separator + "data") : new File(System.getProperty("server.home") + File.separator + redisDataDirectory);
        try {
            if (!sourceDir.exists()) {
                RedisServerUtil.redisLogger.log(Level.WARNING, "Source File not exists , can't take redis backup " + sourceDir.getCanonicalPath().toString());
            }
            else {
                FileUtils.copyDirectory(sourceDir, destDir);
                if (!isValidData(destDir.getCanonicalPath())) {
                    FileUtil.deleteFileOrFolder(destDir);
                    QueueDataMETracking.updateBackupFailureInDB();
                }
                cleanupOldBackupFiles();
            }
        }
        catch (final Exception e) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Exception occurred while backup the redis data. Exception : ", e);
        }
    }
    
    private static boolean isValidData(final String data) {
        boolean isValid = false;
        try {
            final String exeName = new File(System.getProperty("server.home") + File.separator + "redis" + File.separator + "bin" + File.separator + "dmredis-check-dump.exe").getCanonicalPath();
            if (new File(exeName).exists()) {
                final Process process = new ProcessBuilder(new String[] { exeName, data + File.separator + "dump.rdb" }).start();
                final BufferedReader commandOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String s = null;
                while ((s = commandOutput.readLine()) != null) {
                    if (s.contains("checksum is OK")) {
                        isValid = true;
                    }
                }
                process.waitFor();
                final int exitCode = process.exitValue();
                RedisServerUtil.redisLogger.log(Level.FINE, "Exit code : " + exitCode);
            }
            else {
                isValid = true;
            }
        }
        catch (final IOException ex) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Exception while Checking backup is Valid ", ex);
        }
        catch (final InterruptedException e) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Exception while Checking backup is Valid ", e);
        }
        return isValid;
    }
    
    private static boolean restoreDataFromRedisBackup() throws IOException {
        final ArrayList backupList = getSorterBackupFileList();
        if (backupList.isEmpty()) {
            RedisServerUtil.redisLogger.log(Level.SEVERE, "Seems Backup files are t available,Can't proceed the Restore");
            final String dumpFilePath = System.getProperty("server.home") + File.separator + RedisServerUtil.REDIS_DUMP_FILE_LOC;
            final File dumpFile = new File(dumpFilePath);
            dumpFile.delete();
            RedisServerUtil.redisLogger.log(Level.SEVERE, "Going to delete the dump.rdb Path: " + dumpFilePath);
        }
        else {
            final String restorableBackupFromLast = getRedisPropertyValue("redis.restore.backup.lastindex");
            final int restorableBackupindexFromLast = Integer.parseInt((restorableBackupFromLast == null) ? "1" : restorableBackupFromLast);
            final String restorableFolderName = DateTimeUtil.longdateToString((long)new Long(backupList.get(backupList.size() - restorableBackupindexFromLast).toString()), "MMM-dd-yyyy-HH-mm");
            final String sourceDir = new File(System.getProperty("server.home") + File.separator + getRedisPropertyValue("redis.periodic.backup.location") + File.separator + restorableFolderName).getCanonicalPath();
            String dataDirectory = getRedisPropertyValue("redis.source.Directory");
            dataDirectory = ((dataDirectory == null) ? (System.getProperty("server.home") + File.separator + "redis" + File.separator + "data") : (System.getProperty("server.home") + File.separator + dataDirectory));
            try {
                if (!new File(sourceDir).exists()) {
                    RedisServerUtil.redisLogger.log(Level.WARNING, "Source directory {0} was not exists. ", new Object[] { sourceDir });
                    return false;
                }
                final String renameDir = dataDirectory + "_" + System.currentTimeMillis();
                if (new File(renameDir).exists()) {
                    FileUtil.deleteFileOrFolder(new File(renameDir));
                }
                final boolean tempRenameStatus = FileUtil.renameFolder(dataDirectory, renameDir);
                if (!tempRenameStatus) {
                    RedisServerUtil.redisLogger.log(Level.WARNING, " Unable to rename the source {0} to destination {1} , Status :  ", new Object[] { dataDirectory, renameDir, tempRenameStatus });
                    return false;
                }
                FileUtil.copyFolder(new File(sourceDir), new File(dataDirectory));
                RedisServerUtil.redisLogger.log(Level.INFO, "BackUp is restored from SourceDir: " + sourceDir + " to " + "DestDir:" + dataDirectory);
                FileUtil.deleteFileOrFolder(new File(renameDir));
            }
            catch (final Exception e) {
                RedisServerUtil.redisLogger.log(Level.WARNING, " Exception occurred while restoring redis backup data. Exception : ", e);
            }
        }
        return true;
    }
    
    public static void cleanupOldBackupFiles() {
        final ArrayList backupList = getSorterBackupFileList();
        final ArrayList cleanupList = new ArrayList();
        final Iterator iter = backupList.iterator();
        final int length = backupList.size();
        final int size = (getRedisPropertyValue("redis.periodic.numberofbackups") == null) ? 3 : Integer.parseInt(getRedisPropertyValue("redis.periodic.numberofbackups"));
        for (int upto = length - size, index = 0; iter.hasNext() && index < upto; ++index) {
            cleanupList.add(iter.next());
        }
        final Iterator it = cleanupList.iterator();
        String folderName = "";
        while (it.hasNext()) {
            folderName = DateTimeUtil.longdateToString((long)new Long(it.next().toString()), "MMM-dd-yyyy-HH-mm");
            final String path = System.getProperty("server.home") + File.separator + getRedisPropertyValue("redis.periodic.backup.location") + File.separator + folderName;
            if (new File(path).exists()) {
                FileUtil.deleteFileOrFolder(new File(path));
            }
        }
    }
    
    private static ArrayList getSorterBackupFileList() {
        final ArrayList backupList = new ArrayList();
        final File sourceDir = new File(System.getProperty("server.home") + File.separator + getRedisPropertyValue("redis.periodic.backup.location"));
        if (sourceDir.exists() && sourceDir.isDirectory()) {
            final File[] listFiles;
            final File[] files = listFiles = sourceDir.listFiles();
            for (final File f : listFiles) {
                backupList.add(DateTimeUtil.dateInLong(f.getName(), "MMM-dd-yyyy-HH-mm"));
            }
        }
        Collections.sort((List<Comparable>)backupList);
        return backupList;
    }
    
    private static void createRedisBackupScheduler(final int period) throws Exception {
        final Persistence p = (Persistence)BeanUtil.lookup("Persistence");
        final DataObject data = p.constructDataObject();
        final Row taskengineRow = new Row("TaskEngine_Task");
        taskengineRow.set(2, (Object)"redisperiodicbackuptask");
        taskengineRow.set(3, (Object)"com.me.devicemanagement.onpremise.server.redis.RedisPeriodicBackupScheduler");
        final Row scheduleRow = new Row("Schedule");
        scheduleRow.set(2, (Object)"RedisPeriodicBackupScheduler");
        final Row periodicRow = new Row("Periodic");
        periodicRow.set(1, scheduleRow.get(1));
        periodicRow.set(4, (Object)period);
        periodicRow.set(6, (Object)false);
        data.addRow(taskengineRow);
        data.addRow(scheduleRow);
        data.addRow(periodicRow);
        p.add(data);
        final Scheduler s = (Scheduler)BeanUtil.lookup("Scheduler");
        final DataObject taskInputDO = p.constructDataObject();
        final Row taskInputRow = new Row("Task_Input");
        taskInputDO.addRow(taskInputRow);
        s.scheduleTask("RedisPeriodicBackupScheduler", "redisperiodicbackuptask", taskInputDO);
    }
    
    private static void updateRedisBackupScheduler(final Long scheduleID, final int period) throws Exception {
        final Long instance_id = (Long)DBUtil.getValueFromDB("Task_Input", "SCHEDULE_ID", (Object)scheduleID, "INSTANCE_ID");
        final Scheduler sch = (Scheduler)BeanUtil.lookup("Scheduler");
        sch.setTaskInputAdminStatus((long)instance_id, 3);
        final UpdateQuery uq = (UpdateQuery)new UpdateQueryImpl("Periodic");
        uq.setCriteria(new Criteria(Column.getColumn("Periodic", "SCHEDULE_ID"), (Object)scheduleID, 0));
        uq.setUpdateColumn("TIME_PERIOD", (Object)period);
        SyMUtil.getPersistence().update(uq);
    }
    
    private static String getRedisPropertyValue(final String key) {
        final Properties props = com.me.devicemanagement.framework.server.redis.RedisServerUtil.getRedisServerProperties();
        if (props.containsKey(key)) {
            return props.getProperty(key);
        }
        return null;
    }
    
    public static void configureRedisPeriodicBackup() throws Exception {
        if (isRedisSettingsChanged()) {
            final String redisBackupEnabled = getRedisPropertyValue("redis.periodic.backup.enabled");
            final Boolean isRedisBackupEnabled = redisBackupEnabled != null && Boolean.valueOf(redisBackupEnabled);
            final Long scheduleID = (Long)DBUtil.getValueFromDB("Schedule", "SCHEDULE_NAME", (Object)"RedisPeriodicBackupScheduler", "SCHEDULE_ID");
            if (isRedisBackupEnabled) {
                final String interval = getRedisPropertyValue("redis.periodic.backup.interval");
                final int backupInterval = (interval == null) ? 360 : Integer.parseInt(interval);
                if (scheduleID == null) {
                    createRedisBackupScheduler(backupInterval);
                }
                else {
                    updateRedisBackupScheduler(scheduleID, backupInterval);
                }
            }
            else if (scheduleID != null) {
                final Long instance_id = (Long)DBUtil.getValueFromDB("Task_Input", "SCHEDULE_ID", (Object)scheduleID, "INSTANCE_ID");
                final Scheduler sch = (Scheduler)BeanUtil.lookup("Scheduler");
                sch.setTaskInputAdminStatus((long)instance_id, 4);
            }
            updateRSModtimeFile();
        }
        else {
            RedisServerUtil.redisLogger.log(Level.INFO, "redis settings conf file was not modified. ");
        }
    }
    
    private static boolean isRedisSettingsChanged() {
        boolean changed = false;
        try {
            final Long prevModTime = getRedisSettingsLastModifiedTimeProperty();
            RedisServerUtil.redisLogger.log(Level.FINE, "Redis settings Previous ModifiedTime: " + prevModTime);
            if (prevModTime == null) {
                RedisServerUtil.redisLogger.log(Level.INFO, "Redis settings Previous modified time is null. This might be the first server startup or first startup after restore...");
            }
            final long lastModTime = getRedisSettingsConfLastModifiedTime();
            RedisServerUtil.redisLogger.log(Level.FINE, "Last modified time of " + com.me.devicemanagement.framework.server.redis.RedisServerUtil.REDIS_SETTINGS_CONF_FILE + " from file system: " + lastModTime);
            if (prevModTime == null || lastModTime != prevModTime) {
                changed = true;
                RedisServerUtil.redisLogger.log(Level.FINE, "Redis Settings ModifiedTime: " + lastModTime);
            }
        }
        catch (final Exception ex) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Exception occurred while checking the " + com.me.devicemanagement.framework.server.redis.RedisServerUtil.REDIS_SETTINGS_CONF_FILE + " is modified or not.", ex);
            changed = true;
        }
        return changed;
    }
    
    private static Long getRedisSettingsLastModifiedTimeProperty() {
        Long lastModTime = null;
        try {
            final String fname = System.getProperty("server.home") + File.separator + "conf" + File.separator + "rs.modtime";
            if (new File(fname).exists()) {
                final Properties props = com.me.devicemanagement.framework.server.redis.RedisServerUtil.getProperties(fname);
                if (props != null) {
                    final String modTimeStr = props.getProperty("lastModifiedTime");
                    if (modTimeStr != null) {
                        lastModTime = new Long(modTimeStr);
                    }
                }
            }
        }
        catch (final Exception ex) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Caught exception while retrieving redis_settings conf prev modified time.", ex);
        }
        return lastModTime;
    }
    
    private static Long getRedisSettingsConfLastModifiedTime() {
        long lastModTime = -1L;
        String webSettingsFileName = null;
        try {
            webSettingsFileName = new File(System.getProperty("server.home")).getCanonicalPath() + File.separator + com.me.devicemanagement.framework.server.redis.RedisServerUtil.REDIS_SETTINGS_CONF_FILE;
            final File webSettingsFile = new File(webSettingsFileName);
            if (webSettingsFile.exists()) {
                lastModTime = webSettingsFile.lastModified();
            }
        }
        catch (final Exception ex) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Caught error while retrieving last modified time of file {0}. Exception : {1} ", new Object[] { webSettingsFileName, ex });
        }
        return lastModTime;
    }
    
    private static void updateRSModtimeFile() throws IOException {
        final String fname = System.getProperty("server.home") + File.separator + "conf" + File.separator + "rs.modtime";
        if (!new File(fname).exists()) {
            new File(fname).createNewFile();
        }
        final Properties properties = new Properties();
        properties.setProperty("lastModifiedTime", getRedisSettingsConfLastModifiedTime().toString());
        StartupUtil.storeProperties(properties, fname);
    }
    
    public static void disableRedisPeriodicBackupScheduler() {
        try {
            final Long scheduleID = (Long)DBUtil.getValueFromDB("Schedule", "SCHEDULE_NAME", (Object)"RedisPeriodicBackupScheduler", "SCHEDULE_ID");
            if (scheduleID != null) {
                final Long instance_id1 = (Long)DBUtil.getValueFromDB("Task_Input", "SCHEDULE_ID", (Object)scheduleID, "INSTANCE_ID");
                final Scheduler sch1 = (Scheduler)BeanUtil.lookup("Scheduler");
                if (instance_id1 != null) {
                    sch1.setTaskInputAdminStatus((long)instance_id1, 4);
                }
            }
        }
        catch (final Exception ex) {
            RedisServerUtil.redisLogger.log(Level.WARNING, "Unable to disable the redis periodic backup scheduler : ", ex);
        }
    }
    
    static {
        RedisServerUtil.sourceClass = "RedisServerUtil";
        RedisServerUtil.checkAndKillRedis = new CheckAndKillRedis();
    }
    
    public static class CheckAndKillRedis extends Thread
    {
        @Override
        public void run() {
            try {
                final long thresholdTime = RedisServerUtil.getRedisTimeoutForShutdown();
                final long bufferTime = 5000L;
                long totalTime = 0L;
                while (true) {
                    final boolean status = RedisServerUtil.checkRedisConnection();
                    if (status) {
                        break;
                    }
                    Thread.sleep(bufferTime);
                    totalTime += bufferTime;
                    if (totalTime >= thresholdTime) {
                        ++RedisErrorTracker.shutdownCount;
                        QueueDataMETracking.updateDB();
                        System.setProperty("RedisErrorShutdown", "true");
                        com.me.devicemanagement.framework.server.redis.RedisServerUtil.logger.log(Level.INFO, "Checked for " + thresholdTime / 1000L + " seconds ");
                        com.me.devicemanagement.framework.server.redis.RedisServerUtil.redisLogger.log(Level.INFO, "Checked for " + thresholdTime / 1000L + " seconds ");
                        com.me.devicemanagement.framework.server.redis.RedisServerUtil.logger.log(Level.INFO, "Errors " + QueueDataMETracking.getRedisQueueDetailsJSON());
                        com.me.devicemanagement.framework.server.redis.RedisServerUtil.redisLogger.log(Level.INFO, "Errors " + QueueDataMETracking.getRedisQueueDetailsJSON());
                        final boolean procStatus = RedisServerUtil.isProcessRunning("dmredis-server.exe");
                        com.me.devicemanagement.framework.server.redis.RedisServerUtil.redisLogger.log(Level.INFO, "Redis process in task manager", procStatus);
                        com.me.devicemanagement.framework.server.redis.RedisServerUtil.redisLogger.log(Level.INFO, "Going to trigger server restart since unusual problem in redis detected");
                        com.me.devicemanagement.framework.server.redis.RedisServerUtil.logger.log(Level.INFO, "Redis process in task manager", procStatus);
                        com.me.devicemanagement.framework.server.redis.RedisServerUtil.logger.log(Level.WARNING, "MISCONF Redis is configured to save RDB snapshots, but is currently not able to persist on disk. Commands that may modify the data set are disabled. Resource starvation might be one of the reasons here!");
                        ConsoleOut.println("Redis is unable to persist data to disk. Resource starvation might be the reason here");
                        ConsoleOut.println("Restart the SERVER Completely");
                        break;
                    }
                }
            }
            catch (final Exception e) {
                com.me.devicemanagement.framework.server.redis.RedisServerUtil.redisLogger.log(Level.INFO, "Exception while checking and killing redis server", e);
            }
        }
    }
    
    private static class StartRedisProcess extends Thread
    {
        @Override
        public void run() {
            com.me.devicemanagement.framework.server.redis.RedisServerUtil.redisLogger.log(Level.INFO, "Going to start Redis Server");
            try {
                final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
                final String redisExeHome = serverHome + File.separator + "redis" + File.separator + "bin";
                final String redisConfFileName = serverHome + File.separator + RedisServerUtil.REDIS_CONF_FILE;
                final String redisExe = serverHome + File.separator + RedisServerUtil.REDIS_EXE;
                final List<String> command = new ArrayList<String>();
                command.add(redisExe);
                command.add(redisConfFileName);
                final ProcessBuilder p = new ProcessBuilder(command);
                com.me.devicemanagement.framework.server.redis.RedisServerUtil.redisLogger.log(Level.INFO, "Command :" + p.command());
                p.directory(new File(redisExeHome));
                p.redirectErrorStream();
                String result = "";
                final Process proc = p.start();
                final BufferedReader buf = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                for (String str = buf.readLine(); str != null; str = buf.readLine()) {
                    result = result + str + "\n";
                }
                com.me.devicemanagement.framework.server.redis.RedisServerUtil.redisLogger.log(Level.INFO, "Process Builder Execution Result :" + result);
            }
            catch (final Exception e) {
                com.me.devicemanagement.framework.server.redis.RedisServerUtil.redisLogger.log(Level.WARNING, "Exception while starting server", e);
                com.me.devicemanagement.framework.server.redis.RedisServerUtil.redisLogger.log(Level.INFO, "Going to trigger server restart");
                com.me.devicemanagement.framework.server.redis.RedisServerUtil.logger.log(Level.WARNING, "Exception while starting server", e);
                com.me.devicemanagement.framework.server.redis.RedisServerUtil.logger.log(Level.INFO, "Going to trigger server restart");
                ConsoleOut.println("Going to restart the server. Reason :: Exception while starting redis server");
                ConsoleOut.println("Restart the SERVER Completely");
            }
        }
    }
}
