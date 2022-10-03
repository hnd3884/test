package com.me.mdm.server.apps.android.afw;

import com.adventnet.persistence.DataAccessException;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.logging.Logger;

public class AFWAccountErrorHandler
{
    private Logger logger;
    public static final int USER_NOT_INSERTED = 74000;
    public static HashMap errorCodeReasons;
    
    public AFWAccountErrorHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    private static void initializeErrorCodes() {
        (AFWAccountErrorHandler.errorCodeReasons = new HashMap()).put(12100, "mdm.appmgmt.afw.agent_upgrade_for_install");
        AFWAccountErrorHandler.errorCodeReasons.put(84008, "mdm.afw.account.not_profile_device_owner");
        AFWAccountErrorHandler.errorCodeReasons.put(-1, "mdm.afw.account.update_playstore_service");
        AFWAccountErrorHandler.errorCodeReasons.put(-2, "mdm.afw.account.unknown_error");
        AFWAccountErrorHandler.errorCodeReasons.put(74001, "mdm.afw.account.not_profile_device_owner");
        AFWAccountErrorHandler.errorCodeReasons.put(74002, "mdm.afw.account.play_store_not_available");
        AFWAccountErrorHandler.errorCodeReasons.put(74003, "mdm.afw.account.play_store_signature_mismatch");
        AFWAccountErrorHandler.errorCodeReasons.put(74004, "mdm.afw.account.play_service_not_available");
        AFWAccountErrorHandler.errorCodeReasons.put(74005, "mdm.afw.account.user_interaction_required");
        AFWAccountErrorHandler.errorCodeReasons.put(74006, "mdm.afw.account.unknown_error");
        AFWAccountErrorHandler.errorCodeReasons.put(74007, "mdm.afw.account.unknown_error");
        AFWAccountErrorHandler.errorCodeReasons.put(74008, "mdm.afw.account.unknown_error");
        AFWAccountErrorHandler.errorCodeReasons.put(74009, "mdm.afw.account.user_interaction_required");
        AFWAccountErrorHandler.errorCodeReasons.put(84009, "mdm.afw.account.not_profile_device_owner");
        AFWAccountErrorHandler.errorCodeReasons.put(84010, "mdm.afw.account.account_expired");
        AFWAccountErrorHandler.errorCodeReasons.put(74000, "mdm.afw.account.unknown_error");
        AFWAccountErrorHandler.errorCodeReasons.put(74010, "mdm.afw.account.deviceowner_provision_inprogress");
    }
    
    public String getAppDistributionRemarksForErrorCode(final int errorCode, final Long resourceId) {
        String errorText = null;
        try {
            final HashMap remarks = getErrorCodeReasons();
            errorText = remarks.get(errorCode);
            if (errorText == null) {
                String supportMessage = "Failed to add Managed Account on the device";
                supportMessage = URLEncoder.encode(supportMessage, "UTF-8");
                errorText = "mdm.appmgmt.afw.account_addition_failed@@@<l>/webclient#/uems/mdm/support/supportFile?message=" + supportMessage;
                if (resourceId != -1L) {
                    String deviceList = "[\"" + resourceId.toString() + "\"]";
                    deviceList = URLEncoder.encode(deviceList, "UTF-8");
                    errorText = errorText + "&mobileDeviceList=" + deviceList;
                }
                final Long loggedInUserID = MDMUtil.getInstance().getLoggedInUserID();
                if (loggedInUserID != null && loggedInUserID != -1L) {
                    String loggedInUserEmail = DMUserHandler.getUserEmailID(loggedInUserID);
                    if (loggedInUserEmail != null && !loggedInUserEmail.isEmpty()) {
                        loggedInUserEmail = URLEncoder.encode(loggedInUserEmail, "UTF-8");
                        errorText = errorText + "&loggedinUser=" + loggedInUserEmail;
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getAppDistributionRemarksForErrorCode", ex);
            errorText = "mdm.afw.account.unknown_error";
        }
        return errorText;
    }
    
    public static HashMap getErrorCodeReasons() {
        if (AFWAccountErrorHandler.errorCodeReasons == null) {
            initializeErrorCodes();
        }
        return AFWAccountErrorHandler.errorCodeReasons;
    }
    
    public String getRemarksForErrorCode(final int status, final int errorCode, final Long resourceID) {
        String errorText = null;
        if (status == 3) {
            errorText = this.getAppDistributionRemarksForErrorCode(errorCode, resourceID);
        }
        else if (status == 2) {
            errorText = "mdm.appmgmt.afw.account.success";
        }
        else if (status == 4) {
            errorText = "mdm.appmgmt.afw.account.initiated";
        }
        else if (status == 1) {
            errorText = "mdm.appmgmt.afw.account.in_progress";
        }
        else if (status == 5) {
            errorText = "mdm.afw.account.not_initiated";
        }
        return errorText;
    }
    
    public boolean getIfRetryAllowed(final int errorCode, final int status) {
        return status == 1 || status == 4 || status == 5 || (status == 3 && (errorCode == 74000 || errorCode == -1 || errorCode == -2 || errorCode == 74008 || errorCode == 74005 || errorCode == 74006 || errorCode == 74009 || errorCode == 84010 || errorCode == 74007 || errorCode == 74010));
    }
    
    public boolean isRetryNeeded(final Long resourceId) throws DataAccessException {
        final JSONObject accountStatus = new AFWAccountStatusHandler().getAccountStatusDetailsForResource(resourceId);
        if (accountStatus == null) {
            return true;
        }
        final int errorcode = accountStatus.optInt("ERROR_CODE", -1);
        final int status = accountStatus.optInt("ACCOUNT_STATUS", 5);
        return this.getIfRetryAllowed(errorcode, status);
    }
    
    static {
        AFWAccountErrorHandler.errorCodeReasons = null;
    }
}
