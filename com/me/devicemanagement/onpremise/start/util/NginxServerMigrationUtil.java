package com.me.devicemanagement.onpremise.start.util;

import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.io.File;

public class NginxServerMigrationUtil extends ServerMigrationUtil implements ServerMigrationUtilAPI
{
    private static String fs;
    private static String nginxModRewriteConfFilePath;
    private static String nginxModRewriteTemplateFilePath;
    private static String serverMigrateConfFilePath;
    
    public static void generateNginxRedirectConf(final String serverHome) throws Exception {
        final String migrateConfFile = serverHome + File.separator + NginxServerMigrationUtil.SERVER_MIGRATE_CONF_FILE;
        final Properties migrationProps = StartupUtil.getProperties(migrateConfFile);
        final String serverName = migrationProps.getProperty("OldServerFlatName");
        final String serverFqdnName = migrationProps.getProperty("OldServerFQDN");
        final String newServerName = migrationProps.getProperty("NewServerFlatName");
        final String newServerFqdnName = migrationProps.getProperty("NewServerFQDN");
        migrationProps.setProperty("OldServerFlatName", serverName.toLowerCase());
        migrationProps.setProperty("OldServerFQDN", serverFqdnName.toLowerCase());
        migrationProps.setProperty("NewServerFlatName", newServerName.toLowerCase());
        migrationProps.setProperty("NewServerFQDN", newServerFqdnName.toLowerCase());
        try {
            com.me.devicemanagement.framework.start.StartupUtil.findAndReplaceStrings(NginxServerMigrationUtil.nginxModRewriteTemplateFilePath, NginxServerMigrationUtil.nginxModRewriteConfFilePath, migrationProps, "%");
        }
        catch (final Exception e) {
            NginxServerMigrationUtil.logger.log(Level.WARNING, "Exception while generating httpd_mod_rewrite conf.", e);
            throw new Exception("Server migration activation failed.\n\nRetry the operation. If the issue persists, contact support.");
        }
    }
    
    @Override
    public void modifyProductStartupForMigrationEnabled() throws Exception {
        final Properties wsProps = new Properties();
        NginxServerUtils.generateNginxStandaloneConf(wsProps);
        generateNginxRedirectConf(ServerMigrationUtil.getServerHome());
        if (new File(NginxServerMigrationUtil.serverMigrateConfFilePath).exists()) {
            final Properties serverMigrationProps = StartupUtil.getProperties(NginxServerMigrationUtil.serverMigrateConfFilePath);
            serverMigrationProps.setProperty("migrate.server.nginx", "true");
            StartupUtil.storeProperties(serverMigrationProps, NginxServerMigrationUtil.serverMigrateConfFilePath);
            final String nginxStartResult = NginxServerUtils.startNginxServer(ServerMigrationUtil.getServerHome());
            NginxServerMigrationUtil.logger.log(Level.INFO, "Nginx Start Result " + nginxStartResult);
            if (NginxServerUtils.isNginxServerRunning()) {
                NginxServerMigrationUtil.logger.log(Level.INFO, "Nginx Server Started SuccessFully");
            }
            return;
        }
        throw new Exception("Server Migration Conf File is not located in the server. Aborting server migration process");
    }
    
    @Override
    public void modifyProductStartupForMigrationDisabled() throws Exception {
        final File serverMigrateConfFile = new File(NginxServerMigrationUtil.serverMigrateConfFilePath);
        if (serverMigrateConfFile.exists()) {
            serverMigrateConfFile.delete();
        }
        ServerMigrationUtil.clearFileContents(NginxServerMigrationUtil.nginxModRewriteConfFilePath);
    }
    
    static {
        NginxServerMigrationUtil.fs = File.separator;
        NginxServerMigrationUtil.nginxModRewriteConfFilePath = System.getProperty("server.home") + NginxServerMigrationUtil.fs + "nginx" + NginxServerMigrationUtil.fs + "conf" + NginxServerMigrationUtil.fs + "nginx_mod_rewrite.conf";
        NginxServerMigrationUtil.nginxModRewriteTemplateFilePath = System.getProperty("server.home") + NginxServerMigrationUtil.fs + "nginx" + NginxServerMigrationUtil.fs + "conf" + NginxServerMigrationUtil.fs + "nginx_mod_rewrite.conf.template";
        NginxServerMigrationUtil.serverMigrateConfFilePath = System.getProperty("server.home") + NginxServerMigrationUtil.fs + NginxServerMigrationUtil.SERVER_MIGRATE_CONF_FILE;
    }
}
