package com.me.mdm.server.migration;

import java.util.regex.Matcher;
import java.util.Iterator;
import java.util.Map;
import java.io.ByteArrayInputStream;
import org.apache.commons.io.IOUtils;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.logging.Level;
import java.io.InputStream;
import java.util.logging.Logger;

public class MDMEdiscoveryMigrationUtil
{
    private static final Logger LOGGER;
    
    public static InputStream handleFileWrite(final InputStream inputStream, final String filePath) throws Exception {
        try {
            MDMEdiscoveryMigrationUtil.LOGGER.log(Level.INFO, "Processing the file path {0}", filePath);
            String payloadData = null;
            final String installProfileRegex = "^mdm\\/profilerepository\\/(\\d+)\\/profiles\\/(\\d+)\\/install_profile.(xml|json)$";
            final String removeProfileRegex = "^mdm\\/profilerepository\\/(\\d+)\\/profiles\\/(\\d+)\\/remove_profile.(xml|json)$";
            final String updateProfileRegex = "^mdm\\/profilerepository\\/(\\d+)\\/profiles\\/(\\d+)\\/update_profile.xml$";
            final String appConfigProfileRegex = "^mdm\\/profilerepository\\/(\\d+)\\/profiles\\/(\\d+)\\/appconfig_profile.xml$";
            final String managedSettingRegex = "^mdm\\/profilerepository\\/(\\d+)\\/profiles\\/(\\d+)\\/install_managed_setting.xml$";
            final String installKioskRegex = "^mdm\\/profilerepository\\/(\\d+)\\/profiles\\/(\\d+)\\/kiosk_restriction_profile.xml$";
            final String removeKioskRegex = "^mdm\\/profilerepository\\/(\\d+)\\/profiles\\/(\\d+)\\/Remove_kiosk_restriction_profile.xml$";
            final String appConfigRegex = "^mdm\\/profilerepository\\/(\\d+)\\/profiles\\/(\\d+)\\/app_configuration.xml$";
            final String scheduleOsUpdateRegex = "^mdm\\/profilerepository\\/(\\d+)\\/profiles\\/(\\d+)\\/schedule_os_update.xml$";
            final String restrictOsUpdateRegex = "^mdm\\/profilerepository\\/(\\d+)\\/profiles\\/(\\d+)\\/restrict_os_update.xml$";
            final String installOsUpdateRegex = "^mdm\\/profilerepository\\/(\\d+)\\/profiles\\/(\\d+)\\/os_update_install.json";
            final String removeOsUpdateRegex = "^mdm\\/profilerepository\\/(\\d+)\\/profiles\\/(\\d+)\\/os_update_remove.json";
            final String chromeInstallOsUpdateRegex = "^mdm\\/profilerepository\\/(\\d+)\\/profiles\\/(\\d+)\\/chrome_os_update_install.json";
            final String chromeRemoveOsUpdateRegex = "^mdm\\/profilerepository\\/(\\d+)\\/profiles\\/(\\d+)\\/chrome_os_update_remove.json";
            final String complianceRegex = "^mdm\\/profilerepository\\/(\\d+)\\/compliance\\/(\\d+)\\/compliance_profile.json";
            final String removeComplianceRegex = "^mdm\\/profilerepository\\/(\\d+)\\/compliance\\/(\\d+)\\/remove_compliance_profile.json";
            final String installDataUsageRegex = "^mdm\\/profilerepository\\/(\\d+)\\/profiles\\/(\\d+)\\/install_profile_android.json";
            final String removeDataUsageRegex = "^mdm\\/profilerepository\\/(\\d+)\\/profiles\\/(\\d+)\\/remove_profile_android.json";
            final String removePasscodeDisableRegex = "^mdm\\/profilerepository\\/(\\d+)\\/profiles\\/(\\d+)\\/remove_passcode_disable_profile.xml$";
            final String passcodeDisableRegex = "^mdm\\/profilerepository\\/(\\d+)\\/profiles\\/(\\d+)\\/passcode_disable_profile.xml$";
            final String passcodeRestrictRegex = "^mdm\\/profilerepository\\/(\\d+)\\/profiles\\/(\\d+)\\/passcode_restrict_profile.xml$";
            final Map<String, MigrationFileRegeneration> fileRegenerationHashMap = new HashMap<String, MigrationFileRegeneration>();
            fileRegenerationHashMap.put(installProfileRegex, new InstallProfileRegeneration());
            fileRegenerationHashMap.put(removeProfileRegex, new RemoveProfileRegeneration());
            fileRegenerationHashMap.put(updateProfileRegex, new UpdateProfileRegeneration());
            fileRegenerationHashMap.put(appConfigProfileRegex, new AppConfigProfileRegeneration());
            fileRegenerationHashMap.put(managedSettingRegex, new ManageSettingFileGeneration());
            fileRegenerationHashMap.put(installKioskRegex, new KioskRestrictionProfileRegeneration());
            fileRegenerationHashMap.put(removeKioskRegex, new RemoveKioskRestrictionProfileRegeneration());
            fileRegenerationHashMap.put(appConfigRegex, new IOSAppConfigurationRegeneration());
            fileRegenerationHashMap.put(restrictOsUpdateRegex, new RestrictOSUpdateRegeneration());
            fileRegenerationHashMap.put(scheduleOsUpdateRegex, new ScheduleOSUpdateRegeneration());
            fileRegenerationHashMap.put(installOsUpdateRegex, new InstallOSUpdateProfileRegeneration());
            fileRegenerationHashMap.put(removeOsUpdateRegex, new RemoveOSUpdateProfileRegeneration());
            fileRegenerationHashMap.put(chromeInstallOsUpdateRegex, new ChromeInstallOSUpdateProfileRegeneration());
            fileRegenerationHashMap.put(chromeRemoveOsUpdateRegex, new ChromeRemoveOSUpdateProfileRegeneration());
            fileRegenerationHashMap.put(complianceRegex, new ComplianceProfileRegeneration());
            fileRegenerationHashMap.put(removeComplianceRegex, new ComplianceProfileRegeneration());
            fileRegenerationHashMap.put(installDataUsageRegex, new InstallDataUsageProfileRegeneration());
            fileRegenerationHashMap.put(removeDataUsageRegex, new RemoveDataUsageProfileRegeneration());
            fileRegenerationHashMap.put(removePasscodeDisableRegex, new RemovePasscodeDisableProfileRegeneration());
            fileRegenerationHashMap.put(passcodeDisableRegex, new PasscodeDisableProfileRegeneration());
            fileRegenerationHashMap.put(passcodeRestrictRegex, new PasscodeDisableProfileRegeneration());
            for (final String fileRegex : fileRegenerationHashMap.keySet()) {
                final Pattern filePattern = Pattern.compile(fileRegex);
                final Matcher fileMatcher = filePattern.matcher(filePath).find() ? filePattern.matcher(filePath) : null;
                if (fileMatcher != null && fileMatcher.find()) {
                    final String input = IOUtils.toString(inputStream);
                    final MigrationFileRegeneration migrationFileRegeneration = fileRegenerationHashMap.get(fileRegex);
                    payloadData = migrationFileRegeneration.generateFile(fileMatcher);
                    break;
                }
            }
            if (payloadData != null) {
                return new ByteArrayInputStream(payloadData.getBytes());
            }
        }
        catch (final Exception ex) {
            MDMEdiscoveryMigrationUtil.LOGGER.log(Level.SEVERE, "Exception while fetching details for profile", ex);
        }
        return inputStream;
    }
    
    static {
        LOGGER = Logger.getLogger(MDMEdiscoveryMigrationUtil.class.getName());
    }
}
