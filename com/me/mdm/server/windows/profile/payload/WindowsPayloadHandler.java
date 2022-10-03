package com.me.mdm.server.windows.profile.payload;

import java.io.File;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.windows.profile.payload.transform.DO2WindowsPayloadHandler;
import com.me.mdm.server.config.MDMConfigUtil;
import com.me.mdm.server.config.MDMCollectionUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WindowsPayloadHandler
{
    private static WindowsPayloadHandler pHandler;
    private Logger logger;
    
    public WindowsPayloadHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public static WindowsPayloadHandler getInstance() {
        if (WindowsPayloadHandler.pHandler == null) {
            WindowsPayloadHandler.pHandler = new WindowsPayloadHandler();
        }
        return WindowsPayloadHandler.pHandler;
    }
    
    public String generateInstallProfile(final Long collectionID, final String profileFilePath) {
        String commandUUID = null;
        try {
            final String payloadData = this.getWindowsInstallProfilePayloadData(collectionID);
            this.logger.log(Level.INFO, "Profile payloadData for collection ID -- {0} \n\n{1}", new Object[] { collectionID, payloadData });
            ApiFactoryProvider.getFileAccessAPI().writeFile(profileFilePath, payloadData.getBytes());
            this.logger.log(Level.INFO, "Profile file {0} has been genesrated successfully", profileFilePath);
            commandUUID = "InstallProfile;Collection=" + collectionID.toString();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while getting generateProfile ", ex);
        }
        return commandUUID;
    }
    
    public String getWindowsInstallProfilePayloadData(final Long collectionID) {
        String sProfileData = null;
        try {
            final DataObject dataObject = MDMCollectionUtil.getCollection(collectionID);
            final List configDataItemDOList = MDMConfigUtil.getConfigurationDataItems(collectionID);
            final DO2WindowsPayloadHandler handler = new DO2WindowsPayloadHandler();
            final WindowsConfigurationPayload cfgPayload = handler.createPayload(dataObject, configDataItemDOList);
            final String commandUUID = "InstallProfile;Collection=" + collectionID.toString();
            cfgPayload.setCommandUUID(commandUUID);
            sProfileData = cfgPayload.toString();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception ", ex);
        }
        return sProfileData;
    }
    
    public String getWindowsRemoveProfilePayloadData(final Long collectionID) {
        String sProfileData = null;
        try {
            final DataObject dataObject = MDMCollectionUtil.getCollection(collectionID);
            final List configDOList = MDMConfigUtil.getConfigurationDataItems(collectionID);
            final DO2WindowsPayloadHandler handler = new DO2WindowsPayloadHandler();
            final WindowsConfigurationPayload cfgPayload = handler.createRemoveProfilePayload(dataObject, configDOList);
            final String commandUUID = "RemoveProfile;Collection=" + collectionID.toString();
            cfgPayload.setCommandUUID(commandUUID);
            sProfileData = cfgPayload.toString();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception ", ex);
        }
        return sProfileData;
    }
    
    public String generateRemoveProfile(final String profileName, final Long collectionID, final String profileFileName) {
        String commandUUID = null;
        String payloadData = null;
        try {
            payloadData = this.getWindowsRemoveProfilePayloadData(collectionID);
            this.logger.log(Level.INFO, "Profile payloadData for collection ID -- {0} \n\n{1}", new Object[] { collectionID, payloadData });
            ApiFactoryProvider.getFileAccessAPI().writeFile(profileFileName, payloadData.getBytes());
            this.logger.log(Level.INFO, "Profile file {0} has been generated successfully", profileFileName);
            commandUUID = "RemoveProfile;Collection=" + collectionID.toString();
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception  ", exp);
        }
        return commandUUID;
    }
    
    public JSONArray generateAppProfiles(final JSONObject inputJSON) {
        final JSONArray jsonObject = new JSONArray();
        try {
            final Long collectionID = inputJSON.getLong("collectionID");
            final String absoluteParentDirectory = String.valueOf(inputJSON.get("absoluteParentDirectory"));
            final String relativeParentDirectory = String.valueOf(inputJSON.get("relativeParentDirectory"));
            final WindowsConfigurationPayload[] payloads = this.generateAppPayloads(collectionID);
            WindowsConfigurationPayload cfgPayload = null;
            String commandUUID = null;
            for (int i = 0; i < payloads.length; ++i) {
                cfgPayload = payloads[i];
                if (cfgPayload != null) {
                    if (cfgPayload.getConfigurationPayloadType().equalsIgnoreCase("InstallApplication")) {
                        final JSONObject details = new JSONObject();
                        final String profileFileName = absoluteParentDirectory + File.separator + "install_profile.xml";
                        commandUUID = "InstallApplication;Collection=" + collectionID.toString();
                        cfgPayload.setCommandUUID(commandUUID);
                        final String payloadData = cfgPayload.toString();
                        this.logger.log(Level.INFO, "Profile payloadData for collection ID -- {0} \n\n{1}", new Object[] { collectionID, payloadData });
                        ApiFactoryProvider.getFileAccessAPI().writeFile(profileFileName, payloadData.getBytes());
                        this.logger.log(Level.INFO, "Profile file {0} has been generated successfully", profileFileName);
                        final String commandFilePath = relativeParentDirectory + File.separator + "install_profile.xml";
                        details.put("commandUUID", (Object)commandUUID);
                        details.put("commandType", (Object)"InstallApplication");
                        details.put("commandFilePath", (Object)commandFilePath);
                        jsonObject.put((Object)details);
                    }
                    else if (cfgPayload.getConfigurationPayloadType().equalsIgnoreCase("RemoveApplication")) {
                        final JSONObject details = new JSONObject();
                        final String profileFileName = absoluteParentDirectory + File.separator + "remove_profile.xml";
                        commandUUID = "RemoveApplication;Collection=" + collectionID.toString();
                        cfgPayload.setCommandUUID(commandUUID);
                        final String payloadData = cfgPayload.toString();
                        this.logger.log(Level.INFO, "Profile payloadData for collection ID -- {0} \n\n{1}", new Object[] { collectionID, payloadData });
                        ApiFactoryProvider.getFileAccessAPI().writeFile(profileFileName, payloadData.getBytes());
                        this.logger.log(Level.INFO, "Profile file {0} has been generated successfully", profileFileName);
                        final String commandFilePath = relativeParentDirectory + File.separator + "remove_profile.xml";
                        details.put("commandUUID", (Object)commandUUID);
                        details.put("commandType", (Object)"RemoveApplication");
                        details.put("commandFilePath", (Object)commandFilePath);
                        jsonObject.put((Object)details);
                    }
                    else if (cfgPayload.getConfigurationPayloadType().equalsIgnoreCase("UpdateApplication")) {
                        final JSONObject details = new JSONObject();
                        final String profileFileName = absoluteParentDirectory + File.separator + "update_profile.xml";
                        commandUUID = "UpdateApplication;Collection=" + collectionID.toString();
                        cfgPayload.setCommandUUID(commandUUID);
                        final String payloadData = cfgPayload.toString();
                        this.logger.log(Level.INFO, "Profile payloadData for collection ID -- {0} \n\n{1}", new Object[] { collectionID, payloadData });
                        ApiFactoryProvider.getFileAccessAPI().writeFile(profileFileName, payloadData.getBytes());
                        this.logger.log(Level.INFO, "Profile file {0} has been generated successfully", profileFileName);
                        final String commandFilePath = relativeParentDirectory + File.separator + "update_profile.xml";
                        details.put("commandUUID", (Object)commandUUID);
                        details.put("commandType", (Object)"UpdateApplication");
                        details.put("commandFilePath", (Object)commandFilePath);
                        jsonObject.put((Object)details);
                    }
                    else if (cfgPayload.getConfigurationPayloadType().equalsIgnoreCase("ApplicationConfiguration")) {
                        final JSONObject details = new JSONObject();
                        final String profileFileName = absoluteParentDirectory + File.separator + "appconfig_profile.xml";
                        commandUUID = "ApplicationConfiguration;Collection=" + collectionID.toString();
                        if (cfgPayload.getAtomicPayloadContent().getRequestCmds() != null) {
                            cfgPayload.setCommandUUID(commandUUID);
                            final String payloadData = cfgPayload.toString();
                            this.logger.log(Level.INFO, "Profile payloadData for collection ID -- {0} \n\n{1}", new Object[] { collectionID, payloadData });
                            ApiFactoryProvider.getFileAccessAPI().writeFile(profileFileName, payloadData.getBytes());
                            this.logger.log(Level.INFO, "Profile file {0} has been generated successfully", profileFileName);
                        }
                        final String commandFilePath2 = relativeParentDirectory + File.separator + "appconfig_profile.xml";
                        details.put("commandUUID", (Object)commandUUID);
                        details.put("commandType", (Object)"ApplicationConfiguration");
                        details.put("commandFilePath", (Object)commandFilePath2);
                        jsonObject.put((Object)details);
                    }
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return jsonObject;
    }
    
    public WindowsConfigurationPayload[] generateAppPayloads(final Long collectionId) {
        try {
            final DataObject dataObject = MDMCollectionUtil.getCollection(collectionId);
            final List configDOList = MDMConfigUtil.getConfigurationDataItems(collectionId);
            final DO2WindowsPayloadHandler handler = new DO2WindowsPayloadHandler();
            final WindowsConfigurationPayload[] payloads = handler.createAppPayloads(dataObject, configDOList);
            return payloads;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while generate app payloads", ex);
            return new WindowsConfigurationPayload[4];
        }
    }
    
    static {
        WindowsPayloadHandler.pHandler = null;
    }
}
