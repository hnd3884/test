package com.me.mdm.server.datausage.android;

import java.util.Hashtable;
import java.util.List;
import com.adventnet.sym.server.mdm.config.MDMConfigHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayloadHandler;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.mdm.server.datausage.config.DataProfileHandler;

public class AndroidDataProfileHandler implements DataProfileHandler
{
    private static Logger logger;
    public static final String INSTALL_PROFILE_JSON = "install_profile_android.json";
    public static final String REMOVE_PROFILE_JSON = "remove_profile_android.json";
    
    @Override
    public void publishProfileEntity(final Properties collectionProps) {
        Long collectionID = null;
        Long customerID = null;
        final Boolean var4 = false;
        try {
            AndroidDataProfileHandler.logger.log(Level.FINE, "createJSON : collectionProps: ", collectionProps);
            collectionID = ((Hashtable<K, Long>)collectionProps).get("collectionId");
            final Boolean dynamicVariable = Boolean.FALSE;
            customerID = ((Hashtable<K, Long>)collectionProps).get("CUSTOMER_ID");
            String mdmProfileDir = MDMMetaDataUtil.getInstance().checkAndCreateMdmProfileDir(customerID, "profiles", collectionID);
            final String installProfileFileName = mdmProfileDir + File.separator + "install_profile_android.json";
            final String removeProfileFileName = mdmProfileDir + File.separator + "remove_profile_android.json";
            AndroidDataProfileHandler.logger.log(Level.INFO, "createJSON #####  Going to create data profile file in  : {0}", installProfileFileName);
            final AndroidPayloadHandler payloadHdlr = AndroidPayloadHandler.getInstance();
            final List metaDataList = new ArrayList();
            final DeviceCommand installCommand = new DeviceCommand();
            final DeviceCommand removeCommand = new DeviceCommand();
            final Properties installProperties = new Properties();
            final Properties removeProperties = new Properties();
            installCommand.commandFilePath = this.getInstallProfileFilePath(customerID, collectionID);
            removeCommand.commandFilePath = this.getRemoveProfileFilePath(customerID, collectionID);
            final String payloadIdentifier = collectionProps.getProperty("PROFILE_PAYLOAD_IDENTIFIER", null);
            installCommand.commandUUID = payloadHdlr.generateProfile(collectionID, installProfileFileName, "InstallDataProfile");
            removeCommand.commandUUID = payloadHdlr.generateRemoveProfile(payloadIdentifier, collectionID, removeProfileFileName, "RemoveDataProfile");
            installCommand.commandType = "InstallDataProfile";
            removeCommand.commandType = "RemoveDataProfile";
            installCommand.dynamicVariable = dynamicVariable;
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
        catch (final Exception var5) {
            AndroidDataProfileHandler.logger.log(Level.SEVERE, "Exception in createJSON", var5);
        }
    }
    
    private String getInstallProfileFilePath(final Long customerID, final Long collectionID) {
        final String mdmProfileRelativeDirPath = MDMMetaDataUtil.getInstance().mdmProfileRelativeDirPath(customerID, collectionID);
        return mdmProfileRelativeDirPath + File.separator + "install_profile_android.json";
    }
    
    private String getRemoveProfileFilePath(final Long customerID, final Long collectionID) {
        final String mdmProfileRelativeDirPath = MDMMetaDataUtil.getInstance().mdmProfileRelativeDirPath(customerID, collectionID);
        return mdmProfileRelativeDirPath + File.separator + "remove_profile_android.json";
    }
    
    static {
        AndroidDataProfileHandler.logger = Logger.getLogger("MDMConfigLogger");
    }
}
