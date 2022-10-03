package com.me.mdm.agent.handlers;

import com.adventnet.sym.server.mdm.command.DeviceMessage;
import com.adventnet.sym.server.mdm.iosnativeapp.payload.IOSNativeAppCommandPayload;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.iosnativeapp.payload.IOSNativeAppPayloadHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.adventnet.sym.server.mdm.DeviceDetails;
import java.util.List;
import com.me.mdm.server.enrollment.task.InactiveDevicePolicyTask;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import java.util.Map;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.adventnet.sym.server.mdm.queue.MDMDataQueueUtil;
import com.adventnet.sym.server.mdm.queue.QueueControllerHelper;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import java.util.logging.Logger;

public abstract class BaseProcessDeviceRequestHandler implements ProcessDeviceRequestHandler
{
    protected String separator;
    protected Logger accesslogger;
    protected Logger mdmdevicedatalogger;
    protected Logger logger;
    private Logger queueLogger;
    
    public BaseProcessDeviceRequestHandler() {
        this.separator = "\t";
        this.accesslogger = Logger.getLogger("MDMCommandsLogger");
        this.mdmdevicedatalogger = Logger.getLogger("MDMDeviceDataLogger");
        this.logger = Logger.getLogger("MDMLogger");
        this.queueLogger = Logger.getLogger("MDMQueueBriefLogger");
    }
    
    @Override
    public String processRequest(final DeviceRequest request) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected void addResponseToQueue(final DeviceRequest deviceRequest, final String responseBuffer, final int queueDataType) throws Exception {
        final long postTime = System.currentTimeMillis();
        if (deviceRequest.deviceUDID == null) {
            try {
                DMSecurityLogger.info(this.logger, BaseProcessDeviceRequestHandler.class.getName(), "addResponseToQueue", "Device UDID not set for this message {0}", (Object)responseBuffer);
                final JSONObject deviceJSON = new JSONObject(deviceRequest.deviceRequestData.toString());
                final String udids = String.valueOf(deviceJSON.get("UDID"));
                deviceRequest.deviceUDID = udids;
            }
            catch (final Exception exp) {
                this.logger.log(Level.SEVERE, "Exception occured while getting the device UDID");
            }
        }
        final String qFileName = deviceRequest.customerID + "-" + deviceRequest.deviceUDID + "-" + postTime + ".txt";
        final DCQueueData queueData = new DCQueueData();
        queueData.fileName = qFileName;
        queueData.postTime = postTime;
        queueData.queueData = responseBuffer;
        queueData.customerID = deviceRequest.customerID;
        final Map queueExtnTableData = new HashMap();
        final Long resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceRequest.deviceUDID);
        queueExtnTableData.put("CUSTOMER_ID", deviceRequest.customerID);
        queueData.queueExtnTableData = queueExtnTableData;
        queueData.queueDataType = queueDataType;
        final String queueName = QueueControllerHelper.getInstance().getQueueName(queueData.queueDataType, (String)queueData.queueData);
        if (resourceId != null) {
            queueExtnTableData.put("RESOURCE_ID", resourceId);
        }
        this.queueLogger.log(Level.INFO, "QueueName : {0}{1}AddingToQueue{2}{3}{4}{5}{6}{7}", new Object[] { queueName, this.separator, this.separator, queueData.fileName, this.separator, MDMDataQueueUtil.getInstance().getPlatformNameForLogging(queueDataType), this.separator, String.valueOf(postTime) });
        final DCQueue queue = DCQueueHandler.getQueue(queueName);
        this.mdmdevicedatalogger.log(Level.INFO, "Queue data added - FileName : {0}\t QueuDataType : {1}", new Object[] { queueData.fileName, queueData.queueDataType });
        queue.addToQueue(queueData);
    }
    
    protected void initServerRequest(final DeviceRequest deviceRequest, final int repositoryType) throws Exception {
        this.updateAgentLastContact(deviceRequest);
        DeviceCommandRepository.getInstance().loadCommandsForDevice(deviceRequest.deviceUDID, repositoryType);
    }
    
    protected void updateAgentLastContact(final DeviceRequest deviceRequest) {
        if (deviceRequest.resourceID != null) {
            final List resourceList = new ArrayList();
            resourceList.add(deviceRequest.resourceID);
            MDMUtil.addOrupdateAgentLastContact(deviceRequest.resourceID, new Long(System.currentTimeMillis()), null, null, new Long(0L), "");
            new InactiveDevicePolicyTask().updateInactiveDeviceRemarksAfterContact(deviceRequest.resourceID);
        }
    }
    
    protected final String getNextDeviceCommandQuery(final DeviceRequest request) throws Exception {
        String responseData = null;
        DeviceCommand nextCommand = this.getNextDeviceCommand(request);
        if (nextCommand != null && nextCommand.commandType.equalsIgnoreCase("BATTERY_CONFIGURATION")) {
            final DeviceDetails deviceDetails = new DeviceDetails(request.deviceUDID);
            if (deviceDetails.platform == 1 && deviceDetails.agentVersionCode < 1526L) {
                nextCommand = this.getNextDeviceCommand(request);
                this.logger.log(Level.INFO, "Battery Configuration for iOS Device: Version is too low, so restricting this command");
                final Long commandID = DeviceCommandRepository.getInstance().getCommandID("BATTERY_CONFIGURATION");
                DeviceCommandRepository.getInstance().updateResourceCommandStatus(commandID, deviceDetails.resourceId, 2, 12);
            }
        }
        if (nextCommand != null) {
            this.logger.log(Level.INFO, "generateQuery command: {0} commandUUID: {1} UDID: {2}", new Object[] { nextCommand.commandType, nextCommand.commandUUID, request.deviceUDID });
            responseData = this.getNextDeviceCommandQuery(nextCommand, request);
            final String accessMessage = "DATA-OUT: " + nextCommand.commandType + this.separator + request.resourceID + this.separator + request.deviceUDID + this.separator + "Command-Sent" + this.separator + MDMUtil.getCurrentTimeInMillis();
            this.accesslogger.log(Level.INFO, accessMessage);
        }
        return responseData;
    }
    
    private final DeviceCommand getNextDeviceCommand(final DeviceRequest request) {
        return DeviceCommandRepository.getInstance().getDeviceCommandFromCache(request.deviceUDID, request.repositoryType);
    }
    
    protected String getNextDeviceCommandQuery(final DeviceCommand nextCommand, final DeviceRequest request) throws Exception {
        final String command = nextCommand.commandType;
        String strQuery = null;
        try {
            if (command.equalsIgnoreCase("SyncAgentSettings")) {
                final DeviceDetails device = new DeviceDetails(request.deviceUDID);
                final IOSNativeAppCommandPayload createLocationSettingsCommand = IOSNativeAppPayloadHandler.getInstance().createLocationSettingsCommand(device, request.deviceUDID);
                strQuery = createLocationSettingsCommand.toString();
            }
            else if (command.equalsIgnoreCase("AgentUpgrade")) {
                final IOSNativeAppCommandPayload createUpgradeCommand = IOSNativeAppPayloadHandler.getInstance().createAgentUpgradeCommand(request);
                strQuery = createUpgradeCommand.toString();
            }
            else if (command.equalsIgnoreCase("SyncPrivacySettings")) {
                final IOSNativeAppCommandPayload createPrivacySettingCommand = IOSNativeAppPayloadHandler.getInstance().createSyncPrivacySettingsCommand();
                strQuery = createPrivacySettingCommand.toString();
            }
            else if (command.equalsIgnoreCase("LanguagePackUpdate")) {
                final IOSNativeAppCommandPayload languageCommand = IOSNativeAppPayloadHandler.getInstance().createLanguageLicenseCommand();
                strQuery = languageCommand.toString();
            }
            else if (command.equalsIgnoreCase("LocationConfiguration")) {
                final DeviceDetails device = new DeviceDetails(request.deviceUDID);
                final IOSNativeAppCommandPayload locationConfigCommand = IOSNativeAppPayloadHandler.getInstance().createLocationConfigCommand(device, request.deviceUDID);
                strQuery = locationConfigCommand.toString();
            }
            else if (command.equalsIgnoreCase("RemoteSession")) {
                final DeviceDetails device = new DeviceDetails(request.deviceUDID);
                final IOSNativeAppCommandPayload remoteSessionCommand = IOSNativeAppPayloadHandler.getInstance().createRemoteSessionCommand(device, request.deviceUDID);
                strQuery = remoteSessionCommand.toString();
            }
            else if (command.equalsIgnoreCase("TermsOfUse")) {
                final IOSNativeAppCommandPayload syncDocumentsCommand = IOSNativeAppPayloadHandler.getInstance().createTermsSyncCommand();
                strQuery = syncDocumentsCommand.toString();
            }
            else if (command.equalsIgnoreCase("ReregisterNotificationToken")) {
                final IOSNativeAppCommandPayload syncDocumentsCommand = IOSNativeAppPayloadHandler.getInstance().createReRegisterFCMTokenCommand();
                strQuery = syncDocumentsCommand.toString();
            }
        }
        catch (final JSONException ex) {
            this.logger.log(Level.WARNING, "Exception occurred in BaseProcessDeviceRequestHandler.getMextDeviceCommandQuery(){0}", (Throwable)ex);
        }
        return strQuery;
    }
    
    protected JSONObject constructMessage(final DeviceMessage deviceMsg) throws JSONException {
        final JSONObject response = new JSONObject();
        response.put("MessageType", (Object)deviceMsg.messageType);
        response.put("Status", (Object)deviceMsg.status);
        response.put("MessageResponse", (Object)deviceMsg.messageResponse);
        return response;
    }
}
