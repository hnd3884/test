package com.me.mdm.server.apple.command.response.responseprocessor;

import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import java.util.logging.Level;
import com.me.mdm.server.ios.error.IOSErrorStatusHandler;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class AppleSharedDeviceRestrictionRemoveResponseProcessor implements CommandResponseProcessor.ImmediateSeqResponseProcessor
{
    private static Logger logger;
    
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
            response.put("action", 1);
            queueResponse.put("Status", 6);
            final String settingStatus = settingResponse.optString("Status");
            if (settingStatus.equalsIgnoreCase("Error")) {
                response.put("action", 2);
            }
            else if (settingStatus.equalsIgnoreCase("CommandFormatError")) {
                AppleSharedDeviceRestrictionRemoveResponseProcessor.logger.log(Level.INFO, "Failed in immediate response processing");
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
            AppleSharedDeviceRestrictionRemoveResponseProcessor.logger.log(Level.SEVERE, e, () -> "Exception in processing the Lockscreen response for resource:" + String.valueOf(n));
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return queueResponse;
    }
    
    static {
        AppleSharedDeviceRestrictionRemoveResponseProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
