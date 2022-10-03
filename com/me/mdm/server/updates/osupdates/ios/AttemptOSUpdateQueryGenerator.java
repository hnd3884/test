package com.me.mdm.server.updates.osupdates.ios;

import com.adventnet.persistence.DataObject;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import com.me.mdm.server.updates.osupdates.ExtendedOSDetailsDataHandler;
import com.me.mdm.server.updates.osupdates.OSUpdatesDataHandler;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.dd.plist.NSObject;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSArray;
import com.me.mdm.server.updates.osupdates.ResourceOSUpdateDataHandler;
import org.json.JSONObject;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class AttemptOSUpdateQueryGenerator implements CommandQueryCreator
{
    private static final Logger LOGGER;
    
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) {
        String query = null;
        try {
            final IOSCommandPayload payload = PayloadHandler.getInstance().createCommandPayload("ScheduleOSUpdate");
            payload.setCommandUUID(deviceCommand.commandUUID, false);
            this.createScheduleUpdateCommand(payload, resourceID, deviceCommand);
            query = payload.toString();
        }
        catch (final Exception e) {
            AttemptOSUpdateQueryGenerator.LOGGER.log(Level.SEVERE, "error:", e);
        }
        return query;
    }
    
    public void createScheduleUpdateCommand(final IOSCommandPayload payload, final Long resourceID, final DeviceCommand deviceCommand) {
        try {
            final SequentialSubCommand subCommand = SeqCmdUtils.getInstance().getIfSequentialCommandResponse(resourceID, deviceCommand.commandUUID);
            boolean isProperVersionAvailable = false;
            JSONObject commandScopeParams = new JSONObject();
            if (subCommand != null) {
                final JSONObject params = subCommand.params;
                commandScopeParams = params.optJSONObject("cmdScopeParams");
                if (commandScopeParams != null && commandScopeParams.has("POLICY_TYPE") && commandScopeParams.has("DEFER_DAYS")) {
                    isProperVersionAvailable = true;
                }
            }
            if (isProperVersionAvailable) {
                AttemptOSUpdateQueryGenerator.LOGGER.log(Level.INFO, "Going to send Attempt OSUpdate query with prod version");
                final Integer policyType = commandScopeParams.getInt("POLICY_TYPE");
                Integer deferDays = 0;
                if (policyType.equals(3)) {
                    deferDays = commandScopeParams.getInt("DEFER_DAYS");
                }
                final long currentTimeinMillisec = System.currentTimeMillis();
                final Long deferTimeInMS = deferDays * 24L * 60L * 60L * 1000L;
                final Long detectedTimeInMS = currentTimeinMillisec - deferTimeInMS;
                final JSONObject updateJSON = new ResourceOSUpdateDataHandler().getApplicableOSVersionForResource(detectedTimeInMS, resourceID);
                if (updateJSON.has("VERSION")) {
                    final String prodKey = updateJSON.getString("PRODUCT_KEY");
                    final String prodVersion = updateJSON.getString("VERSION");
                    final NSArray osUpdateArray = new NSArray(1);
                    final NSDictionary osUpdateDict = new NSDictionary();
                    osUpdateDict.put("ProductVersion", (Object)prodVersion);
                    osUpdateDict.put("ProductKey", (Object)prodKey);
                    osUpdateDict.put("InstallAction", (Object)"Default");
                    osUpdateArray.setValue(0, (Object)osUpdateDict);
                    payload.getCommandDict().put("Updates", (NSObject)osUpdateArray);
                }
            }
            else {
                AttemptOSUpdateQueryGenerator.LOGGER.log(Level.INFO, "Going to send Attempt OSUpdate query with prod key");
                final Long detectedUpdateID = new ResourceOSUpdateDataHandler().getLatestOSVersionUpdateIDForResource(new VersionChecker(), resourceID);
                if (detectedUpdateID != null) {
                    final DataObject updateDetailsDO = new OSUpdatesDataHandler().getOSUpdateDetails(detectedUpdateID, new IOSUpdatesDetailsHandler());
                    if (!updateDetailsDO.isEmpty()) {
                        final String prodKey2 = updateDetailsDO.getFirstRow("IOSUpdates").get("PRODUCT_KEY").toString();
                        final String prodVersin = updateDetailsDO.getFirstRow("OSUpdates").get("VERSION").toString();
                        final NSArray osUpdateArray2 = new NSArray(1);
                        final NSDictionary osUpdateDict2 = new NSDictionary();
                        osUpdateDict2.put("ProductKey", (Object)prodKey2);
                        osUpdateDict2.put("InstallAction", (Object)"Default");
                        osUpdateArray2.setValue(0, (Object)osUpdateDict2);
                        payload.getCommandDict().put("Updates", (NSObject)osUpdateArray2);
                    }
                }
            }
        }
        catch (final Exception e) {
            AttemptOSUpdateQueryGenerator.LOGGER.log(Level.SEVERE, "Exception in attempt osupdate query", e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
