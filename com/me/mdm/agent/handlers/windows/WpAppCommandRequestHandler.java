package com.me.mdm.agent.handlers.windows;

import com.adventnet.sym.server.mdm.iosnativeapp.payload.IOSNativeAppCommandPayload;
import java.util.logging.Level;
import com.me.mdm.server.windows.apps.nativeapp.payload.WindowsNativeAppPayloadHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.iosnativeapp.payload.IOSNativeAppPayloadHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import org.json.JSONObject;
import com.me.mdm.agent.handlers.DeviceRequest;
import com.me.mdm.agent.handlers.BaseProcessDeviceRequestHandler;

public class WpAppCommandRequestHandler extends BaseProcessDeviceRequestHandler
{
    @Override
    public String processRequest(final DeviceRequest request) throws Exception {
        String responseData = "{}";
        final String status = String.valueOf(new JSONObject((String)request.deviceRequestData).get("Status"));
        if (!status.equalsIgnoreCase("Idle")) {
            this.addResponseToQueue(request, (String)request.deviceRequestData, 143);
        }
        else {
            this.initServerRequest(request, 2);
            WindowsMigrationUtil.getInstance().checkAndAddMigrationCommand(request.resourceID, request.requestMap, 2);
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
    protected String getNextDeviceCommandQuery(final DeviceCommand nextCommand, final DeviceRequest request) throws Exception {
        final String command = nextCommand.commandType;
        String strQuery = null;
        try {
            if (command.equalsIgnoreCase("AppNotificationCredential")) {
                final IOSNativeAppCommandPayload channelUriCommand = IOSNativeAppPayloadHandler.getInstance().createNotificationCredentialCommand();
                strQuery = channelUriCommand.toString();
            }
            else if (command.equalsIgnoreCase("GetLocation")) {
                final IOSNativeAppCommandPayload locationCommand = IOSNativeAppPayloadHandler.getInstance().createLocationCommand();
                strQuery = locationCommand.toString();
            }
            else if (command.equalsIgnoreCase("SyncAppCatalog")) {
                final IOSNativeAppCommandPayload syncAppCatalogCommand = IOSNativeAppPayloadHandler.getInstance().createSyncAppCatalogCommand(request.resourceID);
                strQuery = syncAppCatalogCommand.toString();
            }
            else if (command.equalsIgnoreCase("AppCatalogSummary")) {
                final IOSNativeAppCommandPayload appCatalogStatusSummary = IOSNativeAppPayloadHandler.getInstance().createAppCatalogStatusSummaryCommand(request.resourceID);
                strQuery = appCatalogStatusSummary.toString();
            }
            else if (command.equalsIgnoreCase("CorporateWipe") || command.equalsIgnoreCase("RemoveDevice")) {
                final IOSNativeAppCommandPayload corporateWipeCommand = IOSNativeAppPayloadHandler.getInstance().createCorporateWipeCommand(request.resourceID);
                strQuery = corporateWipeCommand.toString();
                if (command.equalsIgnoreCase("RemoveDevice")) {
                    DeviceCommandRepository.getInstance().deleteResourceCommand("RemoveDevice", request.deviceUDID, 2);
                    if (!DeviceCommandRepository.getInstance().hasDeviceCommandInCacheOrRepo(request.deviceUDID)) {
                        ManagedDeviceHandler.getInstance().removeDeviceInTrash(ManagedDeviceHandler.getInstance().getResourceIDFromUDID(request.deviceUDID));
                    }
                }
            }
            else if (command.equalsIgnoreCase("TermsOfUse")) {
                final IOSNativeAppCommandPayload termsCommand = IOSNativeAppPayloadHandler.getInstance().createTermsSyncCommand();
                strQuery = termsCommand.toString();
            }
            else if (command.equalsIgnoreCase("SyncAgentSettings")) {
                final DeviceDetails device = new DeviceDetails(request.deviceUDID);
                final IOSNativeAppCommandPayload syncAgentCommnad = new WindowsNativeAppPayloadHandler().createSyncAgentSettingsCommand(device, request.deviceUDID);
                strQuery = syncAgentCommnad.toString();
            }
            else if (command.equalsIgnoreCase("SyncDocuments")) {
                final IOSNativeAppCommandPayload syncAgentCommnad2 = new WindowsNativeAppPayloadHandler().createCommandPayload("SyncDocuments");
                strQuery = syncAgentCommnad2.toString();
            }
            else {
                strQuery = super.getNextDeviceCommandQuery(nextCommand, request);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred in WpAppCommandRequestHandler.getNextDeviceCommandQuery(){0}", ex);
        }
        return strQuery;
    }
}
