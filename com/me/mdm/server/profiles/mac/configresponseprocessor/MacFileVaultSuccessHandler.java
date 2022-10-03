package com.me.mdm.server.profiles.mac.configresponseprocessor;

import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.encryption.ios.filevault.MDMFilevaultUtils;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.encryption.ios.filevault.MDMDeviceEncryptionSettingsHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.encryption.MDMEncryptionSettingsHandler;
import com.me.mdm.server.config.MDMConfigUtil;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMProfileResponseListener;

public class MacFileVaultSuccessHandler implements MDMProfileResponseListener
{
    Logger logger;
    boolean isNotify;
    
    public MacFileVaultSuccessHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.isNotify = false;
    }
    
    @Override
    public JSONObject successHandler(final JSONObject params) {
        final JSONObject listenerResponse = new JSONObject();
        try {
            final Long collectionID = Long.parseLong(params.optString("collectionId"));
            final Long resourceId = params.optLong("resourceId");
            final List<Long> resIDList = new ArrayList<Long>();
            resIDList.add(resourceId);
            final Long configDataItemID = MDMConfigUtil.getConfigDataItemIDForCollection(770, collectionID);
            final Long settingsID = MDMEncryptionSettingsHandler.getEncryptionSettingsIDForConfigDataItem(configDataItemID);
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("ENCRYPTION_SETTINGS_ID", (Object)settingsID);
            requestJSON.put("RESOURCE_ID", (Object)resourceId);
            this.logger.log(Level.INFO, "FileVaultLog: Filevault policy is successfully applied to device. Going to map device to Encryption Settings");
            this.logger.log(Level.INFO, "FileVaultLog: DevicetoEncryptionSettings :{0}", requestJSON);
            MDMDeviceEncryptionSettingsHandler.addOrUpdateDeviceTOEncrytptionSetitngs(requestJSON);
            this.logger.log(Level.INFO, "FileVaultLog: Going to add ProfileList command to device to check if profile is already distributed during next time:{0}", resourceId);
            final JSONArray commandUUIDs = new JSONArray();
            commandUUIDs.put((Object)"ProfileList");
            final JSONObject commandObject = new JSONObject();
            commandObject.put(String.valueOf(1), (Object)commandUUIDs);
            listenerResponse.put("commandUUIDs", (Object)commandObject);
            this.isNotify = true;
            this.logger.log(Level.INFO, "FileVaultLog: Going to add FilevaultUserLoginSecurityInfo command to device to automatically update inventory status user logouts :{0}", resourceId);
            final Long fileVaultSecurityCommandID = DeviceCommandRepository.getInstance().addCommand("FileVaultUserLoginSecurityInfo");
            final List<Long> commandIDList = new ArrayList<Long>();
            commandIDList.add(fileVaultSecurityCommandID);
            DeviceCommandRepository.getInstance().assignDeviceCommandToOnUserChannel(commandIDList, resIDList);
            if (MDMFilevaultUtils.isFilevaultPersonalRecoveryKeyImported(resourceId)) {
                final HashMap profileDetails = MDMUtil.getInstance().getProfileDetailsForCollectionId(collectionID);
                final Long userID = profileDetails.get("LAST_MODIFIED_BY");
                MDMFilevaultUtils.rotateFilevaultPersonalRecoveryKey(resourceId, userID);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "FileVaultLog: Exception in MacFileVaultSuccessHandler.successHandler():", ex);
        }
        return listenerResponse;
    }
    
    @Override
    public JSONObject failureHandler(final JSONObject params) {
        this.logger.log(Level.SEVERE, "FileVaultLog: MacFileVault Config failure handler is not supported....");
        return null;
    }
    
    @Override
    public boolean isNotify(final JSONObject params) {
        return this.isNotify;
    }
}
