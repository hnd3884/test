package com.me.mdm.server.easmanagement;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.io.File;
import org.json.simple.JSONObject;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Level;

public class EASMgmtErrorHandler
{
    private static EASMgmtErrorHandler easMgmtErrorHandler;
    
    public static EASMgmtErrorHandler getInstance() {
        if (EASMgmtErrorHandler.easMgmtErrorHandler == null) {
            EASMgmtErrorHandler.easMgmtErrorHandler = new EASMgmtErrorHandler();
        }
        return EASMgmtErrorHandler.easMgmtErrorHandler;
    }
    
    private Integer parseErrorMessageToPinPointErrorID(Integer errorID, final String errorRemarks) {
        Integer tempErrorId = null;
        if (errorRemarks.contains("The WinRM client received an HTTP status code of 403 from the remote WS-Management service.")) {
            tempErrorId = EASMgmtConstants.SSL_REQUIRE_CERTIFICATE_ERROR_ID;
        }
        else if (errorRemarks.contains("To use Basic, specify the computer name as the remote destination, specify Basic authentication and provide user name and password.") || errorRemarks.contains("The WinRM client sent a request to an HTTP server and got a response saying the requested HTTP URL was not available. This is usually returned by a HTTP server that does not support the WS-Management protocol.")) {
            tempErrorId = EASMgmtConstants.BASIC_ATUTH_NOT_ENABLED_ERROR_ID;
        }
        else if (errorRemarks.contains("Access is denied") || errorRemarks.contains("Access Denied") || errorRemarks.contains("Failed to resolve tenant name from SMTP address")) {
            tempErrorId = EASMgmtConstants.INVALID_CREDENTIALS_ERROR_ID;
        }
        else if (errorRemarks.contains("The specified module 'ExchangeOnlineManagement' was not loaded because no valid module file was found in any module directory.")) {
            tempErrorId = EASMgmtConstants.EXO_V2_PRE_REQUISITE;
        }
        if (tempErrorId != null && (EASMgmtConstants.PSSESSION_INTIATE_ERROR.equals(errorID) || tempErrorId.equals(EASMgmtConstants.EXO_V2_PRE_REQUISITE))) {
            errorID = tempErrorId;
        }
        if (EASMgmtConstants.PSSESSION_INTIATE_ERROR.equals(errorID)) {
            errorID = EASMgmtConstants.BASIC_ATUTH_NOT_ENABLED_ERROR_ID;
        }
        return errorID;
    }
    
    private String getDeviceGUID(final String errorRemarks) {
        final int index = errorRemarks.indexOf(" cannot be found.");
        int previousSpaceIndex;
        for (previousSpaceIndex = index - 1; previousSpaceIndex >= 0 && errorRemarks.charAt(previousSpaceIndex) != ' '; --previousSpaceIndex) {}
        final String deviceGUID = errorRemarks.substring(previousSpaceIndex + 1, index);
        EASMgmt.logger.log(Level.INFO, "deviceGUID : {0}", deviceGUID);
        return deviceGUID;
    }
    
    private void handleError(Integer errorID, final String errorRemarks, final Long easServerID, final String taskType) {
        if (taskType.equals("START_SESSION_REQUEST")) {
            MDMApiFactoryProvider.getConditionalExchangeAccessApi().closeSession(easServerID);
        }
        if (!taskType.equals("ENROLLMENT_TASK")) {
            if (errorRemarks != null) {
                errorID = this.parseErrorMessageToPinPointErrorID(errorID, errorRemarks);
            }
            EASMgmt.logger.log(Level.SEVERE, " Error id found to be {0} for task {1}", new Object[] { String.valueOf(errorID), taskType });
            final JSONObject easSyncStatusJSON = new JSONObject();
            if (errorID != null) {
                easSyncStatusJSON.put((Object)"ERROR_CODE", (Object)errorID);
            }
            easSyncStatusJSON.put((Object)"EAS_Sync_Status_ID", (Object)easServerID);
            easSyncStatusJSON.put((Object)"SYNC_STATUS", (Object)0);
            EASMgmtDataHandler.getInstance().addOrUpdateEASSyncStatus(easSyncStatusJSON);
        }
        if (taskType.equals("FULL_CONDITIONAL_ACCESS_REQUEST")) {
            final JSONObject easPolicyStatus = new JSONObject();
            easPolicyStatus.put((Object)"EAS_SERVER_ID", (Object)easServerID);
            easPolicyStatus.put((Object)"POLICY_STATUS", (Object)EASMgmtConstants.POLICY_ENFORCEMENT_DONE);
            EASMgmtDataHandler.getInstance().addorUpdateEASPolicy(easPolicyStatus, false);
        }
        if (taskType.equals("REMOVE_EAS_DEVICE")) {
            try {
                if (errorRemarks.contains(" cannot be found.")) {
                    final String deviceGUID = this.getDeviceGUID(errorRemarks);
                    EASMgmtDataHandler.getInstance().getMailboxDeviceDetailsAndMarkDevice(deviceGUID, 2);
                }
                else if (errorRemarks.contains("You can only delete") || errorRemarks.contains("devices per month. Please wait until next month to delete any more devices from your account")) {
                    final String deviceGUID = this.getDeviceGUID(errorRemarks);
                    EASMgmtDataHandler.getInstance().getMailboxDeviceDetailsAndMarkDevice(deviceGUID, -1);
                }
            }
            catch (final Exception e) {
                EASMgmt.logger.log(Level.SEVERE, null, e);
            }
        }
    }
    
    public void handleError(final Integer errorID, final Long easServerID, final String taskType) {
        this.handleError(errorID, null, easServerID, taskType);
    }
    
    public void handleError(final File errorFile, final Long easServerID, final String taskType) {
        if (errorFile != null) {
            final String exceptionType = MDMUtil.getNormalizedStringAndDeleteFile(errorFile);
            MDMUtil.getInstance();
            final String errorMessage = MDMUtil.getNormalizedStringAndDeleteFile(new File(EASMgmtConstants.getErrorMessageFileAddress(easServerID)));
            EASMgmt.logger.log(Level.SEVERE, exceptionType);
            EASMgmt.logger.log(Level.SEVERE, errorMessage);
            Integer errorID = EASMgmtDataHandler.getInstance().getErrorCode(exceptionType);
            if (exceptionType.equals("System.ArgumentException") && errorMessage.contains("Running the Get-Command command in a remote session returned no results.")) {
                errorID = EASMgmtConstants.NON_ADMIN_USER_ERROR;
            }
            if (exceptionType.equals("System.Exception") && (errorMessage.contains("The password is expired.") || errorMessage.contains("unknown_user_type: Unknown User Type"))) {
                errorID = EASMgmtConstants.INVALID_CREDENTIALS_ERROR_ID;
            }
            this.handleError(errorID, errorMessage, easServerID, taskType);
        }
    }
    
    static {
        EASMgmtErrorHandler.easMgmtErrorHandler = null;
    }
}
