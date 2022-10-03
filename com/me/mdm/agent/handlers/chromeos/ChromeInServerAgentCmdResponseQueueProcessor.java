package com.me.mdm.agent.handlers.chromeos;

import java.util.Map;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.server.command.CommandStatusHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.inv.MDMInvDataHandler;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import org.json.JSONException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.command.CommandProcessor;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.CommandUtil;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.agent.handlers.BaseAppCommandQueueProcessor;

public class ChromeInServerAgentCmdResponseQueueProcessor extends BaseAppCommandQueueProcessor
{
    @Override
    protected void processCommand() throws JSONException {
        Boolean shouldDeleteCommand = Boolean.FALSE;
        if (this.commandResponse.responseType.startsWith("AssetScan") && !this.commandResponse.responseType.contains("AssetScanContainer")) {
            shouldDeleteCommand = this.processAssetScan(this.commandResponse.commandUUID);
        }
        else {
            final String strData = (String)this.queueDataObject.queueData;
            try {
                final HashMap<String, String> hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject(strData));
                final String responsedData = hmap.get("CommandResponse");
                CommandUtil.getInstance().processCommand(responsedData, this.queueDataObject.customerID, hmap, this.queueDataObject.queueDataType, this.queueDataObject);
            }
            catch (final JSONException ex) {
                CommandProcessor.LOGGER.log(Level.SEVERE, "Exception while processing android command", (Throwable)ex);
            }
        }
        if (shouldDeleteCommand) {
            DeviceCommandRepository.getInstance().deleteResourceCommand(this.commandResponse.commandUUID, this.commandResponse.udid);
        }
        else {
            this.updateResourceCommandStatus(12);
        }
    }
    
    private boolean processAssetScan(final String commandUUID) {
        try {
            String remark = "";
            int errorCode = -1;
            int commandStatus = -1;
            if (this.commandResponse.status.equalsIgnoreCase("Error")) {
                errorCode = this.queueData.optInt("ErrorCode", -1);
                final String commandName = I18N.getMsg("dc.common.SCAN_NOW", new Object[0]);
                final DeviceDetails deviceDetails = new DeviceDetails(this.commandResponse.resourceID);
                remark = I18N.getMsg("dc.mdm.actionlog.securitycommands.failure", new Object[] { commandName, deviceDetails.name });
                MDMInvDataPopulator.getInstance().updateDeviceScanStus(this.commandResponse.resourceID, 0, remark);
                MDMInvDataPopulator.getInstance().updateDeviceScanToErrorCode(this.commandResponse.resourceID, 12202);
                commandStatus = 0;
            }
            else {
                int scanFlag = 2;
                String scanRemarks = "mdm.scan.scanning_successful";
                MDMInvDataPopulator.getInstance().deleteDeviceScanToErrCode(this.commandResponse.resourceID);
                final MDMInvDataHandler inventory = new MDMInvDataHandler();
                String respJSON = new JSONObject().put("ResponseData", (Object)this.commandResponse.responseData).toString();
                try {
                    final JSONObject details = this.commandResponse.responseData;
                    details.getJSONObject("DeviceDetails").put("DEVICE_LOCAL_TIME", this.queueDataObject.postTime);
                    respJSON = new JSONObject().put("ResponseData", (Object)details).toString();
                }
                catch (final Exception e) {
                    this.logger.log(Level.WARNING, "Exception while adding device time for battery level tracking ", e);
                }
                final Map<String, String> parsedData = JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject(respJSON));
                if (!inventory.mdmInventoryDataPopulator(this.commandResponse.resourceID, parsedData, 0)) {
                    scanFlag = 0;
                    scanRemarks = "dc.db.mdm.scan.remarks.fetch_data_failed";
                    MDMInvDataPopulator.getInstance().updateDeviceScanStus(this.commandResponse.resourceID, scanFlag, scanRemarks);
                    MDMInvDataPopulator.getInstance().updateDeviceScanToErrorCode(this.commandResponse.resourceID, 12203);
                    commandStatus = 0;
                }
                else {
                    MDMInvDataPopulator.getInstance().updateDeviceScanStus(this.commandResponse.resourceID, scanFlag, scanRemarks);
                    CustomerInfoUtil.getInstance();
                    if (!CustomerInfoUtil.isSAS()) {
                        MDMApiFactoryProvider.getSDPIntegrationAPI().postMDMDataToSDP(this.queueDataObject, 21);
                    }
                    commandStatus = 2;
                }
                remark = "dc.mdm.actionlog.inv.device_scan_success";
            }
            final String sDeviceName = ManagedDeviceHandler.getInstance().getDeviceName(this.commandResponse.resourceID);
            final Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
            final JSONObject commandStatusJSON = new CommandStatusHandler().getRecentCommandInfo(this.commandResponse.resourceID, commandId);
            if (commandStatusJSON.has("ADDED_BY")) {
                commandStatusJSON.put("REMARKS", (Object)remark);
                commandStatusJSON.put("COMMAND_STATUS", commandStatus);
                commandStatusJSON.put("RESOURCE_ID", (Object)this.commandResponse.resourceID);
                if (errorCode != -1) {
                    commandStatusJSON.put("ERROR_CODE", errorCode);
                }
                final String userName = DMUserHandler.getUserNameFromUserID(JSONUtil.optLongForUVH(commandStatusJSON, "ADDED_BY", Long.valueOf(-1L)));
                new CommandStatusHandler().populateCommandStatus(commandStatusJSON);
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2041, null, userName, remark, sDeviceName, CustomerInfoUtil.getInstance().getCustomerIDForResID(this.commandResponse.resourceID));
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(ChromeInServerAgentCmdResponseQueueProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    private void updateResourceCommandStatus(final int commandStatusToUpdate) {
        final Long commandID = DeviceCommandRepository.getInstance().getCommandID(this.commandResponse.commandUUID);
        DeviceCommandRepository.getInstance().updateResourceCommandStatus(commandID, this.commandResponse.udid, 1, commandStatusToUpdate);
    }
}
