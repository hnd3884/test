package com.adventnet.db.adapter.postgres;

import java.util.Hashtable;
import com.zoho.conf.Configuration;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import com.zoho.framework.utils.OSCheckUtil;
import java.util.StringTokenizer;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import com.zoho.framework.utils.FileUtils;
import java.io.File;
import java.util.Properties;
import java.util.List;
import java.util.logging.Level;
import java.io.IOException;
import java.util.logging.Logger;

public class PostgresConfUtil
{
    private static String confFileWithPath;
    private static final String CONF_RECORD_TEMPLATE = "@type@    @database@             @user@             @address@            @authmethodhost@";
    private static final String CONF_RECORD_PATTERN = "(\\w+)\\s*(\\w+)\\s*(\\w+)\\s*(\\p{Graph}+)\\s*(\\w+)";
    private static final Logger LOGGER;
    private static final String COMMENT_CHAR = "#";
    private static DefaultPostgresDBInitializer pgInitializer;
    private static String defaultAuthMode;
    private static final int ADDED = 1;
    private static final int MODIFIED = 0;
    private static final int REMOVED = -1;
    private static String server_home;
    
    static void setDBInitializer(final DefaultPostgresDBInitializer initializer) {
        PostgresConfUtil.pgInitializer = initializer;
    }
    
    static void setDefaultAuthMode(final String authMode) {
        PostgresConfUtil.defaultAuthMode = authMode;
    }
    
    public static void grantAccessForHost(final String hostName, final boolean reloadConf) throws IOException {
        grantAccessForHost(hostName, null, null, null, null, reloadConf);
    }
    
    public static void grantAccessForHost(final String hostName, final String database, final String user, final String authMode, final String type, final boolean reloadConf) throws IOException {
        if (hostName == null) {
            throw new IllegalArgumentException("HostName cannot be null ");
        }
        boolean isModified = false;
        final List<Properties> existingConfFileRecords = getExistingConfFileRecords();
        Properties confRec = null;
        int operation;
        if ((confRec = getExactMatchingRecord(existingConfFileRecords, hostName, database, authMode)) == null) {
            operation = 1;
            isModified = true;
            confRec = createDefaultRecord();
            ((Hashtable<HBAFields, String>)confRec).put(HBAFields.ADDRESS, hostName);
        }
        else {
            operation = 0;
        }
        if (type != null && isExistingValueModified(confRec, HBAFields.TYPE, type)) {
            isModified = true;
            ((Hashtable<HBAFields, String>)confRec).put(HBAFields.TYPE, type);
        }
        if (user != null && isExistingValueModified(confRec, HBAFields.USER, user)) {
            isModified = true;
            ((Hashtable<HBAFields, String>)confRec).put(HBAFields.USER, user);
        }
        if (authMode != null && isExistingValueModified(confRec, HBAFields.AUTH_MODE, authMode)) {
            isModified = true;
            ((Hashtable<HBAFields, String>)confRec).put(HBAFields.AUTH_MODE, authMode);
        }
        if (database != null && isExistingValueModified(confRec, HBAFields.DATABASE, database)) {
            isModified = true;
            ((Hashtable<HBAFields, String>)confRec).put(HBAFields.DATABASE, database);
        }
        if (operation == 0) {
            PostgresConfUtil.LOGGER.log(Level.INFO, "Existing conf record [{0}]", getExactMatchingRecord(existingConfFileRecords, hostName, database));
        }
        PostgresConfUtil.LOGGER.log(Level.INFO, "New conf record [{0}]", confRec);
        if (isModified) {
            writeHbaRecords(confRec, operation, reloadConf);
        }
        else {
            PostgresConfUtil.LOGGER.warning("Both configurations are identical, hence ignoring overwrite pg_hba.conf file.");
        }
    }
    
    public static void changeAuthModeForUnixSocket(final String authMode, final boolean reloadConf) throws IOException {
        final String database = "all";
        final String user = "all";
        final String type = "local";
        final String hostName = "";
        boolean isModified = false;
        final List<Properties> existingConfFileRecords = getExistingConfFileRecords();
        Properties confRec = null;
        if ((confRec = getExactMatchingRecord(existingConfFileRecords, hostName, database)) == null) {
            throw new IllegalArgumentException("The record does not exist!!");
        }
        if (authMode != null && !authMode.trim().isEmpty() && isExistingValueModified(confRec, HBAFields.AUTH_MODE, authMode)) {
            isModified = true;
            ((Hashtable<HBAFields, String>)confRec).put(HBAFields.AUTH_MODE, authMode);
        }
        if (isModified) {
            PostgresConfUtil.LOGGER.log(Level.INFO, "Existing conf record [{0}]", getExactMatchingRecord(existingConfFileRecords, hostName, database));
            writeHbaRecords(confRec, 0, reloadConf);
            PostgresConfUtil.LOGGER.log(Level.INFO, "New conf record [{0}]", confRec);
        }
        else {
            PostgresConfUtil.LOGGER.warning("Both configurations are identical, hence ignoring overwrite pg_hba.conf file.");
        }
    }
    
    public static void revokeAccessForHost(final String hostName, final String database, final boolean reloadConf) throws IOException {
        revokeAccessForHost(hostName, database, reloadConf, null);
    }
    
    public static void revokeAccessForHost(final String hostName, final String database, final boolean reloadConf, final String authMode) throws IOException {
        if (hostName == null) {
            throw new IllegalArgumentException("HostName cannot be null ");
        }
        final List<Properties> existingConfFileRecords = getExistingConfFileRecords();
        final Properties confRec = getExactMatchingRecord(existingConfFileRecords, hostName, database, authMode);
        if (confRec == null) {
            PostgresConfUtil.LOGGER.log(Level.INFO, "Unknown hostname[{0}] specified. Revoke Access operation ignored", hostName);
        }
        else {
            PostgresConfUtil.LOGGER.log(Level.INFO, "Removing conf record ::: {0}", confRec);
            writeHbaRecords(confRec, -1, reloadConf);
        }
    }
    
    public static boolean isDefaultModeTrust(final String hostName, final String database) throws IOException {
        if (hostName == null) {
            throw new IllegalArgumentException("HostName cannot be null ");
        }
        final List<Properties> existingConfFileRecords = getExistingConfFileRecords();
        final Properties confRec = getExactMatchingRecord(existingConfFileRecords, hostName, database);
        if (confRec == null) {
            throw new IllegalArgumentException("Record not found");
        }
        PostgresConfUtil.LOGGER.log(Level.INFO, "Matching HBA Entry ::: {0}", confRec);
        return ((Hashtable<K, String>)confRec).get(HBAFields.AUTH_MODE).equalsIgnoreCase("trust");
    }
    
    public static Properties createDefaultRecord() {
        final Properties defaultRecord = new Properties();
        ((Hashtable<HBAFields, String>)defaultRecord).put(HBAFields.TYPE, "host");
        ((Hashtable<HBAFields, String>)defaultRecord).put(HBAFields.DATABASE, "all");
        ((Hashtable<HBAFields, String>)defaultRecord).put(HBAFields.USER, "all");
        ((Hashtable<HBAFields, String>)defaultRecord).put(HBAFields.ADDRESS, "localhost");
        ((Hashtable<HBAFields, String>)defaultRecord).put(HBAFields.AUTH_MODE, PostgresConfUtil.defaultAuthMode);
        return defaultRecord;
    }
    
    private static String constructHbaRecordAsString(final Properties hbaRecord) {
        final StringBuilder hbaRecBuff = new StringBuilder("@type@    @database@             @user@             @address@            @authmethodhost@");
        for (final HBAFields key : HBAFields.values()) {
            final String value = ((Hashtable<K, String>)hbaRecord).get(key);
            final int tokenIndex = hbaRecBuff.indexOf(key.getTokenString());
            PostgresConfUtil.LOGGER.log(Level.FINE, "token {0} replaced to {1}", new Object[] { key.getTokenString(), value });
            hbaRecBuff.replace(tokenIndex, tokenIndex + key.getTokenString().length(), value);
        }
        return hbaRecBuff.toString();
    }
    
    private static synchronized void writeHbaRecords(final Properties hbaRecord, final int operation, final boolean reloadConf) throws IOException {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        FileReader fr = null;
        BufferedReader buffReader = null;
        final File oldConfigFile = new File(PostgresConfUtil.confFileWithPath + ".old");
        final String configFileChecksum = FileUtils.getDigest(PostgresConfUtil.confFileWithPath);
        if (configFileChecksum == null) {
            throw new IOException("Problem while getting checksum of pg_hba.conf file");
        }
        backupConfFile(new File(PostgresConfUtil.confFileWithPath), oldConfigFile);
        validateChecksum(new File(PostgresConfUtil.confFileWithPath), oldConfigFile);
        try {
            switch (operation) {
                case 1: {
                    fos = new FileOutputStream(PostgresConfUtil.confFileWithPath, true);
                    PostgresConfUtil.LOGGER.log(Level.INFO, "Adding conf record for host {0}", ((Hashtable<K, Object>)hbaRecord).get(HBAFields.ADDRESS));
                    fos.write(constructHbaRecordAsString(hbaRecord).getBytes());
                    fos.write(System.getProperty("line.separator").getBytes());
                    break;
                }
                case 0: {
                    fos = new FileOutputStream(PostgresConfUtil.confFileWithPath);
                    osw = new OutputStreamWriter(fos);
                    bw = new BufferedWriter(osw);
                    fr = new FileReader(oldConfigFile);
                    buffReader = new BufferedReader(fr);
                    String line = null;
                    while ((line = buffReader.readLine()) != null) {
                        if (line.length() != 0 && !line.startsWith("#")) {
                            final Properties existingHbaRecord = parseHbaRecord(line);
                            if (((Hashtable<K, Object>)hbaRecord).get(HBAFields.ADDRESS).toString().equals(((Hashtable<K, Object>)existingHbaRecord).get(HBAFields.ADDRESS)) && ((Hashtable<K, Object>)hbaRecord).get(HBAFields.DATABASE).toString().equals(((Hashtable<K, Object>)existingHbaRecord).get(HBAFields.DATABASE))) {
                                if (operation != 0) {
                                    PostgresConfUtil.LOGGER.log(Level.INFO, "Configuration removed ::: [{0}]", line);
                                    continue;
                                }
                                line = constructHbaRecordAsString(hbaRecord);
                                PostgresConfUtil.LOGGER.log(Level.INFO, "Modified configuration ::: {0}", line);
                            }
                        }
                        bw.append((CharSequence)line);
                        bw.newLine();
                    }
                    break;
                }
                case -1: {
                    fos = new FileOutputStream(PostgresConfUtil.confFileWithPath);
                    osw = new OutputStreamWriter(fos);
                    bw = new BufferedWriter(osw);
                    fr = new FileReader(oldConfigFile);
                    buffReader = new BufferedReader(fr);
                    String line = null;
                    while ((line = buffReader.readLine()) != null) {
                        if (line.length() != 0 && !line.startsWith("#")) {
                            final Properties existingHbaRecord = parseHbaRecord(line);
                            if (line.startsWith("local") && ((Hashtable<K, Object>)existingHbaRecord).get(HBAFields.DATABASE).equals("all")) {
                                PostgresConfUtil.LOGGER.log(Level.SEVERE, "Configuration must not be removed ::: {0}", line);
                                PostgresConfUtil.LOGGER.log(Level.SEVERE, "Ignoring the REMOVE/MODIFY operation");
                            }
                            else if (((Hashtable<K, Object>)hbaRecord).get(HBAFields.ADDRESS).toString().equals(((Hashtable<K, Object>)existingHbaRecord).get(HBAFields.ADDRESS)) && ((Hashtable<K, Object>)hbaRecord).get(HBAFields.DATABASE).toString().equals(((Hashtable<K, Object>)existingHbaRecord).get(HBAFields.DATABASE))) {
                                if (operation != 0) {
                                    PostgresConfUtil.LOGGER.log(Level.INFO, "Configuration removed ::: [{0}]", line);
                                    continue;
                                }
                                line = constructHbaRecordAsString(hbaRecord);
                                PostgresConfUtil.LOGGER.log(Level.INFO, "Modified configuration ::: {0}", line);
                            }
                        }
                        bw.append((CharSequence)line);
                        bw.newLine();
                    }
                    break;
                }
            }
        }
        catch (final IOException ex) {
            PostgresConfUtil.LOGGER.log(Level.WARNING, "Caught Exception while writing HBA records...", ex);
            backupConfFile(oldConfigFile, new File(PostgresConfUtil.confFileWithPath));
            if (!configFileChecksum.equalsIgnoreCase(FileUtils.getDigest(PostgresConfUtil.confFileWithPath))) {
                throw new IOException("Checksum mismatch while restoring from backup file", ex);
            }
            throw ex;
        }
        finally {
            if (bw != null) {
                bw.close();
            }
            if (osw != null) {
                osw.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (buffReader != null) {
                buffReader.close();
            }
            if (fr != null) {
                fr.close();
            }
        }
        if (reloadConf && PostgresConfUtil.pgInitializer != null) {
            PostgresConfUtil.LOGGER.log(Level.INFO, "Reloading pg_hba.conf after modified configuration :: {0}", hbaRecord);
            PostgresConfUtil.pgInitializer.executePgCtlUtility(5432, null, "reload");
        }
        else {
            PostgresConfUtil.LOGGER.log(Level.INFO, "reloadConf ::: {0} || pgInitializer initialized ::: {1}", new Object[] { reloadConf, PostgresConfUtil.pgInitializer != null });
            PostgresConfUtil.LOGGER.log(Level.SEVERE, "Reload configuration step skipped.");
        }
    }
    
    private static void backupConfFile(final File sourceFile, final File backupFile) throws IOException {
        if (!backupFile.exists()) {
            PostgresConfUtil.LOGGER.log(Level.INFO, "Creating pg_hba.conf.old file :: {0}", backupFile.createNewFile() ? "Success" : "Failure");
        }
        else {
            PostgresConfUtil.LOGGER.log(Level.INFO, "Deleting old backup pg_hba.conf file :: {0}", backupFile.delete() ? "Success" : "Failure");
        }
        FileUtils.copyFile(sourceFile, backupFile);
    }
    
    private static void validateChecksum(final File sourceFile, final File backupFile) throws IOException {
        final String configFileMD5 = FileUtils.getDigest(sourceFile.getAbsolutePath());
        final String backupConfigFileMD5 = FileUtils.getDigest(backupFile.getAbsolutePath());
        if (configFileMD5 == null || !configFileMD5.equalsIgnoreCase(backupConfigFileMD5)) {
            throw new IOException("Checksum mismatch between source file and backup file");
        }
    }
    
    private static List<Properties> getExistingConfFileRecords() throws FileNotFoundException, IOException {
        final List<Properties> existingRecords = new ArrayList<Properties>();
        BufferedReader buff = null;
        try {
            buff = new BufferedReader(new FileReader(PostgresConfUtil.confFileWithPath));
            String line = null;
            while ((line = buff.readLine()) != null) {
                if (!line.startsWith("#")) {
                    if (line.trim().equals("")) {
                        continue;
                    }
                    final Properties record = parseHbaRecord(line);
                    existingRecords.add(record);
                }
            }
            return existingRecords;
        }
        finally {
            if (buff != null) {
                buff.close();
            }
        }
    }
    
    private static Properties parseHbaRecord(final String line) {
        final Properties record = new Properties();
        final Scanner s = new Scanner(line);
        final String findInLine = s.findInLine("(\\w+)\\s*(\\w+)\\s*(\\w+)\\s*(\\p{Graph}+)\\s*(\\w+)");
        final StringTokenizer token = new StringTokenizer(findInLine, " \t", false);
        int index = 0;
        final HBAFields[] values = HBAFields.values();
        while (token.hasMoreTokens()) {
            final String tokStr = token.nextToken();
            ((Hashtable<HBAFields, String>)record).put(values[index], tokStr);
            ++index;
        }
        if (record.get(HBAFields.AUTH_MODE) == null) {
            ((Hashtable<HBAFields, Object>)record).put(HBAFields.AUTH_MODE, ((Hashtable<K, Object>)record).get(HBAFields.ADDRESS));
            ((Hashtable<HBAFields, String>)record).put(HBAFields.ADDRESS, "");
        }
        PostgresConfUtil.LOGGER.log(Level.FINE, "conf record ::: {0}", record);
        s.close();
        return record;
    }
    
    private static boolean isExistingValueModified(final Properties prop, final HBAFields field, final String newValue) {
        return !((Hashtable<K, String>)prop).get(field).equals(newValue);
    }
    
    private static Properties getExactMatchingRecord(final List<Properties> existingRecords, final String address, final String database) {
        return getExactMatchingRecord(existingRecords, address, database, null);
    }
    
    private static Properties getExactMatchingRecord(final List<Properties> existingRecords, final String address, final String database, final String authMode) {
        Properties exactMatch = null;
        for (int i = 0; i < existingRecords.size(); ++i) {
            final Properties temp = existingRecords.get(i);
            if (((Hashtable<K, String>)temp).get(HBAFields.ADDRESS).equals(address) && ((Hashtable<K, String>)temp).get(HBAFields.DATABASE).equals(database)) {
                if (authMode == null) {
                    exactMatch = temp;
                    return exactMatch;
                }
                if (authMode != null && ((Hashtable<K, String>)temp).get(HBAFields.AUTH_MODE).equals(authMode)) {
                    exactMatch = temp;
                    return exactMatch;
                }
            }
        }
        return exactMatch;
    }
    
    public static void reloadConf() throws IOException {
        if (PostgresConfUtil.pgInitializer != null) {
            PostgresConfUtil.LOGGER.info("Going to reload conf");
            PostgresConfUtil.pgInitializer.executePgCtlUtility(5432, null, "reload");
        }
    }
    
    public static List<Properties> getAllTrustModeRecords() throws IOException {
        final List<Properties> trustedModeRecords = new ArrayList<Properties>();
        final List<Properties> existingConfFileRecords = getExistingConfFileRecords();
        for (int i = 0; i < existingConfFileRecords.size(); ++i) {
            final Properties temp = existingConfFileRecords.get(i);
            if (((Hashtable<K, String>)temp).get(HBAFields.AUTH_MODE).equals("trust")) {
                trustedModeRecords.add((Properties)temp.clone());
            }
        }
        return trustedModeRecords;
    }
    
    public static void changeAuthModeToMD5(final Properties property) throws IOException {
        if (property == null || property.isEmpty()) {
            throw new IllegalArgumentException("property object cannot be null / empty");
        }
        if (property.get(HBAFields.AUTH_MODE) == null || ((Hashtable<K, Object>)property).get(HBAFields.AUTH_MODE).toString().isEmpty()) {
            throw new IllegalArgumentException("AUTH MODE is null or empty");
        }
        if (((Hashtable<K, Object>)property).get(HBAFields.AUTH_MODE).toString().equals("md5")) {
            return;
        }
        revokeAccessForHost(((Hashtable<K, String>)property).get(HBAFields.ADDRESS), ((Hashtable<K, String>)property).get(HBAFields.DATABASE), false, ((Hashtable<K, String>)property).get(HBAFields.AUTH_MODE));
        grantAccessForHost(((Hashtable<K, String>)property).get(HBAFields.ADDRESS), ((Hashtable<K, String>)property).get(HBAFields.DATABASE), ((Hashtable<K, String>)property).get(HBAFields.USER), "md5", ((Hashtable<K, String>)property).get(HBAFields.TYPE), true);
    }
    
    public static final String getSystemIdentifier(final File dbHomeDirectory) throws Exception {
        final String binDir = Paths.get(dbHomeDirectory.getPath(), "bin", "pg_controldata" + (OSCheckUtil.isWindows(OSCheckUtil.getOS()) ? ".exe" : "")).toString();
        final String dataDir = Paths.get(dbHomeDirectory.getPath(), "data").toString();
        if (!new File(binDir).exists()) {
            throw new IOException("pg_controldata doesn't exists at " + binDir);
        }
        if (!new File(dataDir).exists()) {
            throw new IOException("data directory doesn't exists at " + binDir);
        }
        String systemIdentifier = null;
        Process process = null;
        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(new String[] { binDir, dataDir });
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            process.waitFor();
            try (final BufferedReader inputStreamBuff = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = null;
                while ((line = inputStreamBuff.readLine()) != null) {
                    if (line.trim().contains("system identifier")) {
                        PostgresConfUtil.LOGGER.log(Level.INFO, line);
                        systemIdentifier = line.substring(line.indexOf(":") + 1).trim();
                    }
                }
            }
        }
        finally {
            if (process != null) {
                process.destroy();
            }
        }
        return systemIdentifier;
    }
    
    static {
        PostgresConfUtil.confFileWithPath = null;
        LOGGER = Logger.getLogger(PostgresConfUtil.class.getName());
        PostgresConfUtil.pgInitializer = null;
        PostgresConfUtil.defaultAuthMode = "md5";
        PostgresConfUtil.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
        final String dbhome = Configuration.getString("db.home", PostgresConfUtil.server_home + File.separator + "pgsql");
        PostgresConfUtil.confFileWithPath = dbhome + File.separator + "data" + File.separator + "pg_hba.conf";
    }
    
    public enum HBAFields
    {
        TYPE("@type@"), 
        DATABASE("@database@"), 
        USER("@user@"), 
        ADDRESS("@address@"), 
        AUTH_MODE("@authmethodhost@");
        
        private String tokPattern;
        
        private HBAFields(final String pattern) {
            this.tokPattern = pattern;
        }
        
        public String getTokenString() {
            return this.tokPattern;
        }
    }
}
