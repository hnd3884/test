package com.me.devicemanagement.onpremise.start.util;

import java.net.InetAddress;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import org.json.simple.JSONObject;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Set;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.List;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.io.File;
import com.me.devicemanagement.onpremise.start.DCConsoleOut;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMigrationUtil
{
    public static Logger logger;
    private static final int SHOW_USAGE = 2;
    public static final String SERVER_MIGRATE_CONF_FILE;
    private static final String APACHE_CONF;
    private static final String MOD_REWRITE_CONF_FILE;
    private static final String MOD_REWRITE_CONF_TEMPLATE_FILE;
    private static final String SSL_MOD_REWRITE_CONF_FILE;
    private static final String MIGRATED_SERVER_INFO;
    private static final String DISABLE_MIGRATION_TEMP;
    private static int filecount;
    private static int foldercount;
    
    public static void main(final String[] args) {
        try {
            final String action = args[0];
            if (action.equalsIgnoreCase("enable") || action.equalsIgnoreCase("disable") || action.equalsIgnoreCase("retain")) {
                migrateServer(action);
            }
            else {
                System.exit(2);
            }
        }
        catch (final Exception ex) {
            ServerMigrationUtil.logger.log(Level.WARNING, "Exception while enabling\\disabling server migration settings ::", ex);
            DCConsoleOut.println("\n" + ex.getMessage() + "\n");
            System.exit(1);
        }
    }
    
    private static void migrateServer(final String action) throws Exception {
        DCLogUtil.initLogger();
        ServerMigrationUtil.logger.log(Level.INFO, "-+-+-+-+-+-+-+ INVOKED COMMAND :: server-migration.bat {0} +-+-+-+-+-+-+-+", action);
        if (action.equalsIgnoreCase("enable")) {
            final String serverHome = getServerHome();
            checkUserInput(serverHome);
        }
        if (action.equalsIgnoreCase("retain")) {
            final String migrateConf = getMigrateConfFile();
            if (new File(migrateConf).exists()) {
                ServerMigrationUtil.logger.log(Level.WARNING, "migrate.conf file is present in. Wrong parameter passed while executing bat file. Execute server-migration.bat enable");
                throw new Exception("migrate.conf file is present in. Wrong parameter passed while executing bat file. Execute server-migration.bat enable");
            }
        }
        if (!StartupUtil.maintenanceCompletedSuccessfully()) {
            ServerMigrationUtil.logger.log(Level.WARNING, "Server maintenance is currently going on.");
            throw new Exception("Server Migration cannot be performed when Maintenance is running. Wait for maintenance to complete and try again.");
        }
        final String serviceName = getDCServiceName();
        stopProductServer(serviceName);
        int retryCount = 0;
        while (retryCount < 6) {
            final String lockstring = getServerHome() + File.separator + "bin" + File.separator + ".lock";
            final File lockFile = new File(lockstring);
            if (!lockFile.exists()) {
                break;
            }
            ++retryCount;
            Thread.sleep(10000L);
        }
        ServerMigrationUtil.logger.log(Level.INFO, "Going to Stop the Web Server");
        WebServerUtil.stopServers();
        if (action.equalsIgnoreCase("retain")) {
            enableConfiguringNewServer();
        }
        else if (action.equalsIgnoreCase("enable")) {
            enableServerMigration();
        }
        else {
            disableServerMigration();
        }
    }
    
    public static void stopProductServer(final String serviceName) throws Exception {
        try {
            final ProcessBuilder pb = new ProcessBuilder(new String[] { "sc", "stop", serviceName });
            final File dir = new File(System.getProperty("server.home") + File.separator + "bin");
            pb.directory(dir);
            final Process p = pb.start();
            p.waitFor();
        }
        catch (final Exception ex) {
            ServerMigrationUtil.logger.log(Level.WARNING, "Caught error while executing the command: sc stop " + serviceName + " exception: " + ex);
        }
    }
    
    public static void enableConfiguringNewServer() throws Exception {
        ServerMigrationUtil.logger.log(Level.INFO, "************ ENABLING SERVER MIGRATION - CONFIGURING NEW SERVER AS OLD*******************");
        final String serviceName = getDCServiceName();
        StartupUtil.changeServiceStartupType(serviceName, "disabled");
        final String apacheServiceName = getApacheServiceName();
        StartupUtil.changeServiceStartupType(apacheServiceName, "disabled");
        DCConsoleOut.println("\nServer Migration - Configure New Server as Old enabled successfully.\n");
        updateServerMigrationInfo();
        ServerMigrationUtil.logger.log(Level.INFO, "************ SERVER MIGRATION - CONFIGURE NEW SERVER AS OLD ENABLED  *******************");
    }
    
    private static void enableServerMigration() throws Exception {
        modifyStartupForMigrationEnabled();
        updateServerMigrationInfo();
        ServerMigrationUtil.logger.log(Level.INFO, "************ SERVER MIGRATION ENABLED  *******************");
    }
    
    public static List getValuesfromPropsFile(final String filepath) {
        final Properties props = new Properties();
        final List critical_path_values = new ArrayList();
        try {
            final InputStream in = new FileInputStream(new File(filepath));
            props.load(in);
            final Set critical_path_keys = props.keySet();
            for (final Object path : critical_path_keys) {
                critical_path_values.add(props.getProperty((String)path));
            }
        }
        catch (final Exception e) {
            ServerMigrationUtil.logger.info("Exception: " + e);
        }
        return critical_path_values;
    }
    
    private static void updateServerMigrationInfo() {
        final File mgtdserverinfo_file = new File(ServerMigrationUtil.MIGRATED_SERVER_INFO);
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            if (!mgtdserverinfo_file.exists()) {
                mgtdserverinfo_file.createNewFile();
            }
            fw = new FileWriter(mgtdserverinfo_file, false);
            bw = new BufferedWriter(fw);
            final JSONObject mgtdserverJSON = new JSONObject();
            mgtdserverJSON.put((Object)"ServerMigrationTime", (Object)String.valueOf(System.currentTimeMillis()));
            final List criticalPaths = getValuesfromPropsFile(System.getProperty("server.home") + File.separator + "conf" + File.separator + "criticalFilePaths.properties");
            if (!criticalPaths.isEmpty()) {
                for (final Object p : criticalPaths) {
                    final JSONObject count = getCountusingPath(System.getProperty("server.home") + File.separator + p.toString());
                    if (!count.isEmpty()) {
                        mgtdserverJSON.put((Object)p.toString(), (Object)count);
                    }
                }
            }
            bw.write(mgtdserverJSON.toString());
            bw.close();
            fw.close();
        }
        catch (final Exception e) {
            ServerMigrationUtil.logger.info("Exception caught while counting number of files and folders for server migration\n" + e);
        }
    }
    
    public static JSONObject getCountusingPath(final String path) {
        ServerMigrationUtil.filecount = 0;
        ServerMigrationUtil.foldercount = 0;
        return getCountofFilesandFolders(path);
    }
    
    private static JSONObject getCountofFilesandFolders(final String directoryName) {
        final File directory = new File(directoryName);
        final JSONObject count = new JSONObject();
        if (directory.exists()) {
            final File[] listFiles;
            final File[] fList = listFiles = directory.listFiles();
            for (final File file : listFiles) {
                if (file.isFile()) {
                    ++ServerMigrationUtil.filecount;
                }
                else if (file.isDirectory()) {
                    ++ServerMigrationUtil.foldercount;
                    getCountofFilesandFolders(file.getAbsolutePath());
                }
            }
            count.put((Object)"file_count", (Object)ServerMigrationUtil.filecount);
            count.put((Object)"folder_count", (Object)ServerMigrationUtil.foldercount);
        }
        return count;
    }
    
    private static void disableServerMigration() throws Exception {
        ServerMigrationUtil.logger.log(Level.INFO, "************ DISABLING SERVER MIGRATION *******************");
        modifyStartupForMigrationDisabled();
        fileEntrytoDisableMigration();
        DCConsoleOut.println("\nServer Migration disabled successfully.\n");
        ServerMigrationUtil.logger.log(Level.INFO, "************ SERVER MIGRATION DISABLED  *******************");
    }
    
    private static void fileEntrytoDisableMigration() {
        try {
            final Properties disable_migration_props = new Properties();
            disable_migration_props.setProperty("ServerMigration", "Disabled");
            FileAccessUtil.storeProperties(disable_migration_props, ServerMigrationUtil.DISABLE_MIGRATION_TEMP, false);
        }
        catch (final Exception e) {
            ServerMigrationUtil.logger.info("Exception caught while creating temp file: " + e);
        }
    }
    
    public static String getDCServiceName() {
        final String serverHome = getServerHome();
        String fileProps = serverHome + File.separator + "conf" + File.separator + "custom_wrapperservice.conf";
        if (!new File(fileProps).exists()) {
            fileProps = System.getProperty("server.home") + File.separator + "conf" + File.separator + "wrapper.conf";
        }
        String serviceName = getServiceName(fileProps, "wrapper.name");
        if (serviceName.equals("")) {
            serviceName = "uems_service";
        }
        ServerMigrationUtil.logger.log(Level.INFO, "DC_SERVICE_NAME :: {0}", serviceName);
        return serviceName;
    }
    
    public static String getApacheServiceName() {
        final String apacheServiceName = WebServerUtil.getApacheServiceName();
        ServerMigrationUtil.logger.log(Level.INFO, "APACHE_SERVICE_NAME :: {0}", apacheServiceName);
        final String trimmedServiceName = WebServerUtil.trimServiceNameApacheCompatible(apacheServiceName);
        ServerMigrationUtil.logger.log(Level.INFO, "APACHE_SERVICE_NAME_TRIMMED :: {0}", trimmedServiceName);
        return trimmedServiceName;
    }
    
    public static void generateModRewriteConf() throws Exception {
        final String serverHome = getServerHome();
        ServerMigrationUtil.logger.log(Level.INFO, "SERVER_HOME :: {0}", serverHome);
        generateHttpdModRewriteConf(serverHome);
        generateSSLModRewriteConf(serverHome);
    }
    
    public static void clearModRewriteConf() {
        try {
            final String serverHome = getServerHome();
            final String migrateConfFile = serverHome + File.separator + ServerMigrationUtil.SERVER_MIGRATE_CONF_FILE;
            final boolean status = new File(migrateConfFile).delete();
            ServerMigrationUtil.logger.log(Level.INFO, "migrate.conf deleted? :: {0}", status);
            final String modRewriteConf = serverHome + File.separator + ServerMigrationUtil.MOD_REWRITE_CONF_FILE;
            clearFileContents(modRewriteConf);
            final String sslModRewriteConf = serverHome + File.separator + ServerMigrationUtil.SSL_MOD_REWRITE_CONF_FILE;
            clearFileContents(sslModRewriteConf);
            clearFileContents(ServerMigrationUtil.MIGRATED_SERVER_INFO);
        }
        catch (final Exception e) {
            ServerMigrationUtil.logger.log(Level.WARNING, "Exception while clearing mod_rewrite contents", e);
        }
    }
    
    private static void generateHttpdModRewriteConf(final String serverHome) throws Exception {
        final String migrateConfFile = serverHome + File.separator + ServerMigrationUtil.SERVER_MIGRATE_CONF_FILE;
        final Properties migrationProps = StartupUtil.getProperties(migrateConfFile);
        final String modRewriteConfFile = serverHome + File.separator + ServerMigrationUtil.MOD_REWRITE_CONF_FILE;
        ServerMigrationUtil.logger.log(Level.INFO, "HttpdModRewrite Conf :: {0}", modRewriteConfFile);
        final String modRewriteTemplateFile = serverHome + File.separator + ServerMigrationUtil.MOD_REWRITE_CONF_TEMPLATE_FILE;
        ServerMigrationUtil.logger.log(Level.INFO, "HttpdModRewrite Conf Template :: {0}", modRewriteTemplateFile);
        try {
            com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(modRewriteTemplateFile, modRewriteConfFile, migrationProps, "%");
        }
        catch (final Exception e) {
            ServerMigrationUtil.logger.log(Level.WARNING, "Exception while generating httpd_mod_rewrite conf.", e);
            throw new Exception("Server migration activation failed.\n\nRetry the operation. If the issue persists, contact support.");
        }
    }
    
    private static void generateSSLModRewriteConf(final String serverHome) throws Exception {
        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            final String sslModRewriteConf = serverHome + File.separator + ServerMigrationUtil.SSL_MOD_REWRITE_CONF_FILE;
            ServerMigrationUtil.logger.log(Level.INFO, "SSLModRewrite Conf location :: {0}", sslModRewriteConf);
            fos = new FileOutputStream(sslModRewriteConf);
            pw = new PrintWriter(fos);
            pw.println("RewriteEngine On");
            pw.println("RewriteOptions Inherit");
            pw.flush();
        }
        catch (final Exception e) {
            ServerMigrationUtil.logger.log(Level.WARNING, "Exception while generating SSLModRewrite conf", e);
            throw new Exception("Server migration activation failed.\n\nRetry the operation. If the issue persists, contact support.");
        }
        finally {
            try {
                fos.close();
                pw.close();
            }
            catch (final Exception ex) {}
        }
    }
    
    public static void clearFileContents(final String fileName) {
        if (new File(fileName).exists()) {
            FileOutputStream os = null;
            try {
                os = new FileOutputStream(fileName);
                os.write(new String().getBytes());
                os.close();
            }
            catch (final Exception e) {
                ServerMigrationUtil.logger.log(Level.WARNING, "Exception while clearing file contents", e);
                try {
                    final File file = new File(fileName);
                    file.delete();
                    final File newFile = new File(fileName);
                    newFile.createNewFile();
                }
                catch (final Exception ex) {}
            }
            finally {
                try {
                    os.close();
                }
                catch (final Exception ex2) {}
            }
        }
    }
    
    public static String getServerHome() {
        String serverHome = System.getProperty("server.home");
        try {
            if (serverHome != null) {
                serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            }
        }
        catch (final Exception ex) {
            ServerMigrationUtil.logger.log(Level.WARNING, "Exception while getting serverHome canonical path", ex);
        }
        return serverHome;
    }
    
    public static String getMigrateConfFile() {
        final String migrateConfFile = getServerHome() + File.separator + ServerMigrationUtil.SERVER_MIGRATE_CONF_FILE;
        ServerMigrationUtil.logger.log(Level.INFO, "Migration Conf File :: {0}", migrateConfFile);
        return migrateConfFile;
    }
    
    private static void checkUserInput(final String serverHome) throws Exception {
        final String migrateConfFile = serverHome + File.separator + ServerMigrationUtil.SERVER_MIGRATE_CONF_FILE;
        ServerMigrationUtil.logger.log(Level.INFO, "Migrate conf-file loc :: {0}", migrateConfFile);
        if (!new File(migrateConfFile).exists()) {
            ServerMigrationUtil.logger.log(Level.WARNING, "migrate.conf file is not present in {0}. Server migration settings are not configured.", migrateConfFile);
            throw new Exception("Server Migration settings are not configured.\n\nConfigure the new server details [ Admin > DC Server Migration ] from the webconsole and execute this file.");
        }
        final Properties migrationProps = StartupUtil.getProperties(migrateConfFile);
        if (migrationProps == null || !migrationProps.containsKey("NewServerFlatName")) {
            ServerMigrationUtil.logger.log(Level.WARNING, "migrate.conf file present but not valid.");
            throw new Exception("Server Migration settings are not valid.\n\nConfigure the new server details [ Admin > DC Server Migration ] from the webconsole and execute this file.");
        }
        ServerMigrationUtil.logger.log(Level.INFO, "server-Migrate conf-file properties :: {0}", migrationProps);
        final String newServerFlatName = migrationProps.getProperty("NewServerFlatName");
        String CurrentHostName = "";
        try {
            CurrentHostName = InetAddress.getLocalHost().getHostName();
            ServerMigrationUtil.logger.log(Level.INFO, "Current Host Name " + CurrentHostName);
        }
        catch (final Exception ex) {
            ServerMigrationUtil.logger.log(Level.WARNING, "Unable to find the Host Name due to the exception :  ", ex);
        }
        if (CurrentHostName == "" || CurrentHostName == null) {
            ServerMigrationUtil.logger.log(Level.WARNING, "Unable to find the current Host Name ");
        }
        else if (newServerFlatName.equals(CurrentHostName)) {
            ServerMigrationUtil.logger.log(Level.WARNING, "\"server-migration.bat\" command executed at new server machine. ");
            throw new Exception("Invalid command. \"server-migration.bat enable\" must be executed at old server machine.");
        }
    }
    
    public static String getServiceName(final String fileProps, final String serviceNameHolder) {
        String serviceName = "";
        try {
            serviceName = getFileProperty(serviceNameHolder, fileProps);
            ServerMigrationUtil.logger.log(Level.INFO, "Value of Service Name  : " + serviceName);
        }
        catch (final Exception ex) {
            ServerMigrationUtil.logger.log(Level.WARNING, "Exception occured while finding the service Name from " + fileProps + " : ", ex);
        }
        return serviceName;
    }
    
    public static String getFileProperty(final String property, final String fileName) {
        String value = null;
        try {
            final Properties props = StartupUtil.getProperties(fileName);
            ServerMigrationUtil.logger.log(Level.INFO, " Properties  : " + props);
            value = props.getProperty(property);
            if (value == null) {
                value = "";
            }
        }
        catch (final Exception ex) {
            ServerMigrationUtil.logger.log(Level.WARNING, "Caught exception while getting product property " + property + " :", ex);
        }
        return value;
    }
    
    public static void modifyStartupForMigrationDisabled() throws Exception {
        final ServerMigrationUtilAPI serverMigrationUtilClass = getServerMigrationUtilClass();
        serverMigrationUtilClass.modifyProductStartupForMigrationDisabled();
    }
    
    public static void modifyStartupForMigrationEnabled() throws Exception {
        final ServerMigrationUtilAPI serverMigrationUtilClass = getServerMigrationUtilClass();
        serverMigrationUtilClass.modifyProductStartupForMigrationEnabled();
    }
    
    private static ServerMigrationUtilAPI getServerMigrationUtilClass() throws Exception {
        final Properties webSettingsProperties = WebServerUtil.getWebServerSettings();
        final String webServerName = webSettingsProperties.getProperty("webserver.name");
        if ("apache".equalsIgnoreCase(webServerName)) {
            return new ApacheServerMigrationUtil();
        }
        return new NginxServerMigrationUtil();
    }
    
    static {
        ServerMigrationUtil.logger = Logger.getLogger(ServerMigrationUtil.class.getName());
        SERVER_MIGRATE_CONF_FILE = "conf" + File.separator + "server-migrate.conf";
        APACHE_CONF = "apache" + File.separator + "conf";
        MOD_REWRITE_CONF_FILE = ServerMigrationUtil.APACHE_CONF + File.separator + "httpd_mod_rewrite.conf";
        MOD_REWRITE_CONF_TEMPLATE_FILE = ServerMigrationUtil.APACHE_CONF + File.separator + "templates" + File.separator + "httpd_mod_rewrite.conf.template";
        SSL_MOD_REWRITE_CONF_FILE = ServerMigrationUtil.APACHE_CONF + File.separator + "httpd_ssl_mod_rewrite.conf";
        MIGRATED_SERVER_INFO = System.getProperty("server.home") + File.separator + "conf" + File.separator + "migrated-server-info.json";
        DISABLE_MIGRATION_TEMP = System.getProperty("server.home") + File.separator + "conf" + File.separator + "server-migration-temp.conf";
        ServerMigrationUtil.filecount = 0;
        ServerMigrationUtil.foldercount = 0;
    }
}
