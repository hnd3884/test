package com.me.mdm.server.profiles.ios.response;

import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.ios.error.IOSErrorStatusHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSLockScreenResponseProcessor implements CommandResponseProcessor.QueuedResponseProcessor, CommandResponseProcessor.ImmediateSeqResponseProcessor
{
    private static Logger logger;
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        JSONObject settingResponse = null;
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        String remarks = "";
        try {
            final String collectionID = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
            final IOSErrorStatusHandler statusHandler = new IOSErrorStatusHandler();
            settingResponse = statusHandler.getIOSSettingError(params.optString("strData"));
            final String settingStatus = settingResponse.optString("Status");
            if (settingStatus.equalsIgnoreCase("Acknowledged")) {
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionID, 6, "dc.db.mdm.collection.Successfully_applied_policy");
            }
            else if (settingStatus.equalsIgnoreCase("Error") || settingStatus.equalsIgnoreCase("CommandFormatError")) {
                remarks = settingResponse.optString("EnglishRemarks");
                if (remarks.equals("mdm.profile.wallpaper.error.invalidResponseImage")) {
                    IOSLockScreenResponseProcessor.logger.log(Level.FINE, "Image failed in the device");
                    remarks = "mdm.profile.lockscreen.imagefailed";
                }
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionID, 7, remarks);
            }
        }
        catch (final Exception e) {
            IOSLockScreenResponseProcessor.logger.log(Level.SEVERE, e, () -> "Exception in immediate processing the lockscreen for resource:" + String.valueOf(n));
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return null;
    }
    
    @Override
    public JSONObject processImmediateSeqCommand(final JSONObject params) {
        JSONObject settingResponse = null;
        final JSONObject queueResponse = new JSONObject();
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        try {
            final IOSErrorStatusHandler statusHandler = new IOSErrorStatusHandler();
            final JSONObject response = new JSONObject();
            final JSONObject seqParams = new JSONObject();
            settingResponse = statusHandler.getIOSSettingError(params.optString("strData"));
            final String settingStatus = settingResponse.optString("Status");
            if (settingStatus.equalsIgnoreCase("Acknowledged")) {
                response.put("action", 1);
                queueResponse.put("Status", 6);
            }
            else if (settingStatus.equalsIgnoreCase("Error") || settingStatus.equalsIgnoreCase("CommandFormatError")) {
                response.put("action", 2);
                queueResponse.put("Status", 7);
            }
            seqParams.put("isNeedToRemove", false);
            response.put("commandUUID", (Object)commandUUID);
            response.put("resourceID", (Object)resourceID);
            response.put("params", (Object)seqParams);
            response.put("isNotify", false);
            SeqCmdRepository.getInstance().processSeqCommand(response);
            queueResponse.put("isNeedToAddQueue", false);
        }
        catch (final Exception e) {
            IOSLockScreenResponseProcessor.logger.log(Level.SEVERE, e, () -> "Exception in processing the Lockscreen response for resource:" + String.valueOf(n));
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return queueResponse;
    }
    
    static {
        IOSLockScreenResponseProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
