package com.me.mdm.agent.handlers.ios;

import com.adventnet.sym.server.mdm.iosnativeapp.payload.IOSNativeAppCommandPayload;
import com.adventnet.sym.server.mdm.iosnativeapp.payload.IOSNativeAppPayloadHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.io.File;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.agent.handlers.DeviceRequest;
import com.me.mdm.agent.handlers.BaseProcessDeviceRequestHandler;

public class IOSAppCommandRequestHandler extends BaseProcessDeviceRequestHandler
{
    @Override
    public String processRequest(final DeviceRequest request) throws Exception {
        final HashMap<String, String> hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject((String)request.deviceRequestData));
        String responseData = null;
        final String status = hmap.get("Status");
        final String udid = request.deviceUDID;
        final Long deviceId = request.resourceID;
        if (status != null && status.equalsIgnoreCase("Idle")) {
            this.accesslogger.log(Level.INFO, "DEVICE-IN: IdleRequestReceived{0}{1}{2}{3}{4}IdleReceived{5}{6}", new Object[] { this.separator, deviceId, this.separator, udid, this.separator, this.separator, MDMUtil.getCurrentTimeInMillis() });
            this.initServerRequest(request, request.repositoryType);
        }
        if (status != null && !status.equalsIgnoreCase("Idle")) {
            IosNativeAppHandler.getInstance().addorUpdateIOSAgentInstallationStatus(deviceId, 1);
            this.addResponseToQueue(request, (String)request.deviceRequestData, 140);
        }
        else {
            final String jailBrokenStr = hmap.get("Jailbroken");
            if (jailBrokenStr != null && deviceId != null) {
                final boolean isJailBroken = jailBrokenStr.equals("1");
                MDMInvDataPopulator.getInstance().updateJailBrokenInfo(deviceId, isJailBroken);
            }
        }
        responseData = this.getNextDeviceCommandQuery(request);
        return responseData;
    }
    
    @Override
    protected void updateAgentLastContact(final DeviceRequest deviceRequest) {
        if (deviceRequest.resourceID != null) {
            IosNativeAppHandler.getInstance().addorUpdateIOSAgentInstallationStatus(deviceRequest.resourceID, 1);
        }
    }
    
    @Override
    protected String getNextDeviceCommandQuery(final DeviceCommand deviceCommand, final DeviceRequest request) throws Exception {
        final String command = deviceCommand.commandType;
        final String commandUUID = deviceCommand.commandUUID;
        final String deviceUDID = request.deviceUDID;
        final Long resourceID = request.resourceID;
        final Long customerID = request.customerID;
        String strQuery = null;
        if (command.equalsIgnoreCase("DeviceCompliance") || command.equalsIgnoreCase("RemoveDeviceCompliance")) {
            final String profileParentPath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath();
            final String complianceFullPath = profileParentPath + File.separator + deviceCommand.commandFilePath;
            this.logger.log(Level.INFO, " generateQuery Command: complianceFullPath:  ", complianceFullPath);
            strQuery = PayloadHandler.getInstance().readProfileFromFile(complianceFullPath);
        }
        else if (command.equalsIgnoreCase("BATTERY_CONFIGURATION")) {
            final DeviceDetails device = new DeviceDetails(deviceUDID);
            final IOSNativeAppCommandPayload batteryCommand = IOSNativeAppPayloadHandler.getInstance().createBatteryConfigurationCommand(device);
            batteryCommand.setCommandUUID(commandUUID);
            strQuery = batteryCommand.toString();
        }
        else {
            strQuery = super.getNextDeviceCommandQuery(deviceCommand, request);
        }
        return strQuery;
    }
}
