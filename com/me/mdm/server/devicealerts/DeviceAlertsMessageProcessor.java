package com.me.mdm.server.devicealerts;

import org.json.JSONArray;
import org.json.JSONException;
import com.me.mdm.server.compliance.ComplianceHandler;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class DeviceAlertsMessageProcessor
{
    private static DeviceAlertsMessageProcessor deviceAlertsMessageProcessor;
    public Logger logger;
    
    private DeviceAlertsMessageProcessor() {
        this.logger = Logger.getLogger("MDMDeviceComplianceLogger");
    }
    
    public static DeviceAlertsMessageProcessor getInstance() {
        if (DeviceAlertsMessageProcessor.deviceAlertsMessageProcessor == null) {
            DeviceAlertsMessageProcessor.deviceAlertsMessageProcessor = new DeviceAlertsMessageProcessor();
        }
        return DeviceAlertsMessageProcessor.deviceAlertsMessageProcessor;
    }
    
    public void processDeviceAlertsMessage(final JSONObject messageJSON) throws JSONException {
        try {
            this.logger.log(Level.INFO, "--> processDeviceAlertsMessage()");
            final JSONArray alertsJSONArray = messageJSON.getJSONArray("Alerts");
            final Long resourceId = messageJSON.getLong("ResourceId");
            final Long customerId = messageJSON.getLong("CustomerID");
            final String udid = String.valueOf(messageJSON.get("UDID"));
            final int platformType = messageJSON.getInt("PlatformType");
            for (int i = 0; i < alertsJSONArray.length(); ++i) {
                final JSONObject alertJSON = alertsJSONArray.getJSONObject(i);
                final String alertType = String.valueOf(alertJSON.get("AlertType"));
                alertJSON.put("customer_id", (Object)customerId);
                alertJSON.put("resource_id", (Object)resourceId);
                alertJSON.put("platform_type", platformType);
                alertJSON.put("udid", (Object)udid);
                final String s = alertType;
                switch (s) {
                    case "Compliance": {
                        ComplianceHandler.getInstance().processComplianceAlertMessage(alertJSON);
                        break;
                    }
                    default: {
                        this.logger.log(Level.SEVERE, " -- processDeviceAlertsMessage()  >   Invalid alert type  {0}", alertType);
                        break;
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- processDeviceAlertsMessage()   >   Error   ", e);
            throw (JSONException)e;
        }
    }
    
    static {
        DeviceAlertsMessageProcessor.deviceAlertsMessageProcessor = null;
    }
}
