package com.me.devicemanagement.onpremise.start;

import java.util.Hashtable;
import java.util.Map;
import java.util.Iterator;
import java.io.RandomAccessFile;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.io.FileReader;
import java.util.Enumeration;
import java.net.SocketException;
import java.net.NetworkInterface;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Date;
import java.text.DateFormat;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import com.adventnet.tools.prevalent.Wield;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.InetAddress;
import com.me.devicemanagement.onpremise.start.util.NginxServerUtils;
import org.apache.commons.io.FileUtils;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.nio.file.Paths;
import com.zoho.framework.utils.crypto.EnDecrypt;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.zoho.framework.utils.crypto.EnDecryptAES256Impl;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import com.me.devicemanagement.framework.utils.XMLUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import sun.net.www.protocol.http.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import com.adventnet.mfw.Starter;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import com.me.devicemanagement.onpremise.start.servertroubleshooter.util.ServerTroubleshooterUtil;
import java.io.File;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.start.startupstatus.StartupStatusNotifierApi;
import java.util.logging.Logger;

public class DCStarter
{
    private static final String WEB_SETTINGS_CONF_MODTIME_PROPS_FILE;
    private static final String WEB_SETTINGS_CONF_MODTIME = "lastModifiedTime";
    private static final String MYSQL_HOME = "mysql.home";
    private static final String PGSQL_HOME = "pgsql.home";
    private static final String SERVERFAILURECLASS = "com.me.devicemanagement.onpremise.server.common.ServerFailureHandlerImpl";
    private static final String SET_OS_ARCHITECTURE_FILE;
    private static final String POST_ME_DATA_FILE = "postMEDataInStartup.bat";
    private static Logger logger;
    private static Logger troubleShootLog;
    private static int snapshotEnabled;
    public static String publicIP;
    private static StartupStatusNotifierApi startupStatusNotifier;
    public static String ipAddress;
    
    public static void main(final String[] args) throws Exception {
        DCStarter.logger.log(Level.INFO, "\n\n\n");
        DCStarter.logger.log(Level.INFO, "========================================================");
        DCStarter.logger.log(Level.INFO, "Checking DesktopCentral Server settings ................");
        DCStarter.logger.log(Level.INFO, "========================================================");
        if (isIncompletePPMInstallationFound()) {
            DCStarter.logger.log(Level.WARNING, "########################################################################");
            DCStarter.logger.log(Level.WARNING, "###################     N E E D   A T T E N T I O N    #################");
            DCStarter.logger.log(Level.WARNING, "Found PPM Lock file. Going to shutdown the server to avoid any data corrption.....");
            DCStarter.logger.log(Level.WARNING, "########################################################################");
            try {
                runBat(getServerHome() + File.separator + "bin" + File.separator + "postMEDataInStartup.bat");
                ServerTroubleshooterUtil.getInstance().serverStartupFailure("ppm_lock");
            }
            catch (final Exception e) {
                DCStarter.logger.log(Level.SEVERE, "Exception while calling serverStartupFailure for incomplete previous ppm installation found ", e);
            }
            StartupUtil.triggerServerShutdown("Previous PPM installation is incomplete.The ppm.lock file still exists.");
            return;
        }
        DCStarter.logger.log(Level.INFO, "No PPM Lock file found.");
        DCStarter.logger.log(Level.INFO, "HttpURLConnection class javaVersion initialization method started");
        try {
            updateHttpURLConnectionJavaVerion();
        }
        catch (final Exception e) {
            DCStarter.logger.log(Level.SEVERE, "Exception while JavaVersion Initialization", e);
        }
        DCStarter.logger.log(Level.INFO, "HttpURLConnection class javaVersion initialization method ended");
        resetCustomBrowser();
        resetConf();
        WebServerUtil.stopServers(Boolean.TRUE);
        setDBHome();
        setDcOsArchitecture();
        checkAndSetAJPPort();
        checkAndStartAsFos();
        checkChangesInWebSettingsConf();
        checkDBMigrationConfBackUP();
        DCStarter.logger.log(Level.INFO, "Checking DesktopCentral Server settings is completed.");
        DCStarter.troubleShootLog.log(Level.INFO, "Checking DesktopCentral Server settings is completed.");
        try {
            DCStarter.troubleShootLog.log(Level.INFO, "Going to do preStartupChecks!");
            preStartupChecks();
            DCStarter.troubleShootLog.log(Level.INFO, "preStartupChecks completed!");
        }
        catch (final Exception e) {
            DCStarter.logger.log(Level.SEVERE, "Exception while doing preStartupChecks()", e);
        }
        final String dbName = getDBName();
        if (dbName != null && dbName.equalsIgnoreCase("pgsql") && isRemoteDatabase() && !remotePostgreSQLCompatibleVersion()) {
            ServerTroubleshooterUtil.getInstance().serverStartupFailure("remote_pg_not_compatible");
        }
        if (dbName != null && dbName.equalsIgnoreCase("pgsql") && !isRemoteDatabase()) {
            final String postgresVersion = getPostgresVersion(System.getProperty("db.home")).substring(22);
            final int majorVersion = Integer.parseInt(postgresVersion.split("\\.")[0]);
            if (majorVersion < 10) {
                DCStarter.logger.log(Level.INFO, "Going to do FSM Corruption Check. Because of Postgres version " + majorVersion);
                cleanupFSMCorruptedFiles();
            }
            else {
                DCStarter.logger.log(Level.INFO, "Skipping FSM Corruption Check. Because of Postgres version " + majorVersion);
            }
            if (!new File(ServerTroubleshooterUtil.dbSettingsConf).exists()) {
                ServerTroubleshooterUtil.createDefaultDBPortProps();
            }
            if (!new File(System.getProperty("server.home") + File.separator + "bin" + File.separator + "port_in_use_restart.lock").exists()) {
                ServerTroubleshooterUtil.changeDefaultPortIntoDatabaseParams();
            }
            else {
                DCStarter.logger.log(Level.INFO, "****************************************************");
                DCStarter.logger.log(Level.INFO, "port_in_use_restart.lock file exists..");
                DCStarter.logger.log(Level.INFO, "****************************************************");
            }
        }
        Boolean isIncompatibleArchitecture = Boolean.FALSE;
        try {
            isIncompatibleArchitecture = WebServerUtil.isIncompatibleArchitectureFound(dbName);
            DCStarter.logger.log(Level.INFO, "Is Incompatible Architecture found :" + isIncompatibleArchitecture);
        }
        catch (final Exception e2) {
            DCStarter.logger.log(Level.INFO, "Exception while checking product incompatibility");
            isIncompatibleArchitecture = Boolean.FALSE;
        }
        if (isIncompatibleArchitecture == Boolean.TRUE) {
            final String serverHome = getServerHome();
            DCStarter.logger.log(Level.INFO, "Architecture Incompatible Msg displayed to user");
            WebServerUtil.startWebServer(serverHome);
            WebServerUtil.ProductArchitectureIncompatibility(dbName);
        }
        else {
            if (dbName != null && dbName.equalsIgnoreCase("pgsql")) {
                StartupUtil.deleteBackupLabel();
            }
            Starter.main(args);
        }
    }
    
    private static boolean isIncompletePPMInstallationFound() {
        boolean found = false;
        try {
            final String ppmLockFileNameFull = System.getProperty("server.home") + File.separator + "bin" + File.separator + "ppm_lock";
            final File ppmLockFileNameFullFile = new File(ppmLockFileNameFull);
            DCStarter.logger.log(Level.INFO, "PPM Lock file path: " + ppmLockFileNameFull);
            if (ppmLockFileNameFullFile.exists()) {
                found = true;
                final Properties ppmLockProps = new Properties();
                ppmLockProps.load(new FileInputStream(ppmLockFileNameFullFile));
                DCStarter.logger.log(Level.WARNING, "PPM Lock file found in the setup while server startup. Properties are: " + ppmLockProps);
            }
        }
        catch (final Exception ex) {
            DCStarter.logger.log(Level.WARNING, "Caught exception while checking the existence of ppm lock file.", ex);
        }
        return found;
    }
    
    private static void runBat(final String fileLocation) throws Exception {
        DCStarter.logger.log(Level.INFO, "Calling the command {0}", fileLocation);
        final ProcessBuilder processBuilder = new ProcessBuilder(new String[] { fileLocation });
        final File file = new File(getServerHome() + File.separator + "bin");
        processBuilder.directory(file);
        final Process process = processBuilder.start();
        final int processStatus = process.waitFor();
        DCStarter.logger.log(Level.INFO, "Process Status for the command {0} is {1}", new Object[] { fileLocation, processStatus });
    }
    
    public static void updateHttpURLConnectionJavaVerion() throws Exception {
        final String systemProperties = System.getProperty("server.home") + "/conf/system_properties.conf";
        if (new File(systemProperties).exists()) {
            Properties prop = null;
            try {
                prop = getProperties(systemProperties);
                final String http_agent_value = prop.getProperty("http.agent");
                final String java_version = System.getProperty("java.version");
                System.setProperty("java.version", "");
                System.setProperty("http.agent", http_agent_value);
                final HttpURLConnection connection = new HttpURLConnection(new URL("https://"), Proxy.NO_PROXY);
                System.setProperty("java.version", java_version);
            }
            catch (final IOException e) {
                DCStarter.logger.log(Level.SEVERE, "Exception while establishing the connection", e);
            }
        }
    }
    
    public static Boolean isRemoteDatabase() {
        final String dbName = getDBName();
        try {
            DCStarter.logger.log(Level.INFO, "dbName: " + dbName);
            if (dbName.equalsIgnoreCase("mssql")) {
                DCStarter.logger.log(Level.INFO, "Since the db is mssql, isRemoteDB(): true");
                return true;
            }
            if (dbName.equalsIgnoreCase("pgsql")) {
                DCStarter.logger.log(Level.INFO, "isRemoteDB(): Postgres : ");
                final String fileURL = System.getProperty("server.home") + File.separator + "conf" + File.separator + "customer-config.xml";
                try {
                    final File file = new File(fileURL);
                    if (!file.exists()) {
                        throw new FileNotFoundException("File: " + fileURL + "doesnt exists");
                    }
                    final DocumentBuilder docBuilder = XMLUtils.getDocumentBuilderInstance();
                    final Document doc = docBuilder.parse(file);
                    final Element root = doc.getDocumentElement();
                    final NodeList connList = root.getElementsByTagName("configuration");
                    for (int length = connList.getLength(), i = 0; i < length; ++i) {
                        final Element connectorEl = (Element)connList.item(i);
                        final String name = connectorEl.getAttribute("name");
                        if (name != null && name.equalsIgnoreCase("StartDBServer") && connectorEl.getAttribute("value").equalsIgnoreCase("true")) {
                            DCStarter.logger.log(Level.INFO, "isRemoteDB(): Postgres : False ");
                            return false;
                        }
                        if (name != null && name.equalsIgnoreCase("StartDBServer") && connectorEl.getAttribute("value").equalsIgnoreCase("false")) {
                            DCStarter.logger.log(Level.INFO, "isRemoteDB(): Postgres : True");
                            return true;
                        }
                    }
                }
                catch (final Exception e) {
                    DCStarter.logger.log(Level.WARNING, "caught exception in isRemoteDB(): Postgres : ", e);
                }
            }
        }
        catch (final Exception e2) {
            DCStarter.logger.log(Level.SEVERE, "Exception while checking the bundled DB ", e2);
        }
        return false;
    }
    
    public static boolean remotePostgreSQLCompatibleVersion() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            final String dbConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
            final Properties props = new Properties();
            FileInputStream fis = null;
            try {
                if (new File(dbConfFile).exists()) {
                    fis = new FileInputStream(dbConfFile);
                    props.load(fis);
                    fis.close();
                }
            }
            catch (final Exception ex) {
                DCStarter.logger.log(Level.WARNING, "Caught exception while loading properties from: " + dbConfFile + " Exception: " + ex);
                try {
                    if (fis != null) {
                        fis.close();
                    }
                }
                catch (final Exception ex) {
                    DCStarter.logger.log(Level.WARNING, "Caught exception while loading properties from: " + dbConfFile + " Exception: " + ex);
                }
            }
            finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                }
                catch (final Exception ex2) {
                    DCStarter.logger.log(Level.WARNING, "Caught exception while loading properties from: " + dbConfFile + " Exception: " + ex2);
                }
            }
            String version = null;
            final String driver = props.getProperty("drivername");
            Class.forName(driver);
            final EnDecrypt cryptInstance = (EnDecrypt)new EnDecryptAES256Impl();
            CryptoUtil.setEnDecryptInstance(cryptInstance);
            String password = props.getProperty("password");
            password = CryptoUtil.decrypt(password, 2);
            final String username = props.getProperty("username");
            String query = null;
            final String dbUrl = props.getProperty("url");
            DCStarter.logger.log(Level.INFO, "Inside remotPostgreSQLCompatibleVersion method Remote PostgreSQL, dburl: " + dbUrl);
            query = "SELECT version()";
            try {
                conn = DriverManager.getConnection(dbUrl, username, password);
                stmt = conn.createStatement();
            }
            catch (final Exception e) {
                DCStarter.logger.log(Level.INFO, "Exception while creating connection in remote postrges");
            }
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                version = rs.getObject("version").toString();
                DCStarter.logger.log(Level.INFO, "rs startment : " + rs.getObject("version"));
            }
            final String remoteDBSettingsConf = System.getProperty("server.home") + File.separator + "conf" + File.separator + "remotePostgreSQL.properties";
            final Properties prop = getProperties(remoteDBSettingsConf);
            try {
                final String[] compVersions = prop.getProperty("REMOTE_PG_COMPATIBLE_VERSIONS").split(",");
                for (int i = 0; i < compVersions.length; ++i) {
                    if (version.contains(compVersions[i])) {
                        return true;
                    }
                }
            }
            catch (final Exception e2) {
                DCStarter.logger.log(Level.WARNING, "Exception while reading the remotePoistgres property file", e2);
            }
            if (rs != null) {
                rs.close();
            }
        }
        catch (final Exception e3) {
            DCStarter.logger.log(Level.INFO, "Exception while getting PostgreSQL Db version from Database ");
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
            catch (final SQLException e4) {
                DCStarter.logger.info("Error in snapshot enabler finally block  " + e4.getMessage());
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
            catch (final SQLException e5) {
                DCStarter.logger.info("Error in snapshot enabler finally block  " + e5.getMessage());
            }
        }
        return false;
    }
    
    private static Properties getProperties(final String path) throws Exception {
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
                    DCStarter.logger.log(Level.WARNING, "Exception while copying file {0}", e.toString());
                }
            }
        }
        return props;
    }
    
    public static void setStartupStatusNotifier(final StartupStatusNotifierApi notifier) {
        DCStarter.startupStatusNotifier = notifier;
    }
    
    public static StartupStatusNotifierApi getStartupStatusNotifier() {
        return DCStarter.startupStatusNotifier;
    }
    
    private static void checkDBMigrationConfBackUP() {
        if (ismigrationRevertFound()) {
            DCStarter.logger.log(Level.INFO, "Migration revert found");
            String backupFolder = System.getProperty("server.home") + File.separator + "backup";
            backupFolder = System.getProperty("backupfile.path", backupFolder);
            final String backupPath = backupFolder + File.separator + "customer-config.xml";
            final String destinationPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "customer-config.xml";
            final Path sourceDirectory = Paths.get(backupPath, new String[0]);
            final Path targetDirectory = Paths.get(destinationPath, new String[0]);
            try {
                if (new File(backupPath).exists()) {
                    DCStarter.logger.log(Level.INFO, "Going to replace the " + targetDirectory + " with " + sourceDirectory);
                    Files.copy(sourceDirectory, targetDirectory, StandardCopyOption.REPLACE_EXISTING);
                    deleteBackUpFile(backupFolder);
                    DCStarter.logger.log(Level.INFO, "Customer-config file reverted successfully");
                }
            }
            catch (final IOException e) {
                DCStarter.logger.log(Level.SEVERE, "Excetion occured while replace the customer-config file with the backup file", e);
            }
        }
    }
    
    private static boolean ismigrationRevertFound() {
        boolean found = false;
        try {
            final String migrateLock = System.getProperty("server.home") + File.separator + "bin" + File.separator + "migration.lock";
            final File migrateLockFile = new File(migrateLock);
            found = migrateLockFile.exists();
        }
        catch (final Exception e) {
            DCStarter.logger.log(Level.WARNING, "Exception while checking revert.lock file", e);
        }
        return found;
    }
    
    private static void deleteBackUpFile(final String fileDir) {
        final File file = new File(fileDir);
        try {
            if (file.exists()) {
                FileUtils.deleteDirectory(file);
                DCStarter.logger.log(Level.INFO, " file deleted successfully ");
            }
            else {
                DCStarter.logger.log(Level.INFO, " file does not exist!", file);
            }
        }
        catch (final IOException e) {
            DCStarter.logger.log(Level.SEVERE, "Exception occured while deleting the directory" + file, e);
        }
    }
    
    public static void mssqlSnapShotEnabler() {
        DCStarter.logger.info("Inside mssqlSnapShotEnabler");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            final String dbConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
            final Properties props = new Properties();
            FileInputStream fis = null;
            try {
                if (new File(dbConfFile).exists()) {
                    fis = new FileInputStream(dbConfFile);
                    props.load(fis);
                    fis.close();
                }
            }
            catch (final Exception ex) {
                DCStarter.logger.log(Level.WARNING, "Caught exception while loading properties from: " + dbConfFile + " Exception: " + ex);
                try {
                    if (fis != null) {
                        fis.close();
                    }
                }
                catch (final Exception ex) {
                    DCStarter.logger.log(Level.WARNING, "Caught exception while loading properties from: " + dbConfFile + " Exception: " + ex);
                }
            }
            finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                }
                catch (final Exception ex2) {
                    DCStarter.logger.log(Level.WARNING, "Caught exception while loading properties from: " + dbConfFile + " Exception: " + ex2);
                }
            }
            final String dbUrl = props.getProperty("url");
            if (!dbUrl.toLowerCase().contains("sqlserver")) {
                DCStarter.logger.info("Not mssql Server");
                DCStarter.logger.info("Exiting mssqlSnapShotEnabler");
                return;
            }
            final String driver = props.getProperty("drivername");
            Class.forName(driver);
            final EnDecrypt cryptInstance = (EnDecrypt)new EnDecryptAES256Impl();
            CryptoUtil.setEnDecryptInstance(cryptInstance);
            String password = props.getProperty("password");
            password = CryptoUtil.decrypt(password, 2);
            final String username = props.getProperty("username");
            String databaseName = null;
            String[] tmp = dbUrl.split(":");
            tmp = tmp[4].split(";");
            for (int i = 1; i < tmp.length; ++i) {
                final String[] tmp2 = tmp[i].split("=");
                if ("DatabaseName".equalsIgnoreCase(tmp2[0])) {
                    databaseName = tmp2[1];
                }
            }
            DCStarter.logger.info("in conn");
            conn = DriverManager.getConnection(dbUrl, username, password);
            stmt = conn.createStatement();
            stmt.setQueryTimeout(60);
            int connectionCount = 0;
            int snapShotStatus = 0;
            rs = stmt.executeQuery("SELECT is_read_committed_snapshot_on FROM sys.databases WHERE name= '" + databaseName + "'");
            if (rs.next()) {
                snapShotStatus = Integer.parseInt(rs.getString("is_read_committed_snapshot_on"));
                DCStarter.logger.info("is_read_committed_snapshot_on = " + snapShotStatus);
            }
            if (snapShotStatus == DCStarter.snapshotEnabled) {
                DCStarter.logger.info("snapshot already enabled for mssql");
                DCStarter.logger.info("Exiting mssqlSnapShotEnabler");
                return;
            }
            rs = stmt.executeQuery("select b.name as DatabaseName, count(a.dbid) as TotalConnections from sys.sysprocesses a inner join sys.databases b on a.dbid = b.database_id where b.name='" + databaseName + "' group by a.dbid, b.name;");
            if (rs.next()) {
                connectionCount = Integer.parseInt(rs.getString("TotalConnections"));
                DCStarter.logger.info("Connection count = " + connectionCount);
            }
            if (connectionCount == 1) {
                DCStarter.logger.info("Going to enable MSSQL Snapshot");
                stmt.execute("ALTER DATABASE " + databaseName + " SET READ_COMMITTED_SNAPSHOT ON WITH NO_WAIT");
                stmt.execute("ALTER DATABASE " + databaseName + " SET ALLOW_SNAPSHOT_ISOLATION ON");
            }
            else {
                DCStarter.logger.info("The number of connections = " + connectionCount + " Snapshot not enabled in server");
            }
            if (rs != null) {
                rs.close();
            }
        }
        catch (final Exception e) {
            DCStarter.logger.log(Level.SEVERE, "Error in mssqlSnapShotEnabler " + e.getMessage());
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
                DCStarter.logger.info("Error in snapshot enabler finally block  " + e2.getMessage());
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
                DCStarter.logger.info("Error in snapshot enabler finally block  " + e3.getMessage());
            }
        }
        DCStarter.logger.info("Exiting mssqlSnapShotEnabler");
    }
    
    private static void preStartupChecks() throws Exception {
        DCStarter.troubleShootLog.log(Level.INFO, "going to do Service running in different location check");
        final Boolean isServiceRunningInDiffLocation = ServerTroubleshooterUtil.getInstance().isServiceRunninginDiffererntLocation();
        DCStarter.logger.log(Level.INFO, "Is apache and desktop central service running in different location = " + isServiceRunningInDiffLocation);
        DCStarter.troubleShootLog.log(Level.INFO, "Is apache and desktop central service running in different location = " + isServiceRunningInDiffLocation);
        if (isServiceRunningInDiffLocation) {
            ServerTroubleshooterUtil.getInstance().serverStartupFailure("service_running_diff_location");
        }
        DCStarter.troubleShootLog.log(Level.INFO, "Going to do Disk space low check");
        final Boolean isDiskSpaceTooLow = ServerTroubleshooterUtil.getInstance().isDiskSpaceTooLow();
        DCStarter.logger.log(Level.INFO, "Is Disk space is less than 100 MB = " + isDiskSpaceTooLow);
        DCStarter.troubleShootLog.log(Level.INFO, "Is Disk space is less than 100 MB = " + isDiskSpaceTooLow);
        if (isDiskSpaceTooLow) {
            ServerTroubleshooterUtil.getInstance().serverStartupFailure("disk_space_low");
        }
    }
    
    private static void resetCustomBrowser() {
        WebServerUtil.openCustomBrowser(Boolean.FALSE, "Endpoint Central server has started");
    }
    
    private static void resetConf() {
        NginxServerUtils.resetNginxConf();
    }
    
    private static String getServerHome() throws Exception {
        return new File(System.getProperty("server.home")).getCanonicalPath();
    }
    
    private static void checkAndSetAJPPort() {
        try {
            final Properties wsProps = WebServerUtil.getWebServerSettings();
            final String ajpPortStr = wsProps.getProperty("ajp.port");
            if (ajpPortStr == null) {
                return;
            }
            final int ajpPort = Integer.parseInt(ajpPortStr);
            if (isPortFree(ajpPort)) {
                return;
            }
            final String ajpPortRangeStr = wsProps.getProperty("ajp.port.range");
            DCStarter.logger.log(Level.INFO, "AJP Port " + ajpPort + " is not free. Going to find a free port from the given range: " + ajpPortRangeStr);
            if (ajpPortRangeStr == null) {
                DCStarter.logger.log(Level.WARNING, "Unable to find free ajp port. ajp.port.range is empty.");
                return;
            }
            final String[] ajpPortArr = ajpPortRangeStr.split("-");
            if (ajpPortArr.length < 2) {
                DCStarter.logger.log(Level.WARNING, "Unable to find free ajp port. ajp.port.range is not in correct format.");
                return;
            }
            int startPort = Integer.parseInt(ajpPortArr[0]);
            int endPort = Integer.parseInt(ajpPortArr[1]);
            boolean portFound = false;
            if (startPort > endPort) {
                final int tmp = startPort;
                startPort = endPort;
                endPort = tmp;
            }
            for (int s = startPort; s <= endPort; ++s) {
                DCStarter.logger.log(Level.INFO, "Checking port (for AJP) whether it is free: " + s);
                if (isPortFree(s)) {
                    DCStarter.logger.log(Level.INFO, "Port (for AJP): " + s + " is found free. Going to set this port as AJP port.");
                    final Properties props = new Properties();
                    props.setProperty("ajp.port", String.valueOf(s));
                    WebServerUtil.storeProperWebServerSettings(props);
                    portFound = true;
                    break;
                }
            }
            if (!portFound) {
                DCStarter.logger.log(Level.WARNING, "Unable to find a free port for AJP from given range. It will attempt to use the same port configured already.");
            }
        }
        catch (final Exception ex) {
            DCStarter.logger.log(Level.WARNING, "Caught exception while checking whether ajp port is free.", ex);
        }
    }
    
    private static void checkChangesInWebSettingsConf() {
        try {
            if (isWebServerSettingsChanged() || WebServerUtil.isOSDHTTPConfFilesAvailable()) {
                DCStarter.logger.log(Level.INFO, "WebServer settings are found changed. Going to regenerate the conf files...");
                final Properties wsProps = WebServerUtil.getWebServerSettings();
                final String SERVER_HOST_FQDN = "server.fqdn";
                final String CIPHER_OPTION_KEY_NAME = "webserver.cipheroption";
                final String APACHE_CIPHER_KEY_NAME = "apache.sslciphersuite";
                boolean changedFlag = false;
                if (wsProps.getProperty(SERVER_HOST_FQDN) != null && wsProps.getProperty(SERVER_HOST_FQDN).trim().equals("")) {
                    wsProps.setProperty(SERVER_HOST_FQDN, InetAddress.getLocalHost().getCanonicalHostName());
                    changedFlag = true;
                }
                if (wsProps.getProperty(CIPHER_OPTION_KEY_NAME) != null && !wsProps.getProperty(CIPHER_OPTION_KEY_NAME).trim().isEmpty() && wsProps.getProperty(wsProps.getProperty(CIPHER_OPTION_KEY_NAME)) != null && !wsProps.getProperty(wsProps.getProperty(CIPHER_OPTION_KEY_NAME)).trim().isEmpty()) {
                    wsProps.setProperty(APACHE_CIPHER_KEY_NAME, wsProps.getProperty(wsProps.getProperty(CIPHER_OPTION_KEY_NAME).trim().toLowerCase()));
                    changedFlag = true;
                }
                if (changedFlag) {
                    final WebServerUtil webServerUtil = new WebServerUtil();
                    WebServerUtil.storeProperWebServerSettings(wsProps);
                }
                WebServerUtil.refreshWebServerSettings();
                saveWebServerSettingsLastModifiedTimeProperty(new Long(System.currentTimeMillis()));
            }
            else {
                DCStarter.logger.log(Level.INFO, "WebServer settings are found not changed. No need to regenerate the conf files...");
            }
        }
        catch (final Exception ex) {
            DCStarter.logger.log(Level.WARNING, "Caught exception while checking whether websettings.conf is modified ?", ex);
        }
    }
    
    public static boolean isWebServerSettingsChanged() {
        boolean changed = false;
        try {
            final Long prevModTime = getWebServerSettingsLastModifiedTimeProperty();
            DCStarter.logger.log(Level.INFO, "WebServerSettings Previous ModifiedTime: " + prevModTime);
            if (prevModTime == null) {
                DCStarter.logger.log(Level.INFO, "WebServerSettings Previous modified time is null. This might be the first server startup or first startup after restore...");
            }
            final long lastModTime = WebServerUtil.getWebServerSettingsLastModifiedTime();
            DCStarter.logger.log(Level.INFO, "Last modified time of " + WebServerUtil.WEB_SETTINGS_CONF_FILE + " from file system: " + lastModTime);
            if (prevModTime == null || lastModTime >= prevModTime) {
                changed = true;
                DCStarter.logger.log(Level.INFO, "WebServerSettings ModifiedTime: " + lastModTime);
            }
            final long serverlimitModTime = NginxServerUtils.getServerLimitSettingsLastModifiedTime();
            if (serverlimitModTime >= prevModTime) {
                changed = true;
                DCStarter.logger.log(Level.INFO, "ServerLimitingSettings ModifiedTime: " + serverlimitModTime);
            }
        }
        catch (final Exception ex) {
            DCStarter.logger.log(Level.WARNING, "Exception occurred while checking the " + WebServerUtil.WEB_SETTINGS_CONF_FILE + " is modified or not.", ex);
            changed = true;
        }
        return changed;
    }
    
    private static Long getWebServerSettingsLastModifiedTimeProperty() {
        Long lastModTime = null;
        try {
            final String fname = getServerHome() + File.separator + DCStarter.WEB_SETTINGS_CONF_MODTIME_PROPS_FILE;
            final Properties props = StartupUtil.getProperties(fname);
            if (props != null) {
                final String modTimeStr = props.getProperty("lastModifiedTime");
                if (modTimeStr != null) {
                    lastModTime = new Long(modTimeStr);
                }
            }
        }
        catch (final Exception ex) {
            DCStarter.logger.log(Level.WARNING, "Caught exception while retrieving websettings.conf prev modified time.", ex);
        }
        return lastModTime;
    }
    
    private static void saveWebServerSettingsLastModifiedTimeProperty(final long modTime) {
        try {
            final String fname = getServerHome() + File.separator + DCStarter.WEB_SETTINGS_CONF_MODTIME_PROPS_FILE;
            final Properties props = new Properties();
            props.setProperty("lastModifiedTime", String.valueOf(modTime));
            StartupUtil.storeProperties(props, fname);
        }
        catch (final Exception ex) {
            DCStarter.logger.log(Level.WARNING, "Caught exception while saving websettings.conf last modified time.", ex);
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
    
    public static boolean isUdpPortFree(final int portNum) {
        if (portNum < 0) {
            return false;
        }
        try {
            DCStarter.logger.log(Level.INFO, "Starting UDP socket for port : " + portNum);
            final DatagramSocket sock = new DatagramSocket(portNum);
            sock.close();
            return true;
        }
        catch (final Exception ex) {
            DCStarter.logger.log(Level.SEVERE, "Port " + portNum + " is already in use");
            return false;
        }
    }
    
    public static void startStandAloneApache() {
        try {
            final String stopResult = WebServerUtil.apacheHttpdInvoke(getServerHome(), "stop");
            DCStarter.logger.log(Level.INFO, "startStandAloneApache :: Stop Apache Service: " + stopResult);
            final String startResult = WebServerUtil.apacheHttpdInvoke(getServerHome(), "start");
            DCStarter.logger.log(Level.INFO, "startStandAloneApache :: Start Apache Service: " + startResult);
        }
        catch (final Exception ex) {
            DCStarter.logger.log(Level.SEVERE, "Exception while starting Apache Service as standalone: ", ex);
        }
    }
    
    private static void setDBHome() {
        try {
            System.setProperty("serverFailure.class", "com.me.devicemanagement.onpremise.server.common.ServerFailureHandlerImpl");
            final String dbname = getDBName();
            if (dbname == null || dbname.trim().equals("")) {
                DCStarter.logger.log(Level.WARNING, "DB Name is not known !!!");
                return;
            }
            String dbhome = System.getProperty("db.home");
            DCStarter.logger.log(Level.INFO, "old db.home before setting: " + dbhome);
            if (dbname.equalsIgnoreCase("mysql")) {
                dbhome = System.getProperty("mysql.home");
            }
            else if (dbname.equalsIgnoreCase("pgsql")) {
                dbhome = System.getProperty("pgsql.home");
            }
            DCStarter.logger.log(Level.INFO, "db.home going to be set: " + dbhome);
            if (dbhome != null && dbhome.trim().length() > 0) {
                System.setProperty("db.home", dbhome);
                DCStarter.logger.log(Level.INFO, "new db.home after setting: " + dbhome);
            }
        }
        catch (final Exception ex) {
            DCStarter.logger.log(Level.WARNING, "Caught exception while setting db.home System propery.");
        }
    }
    
    private static String getDBName() {
        String dbName = null;
        try {
            final String fname = getServerHome() + File.separator + "conf" + File.separator + "database_params.conf";
            final Properties dbProps = StartupUtil.getProperties(fname);
            if (dbProps != null) {
                final String url = dbProps.getProperty("url");
                DCStarter.logger.log(Level.INFO, "URL to find DB name is : " + url + "\t from file: " + fname);
                if (url != null) {
                    if (url.toLowerCase().contains("mysql")) {
                        dbName = "mysql";
                    }
                    else if (url.toLowerCase().contains("postgresql")) {
                        dbName = "pgsql";
                    }
                }
            }
        }
        catch (final Exception ex) {
            DCStarter.logger.log(Level.WARNING, "Caught exception while getting dbname...");
        }
        return dbName;
    }
    
    private static void checkAndStartAsFos() {
        try {
            Boolean fosFlag = isFosFileConfigured();
            if (fosFlag) {
                if (!checkValidLicenseForFos()) {
                    DCStarter.logger.log(Level.SEVERE, "Invalid License For Fail Over Service, Starting the server normally.., Not as FailOver Service");
                    fosFlag = false;
                }
                if (!isRemoteDB()) {
                    DCStarter.logger.log(Level.SEVERE, "Remote Database is not used for Fail Over Service, Starting the server normally.., Not as Fail Over Service");
                    fosFlag = false;
                }
                if (!isSpecifiedIP()) {
                    DCStarter.logger.log(Level.SEVERE, "Fail Over Server should be started in one of the specified IP's");
                    fosFlag = false;
                }
            }
            modifyModuleStartStopProcessor(fosFlag);
        }
        catch (final Exception ex) {
            Logger.getLogger(DCStarter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static boolean checkValidLicenseForFos() {
        final String homeDir = System.getProperty("server.home");
        final Wield w = Wield.getInstance();
        w.validateInvoke("License Agreement", homeDir, false, "lib", true);
        Boolean isValidLicenseForFos = Boolean.FALSE;
        try {
            if (w.getUserType().equalsIgnoreCase("T")) {
                isValidLicenseForFos = Boolean.TRUE;
            }
            else {
                Properties fosProp = w.getModuleProperties("FailOverService");
                if (fosProp == null) {
                    fosProp = w.getModuleProperties("AddOnModules");
                }
                if (fosProp == null && !isFosTrialFlagEnabled()) {
                    isValidLicenseForFos = Boolean.FALSE;
                }
                else {
                    if (fosProp != null) {
                        isValidLicenseForFos = Boolean.parseBoolean(fosProp.getProperty("FOSEnabled"));
                    }
                    if (!isValidLicenseForFos && isFosTrialFlagEnabled()) {
                        final long expiryPeriod = getFosTrialExpiryPeriod();
                        if (expiryPeriod > 0L) {
                            isValidLicenseForFos = Boolean.TRUE;
                            DCStarter.logger.log(Level.INFO, "Enabling FOS mode start as trial license active");
                        }
                        else {
                            isValidLicenseForFos = Boolean.FALSE;
                            DCStarter.logger.log(Level.INFO, "Restricting FOS mode start as trial license expired");
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(DCStarter.class.getName()).log(Level.SEVERE, "Fos License File Exception", ex);
        }
        DCStarter.logger.log(Level.INFO, "License Valid For Fail Over Service : {0}", isValidLicenseForFos);
        return isValidLicenseForFos;
    }
    
    public static Boolean isFosTrialFlagEnabled() {
        try {
            final EnDecrypt ed = (EnDecrypt)new EnDecryptAES256Impl();
            CryptoUtil.setEnDecryptInstance(ed);
            final String confFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos_user.conf";
            if (new File(confFile).exists()) {
                final Properties fosProps = StartupUtil.getProperties(confFile);
                if (fosProps != null) {
                    final String isFosTrialed = fosProps.getProperty("isFosTrialed");
                    if (isFosTrialed != null) {
                        if (CryptoUtil.decrypt(isFosTrialed).equalsIgnoreCase("true")) {
                            return Boolean.TRUE;
                        }
                        return Boolean.FALSE;
                    }
                }
            }
        }
        catch (final Exception exc) {
            DCStarter.logger.log(Level.WARNING, " error while checking FOS trial enabled flag");
        }
        return Boolean.FALSE;
    }
    
    public static long getFosTrialExpiryPeriod() {
        long dateDiff = -1L;
        try {
            final EnDecrypt ed = (EnDecrypt)new EnDecryptAES256Impl();
            CryptoUtil.setEnDecryptInstance(ed);
            final String confFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos_user.conf";
            if (new File(confFile).exists()) {
                final Properties fosProps = StartupUtil.getProperties(confFile);
                if (fosProps != null) {
                    final String fosTrialValidity = fosProps.getProperty("FosTrialValidity");
                    if (fosTrialValidity != null) {
                        final String expiryDate = CryptoUtil.decrypt(fosTrialValidity);
                        final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        final Date date = formatter.parse(expiryDate);
                        final Date today = Calendar.getInstance().getTime();
                        dateDiff = getDateDiff(today.getTime(), date.getTime());
                        DCStarter.logger.log(Level.INFO, " DB FOS Trial expiry period " + dateDiff);
                        return dateDiff;
                    }
                }
            }
        }
        catch (final Exception exc) {
            DCStarter.logger.log(Level.WARNING, " error while fetch FOS trial license period ");
        }
        DCStarter.logger.log(Level.INFO, " DB FOS Trial expiry period " + dateDiff);
        return dateDiff;
    }
    
    public static long getDateDiff(final long startTimeInMS, final long endTimeInMS) {
        return (endTimeInMS - startTimeInMS) / 86400000L;
    }
    
    public static boolean isFosFileConfigured() {
        final String confFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos_user.conf";
        Boolean isFosFileConfigured = Boolean.FALSE;
        if (new File(confFile).exists()) {
            final Properties fosProps = StartupUtil.getProperties(confFile);
            if (fosProps != null && fosProps.getProperty("EnableFos") != null && fosProps.getProperty("EnableFos").trim().equalsIgnoreCase("true") && fosProps.getProperty("PublicIP") != null && !fosProps.getProperty("PublicIP").isEmpty()) {
                isFosFileConfigured = Boolean.TRUE;
            }
        }
        DCStarter.logger.log(Level.INFO, "Fos File Configured Properly Check : {0}", isFosFileConfigured);
        return isFosFileConfigured;
    }
    
    private static boolean isRemoteDB() {
        boolean isRemoteDB = false;
        final String fname = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
        final Properties dbProps = StartupUtil.getProperties(fname);
        if (dbProps != null) {
            try {
                final String URL = dbProps.getProperty("url");
                StringTokenizer stk = new StringTokenizer(URL, "//", false);
                stk.nextToken();
                String tok = stk.nextToken();
                stk = new StringTokenizer(tok, ";", false);
                tok = stk.nextToken();
                String hostName = null;
                if (tok.indexOf(":") < 0) {
                    hostName = tok;
                }
                else {
                    final StringTokenizer stk2 = new StringTokenizer(tok, ":", false);
                    hostName = stk2.nextToken();
                }
                final String serverName = hostName;
                if (serverName != null && !serverName.equalsIgnoreCase("localhost") && !serverName.equalsIgnoreCase("127.0.0.1") && !serverName.equalsIgnoreCase(InetAddress.getLocalHost().getHostName()) && !serverName.equalsIgnoreCase(InetAddress.getLocalHost().getHostAddress())) {
                    isRemoteDB = true;
                }
            }
            catch (final UnknownHostException ex) {
                Logger.getLogger(DCStarter.class.getName()).log(Level.SEVERE, "Host name cannot be retrieved..", ex);
            }
        }
        DCStarter.logger.log(Level.INFO, "Remote Database Check : {0}", isRemoteDB);
        return isRemoteDB;
    }
    
    private static boolean isSpecifiedIP() {
        final String FosUserConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos_user.conf";
        final Properties Props = StartupUtil.getProperties(FosUserConfFile);
        final String PrimaryIp = Props.getProperty("PrimaryServerIP");
        final String SecondaryIp = Props.getProperty("SecondaryServerIP");
        if (isIPPresent(PrimaryIp)) {
            DCStarter.ipAddress = PrimaryIp;
            return true;
        }
        if (isIPPresent(SecondaryIp)) {
            DCStarter.ipAddress = SecondaryIp;
            return true;
        }
        return false;
    }
    
    private static void generateFosConf() {
        try {
            final String FosUserConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos_user.conf";
            final String FosConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos.conf";
            final String FosTemplateConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos.conf.template";
            final Properties Props = StartupUtil.getProperties(FosUserConfFile);
            final Properties fosProps = new Properties();
            fosProps.setProperty("publicIP", DCStarter.publicIP = Props.getProperty("PublicIP").trim());
            fosProps.setProperty("ipaddr", DCStarter.ipAddress);
            fosProps.setProperty("publicIP.Ifname", getNICForIP(DCStarter.ipAddress).trim());
            fosProps.setProperty("connectorPort", String.valueOf(WebServerUtil.getServerPort()));
            fosProps.setProperty("remoteinstallationDir", StartupUtil.getInstallationDirName());
            com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(FosTemplateConfFile, FosConfFile, fosProps, "%");
        }
        catch (final Exception ex) {
            Logger.getLogger(DCStarter.class.getName()).log(Level.SEVERE, "Exception while populating fos.conf file", ex);
        }
    }
    
    public static void modifyModuleStartStopProcessor(final Boolean fosFlag) {
        try {
            final File file = new File(System.getProperty("server.home") + File.separator + "conf" + File.separator + "Persistence" + File.separator + "module-startstop-processors.xml");
            final DocumentBuilder docBuilder = XMLUtils.getDocumentBuilderInstance();
            final Document doc = docBuilder.parse(file);
            final Element root = doc.getDocumentElement();
            final NodeList connList = root.getElementsByTagName("ModuleStartStopProcessor");
            final Boolean FosElement = getFosElement(connList);
            if (fosFlag) {
                generateFosConf();
                if (!FosElement) {
                    final Element moduleprocessor = doc.createElement("ModuleStartStopProcessor");
                    moduleprocessor.setAttribute("CLASSNAME", "com.adventnet.persistence.fos.FOS");
                    moduleprocessor.setAttribute("PROCESSOR_NAME", "FOS");
                    final Element moduleprocessorMonitor = doc.createElement("ModuleStartStopProcessor");
                    moduleprocessorMonitor.setAttribute("CLASSNAME", getFOSStartStopProcessor());
                    moduleprocessorMonitor.setAttribute("PROCESSOR_NAME", "FOSMonitor");
                    root.appendChild(moduleprocessor);
                    root.appendChild(moduleprocessorMonitor);
                    writeXml(doc, file);
                    modifyWebServerSettingsForFOS();
                }
                else if (checkAndModifyFosMonitorClass(connList)) {
                    writeXml(doc, file);
                }
            }
            else if (FosElement) {
                removeNode(connList, "FOSMonitor");
                writeXml(doc, file);
                removeNode(connList, "FOS");
                writeXml(doc, file);
            }
        }
        catch (final ParserConfigurationException ex) {
            Logger.getLogger(DCStarter.class.getName()).log(Level.SEVERE, "ParserConfigurationException while parsing ModuleStartStopProcessor.xml file: ", ex);
        }
        catch (final SAXException ex2) {
            Logger.getLogger(DCStarter.class.getName()).log(Level.SEVERE, "SAXException while parsing ModuleStartStopProcessor.xml file: ", ex2);
        }
        catch (final TransformerException ex3) {
            Logger.getLogger(DCStarter.class.getName()).log(Level.SEVERE, "TransformerException while Modifying ModuleStartStopProcessor.xml file: ", ex3);
        }
        catch (final IOException ex4) {
            Logger.getLogger(DCStarter.class.getName()).log(Level.SEVERE, "IOException while modifying ModuleStartStopProcessor.xml file", ex4);
        }
    }
    
    public static void writeXml(final Document doc, final File file) throws TransformerException {
        final DOMSource source = new DOMSource(doc);
        final Transformer transformer = XMLUtils.getTransformerInstance();
        transformer.setOutputProperty("indent", "yes");
        final StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }
    
    public static Boolean getFosElement(final NodeList connList) {
        if (connList.getLength() > 0) {
            for (int i = 0; i < connList.getLength(); ++i) {
                final Element moduleProcessor = (Element)connList.item(i);
                if (moduleProcessor.getAttribute("PROCESSOR_NAME").equalsIgnoreCase("FOS") || moduleProcessor.getAttribute("PROCESSOR_NAME").equalsIgnoreCase("FOSMonitor")) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void removeNode(final NodeList connList, final String attribute) {
        if (connList.getLength() > 0) {
            for (int i = 0; i <= connList.getLength(); ++i) {
                final Element moduleProcessor = (Element)connList.item(i);
                if (moduleProcessor.getAttribute("PROCESSOR_NAME").equalsIgnoreCase(attribute)) {
                    moduleProcessor.getParentNode().removeChild(moduleProcessor);
                }
            }
        }
    }
    
    public static Boolean checkAndModifyFosMonitorClass(final NodeList connList) {
        if (connList.getLength() > 0) {
            for (int i = 0; i <= connList.getLength(); ++i) {
                final Element moduleProcessor = (Element)connList.item(i);
                if (moduleProcessor.getAttribute("PROCESSOR_NAME") != null && moduleProcessor.getAttribute("PROCESSOR_NAME").equalsIgnoreCase("FOSMonitor") && moduleProcessor.getAttribute("CLASSNAME") != null && !moduleProcessor.getAttribute("CLASSNAME").equalsIgnoreCase(getFOSStartStopProcessor())) {
                    moduleProcessor.setAttribute("CLASSNAME", getFOSStartStopProcessor());
                    return true;
                }
            }
        }
        return false;
    }
    
    public static String getNICForIP(final String serverIP) {
        String NIC = "";
        try {
            final Process process = new ProcessBuilder(new String[] { "networkAdapter.exe", serverIP }).start();
            final BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));
            for (String str = buf.readLine(); str != null; str = buf.readLine()) {
                DCStarter.logger.log(Level.WARNING, "String  " + str);
                NIC += str;
            }
            return NIC;
        }
        catch (final IOException ex) {
            Logger.getLogger(DCStarter.class.getName()).log(Level.SEVERE, "Exception while getting NIC from IP..", ex);
            return NIC;
        }
    }
    
    public static void modifyWebServerSettingsForFOS() {
        try {
            final Properties wsProps = WebServerUtil.getWebServerSettings();
            final String SERVER_HOST_FQDN = "server.fqdn";
            wsProps.setProperty(SERVER_HOST_FQDN, DCStarter.publicIP);
            final WebServerUtil webServerUtil = new WebServerUtil();
            WebServerUtil.storeProperWebServerSettings(wsProps);
        }
        catch (final Exception ex) {
            Logger.getLogger(DCStarter.class.getName()).log(Level.SEVERE, "Exception while setting public IP as FQDN for FOS..", ex);
        }
    }
    
    public static boolean isIPPresent(final String ipAddr) {
        Enumeration<NetworkInterface> adapters;
        try {
            adapters = NetworkInterface.getNetworkInterfaces();
        }
        catch (final SocketException exp) {
            throw new RuntimeException(exp);
        }
        while (adapters != null && adapters.hasMoreElements()) {
            final NetworkInterface adapter = adapters.nextElement();
            final Enumeration<InetAddress> addrList = adapter.getInetAddresses();
            while (addrList.hasMoreElements()) {
                final InetAddress inetAddress = addrList.nextElement();
                if (ipAddr.equals(inetAddress.getHostAddress())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static void setDcOsArchitecture() {
        try {
            final String fileLocation = getServerHome() + File.separator + DCStarter.SET_OS_ARCHITECTURE_FILE;
            DCStarter.logger.log(Level.INFO, "Calling the command {0}", fileLocation);
            final ProcessBuilder processBuilder = new ProcessBuilder(new String[] { fileLocation });
            final File file = new File(getServerHome() + File.separator + "bin");
            processBuilder.directory(file);
            final Process process = processBuilder.start();
            final int processStatus = process.waitFor();
            DCStarter.logger.log(Level.INFO, "Process Status for the command {0} is {1}", new Object[] { fileLocation, processStatus });
        }
        catch (final Exception ex) {
            DCStarter.logger.log(Level.SEVERE, "Exception occurs in the method setDcOsArchitecture()", ex);
        }
    }
    
    private static void cleanupFSMCorruptedFiles() {
        BufferedReader bufferedReader = null;
        try {
            final String fileName = getServerHome() + File.separator + "conf" + File.separator + "User-Conf" + File.separator + "FSMCorruptionList.txt";
            if (new File(fileName).exists()) {
                bufferedReader = new BufferedReader(new FileReader(new File(fileName)));
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    if (new File(line).exists()) {
                        DCStarter.logger.log(Level.INFO, "FSM Corrupted File {0} deletion status {1} ", new Object[] { line, new File(line).delete() });
                    }
                    else {
                        DCStarter.logger.log(Level.INFO, "File {0} exists but setup doesn't contain this file", new Object[] { line });
                    }
                }
            }
            else {
                DCStarter.logger.log(Level.INFO, "FSMCorruption file Not exists. Desktopcentral Server was running fine without FSM Corruption.");
            }
        }
        catch (final Exception ex) {
            DCStarter.logger.log(Level.WARNING, "Unable to cleanup FSM Corrupted files. Exception : ", ex);
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (final Exception ex) {
                    DCStarter.logger.log(Level.WARNING, "Exception : ", ex);
                }
            }
            try {
                final String fileName = getServerHome() + File.separator + "conf" + File.separator + "User-Conf" + File.separator + "FSMCorruptionList.txt";
                if (new File(fileName).exists()) {
                    DCStarter.logger.log(Level.INFO, "FSM Corruption list file deletion status : " + new File(fileName).delete());
                }
            }
            catch (final Exception ex) {
                DCStarter.logger.log(Level.WARNING, "Exception occurred while deleting FSMCorruptin list file. Exception : ", ex);
            }
        }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (final Exception ex2) {
                    DCStarter.logger.log(Level.WARNING, "Exception : ", ex2);
                }
            }
            try {
                final String fileName2 = getServerHome() + File.separator + "conf" + File.separator + "User-Conf" + File.separator + "FSMCorruptionList.txt";
                if (new File(fileName2).exists()) {
                    DCStarter.logger.log(Level.INFO, "FSM Corruption list file deletion status : " + new File(fileName2).delete());
                }
            }
            catch (final Exception ex2) {
                DCStarter.logger.log(Level.WARNING, "Exception occurred while deleting FSMCorruptin list file. Exception : ", ex2);
            }
        }
    }
    
    public static String getFOSStartStopProcessor() {
        final String FosConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos.conf";
        final Properties props = StartupUtil.getProperties(FosConfFile);
        if (props.size() > 0 && props.containsKey("fos.startstopprocessor")) {
            return props.getProperty("fos.startstopprocessor").trim();
        }
        return "";
    }
    
    public static String getPostgresVersion(final String pg_Home) throws IOException, InterruptedException {
        DCStarter.logger.info("Getting current version using 'postgres' binary.");
        final boolean isWindows = isWindows();
        final List<String> commandList = new ArrayList<String>();
        final Path postgresPath = Paths.get(pg_Home, "bin", "postgres" + (isWindows ? ".exe" : ""));
        commandList.add(postgresPath.toString());
        commandList.add("-V");
        Process postgresProcess = null;
        BufferedReader br = null;
        try {
            postgresProcess = executeCommand(commandList, null, null);
            br = new BufferedReader(new InputStreamReader(postgresProcess.getInputStream()));
            final String ipStream = br.readLine();
            final int waitFor = postgresProcess.waitFor();
            DCStarter.logger.log(Level.INFO, "postgresProcess waitFor :: {0}", waitFor);
            DCStarter.logger.log(Level.INFO, "Returning version {0}", ipStream);
            return ipStream;
        }
        finally {
            if (br != null) {
                br.close();
            }
            if (postgresProcess != null) {
                postgresProcess.destroy();
            }
        }
    }
    
    private static boolean isWindows() {
        final String osName = System.getProperty("os.name").trim().toLowerCase(Locale.ENGLISH);
        return osName.indexOf("windows") >= 0;
    }
    
    public static Process executeCommand(final List<String> commandList, final Properties envProps, final File directoryPath) throws IOException {
        return executeCommand(commandList, envProps, directoryPath, false, true);
    }
    
    public static Process executeCommand(final List<String> commandList, final Properties envProps, final File directoryPath, final boolean writeToFile, final boolean executeCmd) throws IOException {
        final boolean isWindows = isWindows();
        if (!writeToFile || isWindows) {
            DCStarter.logger.log(Level.INFO, "Command to be executed ::: {0}", commandList);
            final ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            if (directoryPath != null) {
                processBuilder.directory(directoryPath);
            }
            setEnvProps(processBuilder, envProps);
            return processBuilder.start();
        }
        final File extFile = new File(new File(System.getProperty("server.home")).getAbsolutePath() + File.separator + "ext.sh");
        DCStarter.logger.log(Level.INFO, "Writing comman to ext.sh file ::: {0}", commandList);
        final RandomAccessFile f = new RandomAccessFile(extFile.getAbsolutePath(), "rw");
        if (extFile.length() != 0L) {
            f.seek(extFile.length());
            f.write(System.getProperty("line.separator").getBytes());
        }
        for (final String cmd : commandList) {
            f.write(cmd.toString().getBytes());
            f.write(" ".getBytes());
        }
        f.close();
        if (executeCmd) {
            DCStarter.logger.info("Executing all commands in ext.sh ");
            final List<String> extCmdList = new ArrayList<String>();
            extCmdList.add("sh");
            extCmdList.add(extFile.getAbsolutePath());
            DCStarter.logger.log(Level.INFO, "Command to be executed ::: {0}", extCmdList);
            final ProcessBuilder processBuilder2 = new ProcessBuilder(extCmdList);
            processBuilder2.directory(directoryPath);
            setEnvProps(processBuilder2, envProps);
            return processBuilder2.start();
        }
        return null;
    }
    
    public static Process executeCommand(final List<String> commandList, final Properties envProps, final File directoryPath, final boolean writeToFile, final boolean executeCmd, final List<String> valuesToMask) throws IOException {
        final boolean isWindows = isWindows();
        if (!writeToFile || isWindows) {
            DCStarter.logger.log(Level.INFO, "Command to be executed ::: {0}", commandList);
            final ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            if (directoryPath != null) {
                processBuilder.directory(directoryPath);
            }
            setEnvProps(processBuilder, envProps);
            return processBuilder.start();
        }
        final File extFile = new File(new File(System.getProperty("server.home")).getAbsolutePath() + File.separator + "ext.sh");
        DCStarter.logger.log(Level.INFO, "Writing comman to ext.sh file ::: {0}", commandList);
        final RandomAccessFile f = new RandomAccessFile(extFile.getAbsolutePath(), "rw");
        if (extFile.length() != 0L) {
            f.seek(extFile.length());
            f.write(System.getProperty("line.separator").getBytes());
        }
        for (final String cmd : commandList) {
            f.write(cmd.toString().getBytes());
            f.write(" ".getBytes());
        }
        f.close();
        if (executeCmd) {
            DCStarter.logger.info("Executing all commands in ext.sh ");
            final List<String> extCmdList = new ArrayList<String>();
            extCmdList.add("sh");
            extCmdList.add(extFile.getAbsolutePath());
            DCStarter.logger.log(Level.INFO, "Command to be executed ::: {0}", extCmdList);
            final ProcessBuilder processBuilder = new ProcessBuilder(extCmdList);
            processBuilder.directory(directoryPath);
            setEnvProps(processBuilder, envProps);
            return processBuilder.start();
        }
        return null;
    }
    
    public static void setEnvProps(final ProcessBuilder processBuilder, final Properties envVariables) {
        if (envVariables != null) {
            final Map<String, String> environment = processBuilder.environment();
            final Enumeration<Object> keys = ((Hashtable<Object, V>)envVariables).keys();
            while (keys.hasMoreElements()) {
                final String key = keys.nextElement();
                environment.put(key, envVariables.getProperty(key));
            }
        }
    }
    
    static {
        WEB_SETTINGS_CONF_MODTIME_PROPS_FILE = "conf" + File.separator + "ws.modtime";
        SET_OS_ARCHITECTURE_FILE = "bin" + File.separator + "setOSInfoAsAdmin.bat";
        DCStarter.logger = Logger.getLogger("DCServiceLogger");
        DCStarter.troubleShootLog = Logger.getLogger("ServerTroubleshooterLogger");
        DCStarter.snapshotEnabled = 1;
        DCStarter.startupStatusNotifier = null;
        DCStarter.ipAddress = "";
    }
}
