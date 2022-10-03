package com.me.mdm.chrome.agent.wakeup;

import com.me.mdm.chrome.agent.ChromeDeviceManager;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.MemoryOnlyDCQueueDataProcessor;

public class InServerChromeWakeupProcessor extends MemoryOnlyDCQueueDataProcessor
{
    private Logger logger;
    
    public InServerChromeWakeupProcessor() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    public void processData(final DCQueueData qData) {
        try {
            final JSONObject wakeupData = new JSONObject(qData.queueData.toString());
            this.logger.log(Level.INFO, "Going to Wakeup :: {0}", wakeupData.get("ActAs"));
            String id;
            if (String.valueOf(wakeupData.get("ActAs")).equalsIgnoreCase("Device")) {
                id = String.valueOf(wakeupData.get("UDID"));
                ChromeDeviceManager.getInstance().startDeviceAgentWakeup(id, wakeupData.getJSONObject("GOOGLE_ESA"));
            }
            else {
                id = String.valueOf(wakeupData.get("GUID"));
                ChromeDeviceManager.getInstance().startUserAgentWakeup(id, wakeupData.getJSONObject("GOOGLE_ESA"));
            }
            this.logger.log(Level.INFO, "Wakeup for id {0}", id);
        }
        catch (final Exception ex) {
            Logger.getLogger(InServerChromeWakeupProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
