package com.me.mdm.server.profiles.mac.configresponseprocessor;

import java.util.HashMap;
import com.dd.plist.NSDictionary;
import java.util.List;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.security.mac.MacFirmwarePasswordDeviceAssociationHandler;
import com.adventnet.sym.server.mdm.PlistWrapper;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;

public class MacFirmwareFirmwareVerifyFirmwarePasswordResponseProcessor extends MacFirmwareSequentialCommandGeneralResponseProcessor
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
            final JSONObject sequentialCommandParams = params.getJSONObject("PARAMS");
            final JSONObject initialParams = sequentialCommandParams.optJSONObject("initialParams");
            final JSONObject comandScopeParams = sequentialCommandParams.optJSONObject("cmdScopeParams");
            final JSONObject commandLevelParams = sequentialCommandParams.optJSONObject("CommandLevelParams");
            final JSONObject currentCommandParams = sequentialCommandParams.optJSONObject("CurCmdParam");
            final JSONObject previousCommanParams = sequentialCommandParams.optJSONObject("PrevCmdParams");
            final boolean isClearPassword = initialParams.getBoolean("isClearPassword");
            final Long collectionID = commandLevelParams.optLong("CollectionID", -1L);
            if (status.equalsIgnoreCase("Acknowledged")) {
                final NSDictionary responseDict = PlistWrapper.getInstance().getDictForKey("VerifyFirmwarePassword", strData);
                if (responseDict != null) {
                    final HashMap resMap = PlistWrapper.getInstance().getHashFromDict(responseDict);
                    final boolean isPasswordVerified = Boolean.valueOf(resMap.get("PasswordVerified"));
                    currentCommandParams.put("isExistingPasswordVerified", isPasswordVerified);
                    if (!isPasswordVerified) {
                        final Long existingPasswordID = comandScopeParams.getLong("existingPasswordID");
                        MacFirmwarePasswordDeviceAssociationHandler.removeExistingFirmwarePasswordFromDeviceTables(resourceID, existingPasswordID);
                        if (collectionID != -1L) {
                            MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(resourceIDList, collectionID, 7, "mdm.mac.firmware.existing_password_not_verified");
                        }
                    }
                    else {
                        final Long existingPasswordID = comandScopeParams.getLong("existingPasswordID");
                        MacFirmwarePasswordDeviceAssociationHandler.updateDevicePasswordAfterSucessfullVerification(existingPasswordID, resourceID);
                        if (collectionID != -1L) {
                            this.logger.log(Level.INFO, "MacFirmware: Existing firmware password is verified in device");
                        }
                    }
                }
                else {
                    currentCommandParams.put("isExistingPasswordVerified", false);
                }
            }
            else if (status.equalsIgnoreCase("Error")) {
                final String errorcode = String.valueOf(params.get("ErrorCode"));
                final String errorRemarks = String.valueOf(params.get("ErrorCode"));
                if (collectionID != -1L) {
                    MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(resourceIDList, collectionID, 7, errorRemarks);
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
