package com.me.mdm.agent.handlers;

import org.json.JSONException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;

public class BaseAppMessageQueueProcessor
{
    protected Logger messagesAccesslogger;
    protected Logger logger;
    protected DCQueueData queueDataObject;
    protected JSONObject queueData;
    protected DeviceMessageRequest messageRequest;
    
    public BaseAppMessageQueueProcessor() {
        this.messagesAccesslogger = Logger.getLogger("MDMMessagesLogger");
        this.logger = Logger.getLogger("MDMLogger");
        this.queueDataObject = null;
        this.queueData = null;
        this.messageRequest = null;
    }
    
    public void processQueueData(final DCQueueData queueDataObject) {
        try {
            this.initMessageProcessor(queueDataObject);
            this.messagesAccesslogger.log(Level.INFO, "DATA-IN: {0}\t{1}\t{2}\t{3}\t{4}\t{5}", new Object[] { this.messageRequest.messageType, this.messageRequest.resourceID, this.messageRequest.udid, "Message-Received", Long.toString(queueDataObject.postTime), MDMUtil.getCurrentTimeInMillis() - queueDataObject.postTime });
            this.processMessage();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in  BaseDeviceMessageQueueProcessor.processQueueData", ex);
        }
    }
    
    protected void initMessageProcessor(final DCQueueData queueDataObject) throws JSONException {
        this.queueDataObject = queueDataObject;
        this.queueData = new JSONObject(queueDataObject.queueData.toString());
        this.messageRequest = new DeviceMessageRequest(this.queueData);
    }
    
    protected void processMessage() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
