package com.me.mdm.server.integration;

import org.json.simple.JSONObject;
import java.util.logging.Level;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.adventnet.i18n.I18N;
import java.util.logging.Logger;

public class SpiceworksPluginHandler
{
    static final Logger LOGGER;
    private static SpiceworksPluginHandler spiceHandler;
    
    public static SpiceworksPluginHandler getInstance() {
        if (SpiceworksPluginHandler.spiceHandler == null) {
            SpiceworksPluginHandler.spiceHandler = new SpiceworksPluginHandler();
        }
        return SpiceworksPluginHandler.spiceHandler;
    }
    
    public String handleSpiceworksCommand(final Long customerId, final String command) {
        String commandName = "";
        String evaluatorCommand = "";
        try {
            if (command.equalsIgnoreCase("remoteLock")) {
                commandName = I18N.getMsg("dc.mdm.inv.remote_lock", new Object[0]);
                evaluatorCommand = "SPICEWORKS_PLUGIN_REMOTE_LOCK";
            }
            else if (command.equalsIgnoreCase("corporateWipe")) {
                commandName = I18N.getMsg("dc.mdm.inv.corporate_wipe", new Object[0]);
                evaluatorCommand = "SPICEWORKS_PLUGIN_CORPORATE_WIPE";
            }
            else if (command.equalsIgnoreCase("completeWipe")) {
                commandName = I18N.getMsg("dc.mdm.inv.remote_wipe", new Object[0]);
                evaluatorCommand = "SPICEWORKS_PLUGIN_COMPLETE_WIPE";
            }
            else if (command.equalsIgnoreCase("clearPasscode")) {
                commandName = I18N.getMsg("dc.mdm.inv.clear_passcode", new Object[0]);
                evaluatorCommand = "SPICEWORKS_PLUGIN_CLEAR_PASSCODE";
            }
            else if (command.equalsIgnoreCase("remoteAlarm")) {
                commandName = I18N.getMsg("dc.mdm.inv.ring_device", new Object[0]);
                evaluatorCommand = "SPICEWORKS_PLUGIN_REMOTE_ALARM";
            }
            else if (command.equalsIgnoreCase("resetPasscode")) {
                commandName = I18N.getMsg("dc.mdm.inv.reset_passcode", new Object[0]);
                evaluatorCommand = "SPICEWORKS_PLUGIN_RESET_PASSCODE";
            }
            else if (command.equalsIgnoreCase("remoteControl")) {
                commandName = I18N.getMsg("dc.mdm.inv.remote_troubleshoot", new Object[0]);
                evaluatorCommand = "SPICEWORKS_PLUGIN_REMOTE_CONTROL";
            }
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "Integration_Module", evaluatorCommand);
        }
        catch (final Exception ex) {
            SpiceworksPluginHandler.LOGGER.log(Level.WARNING, "Exception in getManagedDeviceList method : {0}", ex);
        }
        return commandName;
    }
    
    public JSONObject getCommandStatusJSON() {
        final JSONObject commandStatusJSON = new JSONObject();
        final JSONObject remoteLockPlatformStatus = new JSONObject();
        remoteLockPlatformStatus.put((Object)1, (Object)true);
        remoteLockPlatformStatus.put((Object)2, (Object)true);
        remoteLockPlatformStatus.put((Object)3, (Object)true);
        commandStatusJSON.put((Object)"remoteLock", (Object)remoteLockPlatformStatus);
        final JSONObject corporateWipePlatformStatus = new JSONObject();
        corporateWipePlatformStatus.put((Object)1, (Object)true);
        corporateWipePlatformStatus.put((Object)2, (Object)true);
        corporateWipePlatformStatus.put((Object)3, (Object)true);
        commandStatusJSON.put((Object)"corporateWipe", (Object)corporateWipePlatformStatus);
        final JSONObject completeWipePlatformStatus = new JSONObject();
        completeWipePlatformStatus.put((Object)1, (Object)true);
        completeWipePlatformStatus.put((Object)2, (Object)true);
        completeWipePlatformStatus.put((Object)3, (Object)true);
        commandStatusJSON.put((Object)"completeWipe", (Object)completeWipePlatformStatus);
        final JSONObject clearPasscodePlatformStatus = new JSONObject();
        clearPasscodePlatformStatus.put((Object)1, (Object)true);
        clearPasscodePlatformStatus.put((Object)2, (Object)true);
        clearPasscodePlatformStatus.put((Object)3, (Object)false);
        commandStatusJSON.put((Object)"clearPasscode", (Object)clearPasscodePlatformStatus);
        final JSONObject remoteAlarmPlatformStatus = new JSONObject();
        remoteAlarmPlatformStatus.put((Object)1, (Object)false);
        remoteAlarmPlatformStatus.put((Object)2, (Object)true);
        remoteAlarmPlatformStatus.put((Object)3, (Object)true);
        commandStatusJSON.put((Object)"remoteAlarm", (Object)remoteAlarmPlatformStatus);
        final JSONObject resetPasscodePlatformStatus = new JSONObject();
        resetPasscodePlatformStatus.put((Object)1, (Object)false);
        resetPasscodePlatformStatus.put((Object)2, (Object)true);
        resetPasscodePlatformStatus.put((Object)3, (Object)true);
        commandStatusJSON.put((Object)"resetPasscode", (Object)resetPasscodePlatformStatus);
        final JSONObject remoteControlPlatformStatus = new JSONObject();
        remoteControlPlatformStatus.put((Object)1, (Object)false);
        remoteControlPlatformStatus.put((Object)2, (Object)true);
        remoteControlPlatformStatus.put((Object)3, (Object)false);
        commandStatusJSON.put((Object)"remoteControl", (Object)remoteControlPlatformStatus);
        return commandStatusJSON;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
        SpiceworksPluginHandler.spiceHandler = null;
    }
}
