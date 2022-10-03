package com.me.devicemanagement.onpremise.start.util;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.Iterator;
import java.io.IOException;
import org.json.simple.parser.ParseException;
import java.io.FileNotFoundException;
import org.json.simple.JSONObject;
import java.io.Reader;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.util.Properties;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.io.File;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.start.DCConsoleOut;
import java.util.logging.Logger;

public class MigratedServerUtil
{
    private static Logger logger;
    private static final String MIGRATED_SERVER_INFO;
    private static final String SERVER_MIGRATION_TIME = "ServerMigrationTime";
    
    public static void main(final String[] args) {
        try {
            DCLogUtil.initLogger();
            deleteWsModifiedTimeFile();
            removeNginxEntryFromServerMigrateFile();
            clearNginxModRewrite();
            handleAuthenticationForServerMigration();
            migratedServerCopyCheck();
            DCConsoleOut.println("Since All the critical folders and files are present , proceding with instllation\n\n");
            MigratedServerUtil.logger.info("Since All the critical folders and files are present , proceding with instllation");
        }
        catch (final Exception e) {
            DCConsoleOut.println("\nAll the files have not been copied properly. Kindly copy the Server completely and execute the bat file again\n\n");
            MigratedServerUtil.logger.log(Level.WARNING, "All the files have not been copied properly. Kindly copy the Server completely and execute the bat file again ");
        }
    }
    
    private static void clearNginxModRewrite() {
        final String NGINX_MOD_REWRITE_CONF_FILE = "nginx" + File.separator + "conf" + File.separator + "nginx_mod_rewrite.conf";
        final String nginxModRewriteConf = System.getProperty("server.home") + File.separator + NGINX_MOD_REWRITE_CONF_FILE;
        if (new File(nginxModRewriteConf).exists()) {
            ServerMigrationUtil.clearFileContents(nginxModRewriteConf);
        }
    }
    
    private static void removeNginxEntryFromServerMigrateFile() {
        final String serverMigrateConfFile = ServerMigrationUtil.getMigrateConfFile();
        MigratedServerUtil.logger.log(Level.INFO, "Server Migrate Conf File " + serverMigrateConfFile);
        if (new File(serverMigrateConfFile).exists()) {
            final Properties properties = StartupUtil.getProperties(serverMigrateConfFile);
            if (properties != null) {
                properties.setProperty("migrate.server.nginx", "false");
            }
            MigratedServerUtil.logger.log(Level.INFO, "Properties after removing nginx migration entry" + properties);
            StartupUtil.storeProperties(properties, serverMigrateConfFile);
            MigratedServerUtil.logger.log(Level.INFO, "After removing properties " + StartupUtil.getProperties(serverMigrateConfFile));
        }
    }
    
    public static void migratedServerCopyCheck() throws Exception {
        try {
            MigratedServerUtil.logger.info("Copy Check in new Installation");
            final FileReader fr = new FileReader(MigratedServerUtil.MIGRATED_SERVER_INFO);
            final JSONParser parser = new JSONParser();
            final Object object = parser.parse((Reader)fr);
            final JSONObject criticalPath = (JSONObject)object;
            final String path = System.getProperty("server.home");
            String criticalPathforStartUp = new String();
            JSONObject existing_count = new JSONObject();
            JSONObject countfromFile = new JSONObject();
            for (final String key : criticalPath.keySet()) {
                if (!key.equals("ServerMigrationTime")) {
                    countfromFile = (JSONObject)criticalPath.get((Object)key);
                    criticalPathforStartUp = path + File.separator + key;
                    existing_count = ServerMigrationUtil.getCountusingPath(criticalPathforStartUp);
                    if (!countfromFile.toString().equals(existing_count.toString())) {
                        DCConsoleOut.println("\nThe count of files and folders in " + key + " does not match that of the old installation! Kindly copy the entire server folder manually and execute the bat file again.\n");
                        MigratedServerUtil.logger.log(Level.WARNING, "The count of files and folders in " + key + " does not match that of the old installation! Kindly copy the entire server folder manually and execute the bat file again.");
                        throw new Exception("The Server Folder has not been copied properly. Kindly repeat the task and proceed again. Check " + key);
                    }
                    continue;
                }
            }
        }
        catch (final FileNotFoundException e) {
            MigratedServerUtil.logger.info(e.toString());
        }
        catch (final ParseException e2) {
            MigratedServerUtil.logger.info(e2.toString());
        }
        catch (final IOException e3) {
            MigratedServerUtil.logger.info(e3.toString());
        }
    }
    
    public static Long getServerMigrationTime() {
        Long serverMigrationTime = 0L;
        try {
            final FileReader fr = new FileReader(MigratedServerUtil.MIGRATED_SERVER_INFO);
            final JSONParser parser = new JSONParser();
            final JSONObject migratedServerInfo = (JSONObject)parser.parse((Reader)fr);
            serverMigrationTime = (Long)migratedServerInfo.get((Object)"ServerMigrationTime");
            return serverMigrationTime;
        }
        catch (final Exception e) {
            MigratedServerUtil.logger.info("Exception caught while getting server migration info: " + e);
            return serverMigrationTime;
        }
    }
    
    private static void deleteWsModifiedTimeFile() {
        final String wsModifiedTime = System.getProperty("server.home") + File.separator + "conf" + File.separator + "ws.modtime";
        try {
            final File wsModTimeFile = new File(wsModifiedTime);
            if (wsModTimeFile.exists()) {
                final boolean isDeleted = wsModTimeFile.delete();
                MigratedServerUtil.logger.info("Going to delete ws.modtime file : " + isDeleted);
            }
        }
        catch (final Exception ex) {
            MigratedServerUtil.logger.log(Level.WARNING, "Exception caught while trying to delete ws.modtime file : ", ex);
        }
    }
    
    private static void handleAuthenticationForServerMigration() {
        final Logger authLog = Logger.getLogger("AgentServerAuthLogger");
        authLog.log(Level.INFO, "Executing handleAuthenticationForServerMigration in MigratedServerUtil ");
        try {
            final String serverHome = WebServerUtil.getServerHomeCanonicalPath();
            final String nginxConfTemplate = serverHome + File.separator + "nginx" + File.separator + "conf" + File.separator + "nginx.conf.template";
            final String nginxConfExcludeTemplate = serverHome + File.separator + "conf" + File.separator + "AuthUpgrade" + File.separator + "nginx.conf.template";
            final String nginxsslConfTemplate = serverHome + File.separator + "nginx" + File.separator + "conf" + File.separator + "nginx-ssl.conf.template";
            final String nginxsslExcludeTemplate = serverHome + File.separator + "conf" + File.separator + "AuthUpgrade" + File.separator + "nginx-ssl.conf.template";
            final String apacheConfTemplate = serverHome + File.separator + "apache" + File.separator + "conf" + File.separator + "httpd.conf.template";
            final String apacheConfExcludeTemplate = serverHome + File.separator + "conf" + File.separator + "AuthUpgrade" + File.separator + "httpd.conf.template.exclude";
            final String nginxExcludeFileContent = new String(FileAccessUtil.getFileAsByteArray(nginxConfExcludeTemplate));
            final String apacheExcludeFileContent = new String(FileAccessUtil.getFileAsByteArray(apacheConfExcludeTemplate));
            if (nginxExcludeFileContent.contains("#AgentUpgrade") && apacheExcludeFileContent.contains("#AgentUpgrade")) {
                swapFiles(nginxConfExcludeTemplate, nginxConfTemplate);
                swapFiles(nginxsslExcludeTemplate, nginxsslConfTemplate);
                swapFiles(apacheConfExcludeTemplate, apacheConfTemplate);
                authLog.log(Level.INFO, "Files Swapped in handleAuthenticationForServerMigration");
            }
            WebServerUtil.addOrUpdateWebServerProps("disable.ds.agent.upgrade", "");
            WebServerUtil.addOrUpdateWebServerProps("migrated.server.auth.swap", "true");
            WebServerUtil.addOrUpdateWebServerProps("migrated.server.auth.swaped.time", String.valueOf(System.currentTimeMillis()));
            authLog.log(Level.INFO, "migrated.server.auth.swap keys added in websettings.conf");
            authLog.log(Level.INFO, "End of handleAuthenticationForServerMigration");
        }
        catch (final Exception e) {
            authLog.log(Level.SEVERE, "Exception caught while handling authentication in Server Migration ", e);
        }
    }
    
    private static void swapFiles(final String filePathOne, final String filePathTwo) throws Exception {
        String temp = "";
        final String tempFile = new File(filePathOne).getParent() + File.separator + "temp.txt";
        BufferedReader br = new BufferedReader(new FileReader(filePathOne));
        BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
        while ((temp = br.readLine()) != null) {
            bw.write(temp);
            bw.newLine();
            bw.flush();
        }
        br.close();
        bw.close();
        br = new BufferedReader(new FileReader(filePathTwo));
        bw = new BufferedWriter(new FileWriter(filePathOne));
        while ((temp = br.readLine()) != null) {
            bw.write(temp);
            bw.newLine();
            bw.flush();
        }
        br.close();
        bw.close();
        br = new BufferedReader(new FileReader(tempFile));
        bw = new BufferedWriter(new FileWriter(filePathTwo));
        while ((temp = br.readLine()) != null) {
            bw.write(temp);
            bw.newLine();
            bw.flush();
        }
        br.close();
        bw.close();
        new File(tempFile).delete();
    }
    
    static {
        MigratedServerUtil.logger = Logger.getLogger(MigratedServerUtil.class.getName());
        MIGRATED_SERVER_INFO = System.getProperty("server.home") + File.separator + "conf" + File.separator + "migrated-server-info.json";
    }
}
