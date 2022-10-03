package com.me.mdm.server.profiles.ios.response;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.adventnet.sym.server.mdm.PlistWrapper;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.profiles.IOSInstallProfileResponseProcessor;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSSingleWebAppFeedbackResponseProcessor implements CommandResponseProcessor.ImmediateSeqResponseProcessor, CommandResponseProcessor.QueuedResponseProcessor
{
    private static Logger logger;
    private String status;
    
    public IOSSingleWebAppFeedbackResponseProcessor() {
        this.status = "Status";
    }
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        final String status = params.optString("strStatus");
        final String strData = params.optString("strData");
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        final Long customerId = params.optLong("customerId");
        try {
            final String collectionID = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
            if (status.equalsIgnoreCase("Acknowledged")) {
                if (this.isSingleWebKioskInstalled(strData)) {
                    new IOSInstallProfileResponseProcessor().processSucceededProfileCommand(Long.valueOf(collectionID), resourceID, customerId);
                }
                else {
                    MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionID, 7, "mdm.profiles.ios.kiosk.single_web_app_error");
                }
            }
            else {
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionID, 7, "mdm.profiles.ios.kiosk.single_web_app_error");
            }
        }
        catch (final Exception e) {
            IOSSingleWebAppFeedbackResponseProcessor.logger.log(Level.SEVERE, "exception in single web app response", e);
        }
        return null;
    }
    
    @Override
    public JSONObject processImmediateSeqCommand(final JSONObject params) {
        final JSONObject queueResponse = new JSONObject();
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        final JSONObject response = new JSONObject();
        final JSONObject seqParams = new JSONObject();
        try {
            final String status = params.optString("strStatus");
            if (status.equalsIgnoreCase("Acknowledged")) {
                final String strData = params.optString("strData");
                if (this.isSingleWebKioskInstalled(strData)) {
                    response.put("action", 1);
                    queueResponse.put("Status", 6);
                    seqParams.put("isNeedToRemove", false);
                }
                else {
                    response.put("action", 4);
                    final JSONObject paramObject = params.optJSONObject("currentSeqParams");
                    Integer retryCount = paramObject.optInt("retryCount", -1);
                    final Long timeout = paramObject.optLong("timeout", -1L);
                    if (retryCount != null && timeout != null && retryCount != -1 && timeout != -1L) {
                        seqParams.put("retryCount", (Object)(--retryCount));
                        seqParams.put("timeout", 30000);
                    }
                    else {
                        seqParams.put("retryCount", 2);
                        seqParams.put("timeout", 30000);
                    }
                }
            }
            else if (status.equalsIgnoreCase("Error") || status.equalsIgnoreCase("CommandFormatError")) {
                response.put("action", 2);
                queueResponse.put("Status", 7);
                queueResponse.put("isNeedToAddQueue", true);
            }
            response.put("commandUUID", (Object)commandUUID);
            response.put("resourceID", (Object)resourceID);
            response.put("params", (Object)seqParams);
            response.put("isNotify", false);
            SeqCmdRepository.getInstance().processSeqCommand(response);
        }
        catch (final Exception e) {
            IOSSingleWebAppFeedbackResponseProcessor.logger.log(Level.SEVERE, e, () -> "Exception in processing the single webapp response for resource:" + String.valueOf(n));
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return queueResponse;
    }
    
    public boolean isSingleWebKioskInstalled(final String strData) {
        final NSArray nsarray = PlistWrapper.getInstance().getArrayForKey("ManagedApplicationFeedback", strData);
        if (nsarray.count() > 0) {
            final NSDictionary appfeedbackDict = (NSDictionary)nsarray.objectAtIndex(0);
            final NSDictionary feedbackDict = (NSDictionary)appfeedbackDict.objectForKey("Feedback");
            if (feedbackDict != null && feedbackDict.containsKey("KioskAgentStatus")) {
                return true;
            }
        }
        return false;
    }
    
    public void handleResponseFromAgent(final JSONObject jsonObject, final Long resourceId) {
        try {
            final JSONObject messageRequest = jsonObject.getJSONObject("MsgRequest");
            messageRequest.getLong("KioskCollectionID");
        }
        catch (final Exception ex) {
            IOSSingleWebAppFeedbackResponseProcessor.logger.log(Level.SEVERE, "Exception in handle agent", ex);
        }
    }
    
    static {
        IOSSingleWebAppFeedbackResponseProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
