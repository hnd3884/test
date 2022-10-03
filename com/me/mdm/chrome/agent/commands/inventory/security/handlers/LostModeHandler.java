package com.me.mdm.chrome.agent.commands.inventory.security.handlers;

import com.adventnet.i18n.I18N;
import org.json.JSONObject;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import java.util.logging.Level;
import com.me.mdm.chrome.agent.core.Response;
import com.me.mdm.chrome.agent.core.Request;
import java.io.IOException;
import com.me.mdm.chrome.agent.GoogleChromeAPIWrapper;
import com.me.mdm.chrome.agent.Context;
import java.util.logging.Logger;

public class LostModeHandler
{
    public Logger logger;
    private static final String DISABLE = "disable";
    private static final String REENABLE = "reenable";
    private static final String DEVICE_STATUS_ACTIVE = "ACTIVE";
    private static final String DEVICE_STATUS_DISABLED = "DISABLED";
    private static final String LOST_MODE_MESSAGE = "LostModeMessage";
    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    
    public LostModeHandler() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    public boolean validateLostModeCommand(final String deviceStatus, final Context context) throws IOException {
        final String deviceState = GoogleChromeAPIWrapper.getEnterpriseDeviceDetails(context).getState();
        return deviceState.equalsIgnoreCase(deviceStatus);
    }
    
    public Response activateLostModeInDevice(final Request request, final Response response, final Context context) {
        try {
            if (this.validateLostModeCommand("ACTIVE", context)) {
                GoogleChromeAPIWrapper.setDeviceAction("disable", context);
            }
            this.validateAndSetDeviceDisabledMessage(request, context);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error enabling lost mode ", ex);
            response.setErrorCode(12132);
            response.setErrorMessage(ex.getMessage());
            response.setStatus("Error");
            if (ex instanceof GoogleJsonResponseException) {
                response.setErrorMessage(((GoogleJsonResponseException)ex).getDetails().getMessage());
                this.logger.log(Level.SEVERE, "GoogleJsonResponseException : ", ((GoogleJsonResponseException)ex).getDetails().getMessage());
            }
        }
        return response;
    }
    
    public void validateAndSetDeviceDisabledMessage(final Request request, final Context context) throws Exception {
        final JSONObject requestData = (JSONObject)request.requestData;
        final String defaultLostModeMessage = I18N.getMsg("mdm.chrome.agent.lost_mode_message", new Object[0]);
        String lostModeMessage = requestData.optString("LostModeMessage", defaultLostModeMessage);
        if (lostModeMessage == null || lostModeMessage.isEmpty()) {
            lostModeMessage = defaultLostModeMessage;
        }
        final String phoneNumber = requestData.optString("PHONE_NUMBER");
        final StringBuilder messageToBeDisplayed = new StringBuilder();
        messageToBeDisplayed.append(lostModeMessage);
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            final String contactNumber = I18N.getMsg("mdm.chrome.agent.contact_number", new Object[] { phoneNumber });
            messageToBeDisplayed.append(System.lineSeparator());
            messageToBeDisplayed.append(contactNumber);
        }
        GoogleChromeAPIWrapper.setDeviceDisabledMessage(messageToBeDisplayed.toString(), context);
    }
    
    public Response deactivateLostModeInDevice(final Response response, final Context context) {
        try {
            if (this.validateLostModeCommand("DISABLED", context)) {
                GoogleChromeAPIWrapper.setDeviceAction("reenable", context);
            }
            GoogleChromeAPIWrapper.revertDeviceDisabledMessage(context);
        }
        catch (final IOException ex) {
            this.logger.log(Level.SEVERE, "Error disabling lost mode", ex);
            response.setErrorCode(12132);
            response.setErrorMessage(ex.getMessage());
            response.setStatus("Error");
            if (ex instanceof GoogleJsonResponseException) {
                response.setErrorMessage(((GoogleJsonResponseException)ex).getDetails().getMessage());
                this.logger.log(Level.SEVERE, "GoogleJsonResponseException : ", ((GoogleJsonResponseException)ex).getDetails().getMessage());
            }
        }
        return response;
    }
}
