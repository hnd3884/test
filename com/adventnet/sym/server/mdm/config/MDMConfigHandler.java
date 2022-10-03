package com.adventnet.sym.server.mdm.config;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.config.SYMConfigUtil;
import com.adventnet.sym.server.mdm.chrome.payload.ChromePayloadHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayloadHandler;
import com.me.mdm.server.seqcommands.windows.WindowsSeqCmdUtil;
import java.util.Collection;
import com.me.mdm.server.windows.profile.payload.WindowsPayloadHandler;
import org.json.JSONArray;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.encryption.ios.filevault.MDMFilevaultUtils;
import com.me.mdm.server.apps.config.AppConfigPolicyDBHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.profiles.AppleCustomProfileHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.me.mdm.server.config.MDMConfigUtil;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.Properties;
import com.me.mdm.server.apps.appupdatepolicy.AppUpdatePolicyConfigHandler;
import com.me.mdm.api.command.schedule.ScheduleConfigHandler;
import com.me.mdm.server.apps.config.AppConfigurationsHandler;
import com.me.mdm.server.datausage.config.DataUsageConfigHandler;
import java.util.List;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.config.ConfigOperations;

public class MDMConfigHandler implements ConfigOperations
{
    private static MDMConfigHandler handler;
    public static final String INSTALL_PROFILE = "install_profile.xml";
    public static final String REMOVE_PROFILE = "remove_profile.xml";
    public static final String INSTALL_MANAGED_SETTING = "install_managed_setting.xml";
    public static final String SCHEDULE_OS_UPDATE = "schedule_os_update.xml";
    public static final String APP_CONFIGURATION = "app_configuration.xml";
    public static final String INSTALL_PROFILE_JSON = "install_profile.json";
    public static final String REMOVE_PROFILE_JSON = "remove_profile.json";
    public static final String FILEVAULT_PRK_ROTATE_XML = "filevault_personalkey_rotate.xml";
    private String className;
    protected static Logger logger;
    public static Logger profileLogger;
    private static final List<Integer> FILEVAULT_CONFIG_ID;
    
    public MDMConfigHandler() {
        this.className = MDMConfigHandler.class.getName();
    }
    
    @Deprecated
    public static MDMConfigHandler getInstance() {
        if (MDMConfigHandler.handler == null) {
            MDMConfigHandler.handler = new MDMConfigHandler();
        }
        return MDMConfigHandler.handler;
    }
    
    public static MDMConfigHandler getInstance(final Integer profileType) {
        if (profileType != null && profileType == 8) {
            return new DataUsageConfigHandler();
        }
        if (profileType != null && profileType == 10) {
            return new AppConfigurationsHandler();
        }
        if (profileType != null && profileType == 11) {
            return new ScheduleConfigHandler();
        }
        if (profileType != null && profileType == 12) {
            return new AppUpdatePolicyConfigHandler();
        }
        return new MDMConfigHandler();
    }
    
    public Properties persistCollection(final Properties collectionProps) throws SyMException {
        Long collectionID = null;
        Long profileID = null;
        int platformType = -1;
        Long modifiedUserID = null;
        try {
            MDMConfigHandler.logger.log(Level.FINE, "persistCollection(): collectionProps: ", collectionProps);
            collectionID = ((Hashtable<K, Long>)collectionProps).get("collectionId");
            profileID = ((Hashtable<K, Long>)collectionProps).get("PROFILE_ID");
            platformType = ((Hashtable<K, Integer>)collectionProps).get("PLATFORM_TYPE");
            modifiedUserID = ((Hashtable<K, Long>)collectionProps).get("LAST_MODIFIED_BY");
            final Boolean isMovedToTrash = ((Hashtable<K, Boolean>)collectionProps).get("IS_MOVED_TO_TRASH");
            final JSONObject profileMap = new JSONObject();
            profileMap.put("PROFILE_ID", (Object)profileID);
            profileMap.put("LAST_MODIFIED_BY", (Object)modifiedUserID);
            if (isMovedToTrash != null) {
                profileMap.put("IS_MOVED_TO_TRASH", (Object)isMovedToTrash);
            }
            if (collectionProps.containsKey("CREATION_TIME")) {
                profileMap.put("CREATION_TIME", ((Hashtable<K, Object>)collectionProps).get("CREATION_TIME"));
            }
            if (collectionProps.containsKey("LAST_MODIFIED_TIME")) {
                profileMap.put("LAST_MODIFIED_TIME", ((Hashtable<K, Object>)collectionProps).get("LAST_MODIFIED_TIME"));
            }
            profileID = ProfileHandler.addOrUpdateProfile(profileMap);
            final Boolean isAppConfig = ((Hashtable<K, Boolean>)collectionProps).get("APP_CONFIG");
            if (!isAppConfig) {
                ProfileHandler.addOrUpdateRecentPublishedProfileToCollection(profileID, collectionID);
            }
            if (platformType == 1 || platformType == 6 || platformType == 7) {
                this.createPList(collectionProps);
            }
            else if (platformType == 2) {
                this.createAndroidJSON(collectionProps);
            }
            else if (platformType == 3) {
                this.createSyncML(collectionProps);
            }
            else if (platformType == 4) {
                this.createChromeJSON(collectionProps);
            }
            ProfileHandler.addOrUpdateProfileCollectionStatus(collectionID, 110);
            String appAction = "APP_ADDED_TO_REPOSITORY";
            if (((Hashtable<K, Boolean>)collectionProps).get("APP_CONFIG") && !((Hashtable<K, Boolean>)collectionProps).get("addConfig")) {
                appAction = "APP_MODIFIED_IN_REPOSITORY";
            }
            MDMConfigHandler.profileLogger.log(Level.INFO, "{0}\t\t{1}\t\t{2}\t\t{3}\t\t{4};", new Object[] { profileID, collectionID, ((Hashtable<K, Boolean>)collectionProps).get("APP_CONFIG") ? "App" : "Profile", platformType, ((Hashtable<K, Boolean>)collectionProps).get("APP_CONFIG") ? appAction : "PROFILE_PUBLISHED" });
        }
        catch (final Exception ex) {
            SyMLogger.error(MDMConfigHandler.logger, this.className, "persistCollection", "Exception", (Throwable)ex);
            throw new SyMException(((SyMException)ex).getErrorCode(), ex.getMessage(), ex.getCause());
        }
        return collectionProps;
    }
    
    public boolean isMobileConfig(final Properties collectionProps) {
        boolean isMobileConfig = false;
        if (collectionProps.get("isMobileConfig") != null) {
            isMobileConfig = ((Hashtable<K, Boolean>)collectionProps).get("isMobileConfig");
        }
        return isMobileConfig;
    }
    
    public void createPList(final Properties collectionProps) throws SyMException {
        Long collectionID = null;
        Long customerID = null;
        String commandUUID = null;
        String removeCommandUUID = null;
        Boolean isAppPolicy = false;
        final Boolean isSubCommandAdded = false;
        Integer profileType = 1;
        if (collectionProps.containsKey("PROFILE_TYPE")) {
            profileType = ((Hashtable<K, Integer>)collectionProps).get("PROFILE_TYPE");
        }
        Long profileId = null;
        try {
            MDMConfigHandler.logger.log(Level.FINE, "createPList(): collectionProps: {0}", collectionProps);
            collectionID = ((Hashtable<K, Long>)collectionProps).get("collectionId");
            isAppPolicy = ((Hashtable<K, Boolean>)collectionProps).get("APP_CONFIG");
            profileId = ((Hashtable<K, Long>)collectionProps).get("PROFILE_ID");
            final List metaDataList = new ArrayList();
            final List configIdList = MDMConfigUtil.getConfigIds(collectionID);
            Boolean dynamicVariable = Boolean.FALSE;
            if (configIdList.contains(new Integer(177)) || configIdList.contains(new Integer(176)) || configIdList.contains(new Integer(774)) || configIdList.contains(new Integer(766)) || configIdList.contains(new Integer(174)) || configIdList.contains(new Integer(175)) || configIdList.contains(new Integer(768)) || configIdList.contains(new Integer(184)) || configIdList.contains(new Integer(179)) || configIdList.contains(new Integer(180)) || configIdList.contains(new Integer(516)) || configIdList.contains(new Integer(773)) || configIdList.contains(new Integer(181)) || configIdList.contains(new Integer(188)) || configIdList.contains(new Integer(771)) || configIdList.contains(new Integer(187)) || configIdList.contains(new Integer(520)) || configIdList.contains(new Integer(521)) || configIdList.contains(new Integer(756)) || configIdList.contains(new Integer(522)) || configIdList.contains(new Integer(178)) || configIdList.contains(new Integer(182)) || configIdList.contains(new Integer(525)) || configIdList.contains(new Integer(767)) || configIdList.contains(new Integer(515)) || configIdList.contains(new Integer(772)) || configIdList.contains(new Integer(770))) {
                dynamicVariable = Boolean.TRUE;
            }
            customerID = ((Hashtable<K, Long>)collectionProps).get("CUSTOMER_ID");
            String mdmProfileDir = MDMMetaDataUtil.getInstance().checkAndCreateMdmProfileDir(customerID, "profiles", collectionID);
            final String profileFileName = mdmProfileDir + File.separator + "install_profile.xml";
            final String removeProfileFileName = mdmProfileDir + File.separator + "remove_profile.xml";
            final String mdmProfileRelativeDirPath = MDMMetaDataUtil.getInstance().mdmProfileRelativeDirPath(customerID, collectionID);
            final String installProfileRelativePath = mdmProfileRelativeDirPath + File.separator + "install_profile.xml";
            final String removeProfileRelativePath = mdmProfileRelativeDirPath + File.separator + "remove_profile.xml";
            MDMConfigHandler.logger.log(Level.INFO, "persistCollection() #####  Going to create profile file in  : {0}", profileFileName);
            MDMConfigHandler.logger.log(Level.INFO, "createPList() #####  Going to create profile file in  : {0}", profileFileName);
            final PayloadHandler payloadHdlr = PayloadHandler.getInstance();
            final DeviceCommand installCommand = new DeviceCommand();
            final DeviceCommand removeCommand = new DeviceCommand();
            final boolean isCustomCommand = new AppleCustomProfileHandler().isCustomCommandConfiguredForCollection(collectionID);
            boolean installProfileNeeded = true;
            final String generateInstallProfile = String.valueOf(((Hashtable<K, Object>)collectionProps).get("installprofileneeded"));
            if (!MDMStringUtils.isEmpty(generateInstallProfile)) {
                installProfileNeeded = Boolean.parseBoolean(generateInstallProfile);
            }
            boolean removeProfileNeeded = true;
            final String generateRemoveProfile = String.valueOf(((Hashtable<K, Object>)collectionProps).get("removeprofileneeded"));
            if (!MDMStringUtils.isEmpty(generateRemoveProfile)) {
                removeProfileNeeded = Boolean.parseBoolean(generateRemoveProfile);
            }
            if (!isAppPolicy) {
                if (profileType == 6) {
                    commandUUID = payloadHdlr.generateAccountConfigProfile(collectionID, profileFileName);
                }
                else if (isCustomCommand) {
                    commandUUID = payloadHdlr.generateCustomCommand(collectionID, profileFileName);
                }
                else if (installProfileNeeded) {
                    commandUUID = payloadHdlr.generateProfile(collectionID, profileFileName);
                }
                final String payloadIdentifier = ((Hashtable<K, String>)collectionProps).get("PROFILE_PAYLOAD_IDENTIFIER");
                if (removeProfileNeeded) {
                    removeCommandUUID = payloadHdlr.generateRemoveProfile(payloadIdentifier, collectionID, removeProfileFileName);
                }
                installCommand.commandType = "InstallProfile";
                removeCommand.commandType = "RemoveProfile";
                installCommand.commandUUID = commandUUID;
                installCommand.commandFilePath = installProfileRelativePath;
                installCommand.dynamicVariable = dynamicVariable;
                removeCommand.commandUUID = removeCommandUUID;
                removeCommand.commandFilePath = removeProfileRelativePath;
                if (installProfileNeeded) {
                    final Properties installProperties = new Properties();
                    installProperties.setProperty("commandUUID", commandUUID);
                    installProperties.setProperty("commandType", installCommand.commandType);
                    installProperties.setProperty("commandFilePath", installCommand.commandFilePath);
                    installProperties.setProperty("dynamicVariable", String.valueOf(installCommand.dynamicVariable));
                    metaDataList.add(installProperties);
                }
                if (removeProfileNeeded) {
                    final Properties removeProperties = new Properties();
                    removeProperties.setProperty("commandUUID", removeCommandUUID);
                    removeProperties.setProperty("commandType", removeCommand.commandType);
                    removeProperties.setProperty("commandFilePath", removeCommand.commandFilePath);
                    removeProperties.setProperty("dynamicVariable", String.valueOf(installCommand.dynamicVariable));
                    metaDataList.add(removeProperties);
                }
            }
            else {
                final Boolean hasAppConfiguration = ((Hashtable<K, Boolean>)collectionProps).get("hasAppConfiguration") && AppConfigPolicyDBHandler.getInstance().isConfigurationApplicableForApp(collectionID);
                final JSONArray commandJSON = payloadHdlr.generateAppProfile(collectionID, mdmProfileRelativeDirPath, mdmProfileDir, hasAppConfiguration);
                for (int i = 0; i < commandJSON.length(); ++i) {
                    final JSONObject details = commandJSON.getJSONObject(i);
                    final Properties properties = new Properties();
                    properties.setProperty("commandUUID", String.valueOf(details.get("commandUUID")));
                    properties.setProperty("commandType", String.valueOf(details.get("commandType")));
                    properties.setProperty("commandFilePath", String.valueOf(details.get("commandFilePath")));
                    properties.setProperty("dynamicVariable", String.valueOf(Boolean.TRUE));
                    if (details.has("priority")) {
                        properties.setProperty("priority", String.valueOf(details.get("priority")));
                    }
                    metaDataList.add(properties);
                }
            }
            boolean seqNeeded = true;
            final String generateSeq = String.valueOf(((Hashtable<K, Object>)collectionProps).get("seqNeeded"));
            if (!MDMStringUtils.isEmpty(generateSeq)) {
                seqNeeded = Boolean.parseBoolean(generateSeq);
            }
            if (ProfileUtil.hasIOSManagedSettingConfigID(configIdList) && seqNeeded) {
                final String settingFileName = mdmProfileDir + File.separator + "install_managed_setting.xml";
                final String settingsCommandUUID = payloadHdlr.createManagedSettingCommandXML(collectionID, settingFileName);
                if (settingsCommandUUID != null) {
                    final String installManagedSettingRelativePath = mdmProfileRelativeDirPath + File.separator + "install_managed_setting.xml";
                    final Properties installSettingProperties = new Properties();
                    installSettingProperties.setProperty("commandUUID", settingsCommandUUID);
                    installSettingProperties.setProperty("commandType", "InstallManagedSettings");
                    installSettingProperties.setProperty("commandFilePath", installManagedSettingRelativePath);
                    installSettingProperties.setProperty("dynamicVariable", String.valueOf(Boolean.FALSE));
                    metaDataList.add(installSettingProperties);
                }
            }
            if (ProfileUtil.getProfileType(((Hashtable<K, Long>)collectionProps).get("PROFILE_ID")) == 3) {
                final String cmdFileName = mdmProfileDir + File.separator + "schedule_os_update.xml";
                final String osUpdateCmdUUID = payloadHdlr.createScheduleOSUpdateCommandXML(collectionID, cmdFileName);
                final String cmdRelPath = mdmProfileRelativeDirPath + File.separator + "schedule_os_update.xml";
                final Properties osUpdateProps = new Properties();
                osUpdateProps.setProperty("commandUUID", osUpdateCmdUUID);
                osUpdateProps.setProperty("commandType", "ScheduleOSUpdate");
                osUpdateProps.setProperty("commandFilePath", cmdRelPath);
                osUpdateProps.setProperty("dynamicVariable", String.valueOf(Boolean.TRUE));
                metaDataList.add(osUpdateProps);
            }
            if (ProfileUtil.containsConfigIDs(MDMConfigHandler.FILEVAULT_CONFIG_ID, configIdList)) {
                final String commandXMLPath = MDMFilevaultUtils.getFilevaultPersonalRecoveryKeyPath(customerID, collectionID);
                payloadHdlr.createFilevaultPersonalRecoveryKeyCommandXML(collectionID, commandXMLPath);
            }
            DeviceCommandRepository.getInstance().addCollectionCommand(collectionID, metaDataList);
            mdmProfileDir = MDMMetaDataUtil.getInstance().getMdmProfileFolderPath(customerID, "profiles", collectionID);
            mdmProfileDir = mdmProfileDir + File.separator + "install_profile.xml";
            getInstance().addorUpdateCollectionMetaData(collectionID, mdmProfileDir, "MDM");
            if (seqNeeded) {
                IOSSeqCmdUtil.getInstance().addSeqCmd(configIdList, collectionID, customerID, profileId, null);
            }
            MDMConfigHandler.logger.log(Level.INFO, "createPList -> commandUUID : {0}", commandUUID);
        }
        catch (final Exception ex) {
            MDMConfigHandler.logger.log(Level.SEVERE, "Exception in createPList", ex);
            throw new SyMException(500, ex.getMessage(), ex.getCause());
        }
    }
    
    private void createSyncML(final Properties collectionProps) {
        Long collectionID = null;
        Long customerID = null;
        Boolean isAppPolicy = false;
        try {
            MDMConfigHandler.logger.log(Level.FINE, "createSyncML : collectionProps: {0}", collectionProps);
            collectionID = ((Hashtable<K, Long>)collectionProps).get("collectionId");
            isAppPolicy = ((Hashtable<K, Boolean>)collectionProps).get("APP_CONFIG");
            final Boolean isMSIApp = ((Hashtable<K, Boolean>)collectionProps).get("isMSIApp");
            final List configIdList = MDMConfigUtil.getConfigIds(collectionID);
            Boolean dynamicVariable = Boolean.FALSE;
            if (configIdList.contains(new Integer(602)) || (configIdList.contains(new Integer(609)) || configIdList.contains(605)) || configIdList.contains(new Integer(603)) || configIdList.contains(new Integer(604)) || configIdList.contains(new Integer(606)) || configIdList.contains(new Integer(608)) || configIdList.contains(new Integer(610)) || configIdList.contains(new Integer(611)) || configIdList.contains(new Integer(612)) || configIdList.contains(new Integer(607))) {
                dynamicVariable = Boolean.TRUE;
            }
            customerID = ((Hashtable<K, Long>)collectionProps).get("CUSTOMER_ID");
            String mdmProfileDir = MDMMetaDataUtil.getInstance().checkAndCreateMdmProfileDir(customerID, "profiles", collectionID);
            final String installProfileFileName = mdmProfileDir + File.separator + "install_profile.xml";
            final String removeProfileFileName = mdmProfileDir + File.separator + "remove_profile.xml";
            MDMConfigHandler.logger.log(Level.INFO, "createSyncML #####  Going to create profile file in  : {0}", installProfileFileName);
            final WindowsPayloadHandler payloadHdlr = WindowsPayloadHandler.getInstance();
            final List metaDataList = new ArrayList();
            final DeviceCommand installCommand = new DeviceCommand();
            final DeviceCommand removeCommand = new DeviceCommand();
            boolean installProfileNeeded = true;
            final String generateInstallProfile = String.valueOf(((Hashtable<K, Object>)collectionProps).get("installprofileneeded"));
            if (!MDMStringUtils.isEmpty(generateInstallProfile)) {
                installProfileNeeded = Boolean.parseBoolean(generateInstallProfile);
            }
            boolean removeProfileNeeded = true;
            final String generateRemoveProfile = String.valueOf(((Hashtable<K, Object>)collectionProps).get("removeprofileneeded"));
            if (!MDMStringUtils.isEmpty(generateRemoveProfile)) {
                removeProfileNeeded = Boolean.parseBoolean(generateRemoveProfile);
            }
            final Properties installProperties = new Properties();
            final Properties removeProperties = new Properties();
            final String mdmProfileRelativeDirPath = MDMMetaDataUtil.getInstance().mdmProfileRelativeDirPath(customerID, collectionID);
            if (!isAppPolicy) {
                installCommand.commandFilePath = mdmProfileRelativeDirPath + File.separator + "install_profile.xml";
                removeCommand.commandFilePath = mdmProfileRelativeDirPath + File.separator + "remove_profile.xml";
                final String payloadIdentifier = collectionProps.getProperty("PROFILE_PAYLOAD_IDENTIFIER", null);
                if (installProfileNeeded) {
                    installCommand.commandUUID = payloadHdlr.generateInstallProfile(collectionID, installProfileFileName);
                }
                if (removeProfileNeeded) {
                    removeCommand.commandUUID = payloadHdlr.generateRemoveProfile(payloadIdentifier, collectionID, removeProfileFileName);
                }
                installCommand.commandType = "InstallProfile";
                removeCommand.commandType = "RemoveProfile";
                installCommand.dynamicVariable = dynamicVariable;
                if (installProfileNeeded) {
                    installProperties.setProperty("commandUUID", installCommand.commandUUID);
                    installProperties.setProperty("commandType", installCommand.commandType);
                    installProperties.setProperty("commandFilePath", installCommand.commandFilePath);
                    installProperties.setProperty("dynamicVariable", String.valueOf(installCommand.dynamicVariable));
                    metaDataList.add(installProperties);
                }
                if (removeProfileNeeded) {
                    removeProperties.setProperty("commandUUID", removeCommand.commandUUID);
                    removeProperties.setProperty("commandType", removeCommand.commandType);
                    removeProperties.setProperty("commandFilePath", removeCommand.commandFilePath);
                    removeProperties.setProperty("dynamicVariable", String.valueOf(installCommand.dynamicVariable));
                    metaDataList.add(removeProperties);
                }
            }
            else {
                final JSONObject inputJSON = new JSONObject().put("collectionID", (Object)collectionID).put("absoluteParentDirectory", (Object)mdmProfileDir).put("relativeParentDirectory", (Object)mdmProfileRelativeDirPath);
                final JSONArray jsonObject = payloadHdlr.generateAppProfiles(inputJSON);
                for (int i = 0; i < jsonObject.length(); ++i) {
                    final JSONObject details = jsonObject.getJSONObject(i);
                    final Properties properties = new Properties();
                    properties.setProperty("commandUUID", String.valueOf(details.get("commandUUID")));
                    properties.setProperty("commandType", String.valueOf(details.get("commandType")));
                    properties.setProperty("commandFilePath", String.valueOf(details.get("commandFilePath")));
                    properties.setProperty("dynamicVariable", String.valueOf(Boolean.TRUE));
                    metaDataList.add(properties);
                }
            }
            final Boolean addConfigCommand = ((Hashtable<K, Boolean>)collectionProps).get("addConfig");
            if (addConfigCommand == null || addConfigCommand) {
                DeviceCommandRepository.getInstance().addCollectionCommand(collectionID, metaDataList);
            }
            mdmProfileDir = MDMMetaDataUtil.getInstance().getMdmProfileFolderPath(customerID, "profiles", collectionID);
            getInstance().addorUpdateCollectionMetaData(collectionID, mdmProfileDir, "MDM");
            final JSONObject seqCmdParams = new JSONObject();
            seqCmdParams.put("CollectionID", (Object)collectionID);
            seqCmdParams.put("isAppPolicy", (Object)isAppPolicy);
            seqCmdParams.put("configIDList", (Object)new JSONArray((Collection)configIdList));
            WindowsSeqCmdUtil.getInstance().addSeqCommand(seqCmdParams);
        }
        catch (final Exception ex) {
            MDMConfigHandler.logger.log(Level.SEVERE, "Exception in createSyncML ", ex);
        }
    }
    
    public void createAndroidJSON(final Properties collectionProps) {
        Long collectionID = null;
        Long customerID = null;
        Boolean isAppPolicy = false;
        try {
            MDMConfigHandler.logger.log(Level.FINE, "createJSON : collectionProps: ", collectionProps);
            collectionID = ((Hashtable<K, Long>)collectionProps).get("collectionId");
            isAppPolicy = ((Hashtable<K, Boolean>)collectionProps).get("APP_CONFIG");
            final List configIdList = MDMConfigUtil.getConfigIds(collectionID);
            Boolean dynamicVariable = Boolean.FALSE;
            if (configIdList.contains(553) || configIdList.contains(554) || configIdList.contains(556) || configIdList.contains(562) || configIdList.contains(564) || configIdList.contains(560) || configIdList.contains(567) || configIdList.contains(566) || configIdList.contains(185) || configIdList.contains(557) || configIdList.contains(559) || configIdList.contains(555)) {
                dynamicVariable = Boolean.TRUE;
            }
            customerID = ((Hashtable<K, Long>)collectionProps).get("CUSTOMER_ID");
            String mdmProfileDir = MDMMetaDataUtil.getInstance().checkAndCreateMdmProfileDir(customerID, "profiles", collectionID);
            final String installProfileFileName = mdmProfileDir + File.separator + "install_profile.json";
            final String removeProfileFileName = mdmProfileDir + File.separator + "remove_profile.json";
            MDMConfigHandler.logger.log(Level.INFO, "createJSON #####  Going to create profile file in  : {0}", installProfileFileName);
            final AndroidPayloadHandler payloadHdlr = AndroidPayloadHandler.getInstance();
            final List metaDataList = new ArrayList();
            final DeviceCommand installCommand = new DeviceCommand();
            final DeviceCommand removeCommand = new DeviceCommand();
            boolean installProfileNeeded = true;
            final String generateInstallProfile = String.valueOf(((Hashtable<K, Object>)collectionProps).get("installprofileneeded"));
            if (!MDMStringUtils.isEmpty(generateInstallProfile)) {
                installProfileNeeded = Boolean.parseBoolean(generateInstallProfile);
            }
            boolean removeProfileNeeded = true;
            final String generateRemoveProfile = String.valueOf(((Hashtable<K, Object>)collectionProps).get("removeprofileneeded"));
            if (!MDMStringUtils.isEmpty(generateRemoveProfile)) {
                removeProfileNeeded = Boolean.parseBoolean(generateRemoveProfile);
            }
            final Properties installProperties = new Properties();
            final Properties removeProperties = new Properties();
            installCommand.commandFilePath = this.getInstallProfileFilePath(customerID, collectionID);
            removeCommand.commandFilePath = this.getRemoveProfileFilePath(customerID, collectionID);
            if (isAppPolicy) {
                installCommand.commandUUID = payloadHdlr.generateInstallAppProfile(collectionProps, installProfileFileName);
                removeCommand.commandUUID = payloadHdlr.generateRemoveAppProfile(collectionProps, removeProfileFileName);
                installCommand.commandType = "InstallApplication";
                removeCommand.commandType = "RemoveApplication";
            }
            else {
                final String payloadIdentifier = collectionProps.getProperty("PROFILE_PAYLOAD_IDENTIFIER", null);
                if (installProfileNeeded) {
                    installCommand.commandUUID = payloadHdlr.generateProfile(collectionID, installProfileFileName);
                }
                if (removeProfileNeeded) {
                    removeCommand.commandUUID = payloadHdlr.generateRemoveProfile(payloadIdentifier, collectionID, removeProfileFileName);
                }
                installCommand.commandType = "InstallProfile";
                removeCommand.commandType = "RemoveProfile";
                installCommand.dynamicVariable = dynamicVariable;
            }
            if (isAppPolicy || installProfileNeeded) {
                installProperties.setProperty("commandUUID", installCommand.commandUUID);
                installProperties.setProperty("commandType", installCommand.commandType);
                installProperties.setProperty("commandFilePath", installCommand.commandFilePath);
                installProperties.setProperty("dynamicVariable", String.valueOf(installCommand.dynamicVariable));
                metaDataList.add(installProperties);
            }
            if (isAppPolicy || removeProfileNeeded) {
                removeProperties.setProperty("commandUUID", removeCommand.commandUUID);
                removeProperties.setProperty("commandType", removeCommand.commandType);
                removeProperties.setProperty("commandFilePath", removeCommand.commandFilePath);
                removeProperties.setProperty("dynamicVariable", String.valueOf(installCommand.dynamicVariable));
                metaDataList.add(removeProperties);
            }
            final Boolean addConfigCommand = ((Hashtable<K, Boolean>)collectionProps).get("addConfig");
            if (addConfigCommand == null || addConfigCommand) {
                DeviceCommandRepository.getInstance().addCollectionCommand(collectionID, metaDataList);
            }
            final HashMap seqCmdParams = new HashMap();
            seqCmdParams.put("ConfigIDList", configIdList);
            seqCmdParams.put("CollectionID", collectionID);
            mdmProfileDir = MDMMetaDataUtil.getInstance().getMdmProfileFolderPath(customerID, "profiles", collectionID);
            getInstance().addorUpdateCollectionMetaData(collectionID, mdmProfileDir, "MDM");
        }
        catch (final Exception ex) {
            MDMConfigHandler.logger.log(Level.SEVERE, "Exception in createJSON", ex);
        }
    }
    
    private void createChromeJSON(final Properties collectionProps) {
        try {
            MDMConfigHandler.logger.log(Level.FINE, "createJSON : collectionProps: ", collectionProps);
            final Long collectionID = ((Hashtable<K, Long>)collectionProps).get("collectionId");
            final Boolean isAppPolicy = ((Hashtable<K, Boolean>)collectionProps).get("APP_CONFIG");
            final List configIdList = MDMConfigUtil.getConfigIds(collectionID);
            Boolean dynamicVariable = Boolean.FALSE;
            if (configIdList.contains(705) || configIdList.contains(704) || configIdList.contains(702) || configIdList.contains(701) || configIdList.contains(703)) {
                dynamicVariable = Boolean.TRUE;
            }
            final Long customerID = ((Hashtable<K, Long>)collectionProps).get("CUSTOMER_ID");
            String mdmProfileDir = MDMMetaDataUtil.getInstance().checkAndCreateMdmProfileDir(customerID, "profiles", collectionID);
            final String installProfileFileName = mdmProfileDir + File.separator + "install_profile.json";
            final String removeProfileFileName = mdmProfileDir + File.separator + "remove_profile.json";
            MDMConfigHandler.logger.log(Level.INFO, "createJSON #####  Going to create profile file in  : {0}", installProfileFileName);
            final ChromePayloadHandler payloadHdlr = ChromePayloadHandler.getInstance();
            final List metaDataList = new ArrayList();
            final DeviceCommand installCommand = new DeviceCommand();
            final DeviceCommand removeCommand = new DeviceCommand();
            boolean installProfileNeeded = true;
            final String generateInstallProfile = String.valueOf(((Hashtable<K, Object>)collectionProps).get("installprofileneeded"));
            if (!MDMStringUtils.isEmpty(generateInstallProfile)) {
                installProfileNeeded = Boolean.parseBoolean(generateInstallProfile);
            }
            boolean removeProfileNeeded = true;
            final String generateRemoveProfile = String.valueOf(((Hashtable<K, Object>)collectionProps).get("removeprofileneeded"));
            if (!MDMStringUtils.isEmpty(generateRemoveProfile)) {
                removeProfileNeeded = Boolean.parseBoolean(generateRemoveProfile);
            }
            final Properties installProperties = new Properties();
            final Properties removeProperties = new Properties();
            installCommand.commandFilePath = this.getInstallProfileFilePath(customerID, collectionID);
            removeCommand.commandFilePath = this.getRemoveProfileFilePath(customerID, collectionID);
            if (isAppPolicy) {
                installCommand.commandUUID = payloadHdlr.generateInstallAppProfile(collectionProps, installProfileFileName);
                removeCommand.commandUUID = payloadHdlr.generateRemoveAppProfile(collectionProps, removeProfileFileName);
                installCommand.commandType = "InstallApplication";
                removeCommand.commandType = "RemoveApplication";
            }
            else {
                final String payloadIdentifier = collectionProps.getProperty("PROFILE_PAYLOAD_IDENTIFIER", null);
                if (installProfileNeeded) {
                    installCommand.commandUUID = payloadHdlr.generateProfile(collectionID, installProfileFileName);
                }
                if (removeProfileNeeded) {
                    removeCommand.commandUUID = payloadHdlr.generateRemoveProfile(payloadIdentifier, collectionID, removeProfileFileName);
                }
                installCommand.commandType = "InstallProfile";
                removeCommand.commandType = "RemoveProfile";
                installCommand.dynamicVariable = dynamicVariable;
            }
            if (isAppPolicy || installProfileNeeded) {
                installProperties.setProperty("commandUUID", installCommand.commandUUID);
                installProperties.setProperty("commandType", installCommand.commandType);
                installProperties.setProperty("commandFilePath", installCommand.commandFilePath);
                installProperties.setProperty("dynamicVariable", String.valueOf(installCommand.dynamicVariable));
                metaDataList.add(installProperties);
            }
            if (isAppPolicy || removeProfileNeeded) {
                removeProperties.setProperty("commandUUID", removeCommand.commandUUID);
                removeProperties.setProperty("commandType", removeCommand.commandType);
                removeProperties.setProperty("commandFilePath", removeCommand.commandFilePath);
                removeProperties.setProperty("dynamicVariable", String.valueOf(installCommand.dynamicVariable));
                metaDataList.add(removeProperties);
            }
            final Boolean addConfigCommand = ((Hashtable<K, Boolean>)collectionProps).get("addConfig");
            if (addConfigCommand == null || addConfigCommand) {
                DeviceCommandRepository.getInstance().addCollectionCommand(collectionID, metaDataList);
            }
            mdmProfileDir = MDMMetaDataUtil.getInstance().getMdmProfileFolderPath(customerID, "profiles", collectionID);
            getInstance().addorUpdateCollectionMetaData(collectionID, mdmProfileDir, "MDM");
        }
        catch (final Exception ex) {
            MDMConfigHandler.logger.log(Level.SEVERE, "Exception in createChromeJSON", ex);
        }
    }
    
    public void addorUpdateCollectionMetaData(final Long collectionId, final String collectionPath, final String domainNBName) throws SyMException {
        SYMConfigUtil.addorUpdateCollectionMetaData(collectionId, collectionPath, domainNBName);
    }
    
    public Properties persistAndDeployCollection(final Properties collectionProps) throws SyMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Properties persistModifiedCollection(final Properties collectionProps) throws SyMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Properties persistAndDeployModifiedCollection(final Properties collectionProps) throws SyMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Properties deployCollection(final Long collectionId, final String userName) throws SyMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Properties deleteCollection(final Long collectionId) throws SyMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Properties suspendCollection(final Long collectionId) throws SyMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Properties suspendCollection(final Long collectionId, final Long userID) throws SyMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Properties resumeCollection(final Long collectionId) throws SyMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Properties resumeCollection(final Long collectionId, final Long userID) throws SyMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void moveCollectionsToTrash(final List<Long> collectionIds, final Long userID) throws SyMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected String getInstallProfileFilePath(final Long customerID, final Long collectionID) {
        final String mdmProfileRelativeDirPath = MDMMetaDataUtil.getInstance().mdmProfileRelativeDirPath(customerID, collectionID);
        return mdmProfileRelativeDirPath + File.separator + "install_profile.json";
    }
    
    protected String getRemoveProfileFilePath(final Long customerID, final Long collectionID) {
        final String mdmProfileRelativeDirPath = MDMMetaDataUtil.getInstance().mdmProfileRelativeDirPath(customerID, collectionID);
        return mdmProfileRelativeDirPath + File.separator + "remove_profile.json";
    }
    
    public void republishProfileBasedOnPlatform(final int platformType, final Properties collectionProps) throws SyMException {
        if (platformType == 1 || platformType == 6 || platformType == 7) {
            this.createPList(collectionProps);
        }
        else if (platformType == 2) {
            this.createAndroidJSON(collectionProps);
        }
        else if (platformType == 3) {
            this.createSyncML(collectionProps);
        }
        else if (platformType == 4) {
            this.createChromeJSON(collectionProps);
        }
    }
    
    static {
        MDMConfigHandler.handler = null;
        MDMConfigHandler.logger = Logger.getLogger("MDMConfigLogger");
        MDMConfigHandler.profileLogger = Logger.getLogger("MDMProfileConfigLogger");
        FILEVAULT_CONFIG_ID = new ArrayList<Integer>() {
            {
                this.add(770);
            }
        };
    }
}
