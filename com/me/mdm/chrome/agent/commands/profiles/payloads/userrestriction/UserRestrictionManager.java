package com.me.mdm.chrome.agent.commands.profiles.payloads.userrestriction;

import com.me.mdm.chrome.agent.GoogleChromeApiErrorHandler;
import com.me.mdm.chrome.agent.GoogleChromeAPIWrapper;
import java.util.logging.Level;
import com.me.mdm.chrome.agent.commands.profiles.PayloadResponse;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.Context;
import java.util.logging.Logger;

public class UserRestrictionManager
{
    public Logger logger;
    public static final int POLICY_UNDEFINED = 0;
    public static final int POLICY_ALLOWED = 1;
    public static final int POLICY_RESTRICTED = 2;
    public static final String ALLOW_INCOGNITO = "ALLOW_INCOGNITO";
    public static final String DISALLOW_INCOGNITO = "DISALLOW_INCOGNITO";
    public static final String ALLOW_TASK_MANAGER = "taskManagerEndProcessDisabled";
    public static final String SHOW_HOME_BUTTON = "showHomeButtonMode";
    public static final String IS_PRINTING_ALLOWED = "printingDisabled";
    public static final String EXTERNAL_STORAGE_ACCESSIBLE_MODE = "accessMode";
    public static final String DISABLE_SCREENLOCK = "DisableScreenLock";
    
    public UserRestrictionManager() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    public PayloadResponse parseUserRestriction(final Context context, final JSONObject restrictions, final PayloadResponse payloadResponse) {
        try {
            this.logger.log(Level.INFO, "Going to apply User Restrictions");
            final boolean isincognitoAllowed = restrictions.optBoolean("ALLOW_INCOGNITO", true);
            final boolean isEndProcessAllowed = restrictions.optBoolean("taskManagerEndProcessDisabled", true);
            final int showHomeButton = restrictions.optInt("showHomeButtonMode", 0);
            final boolean isPrintingAllowed = restrictions.optBoolean("printingDisabled", true);
            final int isexternalStorageAllowed = restrictions.optInt("accessMode", 0);
            final boolean isScreenLockDisabled = restrictions.optBoolean("DisableScreenLock", false);
            GoogleChromeAPIWrapper.initiateUserPolicy();
            this.setIncognitoRestriction(isincognitoAllowed);
            this.setHomeButtonPolicy(showHomeButton);
            this.setExternalStorageAccessibleMode(isexternalStorageAllowed);
            GoogleChromeAPIWrapper.setTaskEndProcessPolicy(isEndProcessAllowed);
            GoogleChromeAPIWrapper.setPrintingRestriction(isPrintingAllowed);
            GoogleChromeAPIWrapper.setScreenLockRestriction(isScreenLockDisabled);
            GoogleChromeAPIWrapper.updateUserPolicy(context);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in parseUserRestriction", ex);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(ex, true);
            payloadResponse.setErrorCode(errorJSON.optInt("errorCode", 70010));
            payloadResponse.setErrorMsg(errorJSON.optString("errorMsg", ex.getMessage()));
        }
        return payloadResponse;
    }
    
    public PayloadResponse revertRestriction(final Context context, final PayloadResponse payloadResponse) {
        try {
            GoogleChromeAPIWrapper.getUserPolicy(context);
            GoogleChromeAPIWrapper.revertIncognitoRestriction();
            GoogleChromeAPIWrapper.revertHomeButtonPolicy();
            GoogleChromeAPIWrapper.revertTaskEndProcessPolicy();
            GoogleChromeAPIWrapper.revertExternalStorageAccessibilityPolicy();
            GoogleChromeAPIWrapper.revertPrintingRestriction();
            GoogleChromeAPIWrapper.revertScreenLockRestriction();
            GoogleChromeAPIWrapper.updateUserPolicy(context);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in revertUserRestriction", ex);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(ex, false);
            payloadResponse.setErrorCode(errorJSON.optInt("errorCode", 70010));
            payloadResponse.setErrorMsg(errorJSON.optString("errorMsg", ex.getMessage()));
        }
        return payloadResponse;
    }
    
    public void setIncognitoRestriction(final boolean incognitoAllowed) {
        if (incognitoAllowed) {
            GoogleChromeAPIWrapper.setIncognitoRestriction("ALLOW_INCOGNITO");
        }
        else {
            GoogleChromeAPIWrapper.setIncognitoRestriction("DISALLOW_INCOGNITO");
        }
    }
    
    public void setHomeButtonPolicy(final int showHomeButtonMode) {
        String showHomeButtonStr;
        if (showHomeButtonMode == 1) {
            showHomeButtonStr = "SHOW_HOME_BUTTON_ALWAYS";
        }
        else if (showHomeButtonMode == 2) {
            showHomeButtonStr = "SHOW_HOME_BUTTON_NEVER";
        }
        else {
            showHomeButtonStr = "SHOW_HOME_BUTTON_MODE_UNSPECIFIED";
        }
        GoogleChromeAPIWrapper.setHomeButtonPolicy(showHomeButtonStr);
    }
    
    public void setExternalStorageAccessibleMode(final int mode) {
        String modeStr;
        if (mode == 1) {
            modeStr = "DISABLED";
        }
        else if (mode == 2) {
            modeStr = "READ_ONLY";
        }
        else if (mode == 3) {
            modeStr = "READ_WRITE";
        }
        else {
            modeStr = "UNSPECIFIED";
        }
        GoogleChromeAPIWrapper.setExternalStorageAccessibilityPolicy(modeStr);
    }
}
