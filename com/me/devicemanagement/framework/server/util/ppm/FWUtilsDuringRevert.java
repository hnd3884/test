package com.me.devicemanagement.framework.server.util.ppm;

import java.io.IOException;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FWUtilsDuringRevert
{
    protected Logger logger;
    
    public FWUtilsDuringRevert() {
        this.logger = Logger.getLogger(FWUtilsDuringRevert.class.getName());
    }
    
    public void deleteCustomUserEntriesConf() throws IOException {
        this.logger.log(Level.INFO, "Entering into deleteCustomUserEntriesConf ... ");
        final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
        final File customUserEntriesPath = new File(serverHome + File.separator + "conf" + File.separator + "User-conf" + File.separator + "customUserEntries.conf");
        if (customUserEntriesPath.exists()) {
            customUserEntriesPath.delete();
        }
        this.logger.log(Level.INFO, "customUserEntries.conf file deleted...");
    }
    
    public void deleteWebServerConfigurationFile() throws IOException {
        this.logger.log(Level.INFO, "Entering into deleteServerSecurityEntries ... ");
        final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
        final String webServerConfPath = serverHome + File.separator + "conf" + File.separator + "User-conf" + File.separator + "webserver_configurations.json";
        final File file = new File(webServerConfPath);
        if (file.exists()) {
            file.delete();
        }
        this.logger.log(Level.INFO, "server_security.conf file deleted...");
    }
}
