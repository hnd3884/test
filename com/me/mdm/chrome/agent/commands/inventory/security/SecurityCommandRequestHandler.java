package com.me.mdm.chrome.agent.commands.inventory.security;

import java.io.IOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.directory.model.ChromeOsDeviceAction;
import com.me.mdm.chrome.agent.Context;
import java.util.logging.Level;
import com.me.mdm.chrome.agent.commands.inventory.security.handlers.DeviceCommandHandler;
import com.me.mdm.chrome.agent.commands.inventory.security.handlers.LostModeHandler;
import com.me.mdm.chrome.agent.core.Response;
import com.me.mdm.chrome.agent.core.Request;
import java.util.logging.Logger;
import com.me.mdm.chrome.agent.core.ProcessRequestHandler;

public class SecurityCommandRequestHandler extends ProcessRequestHandler
{
    public Logger logger;
    private static final String DEPROVISION = "deprovision";
    
    public SecurityCommandRequestHandler() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    @Override
    public void processRequest(final Request request, final Response response) {
        final String requestCommand = request.requestType;
        final Context context = request.getContainer().getContext();
        try {
            if (requestCommand.equalsIgnoreCase("EnableLostMode")) {
                new LostModeHandler().activateLostModeInDevice(request, response, context);
            }
            else if (requestCommand.equalsIgnoreCase("DisableLostMode")) {
                new LostModeHandler().deactivateLostModeInDevice(response, context);
            }
            else if (requestCommand.startsWith("RestartDevice")) {
                new DeviceCommandHandler().issueDeviceCommand(request, response, context);
            }
            else if (requestCommand.equalsIgnoreCase("Deprovision") || requestCommand.equalsIgnoreCase("CorporateWipe") || requestCommand.equalsIgnoreCase("RemoveDevice")) {
                this.deProvisionDevice(request, response, context);
            }
            else {
                this.logger.log(Level.WARNING, "This Request Type is not implemented{0}", requestCommand);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Unknown error ", e);
        }
    }
    
    public void deProvisionDevice(final Request request, final Response response, final Context context) {
        try {
            context.getDirectoryService().chromeosdevices().action(context.getEnterpriseId(), context.getUdid(), new ChromeOsDeviceAction().setAction("deprovision").setDeprovisionReason("retiring_device")).execute();
        }
        catch (final IOException ex) {
            this.logger.log(Level.WARNING, "Error deprovision the device", ex);
            response.setErrorCode(21003);
            response.setErrorMessage(ex.getMessage());
            if (ex instanceof GoogleJsonResponseException) {
                response.setErrorMessage(((GoogleJsonResponseException)ex).getDetails().getMessage());
            }
        }
    }
}
