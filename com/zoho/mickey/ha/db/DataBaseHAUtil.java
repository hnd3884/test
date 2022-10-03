package com.zoho.mickey.ha.db;

import java.util.Hashtable;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.zoho.mickey.ha.DBUtil;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import com.adventnet.persistence.ConfigurationParser;
import java.io.BufferedWriter;
import java.util.Iterator;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.util.List;
import org.json.JSONException;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.sql.DriverManager;
import com.adventnet.db.adapter.postgres.DefaultPostgresDBInitializer;
import com.adventnet.persistence.PersistenceException;
import com.zoho.mickey.exception.PasswordException;
import com.adventnet.db.adapter.postgres.PostgresConfUtil;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import com.adventnet.db.adapter.postgres.PostgresBackupHandler;
import com.zoho.framework.utils.crypto.EnDecryptUtil;
import com.adventnet.persistence.PersistenceInitializer;
import com.zoho.framework.utils.FileUtils;
import java.util.Properties;
import com.adventnet.db.adapter.DBInitializer;
import com.adventnet.persistence.PersistenceUtil;
import java.util.logging.Level;
import com.adventnet.db.api.RelationalAPI;
import com.zoho.conf.Configuration;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.nio.file.Paths;
import com.zoho.mickey.ha.HAException;
import com.zoho.mickey.ha.HAImpl;
import org.json.JSONObject;
import com.zoho.mickey.ha.HAConfig;
import com.zoho.mickey.ha.filereplication.FileReplicationHandler;
import java.util.logging.Logger;

public class DataBaseHAUtil
{
    private static final Logger LOGGER;
    private static FileReplicationHandler replHandler;
    private static String dbhome;
    private static final int MAX_RETRIES_COUNT;
    private static final String LAST_SERVING_TIME = "lastServingTime";
    private static final String DB_IDENTIFIER = "DBIdentifier";
    private static final String TAKE_OVER = "takeOver";
    
    public static boolean hasCurrentDBServedAsLastMaster(final HAConfig config) throws InstantiationException, IllegalAccessException, ClassNotFoundException, HAException, FileNotFoundException, IOException {
        JSONObject servingtimeProps = new JSONObject();
        JSONObject servingtimePropsInPeer = new JSONObject();
        if (!DataBaseHAImpl.HA_FILE.exists()) {
            return !HAImpl.UNIQUE_ID_FILE.exists();
        }
        servingtimeProps = loadValue(loadDataFromFile(), "lastServingTime");
        if (servingtimeProps == null) {
            throw new HAException("last serving time of databases cannot be null");
        }
        if (!servingtimeProps.has(config.getPeerIP())) {
            return true;
        }
        getFileReplicationHandler(config).replicateFiles(config, Arrays.asList(Paths.get("conf", "ha", "ha.json").toString()));
        servingtimePropsInPeer = loadValue(loadDataFromFile(), "lastServingTime");
        final Long servingTime = Long.parseLong(String.valueOf(servingtimeProps.has(config.ipaddr()) ? servingtimeProps.get(config.ipaddr()) : Integer.valueOf(0)));
        final Long servingTimeOfPeer = Long.parseLong(String.valueOf(servingtimeProps.has(config.getPeerIP()) ? servingtimeProps.get(config.getPeerIP()) : Integer.valueOf(0)));
        final Long servingTimeInPeer = Long.parseLong(String.valueOf(servingtimePropsInPeer.has(config.ipaddr()) ? servingtimePropsInPeer.get(config.ipaddr()) : Integer.valueOf(0)));
        final Long servingTimeOfPeerInPeer = Long.parseLong(String.valueOf(servingtimePropsInPeer.has(config.getPeerIP()) ? servingtimePropsInPeer.get(config.getPeerIP()) : Integer.valueOf(0)));
        Long servingTimeOfCurrentMachine;
        if (servingTime < servingTimeInPeer) {
            servingTimeOfCurrentMachine = servingTimeInPeer;
        }
        else {
            servingTimeOfCurrentMachine = servingTime;
        }
        Long servingTimeOfPeerMachine;
        if (servingTimeOfPeer < servingTimeOfPeerInPeer) {
            servingTimeOfPeerMachine = servingTimeOfPeerInPeer;
        }
        else {
            servingTimeOfPeerMachine = servingTimeOfPeer;
        }
        if (servingTimeOfCurrentMachine < servingTimeOfPeerMachine) {
            return false;
        }
        final JSONObject takeOverStatus = loadValue(loadDataFromFile(), "takeOver");
        if (takeOverStatus != null) {
            final String takeOverStatusOfPeer = String.valueOf(takeOverStatus.has(config.getPeerIP()) ? takeOverStatus.get(config.getPeerIP()) : "success");
            if (takeOverStatusOfPeer.equalsIgnoreCase("TAKEOVER_FAILED")) {
                throw new HAException("Take over of peer machine has been failed, current machine cannot be started if peer machine takeover is failed.Please start peer machine");
            }
        }
        return true;
    }
    
    public static FileReplicationHandler getFileReplicationHandler(final HAConfig config) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (DataBaseHAUtil.replHandler == null || !DataBaseHAUtil.replHandler.getClass().getName().equals(config.ReplicationHandler())) {
            DataBaseHAUtil.replHandler = (FileReplicationHandler)Thread.currentThread().getContextClassLoader().loadClass(config.ReplicationHandler()).newInstance();
        }
        return DataBaseHAUtil.replHandler;
    }
    
    private static JSONObject loadValue(final JSONObject obj, final String key) throws FileNotFoundException, IOException {
        if (obj == null) {
            return null;
        }
        if (obj.has(key)) {
            return (JSONObject)obj.get(key);
        }
        return null;
    }
    
    public static void syncDB(final HAConfig config) throws Exception {
        getFileReplicationHandler(config).replicateFiles(config, Arrays.asList(Paths.get("conf" + File.separator + "ha" + File.separator + DataBaseHAImpl.HA_FILE.getName(), new String[0]).toString()));
        final String dataDirPath = Paths.get(Configuration.getString("db.home"), "data").toString();
        final DBInitializer dbInitializer = RelationalAPI.getInstance().getDBAdapter().getDBInitializer();
        final Properties dbProps = RelationalAPI.getInstance().getDBAdapter().getDBProps();
        if (!Paths.get(dataDirPath, new String[0]).toFile().exists() || isDBIDDifferent(config)) {
            DataBaseHAUtil.LOGGER.log(Level.SEVERE, "Data directory does not exists (or) master and slave databases have different database identifiers, taking the hot back up of master database");
            syncData(config, true, dbProps);
            if (isDBIDDifferent(config)) {
                throw new HAException("master and slave databases have different database identifiers even after attempting hot back up");
            }
        }
        else if (!isDBAlreadyPopulated(config, dbProps)) {
            final Integer port = ((Hashtable<K, Integer>)dbProps).get("Port");
            final String host = ((Hashtable<K, String>)dbProps).get("Server");
            dbInitializer.stopDBServer((int)port, host, dbProps.getProperty("username"), PersistenceUtil.getDBPasswordProvider("postgres").getPassword((Object)dbProps.getProperty("password")));
            syncData(config, true, dbProps);
            if (isDBIDDifferent(config)) {
                throw new HAException("master and slave databases have different database identifiers even after attempting hot back up");
            }
            dbInitializer.startDBServer((int)port, host, dbProps.getProperty("username"), PersistenceUtil.getDBPasswordProvider("postgres").getPassword((Object)dbProps.getProperty("password")));
        }
    }
    
    private static void syncData(final HAConfig config, final boolean cloneDB, final Properties dbProps) throws Exception {
        final String dataDirPath = Paths.get(Configuration.getString("db.home"), "data").toString();
        final Long currentMillis = System.currentTimeMillis();
        final File dbParamsFile = Paths.get(Configuration.getString("server.home"), "conf", "database_params.conf").toFile();
        final File renamedDBParamsFile = Paths.get(Configuration.getString("server.home"), "conf", "database_params_" + currentMillis.toString() + ".conf").toFile();
        final File customerConfFile = Paths.get(Configuration.getString("server.home"), "conf", "customer-config.xml").toFile();
        final File renamedCustomerConfFile = Paths.get(Configuration.getString("server.home"), "conf", "customer-config_" + currentMillis.toString() + ".xml").toFile();
        final File dataDir = new File(dataDirPath);
        final File renamedDataDir = new File(dataDirPath + "_" + currentMillis);
        Properties newProps = null;
        try {
            if (!FileUtils.moveWithRetry(dbParamsFile, renamedDBParamsFile) || !FileUtils.moveWithRetry(customerConfFile, renamedCustomerConfFile)) {
                throw new RuntimeException("Exception occurred while renaming configuration files");
            }
            getFileReplicationHandler(config).replicateFiles(config, Arrays.asList("conf" + File.separator + "database_params.conf|customer-config.xml"));
            if (!customerConfFile.exists() || !dbParamsFile.exists()) {
                throw new HAException("Mirroring of configuration files from master setup failed");
            }
            newProps = PersistenceInitializer.getDBProps(dbParamsFile.toString());
            if (dbProps.getProperty("superuser_pass") != null) {
                dbProps.remove("superuser_pass");
            }
            EnDecryptUtil.setCryptTag(getCryptKey(customerConfFile));
            if (cloneDB) {
                if (dataDir.exists() && !FileUtils.moveDirectoryWithRetry(dataDir, renamedDataDir)) {
                    throw new Exception("unable to rename data directory");
                }
                ((PostgresBackupHandler)RelationalAPI.getInstance().getDBAdapter().getBackupHandler()).executeBackup(dataDir, newProps.getProperty("repl.username"), PersistenceUtil.getDBPasswordProvider("postgres").getPassword((Object)newProps.getProperty("repl.password")), Integer.parseInt(config.getPeerDBPort()), config.getPeerIP());
                DataBaseHAUtil.LOGGER.log(Level.INFO, "hot backup of master database has been taken successfully");
            }
        }
        catch (final Exception e) {
            DataBaseHAUtil.LOGGER.log(Level.SEVERE, "Exception occurred while taking hot backup of master database :: " + e.getMessage());
            if (renamedDBParamsFile.exists()) {
                Files.deleteIfExists(dbParamsFile.toPath());
                FileUtils.moveWithRetry(renamedDBParamsFile, dbParamsFile);
            }
            if (renamedCustomerConfFile.exists()) {
                Files.deleteIfExists(customerConfFile.toPath());
                FileUtils.moveWithRetry(renamedCustomerConfFile, customerConfFile);
                EnDecryptUtil.setCryptTag(getCryptKey(customerConfFile));
            }
            if (renamedDataDir.exists()) {
                FileUtils.deleteDir(dataDir);
                if (!FileUtils.moveDirectoryWithRetry(renamedDataDir, dataDir)) {
                    DataBaseHAUtil.LOGGER.log(Level.SEVERE, "unable to rename old data directory");
                }
            }
            throw e;
        }
        final String newPassword = PersistenceUtil.getDBPasswordProvider("postgres").getPassword((Object)newProps.getProperty("password"));
        dbProps.setProperty("password", newPassword);
        Configuration.setString("ha.dbparams.updated", "true");
        Files.copy(renamedDBParamsFile.toPath(), dbParamsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        PersistenceUtil.removeKeyInDBConf("password");
        PersistenceUtil.addKeyInDBConf("password", PersistenceUtil.getDBPasswordProvider("postgres").getEncryptedPassword(newPassword));
        PersistenceUtil.removeKeyInDBConf("repl.username");
        PersistenceUtil.addKeyInDBConf("repl.username", newProps.getProperty("repl.username"));
        PersistenceUtil.removeKeyInDBConf("repl.password");
        PersistenceUtil.addKeyInDBConf("repl.password", newProps.getProperty("repl.password"));
        PersistenceUtil.removeKeyInDBConf("superuser_pass");
        Configuration.setString("gen.db.password", "false");
        final String peerIP = config.getPeerIP();
        final boolean isHostName = peerIP.contains("[a-zA-Z]+");
        DataBaseHAUtil.LOGGER.info("isHostName :: " + isHostName);
        PostgresConfUtil.grantAccessForHost(peerIP + (isHostName ? "" : "/32"), "all", "all", "md5", "host", false);
        PostgresConfUtil.grantAccessForHost(peerIP + (isHostName ? "" : "/32"), "replication", newProps.getProperty("repl.username"), "md5", "host", true);
        addIPInListenAddress(peerIP);
    }
    
    protected static String[] getCredentials(final Properties dbProps) throws PasswordException, PersistenceException {
        String password = PersistenceUtil.getDBPasswordProvider("postgres").getPassword((Object)dbProps.getProperty("superuser_pass"));
        String userName;
        if (password != null) {
            userName = "postgres";
        }
        else {
            userName = dbProps.getProperty("username");
            password = PersistenceUtil.getDBPasswordProvider("postgres").getPassword((Object)dbProps.getProperty("password"));
        }
        return new String[] { userName, password };
    }
    
    private static boolean isColdStart(final Properties dbProps) throws Exception {
        final String coldStartCmd = "select 'exists' from information_schema.tables where upper(table_name) = upper('SeqGenState');";
        final String[] credentials = getCredentials(dbProps);
        final String userName = credentials[0];
        final String password = credentials[1];
        final String dbName = ((Hashtable<K, String>)dbProps).get("DBName");
        final String host = ((Hashtable<K, String>)dbProps).get("Server");
        final int port = ((Hashtable<K, Integer>)dbProps).get("Port");
        if (!((DefaultPostgresDBInitializer)RelationalAPI.getInstance().getDBAdapter().getDBInitializer()).isDBExists(port, host, userName, password, dbName)) {
            DataBaseHAUtil.LOGGER.log(Level.INFO, "database does not exist");
            return true;
        }
        final RelationalAPI relApi = RelationalAPI.getInstance();
        try (final Connection c = DriverManager.getConnection(dbProps.getProperty("url"), userName, password);
             final DataSet ds = relApi.executeQuery(coldStartCmd, c)) {
            final boolean hasNext = ds.next();
            if (!hasNext || (hasNext && "exists".equals(ds.getValue(1)))) {
                DataBaseHAUtil.LOGGER.log(Level.INFO, "DB is populated");
                return false;
            }
        }
        return true;
    }
    
    private static boolean isDBAlreadyPopulated(final HAConfig config, final Properties dbProps) throws Exception {
        return !((DefaultPostgresDBInitializer)RelationalAPI.getInstance().getDBAdapter().getDBInitializer()).checkServerStatus((String)((Hashtable<K, String>)dbProps).get("Server"), (int)((Hashtable<K, Integer>)dbProps).get("Port"), dbProps.getProperty("username")) || (!isColdStart(dbProps) && isHAConfigured(config, dbProps));
    }
    
    private static boolean isHAConfigured(final HAConfig config, final Properties dbProps) throws Exception {
        final String nodesCountSQL = "select count(*) from FOSNODEDETAILS;";
        final String[] credentials = getCredentials(dbProps);
        final String userName = credentials[0];
        final String password = credentials[1];
        final RelationalAPI relApi = RelationalAPI.getInstance();
        try (final Connection c = DriverManager.getConnection(dbProps.getProperty("url"), userName, password);
             final DataSet ds = relApi.executeQuery(nodesCountSQL, c)) {
            final boolean hasNext = ds.next();
            if (!hasNext || (hasNext && (long)ds.getValue(1) == 0L)) {
                DataBaseHAUtil.LOGGER.log(Level.INFO, "HA is not configured");
                return false;
            }
        }
        if (getDBServingTime(config.ipaddr()) == null) {
            DataBaseHAUtil.LOGGER.info("Data is already synced. Going to sync configurations");
            syncData(config, false, dbProps);
        }
        return true;
    }
    
    private static boolean isDBIDDifferent(final HAConfig config) throws NumberFormatException, JSONException, Exception {
        final JSONObject dbIdValues;
        if ((dbIdValues = loadValue(loadDataFromFile(), "DBIdentifier")) != null && dbIdValues.has(config.getPeerIP())) {
            return Long.parseLong((String)dbIdValues.get(config.getPeerIP())) != Long.parseLong(PostgresConfUtil.getSystemIdentifier(new File(Configuration.getString("db.home"))));
        }
        throw new HAException("Database Identifier of master database couldn't be found");
    }
    
    private static void addIPInListenAddress(final String peerIP) throws IOException {
        final String extConf = Paths.get(Configuration.getString("db.home"), "ext_conf").toString();
        final List<Path> list = Files.list(Paths.get(extConf, new String[0])).sorted().collect((Collector<? super Path, ?, List<Path>>)Collectors.toList());
        final Properties extraProps = new Properties();
        Path latestFile = null;
        final Iterator<Path> iterator = list.iterator();
        while (iterator.hasNext()) {
            final Path file = latestFile = iterator.next();
            extraProps.putAll(FileUtils.readPropertyFile(file.toFile()));
        }
        DataBaseHAUtil.LOGGER.log(Level.INFO, "Adding peer-ip address in listen address if not present already");
        String listenAddresses = ((Hashtable<K, String>)extraProps).get("listen_addresses");
        if (listenAddresses != null) {
            if (listenAddresses.contains("*") || listenAddresses.contains(peerIP)) {
                DataBaseHAUtil.LOGGER.log(Level.INFO, "peerIP is already present in listen address list");
                return;
            }
            listenAddresses = listenAddresses.substring(0, listenAddresses.length() - 1) + ", " + peerIP + "'";
        }
        else {
            listenAddresses = "'localhost," + peerIP + "'";
        }
        try (final BufferedWriter bfw = Files.newBufferedWriter(latestFile, StandardOpenOption.APPEND)) {
            bfw.write("\nlisten_addresses = " + listenAddresses);
        }
    }
    
    private static String getCryptKey(final File customerConfFile) throws IOException, Exception {
        final ConfigurationParser parser = new ConfigurationParser(customerConfFile.getCanonicalPath());
        final HashMap<String, String> keyVsValue = new HashMap<String, String>();
        keyVsValue.putAll(parser.getConfigurationValues());
        final String cryptTag = keyVsValue.get("CryptTag");
        if (cryptTag == null) {
            throw new Exception("crypt tag is empty");
        }
        return cryptTag;
    }
    
    public static boolean isDataSyncedWithPrimary() throws SQLException, QueryConstructionException, IOException, PasswordException, PersistenceException {
        final Properties dbProps = RelationalAPI.getInstance().getDBAdapter().getDBProps();
        final String[] credentials = getCredentials(dbProps);
        final String userName = credentials[0];
        final String password = credentials[1];
        final String dbName = ((Hashtable<K, String>)dbProps).get("DBName");
        final String host = ((Hashtable<K, String>)dbProps).get("Server");
        final String monitorSQL = "select * from pg_stat_wal_receiver;";
        final Process p = executeCommand(((Hashtable<K, Integer>)dbProps).get("Port"), host, userName, password, dbName, monitorSQL);
        try (final BufferedReader ipBuf = new BufferedReader(new InputStreamReader(p.getInputStream()));
             final BufferedReader errBuf = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
            String dataRetrieved = null;
            while ((dataRetrieved = ipBuf.readLine()) != null) {
                if (dataRetrieved.contains("streaming")) {
                    return true;
                }
            }
            String errorData = null;
            String errorStream;
            while ((errorStream = errBuf.readLine()) != null) {
                errorData += errorStream;
            }
            if (errorData != null) {
                DataBaseHAUtil.LOGGER.log(Level.WARNING, "Error occurred while syncing data with master : " + host + " :: " + errorData);
            }
        }
        finally {
            p.destroy();
        }
        return false;
    }
    
    public static boolean isTimeInSync(final HAConfig config, final Properties dbProps) throws Exception {
        final String dbName = ((Hashtable<K, String>)dbProps).get("DBName");
        final String[] credentials = getCredentials(dbProps);
        final String userName = credentials[0];
        final String password = credentials[1];
        final String timingOfMaster = getTiming(config.getPeerIP(), Integer.parseInt(config.getPeerDBPort()), userName, password, dbName);
        if (timingOfMaster == null) {
            throw new Exception("Unable to get the timing of peer machine");
        }
        final Double masterTime = Double.parseDouble(timingOfMaster);
        final Long startTime = System.currentTimeMillis();
        final String timingOfSlave = getTiming(((Hashtable<K, String>)dbProps).get("Server"), ((Hashtable<K, Integer>)dbProps).get("Port"), userName, password, dbName);
        final Long endTime = System.currentTimeMillis();
        if (timingOfSlave == null) {
            throw new Exception("Unable to get the timing of machine");
        }
        final Double slaveTime = Double.parseDouble(timingOfSlave) - (endTime - startTime);
        final Double timeDiff = slaveTime - masterTime;
        return timeDiff >= -10000.0 && timeDiff <= 10000.0;
    }
    
    private static String getTiming(final String host, final Integer port, final String userName, final String password, final String database) throws IOException {
        String dataRetrieved = null;
        for (int retrycount = 0; retrycount < DataBaseHAUtil.MAX_RETRIES_COUNT; ++retrycount) {
            Process p = null;
            try {
                final String currTimeCmd = "select (extract(EPOCH FROM now() AT TIME ZONE 'GMT') * 1000)";
                p = executeCommand(port, host, userName, password, database, currTimeCmd);
                try (final BufferedReader ipBuf = new BufferedReader(new InputStreamReader(p.getInputStream()));
                     final BufferedReader errBuf = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
                    while ((dataRetrieved = ipBuf.readLine()) != null && (dataRetrieved == null || !dataRetrieved.matches(".*\\d.*"))) {}
                    if (dataRetrieved != null) {
                        break;
                    }
                    String errorData = null;
                    String errorStream;
                    while ((errorStream = errBuf.readLine()) != null) {
                        errorData += errorStream;
                    }
                    if (errorData != null) {
                        DataBaseHAUtil.LOGGER.log(Level.WARNING, "Error occurred while trying to get timing of server : " + host + " :: " + errorData);
                    }
                }
            }
            catch (final IOException e) {
                DataBaseHAUtil.LOGGER.log(Level.SEVERE, " \n \n Exception while getting the timing :: {0}.", e);
            }
            finally {
                p.destroy();
            }
        }
        return dataRetrieved;
    }
    
    private static Process executeCommand(final int port, final String host, final String userName, final String passwd, final String dbName, final String commandToBeExecute) throws IOException {
        try {
            final List<String> commandList = new ArrayList<String>();
            commandList.add(new File(Paths.get(DataBaseHAUtil.dbhome, "bin", "psql").toString()).getCanonicalPath());
            commandList.add("-U");
            commandList.add(userName);
            commandList.add("-p");
            commandList.add(String.valueOf(port));
            if (!host.equals("localhost")) {
                commandList.add("-h");
                commandList.add(host);
            }
            else {
                final String hostAddress = RelationalAPI.getInstance().getDBAdapter().getDBInitializer().getHostAddressName(host);
                DataBaseHAUtil.LOGGER.log(Level.INFO, "hostAddress of localhost is {0}", hostAddress);
                commandList.add("-h");
                commandList.add(hostAddress);
            }
            commandList.add("-c");
            commandList.add(commandToBeExecute);
            commandList.add("-d");
            commandList.add(dbName);
            commandList.add("-w");
            final ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            final Map<String, String> environment = processBuilder.environment();
            environment.put("PGPASSWORD", passwd);
            return processBuilder.start();
        }
        catch (final Exception ee) {
            ee.printStackTrace();
            return null;
        }
    }
    
    public static void updateDBServingTime(final String ipAddress, final String time) throws FileNotFoundException, IOException {
        JSONObject obj = loadDataFromFile();
        obj = ((obj == null) ? new JSONObject() : obj);
        JSONObject servingTime = new JSONObject();
        if (obj.has("lastServingTime")) {
            servingTime = (JSONObject)obj.get("lastServingTime");
        }
        servingTime.put(ipAddress, (Object)time);
        obj.put("lastServingTime", (Object)servingTime);
        writeToFile(obj);
    }
    
    public static String getDBServingTime(final String ipAddress) throws FileNotFoundException, IOException {
        JSONObject obj = loadDataFromFile();
        obj = ((obj == null) ? new JSONObject() : obj);
        JSONObject servingTime = new JSONObject();
        if (obj.has("lastServingTime")) {
            servingTime = (JSONObject)obj.get("lastServingTime");
        }
        return servingTime.has(ipAddress) ? servingTime.getString(ipAddress) : null;
    }
    
    protected static void updateTakeOverFailedStatus(final String ipAddress) throws FileNotFoundException, IOException {
        JSONObject obj = loadDataFromFile();
        obj = ((obj == null) ? new JSONObject() : obj);
        JSONObject takeOver = new JSONObject();
        if (obj.has("takeOver")) {
            takeOver = (JSONObject)obj.get("takeOver");
        }
        takeOver.put(ipAddress, (Object)"TAKEOVER_FAILED");
        obj.put("takeOver", (Object)takeOver);
        writeToFile(obj);
    }
    
    protected static void deleteFailedTakeOverStatus(final String ipAddress) throws FileNotFoundException, IOException {
        JSONObject obj = loadDataFromFile();
        obj = ((obj == null) ? new JSONObject() : obj);
        if (obj.has("takeOver")) {
            obj.remove("takeOver");
        }
        writeToFile(obj);
    }
    
    public static void updateDatabaseIdentifier(final String ipAddress, final String dbIdentifier) throws FileNotFoundException, IOException {
        JSONObject obj = loadDataFromFile();
        obj = ((obj == null) ? new JSONObject() : obj);
        JSONObject databaseIdentifier = new JSONObject();
        if (obj.has("DBIdentifier")) {
            databaseIdentifier = (JSONObject)obj.get("DBIdentifier");
        }
        databaseIdentifier.put(ipAddress, (Object)dbIdentifier);
        obj.put("DBIdentifier", (Object)databaseIdentifier);
        writeToFile(obj);
    }
    
    private static void writeToFile(final JSONObject obj) throws FileNotFoundException, IOException {
        if (!DataBaseHAImpl.HA_FILE.exists()) {
            DataBaseHAUtil.LOGGER.log(Level.INFO, "creating ha.json file");
            DataBaseHAImpl.HA_FILE.getParentFile().mkdirs();
            DataBaseHAImpl.HA_FILE.createNewFile();
        }
        Files.write(DataBaseHAImpl.HA_FILE.toPath(), obj.toString().getBytes(), new OpenOption[0]);
    }
    
    private static JSONObject loadDataFromFile() throws FileNotFoundException, IOException {
        if (!DataBaseHAImpl.HA_FILE.exists()) {
            DataBaseHAUtil.LOGGER.log(Level.INFO, "ha.json doesn't exist.");
            return new JSONObject();
        }
        final String data = new String(Files.readAllBytes(DataBaseHAImpl.HA_FILE.toPath()));
        if (data != null && !data.isEmpty()) {
            return new JSONObject(data);
        }
        return new JSONObject();
    }
    
    public static void pushFile(final HAConfig config, final String fileName) throws Exception {
        final Properties replProps = FileUtils.readPropertyFile(new File(config.replConf()));
        String dirName = "";
        String fileToPush = "";
        if (fileName.contains("\\")) {
            final int index = fileName.lastIndexOf("\\");
            dirName = fileName.substring(0, index);
            fileToPush = fileName.substring(index + 1);
        }
        Process p = null;
        int exitValue = 0;
        final List<String> commandList = new ArrayList<String>();
        commandList.add("cmd");
        commandList.add("/c");
        commandList.add("robocopy");
        commandList.add("..\\" + dirName);
        commandList.add("\\\\" + config.getPeerIP() + "\\" + replProps.getProperty("repl.remoteinstallationDir") + "\\" + dirName);
        commandList.add("/MIR");
        commandList.add("/tee");
        commandList.add(fileToPush);
        commandList.add("/R:0");
        commandList.add("/W:0");
        DataBaseHAUtil.LOGGER.info("robcopy command to be executed" + commandList);
        try {
            DataBaseHAUtil.LOGGER.log(Level.INFO, "Command to be executed ::: {0}", commandList);
            final ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            processBuilder.directory(Paths.get(Configuration.getString("server.home"), "bin").toFile());
            p = processBuilder.start();
            RelationalAPI.getInstance().getDBAdapter().getDBInitializer().dump(p, DataBaseHAUtil.LOGGER);
            p.waitFor();
            exitValue = p.exitValue();
            if (exitValue < 8) {
                DataBaseHAUtil.LOGGER.info("pushing completed. ExitValue :[ " + exitValue + " ]");
            }
            else {
                if (exitValue > 7 && exitValue < 17) {
                    throw new Exception("Error in mirroring Files. ErrorCode :[ " + exitValue + " ]");
                }
                throw new Exception("Error in executing script. ErrorCode :[ " + exitValue + " ]");
            }
        }
        finally {
            p.destroy();
        }
    }
    
    public static void promoteDB(final HAConfig config) throws HAException {
        final File configureHA = new File(Configuration.getString("db.home") + File.separator + "data" + File.separator + "ha_configured");
        final File triggerFile = new File(Configuration.getString("db.home") + File.separator + "data" + File.separator + "pg_promote");
        final Properties dbProps = RelationalAPI.getInstance().getDBAdapter().getDBProps();
        boolean takeover_complete = false;
        try {
            if (!triggerFile.createNewFile()) {
                throw new HAException("Unable to create trigger file at " + triggerFile.getAbsolutePath());
            }
            DataBaseHAUtil.LOGGER.log(Level.INFO, "Trigger file created successfully at {0}", triggerFile.getAbsoluteFile());
            final int i = 0;
            try (final Connection connection = getConnection(dbProps)) {
                while (i < config.dbTakeOverFailureRetryCount() && !takeover_complete) {
                    if (!RelationalAPI.getInstance().getDBAdapter().isReadOnly(connection)) {
                        takeover_complete = true;
                        break;
                    }
                    Thread.sleep(1000L);
                }
            }
            if (!takeover_complete) {
                throw new HAException("Database takeover is not complete");
            }
        }
        catch (final HAException haException) {
            throw haException;
        }
        catch (final Exception e) {
            throw new HAException(e.getMessage(), e);
        }
        finally {
            if (takeover_complete) {
                try {
                    updateTime(config.ipaddr(), dbProps);
                }
                catch (final Exception e2) {
                    throw new HAException("unable to update serving time of database", e2);
                }
                if (triggerFile.exists()) {
                    FileUtils.deleteFile(triggerFile);
                }
                try {
                    if (!configureHA.exists()) {
                        final Path path = Files.createFile(configureHA.toPath(), (FileAttribute<?>[])new FileAttribute[0]);
                        if (path == null || !path.toFile().exists()) {
                            throw new HAException("unable to create " + configureHA + " file");
                        }
                    }
                    DataBaseHAUtil.LOGGER.log(Level.INFO, "created ha_configured file in data folder");
                    executeSwitchWALQuery(dbProps);
                }
                catch (final Exception ex) {
                    throw new HAException("Exception occurred while executing switch wal query", ex);
                }
            }
        }
    }
    
    protected static void assertBuildNumber(final HAConfig config) throws DataAccessException {
        final DataObject dobj = DBUtil.getMaxBuildNumberDO();
        DataBaseHAUtil.LOGGER.log(Level.FINER, " Build number check : {0}", new Object[] { dobj });
        if (dobj.isEmpty()) {
            return;
        }
        final Long latestBuildNumber = Long.parseLong(dobj.getFirstValue("FOSNodeDetails", "BUILD_NUMBER").toString());
        DataBaseHAUtil.LOGGER.log(Level.FINE, "HA config: " + config);
        final long currentBuildNumber = config.versionHandler().getCurrentBuildNumber();
        final Criteria cr = new Criteria(new Column("FOSNodeDetails", "IP"), (Object)config.ipaddr(), 0);
        if (dobj.getRow("FOSNodeDetails", cr) == null) {
            DataBaseHAUtil.LOGGER.log(Level.FINER, "Latest build number in DB : {0}  Current build Number by version Handler {1}", new Object[] { latestBuildNumber, currentBuildNumber });
            if (currentBuildNumber == latestBuildNumber) {
                return;
            }
            if (currentBuildNumber < latestBuildNumber) {
                throw new IllegalStateException("Other node in the system has higher build number [" + latestBuildNumber + "]. Pull the changes from the other node using Replication script and restart the product ");
            }
            throw new IllegalStateException("Invalid scenario. Cannot add the node with high build number than latestbuildnumber into HA system");
        }
        else if (config.versionHandler().getCurrentBuildNumber() > latestBuildNumber) {
            throw new IllegalStateException("Build number returned by version handler is greater than DB's value. This may be due to PPM revert failure");
        }
    }
    
    protected static void addEntries(final String currentIPaddr, final String peerIPAddr) throws DataAccessException {
        DBUtil.addEntry(currentIPaddr, "alive");
        if (peerIPAddr != null) {
            DBUtil.addEntry(peerIPAddr, "down");
        }
    }
    
    protected static void removeEntry(final String ipaddr, final String hostName) throws DataAccessException {
        DBUtil.removeEntry(ipaddr, hostName);
    }
    
    public static void updateServingTime(final String currentIPaddr) throws DataAccessException {
        final DataObject dobj = DataAccess.get("FOSNodeDetails", new Criteria(new Column("FOSNodeDetails", "IP"), (Object)currentIPaddr, 0));
        if (!dobj.isEmpty() && dobj.getFirstRow("FOSNodeDetails").get("LASTSERVINGTIME") == null) {
            DBUtil.updateServingTime(currentIPaddr);
        }
    }
    
    public static void updateTime(final String ipAddress, final Properties dbProps) throws Exception {
        final String sql = "select (extract(EPOCH FROM now() AT TIME ZONE 'GMT') * 1000)";
        final RelationalAPI relApi = RelationalAPI.getInstance();
        String time;
        try (final Connection conn = getConnection(dbProps);
             final DataSet ds = relApi.executeQuery(sql, conn)) {
            ds.next();
            time = ds.getAsString(1);
        }
        catch (final Exception e) {
            DataBaseHAUtil.LOGGER.log(Level.SEVERE, "Exception occurred while getting current time from database");
            throw e;
        }
        updateDBServingTime(ipAddress, time.substring(0, time.indexOf(".")));
    }
    
    public static Connection getConnection(final Properties dbProps) throws Exception {
        final RelationalAPI relApi = RelationalAPI.getInstance();
        Connection conn = null;
        if (dbProps == null) {
            conn = relApi.getConnection();
        }
        else {
            final String[] credentials = getCredentials(dbProps);
            final String userName = credentials[0];
            final String password = credentials[1];
            String url = dbProps.getProperty("url");
            final StringBuilder builder = new StringBuilder(url);
            final String dbName = dbProps.getProperty("DBName");
            final int lastIndex = url.lastIndexOf(dbName);
            builder.replace(lastIndex, lastIndex + dbName.length(), "template1");
            url = builder.toString();
            conn = DriverManager.getConnection(url, userName, password);
        }
        return conn;
    }
    
    public static void executeSwitchWALQuery(final Properties dbProps) throws Exception {
        final String switchWalQuery = "select pg_switch_wal()";
        final RelationalAPI relApi = RelationalAPI.getInstance();
        try (final Connection conn = getConnection(dbProps);
             final DataSet ds = relApi.executeQuery(switchWalQuery, conn)) {
            DataBaseHAUtil.LOGGER.log(Level.INFO, "executing switch WAL query");
        }
        catch (final SQLException e) {
            DataBaseHAUtil.LOGGER.log(Level.SEVERE, "Exception occurred while executing switch WAL query");
            throw e;
        }
    }
    
    static {
        LOGGER = Logger.getLogger(DataBaseHAUtil.class.getName());
        DataBaseHAUtil.dbhome = Configuration.getString("db.home");
        MAX_RETRIES_COUNT = Integer.parseInt(Configuration.getString("DBStartupRetries", "120"));
    }
}
