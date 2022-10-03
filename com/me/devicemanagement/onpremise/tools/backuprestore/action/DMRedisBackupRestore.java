package com.me.devicemanagement.onpremise.tools.backuprestore.action;

import java.util.List;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.FileUtil;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.CompressUtil;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.DMBackupPasswordHandler;
import java.util.Enumeration;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.net.ServerSocket;
import com.zoho.framework.utils.crypto.EnDecrypt;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.zoho.framework.utils.crypto.EnDecryptAES256Impl;
import java.util.Random;
import org.apache.commons.lang.RandomStringUtils;
import java.security.SecureRandom;
import redis.clients.jedis.exceptions.JedisConnectionException;
import java.util.Scanner;
import java.io.InputStream;
import java.io.IOException;
import redis.clients.jedis.Jedis;
import java.io.File;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreContants;
import java.util.logging.Level;
import java.util.HashMap;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import java.util.logging.Logger;
import java.util.Properties;
import com.me.devicemanagement.onpremise.tools.backuprestore.handler.Informable;

public class DMRedisBackupRestore
{
    private Informable informable;
    private String destFolder;
    private static Properties dbBackupProps;
    private String serverHome;
    private static final Logger LOGGER;
    public static final String REDIS_SETTINGS_CONF_FILE;
    public static final String REDIS_CONF_FILE;
    public static final String REDIS_TEMPLATE_CONF_FILE;
    public static final String REDIS_EXE_FILENAME = "dmredis-server.exe";
    public static final String REDIS_EXE;
    private static final String AOF_FILE_PATH;
    public static final String REDIS_PORT = "redis.port";
    public static final String REDIS_PORT_RANGE = "redis.port.range";
    public static final String ENABLE_AOF = "enableAOF";
    public static final String REDIS_LOG_LEVEL = "redis.log.level";
    public static final String REDIS_MAX_MEMORY = "redis.maxmemory";
    public static final String REDIS_MAX_CONNECTION = "redis.maxconnections";
    public static final String DEFAULT_REDIS_PORT = "6379";
    public static final String DEFAULT_REDIS_LOG_LEVEL = "verbose";
    public static final String DEFAULT_REDIS_MAX_MEMORY = "1GB";
    public static final String DEFAULT_REDIS_MAX_CONNECTION = "10000";
    public static final String KEY_PREFIX_SUFFIX = "%";
    public static final String REDIS_HOST = "localhost";
    public static final String SERVER_IP = "server.ip";
    private static String sourceClass;
    
    public DMRedisBackupRestore(final Informable informable) {
        this(null, informable);
    }
    
    public DMRedisBackupRestore(final String dest, final Informable inf) {
        this.informable = null;
        this.serverHome = null;
        this.informable = inf;
        this.destFolder = dest;
        this.serverHome = System.getProperty("server.home");
        BackupRestoreUtil.setSevenZipLoc();
    }
    
    public static void includeRedisDataFolder(final HashMap<Integer, Properties> backupList) throws Exception {
        DMRedisBackupRestore.LOGGER.log(Level.INFO, "Going to add Redis Data folder PreCheck list");
        final Properties rProps = getRedisServerProperties();
        boolean isAOFEnabled = false;
        if (rProps != null) {
            isAOFEnabled = Boolean.parseBoolean(rProps.getProperty("enableAOF"));
        }
        final Properties redisProps = new Properties();
        final String serverHome = System.getProperty("server.home");
        final File actualPath = new File(serverHome, BackupRestoreContants.REDIS_DATA_FOLDER_LOC);
        final File dumpFile = new File(serverHome, BackupRestoreContants.REDIS_DUMP_FILE_LOC);
        final File aofFile = new File(serverHome, BackupRestoreContants.REDIS_AOF_FILE_LOC);
        if (!actualPath.exists() || !dumpFile.exists() || (isAOFEnabled && !aofFile.exists())) {
            DMRedisBackupRestore.LOGGER.log(Level.WARNING, "File {0} does not exist! ignore_error is false. Hence returning.", BackupRestoreContants.REDIS_DATA_FOLDER_LOC);
            final String displayName = new BackupRestoreUtil().getValueFromGenProps("displayname");
            throw BackupRestoreUtil.createException(-13, new Object[] { displayName }, null);
        }
        redisProps.setProperty("ignore_error", "false");
        redisProps.setProperty("backup_type", "copy");
        redisProps.setProperty("file_path", BackupRestoreContants.REDIS_DATA_FOLDER_LOC);
        backupList.put(2001, redisProps);
        DMRedisBackupRestore.LOGGER.log(Level.INFO, "Redis data dump file is added in PreCheck list");
    }
    
    public static void backupRedis() throws Exception {
        DMRedisBackupRestore.LOGGER.log(Level.INFO, "########## Starting Redis Data Copy backup ##########");
        includeRedisDataFolder(DMBackupAction.backupList);
        DMRedisBackupRestore.LOGGER.log(Level.INFO, "########## Copy backup completed. Redis data folder entry is added in compression list ##########");
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
            DMRedisBackupRestore.LOGGER.log(Level.WARNING, "Exception while getting redis port ", e);
        }
        return port;
    }
    
    public static String getPasswordFromCache() {
        final String password = System.getProperty("REDIS_AUTHPW");
        return password;
    }
    
    private static Jedis getJedisConnection() throws Exception {
        Jedis jedis = null;
        try {
            final int port = getRedisPort();
            jedis = new Jedis("localhost", port);
            final boolean isRedisSecure = Boolean.parseBoolean(System.getProperty("isRedisSecure"));
            if (isRedisSecure) {
                final String redisPW = getPasswordFromCache();
                if (redisPW != null && !redisPW.equalsIgnoreCase("")) {
                    final String status = jedis.auth(redisPW);
                    DMRedisBackupRestore.LOGGER.log(Level.INFO, "Redis Connection Authentication Status :" + status);
                }
            }
        }
        catch (final Exception e) {
            DMRedisBackupRestore.LOGGER.log(Level.WARNING, "Exception while getting Redis Connection :", e);
            throw e;
        }
        return jedis;
    }
    
    public static boolean isProcessRunning(final String processName) throws IOException, InterruptedException {
        final ProcessBuilder processBuilder = new ProcessBuilder(new String[] { "tasklist.exe" });
        final Process process = processBuilder.start();
        final String tasksList = toString(process.getInputStream());
        return tasksList.contains(processName);
    }
    
    private static String toString(final InputStream inputStream) {
        final Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
        final String string = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        return string;
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
            DMRedisBackupRestore.LOGGER.log(Level.INFO, "Jedis Process is not running");
            try {
                final boolean isRedisRunning = isProcessRunning("dmredis-server.exe");
                DMRedisBackupRestore.LOGGER.log(Level.WARNING, "Redis Server Running (checking tasklist ) :" + isRedisRunning);
            }
            catch (final Exception ex) {
                DMRedisBackupRestore.LOGGER.log(Level.WARNING, "Exception while checking redis process from tasklist", ex);
            }
        }
        catch (final Exception e2) {
            DMRedisBackupRestore.LOGGER.log(Level.WARNING, "Exception while checking redis connection", e2);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return isJedisRunning;
    }
    
    private static boolean waitAndCheckConnectionUntilTimeOut(final long timeOut, final long split) {
        final long startTime = System.currentTimeMillis();
        try {
            for (long i = 0L; i < timeOut; i += split, ++i) {
                if (checkRedisConnection()) {
                    final long totalTime = (System.currentTimeMillis() - startTime) / 1000L;
                    DMRedisBackupRestore.LOGGER.log(Level.INFO, "Received connection from redis after " + totalTime + " secs");
                    return true;
                }
                Thread.sleep(split);
            }
        }
        catch (final Exception e) {
            DMRedisBackupRestore.LOGGER.log(Level.WARNING, "Exception while checking redis connection", e);
        }
        return false;
    }
    
    private static String generateRedisBackupRestoreAuthPW() {
        String password = "";
        try {
            password = RandomStringUtils.random(20, 0, 0, true, true, (char[])null, (Random)new SecureRandom());
        }
        catch (final Exception e) {
            DMRedisBackupRestore.LOGGER.log(Level.WARNING, "Exception while generating redis password :", e);
        }
        return password;
    }
    
    public static void setAuthenticationToRedis() {
        Jedis jedis = null;
        try {
            final EnDecrypt ed = (EnDecrypt)new EnDecryptAES256Impl();
            CryptoUtil.setEnDecryptInstance(ed);
            final String password = generateRedisBackupRestoreAuthPW();
            if (password != null && !password.equalsIgnoreCase("")) {
                jedis = getJedisConnection();
                final String status = jedis.configSet("requirepass", password);
                if (status.equalsIgnoreCase("OK")) {
                    System.setProperty("isRedisSecure", "true");
                    System.setProperty("REDIS_AUTHPW", password);
                    DMRedisBackupRestore.LOGGER.log(Level.INFO, "Redis Server is set with password for secure access");
                }
                else {
                    DMRedisBackupRestore.LOGGER.log(Level.INFO, "Cannot set password to redis server");
                }
            }
        }
        catch (final Exception e) {
            DMRedisBackupRestore.LOGGER.log(Level.WARNING, "Cannot set password to redis server", e);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    
    public static boolean startServer() throws Exception {
        boolean isJedisRunning = false;
        try {
            final StartRedisProcess redisServer = new StartRedisProcess();
            redisServer.start();
            isJedisRunning = waitAndCheckConnectionUntilTimeOut(30000L, 5000L);
            DMRedisBackupRestore.LOGGER.log(Level.INFO, "Check Connection after starting server :" + isJedisRunning);
            if (isJedisRunning) {
                setAuthenticationToRedis();
            }
        }
        catch (final Exception e) {
            throw e;
        }
        return isJedisRunning;
    }
    
    private void regenerateAOF() throws Exception {
        Jedis jedis = null;
        try {
            jedis = getJedisConnection();
            final long startTime = System.currentTimeMillis();
            jedis.bgrewriteaof();
            String info;
            int i;
            for (info = "", i = 0; !info.contains("aof_rewrite_in_progress:0") || !info.contains("aof_last_bgrewrite_status:ok"); info = jedis.info("persistence"), ++i) {}
            DMRedisBackupRestore.LOGGER.log(Level.INFO, "AOF Rewrite Status " + info + " after " + i + "status");
            final long endTime = System.currentTimeMillis();
            final long totalTime = endTime - startTime;
            DMRedisBackupRestore.LOGGER.log(Level.INFO, "BGAOF Rewrite time :" + totalTime);
        }
        catch (final Exception e) {
            throw e;
        }
        finally {
            if (jedis == null) {
                jedis.close();
            }
        }
    }
    
    public static void shutdownServer() throws Exception {
        DMRedisBackupRestore.LOGGER.log(Level.INFO, "Get Client List before shutting down");
        Jedis jedis = null;
        try {
            jedis = getJedisConnection();
            if (jedis.ping().equalsIgnoreCase("PONG")) {
                final String clientList = jedis.clientList();
                DMRedisBackupRestore.LOGGER.log(Level.INFO, "CLIENT LIST BEFORE SHUTDOWN  :" + clientList);
                jedis.shutdown();
                DMRedisBackupRestore.LOGGER.log(Level.INFO, "REDIS SEVER IS SUCCESSFULLY SHUTDOWN");
            }
        }
        catch (final Exception e) {
            DMRedisBackupRestore.LOGGER.log(Level.WARNING, "Exception while shutting down redis server", e);
            throw e;
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
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
    
    public static void initializeRedisConfigurations() {
        checkAndSetRedisPort();
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
            DMRedisBackupRestore.LOGGER.log(Level.INFO, "Redis Port " + redisPort + " is not free. Going to find a free port from the given range: " + redisPortRangeStr);
            if (redisPortRangeStr == null) {
                DMRedisBackupRestore.LOGGER.log(Level.WARNING, "Unable to find free redis port. redis.port.range is empty.");
                return false;
            }
            final String[] redisPortArr = redisPortRangeStr.split("-");
            if (redisPortArr.length < 2) {
                DMRedisBackupRestore.LOGGER.log(Level.WARNING, "Unable to find free redis port. redis.port.range is not in correct format.");
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
                DMRedisBackupRestore.LOGGER.log(Level.INFO, "Checking port (for Redis) whether it is free: " + s);
                if (isPortFree(s)) {
                    DMRedisBackupRestore.LOGGER.log(Level.INFO, "Port (for Redis): " + s + " is found free. Going to set this port as Redis port.");
                    final Properties props = new Properties();
                    props.setProperty("redis.port", String.valueOf(s));
                    storeProperRedisProperties(props);
                    portFound = true;
                    break;
                }
            }
            if (!portFound) {
                DMRedisBackupRestore.LOGGER.log(Level.WARNING, "Unable to find a free port for redis from given range. It will attempt to use the same port configured already.");
                return false;
            }
        }
        catch (final Exception ex) {
            DMRedisBackupRestore.LOGGER.log(Level.WARNING, "Caught exception while checking whether ajp port is free.", ex);
            return false;
        }
        return true;
    }
    
    public static void storeProperRedisProperties(final Properties rProps) {
        try {
            final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            final String confFile = serverHome + File.separator + DMRedisBackupRestore.REDIS_SETTINGS_CONF_FILE;
            storeProperties(rProps, confFile, null);
        }
        catch (final Exception e) {
            DMRedisBackupRestore.LOGGER.log(Level.SEVERE, "Exception while storing redis properties: ", e);
        }
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
            DMRedisBackupRestore.LOGGER.log(Level.SEVERE, "Caught exception: " + ex);
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception ex) {
                DMRedisBackupRestore.LOGGER.log(Level.SEVERE, "Caught exception: " + ex);
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
                DMRedisBackupRestore.LOGGER.log(Level.SEVERE, "Caught exception: " + ex2);
            }
        }
    }
    
    private boolean handlePostRestore() throws Exception {
        DMRedisBackupRestore.LOGGER.log(Level.INFO, "########## Post Redis Restore started #######");
        disableAOFForRedis();
        initializeRedisConfigurations();
        if (startServer()) {
            this.regenerateAOF();
            shutdownServer();
            enableAOFForRedis();
            DMRedisBackupRestore.LOGGER.log(Level.INFO, "########## Post Redis Restore is completed ##########");
            return Boolean.TRUE;
        }
        throw new Exception("Cannot start Redis server. Hence, restore process cannot continue");
    }
    
    private static Properties getRedisServerProperties() throws Exception {
        final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
        final String confFile = serverHome + File.separator + DMRedisBackupRestore.REDIS_SETTINGS_CONF_FILE;
        return BackupRestoreUtil.getInstance().getPropertiesFromFile(confFile);
    }
    
    private static void enableAOFForRedis() throws Exception {
        final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
        final String redisConfFileName = serverHome + File.separator + DMRedisBackupRestore.REDIS_CONF_FILE;
        final String redisTemplateFileName = serverHome + File.separator + DMRedisBackupRestore.REDIS_TEMPLATE_CONF_FILE;
        final Properties redisProps = getRedisServerProperties();
        redisProps.setProperty("enableAOF", "yes");
        BackupRestoreUtil.findAndReplaceStrings(redisTemplateFileName, redisConfFileName, redisProps, "%", true);
    }
    
    private static void disableAOFForRedis() throws Exception {
        final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
        final String redisConfFileName = serverHome + File.separator + DMRedisBackupRestore.REDIS_CONF_FILE;
        final String redisTemplateFileName = serverHome + File.separator + DMRedisBackupRestore.REDIS_TEMPLATE_CONF_FILE;
        final Properties redisProps = getRedisServerProperties();
        redisProps.setProperty("enableAOF", "no");
        BackupRestoreUtil.findAndReplaceStrings(redisTemplateFileName, redisConfFileName, redisProps, "%", true);
        DMRedisBackupRestore.LOGGER.log(Level.INFO, "Going to disable AOF file backup for redis in redis configuration file");
        final String aofFilePath = serverHome + File.separator + DMRedisBackupRestore.AOF_FILE_PATH;
        final File aofFile = new File(aofFilePath);
        if (aofFile.exists()) {
            final boolean result = aofFile.delete();
            DMRedisBackupRestore.LOGGER.log(Level.INFO, "AOF File delete status : " + result);
            if (!result) {
                final Exception fileDelFailedEx = new Exception("AOF File Delete Failed");
                throw fileDelFailedEx;
            }
        }
        else {
            DMRedisBackupRestore.LOGGER.log(Level.INFO, "AOF file not present in setup");
        }
    }
    
    private boolean restoreRedisData() throws Exception {
        final CompressUtil compressUtil = new CompressUtil(this.informable, DMBackupPasswordHandler.getInstance().getPassword(Boolean.FALSE));
        boolean extractSuccess;
        try {
            extractSuccess = compressUtil.decompress(DMRestoreAction.sourceFile, BackupRestoreContants.REDIS_DUMP_FILE_LOC, this.serverHome);
            if (!extractSuccess) {
                throw new Exception("Unable to extract Redis dump file " + BackupRestoreContants.REDIS_DUMP_FILE_LOC);
            }
            DMRedisBackupRestore.LOGGER.log(Level.INFO, "Redis data dump extracted successfully");
        }
        catch (final Exception ex) {
            throw ex;
        }
        return extractSuccess;
    }
    
    public boolean doRedisRevert(final String tempSuffix, final boolean isAOFEnabled) throws Exception {
        final File dataFolder = new File(this.serverHome, BackupRestoreContants.REDIS_DATA_FOLDER_LOC);
        final String tempDataFolderPath = dataFolder.getAbsolutePath() + "-" + tempSuffix;
        DMRedisBackupRestore.LOGGER.log(Level.INFO, "Reverting Redis Restore..");
        if (FileUtil.isFileExists(dataFolder.getAbsolutePath()) && FileUtil.isFileExists(tempDataFolderPath)) {
            FileUtil.deleteFileOrFolder(dataFolder);
            final boolean dataCopySuccess = FileUtil.renameFolder(tempDataFolderPath, dataFolder.getAbsolutePath());
            DMRedisBackupRestore.LOGGER.log(Level.INFO, "Revert operation of data folder" + dataCopySuccess);
            return dataCopySuccess;
        }
        if (!FileUtil.isFileExists(tempDataFolderPath)) {
            FileUtil.deleteFileOrFolder(dataFolder);
        }
        if (isAOFEnabled) {
            shutdownServer();
        }
        return false;
    }
    
    public void doRedisRestore(final String tempSuffix, final boolean isAOFEnabled) throws Exception {
        boolean isRedisRestore = Boolean.FALSE;
        final FileUtil fileUtil = new FileUtil();
        final File dataFolder = new File(this.serverHome, BackupRestoreContants.REDIS_DATA_FOLDER_LOC);
        final String tempDataFolderPath = dataFolder.getAbsolutePath() + "-" + tempSuffix;
        try {
            if (new File(dataFolder.getAbsolutePath()).exists()) {
                final boolean dataCopySuccess = FileUtil.renameFolder(dataFolder.getAbsolutePath(), tempDataFolderPath);
                if (!dataCopySuccess) {
                    throw new Exception("Redis data folder backup failed. Hence, cannot proceed with restore process.");
                }
                if (this.restoreRedisData()) {
                    if (isAOFEnabled) {
                        isRedisRestore = this.handlePostRestore();
                    }
                    else {
                        isRedisRestore = Boolean.TRUE;
                    }
                    DMRedisBackupRestore.LOGGER.log(Level.INFO, "Data folder copy is successful");
                }
            }
            else if (this.restoreRedisData()) {
                if (isAOFEnabled) {
                    isRedisRestore = this.handlePostRestore();
                }
                else {
                    isRedisRestore = Boolean.TRUE;
                }
                DMRedisBackupRestore.LOGGER.log(Level.INFO, "Data folder copy is successful");
            }
        }
        catch (final Exception e) {
            if (!isRedisRestore && isAOFEnabled) {
                shutdownServer();
            }
            throw e;
        }
        finally {
            if (!isRedisRestore && isAOFEnabled) {
                shutdownServer();
                throw new Exception("Redis Restore Failed");
            }
        }
    }
    
    static {
        DMRedisBackupRestore.dbBackupProps = null;
        LOGGER = Logger.getLogger("ScheduleDBBackup");
        REDIS_SETTINGS_CONF_FILE = "conf" + File.separator + "redis_settings.conf";
        REDIS_CONF_FILE = "redis" + File.separator + "conf" + File.separator + "redis.windows.conf";
        REDIS_TEMPLATE_CONF_FILE = "redis" + File.separator + "conf" + File.separator + "redis.windows.conf.template";
        REDIS_EXE = "redis" + File.separator + "bin" + File.separator + "dmredis-server.exe";
        AOF_FILE_PATH = "redis" + File.separator + "data" + File.separator + "appendonly.aof";
        DMRedisBackupRestore.sourceClass = "DMRedisBackupRestore";
    }
    
    private static class StartRedisProcess extends Thread
    {
        @Override
        public void run() {
            DMRedisBackupRestore.LOGGER.log(Level.INFO, "Going to start Redis Server");
            try {
                final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
                final String redisExeHome = serverHome + File.separator + "redis" + File.separator + "bin";
                final String redisConfFileName = serverHome + File.separator + DMRedisBackupRestore.REDIS_CONF_FILE;
                final String redisExe = serverHome + File.separator + DMRedisBackupRestore.REDIS_EXE;
                final List<String> command = new ArrayList<String>();
                command.add(redisExe);
                command.add(redisConfFileName);
                final ProcessBuilder p = new ProcessBuilder(command);
                DMRedisBackupRestore.LOGGER.log(Level.INFO, "Command to start REDIS Server:" + p.command());
                p.directory(new File(redisExeHome));
                p.redirectErrorStream();
                String result = "";
                final Process proc = p.start();
                final BufferedReader buf = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                for (String str = buf.readLine(); str != null; str = buf.readLine()) {
                    result = result + str + "\n";
                }
                DMRedisBackupRestore.LOGGER.log(Level.INFO, "Process Builder Execution Result :" + result);
            }
            catch (final Exception e) {
                DMRedisBackupRestore.LOGGER.log(Level.WARNING, "Exception while starting server", e);
            }
        }
    }
}
