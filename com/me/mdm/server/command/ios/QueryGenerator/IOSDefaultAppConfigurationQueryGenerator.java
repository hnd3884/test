package com.me.mdm.server.command.ios.QueryGenerator;

import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import com.me.mdm.core.auth.APIKey;
import com.adventnet.sym.server.mdm.command.DynamicVariableHandler;
import com.dd.plist.NSArray;
import com.me.mdm.server.agent.DiscoveryServiceHandler;
import com.dd.plist.NSObject;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import com.dd.plist.NSDictionary;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import org.json.JSONObject;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class IOSDefaultAppConfigurationQueryGenerator implements CommandQueryCreator
{
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) throws Exception {
        final JSONObject apiKeyJson = new JSONObject();
        final Long erid = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestIdFromUdid(strUDID);
        apiKeyJson.put("ENROLLMENT_REQUEST_ID", (Object)erid);
        final APIKey key = MDMDeviceAPIKeyGenerator.getInstance().generateAPIKey(apiKeyJson);
        final IOSCommandPayload commandPayload = PayloadHandler.getInstance().createCommandPayload("Settings");
        final NSDictionary commandDict = new NSDictionary();
        commandDict.put("Item", (Object)"ApplicationConfiguration");
        commandDict.put("Identifier", (Object)"com.manageengine.mdm.iosagent");
        commandDict.put("Configuration", (NSObject)MDMiOSEntrollmentUtil.getMDMDefaultAppConfiguration());
        DiscoveryServiceHandler.getInstance().setIOSAgentCommDetails(commandDict, key);
        PayloadHandler.getInstance().addKioskAppConfigurationCommand(commandDict, resourceID);
        final NSArray configArray = new NSArray(1);
        configArray.setValue(0, (Object)commandDict);
        commandPayload.getCommandDict().put("Settings", (NSObject)configArray);
        commandPayload.setCommandUUID(deviceCommand.commandUUID, Boolean.FALSE);
        String strQuery = DynamicVariableHandler.replaceDynamicVariables(commandPayload.toString(), strUDID);
        if (key != null) {
            strQuery = MDMDeviceAPIKeyGenerator.getInstance().replaceDeviceAPIKeyPlaceHolder(strQuery, key, false, strUDID);
        }
        return strQuery;
    }
}
