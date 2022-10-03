package com.me.mdm.chrome.agent;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import java.util.logging.Logger;

public class GoogleChromeApiErrorHandler
{
    public static Logger logger;
    public static final String DEVICE_NOT_IN_KIOSK = "device configured with an auto-launched kiosk app";
    public static final String DEVICE_HAS_PENDING_COMMANDS = "there are commands still pending";
    public static final String ERROR_CODE = "errorCode";
    public static final String ERROR_MESSAGE = "errorMsg";
    public static final String CDM_API_NOT_ENABLED_USER = "Chrome Management - Partner Access is not enabled for this user";
    public static final String CDM_API_NOT_ENABLED_DEVICE = "does not have CDM API enabled";
    
    public static int getErrorCode(final Exception ex) {
        int errorCode = 12132;
        try {
            final String googleErrorMessage = ((GoogleJsonResponseException)ex).getDetails().getMessage();
            if (googleErrorMessage.contains("device configured with an auto-launched kiosk app")) {
                errorCode = 70000;
            }
        }
        catch (final Exception e) {
            GoogleChromeApiErrorHandler.logger.log(Level.INFO, "Exception in getting GOOGLE CHROME API ERROR code", e);
        }
        return errorCode;
    }
    
    public static JSONObject getErrorResponseJSON(final Exception ex, final Boolean isProfileInstall) {
        final JSONObject errorResponseJSON = new JSONObject();
        String errorMsg = "mdm.android.appmgmt.unknown_error@@@<l>" + MDMUtil.getInstance().getSupportFileUploadUrl(null);
        int errorCode = 70010;
        try {
            if (ex instanceof GoogleJsonResponseException) {
                final String googleErrorMsg = ((GoogleJsonResponseException)ex).getDetails().getMessage();
                if (googleErrorMsg.contains("device configured with an auto-launched kiosk app")) {
                    errorCode = 70000;
                }
                else if (googleErrorMsg.contains("does not have CDM API enabled")) {
                    final String errorArg = "mdm.chrome.agent.cdm_api_not_enabled_device";
                    if (isProfileInstall) {
                        errorMsg = "mdm.profile.associate_error@@@" + errorArg;
                    }
                    else {
                        errorMsg = "mdm.profile.disassociate_error@@@" + errorArg;
                    }
                }
                else if (googleErrorMsg.contains("Chrome Management - Partner Access is not enabled for this user")) {
                    final String errorArg = "mdm.chrome.agent.cdm_api_not_enabled_user";
                    if (isProfileInstall) {
                        errorMsg = "mdm.profile.associate_error@@@" + errorArg;
                    }
                    else {
                        errorMsg = "mdm.profile.disassociate_error@@@" + errorArg;
                    }
                }
                else {
                    errorMsg = googleErrorMsg;
                }
            }
        }
        catch (final Exception e) {
            GoogleChromeApiErrorHandler.logger.log(Level.INFO, "Exception while getting error response", ex);
        }
        errorResponseJSON.put("errorCode", errorCode);
        errorResponseJSON.put("errorMsg", (Object)errorMsg);
        GoogleChromeApiErrorHandler.logger.log(Level.INFO, "getErrorResponseJSON : {0}", errorResponseJSON);
        return errorResponseJSON;
    }
    
    static {
        GoogleChromeApiErrorHandler.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
}
