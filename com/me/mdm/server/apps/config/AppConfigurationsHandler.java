package com.me.mdm.server.apps.config;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayloadHandler;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.Properties;
import com.adventnet.sym.server.mdm.config.MDMConfigHandler;

public class AppConfigurationsHandler extends MDMConfigHandler
{
    @Override
    public Properties persistCollection(final Properties collectionProps) {
        Long collectionID = null;
        Long profileID = null;
        int platformType = -1;
        Long modifiedUserID = null;
        try {
            AppConfigurationsHandler.profileLogger.log(Level.FINE, " App config persistCollection(): collectionProps: ", collectionProps);
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
            profileID = ProfileHandler.addOrUpdateProfile(profileMap);
            ProfileHandler.addOrUpdateRecentPublishedProfileToCollection(profileID, collectionID);
            if (platformType == 1) {
                this.createPList(collectionProps);
            }
            else if (platformType == 2) {
                this.createAndroidJSON(collectionProps);
            }
            ProfileHandler.addOrUpdateProfileCollectionStatus(collectionID, 110);
            AppConfigurationsHandler.profileLogger.log(Level.INFO, "{0}\t\t{1}\t\t{2}\t\t{3}\t\t{4};", new Object[] { profileID, collectionID, "App Config Profile", platformType, "Profile Published" });
        }
        catch (final Exception ex) {
            AppConfigurationsHandler.profileLogger.log(Level.SEVERE, "Exception in persist collection of app configuration handler", ex);
        }
        return collectionProps;
    }
    
    @Override
    public void createAndroidJSON(final Properties collectionProps) {
        Long collectionID = null;
        Long customerID = null;
        try {
            AppConfigurationsHandler.profileLogger.log(Level.FINE, " App config profile createJSON : collectionProps: ", collectionProps);
            collectionID = ((Hashtable<K, Long>)collectionProps).get("collectionId");
            customerID = ((Hashtable<K, Long>)collectionProps).get("CUSTOMER_ID");
            String mdmProfileDir = MDMMetaDataUtil.getInstance().checkAndCreateMdmProfileDir(customerID, "profiles", collectionID);
            final String installProfileFileName = mdmProfileDir + File.separator + "install_profile.json";
            final String removeProfileFileName = mdmProfileDir + File.separator + "remove_profile.json";
            AppConfigurationsHandler.profileLogger.log(Level.INFO, "createJSON #####  Going to create  app config profile file in  : {0}", installProfileFileName);
            final AndroidPayloadHandler payloadHdlr = AndroidPayloadHandler.getInstance();
            final List metaDataList = new ArrayList();
            final DeviceCommand installCommand = new DeviceCommand();
            final DeviceCommand removeCommand = new DeviceCommand();
            final Properties installProperties = new Properties();
            final Properties removeProperties = new Properties();
            installCommand.commandFilePath = this.getInstallProfileFilePath(customerID, collectionID);
            removeCommand.commandFilePath = this.getRemoveProfileFilePath(customerID, collectionID);
            installCommand.commandUUID = payloadHdlr.generateAppConfigurationInstallProfile(collectionProps, installProfileFileName);
            removeCommand.commandUUID = payloadHdlr.generateAppConfigurationRemoveProfile(collectionProps, removeProfileFileName);
            installCommand.commandType = "InstallApplicationConfiguration";
            removeCommand.commandType = "RemoveApplicationConfiguration";
            installCommand.dynamicVariable = Boolean.TRUE;
            installProperties.setProperty("commandUUID", installCommand.commandUUID);
            installProperties.setProperty("commandType", installCommand.commandType);
            installProperties.setProperty("commandFilePath", installCommand.commandFilePath);
            installProperties.setProperty("dynamicVariable", String.valueOf(installCommand.dynamicVariable));
            metaDataList.add(installProperties);
            removeProperties.setProperty("commandUUID", removeCommand.commandUUID);
            removeProperties.setProperty("commandType", removeCommand.commandType);
            removeProperties.setProperty("commandFilePath", removeCommand.commandFilePath);
            removeProperties.setProperty("dynamicVariable", String.valueOf(installCommand.dynamicVariable));
            metaDataList.add(removeProperties);
            final Boolean addConfigCommand = ((Hashtable<K, Boolean>)collectionProps).get("addConfig");
            if (addConfigCommand == null || addConfigCommand) {
                DeviceCommandRepository.getInstance().addCollectionCommand(collectionID, metaDataList);
            }
            mdmProfileDir = MDMMetaDataUtil.getInstance().getMdmProfileFolderPath(customerID, "profiles", collectionID);
            MDMConfigHandler.getInstance().addorUpdateCollectionMetaData(collectionID, mdmProfileDir, "MDM");
        }
        catch (final Exception ex) {
            AppConfigurationsHandler.profileLogger.log(Level.SEVERE, "Exception in createJSON for App config profile", ex);
        }
    }
    
    @Override
    public void createPList(final Properties collectionProps) throws SyMException {
        Long collectionID = null;
        Long customerID = null;
        String commandUUID = null;
        String removeCommandUUID = null;
        try {
            AppConfigurationsHandler.profileLogger.log(Level.FINE, "createPList() -> App config profile : collectionProps: {0}", collectionProps);
            collectionID = ((Hashtable<K, Long>)collectionProps).get("collectionId");
            final List metaDataList = new ArrayList();
            customerID = ((Hashtable<K, Long>)collectionProps).get("CUSTOMER_ID");
            String mdmProfileDir = MDMMetaDataUtil.getInstance().checkAndCreateMdmProfileDir(customerID, "profiles", collectionID);
            final String profileFileName = mdmProfileDir + File.separator + "install_profile.xml";
            final String removeProfileFileName = mdmProfileDir + File.separator + "remove_profile.xml";
            final String mdmProfileRelativeDirPath = MDMMetaDataUtil.getInstance().mdmProfileRelativeDirPath(customerID, collectionID);
            final String installProfileRelativePath = mdmProfileRelativeDirPath + File.separator + "install_profile.xml";
            final String removeProfileRelativePath = mdmProfileRelativeDirPath + File.separator + "remove_profile.xml";
            AppConfigurationsHandler.profileLogger.log(Level.INFO, "persistCollection() -> App config profile #####  Going to create app configuration profile file in  : {0}", profileFileName);
            AppConfigurationsHandler.profileLogger.log(Level.INFO, "createPList() -> App config profile #####  Going to create app config profile file in  : {0}", profileFileName);
            final PayloadHandler payloadHdlr = PayloadHandler.getInstance();
            final DeviceCommand installCommand = new DeviceCommand();
            final DeviceCommand removeCommand = new DeviceCommand();
            commandUUID = payloadHdlr.createInstallIosMultiAppConfigCommand(collectionID, profileFileName);
            removeCommandUUID = payloadHdlr.createRemoveIosMultiAppConfigCommand(collectionID, removeProfileFileName);
            installCommand.commandType = "InstallApplicationConfiguration";
            removeCommand.commandType = "RemoveApplicationConfiguration";
            installCommand.commandUUID = commandUUID;
            installCommand.commandFilePath = installProfileRelativePath;
            installCommand.dynamicVariable = Boolean.TRUE;
            removeCommand.commandUUID = removeCommandUUID;
            removeCommand.commandFilePath = removeProfileRelativePath;
            final Properties installProperties = new Properties();
            installProperties.setProperty("commandUUID", commandUUID);
            installProperties.setProperty("commandType", installCommand.commandType);
            installProperties.setProperty("commandFilePath", installCommand.commandFilePath);
            installProperties.setProperty("dynamicVariable", String.valueOf(installCommand.dynamicVariable));
            final Properties removeProperties = new Properties();
            removeProperties.setProperty("commandUUID", removeCommandUUID);
            removeProperties.setProperty("commandType", removeCommand.commandType);
            removeProperties.setProperty("commandFilePath", removeCommand.commandFilePath);
            removeProperties.setProperty("dynamicVariable", String.valueOf(installCommand.dynamicVariable));
            metaDataList.add(installProperties);
            metaDataList.add(removeProperties);
            DeviceCommandRepository.getInstance().addCollectionCommand(collectionID, metaDataList);
            mdmProfileDir = MDMMetaDataUtil.getInstance().getMdmProfileFolderPath(customerID, "profiles", collectionID);
            mdmProfileDir = mdmProfileDir + File.separator + "install_profile.xml";
            MDMConfigHandler.getInstance().addorUpdateCollectionMetaData(collectionID, mdmProfileDir, "MDM");
            AppConfigurationsHandler.profileLogger.log(Level.INFO, "createPList -> app config profile -> commandUUID : {0}", commandUUID);
        }
        catch (final Exception ex) {
            AppConfigurationsHandler.profileLogger.log(Level.SEVERE, "Exception in createPList for app config profile", ex);
            throw new SyMException(500, ex.getMessage(), ex.getCause());
        }
    }
}
