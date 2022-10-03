package com.me.mdm.server.profiles.mac.configresponseprocessor;

import com.dd.plist.NSDictionary;
import java.util.List;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import java.util.logging.Level;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.me.mdm.server.security.mac.MacFirmwarePasswordDeviceAssociationHandler;
import com.adventnet.sym.server.mdm.PlistWrapper;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;

public class MacFirmwareFirmwareClearFirmwarePasswordResponseProcessor extends MacFirmwareSequentialCommandGeneralResponseProcessor
{
    @Override
    public JSONObject processSeqQueuedCommand(JSONObject params) {
        final Long resourceID = JSONUtil.optLongForUVH(params, "resourceId", Long.valueOf(-1L));
        final String commandUDID = params.optString("strCommandUuid");
        try {
            params = super.processSeqQueuedCommand(params);
            final String status = String.valueOf(params.get("strStatus"));
            final String strData = String.valueOf(params.get("strData"));
            final JSONObject seqCommandParams = params.getJSONObject("PARAMS");
            final List resourceIDList = new ArrayList();
            resourceIDList.add(resourceID);
            final JSONObject sequentialCommandParams = params.getJSONObject("PARAMS");
            final JSONObject initialParams = sequentialCommandParams.optJSONObject("initialParams");
            final JSONObject comandScopeParams = sequentialCommandParams.optJSONObject("cmdScopeParams");
            final JSONObject commandLevelParams = sequentialCommandParams.optJSONObject("CommandLevelParams");
            final JSONObject currentCommandParams = new JSONObject();
            final Long collectionID = commandLevelParams.optLong("CollectionID", -1L);
            if (status.equalsIgnoreCase("Acknowledged")) {
                final NSDictionary firmwareDict = PlistWrapper.getInstance().getDictForKey("SetFirmwarePassword", strData);
                if (firmwareDict != null) {
                    final Boolean isPasswordCleared = Boolean.valueOf(firmwareDict.get((Object)"PasswordChanged").toString());
                    currentCommandParams.put("isPasswordCleared", (Object)isPasswordCleared);
                    if (isPasswordCleared) {
                        final Long existingPasswordID = comandScopeParams.getLong("existingPasswordID");
                        MacFirmwarePasswordDeviceAssociationHandler.updateDevicePasswordAfterSucessfullPasswordSet(existingPasswordID, resourceID);
                        MacFirmwarePasswordDeviceAssociationHandler.addOrUpdateInventoryFirmwareDevice(resourceID, null);
                        if (collectionID != -1L) {
                            ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(resourceID, collectionID);
                        }
                    }
                }
                else {
                    currentCommandParams.put("isPasswordCleared", false);
                }
            }
            else if (status.equalsIgnoreCase("Error")) {
                currentCommandParams.put("isPasswordCleared", false);
                if (collectionID != -1L) {
                    MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(resourceIDList, collectionID, 7, "mdm.mac.firmware.passswd_unable_to_clear");
                }
            }
            params.put("params", (Object)currentCommandParams);
            SeqCmdRepository.getInstance().processSeqCommand(params);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "MacFirmware: Exception in " + this.getClass().getName() + " processQueuedCommand for params" + params, ex);
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUDID);
        }
        return params;
    }
}
