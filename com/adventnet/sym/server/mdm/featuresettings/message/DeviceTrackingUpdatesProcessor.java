package com.adventnet.sym.server.mdm.featuresettings.message;

import com.me.mdm.server.settings.wifi.MdDeviceWifiSSIDDBHandler;
import org.json.JSONArray;
import com.me.mdm.server.settings.battery.MdDeviceBatteryDetailsDBHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.agent.handlers.DeviceMessageRequest;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class DeviceTrackingUpdatesProcessor extends DCQueueDataProcessor
{
    public static final Logger LOGGER;
    Logger queueLogger;
    String separator;
    
    public DeviceTrackingUpdatesProcessor() {
        this.queueLogger = Logger.getLogger("MDMQueueBriefLogger");
        this.separator = "\t";
    }
    
    public void processData(final DCQueueData qData) {
        final long sysTime = System.currentTimeMillis();
        this.queueLogger.log(Level.INFO, "Device tracking updates started{0}{1}{2} Time spent waiting in queue - {3}", new Object[] { this.separator, qData.fileName, this.separator, sysTime - qData.postTime });
        qData.customerID = qData.queueExtnTableData.get("CUSTOMER_ID");
        final JSONObject queueDataJson = new JSONObject(qData.queueData.toString());
        final DeviceMessageRequest messageRequest = new DeviceMessageRequest(queueDataJson);
        final String sUDID = messageRequest.udid;
        final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(sUDID);
        final String strData = (String)qData.queueData;
        final JSONObject message = new JSONObject(strData);
        final String msgType = messageRequest.messageType;
        message.put("CUSTOMER_ID", (Object)(qData.customerID + ""));
        if (msgType.equals("BatteryStatusUpdate")) {
            this.processBatteryMessage(resourceID, message);
        }
        else if (msgType.equalsIgnoreCase("WifiSsidMsg")) {
            this.processWifiSSIDMessage(resourceID, message);
        }
        else {
            this.queueLogger.log(Level.WARNING, "Dropping old queue data: {0}", new Object[] { qData });
        }
        this.queueLogger.log(Level.INFO, "Device tracking updates ended{0}{1}{2} Time spent for updating tracking details - {3}", new Object[] { this.separator, qData.fileName, this.separator, System.currentTimeMillis() - sysTime });
    }
    
    private void processBatteryMessage(final Long resourceID, final JSONObject message) {
        try {
            final JSONArray messageJsonArr = message.optJSONArray("Message");
            MdDeviceBatteryDetailsDBHandler.addOrUpdateBatteryDetails(resourceID, messageJsonArr);
        }
        catch (final Exception e) {
            DeviceTrackingUpdatesProcessor.LOGGER.log(Level.SEVERE, "Exception while processing IOS updates", e);
        }
    }
    
    private void processWifiSSIDMessage(final Long resourceID, final JSONObject message) {
        try {
            final JSONArray messageJsonArr = message.optJSONArray("Message");
            MdDeviceWifiSSIDDBHandler.getInstance().addOrUpdateWifiSSIDDetails(resourceID, messageJsonArr);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in process WIfi ssid Message", ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
