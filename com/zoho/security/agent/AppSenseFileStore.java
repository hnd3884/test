package com.zoho.security.agent;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.nio.file.Paths;
import java.io.IOException;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.pojos.log.ZSEC_APPSENSE_LOCALWRITE;
import java.io.FileWriter;
import java.io.File;
import org.json.JSONObject;
import com.zoho.security.wafad.WAFAttackDiscovery;
import com.zoho.security.agent.notification.PropertyNotification;
import com.zoho.security.appfirewall.AppFirewallInitializer;
import org.json.JSONArray;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppSenseFileStore implements Runnable
{
    private static final Logger LOGGER;
    
    @Override
    public void run() {
        final String threadName = Thread.currentThread().getName();
        AppSenseFileStore.LOGGER.log(Level.INFO, "Background thread \"{0}\" to Store APPSense configuration to local file has been initiated ", threadName);
        if (Components.COMPONENT.INVENTORY.name().equals(threadName)) {
            final JSONObject configObj = LocalConfigurations.getConfigurationObject();
            if (configObj.length() > 0) {
                final JSONArray configArray = new JSONArray();
                configArray.put((Object)configObj);
                save(configArray, AppSenseConstants.INVENTORY_TMPFILE, AppSenseConstants.INVENTORY_FILE);
            }
        }
        else if (Components.COMPONENT.APPFIREWALL.name().equals(threadName)) {
            save(AppFirewallInitializer.getRulesAsJSON(), AppSenseConstants.FWRULES_TMPFILE, AppSenseConstants.FWRULES_FILE);
            AppFirewallInitializer.resetFlag();
        }
        else if (Components.COMPONENT.PROPERTY.name().equals(threadName)) {
            PropertyNotification.saveProperties();
        }
        else if (Components.COMPONENT.ATTACK_DISCOVERY.name().equals(threadName)) {
            final JSONObject wafAttackDiscoveryInfoObj = WAFAttackDiscovery.getWAFAttackDiscoveryInfosAsJSON();
            final JSONArray wafAttackDiscoveryInfosJsonArray = new JSONArray();
            wafAttackDiscoveryInfosJsonArray.put((Object)wafAttackDiscoveryInfoObj);
            save(wafAttackDiscoveryInfosJsonArray, AppSenseConstants.WAF_ATTACK_DISCOVERY_INFO_TMPFILE, AppSenseConstants.WAF_ATTACK_DISCOVERY_INFO_FILE);
        }
    }
    
    public static synchronized void save(final JSONArray rulesJSON, final String tmpFile, final String origFile) {
        if (rulesJSON.length() >= 0) {
            FileWriter fileWriter = null;
            try {
                final File dir = new File(AppSenseConstants.HOME_DIR);
                if (dir.mkdir()) {
                    AppSenseFileStore.LOGGER.log(Level.INFO, "Appsense Directory \"{0}\" created successfully", AppSenseConstants.HOME_DIR);
                }
                fileWriter = new FileWriter(tmpFile);
                fileWriter.write(rulesJSON.toString());
                saveTmpToPersistantFile(tmpFile, origFile);
                AppSenseFileStore.LOGGER.log(Level.INFO, "AppSense configurations written successfully in local file \"{0}\"", origFile);
            }
            catch (final IOException e) {
                ZSEC_APPSENSE_LOCALWRITE.pushException(e.getMessage(), (ExecutionTimer)null);
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    }
                    catch (final IOException e) {
                        ZSEC_APPSENSE_LOCALWRITE.pushExceptionWithMsg("Exception occurred in Finally ", e.getMessage(), (ExecutionTimer)null);
                    }
                }
            }
            finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    }
                    catch (final IOException e2) {
                        ZSEC_APPSENSE_LOCALWRITE.pushExceptionWithMsg("Exception occurred in Finally ", e2.getMessage(), (ExecutionTimer)null);
                    }
                }
            }
        }
    }
    
    public static void saveTmpToPersistantFile(final String tmpFile, final String origFile) {
        final Path sourcePath = Paths.get(tmpFile, new String[0]);
        final Path destinationPath = Paths.get(origFile, new String[0]);
        try {
            Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (final IOException e) {
            ZSEC_APPSENSE_LOCALWRITE.pushExceptionWithMsg("AppSense configuration .tmp to persistant file renaming failed  ", e.getMessage(), (ExecutionTimer)null);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(AppSenseFileStore.class.getName());
    }
}
