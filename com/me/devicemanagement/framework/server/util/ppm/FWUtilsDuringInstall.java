package com.me.devicemanagement.framework.server.util.ppm;

import java.util.Hashtable;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.Properties;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FWUtilsDuringInstall
{
    protected Logger logger;
    
    public FWUtilsDuringInstall() {
        this.logger = Logger.getLogger(FWUtilsDuringInstall.class.getName());
    }
    
    public void createCustomUserEntries(final String sanitation_char, final String replace_char) throws Exception {
        this.logger.log(Level.INFO, "Entering into customUserEntries method... ");
        final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
        final String customUserEntriesConfPath = serverHome + File.separator + "conf" + File.separator + "User-conf" + File.separator + "customUserEntries.conf";
        final Properties customUserEntryprops = new Properties();
        ((Hashtable<String, String>)customUserEntryprops).put("export_sanitation_characters", sanitation_char);
        ((Hashtable<String, String>)customUserEntryprops).put("export_sanitation_replacement_char", replace_char);
        FileAccessUtil.storeProperties(customUserEntryprops, customUserEntriesConfPath, false);
        this.logger.log(Level.INFO, "customUserEntries.conf file created ...");
    }
    
    public void createWebServerConfigurationFile(final JSONObject jsonObject) throws Exception {
        this.logger.log(Level.INFO, "Entering into createWebServerConfigurationFile method... ");
        final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
        final String webServerFilePath = serverHome + File.separator + "conf" + File.separator + "User-conf" + File.separator + "webserver_configurations.json";
        FileAccessUtil.writeDataInFile(webServerFilePath, jsonObject.toString());
        this.logger.log(Level.INFO, "webserver_configurations file created ...");
    }
}
