package com.zoho.security.agent;

import java.io.File;
import com.zoho.security.wafad.WAFAttackDiscovery;
import com.adventnet.iam.security.SecurityUtil;
import com.adventnet.iam.security.SecurityFrameworkUtil;
import java.util.Iterator;
import com.zoho.security.agent.notification.DefaultNotificationReceiver;
import org.json.JSONArray;
import com.zoho.security.appfirewall.AppFirewallPolicyLoader;
import com.zoho.security.eventfw.pojos.log.ZSEC_AFW_RULE_EXPIRY;
import com.zoho.security.appfirewall.AppFirewallInitializer;
import java.util.logging.Level;
import java.util.AbstractMap;
import java.nio.file.Path;
import java.io.IOException;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.pojos.log.ZSEC_APPSENSE_LOCALWRITE;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.logging.Logger;
import org.json.JSONObject;

public class LocalConfigurations
{
    private static boolean isAF_file;
    private static boolean isProperty_file;
    private static boolean isInventory_file;
    private static boolean isWAFAttackDiscoveryInfos_file;
    private static JSONObject configObj;
    public static final String HOME_DIR;
    public static final Logger LOGGER;
    
    public static boolean isAppSenseLocalFileExist(final String fileName, final String tmpFile) {
        final Path tmpFilePath = Paths.get(tmpFile, new String[0]);
        final Path filePath = Paths.get(fileName, new String[0]);
        if (Files.exists(tmpFilePath, LinkOption.NOFOLLOW_LINKS)) {
            try {
                if (Files.exists(filePath, LinkOption.NOFOLLOW_LINKS)) {
                    Files.delete(filePath);
                }
                Files.delete(tmpFilePath);
            }
            catch (final IOException e) {
                ZSEC_APPSENSE_LOCALWRITE.pushExceptionWithMsg("Exception occurred while deleting file", e.getMessage(), (ExecutionTimer)null);
            }
        }
        else if (Files.exists(filePath, LinkOption.NOFOLLOW_LINKS)) {
            return true;
        }
        return false;
    }
    
    static AbstractMap.SimpleEntry<Boolean, String> isTmpFileExists() {
        final StringBuilder tmpFile = new StringBuilder();
        if (isAppSenseTmpFileExist(getAppFirewallFileName(), getAppFirewallTmpFileName())) {
            tmpFile.append(getAppFirewallFileName()).append(",");
        }
        if (isAppSenseTmpFileExist(getPropertyFileName(), getPropertyTmpFileName())) {
            tmpFile.append(getPropertyFileName()).append(",");
        }
        if (tmpFile.length() > 0) {
            return new AbstractMap.SimpleEntry<Boolean, String>(true, tmpFile.substring(0, tmpFile.length() - 1));
        }
        return new AbstractMap.SimpleEntry<Boolean, String>(false, "");
    }
    
    private static boolean isAppSenseTmpFileExist(final String fileName, final String tmpFile) {
        final Path tmpFilePath = Paths.get(tmpFile, new String[0]);
        final Path filePath = Paths.get(fileName, new String[0]);
        if (Files.exists(tmpFilePath, LinkOption.NOFOLLOW_LINKS)) {
            try {
                if (Files.exists(filePath, LinkOption.NOFOLLOW_LINKS)) {
                    Files.delete(filePath);
                }
                Files.delete(tmpFilePath);
                return true;
            }
            catch (final IOException e) {
                ZSEC_APPSENSE_LOCALWRITE.pushExceptionWithMsg("Exception occurred while deleting file", e.getMessage(), (ExecutionTimer)null);
            }
        }
        return false;
    }
    
    public static boolean deletFile(final String fileName) {
        try {
            if (isLocalConfigFileName(fileName)) {
                final Path filePath = Paths.get(fileName, new String[0]);
                if (Files.exists(filePath, LinkOption.NOFOLLOW_LINKS)) {
                    Files.delete(filePath);
                }
                LocalConfigurations.LOGGER.log(Level.INFO, "Deleted Old Configuration \"{0}\" from the home", fileName);
                return true;
            }
        }
        catch (final IOException e) {
            ZSEC_APPSENSE_LOCALWRITE.pushExceptionWithMsg("Exception occurred while deleting file", e.getMessage(), (ExecutionTimer)null);
        }
        return false;
    }
    
    private static boolean isLocalConfigFileName(final String fileName) {
        return fileName.equals(getPropertyOldFileName()) || fileName.equals(getPropertyFileName()) || fileName.equals(getAppFirewallFileName());
    }
    
    public static void saveToFile(final String component) {
        new Thread(new AppSenseFileStore(), component).start();
    }
    
    public static synchronized void saveRuleToFile() {
        if (AppFirewallInitializer.enableExpiryCheck) {
            AppFirewallInitializer.enableExpiryCheck = false;
            ZSEC_AFW_RULE_EXPIRY.pushSuccess("Expired rules removed from the localfile configuration", AppFirewallInitializer.getExpiredRuleIdList(), (ExecutionTimer)null);
            new Thread(new AppSenseFileStore(), Components.COMPONENT.APPFIREWALL.name()).start();
        }
    }
    
    public static String getAppFirewallTmpFileName() {
        return AppSenseConstants.FWRULES_TMPFILE;
    }
    
    public static String getAppFirewallFileName() {
        return AppSenseConstants.FWRULES_FILE;
    }
    
    public static String getPropertyFileName() {
        return AppSenseConstants.PROPERTY_FILE;
    }
    
    public static String getPropertyTmpFileName() {
        return AppSenseConstants.PROPERTY_TMPFILE;
    }
    
    public static String getInventoryFileName() {
        return AppSenseConstants.INVENTORY_FILE;
    }
    
    public static String getInventoryTmpFileName() {
        return AppSenseConstants.INVENTORY_TMPFILE;
    }
    
    public static String getWAFAttackDiscoveryInfoFileName() {
        return AppSenseConstants.WAF_ATTACK_DISCOVERY_INFO_FILE;
    }
    
    public static String getWAFAttackDiscoveryInfoTmpFileName() {
        return AppSenseConstants.WAF_ATTACK_DISCOVERY_INFO_TMPFILE;
    }
    
    public static boolean isExists() {
        if (isAppSenseLocalFileExist(getPropertyOldFileName(), getPropertyOldTmpFileName())) {
            deletFile(getPropertyOldFileName());
            deletFile(getAppFirewallFileName());
            return false;
        }
        if (isAppSenseLocalFileExist(getAppFirewallFileName(), getAppFirewallTmpFileName())) {
            LocalConfigurations.isAF_file = true;
        }
        if (isAppSenseLocalFileExist(getPropertyFileName(), getPropertyTmpFileName())) {
            LocalConfigurations.isProperty_file = true;
        }
        if (isAppSenseLocalFileExist(getInventoryFileName(), getInventoryTmpFileName())) {
            LocalConfigurations.isInventory_file = true;
        }
        if (isAppSenseLocalFileExist(getWAFAttackDiscoveryInfoFileName(), getWAFAttackDiscoveryInfoTmpFileName())) {
            LocalConfigurations.isWAFAttackDiscoveryInfos_file = true;
        }
        LocalConfigurations.LOGGER.log(Level.INFO, " LOCAL FILE EXISTENCE  isAFFile : {0}, isProperty File : {1} , isInventoryFile : {2} , isWafAttackDiscoveryInfosFile : {3} ", new Object[] { LocalConfigurations.isAF_file, LocalConfigurations.isProperty_file, LocalConfigurations.isInventory_file, LocalConfigurations.isWAFAttackDiscoveryInfos_file });
        return LocalConfigurations.isAF_file || LocalConfigurations.isProperty_file || LocalConfigurations.isInventory_file || LocalConfigurations.isWAFAttackDiscoveryInfos_file;
    }
    
    private static String getPropertyOldFileName() {
        return AppSenseConstants.OLD_PROPERTY_FILE;
    }
    
    private static String getPropertyOldTmpFileName() {
        return AppSenseConstants.OLD_PROPERTY_TMPFILE;
    }
    
    static AbstractMap.SimpleEntry<Boolean, Boolean> loadAppFireWallConfigurations() {
        if (LocalConfigurations.isAF_file) {
            return AppFirewallPolicyLoader.loadAFWPolicyFromAppSenseLocalFile();
        }
        return new AbstractMap.SimpleEntry<Boolean, Boolean>(false, false);
    }
    
    public static void notifyServerOnModifiedConfigurations() {
        final JSONArray modifiedData = new JSONArray();
        addChangeIfExists(Components.COMPONENT.HASH, modifiedData);
        addChangeIfExists(Components.COMPONENT.INVENTORY, modifiedData);
        if (modifiedData.length() > 0) {
            saveToFile(Components.COMPONENT.INVENTORY.name());
            AppSenseAgent.notifyConfigChange(modifiedData);
        }
    }
    
    private static boolean addChangeIfExists(final Components.COMPONENT component, final JSONArray modifiedData) {
        boolean isChanged = false;
        for (final Components.COMPONENT_NAME subComp : component.getSubComponents()) {
            if (DefaultNotificationReceiver.getInstance(component).isChangePushEnabled(subComp)) {
                final Object obj = DefaultNotificationReceiver.getInstance(component).getRecentDataOnChange(LocalConfigurations.configObj, subComp);
                if (obj == null) {
                    continue;
                }
                final JSONObject mData = new JSONObject();
                mData.put("COMPONENT", (Object)component.name());
                mData.put("NAME", (Object)subComp.getValue().toUpperCase());
                mData.put("VALUE", obj);
                modifiedData.put((Object)mData);
                LocalConfigurations.configObj.put(subComp.getValue(), obj);
                isChanged = true;
            }
        }
        return isChanged;
    }
    
    public static JSONObject getConfigurationObject() {
        return LocalConfigurations.configObj;
    }
    
    public static boolean loadConfigAndInventory() {
        if (LocalConfigurations.isInventory_file) {
            try {
                final String jsonStr = SecurityFrameworkUtil.readFile(getInventoryFileName());
                if (SecurityUtil.isValid((Object)jsonStr)) {
                    LocalConfigurations.configObj = new JSONArray(jsonStr).getJSONObject(0);
                    return true;
                }
            }
            catch (final Exception e) {
                LocalConfigurations.LOGGER.log(Level.WARNING, " Exception occurred while reading inventory file  : {0}.", e.getMessage());
            }
        }
        return false;
    }
    
    public static boolean loadWAFAttackDiscoveryInfos() {
        if (LocalConfigurations.isWAFAttackDiscoveryInfos_file && SecurityFrameworkUtil.isWAFInstrumentLoaded()) {
            try {
                final String jsonStr = SecurityFrameworkUtil.readFile(getWAFAttackDiscoveryInfoFileName());
                if (SecurityUtil.isValid((Object)jsonStr)) {
                    final JSONObject wafAttackDiscoveryInfosObj = new JSONArray(jsonStr).getJSONObject(0);
                    WAFAttackDiscovery.addLocalCatchWAFAttackDiscoveryInfo(wafAttackDiscoveryInfosObj);
                    return true;
                }
            }
            catch (final Exception e) {
                LocalConfigurations.LOGGER.log(Level.WARNING, " Exception occurred while reading wafinstrument infos file  : {0}.", e.getMessage());
            }
        }
        return false;
    }
    
    static {
        LocalConfigurations.isAF_file = false;
        LocalConfigurations.isProperty_file = false;
        LocalConfigurations.isInventory_file = false;
        LocalConfigurations.isWAFAttackDiscoveryInfos_file = false;
        LocalConfigurations.configObj = new JSONObject();
        HOME_DIR = System.getProperty("user.home") + File.separator + "appsense";
        LOGGER = Logger.getLogger(LocalConfigurations.class.getName());
    }
}
