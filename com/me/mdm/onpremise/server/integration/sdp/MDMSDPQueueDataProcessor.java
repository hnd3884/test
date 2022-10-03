package com.me.mdm.onpremise.server.integration.sdp;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class MDMSDPQueueDataProcessor extends DCQueueDataProcessor
{
    private static final Logger SDPINTEGLOGGER;
    
    public void processData(final DCQueueData qData) {
        try {
            MDMSDPQueueDataProcessor.SDPINTEGLOGGER.log(Level.INFO, "Processing started for test : {0} ", qData.fileName);
            MDMSDPQueueDataProcessor.SDPINTEGLOGGER.log(Level.INFO, "Getting XML/JSON data from DCQueue class");
            MDMSDPAssetDataProcessor.getInstance().handleMDMAssetData(qData);
        }
        catch (final Exception e) {
            MDMSDPQueueDataProcessor.SDPINTEGLOGGER.log(Level.WARNING, "Exception in MDMSDPQueueDataProcessor - processData", e);
        }
    }
    
    static {
        SDPINTEGLOGGER = Logger.getLogger("MDMSDPIntegrationLog");
    }
}
