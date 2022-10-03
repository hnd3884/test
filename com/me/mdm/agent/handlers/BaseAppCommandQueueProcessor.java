package com.me.mdm.agent.handlers;

import org.json.JSONException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;

public class BaseAppCommandQueueProcessor
{
    protected Logger accesslogger;
    protected Logger logger;
    protected DCQueueData queueDataObject;
    protected JSONObject queueData;
    protected DeviceCommandResponse commandResponse;
    
    public BaseAppCommandQueueProcessor() {
        this.accesslogger = Logger.getLogger("MDMCommandsLogger");
        this.logger = Logger.getLogger("MDMLogger");
        this.queueDataObject = null;
        this.queueData = null;
        this.commandResponse = null;
    }
    
    public void processQueueData(final DCQueueData queueDataObject) {
        try {
            this.initCommandProcessor(queueDataObject);
            this.accesslogger.log(Level.INFO, "DATA-IN: {0}\t{1}\t{2}\t{3}\t{4}\t{5}", new Object[] { this.commandResponse.commandUUID, this.commandResponse.resourceID, this.commandResponse.udid, this.commandResponse.status, Long.toString(queueDataObject.postTime), MDMUtil.getCurrentTimeInMillis() - queueDataObject.postTime });
            this.processCommand();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in BaseDeviceCommandQueueProcessor.processQueueData", ex);
        }
    }
    
    protected void initCommandProcessor(final DCQueueData queueDataObject) throws JSONException {
        this.queueDataObject = queueDataObject;
        this.queueData = new JSONObject(queueDataObject.queueData.toString());
        this.commandResponse = new DeviceCommandResponse(this.queueData);
    }
    
    protected void processCommand() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
