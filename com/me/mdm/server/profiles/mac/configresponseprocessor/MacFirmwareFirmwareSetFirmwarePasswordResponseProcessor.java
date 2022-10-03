package com.me.mdm.server.profiles.mac.configresponseprocessor;

import com.dd.plist.NSDictionary;
import java.util.HashMap;
import java.util.List;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import java.util.logging.Level;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.security.mac.MacFirmwarePasswordDeviceAssociationHandler;
import com.adventnet.sym.server.mdm.PlistWrapper;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;

public class MacFirmwareFirmwareSetFirmwarePasswordResponseProcessor extends MacFirmwareSequentialCommandGeneralResponseProcessor
{
    @Override
    public JSONObject processSeqQueuedCommand(JSONObject params) {
        final Long resourceID = JSONUtil.optLongForUVH(params, "resourceId", Long.valueOf(-1L));
        final String commandUDID = params.optString("strCommandUuid");
        try {
            params = super.processSeqQueuedCommand(params);
            final String status = String.valueOf(params.get("strStatus"));
            final String strData = String.valueOf(params.get("strData"));
            final List resourceIDList = new ArrayList();
            resourceIDList.add(resourceID);
            final JSONObject sequentialParams = params.getJSONObject("PARAMS");
            final JSONObject commandLevelParams = sequentialParams.optJSONObject("CommandLevelParams");
            final Long collectionID = commandLevelParams.optLong("CollectionID");
            final JSONObject commandScopeParams = sequentialParams.optJSONObject("cmdScopeParams");
            if (status.equalsIgnoreCase("Acknowledged")) {
                final HashMap hsmap = PlistWrapper.getInstance().getHashFromPlist(strData);
                if (hsmap != null) {
                    final NSDictionary firmwareDict = PlistWrapper.getInstance().getDictForKey("SetFirmwarePassword", strData);
                    final Boolean isPasswordChanged = Boolean.valueOf(firmwareDict.get((Object)"PasswordChanged").toString());
                    if (isPasswordChanged) {
                        final Long existingPasswordID = commandScopeParams.optLong("existingPasswordID", -1L);
                        final Long newPasswordID = commandScopeParams.getLong("newPasswordID");
                        MacFirmwarePasswordDeviceAssociationHandler.updateDevicePasswordAfterSucessfullVerification(newPasswordID, resourceID);
                        MacFirmwarePasswordDeviceAssociationHandler.addOrUpdateInventoryFirmwareDevice(resourceID, newPasswordID);
                        if (existingPasswordID != -1L) {
                            MacFirmwarePasswordDeviceAssociationHandler.updateDevicePasswordAfterSucessfullPasswordSet(existingPasswordID, resourceID);
                        }
                        if (collectionID != -1L) {
                            MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(resourceIDList, collectionID, 6, "mdm.mac.firmware.firmware_successfully_set");
                        }
                    }
                    else if (collectionID != -1L) {
                        MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(resourceIDList, collectionID, 7, "mdm.mac.firmware.firmware_unable_to_set_known_reason");
                    }
                }
            }
            else if (status.equalsIgnoreCase("Error") && collectionID != -1L) {
                MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(resourceIDList, collectionID, 7, "mdm.mac.firmware.unable_to_set_unknown");
            }
            SeqCmdRepository.getInstance().processSeqCommand(params);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "MacFirmware: Exception in " + this.getClass().getName() + " processQueuedCommand for params" + params, ex);
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUDID);
        }
        return params;
    }
}
