package com.adventnet.sym.server.mdm.ios.payload.transform;

import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.dd.plist.NSObject;
import com.dd.plist.NSDictionary;
import com.me.mdm.server.profiles.config.MacEnergySaverPolicyConfigHandler;
import com.adventnet.sym.server.mdm.ios.payload.MacEnergySaverPolicyPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2MacEnergySaverPolicyPayload implements DO2Payload
{
    private Logger logger;
    
    public DO2MacEnergySaverPolicyPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        try {
            final MacEnergySaverPolicyPayload[] payloadArray = { null };
            final JSONObject jsonObject = new MacEnergySaverPolicyConfigHandler().DOToAPIJSON(dataObject, "macenergysaverpolicy").getJSONObject(0);
            final MacEnergySaverPolicyPayload payload = new MacEnergySaverPolicyPayload(1, "MDM", "com.manageengine.energy.saver.profile", "Energy Saver Policy", "com.apple.MCX");
            payload.setSleepDisabled(jsonObject.getBoolean("SLEEP_DISABLED".toLowerCase()));
            payload.setDestroyFVKeyOnStandby(jsonObject.getBoolean("DESTROY_FV_STANDBY".toLowerCase()));
            if (jsonObject.has("DESKTOP_SETTINGS")) {
                payload.setDesktopSettings(this.getEnergySaverConfigurationDict(jsonObject.getJSONObject("DESKTOP_SETTINGS")));
            }
            if (jsonObject.has("PORTABLE_BATTERY_SETTINGS")) {
                payload.setPortableBatterySettings(this.getEnergySaverConfigurationDict(jsonObject.getJSONObject("PORTABLE_BATTERY_SETTINGS")));
            }
            if (jsonObject.has("PORTABLE_ACPOWER_SETTINGS")) {
                payload.setPortableACPowerSettings(this.getEnergySaverConfigurationDict(jsonObject.getJSONObject("PORTABLE_ACPOWER_SETTINGS")));
            }
            final NSDictionary scheduleDict = new NSDictionary();
            if (jsonObject.has("SCHEDULE_SETTINGS_POWEROFF")) {
                scheduleDict.put("RepeatingPowerOff", (NSObject)this.getEnergyScheduleDict(jsonObject.getJSONObject("SCHEDULE_SETTINGS_POWEROFF")));
            }
            if (jsonObject.has("SCHEDULE_SETTINGS_POWERON")) {
                scheduleDict.put("RepeatingPowerOn", (NSObject)this.getEnergyScheduleDict(jsonObject.getJSONObject("SCHEDULE_SETTINGS_POWERON")));
            }
            if (!scheduleDict.isEmpty()) {
                payload.setSchedule(scheduleDict);
            }
            payloadArray[0] = payload;
            return payloadArray;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Unable to convert dataObject to payload for macOS Energy saver", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private NSDictionary getEnergySaverConfigurationDict(final JSONObject jsonObject) {
        final NSDictionary dictionary = new NSDictionary();
        dictionary.put("Automatic Restart On Power Loss", (Object)this.convertBooleanToInt(jsonObject.getBoolean("RESTART_AFTER_POWER_LOSS")));
        dictionary.put("Disk Sleep Timer", (Object)jsonObject.getInt("DISK_SLEEP_TIME"));
        dictionary.put("Display Sleep Timer", (Object)jsonObject.getInt("DISPLAY_SLEEP_TIME"));
        dictionary.put("Dynamic Power Step", (Object)this.convertBooleanToInt(jsonObject.getBoolean("DYNAMIC_POWER_SETUP")));
        dictionary.put("Reduce Processor Speed", (Object)this.convertBooleanToInt(jsonObject.getBoolean("REDUCE_PROCESSOR_SPEED")));
        dictionary.put("System Sleep Timer", (Object)jsonObject.getInt("SYSTEM_SLEEP_TIME"));
        dictionary.put("Wake On LAN", (Object)this.convertBooleanToInt(jsonObject.getBoolean("WAKE_ON_LAN")));
        dictionary.put("Wake On Modem Ring", (Object)this.convertBooleanToInt(jsonObject.getBoolean("WAKE_ON_MODEM_RING")));
        return dictionary;
    }
    
    private NSDictionary getEnergyScheduleDict(final JSONObject jsonObject) {
        final NSDictionary dictionary = new NSDictionary();
        dictionary.put("eventtype", (Object)this.getEventName(jsonObject.getInt("EVENT_TYPE")));
        dictionary.put("time", (Object)jsonObject.getInt("TIME_IN_MINUTES"));
        dictionary.put("weekdays", (Object)jsonObject.getInt("WEEKDAY"));
        return dictionary;
    }
    
    private String getEventName(final Integer eventType) {
        switch (eventType) {
            case 1: {
                return "wake";
            }
            case 3: {
                return "wakepoweron";
            }
            case 2: {
                return "poweron";
            }
            case 4: {
                return "sleep";
            }
            case 5: {
                return "shutdown";
            }
            case 6: {
                return "restart";
            }
            default: {
                return null;
            }
        }
    }
    
    private Integer convertBooleanToInt(final Boolean value) {
        return ((boolean)value) ? 1 : 0;
    }
}
