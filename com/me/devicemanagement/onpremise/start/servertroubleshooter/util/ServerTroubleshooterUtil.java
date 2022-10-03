package com.me.devicemanagement.onpremise.start.servertroubleshooter.util;

import java.util.Hashtable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FilenameFilter;
import java.util.ArrayDeque;
import java.util.StringTokenizer;
import java.net.SocketException;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.io.FileWriter;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.w3c.dom.Attr;
import java.util.Iterator;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerException;
import java.io.Writer;
import java.io.StringWriter;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Node;
import java.net.InetAddress;
import javax.xml.transform.Transformer;
import java.io.IOException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import com.me.devicemanagement.framework.utils.XMLUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import com.me.devicemanagement.onpremise.properties.util.GeneralPropertiesLoader;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.mfw.Starter;
import com.me.devicemanagement.onpremise.start.DCStarter;
import com.me.devicemanagement.onpremise.start.util.NginxServerUtils;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Locale;
import com.zoho.framework.utils.crypto.EnDecrypt;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.zoho.framework.utils.crypto.EnDecryptAES256Impl;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.Properties;
import java.io.File;
import java.util.logging.Logger;

public class ServerTroubleshooterUtil
{
    public static Logger logger;
    public static String serverHome;
    public static ServerTroubleshooterUtil serverTroubleshooterUtil;
    public static String dbSettingsConf;
    public static Exception firstStartupFailure;
    static String isTroubleShootEnabled;
    String serverLocation;
    static String pageTitle;
    static String heading;
    static String unReachableMsg;
    static String solution;
    static String solutionDetails;
    static String recommendation;
    static String solutionHeading;
    static String productUrl;
    static String uploadLogsUrl;
    static String contactSupportMsg;
    static String mailContent;
    static String subject;
    
    public ServerTroubleshooterUtil() {
        this.serverLocation = this.getServerRunningLocation();
    }
    
    public void serverStartupFailure(final String failureCase) throws Exception {
        this.postStartupFailureDetails(failureCase);
        switch (failureCase) {
            case "service_running_diff_location": {
                this.serviceRunningInDifferentLocation();
                break;
            }
            case "disk_space_low": {
                this.diskSpaceTooLow(true);
                break;
            }
            case "arch_incompatible": {
                this.isArchitectureIncompatible();
                break;
            }
            case "pgsql_startup_failure_unknown_case": {
                final String pgLog = ServerTroubleshooterUtil.serverHome + File.separator + "pgsql" + File.separator + "data" + File.separator + "pg_log";
                if (copyDebuggerLogs(pgLog)) {
                    this.copyPgLogs(true);
                    break;
                }
                this.copyPgLogs(false);
                break;
            }
            case "mssql_invalid_login_credentials": {
                this.sqlAuthenticationFailure();
                break;
            }
            case "mssql_transaction_log_size_exceed": {
                this.sqlTransactionLogSizeExceed();
                break;
            }
            case "mssql_connection_lost": {
                this.sqlConnectionLost();
                break;
            }
            case "ppm_lock": {
                this.ppmLockFileFound();
                break;
            }
            case "build_number_incompatible": {
                this.buildNumberInCompatibilityFound();
                break;
            }
            case "revert_lock": {
                this.revertLockFileFound();
                break;
            }
            case "migration_lock": {
                this.migrationLockFileFound();
                break;
            }
            case "file_db_mismatch": {
                this.dbMismatchWithFileSystem();
                break;
            }
            case "ssl_certificate_missing": {
                this.errorInSSLCertificate();
                break;
            }
            case "ssl_certificate_key_mismatch": {
                this.sslCertificateKeyMismatch();
                break;
            }
            case "ssl_certificate_key_encrypted": {
                this.sslCertificateKeyEncrypted();
                break;
            }
            case "ssl_certificate_chain_issue": {
                this.sslCertificateChainIssue();
                break;
            }
            case "webserver_port_in_use": {
                this.webServerPortInUse();
                break;
            }
            case "software_or_patch_store_not_reachable": {
                this.patchOrSoftwareLocationNotReachable();
                break;
            }
            case "osd_repo_not_reachable": {
                this.osdRepositoryNotReachable();
                break;
            }
            case "pgsql_in_recovery_mode": {
                this.postgresInRecovery();
                break;
            }
            case "pgsql_fails_in_recovery_mode": {
                this.pgFailsInRecovery();
                break;
            }
            case "remote_pg_not_compatible": {
                this.remotePGNotcompatible();
                break;
            }
        }
    }
    
    public void remotePGNotcompatible() throws Exception {
        this.getRootCauseSolutionForStartupFailure("remote_pg_not_compatible");
        this.showSolutionPage("remote_pg_not_compatible");
    }
    
    public void pgFailsInRecovery() throws Exception {
        this.getRootCauseSolutionForStartupFailure("pgsql_fails_in_recovery_mode");
        this.showSolutionPage("pgsql_fails_in_recovery_mode");
    }
    
    private void sqlConnectionLost() throws Exception {
        ServerTroubleshooterUtil.logger.info("in sqlConnectionLost block");
        final Properties sqlRetryProps = this.getConfProperties("SQLConnectionRetryTool.conf");
        if (sqlRetryProps.getProperty("retry.enabled").equalsIgnoreCase("true")) {
            this.getRootCauseSolutionForStartupFailure("mssql_connection_retry");
            this.showSolutionPage("mssql_connection_retry");
            ServerTroubleshooterUtil.logger.info("Calling sqlserverretry.bat to auto retry sql server");
            final ProcessBuilder pb = new ProcessBuilder(new String[] { "cmd", "/c", "sqlserverretry.bat", "start", sqlRetryProps.getProperty("retry.interval"), this.getSqlServerPassword() });
            final File dir = new File(System.getProperty("server.home") + File.separator + "bin");
            pb.directory(dir);
            pb.start();
        }
        else {
            ServerTroubleshooterUtil.logger.info("SQL Retry disabled, showing default page.");
            this.getRootCauseSolutionForStartupFailure("mssql_connection_lost");
            this.showSolutionPage("mssql_connection_lost");
        }
    }
    
    public void postgresInRecovery() throws Exception {
        this.getRootCauseSolutionForStartupFailure("pgsql_in_recovery_mode");
        this.showSolutionPage("pgsql_in_recovery_mode");
    }
    
    private Properties getConfProperties(final String confFile_Name) throws Exception {
        ServerTroubleshooterUtil.logger.log(Level.FINE, "Getting properties from conf file : " + confFile_Name);
        final String confFileLoc = System.getProperty("server.home") + File.separator + "ServerTroubleShooter" + File.separator + "conf" + File.separator + confFile_Name;
        final Properties confProperties = new Properties();
        final File conffile = new File(confFileLoc);
        if (!conffile.exists()) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "conf file not found setting default values");
        }
        else {
            confProperties.load(new FileInputStream(confFileLoc));
            ServerTroubleshooterUtil.logger.log(Level.FINE, "Properties : " + confProperties.toString());
        }
        return confProperties;
    }
    
    private String getSqlServerPassword() {
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
            catch (final Exception e) {
                ServerTroubleshooterUtil.logger.log(Level.WARNING, "Caught exception while loading properties from: " + dbConfFile + " Exception: " + e);
                try {
                    if (fis != null) {
                        fis.close();
                    }
                }
                catch (final Exception e) {
                    ServerTroubleshooterUtil.logger.log(Level.WARNING, "Caught exception while loading properties from: " + dbConfFile + " Exception: " + e);
                }
            }
            finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                }
                catch (final Exception e2) {
                    ServerTroubleshooterUtil.logger.log(Level.WARNING, "Caught exception while loading properties from: " + dbConfFile + " Exception: " + e2);
                }
            }
            final EnDecrypt cryptInstance = (EnDecrypt)new EnDecryptAES256Impl();
            CryptoUtil.setEnDecryptInstance(cryptInstance);
            final String password = props.getProperty("password");
            return CryptoUtil.decrypt(password, 2);
        }
        catch (final Exception e3) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Exception while getting SQL Server password ", e3);
            return null;
        }
    }
    
    public static String getString(final String key, Locale locale) {
        String value = "";
        try {
            if (locale == null) {
                locale = Locale.getDefault();
            }
            value = ResourceBundle.getBundle("resources/troubleshooter/troubleshooter_solution", locale).getString(key);
            ServerTroubleshooterUtil.logger.log(Level.FINEST, "Key :value = " + key + ":" + value);
            return value;
        }
        catch (final MissingResourceException mre) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Missing troubleshooter solution  for locale : " + locale + " ; Key : " + key, mre);
            return "";
        }
        catch (final Exception ex) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Exception in getting value for locale : " + locale + " ; Key : " + key, ex);
            return "";
        }
    }
    
    public void sslCertificateChainIssue() throws Exception {
        this.getRootCauseSolutionForStartupFailure("ssl_certificate_chain_issue");
        final HashMap troubleShootMap = this.getHashMapToGenerateEmailContent(ServerTroubleshooterUtil.mailContent);
        this.alertCustomerThroughMail(troubleShootMap);
    }
    
    public void sslCertificateKeyEncrypted() throws Exception {
        this.getRootCauseSolutionForStartupFailure("ssl_certificate_key_encrypted");
        final HashMap troubleShootMap = this.getHashMapToGenerateEmailContent(ServerTroubleshooterUtil.mailContent);
        this.alertCustomerThroughMail(troubleShootMap);
    }
    
    public void patchOrSoftwareLocationNotReachable() throws Exception {
        final Properties shareAccess = WebServerUtil.getRepositoriesAccessStatus();
        final Boolean storeStatus = Boolean.valueOf(shareAccess.getProperty("store_access_status"));
        final Boolean swStatus = Boolean.valueOf(shareAccess.getProperty("swrepository_access_status"));
        if (!storeStatus) {
            this.getRootCauseSolutionForStartupFailure("patch_not_reachable");
        }
        if (!swStatus) {
            this.getRootCauseSolutionForStartupFailure("software_not_reachable");
        }
        if (!storeStatus && !swStatus) {
            this.getRootCauseSolutionForStartupFailure("software_or_patch_store_not_reachable");
        }
        this.showSolutionPage("software_or_patch_store_not_reachable");
        final HashMap troubleShootMap = this.getHashMapToGenerateEmailContent(ServerTroubleshooterUtil.mailContent);
        this.alertCustomerThroughMail(troubleShootMap);
    }
    
    public void osdRepositoryNotReachable() throws Exception {
        final Properties shareAccess = WebServerUtil.getRepositoriesAccessStatus();
        final Boolean osdStatus = Boolean.valueOf(shareAccess.getProperty("osd_repository_access_status"));
        if (!osdStatus) {
            this.getRootCauseSolutionForStartupFailure("osd_repo_not_reachable");
        }
        this.showSolutionPage("osd_repo_not_reachable");
        final HashMap troubleShootMap = this.getHashMapToGenerateEmailContent(ServerTroubleshooterUtil.mailContent);
        this.alertCustomerThroughMail(troubleShootMap);
    }
    
    public String getUsedPort() {
        final Properties sharedAccessProps = StartupUtil.getProperties(ServerTroubleshooterUtil.serverHome + File.separator + WebServerUtil.SHARE_ACCESS_STATUS_FILE);
        final String httpPort = sharedAccessProps.getProperty("http.port");
        final String httpsPort = sharedAccessProps.getProperty("https.port");
        final String usedPort = (httpPort != null) ? httpPort : httpsPort;
        return usedPort;
    }
    
    public void webServerPortInUse() throws Exception {
        final Properties webServerProps = WebServerUtil.getWebServerSettings();
        ServerTroubleshooterUtil.logger.log(Level.INFO, "Share Access = " + webServerProps);
        final String httpPort = webServerProps.getProperty("http.port");
        final String httpsPort = webServerProps.getProperty("https.port");
        final String usedPort = this.getUsedPort();
        if (httpPort != null || httpsPort != null) {
            ServerTroubleshooterUtil.logger.log(Level.INFO, "usedPort = " + usedPort);
            this.getRootCauseSolutionForStartupFailure("webserver_port_in_use");
            this.showSolutionPage("webserver_port_in_use");
            if (ServerTroubleshooterUtil.isTroubleShootEnabled.equalsIgnoreCase("true")) {
                final String webSettingsConf = WebServerUtil.getServerHomeCanonicalPath() + File.separator + "conf" + File.separator + "websettings.conf";
                if (new File(webSettingsConf).exists()) {
                    copyFilesToLog(webSettingsConf, "websettings.conf");
                }
                final String webServerConfFile = WebServerUtil.getWebServerConfFilePath();
                if (new File(webServerConfFile).exists()) {
                    final String webServerName = WebServerUtil.getWebServerName();
                    if ("apache".equalsIgnoreCase(webServerName)) {
                        copyFilesToLog(webServerConfFile, "httpd.conf");
                    }
                    else if ("nginx".equalsIgnoreCase(webServerName)) {
                        copyFilesToLog(webServerConfFile, "nginx.conf");
                    }
                }
                final HashMap<String, String> troubleshootMap = this.getHashMapToGenerateEmailContent(ServerTroubleshooterUtil.mailContent);
                this.alertCustomerThroughMail(troubleshootMap);
            }
        }
    }
    
    public HashMap<String, String> getHashMapToGenerateEmailContent(final String mailContent) {
        final HashMap<String, String> troubleshootMap = new HashMap<String, String>();
        troubleshootMap.put("mailContent", mailContent);
        return troubleshootMap;
    }
    
    public void showSolutionPage(final String failureCase) throws Exception {
        try {
            final Properties wsProps = new Properties();
            final String generalProperty = ServerTroubleshooterUtil.serverHome + File.separator + "conf" + File.separator + "general_properties.conf";
            ServerTroubleshooterUtil.logger.log(Level.INFO, "General property = " + generalProperty);
            getInstance();
            final String product = getProperties(generalProperty).getProperty("displayname");
            switch (failureCase) {
                case "software_or_patch_store_not_reachable":
                case "osd_repo_not_reachable": {
                    final Properties accessStatusProps = WebServerUtil.getRepositoriesAccessStatus();
                    WebServerUtil.createShareAccessPropsFile(accessStatusProps);
                    final Properties webServerProperties = WebServerUtil.getWebServerSettings();
                    if (WebServerUtil.getWebServerName().equals("apache")) {
                        WebServerUtil.generateApacheHttpdConfFilesWithDefaultLoc(accessStatusProps);
                        WebServerUtil.getApacheHttpdConfFilesPropertiesWithDefaultLoc(accessStatusProps);
                    }
                    else if (WebServerUtil.getWebServerName().equals("nginx")) {
                        ServerTroubleshooterUtil.logger.log(Level.INFO, "Nginx Troubleshooting");
                        final Properties locProps = NginxServerUtils.getNginxConfFilesPropertiesWithDefaultLoc(accessStatusProps);
                        ServerTroubleshooterUtil.logger.log(Level.INFO, "LocProps Nginx " + locProps);
                        NginxServerUtils.generateNginxConfFilesWithDefaultLoc(accessStatusProps, locProps);
                    }
                    WebServerUtil.storeProperWebServerSettings(webServerProperties);
                    invokeTroubleShooterWindow(wsProps);
                    break;
                }
                case "webserver_port_in_use": {
                    final Properties webServerProps = WebServerUtil.getWebServerSettings();
                    final String httpPort = webServerProps.getProperty("http.port");
                    final String httpsPort = webServerProps.getProperty("https.port");
                    final Properties portProps = new Properties();
                    final String usedPort = this.getUsedPort();
                    if (usedPort.contains(httpPort)) {
                        portProps.setProperty("http.port", httpPort);
                    }
                    if (usedPort.contains(httpsPort)) {
                        portProps.setProperty("https.port", httpsPort);
                    }
                    if (WebServerUtil.getWebServerName().equals("apache")) {
                        WebServerUtil.generateApacheHttpdAndHttpdSSlConfFileWithDefaultLoc(portProps);
                        WebServerUtil.startApacheInDifferentPort(System.getProperty("server.home"));
                    }
                    else if (WebServerUtil.getWebServerName().equals("nginx")) {}
                    invokeTroubleShooterWindow(portProps);
                    break;
                }
                case "pgsql_in_recovery_mode": {
                    invokeTroubleShooterWindow(wsProps);
                    break;
                }
                case "pgsql_fails_in_recovery_mode":
                case "arch_incompatible":
                case "mssql_invalid_login_credentials":
                case "mssql_transaction_log_size_exceed":
                case "mssql_connection_retry":
                case "mssql_connection_lost": {
                    ServerTroubleshooterUtil.logger.log(Level.INFO, "pageTitle = " + ServerTroubleshooterUtil.pageTitle);
                    ServerTroubleshooterUtil.logger.log(Level.INFO, "pageTitle = " + ServerTroubleshooterUtil.heading);
                    ServerTroubleshooterUtil.logger.log(Level.INFO, "pageTitle = " + ServerTroubleshooterUtil.unReachableMsg);
                    ServerTroubleshooterUtil.logger.log(Level.INFO, "pageTitle = " + ServerTroubleshooterUtil.solution);
                    ServerTroubleshooterUtil.logger.log(Level.INFO, "pageTitle = " + ServerTroubleshooterUtil.solutionDetails);
                    invokeTroubleShooterWindow(wsProps);
                    break;
                }
                default: {
                    if (ServerTroubleshooterUtil.isTroubleShootEnabled.equalsIgnoreCase("true")) {
                        invokeTroubleShooterWindow(wsProps);
                        break;
                    }
                    break;
                }
            }
        }
        catch (final Exception e) {
            ServerTroubleshooterUtil.logger.log(Level.SEVERE, "Exception while showing the server startup failure html(maintenace_mode) to customer.", e);
            throw e;
        }
    }
    
    private static void invokeTroubleShooterWindow(final Properties wsProps) {
        try {
            if (WebServerUtil.getWebServerName().equals("nginx")) {
                NginxServerUtils.generateNginxStandaloneConf(wsProps);
            }
            final boolean isStarted = WebServerUtil.startWebServer(ServerTroubleshooterUtil.serverHome);
            WebServerUtil.generateHtmlRedirectionFile(ServerTroubleshooterUtil.pageTitle, ServerTroubleshooterUtil.heading, ServerTroubleshooterUtil.unReachableMsg, ServerTroubleshooterUtil.solution, ServerTroubleshooterUtil.solutionDetails + ServerTroubleshooterUtil.recommendation, ServerTroubleshooterUtil.solutionHeading, ServerTroubleshooterUtil.contactSupportMsg);
            String pageUrlToOpen = "";
            if (!isStarted) {
                pageUrlToOpen = WebServerUtil.getServerHomeCanonicalPath() + File.separator + WebServerUtil.MAINTENANCE_MODE_FILE;
            }
            else {
                pageUrlToOpen = WebServerUtil.getServerProtocol() + "://" + WebServerUtil.getMachineName() + ":" + WebServerUtil.getAvailablePort();
            }
            if (DCStarter.getStartupStatusNotifier() != null && DCStarter.getStartupStatusNotifier().isStatusNotifierRunning()) {
                DCStarter.getStartupStatusNotifier().removeStatusNotifier(pageUrlToOpen);
            }
            else {
                WebServerUtil.openBrowserUsingDCWinutil(pageUrlToOpen);
            }
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Server startup failure due to :" + ServerTroubleshooterUtil.heading);
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Solution for starting the server :" + ServerTroubleshooterUtil.solution + " FailureCase_SolutionDetails:" + ServerTroubleshooterUtil.solutionDetails + ServerTroubleshooterUtil.recommendation);
        }
        catch (final Exception e) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Exception while invoking Trouble Shooter Window ", e);
        }
    }
    
    public void alertCustomerThroughMail(final HashMap<String, String> hashMap) throws Exception {
        if (ServerTroubleshooterUtil.isTroubleShootEnabled.equalsIgnoreCase("true")) {
            final String bodyContentXML = this.generateXMLFromHashMap(hashMap, "root", "troubleshoot");
            final String bodyContent = getHTMLContentFromXML(bodyContentXML);
            ServerTroubleshooterMailerUtil.sendMail(bodyContent, ServerTroubleshooterUtil.subject);
        }
    }
    
    public void isArchitectureIncompatible() throws Exception {
        Starter.LoadJars();
        this.getRootCauseSolutionForStartupFailure("arch_incompatible");
        ServerTroubleshooterUtil.logger.log(Level.INFO, "Product arch incompatible ...");
        ServerTroubleshooterUtil.logger.log(Level.INFO, ServerTroubleshooterUtil.pageTitle);
        ServerTroubleshooterUtil.logger.log(Level.INFO, ServerTroubleshooterUtil.heading);
        ServerTroubleshooterUtil.logger.log(Level.INFO, ServerTroubleshooterUtil.solution);
        ServerTroubleshooterUtil.logger.log(Level.INFO, ServerTroubleshooterUtil.solutionDetails);
        this.showSolutionPage("arch_incompatible");
        final HashMap<String, String> troubleshootMap = this.getHashMapToGenerateEmailContent(ServerTroubleshooterUtil.mailContent);
        this.alertCustomerThroughMail(troubleshootMap);
    }
    
    public static String getPropertyValueFromFile(final String fileName, final String property) {
        String value = null;
        try {
            final Properties props = getProperties(fileName);
            value = props.getProperty(property);
        }
        catch (final Exception ex) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Exception occurred while getting property value from file. Exception : ", ex);
        }
        return value;
    }
    
    public static void writeServiceRunningInfoIntoFile(final String binDir, final String serviceName, final String serviceStatusFile) {
        try {
            final String winUtil = binDir + File.separator + "dcwinutil.exe";
            final File file = new File(binDir);
            final List<String> command = new ArrayList<String>();
            command.add(winUtil);
            command.add("-serviceinfo");
            command.add(serviceName);
            command.add(serviceStatusFile);
            final ProcessBuilder processbuilder = new ProcessBuilder(command);
            processbuilder.directory(file);
            final Process process = processbuilder.start();
            ServerTroubleshooterUtil.logger.log(Level.INFO, "Command For Find service status : " + processbuilder.command());
            final int exitValue = process.waitFor();
            ServerTroubleshooterUtil.logger.log(Level.INFO, "EXIT VALUE :: {0}", exitValue);
        }
        catch (final Exception ex) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Caught exception while Writing " + serviceName + " service status into " + serviceStatusFile + " : ", ex);
            ex.printStackTrace();
        }
    }
    
    public String getServerRunningLocation(String location, final String stringAfterTrim) {
        String result = location;
        try {
            location = location.replaceAll("\"", "");
            location = location.replace("\\", "/");
            location = location.replaceAll(stringAfterTrim, "");
            location = location.replace("/", "\\");
            location = (result = location.replace("\"", ""));
            ServerTroubleshooterUtil.logger.log(Level.INFO, "Value of server Location from service_status.props : " + result);
        }
        catch (final Exception ex) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Caught exception while getting Server Running Location. Exception : ", ex);
        }
        return result;
    }
    
    public static Properties getProperties(final String confFileName) {
        if (new File(confFileName).getName().equals("general_properties.conf")) {
            try {
                return GeneralPropertiesLoader.getInstance().getProperties();
            }
            catch (final Exception e) {
                ServerTroubleshooterUtil.logger.log(Level.WARNING, "Exception in getGeneralProperties : ServerTroubleShooterUtil.java ", e);
            }
        }
        if (confFileName.contains("websettings.conf")) {
            return StartupUtil.getProperties(confFileName);
        }
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
            ServerTroubleshooterUtil.logger.log(Level.SEVERE, "Exception occurred while getting properties : ", ex);
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception ex2) {}
        }
        return props;
    }
    
    public static String getDBName() {
        String dbName = null;
        try {
            final String dataBaseParamsFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
            final String url = getPropertyValueFromFile(dataBaseParamsFile, "url");
            ServerTroubleshooterUtil.logger.log(Level.INFO, "URL to find DB name is : " + url + "\t from file: " + dataBaseParamsFile);
            if (url != null) {
                if (url.toLowerCase().contains("mysql")) {
                    dbName = "mysql";
                }
                else if (url.toLowerCase().contains("postgresql")) {
                    dbName = "pgsql";
                }
            }
            if (dbName == null && url.toLowerCase().contains("sqlserver")) {
                dbName = "mssql";
            }
        }
        catch (final Exception ex) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Caught exception while getting dbname...");
        }
        return dbName;
    }
    
    public static HashMap getDBPropertiesFromFile() {
        final HashMap hash = new HashMap();
        try {
            final String dataBaseParamsFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
            final Properties dbProps = getProperties(dataBaseParamsFile);
            if (dbProps != null) {
                final String connectionUrl = dbProps.getProperty("url");
                final String username = dbProps.getProperty("username");
                final String password = dbProps.getProperty("password");
                hash.put("dbURL", connectionUrl);
                final String dbName = getDBName();
                if (dbName == null) {
                    return null;
                }
                if ("mysql".equals(dbName) || "pgsql".equals(dbName)) {
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
                else if ("mssql".equals(dbName)) {
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
        }
        catch (final Exception ex) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Exception occurred while gettting db details from props");
        }
        return hash;
    }
    
    public static boolean isPortEngaged(final int portNum) {
        if (portNum < 0) {
            return false;
        }
        try {
            final Socket sock = new Socket((String)null, portNum);
            sock.close();
            return true;
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    public static boolean copyDebuggerLogs(final String debuggerLog) {
        boolean fileCopyStatus = false;
        try {
            final String logsFolder = System.getProperty("server.home") + File.separator + "logs" + File.separator + "DebugHelper-logs";
            fileCopyStatus = copyDirectory(new File(debuggerLog), new File(logsFolder));
            ServerTroubleshooterUtil.logger.log(Level.INFO, "Copy status of pglogs folder to logs folder status : " + copyDirectory(new File(debuggerLog), new File(logsFolder)));
        }
        catch (final Exception ex) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Unable to copy debugger logs into logs folder. Exception : ", ex);
        }
        return fileCopyStatus;
    }
    
    public static boolean copyFilesToLog(final String sourceFilePath, final String destinationFileName) {
        boolean isCopied = false;
        try {
            final String logsFolder = System.getProperty("server.home") + File.separator + "logs" + File.separator + "DebugHelper-logs";
            isCopied = copyFile(new File(sourceFilePath), new File(logsFolder + File.separator + destinationFileName));
            ServerTroubleshooterUtil.logger.log(Level.INFO, "Copy status of pglogs folder to logs folder status : " + copyFile(new File(sourceFilePath), new File(logsFolder + File.separator + destinationFileName)));
        }
        catch (final Exception e) {
            ServerTroubleshooterUtil.logger.log(Level.INFO, "Exception while copying the license files : ", e);
        }
        return isCopied;
    }
    
    public static String getEXENameFromPort(final int port) {
        ServerTroubleshooterUtil.logger.log(Level.INFO, " Port number : " + port);
        ServerTroubleshooterUtil.logger.log(Level.INFO, " Port number in webserver util: " + port);
        String processsID = null;
        String exeName = null;
        try {
            final String serverHome = WebServerUtil.getServerHomeCanonicalPath();
            final File filepath = new File(serverHome);
            final List<String> command = new ArrayList<String>();
            BufferedReader in = null;
            command.add("netstat");
            command.add("-anob");
            final ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(filepath);
            final Process process = processBuilder.start();
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String outputLine = null;
            String[] outputArray = null;
            final String portValue = ":" + port;
            if (in != null) {
                while ((outputLine = in.readLine()) != null) {
                    if (outputLine.contains(portValue)) {
                        ServerTroubleshooterUtil.logger.log(Level.INFO, "Output line = " + outputLine);
                        outputArray = outputLine.split("\\s+");
                        if (outputArray[2].contains(portValue)) {
                            ServerTroubleshooterUtil.logger.log(Level.INFO, "output array[2] = " + outputArray[2]);
                            processsID = outputArray[5];
                            break;
                        }
                        continue;
                    }
                }
                exeName = getExeNameFromPID(processsID);
            }
        }
        catch (final Exception ex) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Exception occurred while  getting exe name from : ", ex);
        }
        return exeName;
    }
    
    public static String getExeNameFromPID(final String pid) {
        ServerTroubleshooterUtil.logger.log(Level.INFO, "Finding the process name for the process id :" + pid);
        String exeName = null;
        try {
            final String serverHome = WebServerUtil.getServerHomeCanonicalPath();
            final File filepath = new File(serverHome);
            final List<String> command = new ArrayList<String>();
            BufferedReader input = null;
            command.add("tasklist.exe");
            final ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(filepath);
            final Process process = processBuilder.start();
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                if (line.contains(pid.trim())) {
                    final String[] array = line.split("\\s+");
                    if (!array[1].equals(pid.trim())) {
                        continue;
                    }
                    ServerTroubleshooterUtil.logger.log(Level.INFO, "Exename = " + array[0]);
                    exeName = array[0].trim();
                }
            }
            input.close();
        }
        catch (final Exception ex) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Exception occurred while  getting exe name from -- : ", ex);
        }
        return exeName;
    }
    
    public static boolean isServiceRunninginLocalSystemAccount() {
        final String serviceStatusFile = ServerTroubleshooterUtil.serverHome + File.separator + "logs" + File.separator + "service_status.props";
        final String binDir = ServerTroubleshooterUtil.serverHome + File.separator + "bin";
        final String serviceName = getServiceNameFromWrapper();
        writeServiceRunningInfoIntoFile(binDir, serviceName, serviceStatusFile);
        final String ServiceRunningAccount = getPropertyValueFromFile(serviceStatusFile, "ExistingStartName");
        return ServiceRunningAccount == null || ServiceRunningAccount.equalsIgnoreCase("LocalSystem");
    }
    
    public static boolean isPgCheckpointNotLocated() {
        final Boolean returns = null;
        try {
            final String pgLog = ServerTroubleshooterUtil.serverHome + File.separator + "pgsql" + File.separator + "data" + File.separator + "pg_log";
            final File pgLogFile = getLatestFileFromDir(new File(pgLog));
            ServerTroubleshooterUtil.logger.log(Level.INFO, "the latest pg log file is: " + pgLogFile);
            final BufferedReader in = new BufferedReader(new FileReader(pgLogFile));
            final String[] last7 = new String[7];
            int count = 0;
            while (in.ready()) {
                last7[count++ % 7] = in.readLine();
            }
            for (int i = 0; i < 7; ++i) {
                ServerTroubleshooterUtil.logger.log(Level.INFO, "line " + i + " is: " + last7[(i + count) % 7]);
                if (last7[1].split("  ")[1].contains("invalid primary checkpoint record")) {
                    return true;
                }
            }
            in.close();
        }
        catch (final Exception e) {
            ServerTroubleshooterUtil.logger.log(Level.INFO, "exception while checking the error in pgLogs " + e);
        }
        return false;
    }
    
    private static File getLatestFileFromDir(final File dirPath) {
        final File[] files = dirPath.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }
        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; ++i) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        return lastModifiedFile;
    }
    
    public static String getServiceNameFromWrapper() {
        String wrapperConfFile = ServerTroubleshooterUtil.serverHome + File.separator + "conf" + File.separator + "custom_wrapperservice.conf";
        if (!new File(wrapperConfFile).exists()) {
            wrapperConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "wrapper.conf";
        }
        final String serviceName = getPropertyValueFromFile(wrapperConfFile, "wrapper.name");
        if (serviceName == null || serviceName.trim().length() < 1) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Unable to get Service Name from wrapper.conf file.");
        }
        ServerTroubleshooterUtil.logger.log(Level.INFO, "dcServiceName in wrapper conf fle = " + serviceName);
        return serviceName;
    }
    
    public void serviceRunningInDifferentLocation() throws Exception {
        if (!this.serverLocation.equalsIgnoreCase("service not registered") && this.serverLocation != null) {
            ServerTroubleshooterUtil.logger.log(Level.INFO, "Apache and server running in different location");
            this.serverLocation = this.getServerRunningLocation(this.serverLocation, "/bin/wrapper.exe.*");
            ServerTroubleshooterUtil.logger.log(Level.INFO, "Trying to install and start the server from service running location -  " + this.serverLocation);
            this.installOrStartService("runOnceAsAdmin.bat", this.serverLocation);
            Thread.sleep(60000L);
            this.installOrStartService("DCService.bat -t", this.serverLocation);
        }
    }
    
    public void installOrStartService(final String batToBeExecuted, final String serviceRunningLocation) {
        final String binDir = serviceRunningLocation + File.separator + "bin";
        final File filepath = new File(binDir);
        final List<String> command = new ArrayList<String>();
        BufferedReader in = null;
        try {
            command.add("cmd.exe");
            command.add("/c");
            command.add(batToBeExecuted);
            final ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(filepath);
            ServerTroubleshooterUtil.logger.log(Level.INFO, "COMMAND : {0}" + processBuilder.command());
            final Process process = processBuilder.start();
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String outputLine = null;
            if (in != null) {
                while ((outputLine = in.readLine()) != null) {
                    ServerTroubleshooterUtil.logger.log(Level.INFO, outputLine);
                }
                in.close();
            }
            else {
                ServerTroubleshooterUtil.logger.log(Level.INFO, "Problem in executing the command " + batToBeExecuted);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final Exception ex2) {
                ex2.printStackTrace();
            }
        }
    }
    
    public Boolean isServiceRunninginDiffererntLocation() throws Exception {
        if (ServerTroubleshooterUtil.isTroubleShootEnabled.equalsIgnoreCase("true") && !this.serverLocation.equalsIgnoreCase("service not registered") && this.serverLocation != null) {
            this.serverLocation = this.getServerRunningLocation(this.serverLocation, "/bin/wrapper.exe.*");
            final String apacheServiceRunningLocation = this.getApacheServerRunningLocation();
            if (!apacheServiceRunningLocation.equalsIgnoreCase("apache service not registered") && apacheServiceRunningLocation != null) {
                final String apacheServerLocation = this.getServerRunningLocation(apacheServiceRunningLocation, "/bin/dcserverhttpd.exe.*");
                if (!apacheServerLocation.equalsIgnoreCase(this.serverLocation + File.separator + "apache")) {
                    ServerTroubleshooterUtil.logger.log(Level.INFO, "Services running in different location");
                    ServerTroubleshooterUtil.logger.log(Level.INFO, "Server running in " + this.serverLocation);
                    ServerTroubleshooterUtil.logger.log(Level.INFO, "Apache running in " + apacheServerLocation);
                    return true;
                }
            }
        }
        return false;
    }
    
    public String getServerRunningLocation() {
        try {
            final String serviceStatusFile = ServerTroubleshooterUtil.serverHome + File.separator + "logs" + File.separator + "service_status.props";
            final String binDir = ServerTroubleshooterUtil.serverHome + File.separator + "bin";
            final String dcServiceName = getServiceNameFromWrapper();
            writeServiceRunningInfoIntoFile(binDir, dcServiceName, serviceStatusFile);
            final String dcServiceRunningLocation = getPropertyValueFromFile(serviceStatusFile, "ServicePath");
            final String dcServiceStartupType = getPropertyValueFromFile(serviceStatusFile, "StartupType");
            if (dcServiceRunningLocation == null || dcServiceRunningLocation.trim().length() == 0 || dcServiceRunningLocation == "--" || dcServiceRunningLocation.length() < 3 || Integer.parseInt(dcServiceStartupType) == -1) {
                ServerTroubleshooterUtil.logger.log(Level.WARNING, " {0} Was not registered in customer setup. ", dcServiceName);
                return "service not registered";
            }
            return dcServiceRunningLocation;
        }
        catch (final Exception e) {
            ServerTroubleshooterUtil.logger.log(Level.SEVERE, "Exception while getting server running location..", e);
            return null;
        }
    }
    
    public String getApacheServerRunningLocation() {
        try {
            final String webSettingsConfFile = ServerTroubleshooterUtil.serverHome + File.separator + "conf" + File.separator + "websettings.conf";
            final String apacheServiceDisplayName = getPropertyValueFromFile(webSettingsConfFile, "apache.service.name");
            String apacheServiceName = null;
            if (apacheServiceDisplayName == null || apacheServiceDisplayName.trim().length() < 1) {
                ServerTroubleshooterUtil.logger.log(Level.WARNING, "Unable to get Apache Service Name from websettings.conf file.");
                return null;
            }
            apacheServiceName = apacheServiceDisplayName.replaceAll(" ", "");
            final String serviceStatusFile = ServerTroubleshooterUtil.serverHome + File.separator + "logs" + File.separator + "service_status.props";
            final String binDir = ServerTroubleshooterUtil.serverHome + File.separator + "bin";
            writeServiceRunningInfoIntoFile(binDir, apacheServiceName, serviceStatusFile);
            final String apacheServiceRunningLocation = getPropertyValueFromFile(serviceStatusFile, "ServicePath");
            final String apacheServiceStartupType = getPropertyValueFromFile(serviceStatusFile, "StartupType");
            if (apacheServiceRunningLocation == null || apacheServiceRunningLocation.trim().length() == 0 || apacheServiceRunningLocation == "--" || apacheServiceRunningLocation.length() < 3 || Integer.parseInt(apacheServiceStartupType) == -1) {
                ServerTroubleshooterUtil.logger.log(Level.WARNING, " {0} Was not registered in customer setup. ", apacheServiceName);
                return "apache service not registered";
            }
            return apacheServiceRunningLocation;
        }
        catch (final Exception e) {
            ServerTroubleshooterUtil.logger.log(Level.SEVERE, "Exception while getting apache running location..", e);
            return null;
        }
    }
    
    public void ppmLockFileFound() throws Exception {
        final String ppmLockFileNameFull = ServerTroubleshooterUtil.serverHome + File.separator + "bin" + File.separator + "ppm.lock";
        copyFilesToLog(ppmLockFileNameFull, "ppm.lock");
        this.getRootCauseSolutionForStartupFailure("ppm_lock");
        final HashMap<String, String> troubleshootMap = this.getHashMapToGenerateEmailContent(ServerTroubleshooterUtil.mailContent);
        this.alertCustomerThroughMail(troubleshootMap);
        this.showSolutionPage("ppm_lock");
    }
    
    public void buildNumberInCompatibilityFound() throws Exception {
        copyDebuggerLogs(ServerTroubleshooterUtil.serverHome + File.separator + "conf");
        final String serverLockFileNameFull = ServerTroubleshooterUtil.serverHome + File.separator + "bin" + File.separator + "server.lock";
        if (new File(serverLockFileNameFull).exists()) {
            copyFilesToLog(serverLockFileNameFull, "server.lock");
        }
        this.getRootCauseSolutionForStartupFailure("build_number_incompatible");
        final HashMap<String, String> troubleshootMap = this.getHashMapToGenerateEmailContent(ServerTroubleshooterUtil.mailContent);
        this.alertCustomerThroughMail(troubleshootMap);
        this.showSolutionPage("build_number_incompatible");
    }
    
    public void revertLockFileFound() throws Exception {
        final String revertLock = ServerTroubleshooterUtil.serverHome + File.separator + "bin" + File.separator + "revert.lock";
        if (new File(revertLock).exists()) {
            copyFilesToLog(revertLock, "revert.lock");
            this.getRootCauseSolutionForStartupFailure("revert_lock");
            final HashMap<String, String> troubleshootMap = this.getHashMapToGenerateEmailContent(ServerTroubleshooterUtil.mailContent);
            this.alertCustomerThroughMail(troubleshootMap);
            this.showSolutionPage("revert_lock");
        }
    }
    
    public void migrationLockFileFound() throws Exception {
        final String migrateLock = ServerTroubleshooterUtil.serverHome + File.separator + "bin" + File.separator + "migration.lock";
        if (new File(migrateLock).exists()) {
            this.getRootCauseSolutionForStartupFailure("migration_lock");
            final HashMap<String, String> troubleshootMap = this.getHashMapToGenerateEmailContent(ServerTroubleshooterUtil.mailContent);
            this.alertCustomerThroughMail(troubleshootMap);
            this.showSolutionPage("migration_lock");
        }
    }
    
    public static boolean copyDirectory(final File sourceLocation, final File targetLocation) throws Exception {
        try {
            if (sourceLocation.isDirectory()) {
                if (!targetLocation.exists()) {
                    targetLocation.mkdirs();
                }
                final String[] children = sourceLocation.list();
                for (int i = 0; i < children.length; ++i) {
                    if (!copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]))) {
                        return false;
                    }
                }
                return true;
            }
            return copyFile(sourceLocation, targetLocation);
        }
        catch (final Exception e) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Failed to copy folder " + sourceLocation + " to location " + targetLocation, e);
            return false;
        }
    }
    
    public static boolean copyFile(final File srcFile, final File destFile) throws Exception {
        boolean retType = false;
        InputStream inFile = null;
        OutputStream outFile = null;
        ServerTroubleshooterUtil.logger.log(Level.FINE, "Going to copy file.......");
        ServerTroubleshooterUtil.logger.log(Level.FINE, "Going to copy file......." + srcFile.getAbsolutePath() + " : " + destFile.getAbsolutePath());
        try {
            final String parentLoc = destFile.getParent();
            if (parentLoc != null && !parentLoc.equals("") && !new File(parentLoc).exists()) {
                new File(parentLoc).mkdirs();
            }
            inFile = new FileInputStream(srcFile);
            outFile = new FileOutputStream(destFile);
            final byte[] buf = new byte[1024];
            int len;
            while ((len = inFile.read(buf)) > 0) {
                outFile.write(buf, 0, len);
            }
            retType = true;
        }
        catch (final Exception e) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Exception while copying file.......", e);
        }
        finally {
            if (inFile != null) {
                inFile.close();
            }
            if (outFile != null) {
                outFile.close();
            }
        }
        return retType;
    }
    
    public void errorInSSLCertificate() throws Exception {
        final String certificateFile = WebServerUtil.getServerCertificateFilePath();
        final String certificateKeyFile = WebServerUtil.getServerPrivateKeyFilePath();
        if (!new File(certificateFile).exists() || !new File(certificateKeyFile).exists()) {
            this.getRootCauseSolutionForStartupFailure("ssl_certificate_missing");
            final HashMap<String, String> troubleshootMap = this.getHashMapToGenerateEmailContent(ServerTroubleshooterUtil.mailContent);
            this.alertCustomerThroughMail(troubleshootMap);
            this.showSolutionPage("ssl_certificate_missing");
        }
    }
    
    public void dbMismatchWithFileSystem() throws Exception {
        copyDebuggerLogs(ServerTroubleshooterUtil.serverHome + File.separator + "conf");
        final String serverLockFileNameFull = ServerTroubleshooterUtil.serverHome + File.separator + "bin" + File.separator + "filesystem.lock";
        if (new File(serverLockFileNameFull).exists()) {
            copyFilesToLog(serverLockFileNameFull, "filesystem.lock");
        }
        this.getRootCauseSolutionForStartupFailure("file_db_mismatch");
        final HashMap<String, String> troubleshootMap = this.getHashMapToGenerateEmailContent(ServerTroubleshooterUtil.mailContent);
        this.alertCustomerThroughMail(troubleshootMap);
        this.showSolutionPage("file_db_mismatch");
    }
    
    public void sslCertificateKeyMismatch() throws Exception {
        this.getRootCauseSolutionForStartupFailure("ssl_certificate_key_mismatch");
        final HashMap<String, String> troubleshootMap = this.getHashMapToGenerateEmailContent(ServerTroubleshooterUtil.mailContent);
        this.alertCustomerThroughMail(troubleshootMap);
        this.showSolutionPage("ssl_certificate_key_mismatch");
    }
    
    public static ServerTroubleshooterUtil getInstance() {
        if (ServerTroubleshooterUtil.serverTroubleshooterUtil == null) {
            ServerTroubleshooterUtil.serverTroubleshooterUtil = new ServerTroubleshooterUtil();
        }
        return ServerTroubleshooterUtil.serverTroubleshooterUtil;
    }
    
    public String getValue(final String value, final Object[] params) {
        if (value != null) {
            final MessageFormat messageFormat = new MessageFormat(value);
            return messageFormat.format(params);
        }
        return value;
    }
    
    public Boolean isDiskSpaceTooLow() throws Exception {
        if (ServerTroubleshooterUtil.isTroubleShootEnabled.equalsIgnoreCase("true")) {
            try {
                long minimumRequiredFreeSpace = 524288000L;
                final String systemPropertiesPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "system_properties.conf";
                final Properties properties = getProperties(systemPropertiesPath);
                if (properties.getProperty("diskcheck.min") != null && properties.getProperty("diskcheck.enable").equalsIgnoreCase("true")) {
                    final Long minDiskSpace = Long.valueOf(properties.getProperty("diskcheck.min"));
                    final String byteType = properties.getProperty("diskcheck.bytetype");
                    if (byteType.trim().equalsIgnoreCase("gb")) {
                        minimumRequiredFreeSpace = minDiskSpace * 1024L * 1024L * 1024L;
                    }
                    else if (byteType.trim().equalsIgnoreCase("mb")) {
                        minimumRequiredFreeSpace = minDiskSpace * 1024L * 1024L;
                    }
                }
                final File file = new File(ServerTroubleshooterUtil.serverHome);
                final long freeSpaceInBytes = file.getFreeSpace();
                ServerTroubleshooterUtil.logger.log(Level.INFO, "Free space available = " + freeSpaceInBytes / 1024L + " MB." + "Free space required = " + minimumRequiredFreeSpace / 1024L + " MB.");
                if (freeSpaceInBytes < minimumRequiredFreeSpace) {
                    return true;
                }
            }
            catch (final Exception e) {
                ServerTroubleshooterUtil.logger.log(Level.SEVERE, "Exception occurred while checking disk space availability..", e);
            }
        }
        return false;
    }
    
    public void diskSpaceTooLow(final boolean isShutdownNeeded) throws Exception {
        Starter.LoadJars();
        this.getRootCauseSolutionForStartupFailure("disk_space_low");
        final HashMap<String, String> troubleshootMap = this.getHashMapToGenerateEmailContent(ServerTroubleshooterUtil.mailContent);
        this.showSolutionPage("disk_space_low");
        this.alertCustomerThroughMail(troubleshootMap);
        if (isShutdownNeeded) {
            StartupUtil.triggerServerShutdown("Disk space is too low.");
        }
    }
    
    public static String getHTMLContentFromXML(final String xmlContent) {
        String htmlContent = "";
        final String xslFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "DesktopCentral" + File.separator + "xsl" + File.separator + "troubleshootReport.xsl";
        ServerTroubleshooterUtil.logger.log(Level.INFO, "Inside getHTMLContentFromXML");
        InputStream xslInputStream = null;
        InputStream xmlInputStream = null;
        ByteArrayOutputStream htmloutputStream = null;
        ByteArrayOutputStream xmlOutputStream = null;
        try {
            htmloutputStream = new ByteArrayOutputStream();
            xmlOutputStream = new ByteArrayOutputStream();
            xslInputStream = new FileInputStream(xslFile);
            xmlInputStream = new ByteArrayInputStream(xmlContent.getBytes("UTF-8"));
            final Transformer transformer = XMLUtils.getTransformerInstance(xslInputStream);
            transformer.transform(new StreamSource(xmlInputStream), new StreamResult(htmloutputStream));
            htmlContent = new String(htmloutputStream.toByteArray(), "UTF-8");
        }
        catch (final Exception e) {
            e.printStackTrace();
            if (xslInputStream != null) {
                try {
                    xslInputStream.close();
                }
                catch (final IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (xmlInputStream != null) {
                try {
                    xmlInputStream.close();
                }
                catch (final IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (xmlOutputStream != null) {
                try {
                    xmlOutputStream.close();
                }
                catch (final IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (htmloutputStream != null) {
                try {
                    htmloutputStream.close();
                }
                catch (final IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        finally {
            if (xslInputStream != null) {
                try {
                    xslInputStream.close();
                }
                catch (final IOException ex2) {
                    ex2.printStackTrace();
                }
            }
            if (xmlInputStream != null) {
                try {
                    xmlInputStream.close();
                }
                catch (final IOException ex2) {
                    ex2.printStackTrace();
                }
            }
            if (xmlOutputStream != null) {
                try {
                    xmlOutputStream.close();
                }
                catch (final IOException ex2) {
                    ex2.printStackTrace();
                }
            }
            if (htmloutputStream != null) {
                try {
                    htmloutputStream.close();
                }
                catch (final IOException ex2) {
                    ex2.printStackTrace();
                }
            }
        }
        htmlContent = htmlContent.replaceAll("&lt;", "<");
        htmlContent = htmlContent.replaceAll("&gt;", ">");
        return htmlContent;
    }
    
    public String generateXMLFromHashMap(final HashMap hashMap, final String root, final String rootElementValue) throws Exception {
        try {
            final String generalProperty = ServerTroubleshooterUtil.serverHome + File.separator + "conf" + File.separator + "general_properties.conf";
            ServerTroubleshooterUtil.logger.log(Level.INFO, "General property = " + generalProperty);
            getInstance();
            final String product = getProperties(generalProperty).getProperty("displayname");
            hashMap.put("Logurl", ServerTroubleshooterUtil.contactSupportMsg);
            hashMap.put("MachineName", InetAddress.getLocalHost().getHostName());
            final String producturl = "<a href = '" + ServerTroubleshooterUtil.productUrl + "'>" + product + " </a>";
            hashMap.put("Producturl", producturl);
            if (!this.serverLocation.equalsIgnoreCase("service not registered") && this.serverLocation != null) {
                this.serverLocation = this.getServerRunningLocation(this.serverLocation, "/bin/wrapper.exe.*");
                final String logsFolder = this.serverLocation + File.separator + "logs";
                hashMap.put("LogFolderLocation", logsFolder);
            }
            if (String.valueOf(hashMap.get(ServerTroubleshooterUtil.solution)).equals("Contact " + product + " support with <a target=\"_blank\" href=\"" + ServerTroubleshooterUtil.uploadLogsUrl + "\">Logs.</a>")) {
                hashMap.put("Logurl", "");
            }
            ServerTroubleshooterUtil.logger.log(Level.INFO, "Inside generateXMLFromHashMap");
            final DocumentBuilder docBuilder = XMLUtils.getDocumentBuilderInstance();
            final Document document = docBuilder.newDocument();
            final Element rootElement = document.createElement(root);
            document.appendChild(rootElement);
            final Element element = document.createElement(rootElementValue);
            rootElement.appendChild(element);
            for (final Object key : hashMap.keySet()) {
                final Attr attribute = document.createAttribute((String)key);
                attribute.setValue(hashMap.get(key));
                element.setAttributeNode(attribute);
            }
            final DOMSource domSource = new DOMSource(document);
            final StringWriter writer = new StringWriter();
            final StreamResult result = new StreamResult(writer);
            final Transformer transformer = XMLUtils.getTransformerInstance();
            transformer.transform(domSource, result);
            return writer.toString();
        }
        catch (final TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public void postgresDBPortInUse() throws Exception {
        this.getRootCauseSolutionForStartupFailure("db_port_in_use");
        this.showSolutionPage("db_port_in_use");
    }
    
    public void serviceRunningByUserAccount() throws Exception {
        this.getRootCauseSolutionForStartupFailure("service_running_by_user_account");
        this.showSolutionPage("service_running_by_user_account");
    }
    
    public void copyPgLogs(final boolean isPgLogsCopied) throws Exception {
        if (isPgLogsCopied) {
            this.getRootCauseSolutionForStartupFailure("pgsql_startup_failure_unknown_case");
            final HashMap<String, String> troubleshootMap = this.getHashMapToGenerateEmailContent(ServerTroubleshooterUtil.mailContent);
            this.alertCustomerThroughMail(troubleshootMap);
            this.showSolutionPage("pgsql_startup_failure_unknown_case");
        }
        else {
            this.getRootCauseSolutionForStartupFailure("pgsql_startup_failure_unknown_case_copy_failed");
            final HashMap<String, String> troubleshootMap = this.getHashMapToGenerateEmailContent(ServerTroubleshooterUtil.mailContent);
            this.alertCustomerThroughMail(troubleshootMap);
            this.showSolutionPage("pgsql_startup_failure_unknown_case_copy_failed");
        }
    }
    
    public void sqlAuthenticationFailure() throws Exception {
        this.getRootCauseSolutionForStartupFailure("mssql_invalid_login_credentials");
        this.showSolutionPage("mssql_invalid_login_credentials");
        final HashMap<String, String> troubleshootMap = this.getHashMapToGenerateEmailContent(ServerTroubleshooterUtil.mailContent);
        this.alertCustomerThroughMail(troubleshootMap);
    }
    
    public void sqlTransactionLogSizeExceed() throws Exception {
        this.getRootCauseSolutionForStartupFailure("mssql_transaction_log_size_exceed");
        this.showSolutionPage("mssql_transaction_log_size_exceed");
        final HashMap<String, String> troubleshootMap = this.getHashMapToGenerateEmailContent(ServerTroubleshooterUtil.mailContent);
        this.alertCustomerThroughMail(troubleshootMap);
    }
    
    public void getRootCauseSolutionForStartupFailure(final String failureCase) throws Exception {
        final String generalProperty = ServerTroubleshooterUtil.serverHome + File.separator + "conf" + File.separator + "general_properties.conf";
        ServerTroubleshooterUtil.logger.log(Level.INFO, "General property = " + generalProperty);
        getInstance();
        final String product = getProperties(generalProperty).getProperty("displayname");
        ServerTroubleshooterUtil.pageTitle = "Unable to start " + product + " server";
        String supportID = "";
        if (product.equalsIgnoreCase("Mobile Device Manager Plus")) {
            supportID = "mdm-support";
        }
        else if (product.equalsIgnoreCase("OS Deployer")) {
            supportID = "osdeployer-support";
        }
        else {
            supportID = "desktopcentral-support";
        }
        getInstance();
        final String genSolutionHeading = getString("troubleshooter_solution_header", null);
        if (genSolutionHeading.equals("")) {
            ServerTroubleshooterUtil.solutionHeading = "";
        }
        else {
            ServerTroubleshooterUtil.solutionHeading = "Solution";
        }
        getInstance();
        final String genHeading = getString(failureCase, null);
        if (genHeading.equals("")) {
            ServerTroubleshooterUtil.heading = "";
        }
        getInstance();
        final String genUnReachableMsg = getString(failureCase + "_unreachableMsg", null);
        if (genUnReachableMsg.equals("")) {
            ServerTroubleshooterUtil.unReachableMsg = "";
        }
        getInstance();
        final String genSolution = getString(failureCase + "_solution", null);
        if (genSolution.equals("")) {
            ServerTroubleshooterUtil.solution = "";
        }
        getInstance();
        final String genSolutionDetails = getString(failureCase + "_solutionDetail", null);
        if (genSolutionDetails.equals("")) {
            ServerTroubleshooterUtil.solutionDetails = "";
        }
        getInstance();
        final String genRecommendation = getString(failureCase + "_recommendation", null);
        if (genRecommendation.equals("")) {
            ServerTroubleshooterUtil.recommendation = "";
        }
        getInstance();
        final String genMailContent = getString(failureCase + "_mailContent", null);
        if (genMailContent.equals("")) {
            ServerTroubleshooterUtil.mailContent = "";
        }
        getInstance();
        final String genSubject = getString(failureCase + "_mailSubject", null);
        if (genSubject.equals("")) {
            ServerTroubleshooterUtil.subject = "";
        }
        final HashMap dbProps = getDBPropertiesFromFile();
        switch (failureCase) {
            case "disk_space_low": {
                if (!this.serverLocation.equalsIgnoreCase("service not registered") && this.serverLocation != null) {
                    this.serverLocation = this.getServerRunningLocation(this.serverLocation, "/bin/wrapper.exe.*");
                }
                String minRequiredDiskSpace = "500MB";
                String requiredDiskSpaceinGB = "3GB";
                final String systemPropertiesPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "system_properties.conf";
                final Properties properties = getProperties(systemPropertiesPath);
                if (properties.getProperty("diskcheck.max") != null && properties.getProperty("diskcheck.enable").equalsIgnoreCase("true")) {
                    if (properties.getProperty("diskcheck.bytetype").equalsIgnoreCase("mb")) {
                        minRequiredDiskSpace = properties.getProperty("diskcheck.min") + "MB";
                    }
                    if (properties.getProperty("diskcheck.bytetype").equalsIgnoreCase("gb")) {
                        minRequiredDiskSpace = properties.getProperty("diskcheck.min") + "GB";
                    }
                    String requiredDiskSpace = properties.getProperty("diskcheck.max");
                    if (properties.getProperty("diskcheck.bytetype").equalsIgnoreCase("mb")) {
                        requiredDiskSpace = String.valueOf(Long.valueOf(properties.getProperty("diskcheck.max")) / 1000L);
                    }
                    requiredDiskSpaceinGB = requiredDiskSpace + "GB";
                }
                final String cleanupUrl = ServerTroubleshooterUtil.productUrl + "/removing-unwanted-files-from-dc-how-to.html";
                final String serverMaintenanceUrl = ServerTroubleshooterUtil.productUrl + "/server-maintenance.html";
                final String binFolderPath = WebServerUtil.getServerHomeCanonicalPath() + File.separator + "bin";
                ServerTroubleshooterUtil.heading = genHeading;
                ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { requiredDiskSpaceinGB, this.serverLocation });
                ServerTroubleshooterUtil.solutionDetails = getInstance().getValue(genSolutionDetails, new Object[] { binFolderPath, product, serverMaintenanceUrl });
                ServerTroubleshooterUtil.mailContent = getInstance().getValue(genMailContent, new Object[] { product, minRequiredDiskSpace });
                ServerTroubleshooterUtil.subject = getInstance().getValue(genSubject, new Object[] { product });
                break;
            }
            case "arch_incompatible": {
                ServerTroubleshooterUtil.heading = genHeading;
                ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { product, ServerTroubleshooterUtil.uploadLogsUrl });
                ServerTroubleshooterUtil.mailContent = getInstance().getValue(genMailContent, new Object[] { product, supportID });
                ServerTroubleshooterUtil.subject = getInstance().getValue(genSubject, new Object[] { product });
                break;
            }
            case "db_port_in_use": {
                final String dbPort = dbProps.get("PORT").toString();
                final String process = getEXENameFromPort(Integer.parseInt(dbPort));
                ServerTroubleshooterUtil.heading = getInstance().getValue(genHeading, new Object[] { product });
                ServerTroubleshooterUtil.unReachableMsg = getInstance().getValue(genUnReachableMsg, new Object[] { dbPort, process });
                ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { product });
                break;
            }
            case "service_running_by_user_account": {
                ServerTroubleshooterUtil.heading = getInstance().getValue(genHeading, new Object[] { product });
                ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { product });
                break;
            }
            case "pgsql_startup_failure_unknown_case": {
                ServerTroubleshooterUtil.heading = genHeading;
                ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { product, ServerTroubleshooterUtil.uploadLogsUrl });
                ServerTroubleshooterUtil.mailContent = getInstance().getValue(genMailContent, new Object[] { product, supportID });
                ServerTroubleshooterUtil.subject = getInstance().getValue(genSubject, new Object[] { product });
                break;
            }
            case "pgsql_startup_failure_unknown_case_copy_failed": {
                ServerTroubleshooterUtil.heading = genHeading;
                final String pgLog = ServerTroubleshooterUtil.serverHome + File.separator + "pgsql" + File.separator + "data" + File.separator + "pg_log";
                ServerTroubleshooterUtil.solutionDetails = getInstance().getValue(genSolutionDetails, new Object[] { pgLog, ServerTroubleshooterUtil.serverHome + File.separator + "logs" });
                ServerTroubleshooterUtil.mailContent = getInstance().getValue(genMailContent, new Object[] { product, supportID });
                ServerTroubleshooterUtil.subject = getInstance().getValue(genSubject, new Object[] { product });
                break;
            }
            case "ppm_lock": {
                ServerTroubleshooterUtil.heading = genHeading;
                ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { product, ServerTroubleshooterUtil.uploadLogsUrl });
                ServerTroubleshooterUtil.mailContent = getInstance().getValue(genMailContent, new Object[] { product, supportID });
                ServerTroubleshooterUtil.subject = getInstance().getValue(genSubject, new Object[] { product, supportID });
                break;
            }
            case "build_number_incompatible": {
                ServerTroubleshooterUtil.heading = getInstance().getValue(genHeading, new Object[] { product });
                ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { product, ServerTroubleshooterUtil.uploadLogsUrl });
                ServerTroubleshooterUtil.mailContent = getInstance().getValue(genMailContent, new Object[] { product, supportID });
                ServerTroubleshooterUtil.subject = getInstance().getValue(genSubject, new Object[] { product });
                break;
            }
            case "revert_lock": {
                ServerTroubleshooterUtil.heading = genHeading;
                ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { product, ServerTroubleshooterUtil.uploadLogsUrl });
                ServerTroubleshooterUtil.mailContent = getInstance().getValue(genMailContent, new Object[] { product, supportID });
                ServerTroubleshooterUtil.subject = getInstance().getValue(genSubject, new Object[] { product });
                break;
            }
            case "migration_lock": {
                ServerTroubleshooterUtil.heading = genHeading;
                ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { product, ServerTroubleshooterUtil.uploadLogsUrl });
                ServerTroubleshooterUtil.mailContent = getInstance().getValue(genMailContent, new Object[] { product, supportID });
                ServerTroubleshooterUtil.subject = getInstance().getValue(genSubject, new Object[] { product });
                break;
            }
            case "file_db_mismatch": {
                ServerTroubleshooterUtil.heading = genHeading;
                final String serverBinPath = WebServerUtil.getServerHomeCanonicalPath() + File.separator + "bin";
                final Properties fileSystemProps = getProperties(ServerTroubleshooterUtil.serverHome + File.separator + "bin" + File.separator + "filesystem.lock");
                final String lastBackupFilename = fileSystemProps.getProperty("LastSuccessfullScheduledBackup");
                final String BackupLocation = fileSystemProps.getProperty("ScheduledBackupLocation");
                final String restoreTime = fileSystemProps.getProperty("LastBakRestoreTime");
                final String fullBackupPath = BackupLocation + File.separator + lastBackupFilename;
                if (restoreTime != null) {
                    ServerTroubleshooterUtil.unReachableMsg = getInstance().getValue(genSolutionDetails, new Object[] { product, restoreTime });
                }
                else {
                    ServerTroubleshooterUtil.unReachableMsg = getInstance().getValue(genUnReachableMsg, new Object[] { product });
                }
                ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { product, lastBackupFilename, serverBinPath, fullBackupPath, product, product });
                ServerTroubleshooterUtil.solutionDetails = "";
                ServerTroubleshooterUtil.mailContent = getInstance().getValue(genSolution, new Object[] { lastBackupFilename, BackupLocation });
                ServerTroubleshooterUtil.subject = getInstance().getValue(genSubject, new Object[] { product });
                break;
            }
            case "ssl_certificate_missing": {
                ServerTroubleshooterUtil.heading = genHeading;
                ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { product, ServerTroubleshooterUtil.uploadLogsUrl });
                ServerTroubleshooterUtil.mailContent = getInstance().getValue(genMailContent, new Object[] { product, supportID });
                ServerTroubleshooterUtil.subject = getInstance().getValue(genSubject, new Object[] { product });
                break;
            }
            case "remote_pg_not_compatible": {
                ServerTroubleshooterUtil.heading = genHeading;
                ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { product });
                ServerTroubleshooterUtil.mailContent = getInstance().getValue(genMailContent, new Object[] { product });
                ServerTroubleshooterUtil.subject = getInstance().getValue(genSubject, new Object[] { product });
                break;
            }
            case "ssl_certificate_key_mismatch": {
                ServerTroubleshooterUtil.heading = genHeading;
                ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { product, ServerTroubleshooterUtil.uploadLogsUrl });
                ServerTroubleshooterUtil.mailContent = getInstance().getValue(genMailContent, new Object[] { product, supportID });
                ServerTroubleshooterUtil.subject = getInstance().getValue(genSubject, new Object[] { product });
                break;
            }
            case "webserver_port_in_use": {
                final Properties webServerProps = WebServerUtil.getWebServerSettings();
                final String httpPort = webServerProps.getProperty("http.port");
                final String httpsPort = webServerProps.getProperty("https.port");
                final String usedPort = this.getUsedPort();
                ServerTroubleshooterUtil.heading = getInstance().getValue(genHeading, new Object[] { usedPort, product });
                final String exeName = getEXENameFromPort(Integer.parseInt(usedPort));
                ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { usedPort, exeName });
                ServerTroubleshooterUtil.mailContent = getInstance().getValue(genMailContent, new Object[] { usedPort, exeName, product });
                ServerTroubleshooterUtil.subject = getInstance().getValue(genSubject, new Object[] { product });
                break;
            }
            case "software_or_patch_store_not_reachable": {
                final Properties wsProps = WebServerUtil.getWebServerSettings();
                String storePath = wsProps.getProperty("store.loc");
                storePath = storePath.replace("/", "\\");
                String softwarePath = wsProps.getProperty("swrepository.loc");
                softwarePath = softwarePath.replace("/", "\\");
                ServerTroubleshooterUtil.heading = product + " is unable to reach the Patch Store / Software Repository";
                ServerTroubleshooterUtil.unReachableMsg = getInstance().getValue(genUnReachableMsg, new Object[] { "Patch Store", storePath, product });
                ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { "Patch Store", storePath }) + getInstance().getValue("software_or_patch_store_not_reachable_solution", new Object[] { "Patch Store", softwarePath });
                final String binPath = WebServerUtil.getServerHomeCanonicalPath() + File.separator + "bin";
                ServerTroubleshooterUtil.solutionDetails = getInstance().getValue(genSolutionDetails, new Object[] { binPath, product });
                ServerTroubleshooterUtil.subject = getInstance().getValue(genSubject, new Object[] { product });
                ServerTroubleshooterUtil.mailContent = getInstance().getValue(genMailContent, new Object[] { "Software/Patch", softwarePath + "/" + storePath, product });
                break;
            }
            case "patch_not_reachable": {
                final Properties webProps = WebServerUtil.getWebServerSettings();
                String storeLocation = webProps.getProperty("store.loc");
                storeLocation = storeLocation.replace("/", "\\");
                final ServerTroubleshooterUtil instance = getInstance();
                getInstance();
                ServerTroubleshooterUtil.heading = instance.getValue(getString("software_or_patch_store_not_reachable", null), new Object[] { "Patch Store Location", product });
                final ServerTroubleshooterUtil instance2 = getInstance();
                getInstance();
                ServerTroubleshooterUtil.unReachableMsg = instance2.getValue(getString("software_or_patch_store_not_reachable_unreachableMsg", null), new Object[] { "Patch Store", storeLocation });
                final ServerTroubleshooterUtil instance3 = getInstance();
                getInstance();
                ServerTroubleshooterUtil.solution = instance3.getValue(getString("software_or_patch_store_not_reachable_solution", null), new Object[] { "Patch Store", storeLocation });
                final String binLocation = WebServerUtil.getServerHomeCanonicalPath() + File.separator + "bin";
                final ServerTroubleshooterUtil instance4 = getInstance();
                getInstance();
                ServerTroubleshooterUtil.solutionDetails = instance4.getValue(getString("software_or_patch_store_not_reachable_solutionDetail", null), new Object[] { binLocation, product });
                final ServerTroubleshooterUtil instance5 = getInstance();
                getInstance();
                ServerTroubleshooterUtil.mailContent = instance5.getValue(getString("software_or_patch_store_not_reachable_mailContent", null), new Object[] { "Patch Store", storeLocation, product });
                final ServerTroubleshooterUtil instance6 = getInstance();
                getInstance();
                ServerTroubleshooterUtil.subject = instance6.getValue(getString("software_or_patch_store_not_reachable_mailSubject", null), new Object[] { product });
                break;
            }
            case "software_not_reachable": {
                final Properties wsProp = WebServerUtil.getWebServerSettings();
                String swPath = wsProp.getProperty("swrepository.loc");
                swPath = swPath.replace("/", "\\");
                final ServerTroubleshooterUtil instance7 = getInstance();
                getInstance();
                ServerTroubleshooterUtil.heading = instance7.getValue(getString("software_or_patch_store_not_reachable", null), new Object[] { "Software Repository Location", product });
                final ServerTroubleshooterUtil instance8 = getInstance();
                getInstance();
                ServerTroubleshooterUtil.unReachableMsg = instance8.getValue(getString("software_or_patch_store_not_reachable_unreachableMsg", null), new Object[] { "Software Repository", swPath });
                final ServerTroubleshooterUtil instance9 = getInstance();
                getInstance();
                ServerTroubleshooterUtil.solution = instance9.getValue(getString("software_or_patch_store_not_reachable_solution", null), new Object[] { "Software Repository Location", swPath });
                final String binDir = WebServerUtil.getServerHomeCanonicalPath() + File.separator + "bin";
                final ServerTroubleshooterUtil instance10 = getInstance();
                getInstance();
                ServerTroubleshooterUtil.solutionDetails = instance10.getValue(getString("software_or_patch_store_not_reachable_solutionDetail", null), new Object[] { binDir, product });
                final ServerTroubleshooterUtil instance11 = getInstance();
                getInstance();
                ServerTroubleshooterUtil.mailContent = instance11.getValue(getString("software_or_patch_store_not_reachable_mailContent", null), new Object[] { "Software Repository Location", swPath, product });
                final ServerTroubleshooterUtil instance12 = getInstance();
                getInstance();
                ServerTroubleshooterUtil.subject = instance12.getValue(getString("software_or_patch_store_not_reachable_mailSubject", null), new Object[] { product });
                break;
            }
            case "osd_repo_not_reachable": {
                final Properties sharedAccessProps = StartupUtil.getProperties(ServerTroubleshooterUtil.serverHome + File.separator + WebServerUtil.SHARE_ACCESS_STATUS_FILE);
                String osdRepo = sharedAccessProps.getProperty("osd_repository_path");
                osdRepo = osdRepo.replace("/", "\\");
                ServerTroubleshooterUtil.heading = product + " is unable to reach the OSD Repository";
                ServerTroubleshooterUtil.unReachableMsg = getInstance().getValue(genUnReachableMsg, new Object[] { osdRepo });
                ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { osdRepo });
                ServerTroubleshooterUtil.solutionDetails = getInstance().getValue(genSolutionDetails, new Object[0]);
                ServerTroubleshooterUtil.subject = getInstance().getValue(genSubject, new Object[] { product });
                ServerTroubleshooterUtil.mailContent = getInstance().getValue(genMailContent, new Object[] { osdRepo, product });
                break;
            }
            case "mssql_invalid_login_credentials": {
                String database = null;
                if (DBUtil.getActiveDBName().equalsIgnoreCase("postgres")) {
                    database = "PostgreSQL";
                }
                else if (DBUtil.getActiveDBName().equalsIgnoreCase("mssql")) {
                    database = "SQL";
                }
                ServerTroubleshooterUtil.heading = getInstance().getValue(genHeading, new Object[] { product, database });
                ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { product, database });
                ServerTroubleshooterUtil.solutionDetails = getInstance().getValue(genSolutionDetails, new Object[] { product, database });
                ServerTroubleshooterUtil.mailContent = getInstance().getValue(genMailContent, new Object[] { product, database, database });
                ServerTroubleshooterUtil.subject = getInstance().getValue(genSubject, new Object[] { product, database });
                break;
            }
            case "mssql_transaction_log_size_exceed": {
                ServerTroubleshooterUtil.heading = getInstance().getValue(genHeading, new Object[] { product });
                final ServerTroubleshooterUtil instance13 = getInstance();
                final String value = genSolution;
                final Object[] params = { null };
                final int n2 = 0;
                getInstance();
                params[n2] = getProperties(generalProperty).getProperty("customize_transaction_log_size");
                ServerTroubleshooterUtil.solution = instance13.getValue(value, params);
                ServerTroubleshooterUtil.solutionDetails = getInstance().getValue(genSolutionDetails, new Object[] { product });
                final ServerTroubleshooterUtil instance14 = getInstance();
                final String value2 = genMailContent;
                final Object[] params2 = { product, null };
                final int n3 = 1;
                getInstance();
                params2[n3] = getProperties(generalProperty).getProperty("customize_transaction_log_size");
                ServerTroubleshooterUtil.mailContent = instance14.getValue(value2, params2);
                ServerTroubleshooterUtil.subject = getInstance().getValue(genSubject, new Object[] { product });
                break;
            }
            case "mssql_connection_lost": {
                if (DBUtil.getActiveDBName().equalsIgnoreCase("postgres")) {
                    ServerTroubleshooterUtil.heading = getInstance().getValue(genHeading, new Object[] { product, "PostgreSQL" });
                    ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { "PostgreSQL" });
                    ServerTroubleshooterUtil.solutionDetails = getInstance().getValue(genSolutionDetails, new Object[] { product });
                    break;
                }
                if (DBUtil.getActiveDBName().equalsIgnoreCase("mssql")) {
                    ServerTroubleshooterUtil.heading = getInstance().getValue(genHeading, new Object[] { product, "SQL" });
                    ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { "SQL" });
                    ServerTroubleshooterUtil.solutionDetails = getInstance().getValue(genSolutionDetails, new Object[] { product });
                    break;
                }
                break;
            }
            case "mssql_connection_retry": {
                if (DBUtil.getActiveDBName().equalsIgnoreCase("postgreSQL")) {
                    ServerTroubleshooterUtil.heading = getInstance().getValue(genHeading, new Object[] { "PostgreSQL" });
                    final String hostName = dbProps.get("HOST");
                    ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { hostName, "PostgreSQL" });
                    ServerTroubleshooterUtil.solutionDetails = getInstance().getValue(genSolutionDetails, new Object[] { product, "PostgreSQL", "PostgreSQL", product });
                    ServerTroubleshooterUtil.unReachableMsg = getInstance().getValue(genUnReachableMsg, new Object[] { product, "PostgreSQL" });
                    break;
                }
                if (DBUtil.getActiveDBName().equalsIgnoreCase("mssql")) {
                    ServerTroubleshooterUtil.heading = getInstance().getValue(genHeading, new Object[] { "SQL" });
                    final String hostName = dbProps.get("HOST");
                    ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { hostName, "SQL" });
                    ServerTroubleshooterUtil.solutionDetails = getInstance().getValue(genSolutionDetails, new Object[] { product, "SQL", "SQL", product });
                    ServerTroubleshooterUtil.unReachableMsg = getInstance().getValue(genUnReachableMsg, new Object[] { product, "SQL" });
                    break;
                }
                break;
            }
            case "ssl_certificate_key_encrypted": {
                ServerTroubleshooterUtil.mailContent = getInstance().getValue(genMailContent, new Object[] { product, supportID });
                ServerTroubleshooterUtil.subject = getInstance().getValue(genSubject, new Object[] { product });
                break;
            }
            case "ssl_certificate_chain_issue": {
                ServerTroubleshooterUtil.mailContent = getInstance().getValue(genMailContent, new Object[] { product, supportID });
                ServerTroubleshooterUtil.subject = getInstance().getValue(genSubject, new Object[] { product });
                break;
            }
            case "pgsql_in_recovery_mode": {
                ServerTroubleshooterUtil.heading = getInstance().getValue(genHeading, new Object[] { product });
                ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { product });
                ServerTroubleshooterUtil.unReachableMsg = getInstance().getValue(genUnReachableMsg, new Object[] { product });
                break;
            }
            case "pgsql_fails_in_recovery_mode": {
                ServerTroubleshooterUtil.heading = getInstance().getValue(genHeading, new Object[] { product });
                if (new File(ServerTroubleshooterUtil.serverHome + File.separator + "bin" + File.separator + "BackupDetails.txt").exists()) {
                    final String backupFileName = getPropertyValueFromFile(ServerTroubleshooterUtil.serverHome + File.separator + "bin" + File.separator + "BackupDetails.txt", "LastSuccessfullScheduledBackup");
                    final String backupLocation = getPropertyValueFromFile(ServerTroubleshooterUtil.serverHome + File.separator + "bin" + File.separator + "BackupDetails.txt", "ScheduledBackupLocation");
                    final String buildNumStrFromConf = getProductProperty("buildnumber");
                    if (backupFileName.contains(buildNumStrFromConf)) {
                        ServerTroubleshooterUtil.solution = getInstance().getValue(genSolution, new Object[] { backupFileName, backupLocation });
                    }
                    else {
                        ServerTroubleshooterUtil.solution = "Please upload the logs to support for further assistance.";
                    }
                }
                else {
                    ServerTroubleshooterUtil.solution = "Please upload the logs to support for further assistance.";
                }
                ServerTroubleshooterUtil.unReachableMsg = genUnReachableMsg;
                break;
            }
        }
    }
    
    public static String getProductProperty(final String key) {
        String value = null;
        try {
            final String fname = ServerTroubleshooterUtil.serverHome + File.separator + "conf" + File.separator + "product.conf";
            ServerTroubleshooterUtil.logger.log(Level.FINE, "***********getProductProperty***********fname: " + fname);
            final Properties props = getProperties(fname);
            value = props.getProperty(key);
            if (value == null) {
                value = "";
            }
        }
        catch (final Exception ex) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Caught exception while getting product property: " + key, ex);
        }
        return value;
    }
    
    public void setPropertyValueToFile(final String fileName, final String property, final String value) {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(fileName);
            final Properties props = new Properties();
            props.load(in);
            in.close();
            out = new FileOutputStream(fileName);
            props.setProperty(property, value);
            props.store(out, null);
            out.close();
        }
        catch (final Exception e) {
            ServerTroubleshooterUtil.logger.log(Level.SEVERE, "Exception while updating the value of the property " + property + " in " + fileName);
            if (in != null) {
                try {
                    in.close();
                }
                catch (final IOException e2) {
                    ServerTroubleshooterUtil.logger.log(Level.SEVERE, "I/O Exception inside setPropertyValueToFile..", e2);
                }
            }
            if (out != null) {
                try {
                    out.close();
                }
                catch (final IOException e2) {
                    ServerTroubleshooterUtil.logger.log(Level.SEVERE, "I/O Exception inside setPropertyValueToFile..", e2);
                }
            }
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (final IOException e3) {
                    ServerTroubleshooterUtil.logger.log(Level.SEVERE, "I/O Exception inside setPropertyValueToFile..", e3);
                }
            }
            if (out != null) {
                try {
                    out.close();
                }
                catch (final IOException e3) {
                    ServerTroubleshooterUtil.logger.log(Level.SEVERE, "I/O Exception inside setPropertyValueToFile..", e3);
                }
            }
        }
    }
    
    public static void killExeIfRunning(final String exeName, final String exeLocation, final String exeStatusFile) {
        try {
            final String binDir = System.getProperty("server.home") + File.separator + "bin";
            final String winUtil = binDir + File.separator + "dcwinutil.exe";
            final File file = new File(binDir);
            final List<String> command = new ArrayList<String>();
            command.add(winUtil);
            command.add("-Killifexist");
            command.add(exeName);
            command.add(exeLocation);
            command.add(exeStatusFile);
            final ProcessBuilder processbuilder = new ProcessBuilder(command);
            processbuilder.directory(file);
            final Process process = processbuilder.start();
            ServerTroubleshooterUtil.logger.log(Level.INFO, "Command For Find service status : " + processbuilder.command());
            final int exitValue = process.waitFor();
            ServerTroubleshooterUtil.logger.log(Level.INFO, "EXIT VALUE :: {0}", exitValue);
        }
        catch (final Exception ex) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Caught exception while Writing " + exeName + " service status into " + exeStatusFile + " : ", ex);
            ex.printStackTrace();
        }
    }
    
    public static void changeUserAccountToSystemAccount(final String serviceName, final String exeStatusFile) {
        try {
            final String binDir = System.getProperty("server.home") + File.separator + "bin";
            final String winUtil = binDir + File.separator + "dcwinutil.exe";
            final File file = new File(binDir);
            final List<String> command = new ArrayList<String>();
            command.add(winUtil);
            command.add("-changeserviceconfig");
            command.add(serviceName);
            command.add(exeStatusFile);
            final ProcessBuilder processbuilder = new ProcessBuilder(command);
            processbuilder.directory(file);
            final Process process = processbuilder.start();
            ServerTroubleshooterUtil.logger.log(Level.INFO, "Command For Find service status : " + processbuilder.command());
            final int exitValue = process.waitFor();
            ServerTroubleshooterUtil.logger.log(Level.INFO, "EXIT VALUE :: {0}", exitValue);
        }
        catch (final Exception ex) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Caught exception while change User Acccount to System Acccount. Exception : ", ex);
            ex.printStackTrace();
        }
    }
    
    public static int getNextFreePort(final String existingDBPort) {
        boolean isDBPortInUse = true;
        int dbPort = Integer.parseInt(existingDBPort) + 1;
        int startupPortRange = 5;
        if (new File(ServerTroubleshooterUtil.dbSettingsConf).exists()) {
            final String dbPortRange = getPropertyValueFromFile(ServerTroubleshooterUtil.dbSettingsConf, "DB_PORT_STARTUP_RANGE");
            if (dbPortRange != null && dbPortRange.trim().length() > 0) {
                startupPortRange = Integer.parseInt(dbPortRange);
            }
        }
        for (int dbPortChangeRange = dbPort + startupPortRange; dbPort < dbPortChangeRange && isDBPortInUse; ++dbPort) {
            isDBPortInUse = isPortEngaged(dbPort);
            if (isDBPortInUse) {}
        }
        if (isDBPortInUse) {
            return 0;
        }
        return dbPort;
    }
    
    public static boolean isDBPortChanged(final String existingDBPort, final String nextFreePort) {
        try {
            final File dbPropsFile = new File(System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf");
            if (dbPropsFile.exists()) {
                final BufferedReader reader = new BufferedReader(new FileReader(dbPropsFile));
                String line = null;
                String output = "";
                while ((line = reader.readLine()) != null) {
                    output = output + line + "\n";
                }
                if (reader != null) {
                    reader.close();
                }
                output = output.replaceAll(existingDBPort, nextFreePort);
                final FileWriter fw = new FileWriter(dbPropsFile);
                fw.write(output);
                fw.close();
            }
        }
        catch (final Exception ex) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Exception occurred while change the port..", ex);
            return false;
        }
        return true;
    }
    
    public static void createDefaultDBPortProps() {
        try {
            final HashMap dbProps = getDBPropertiesFromFile();
            final String dbPort = dbProps.get("PORT").toString();
            if (dbPort != null) {
                final Properties portProps = new Properties();
                ((Hashtable<String, String>)portProps).put("PGSQL_DB_PORT", dbPort);
                ((Hashtable<String, String>)portProps).put("DB_PORT_STARTUP_RANGE", "5");
                StartupUtil.storeProperties(portProps, ServerTroubleshooterUtil.dbSettingsConf);
            }
        }
        catch (final Exception ex) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Exception occurred while creating default db port props file. Exception : ", ex);
        }
    }
    
    public static void changeDefaultPortIntoDatabaseParams() {
        if (new File(ServerTroubleshooterUtil.dbSettingsConf).exists()) {
            final String defaultDBPort = getPropertyValueFromFile(ServerTroubleshooterUtil.dbSettingsConf, "PGSQL_DB_PORT");
            final HashMap dbProps = getDBPropertiesFromFile();
            final String dbPort = dbProps.get("PORT").toString();
            if (dbPort != null && defaultDBPort != null && !dbPort.trim().equalsIgnoreCase(defaultDBPort.trim())) {
                ServerTroubleshooterUtil.logger.log(Level.INFO, "Database Port changed to default Port : " + isDBPortChanged(dbPort, defaultDBPort));
            }
        }
    }
    
    public void writeStartupFailureInfoToConfFile(final Properties properties) throws Exception {
        StartupUtil.storeProperties(properties, System.getProperty("server.home") + File.separator + ServerTroubleshooterConstants.STARTUP_INFO_CONF_FILE_PATH, "Server Startup Status Details");
    }
    
    public void postStartupFailureDetails(final String failureCase) {
        try {
            final boolean isFileExist = this.isStartupConfFileAvailable();
            final Properties props = new Properties();
            ((Hashtable<String, String>)props).put("LST_StartupStatus", "failure");
            ((Hashtable<String, String>)props).put("LST_FailureReason", failureCase);
            String isFosConfigured = "False";
            if (isFosConfigured()) {
                isFosConfigured = "True";
            }
            ((Hashtable<String, String>)props).put("IS_FOS_CONFIGURED", isFosConfigured);
            if (!isFileExist) {
                ((Hashtable<String, String>)props).put("FST_StartupStatus", "Failure");
                ((Hashtable<String, String>)props).put("FST_FailureReason", failureCase);
                ((Hashtable<String, String>)props).put("ServerSuccStartedAtleastOnce", "No");
                ((Hashtable<String, String>)props).put("IsDataNeedToPost", "Yes");
                ServerTroubleshooterUtil.logger.log(Level.INFO, "First Startup Details Keys Added in Properties.[ In ServerTroubleshooterUtil ]");
            }
            if (isFileExist) {
                Properties startUpProps = new Properties();
                startUpProps = StartupUtil.getProperties(System.getProperty("server.home") + File.separator + ServerTroubleshooterConstants.STARTUP_INFO_CONF_FILE_PATH);
                if (!startUpProps.containsKey("IsServerAndDBInDiffSubnetStartedAtLeastOnce") && !isDBInSameNetwork() && isFosConfigured()) {
                    ((Hashtable<String, String>)props).put("IsServerAndDBInDiffSubnetStartedAtLeastOnce", "No");
                }
            }
            ServerTroubleshooterUtil.logger.log(Level.INFO, "Going to write file with properties from ServerTroubleshooterUtil");
            this.writeStartupFailureInfoToConfFile(props);
            final String isDataNeedToPost = this.getValueFromStartupInfoConf("IsDataNeedToPost");
            if (!isFileExist || (isDataNeedToPost != null && isDataNeedToPost.equalsIgnoreCase("Yes"))) {
                this.callBatFileToPostData();
            }
        }
        catch (final Exception ex) {
            ServerTroubleshooterUtil.logger.log(Level.SEVERE, "Exception on post startup issues ", ex);
        }
    }
    
    private static boolean isFosConfigured() throws Exception {
        Properties failoverUserProps = new Properties();
        final String fosPropsFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos_user.conf";
        failoverUserProps = StartupUtil.getProperties(fosPropsFilePath);
        final String secondaryIP = failoverUserProps.getProperty("SecondaryServerIP");
        return secondaryIP != null && !"".equals(secondaryIP);
    }
    
    public static boolean isDBInSameNetwork() {
        Boolean isDBInSameNetwork = Boolean.FALSE;
        try {
            final String hostname = getDBHostName();
            final InetAddress dbAddress = InetAddress.getByName(hostname);
            final InetAddress serverAddress = InetAddress.getLocalHost();
            final String mask = getMask(serverAddress);
            isDBInSameNetwork = sameNetwork(dbAddress, serverAddress, mask);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return isDBInSameNetwork;
    }
    
    public static String getMask(final InetAddress serverAddress) throws SocketException {
        final NetworkInterface networkInterface = NetworkInterface.getByInetAddress(serverAddress);
        final int prflen = networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength();
        final int shft = -1 << 32 - prflen;
        final int oct1 = (byte)((shft & 0xFF000000) >> 24) & 0xFF;
        final int oct2 = (byte)((shft & 0xFF0000) >> 16) & 0xFF;
        final int oct3 = (byte)((shft & 0xFF00) >> 8) & 0xFF;
        final int oct4 = (byte)(shft & 0xFF) & 0xFF;
        final String submask = oct1 + "." + oct2 + "." + oct3 + "." + oct4;
        return submask;
    }
    
    public static boolean sameNetwork(final InetAddress ip1, final InetAddress ip2, final String mask) throws Exception {
        final byte[] a1 = ip1.getAddress();
        final byte[] a2 = ip2.getAddress();
        final byte[] m = InetAddress.getByName(mask).getAddress();
        for (int i = 0; i < a1.length; ++i) {
            if ((a1[i] & m[i]) != (a2[i] & m[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static String getDBHostName() throws Exception {
        String serverName = null;
        final String fname = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
        final FileReader fileReader = new FileReader(fname);
        try {
            final Properties dbProps = new Properties();
            dbProps.load(fileReader);
            if (dbProps != null) {
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
                serverName = hostName;
            }
            ServerTroubleshooterUtil.logger.log(Level.INFO, "Database Name from database_params.conf: {0}", serverName);
        }
        catch (final Exception e) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Exception in getting DB Host name : ", e);
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            }
            catch (final Exception ex) {
                ServerTroubleshooterUtil.logger.log(Level.WARNING, "Exception in closing reader : ", ex);
            }
        }
        finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            }
            catch (final Exception ex2) {
                ServerTroubleshooterUtil.logger.log(Level.WARNING, "Exception in closing reader : ", ex2);
            }
        }
        return serverName;
    }
    
    public void collectFirstStartupFailureData(final String failureCase) {
        if (failureCase.equalsIgnoreCase("pgsql_startup_failure_unknown_case")) {
            this.storePgStartupFailedData();
        }
    }
    
    public void callBatFileToPostData() {
        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(new String[] { System.getProperty("server.home") + File.separator + ServerTroubleshooterConstants.FAILURE_METRACK_POST_BAT_FILE_PATH, "startup" });
            final Process licBatExecutionProcess = processBuilder.start();
            final int errorCode = licBatExecutionProcess.waitFor();
            ServerTroubleshooterUtil.logger.log(Level.INFO, "ServerStatusUpdaterMetrack.bat execution completed. Error code : " + errorCode);
        }
        catch (final Exception ex) {
            ServerTroubleshooterUtil.logger.log(Level.SEVERE, "Exception on initiate metrack post bat file : ", ex);
        }
    }
    
    public String getValueFromStartupInfoConf(final String keyName) {
        final Properties loadProps = StartupUtil.getProperties(System.getProperty("server.home") + File.separator + ServerTroubleshooterConstants.STARTUP_INFO_CONF_FILE_PATH);
        return loadProps.getProperty(keyName);
    }
    
    public boolean isStartupConfFileAvailable() {
        boolean flag = false;
        final File file = new File(System.getProperty("server.home") + File.separator + ServerTroubleshooterConstants.STARTUP_INFO_CONF_FILE_PATH);
        flag = file.exists();
        ServerTroubleshooterUtil.logger.log(Level.INFO, "-----------------------------------------------------------------");
        ServerTroubleshooterUtil.logger.log(Level.INFO, "Startupinfo.conf File Exist Status is {0}", flag);
        ServerTroubleshooterUtil.logger.log(Level.INFO, "-----------------------------------------------------------------");
        return flag;
    }
    
    public void setStartFailureException(final Exception e) {
        ServerTroubleshooterUtil.firstStartupFailure = e;
    }
    
    public void storePgStartupFailedData() {
        final Properties props = new Properties();
        try {
            String collectedLogs = collectPgStartupFailureLogData();
            if (!collectedLogs.isEmpty() && collectedLogs.length() < 4096) {
                ((Hashtable<String, String>)props).put("FST_PgFailureLogs", collectedLogs);
            }
            else if (!collectedLogs.isEmpty()) {
                collectedLogs = collectedLogs.substring(0, 2048);
                ((Hashtable<String, String>)props).put("FST_PgFailureLogs", collectedLogs);
            }
            if (ServerTroubleshooterUtil.firstStartupFailure != null) {
                Throwable rootCause;
                for (rootCause = ServerTroubleshooterUtil.firstStartupFailure.getCause(); rootCause.getCause() != null && rootCause.getCause() != rootCause; rootCause = rootCause.getCause()) {}
                ((Hashtable<String, String>)props).put("FST_PgFailureEx", rootCause.getMessage());
            }
            if (!props.isEmpty()) {
                this.writeStartupFailureInfoToConfFile(props);
            }
        }
        catch (final Exception e) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Exception while storing collected data in startupinfo.conf file.", e);
        }
    }
    
    public static String collectPgStartupFailureLogData() {
        final int minCount = 5;
        final String pgLog = "pg_log";
        final String pgData = ServerTroubleshooterUtil.serverHome + File.separator + "pgsql" + File.separator + "data";
        final String serverPgLog = ServerTroubleshooterUtil.serverHome + File.separator + "logs" + File.separator + "pglog_0.log";
        final ArrayDeque<String> fatalList = new ArrayDeque<String>(5);
        final ArrayDeque<String> endList = new ArrayDeque<String>(5);
        final ArrayDeque<String> serverPgList = new ArrayDeque<String>(5);
        BufferedReader logReader = null;
        final File pgLogFile = new File(pgData + File.separator + "pg_log");
        ServerTroubleshooterUtil.logger.log(Level.INFO, "Started to collect the first pg startup failure details");
        try {
            if (pgLogFile.exists() && pgLogFile.isDirectory()) {
                final File[] pgLogList = new File(pgData, "pg_log").listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(final File dir, final String fileName) {
                        return fileName.startsWith("postgresql") && fileName.toLowerCase().endsWith(".log");
                    }
                });
                final File filteredLog = getLastModifiedFile(pgLogList);
                logReader = Files.newBufferedReader(Paths.get(filteredLog.getPath(), new String[0]));
                String lines = null;
                while ((lines = logReader.readLine()) != null) {
                    try {
                        if (5 <= endList.size()) {
                            final String queueEnd = endList.remove();
                            if (queueEnd.contains("FATAL:")) {
                                if (5 <= fatalList.size()) {
                                    fatalList.remove();
                                }
                                fatalList.add(queueEnd.substring(queueEnd.indexOf(":  ") + 3));
                            }
                        }
                        if (lines.contains("FATAL:")) {
                            endList.add(lines.substring(lines.indexOf("FATAL:")));
                        }
                        else {
                            endList.add(lines.substring(lines.indexOf(":  ") + 3));
                        }
                    }
                    catch (final Exception e) {
                        ServerTroubleshooterUtil.logger.log(Level.WARNING, "Unable to filter the log data. ", e);
                    }
                }
                final File serverPgLogPath = new File(serverPgLog);
                if (serverPgLogPath.exists()) {
                    logReader = Files.newBufferedReader(Paths.get(serverPgLogPath.getPath(), new String[0]));
                    while ((lines = logReader.readLine()) != null) {
                        if (5 < serverPgList.size()) {
                            serverPgList.remove();
                        }
                        serverPgList.add(lines.substring(lines.indexOf(": ") + 2));
                    }
                }
                ServerTroubleshooterUtil.logger.log(Level.INFO, "Collect the first pg startup failure details successfully");
            }
        }
        catch (final Exception e2) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Exception while collecting pgsql startup failure details.", e2);
        }
        return endList.toString() + serverPgList.toString() + fatalList.toString();
    }
    
    public static File getLastModifiedFile(final File[] files) throws Exception {
        final int len = files.length;
        File lastModifiedFile = null;
        for (int s = 0; s < len; ++s) {
            if (lastModifiedFile == null || files[s].lastModified() > lastModifiedFile.lastModified()) {
                lastModifiedFile = files[s];
            }
        }
        return lastModifiedFile;
    }
    
    public static Properties parseHbaRecord(final String strRecord) {
        Properties pr = null;
        final String[] hbaFieldsInOrder = { "TYPE", "DATABASE", "USER", "ADDRESS", "METHOD" };
        try {
            if (strRecord == null || strRecord.trim().length() == 0) {
                return pr;
            }
            pr = new Properties();
            final String[] rdArr = strRecord.split(" +");
            if (rdArr.length < hbaFieldsInOrder.length) {
                return pr;
            }
            for (int s = 0; s < rdArr.length; ++s) {
                pr.setProperty(hbaFieldsInOrder[s], rdArr[s].trim());
            }
        }
        catch (final Exception ex) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Caught exception while parsing the record string: " + strRecord, ex);
        }
        return pr;
    }
    
    public static List getHbaRecords(final File confFileWithPath) throws IOException {
        final boolean isparsed = false;
        final List<Properties> hbaRecs = new ArrayList<Properties>();
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(confFileWithPath);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String rline = null;
            while ((rline = br.readLine()) != null) {
                if (!rline.trim().startsWith("#")) {
                    if (rline.trim().length() <= 0) {
                        continue;
                    }
                    final Properties pr = parseHbaRecord(rline);
                    hbaRecs.add(pr);
                }
            }
        }
        catch (final Exception ex) {
            ServerTroubleshooterUtil.logger.log(Level.WARNING, "Caught Exception while reading HBA records...", ex);
        }
        finally {
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
        return hbaRecs;
    }
    
    static {
        ServerTroubleshooterUtil.logger = Logger.getLogger("ServerTroubleshooterLogger");
        ServerTroubleshooterUtil.serverHome = System.getProperty("server.home");
        ServerTroubleshooterUtil.serverTroubleshooterUtil = null;
        ServerTroubleshooterUtil.dbSettingsConf = System.getProperty("server.home") + File.separator + "conf" + File.separator + "dbSettings.conf";
        ServerTroubleshooterUtil.firstStartupFailure = null;
        final String systemProperties = System.getProperty("server.home") + "/conf/system_properties.conf";
        if (new File(systemProperties).exists()) {
            final Properties prop = getProperties(systemProperties);
            if (prop.containsKey("enable.server.troubleshoot")) {
                ServerTroubleshooterUtil.isTroubleShootEnabled = prop.getProperty("enable.server.troubleshoot");
            }
            else {
                ServerTroubleshooterUtil.isTroubleShootEnabled = "true";
            }
        }
        ServerTroubleshooterUtil.pageTitle = "";
        ServerTroubleshooterUtil.heading = "";
        ServerTroubleshooterUtil.unReachableMsg = "";
        ServerTroubleshooterUtil.solution = "";
        ServerTroubleshooterUtil.solutionDetails = "";
        ServerTroubleshooterUtil.recommendation = "";
        ServerTroubleshooterUtil.solutionHeading = "";
        ServerTroubleshooterUtil.productUrl = WebServerUtil.getProductUrl();
        ServerTroubleshooterUtil.uploadLogsUrl = WebServerUtil.getUploadLogsUrl();
        ServerTroubleshooterUtil.contactSupportMsg = "If the problem persists, please <a target=\"_blank\" href=\"" + ServerTroubleshooterUtil.uploadLogsUrl + "\">upload</a> the logs to support for further assistance.";
        ServerTroubleshooterUtil.mailContent = "";
        ServerTroubleshooterUtil.subject = "";
    }
}
