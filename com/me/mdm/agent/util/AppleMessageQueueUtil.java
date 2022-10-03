package com.me.mdm.agent.util;

import com.me.mdm.server.command.CommandResponseProcessor;
import java.util.Map;
import org.json.JSONObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.HashMap;
import java.util.logging.Logger;

public class AppleMessageQueueUtil
{
    private static AppleMessageQueueUtil appleMessageQueueUtil;
    Logger logger;
    private static final HashMap<String, String> GET_CLASS_FOR_PROCESSING;
    
    private AppleMessageQueueUtil() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static AppleMessageQueueUtil getInstance() {
        if (AppleMessageQueueUtil.appleMessageQueueUtil == null) {
            AppleMessageQueueUtil.appleMessageQueueUtil = new AppleMessageQueueUtil();
        }
        return AppleMessageQueueUtil.appleMessageQueueUtil;
    }
    
    public void processMessageQueue(final String messageType, final HashMap hashPlist, final DCQueueData dcQueueData) {
        try {
            final String udid = hashPlist.get("UDID");
            final Long customerID = Long.parseLong(String.valueOf(dcQueueData.queueExtnTableData.get("CUSTOMER_ID")));
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid, customerID);
            final String fileName = dcQueueData.fileName;
            this.logger.log(Level.INFO, "processMessageQueue():- Started for -> MessageType={0} & strUDID={1} & fileName={2} ", new Object[] { messageType, udid, fileName });
            final JSONObject params = new JSONObject();
            params.put("RESOURCE_ID", (Object)resourceID);
            params.put("CUSTOMER_ID", (Object)customerID);
            params.put("hashPlist", (Map)hashPlist);
            params.put("reqTime", dcQueueData.postTime);
            params.put("UDID", (Object)udid);
            final CommandResponseProcessor.QueuedResponseProcessor processor = this.getInstanceForQueueResponse(messageType);
            processor.processQueuedCommand(params);
            this.logger.log(Level.INFO, "processMessageQueue():- Finished for -> MessageType={0} & strUDID={1} & fileName={2} ", new Object[] { messageType, udid, fileName });
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in processMessageQueue():-", e);
        }
    }
    
    public CommandResponseProcessor.QueuedResponseProcessor getInstanceForQueueResponse(final String messageType) throws Exception {
        final String className = AppleMessageQueueUtil.GET_CLASS_FOR_PROCESSING.get(messageType);
        return (CommandResponseProcessor.QueuedResponseProcessor)Class.forName(className).newInstance();
    }
    
    static {
        AppleMessageQueueUtil.appleMessageQueueUtil = null;
        GET_CLASS_FOR_PROCESSING = new HashMap<String, String>() {
            {
                this.put("SetBootstrapToken", "com.me.mdm.server.agent.mac.SetBootstrapTokenReqProcessor");
            }
        };
    }
}
