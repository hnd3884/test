package com.me.mdm.agent.handlers.windows;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.settings.location.MDMGeoLocationHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import org.json.JSONException;
import org.json.JSONObject;
import com.me.mdm.server.windows.notification.WpDeviceChannelUri;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.mdm.agent.handlers.BaseAppCommandQueueProcessor;

public class WpAppCommandResponseQueueProcessor extends BaseAppCommandQueueProcessor
{
    private static String sCHANNEL_URI;
    
    @Override
    protected void processCommand() throws Exception {
        Boolean shouldDeleteCommand = Boolean.FALSE;
        if (this.commandResponse.responseType.equalsIgnoreCase("AppNotificationCredential")) {
            shouldDeleteCommand = this.processUpdateChannelUriResponse();
        }
        else if (this.commandResponse.responseType.equalsIgnoreCase("GetLocation")) {
            shouldDeleteCommand = this.processGetLocationResponse();
        }
        else if (this.commandResponse.responseType.equalsIgnoreCase("SyncAppCatalog") || this.commandResponse.responseType.equalsIgnoreCase("AppCatalogSummary")) {
            shouldDeleteCommand = this.processAppCatalogCommands();
        }
        else if (this.commandResponse.responseType.equalsIgnoreCase("CorporateWipe")) {
            shouldDeleteCommand = this.processCorporateWipeResponse();
        }
        else if (this.commandResponse.responseType.equalsIgnoreCase("SyncAgentSettings")) {
            shouldDeleteCommand = this.processSyncAgentSettingsAck();
        }
        else if (this.commandResponse.responseType.equalsIgnoreCase("AgentUpgrade")) {
            shouldDeleteCommand = this.processAgentUpgradeAck();
        }
        else if (this.commandResponse.responseType.equalsIgnoreCase("PrivacySettings") || this.commandResponse.responseType.equalsIgnoreCase("TermsOfUse") || this.commandResponse.responseType.equalsIgnoreCase("SyncPrivacySettings")) {
            shouldDeleteCommand = Boolean.TRUE;
        }
        if (shouldDeleteCommand) {
            DeviceCommandRepository.getInstance().deleteResourceCommand(this.commandResponse.commandUUID, ManagedDeviceHandler.getInstance().getResourceIDFromUDID(this.commandResponse.udid));
        }
        else {
            this.updateResourceCommandStatus(12);
        }
    }
    
    private Boolean processUpdateChannelUriResponse() throws JSONException {
        Boolean retVal = Boolean.TRUE;
        final JSONObject responseData = this.commandResponse.responseData;
        if (this.commandResponse.status.equalsIgnoreCase("Error")) {
            retVal = Boolean.FALSE;
        }
        else {
            retVal = new WpDeviceChannelUri().updateNativeAppChannelUri(this.commandResponse.udid, String.valueOf(responseData.get(WpAppCommandResponseQueueProcessor.sCHANNEL_URI)));
        }
        return retVal;
    }
    
    private Boolean processGetLocationResponse() throws Exception {
        Boolean retVal = Boolean.TRUE;
        final JSONObject responseData = this.commandResponse.responseData;
        final DeviceDetails deviceDetails = new DeviceDetails(this.commandResponse.udid);
        final Long resourceID = deviceDetails.resourceId;
        int statusCode = 200;
        String remarksKey = null;
        String englishMessage = null;
        if (this.commandResponse.status.equalsIgnoreCase("Error")) {
            retVal = Boolean.FALSE;
            statusCode = responseData.getInt("ErrorCode");
            if (statusCode == -2147024891) {
                statusCode = 12136;
            }
            remarksKey = String.valueOf(responseData.get("ErrorMessage"));
            englishMessage = responseData.optString("ErrorDescription", remarksKey);
            MDMGeoLocationHandler.getInstance().addorUpdateDeviceLocationErrorCode(resourceID, statusCode);
        }
        else {
            MDMGeoLocationHandler.getInstance().deleteDeviceLocationErrorCode(resourceID);
            responseData.put("LocationUpdationTime", System.currentTimeMillis());
            MDMGeoLocationHandler.getInstance().addOrUpdateDeviceLocationDetails(responseData, this.commandResponse.udid);
        }
        MDMUtil.getInstance().updateSecurityCommandsStatus(this.commandResponse.udid, this.commandResponse.commandUUID, statusCode, deviceDetails.customerId, remarksKey, englishMessage);
        return retVal;
    }
    
    private Boolean processAppCatalogCommands() {
        Boolean retVal = Boolean.TRUE;
        if (this.commandResponse.status.equalsIgnoreCase("Error")) {
            retVal = Boolean.FALSE;
        }
        return retVal;
    }
    
    private Boolean processCorporateWipeResponse() {
        final Boolean retVal = Boolean.FALSE;
        if (this.commandResponse.status.equalsIgnoreCase("Acknowledged")) {
            final Long resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(this.commandResponse.udid);
            if (resourceId != null) {
                IosNativeAppHandler.getInstance().addorUpdateIOSAgentInstallationStatus(resourceId, 0);
            }
            DeviceCommandRepository.getInstance().deleteResourceCommand(this.commandResponse.commandUUID, this.commandResponse.udid, 2);
            if (!DeviceCommandRepository.getInstance().hasDeviceCommandInCacheOrRepo(this.commandResponse.udid)) {
                try {
                    ManagedDeviceHandler.getInstance().removeDeviceInTrash(ManagedDeviceHandler.getInstance().getResourceIDFromUDID(this.commandResponse.udid));
                }
                catch (final Exception ex) {
                    Logger.getLogger(WpAppCommandResponseQueueProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return retVal;
    }
    
    private Boolean processAgentUpgradeAck() {
        Boolean retVal = Boolean.TRUE;
        if (this.commandResponse.status.equalsIgnoreCase("Error")) {
            retVal = Boolean.FALSE;
        }
        return retVal;
    }
    
    private Boolean processSyncAgentSettingsAck() {
        Boolean retVal = Boolean.TRUE;
        if (this.commandResponse.status.equalsIgnoreCase("Error")) {
            retVal = Boolean.FALSE;
        }
        return retVal;
    }
    
    private void updateResourceCommandStatus(final int commandStatusToUpdate) {
        final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(this.commandResponse.udid);
        final Long commandID = DeviceCommandRepository.getInstance().getCommandID(this.commandResponse.commandUUID);
        DeviceCommandRepository.getInstance().updateResourceCommandStatus(commandID, resourceID, 2, commandStatusToUpdate);
    }
    
    static {
        WpAppCommandResponseQueueProcessor.sCHANNEL_URI = "ChannelURI";
    }
}
