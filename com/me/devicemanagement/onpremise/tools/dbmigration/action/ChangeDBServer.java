package com.me.devicemanagement.onpremise.tools.dbmigration.action;

import java.util.Hashtable;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import com.me.devicemanagement.onpremise.start.util.DCLogUtil;
import java.util.List;
import java.util.ArrayList;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Enumeration;
import com.zoho.mickey.exception.PasswordException;
import com.adventnet.persistence.PersistenceException;
import java.sql.ResultSet;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import java.io.Writer;
import java.io.BufferedWriter;
import java.nio.file.OpenOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.Iterator;
import java.util.Collection;
import java.io.FileWriter;
import java.util.regex.Pattern;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import com.me.devicemanagement.framework.utils.XMLUtils;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.adventnet.persistence.PersistenceInitializer;
import java.io.FileNotFoundException;
import com.adventnet.persistence.PersistenceUtil;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.Locale;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import com.me.devicemanagement.onpremise.tools.dbmigration.utils.DBMigrationUtils;
import java.util.logging.Level;
import com.adventnet.persistence.ConfigurationParser;
import java.util.Properties;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.HashMap;
import com.me.devicemanagement.onpremise.tools.dbmigration.gui.ChangeDBServerGUI;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.logging.Logger;

public class ChangeDBServer
{
    private static Logger logger;
    private static ChangeDBServer changeDBServerObject;
    public static FileLock fl;
    public static FileChannel f;
    public static int dbCreationErrorCode;
    
    public static ChangeDBServer getInstance() {
        if (ChangeDBServer.changeDBServerObject == null) {
            ChangeDBServer.changeDBServerObject = new ChangeDBServer();
        }
        return ChangeDBServer.changeDBServerObject;
    }
    
    public static void main(final String[] args) throws Exception {
        ChangeDBServerGUI.main(args);
    }
    
    public HashMap getDBDetails(final String serverType) throws Exception {
        HashMap hash = new LinkedHashMap();
        final String activeDB = this.getActiveDBName();
        final String sBackupFile = System.getProperty("server.home") + File.separator + "DBMigration" + File.separator + "database_params_backup.conf";
        final String sDataBaseParamsFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
        boolean noDBBackUpConf = false;
        if (serverType != null && !serverType.equalsIgnoreCase(activeDB)) {
            if (new File(sBackupFile).exists()) {
                hash = this.getDBPropertiesFromFile(sBackupFile, activeDB);
            }
            else {
                noDBBackUpConf = true;
                hash = this.getDBPropertiesFromFile(sDataBaseParamsFile, activeDB);
            }
        }
        else {
            hash = this.getDBPropertiesFromFile(sDataBaseParamsFile, activeDB);
        }
        if (!noDBBackUpConf) {
            return hash;
        }
        final HashMap dbDetails = new HashMap();
        dbDetails.put("HOST", "localhost");
        dbDetails.put("PORT", "");
        dbDetails.put("DATABASE", hash.get("DATABASE"));
        dbDetails.put("DOMAIN_NAME", "");
        dbDetails.put("USER", "");
        dbDetails.put("PASSWORD", "");
        return dbDetails;
    }
    
    public HashMap getDBPropertiesFromFile(final String sDataBaseParamsFile, final String serverType) throws Exception {
        final Properties dbProps = this.getProperties(sDataBaseParamsFile);
        final HashMap hash = new HashMap();
        if (dbProps != null) {
            final String connectionUrl = dbProps.getProperty("url");
            final String username = dbProps.getProperty("username");
            final String password = dbProps.getProperty("password");
            if ("mysql".equals(serverType) || "postgres".equals(serverType)) {
                String[] tmp = connectionUrl.split(":");
                hash.put("HOST", tmp[2].substring(2));
                tmp = tmp[3].split("/");
                hash.put("PORT", tmp[0]);
                if (tmp[1].indexOf("?") == -1) {
                    hash.put("DATABASE", tmp[1]);
                }
                else {
                    hash.put("DATABASE", tmp[1].substring(0, tmp[1].indexOf("?")));
                }
            }
            else if ("mssql".equals(serverType)) {
                String[] tmp = connectionUrl.split(":");
                hash.put("HOST", tmp[2].substring(2));
                tmp = tmp[3].split(";");
                hash.put("PORT", tmp[0]);
                for (int i = 1; i < tmp.length; ++i) {
                    final String[] tmp2 = tmp[i].split("=");
                    if ("DatabaseName".equalsIgnoreCase(tmp2[0])) {
                        hash.put("DATABASE", tmp2[1]);
                    }
                    else if ("Domain".equals(tmp2[0])) {
                        hash.put("DOMAIN_NAME", tmp2[1]);
                    }
                    else if ("authenticationScheme=NTLM".equalsIgnoreCase(tmp2[0])) {
                        hash.put("NTLMSetting", Boolean.TRUE);
                    }
                }
            }
            if (username != null) {
                hash.put("USER", username);
            }
            else {
                hash.put("USER", "");
            }
            if (password != null) {
                hash.put("PASSWORD", password);
            }
            else {
                hash.put("PASSWORD", "");
            }
        }
        return hash;
    }
    
    public String getActiveDBName() {
        try {
            final String fileName = System.getProperty("server.home") + "/conf/Persistence/persistence-configurations.xml";
            final ConfigurationParser parser = new ConfigurationParser(fileName);
            return String.valueOf(parser.getConfigurationValue("DBName"));
        }
        catch (final Exception ex) {
            ChangeDBServer.logger.log(Level.WARNING, "Exception while getting Active DB Name {0}", ex);
            return null;
        }
    }
    
    public void updateDBConfigFile(final String server, final String host, final String port, final boolean isWinAuthType, final boolean isNTLMEnabled, final String domain, final String user, final String pwd, final String serverdir, final String instanceName, final String dataBaseName, final String driver, final String dbAdapter, final String sqlGenerator, final String exceptionSorter, final boolean forDBMTool, final String fileUrl, final boolean isBundledDB) throws Exception {
        if (host != null && !host.trim().equals("")) {
            final String connection_url = this.createURL(server, dataBaseName, host, port, isWinAuthType, isNTLMEnabled, domain, forDBMTool);
            this.writeDatabaseParams(server, user, pwd, connection_url, driver, exceptionSorter, forDBMTool, fileUrl, isBundledDB);
            if (!forDBMTool) {
                this.writePersistenceConfiguration(server, isBundledDB);
            }
            else {
                this.writePersistenceConfiguration(server);
            }
            return;
        }
        throw new Exception("Invalid host name!!!");
    }
    
    private void writePersistenceConfiguration(final String destDBName) throws Exception {
        try {
            final String activeDBName = DBMigrationUtils.getDBName();
            if (!activeDBName.equalsIgnoreCase("mssql") || ChangeDBServerGUI.masterkeyPassword != null) {
                final String srcFileURL = System.getProperty("server.home") + File.separator + "conf" + File.separator + "customer-config.xml";
                final String destPath = System.getProperty("backupfile.path", DBMigrationUtils.BACKUPPATH);
                if (destPath != null) {
                    DBMigrationUtils.createBackupFile(srcFileURL, destPath);
                }
                final String masterKeyPassword = this.getMasterKey();
                DBMigrationUtils.writePersistenceConfiguration(masterKeyPassword);
            }
        }
        catch (final Exception e) {
            ChangeDBServer.logger.log(Level.WARNING, "Exception while getting Active DB Name {0}", e);
        }
    }
    
    private String getMasterKey() {
        String masterKey = null;
        if (ChangeDBServerGUI.masterkeyPassword != null) {
            masterKey = ChangeDBServerGUI.masterkeyPassword;
        }
        else {
            masterKey = ChangeDBServerGUI.masterKey;
        }
        return masterKey;
    }
    
    public String createDB(final String stype, final String server, final String port, final boolean isWinAuthType, final boolean isNTLMEnabled, final String domain, final String user, final String pword, final String driver, final String dataBaseName) throws Exception {
        String message = BackupRestoreUtil.getString("desktopcentral.tools.changedb.db_created_successfully", (Locale)null);
        String dbUrl = null;
        ChangeDBServer.dbCreationErrorCode = 1000;
        Connection conn = null;
        Statement stmt = null;
        try {
            if ("mssql".equals(stype)) {
                dbUrl = DBMigrationUtils.getMssqlDBUrl(server, port, domain, isWinAuthType, isNTLMEnabled, null, null);
            }
            else if ("mysql".equals(stype)) {
                dbUrl = "jdbc:mysql://" + server + ":" + port + "/";
            }
            else if ("postgres".equals(stype)) {
                dbUrl = "jdbc:postgresql://" + server + ":" + port + "/";
            }
            Class.forName(driver);
            conn = DriverManager.getConnection(dbUrl, user, pword);
            stmt = conn.createStatement();
            stmt.executeUpdate("create database " + dataBaseName);
            if ("mssql".equals(stype)) {
                stmt.executeUpdate("ALTER DATABASE " + dataBaseName + " COLLATE SQL_Latin1_General_CP1_CI_AS");
                stmt.executeUpdate("ALTER DATABASE " + dataBaseName + " SET RECOVERY SIMPLE ");
                stmt.executeUpdate("ALTER DATABASE " + dataBaseName + " MODIFY FILE (NAME=" + dataBaseName + "_log,MAXSIZE= 5120MB);");
            }
        }
        catch (final SQLException e) {
            ChangeDBServer.dbCreationErrorCode = e.getErrorCode();
            message = e.getMessage();
            ChangeDBServer.logger.log(Level.WARNING, "Exception while creating DB -- {0}", message);
        }
        catch (final Exception e2) {
            ChangeDBServer.logger.log(Level.WARNING, "Exception while creating DB -- {0}", e2.getMessage());
            if (e2.getMessage().indexOf("refused") > 0) {
                if ("mssql".equals(stype)) {
                    message = BackupRestoreUtil.getString("desktopcentral.tools.changedb.connection_refused_sqlservice_not_running", new Object[] { server }, (Locale)null);
                }
                else if ("postgres".equals(stype)) {
                    message = BackupRestoreUtil.getString("desktopcentral.tools.changedb.remotepg_verify", (Locale)null);
                }
                else {
                    message = BackupRestoreUtil.getString("desktopcentral.tools.changedb.mysql_verification", (Locale)null);
                }
            }
            else if (e2.getMessage().toLowerCase().indexOf("sso failed:") > 0) {
                message = BackupRestoreUtil.getString("desktopcentral.tools.changedb.restart_sql", (Locale)null);
            }
            else if (e2.getMessage().toLowerCase().indexOf("access denied") > 0) {
                message = BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.accecc_denied", (Locale)null);
            }
            else if (e2.getMessage().toLowerCase().indexOf("port out of range") > 0) {
                message = BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.out_of_range", (Locale)null);
            }
            else if (e2.getMessage().toLowerCase().indexOf("unknownhost") > 0) {
                message = BackupRestoreUtil.getString("desktopcentral.tools.changedb.check_server_name", new Object[] { server }, (Locale)null);
            }
            else {
                message = e2.getMessage();
            }
        }
        finally {
            if (conn != null && !conn.isClosed()) {
                stmt.close();
                conn.close();
            }
        }
        ChangeDBServer.logger.log(Level.INFO, "Message returned to user while creating DB -- {0}", message + " DB URL = " + dbUrl);
        return message;
    }
    
    public String isDBServerRunning(final String stype, final String server, final String port, final boolean isWinAuthType, final boolean isNTLMEnabled, final String domain, final String user, String pword) throws Exception {
        ChangeDBServer.logger.log(Level.INFO, "Inside isDBServerRunning method user name : " + user);
        String isRunning = "yes";
        String dbUrl = null;
        if ("mssql".equals(stype)) {
            pword = PersistenceUtil.getDBPasswordProvider().getPassword((Object)pword);
            dbUrl = DBMigrationUtils.getMssqlDBUrl(server, port, domain, isWinAuthType, isNTLMEnabled, null, null);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        }
        else if ("mysql".equals(stype)) {
            dbUrl = "jdbc:mysql://" + server + ":" + port + "/";
            Class.forName("org.gjt.mm.mysql.Driver");
        }
        else if ("postgres".equals(stype)) {
            dbUrl = "jdbc:postgresql://" + server + ":" + port + "/";
            Class.forName("org.postgresql.Driver");
        }
        ChangeDBServer.logger.log(Level.INFO, "Inside isDBServerRunning method, DB URL == " + dbUrl);
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbUrl, user, pword);
        }
        catch (final Exception e) {
            ChangeDBServer.logger.log(Level.INFO, "--------------------------------------------------");
            ChangeDBServer.logger.log(Level.INFO, e.getMessage());
            ChangeDBServer.logger.log(Level.INFO, "--------------------------------------------------");
            if (e.getMessage().indexOf("refused") > 0) {
                if ("mssql".equals(stype)) {
                    isRunning = BackupRestoreUtil.getString("desktopcentral.tools.changedb.connection_refused_sqltcp", (Locale)null);
                }
                else if ("postgres".equals(stype)) {
                    isRunning = BackupRestoreUtil.getString("desktopcentral.tools.changedb.remotepg_verify", (Locale)null);
                }
                else {
                    isRunning = BackupRestoreUtil.getString("desktopcentral.tools.changedb.mysql_verification", (Locale)null);
                }
            }
            else if (e.getMessage().toLowerCase().indexOf("access denied") > 0) {
                isRunning = BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.accecc_denied", (Locale)null);
            }
            else if (e.getMessage().toLowerCase().indexOf("port out of range") > 0) {
                isRunning = BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.out_of_range", (Locale)null);
            }
            else if (e.getMessage().toLowerCase().indexOf("unknownhost") > 0) {
                isRunning = BackupRestoreUtil.getString("desktopcentral.tools.changedb.check_server_name", new Object[] { server }, (Locale)null);
            }
            else if (e.getMessage().toLowerCase().indexOf("syntax of the connection url") > 0 || e.getMessage().toLowerCase().indexOf("sso failed:") > 0) {
                isRunning = BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.enter_all_fields", (Locale)null);
            }
            else {
                isRunning = e.getMessage();
            }
        }
        finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        }
        return isRunning;
    }
    
    public void writeDatabaseParams(final String server, final String usrName, String pswd, final String connection_url, final String driver, final String exceptionSorter, final Boolean forDBMTool, final String fileURL, final Boolean isBundledDB) throws Exception {
        ChangeDBServer.logger.log(Level.INFO, "Into WriteDatabaseParams method");
        final LinkedHashMap databaseParamsHMap = new LinkedHashMap();
        if (System.getProperty("server.home") == null) {
            throw new Exception("System Property \"server.home\" not configured");
        }
        final String dbParamsfileURL = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
        final File file = new File(dbParamsfileURL);
        if (!file.exists()) {
            throw new FileNotFoundException("File: " + dbParamsfileURL + "doesnt exists");
        }
        ChangeDBServer.logger.log(Level.INFO, "postgres file update starts");
        final String sBackupDir = System.getProperty("server.home") + File.separator + "DBMigration";
        if (!new File(sBackupDir).exists()) {
            new File(sBackupDir).mkdirs();
        }
        copyfile(dbParamsfileURL, fileURL);
        PersistenceInitializer.loadPersistenceConfigurations();
        pswd = CryptoUtil.encrypt(pswd, 2);
        databaseParamsHMap.put("r_username.*=.*", "");
        databaseParamsHMap.put("drivername.*=.*", "drivername=" + driver);
        databaseParamsHMap.put("username.*=.*", "username=" + usrName);
        databaseParamsHMap.put(".*password.*=.*", "password=" + pswd);
        databaseParamsHMap.put("url.*=.*", "url=" + connection_url);
        databaseParamsHMap.put("exceptionsorterclassname.*=.*", "exceptionsorterclassname=" + exceptionSorter);
        if (isBundledDB) {
            databaseParamsHMap.put("superuser_pass.*=.*", "superuser_pass=Stonebraker");
        }
        this.findAndReplaceStringInFile(fileURL, databaseParamsHMap);
        if (forDBMTool) {
            this.findAndReplaceStringInFile(fileURL, databaseParamsHMap);
        }
        else {
            this.findAndReplaceStringInFile(dbParamsfileURL, databaseParamsHMap);
        }
    }
    
    public void writePersistenceConfiguration(final String dbName, final boolean isBundledDB) throws Exception {
        ChangeDBServer.logger.log(Level.INFO, "Inside writePersistenceConfiguration");
        final String activeDBName = this.getActiveDBName();
        final String fileURL = System.getProperty("server.home") + "/conf/customer-config.xml";
        final File file = new File(fileURL);
        if (!file.exists()) {
            throw new FileNotFoundException("File: " + fileURL + "doesnt exists");
        }
        if (!dbName.equalsIgnoreCase(activeDBName)) {
            final String sBackupDir = System.getProperty("server.home") + File.separator + "dbmigration";
            if (!new File(sBackupDir).exists()) {
                new File(sBackupDir).mkdirs();
            }
            final String backupFileURL = sBackupDir + File.separator + "customer-config_backup_" + activeDBName + ".xml";
            copyfile(fileURL, backupFileURL);
        }
        final DocumentBuilder docBuilder = XMLUtils.getDocumentBuilderInstance();
        final Document doc = docBuilder.parse(file);
        final Element root = doc.getDocumentElement();
        final NodeList connList = root.getElementsByTagName("configuration");
        for (int length = connList.getLength(), i = 0; i < length; ++i) {
            final Element connectorEl = (Element)connList.item(i);
            final String name = connectorEl.getAttribute("name");
            if (name != null && name.equals("DBName")) {
                connectorEl.setAttribute("value", dbName);
            }
            if (name != null && name.equals("StartDBServer")) {
                connectorEl.setAttribute("value", this.startDBServer(dbName, isBundledDB));
                ChangeDBServer.logger.log(Level.INFO, "Inside writePersistenceConfiguration - StartDBServer" + this.startDBServer(dbName, isBundledDB));
            }
            if (name != null && name.equals("DSAdapter")) {
                connectorEl.setAttribute("value", dbName);
            }
        }
        this.writeToXML(file, root);
    }
    
    private static void copyfile(final String sourcerFilePath, final String destinationFilePath) {
        try {
            final File sourcerFile = new File(sourcerFilePath);
            final File destinationFile = new File(destinationFilePath);
            final InputStream in = new FileInputStream(sourcerFile);
            final OutputStream out = new FileOutputStream(destinationFile);
            final byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
        catch (final Exception e) {
            ChangeDBServer.logger.log(Level.WARNING, "Exception while copying file {0}", e.toString());
        }
    }
    
    private String startDBServer(final String serverType, final boolean isBundledDB) {
        if ("mssql".equalsIgnoreCase(serverType)) {
            return "false";
        }
        boolean remoteDB = false;
        if ("postgres".equalsIgnoreCase(serverType) && !isBundledDB) {
            remoteDB = true;
        }
        return String.valueOf(!remoteDB);
    }
    
    public void findAndReplaceStringInFile(final String fileName, final LinkedHashMap linkedHMap) throws Exception {
        String findStr = null;
        String replaceStr = null;
        FileReader filereader = null;
        FileWriter filewriter = null;
        try {
            filereader = new FileReader(fileName);
            int read = 0;
            final char[] chBuf = new char[500];
            final StringBuilder strBuilder = new StringBuilder();
            while ((read = filereader.read(chBuf)) > -1) {
                strBuilder.append(chBuf, 0, read);
            }
            filereader.close();
            String finalStr = strBuilder.toString();
            final Collection collectLinkedHMap = linkedHMap.keySet();
            final Iterator iterate = collectLinkedHMap.iterator();
            while (iterate.hasNext()) {
                findStr = iterate.next();
                replaceStr = linkedHMap.get(findStr);
                final Pattern findStrPattern = Pattern.compile(findStr);
                final Matcher matcher = findStrPattern.matcher(finalStr);
                if (matcher.find()) {
                    finalStr = finalStr.replaceAll(findStr, replaceStr);
                }
                else {
                    finalStr = finalStr.concat("\n\n" + replaceStr);
                }
            }
            filewriter = new FileWriter(fileName, false);
            filewriter.write(finalStr, 0, finalStr.length());
        }
        catch (final Exception ex) {
            ChangeDBServer.logger.log(Level.WARNING, "Caught exception in findAndReplaceStringInFile() for the file : " + fileName, ex);
            throw ex;
        }
        finally {
            if (filereader != null) {
                filereader.close();
            }
            if (filewriter != null) {
                filewriter.close();
            }
        }
    }
    
    private Properties getProperties(final String path) throws Exception {
        FileInputStream fis = null;
        final Properties props = new Properties();
        try {
            fis = new FileInputStream(path);
            props.load(fis);
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (final Exception e) {
                    ChangeDBServer.logger.log(Level.WARNING, "Exception while copying file {0}", e.toString());
                }
            }
        }
        return props;
    }
    
    public static void addDBPropsIntoFile(final String data) {
        final String filePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath, new String[0])));
            if (!content.contains(data)) {
                content += data;
                Files.write(Paths.get(filePath, new String[0]), content.getBytes(), new OpenOption[0]);
            }
        }
        catch (final Exception ex) {
            ChangeDBServer.logger.log(Level.WARNING, "Caught exception while writing in database_params.conf", ex);
        }
    }
    
    public Properties dbmigrationProperties(final String fileName) {
        final Properties dbProps = new Properties();
        try {
            final File migrationConf = new File(fileName);
            if (!migrationConf.exists()) {
                ChangeDBServer.logger.info("[" + migrationConf.getAbsolutePath() + "] file not found...");
            }
            else {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(migrationConf);
                    dbProps.load(fis);
                }
                finally {
                    if (fis != null) {
                        fis.close();
                    }
                }
            }
        }
        catch (final Exception ex) {}
        return dbProps;
    }
    
    private void writeToXML(final File file, final Element root) throws Exception {
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        final String encoding = "ISO-8859-1";
        final Transformer transformer = XMLUtils.getTransformerInstance();
        final DOMSource source = new DOMSource(root);
        final StreamResult result = new StreamResult(writer);
        final Properties prop = new Properties();
        ((Hashtable<String, String>)prop).put("indent", "yes");
        ((Hashtable<String, String>)prop).put("encoding", encoding);
        ((Hashtable<String, String>)prop).put("method", "xml");
        transformer.setOutputProperties(prop);
        transformer.transform(source, result);
    }
    
    public String createURL(final String server, final String dataBaseName, final String host, final String port, final boolean isWinAuthType, final boolean isNTLMEnabled, final String domain, final boolean forDBMTool) {
        String url = null;
        String dbURLFiller = ";databaseName=";
        if (forDBMTool) {
            dbURLFiller = "/";
        }
        if (server.equalsIgnoreCase("mysql")) {
            url = "jdbc:mysql://" + host + ":" + port + "/" + dataBaseName + "?useUnicode=true&characterEncoding=UTF-8&jdbcCompliantTruncation=false";
        }
        else if (server.equalsIgnoreCase("postgres")) {
            url = "jdbc:postgresql://" + host + ":" + port + "/" + dataBaseName;
        }
        else if (server.equalsIgnoreCase("mssql")) {
            url = DBMigrationUtils.getMssqlDBUrl(host, port, domain, isWinAuthType, isNTLMEnabled, dataBaseName, dbURLFiller);
        }
        ChangeDBServer.logger.log(Level.INFO, "Indside create URL method, DB Url is = ", url);
        return url;
    }
    
    public String getBackupFileName() throws Exception {
        final String sBackupDir = System.getProperty("server.home") + File.separator + "DBMigration";
        if (!new File(sBackupDir).exists()) {
            ChangeDBServer.logger.log(Level.INFO, "backupDir " + sBackupDir + " does not exists. Going to create directory");
            new File(sBackupDir).mkdirs();
            ChangeDBServer.logger.log(Level.INFO, "backupDir " + sBackupDir + " created");
        }
        return sBackupDir;
    }
    
    public static boolean ismigrationRevertFound() {
        boolean found = false;
        try {
            final String migrateLock = System.getProperty("server.home") + File.separator + "bin" + File.separator + "migration.lock";
            final File migrateLockFile = new File(migrateLock);
            found = migrateLockFile.exists();
        }
        catch (final Exception e) {
            ChangeDBServer.logger.log(Level.INFO, "Exception while checking revert.lock file", e);
        }
        return found;
    }
    
    public Properties isDBAlreadyExists(final String dbType, final String server, final String port, final boolean isWinAuthType, final boolean isNTLMEnabled, final String domain, final String user, String pword, final String dbname) throws ClassNotFoundException, SQLException, PersistenceException, PasswordException {
        pword = PersistenceUtil.getDBPasswordProvider().getPassword((Object)pword);
        String dbUrl = null;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        final Properties props = new Properties();
        if ("SQL Server".equalsIgnoreCase(dbType)) {
            dbUrl = DBMigrationUtils.getMssqlDBUrl(server, port, domain, isWinAuthType, isNTLMEnabled, null, null);
            ChangeDBServer.logger.log(Level.INFO, "Inside isDBAlreadyExists method SQL server, dburl: " + dbUrl);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            query = "SELECT * FROM sys.databases WHERE NAME ='" + dbname + "'";
        }
        else if ("Remote Postgres".equalsIgnoreCase(dbType)) {
            dbUrl = "jdbc:postgresql://" + server + ":" + port + "/";
            ChangeDBServer.logger.log(Level.INFO, "Inside isDBAlreadyExists method Remote PostgreSQL, dburl: " + dbUrl);
            Class.forName("org.postgresql.Driver");
            query = "SELECT datname FROM pg_catalog. pg_database where datname = '" + dbname + "'";
        }
        try {
            conn = DriverManager.getConnection(dbUrl, user, pword);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                props.setProperty("isDBalreadyExist", "true");
                ChangeDBServer.logger.log(Level.INFO, "############## Database with name '" + dbname + "' already exists.So going to show alert and stop Migration Process.#######################");
                stmt.executeUpdate("use " + dbname);
                query = "select top 1* from DCServerBuildHistory order by BUILD_DETECTED_AT desc";
                rs = stmt.executeQuery(query);
                if (rs.next()) {
                    final Object build_number = rs.getObject("BUILD_NUMBER");
                    props.setProperty("build_number", build_number.toString());
                    ChangeDBServer.logger.log(Level.INFO, "Existing database Build number: " + build_number.toString());
                }
                query = "select * from DCServerInfo";
                rs = stmt.executeQuery(query);
                if (rs.next()) {
                    final Object server_name = rs.getObject("SERVER_MAC_NAME");
                    props.setProperty("server_name", server_name.toString());
                    ChangeDBServer.logger.log(Level.INFO, "Existing database pointing to server: " + server_name.toString());
                }
            }
        }
        catch (final SQLException ex) {
            ChangeDBServer.logger.log(Level.WARNING, "Problem while getting isDBAlreadyExists details ", ex);
        }
        finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return props;
    }
    
    public String getRemotePotgreSQLVersionFromDB(final String server, final String port, final String user, String pword) {
        String version = null;
        String dbUrl = null;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            pword = PersistenceUtil.getDBPasswordProvider().getPassword((Object)pword);
            final Properties props = new Properties();
            dbUrl = "jdbc:postgresql://" + server + ":" + port + "/";
            ChangeDBServer.logger.log(Level.INFO, "Inside remotPostgreSQLCompatibleVersion method Remote PostgreSQL, dburl: " + dbUrl);
            Class.forName("org.postgresql.Driver");
            query = "SELECT version()";
            conn = DriverManager.getConnection(dbUrl, user, pword);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                version = rs.getObject("version").toString();
                ChangeDBServer.logger.log(Level.INFO, "rs startment : " + rs.getObject("version"));
            }
            if (rs != null) {
                rs.close();
            }
        }
        catch (final Exception e) {
            ChangeDBServer.logger.log(Level.INFO, "Exception while getting PostgreSQL Db version from Database ");
            try {
                if (conn != null) {
                    conn.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final SQLException e2) {
                ChangeDBServer.logger.info("Error in snapshot enabler finally block  " + e2.getMessage());
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final SQLException e3) {
                ChangeDBServer.logger.info("Error in snapshot enabler finally block  " + e3.getMessage());
            }
        }
        return version;
    }
    
    public boolean remotPostgreSQLCompatibleVersion(final String server, final String port, final String user, final String pword) {
        final String version = this.getRemotePotgreSQLVersionFromDB(server, port, user, pword);
        ChangeDBServer.logger.log(Level.INFO, "rs startment : " + version);
        try {
            final Properties prop = this.getProperties(System.getProperty("server.home") + File.separator + "conf" + File.separator + "remotePostgreSQL.properties");
            final String[] compVersions = prop.getProperty("REMOTE_PG_COMPATIBLE_VERSIONS").split(",");
            for (int i = 0; i < compVersions.length; ++i) {
                if (version.contains(compVersions[i])) {
                    return true;
                }
            }
        }
        catch (final Exception e) {
            ChangeDBServer.logger.log(Level.WARNING, "Exception while reading the remotePoistgres property file", e);
        }
        return false;
    }
    
    public void updateServerStartupTimeInFile(final String desDB, final String server, final String port, final boolean isWinAuthType, final boolean isNTLMEnabled, final String domain, final String user, String pword, final String dbname, final boolean forDBMTool) throws ClassNotFoundException, SQLException, PersistenceException, PasswordException {
        pword = PersistenceUtil.getDBPasswordProvider().getPassword((Object)pword);
        String dbUrl = null;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        dbUrl = this.createURL(desDB, dbname, server, port, isWinAuthType, isNTLMEnabled, domain, forDBMTool);
        ChangeDBServer.logger.log(Level.INFO, "Inside updateServerStartupTimeInFile method, dburl: " + dbUrl);
        if ("postgres".equals(desDB)) {
            Class.forName("org.postgresql.Driver");
        }
        else if ("mssql".equals(desDB)) {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        }
        final String query = "select * from DCServerUptimeHistory order by START_TIME desc  limit 1";
        try {
            conn = DriverManager.getConnection(dbUrl, user, pword);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                final String serverStartupTime = rs.getObject("START_TIME").toString();
                final String serverHome = System.getProperty("server.home");
                final String serverStartTimeFile = serverHome + File.separator + "conf" + File.separator + "server.starttime";
                if (!new File(serverStartTimeFile).exists()) {
                    new File(serverStartTimeFile).createNewFile();
                }
                final Properties fileSystemProps = new Properties();
                fileSystemProps.setProperty("last_server_startup_time", String.valueOf(serverStartupTime));
                storeProperties(fileSystemProps, serverStartTimeFile, null);
            }
            ChangeDBServer.logger.log(Level.INFO, "Successfully updated Server startup time in file");
        }
        catch (final Exception ex) {
            ChangeDBServer.logger.log(Level.WARNING, "Problem while update server startup time into file ", ex);
        }
        finally {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
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
            ChangeDBServer.logger.log(Level.SEVERE, "Caught exception: " + ex);
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
            catch (final Exception ex2) {}
        }
    }
    
    public static void createMigrateLockFile() {
        FileOutputStream fos = null;
        try {
            ChangeDBServer.logger.log(Level.INFO, "Invoked  createMigrateLockFile: ");
            final String migrateLockFileName = System.getProperty("server.home") + File.separator + "bin" + File.separator + "migration.lock";
            final File migrateLockFile = new File(migrateLockFileName);
            if (migrateLockFile.exists()) {
                ChangeDBServer.logger.log(Level.INFO, "Migrate Lock file already exists. Going to delete: {0}", migrateLockFile);
                migrateLockFile.delete();
            }
            final Properties migrateProps = new Properties();
            final String timeStamp = new Date(System.currentTimeMillis()).toString();
            migrateProps.setProperty("timestamp", timeStamp);
            fos = new FileOutputStream(migrateLockFile);
            migrateProps.store(fos, null);
            fos.close();
            final File flock = new File("migration.lock");
            ChangeDBServer.f = new RandomAccessFile(flock, "rw").getChannel();
            ChangeDBServer.fl = ChangeDBServer.f.lock();
            ChangeDBServer.logger.log(Level.INFO, "Migrate lock file {0} is created.", migrateLockFile);
        }
        catch (final Exception ex) {
            ChangeDBServer.logger.log(Level.WARNING, "Exception while creation migration.lock file :: ", ex);
            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception ex) {
                ChangeDBServer.logger.log(Level.WARNING, "Exception while creation Migrate.lock file :: ", ex);
            }
        }
        finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception ex2) {
                ChangeDBServer.logger.log(Level.WARNING, "Exception while creation Migrate.lock file :: ", ex2);
            }
        }
    }
    
    public static boolean isFileLocked() {
        boolean isLocked = false;
        try {
            final File flock = new File("migration.lock");
            final FileChannel f1 = new RandomAccessFile(flock, "rw").getChannel();
            FileLock trylock = null;
            for (int i = 0; i < 3; ++i) {
                if ((trylock = f1.tryLock()) == null) {
                    isLocked = true;
                    break;
                }
                isLocked = false;
                Thread.sleep(2000L);
            }
        }
        catch (final Exception ex) {
            ChangeDBServer.logger.log(Level.WARNING, "Exception while checking file lock :: ", ex);
        }
        return isLocked;
    }
    
    public void startDCServer() {
        this.deleteMigrateFileIfExists();
        final String binDir = System.getProperty("server.home") + File.separator + "bin";
        ChangeDBServer.logger.log(Level.WARNING, "bin Dir " + binDir);
        final String startupBatchFile = binDir + File.separator + "DCService.bat";
        ChangeDBServer.logger.log(Level.WARNING, "startupBatchFile " + startupBatchFile);
        if (new File(startupBatchFile).exists()) {
            final File filepath = new File(binDir);
            final List<String> command = new ArrayList<String>();
            try {
                command.add(startupBatchFile);
                command.add("-t");
                ChangeDBServer.logger.log(Level.WARNING, "command " + command);
                final ProcessBuilder processBuilder = new ProcessBuilder(command);
                processBuilder.directory(filepath);
                final Process process = processBuilder.start();
                ChangeDBServer.logger.log(Level.INFO, "COMMAND: {0}", processBuilder.command());
                this.closeWindow(0);
            }
            catch (final Exception ex) {
                ChangeDBServer.logger.log(Level.WARNING, "Exception occurred while starting Server ", ex);
            }
        }
    }
    
    private void deleteMigrateFileIfExists() {
        final String migrateLockFileName = System.getProperty("server.home") + File.separator + "bin" + File.separator + "migration.lock";
        final File file = new File(migrateLockFileName);
        try {
            if (file.exists()) {
                if (ChangeDBServer.fl != null) {
                    ChangeDBServer.fl.release();
                    ChangeDBServer.f.close();
                }
                ChangeDBServer.logger.log(Level.INFO, "Lock file exists. Going to delete: {0} ", file);
                final boolean result = file.delete();
                ChangeDBServer.logger.log(Level.INFO, "Lock file delete result :: {0} ", result);
            }
            else {
                ChangeDBServer.logger.log(Level.INFO, "Lock file {0} does not exist!", migrateLockFileName);
            }
        }
        catch (final Exception ex) {
            ChangeDBServer.logger.log(Level.WARNING, "Exception occurred while deleting the migration lock file ", ex);
        }
    }
    
    public void closeWindow(final int status) {
        this.deleteMigrateFileIfExists();
        printOneLineLog(Level.INFO, "Closing DB Migration");
        System.exit(status);
    }
    
    public static void printOneLineLog(final Level level, final String logmessage) {
        if (System.getProperty("uniformlogformatter.enable", "false").equalsIgnoreCase("true")) {
            DCLogUtil.getOneLineLoggerInstance().log(level, logmessage);
        }
    }
    
    public String getValue(final String value, final Object[] params) {
        if (value != null) {
            final MessageFormat messageFormat = new MessageFormat(value);
            return messageFormat.format(params);
        }
        return value;
    }
    
    public void changeSuperUserPassword() {
        String newPassword = "Stonebraker";
        try {
            ChangeDBServer.logger.log(Level.INFO, "inside change super user password");
            final String changeDBPasswordBat = System.getProperty("server.home") + File.separator + "bin" + File.separator + "changeDBPassword.bat";
            newPassword = PersistenceUtil.generateRandomPassword();
            final ProcessBuilder processBuilder = new ProcessBuilder(new String[] { changeDBPasswordBat, "-U", "postgres", "-p", "Stonebraker", "-P", newPassword });
            processBuilder.redirectErrorStream(true);
            final Process process = processBuilder.start();
            try (final BufferedReader ipBuf = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = null;
                while ((line = ipBuf.readLine()) != null) {
                    ChangeDBServer.logger.log(Level.INFO, line);
                }
            }
            removePWDEntry(true);
            addDBPropsIntoFile("superuser_pass=" + newPassword);
        }
        catch (final Exception ex) {
            ChangeDBServer.logger.log(Level.INFO, "Exception while changing super user password to random", ex);
        }
    }
    
    public static void removePWDEntry(final boolean isSuperUserPWDEntry) {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        FileWriter fileWriter = null;
        final String dbParamsFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
        final StringBuilder fileString = new StringBuilder();
        String pwdKey = "superuser_pass=";
        if (!isSuperUserPWDEntry) {
            pwdKey = "password=";
        }
        try {
            fileReader = new FileReader(dbParamsFilePath);
            bufferedReader = new BufferedReader(fileReader);
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                if (line.trim().contains(pwdKey)) {
                    line = "";
                }
                fileString.append(line + "\n");
            }
        }
        catch (final Exception ex) {
            ChangeDBServer.logger.log(Level.WARNING, "Exception in reading DB params file: " + ex);
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }
            }
            catch (final Exception ex) {
                ChangeDBServer.logger.log(Level.WARNING, "Exception in closing DB params reader: " + ex);
            }
        }
        finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }
            }
            catch (final Exception ex2) {
                ChangeDBServer.logger.log(Level.WARNING, "Exception in closing DB params reader: " + ex2);
            }
        }
        try {
            fileWriter = new FileWriter(dbParamsFilePath);
            fileWriter.write(fileString.toString());
        }
        catch (final Exception ex) {
            ChangeDBServer.logger.log(Level.WARNING, "Exception in writing DB params file: ", ex);
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            }
            catch (final Exception ex) {
                ChangeDBServer.logger.log(Level.WARNING, "Exception in closing DB params writer: " + ex);
            }
        }
        finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            }
            catch (final Exception ex3) {
                ChangeDBServer.logger.log(Level.WARNING, "Exception in closing DB params writer: " + ex3);
            }
        }
        ChangeDBServer.logger.log(Level.INFO, "super user entry removed");
    }
    
    static {
        ChangeDBServer.logger = Logger.getLogger(ChangeDBServerGUI.class.getName());
        ChangeDBServer.changeDBServerObject = null;
        ChangeDBServer.fl = null;
        ChangeDBServer.f = null;
        ChangeDBServer.dbCreationErrorCode = 0;
    }
}
