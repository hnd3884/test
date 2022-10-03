package com.me.mdm.server.profiles.mac.configresponseprocessor;

import java.util.List;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.logging.Level;
import com.dd.plist.NSDictionary;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.PlistWrapper;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MacFirmwareFirmwarePreSecurityInfoResponseProcessor extends MacFirmwareSequentialCommandGeneralResponseProcessor
{
    Logger logger;
    
    public MacFirmwareFirmwarePreSecurityInfoResponseProcessor() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public JSONObject processSeqQueuedCommand(JSONObject params) {
        final Long resourceID = JSONUtil.optLongForUVH(params, "resourceId", Long.valueOf(-1L));
        final String commandUDID = params.optString("strCommandUuid");
        try {
            params = super.processSeqQueuedCommand(params);
            final String strData = String.valueOf(params.get("strData"));
            final String status = String.valueOf(params.get("strStatus"));
            final List resourceIDList = new ArrayList();
            resourceIDList.add(resourceID);
            final NSDictionary nsDict = PlistWrapper.getInstance().getDictForKey("SecurityInfo", strData);
            HashMap hsmap = new HashMap();
            hsmap = PlistWrapper.getInstance().getHashFromDict(nsDict);
            MDMInvDataPopulator.getInstance().addOrUpdateIOSSecurityInfo(resourceID, hsmap);
            final JSONObject sequentialCommandParams = params.getJSONObject("PARAMS");
            final JSONObject initialParams = sequentialCommandParams.optJSONObject("initialParams");
            final JSONObject comandScopeParams = sequentialCommandParams.optJSONObject("cmdScopeParams");
            final JSONObject commandLevelParams = sequentialCommandParams.optJSONObject("CommandLevelParams");
            final JSONObject currentCommandParams = new JSONObject();
            final JSONObject previousCommanParams = sequentialCommandParams.optJSONObject("PrevCmdParams");
            final boolean isClearPassword = initialParams.getBoolean("isClearPassword");
            final Long collectionID = commandLevelParams.optLong("CollectionID", -1L);
            if (status.equalsIgnoreCase("Acknowledged")) {
                if (collectionID != -1L) {
                    if (isClearPassword) {
                        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collectionID, 3, "mdm.profile.firmware_clear_initiated");
                    }
                    else {
                        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collectionID, 3, "mdm.profile.firmware_initiated");
                    }
                }
                final NSDictionary firmwareDict = (NSDictionary)PlistWrapper.getInstance().getDictForKey("SecurityInfo", strData).get((Object)"FirmwarePasswordStatus");
                if (firmwareDict != null) {
                    final boolean isFirmwareAlreadySet = Boolean.valueOf(firmwareDict.get((Object)"PasswordExists").toString());
                    final boolean isFirmwareChangePending = Boolean.valueOf(firmwareDict.get((Object)"ChangePending").toString());
                    currentCommandParams.put("isPreviousPasswordWaitingForRestart", isFirmwareChangePending);
                    currentCommandParams.put("isFirmwareAlreadySet", isFirmwareAlreadySet);
                    if (isFirmwareAlreadySet || isFirmwareChangePending) {
                        if (!comandScopeParams.has("existingPasswordID") && collectionID != -1L) {
                            MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(resourceIDList, collectionID, 8, "mdm.profile.firmware_already_set");
                        }
                        if (isFirmwareChangePending) {
                            params.put("action", 2);
                            if (collectionID != -1L) {
                                if (!isClearPassword) {
                                    MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(resourceIDList, collectionID, 7, "mdm.mac.firmware.restart_reqd");
                                }
                                else {
                                    MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(resourceIDList, collectionID, 7, "mdm.mac.firmware.restart_reqd_remove");
                                }
                            }
                        }
                        else if (collectionID != -1L) {
                            this.logger.log(Level.INFO, "Firmware is already applied on device , going to set new password");
                        }
                    }
                    else if (!isClearPassword) {
                        this.logger.log(Level.INFO, "Firmware is is not enabled on device, going to set password");
                    }
                    else {
                        params.put("action", 2);
                        ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(resourceID, collectionID);
                    }
                }
                else {
                    params.put("action", 5);
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
