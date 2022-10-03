package com.me.mdm.chrome.agent.commands.profiles.payloads.userrestriction;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import com.me.mdm.chrome.agent.GoogleChromeApiErrorHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.chrome.agent.GoogleChromeAPIWrapper;
import java.util.logging.Level;
import com.me.mdm.chrome.agent.commands.profiles.PayloadResponse;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.Context;
import java.util.logging.Logger;

public class ChromeBrowserRestrictionManager
{
    public Logger logger;
    public static final int POLICY_UNDEFINED = 0;
    public static final int POLICY_ALLOWED = 1;
    public static final int POLICY_RESTRICTED = 2;
    public static final String POPUP_SETTINGS = "PopUpSettings";
    public static final String IS_POPUP_ALLOWED = "isPopupAllowed";
    public static final String SAFE_BROWSING_MODE = "SafeBrowsingMode";
    public static final String DISABLE_SAFE_BROWSING_PROCEED = "DisableSafeBrowsingandProceedAnyWay";
    public static final String RESTORE_ON_STARTUP_URL = "RestoreOnStartUpUrls";
    public static final String HOME_PAGE_SETTINGS = "HomePageSettings";
    public static final String SAVING_HISTORY = "isSavingHistoryAllowed";
    public static final String DELETING_HISTORY = "isDeletingHistoryAllowed";
    public static final String ALLOW_INCOGNITO = "AllowIncognito";
    public static final String BOOKMARK_BAR_ENABLED = "BookmarksBarEnabled";
    public static final String IS_EDITTING_ALLOWED = "IsEditBookmarksAllowed";
    public static final String CHROME_BROWSER_RESTRICTIONS = "ChromeBrowserRestrictions";
    
    public ChromeBrowserRestrictionManager() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    public PayloadResponse setChromeBrowserRestrictions(final Context context, final JSONObject restrictions, final PayloadResponse payloadResponse) {
        try {
            this.logger.log(Level.INFO, "Payload data : {0}", restrictions);
            final JSONObject popUpSettings = restrictions.optJSONObject("PopUpSettings");
            final int safeBrowsingMode = restrictions.optInt("SafeBrowsingMode", 0);
            final boolean isDisableSafeBrowsing = restrictions.optBoolean("DisableSafeBrowsingandProceedAnyWay", false);
            final JSONArray restoreOnStartUpUrls = restrictions.optJSONArray("RestoreOnStartUpUrls");
            final JSONObject homePageSettings = restrictions.optJSONObject("HomePageSettings");
            final boolean saveHistory = restrictions.optBoolean("isSavingHistoryAllowed", false);
            final boolean deleteHistory = restrictions.optBoolean("isDeletingHistoryAllowed", false);
            final int allowIncognito = restrictions.optInt("AllowIncognito", 0);
            final int isBookmarkBarEnabled = restrictions.optInt("BookmarksBarEnabled", 0);
            final boolean isEditBookmarksAllowed = restrictions.optBoolean("IsEditBookmarksAllowed", false);
            GoogleChromeAPIWrapper.initiateUserPolicy();
            this.setChromePopupSettings(popUpSettings);
            this.setSafeBrowsingModeEnabled(safeBrowsingMode);
            this.setHomePageSettings(homePageSettings);
            this.setBookMarksEnabled(isBookmarkBarEnabled, isEditBookmarksAllowed);
            if (restoreOnStartUpUrls.length() > 0) {
                final List<String> restoreURLS = JSONUtil.getInstance().convertJSONArrayTOList(restoreOnStartUpUrls);
                GoogleChromeAPIWrapper.setRestoreOnStartUpUrls(restoreURLS);
            }
            GoogleChromeAPIWrapper.setDisableSafeModeandProceedAnyway(isDisableSafeBrowsing);
            GoogleChromeAPIWrapper.setSaveBrowserHistoryPolicy(saveHistory);
            GoogleChromeAPIWrapper.setDeleteBrowserHistoryPolicy(deleteHistory);
            this.setIncongitoMode(allowIncognito);
            GoogleChromeAPIWrapper.updateUserPolicy(context);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in setChromeBrowserRestrictions", ex);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(ex, true);
            payloadResponse.setErrorCode(errorJSON.optInt("errorCode", 70010));
            payloadResponse.setErrorMsg(errorJSON.optString("errorMsg", ex.getMessage()));
        }
        return payloadResponse;
    }
    
    public void setChromePopupSettings(final JSONObject popUpSettings) {
        final int allowPopup = popUpSettings.optInt("isPopupAllowed", 0);
        String popupsDefaultSettings;
        if (allowPopup == 1) {
            popupsDefaultSettings = "POPUPS_DEFAULT_MODE_ALLOW";
        }
        else if (allowPopup == 2) {
            popupsDefaultSettings = "POPUPS_DEFAULT_MODE_DISALLOW";
        }
        else {
            popupsDefaultSettings = "POPUPS_DEFAULT_MODE_UNSPECIFIED";
        }
        GoogleChromeAPIWrapper.setPopUpsDefaultSettings(popupsDefaultSettings);
        final JSONArray allowedURLsJSON = popUpSettings.optJSONArray("AllowedURLs");
        List<String> allowedUrls = new ArrayList<String>();
        if (allowedURLsJSON.length() > 0) {
            allowedUrls = JSONUtil.getInstance().convertJSONArrayTOList(allowedURLsJSON);
        }
        GoogleChromeAPIWrapper.setPopupsAllowedUrls(allowedUrls);
        final JSONArray blockedURLsJSON = popUpSettings.optJSONArray("BlockedURLs");
        List<String> blockedUrls = new ArrayList<String>();
        if (blockedURLsJSON.length() > 0) {
            blockedUrls = JSONUtil.getInstance().convertJSONArrayTOList(blockedURLsJSON);
        }
        GoogleChromeAPIWrapper.setPopupsBlockedUrls(blockedUrls);
    }
    
    public void setSafeBrowsingModeEnabled(final int mode) {
        String safeBrowsingEnabled;
        if (mode == 1) {
            safeBrowsingEnabled = "SAFE_BROWSING_ALWAYS_ACTIVE";
        }
        else if (mode == 2) {
            safeBrowsingEnabled = "SAFE_BROWSING_NEVER_ACTIVE";
        }
        else {
            safeBrowsingEnabled = "SAFE_BROWSING_MODE_UNSPECIFIED";
        }
        GoogleChromeAPIWrapper.setSafeBrowsingPolicy(safeBrowsingEnabled);
    }
    
    public void setHomePageSettings(final JSONObject homePageSetting) {
        final int mode = homePageSetting.optInt("TabMode", 0);
        String url = null;
        String modeStr;
        if (mode == 1) {
            modeStr = "HOMEPAGE_MODE_NEW_TAB_PAGE";
        }
        else if (mode == 2) {
            modeStr = "HOMEPAGE_MODE_URL";
            url = homePageSetting.optString("HomePageURL");
        }
        else {
            modeStr = "HOMEPAGE_MODE_UNSPECIFIED";
        }
        GoogleChromeAPIWrapper.setHomePageSettings(modeStr, url);
    }
    
    public void setBookMarksEnabled(final int isBookmarksBarEnabled, final boolean isEditingAllowed) {
        String mode;
        if (isBookmarksBarEnabled == 1) {
            mode = "BOOKMARKS_BAR_ENABLED_ALWAYS";
            GoogleChromeAPIWrapper.setEditBookmarkPolicy(!isEditingAllowed);
        }
        else if (isBookmarksBarEnabled == 2) {
            mode = "BOOKMARKS_BAR_ENABLED_NEVER";
        }
        else {
            mode = "BOOKMARKS_BAR_ENABLED_MODE_UNSPECIFIED";
        }
        GoogleChromeAPIWrapper.setBookMarksBarEnabledPolicy(mode);
    }
    
    public void setIncongitoMode(final int isIncognitoAllowed) {
        if (isIncognitoAllowed == 1) {
            new UserRestrictionManager().setIncognitoRestriction(true);
        }
        else if (isIncognitoAllowed == 2) {
            new UserRestrictionManager().setIncognitoRestriction(false);
        }
    }
    
    public PayloadResponse revertBrowserRestrictions(final Context context, final PayloadResponse payloadResponse) {
        try {
            GoogleChromeAPIWrapper.getUserPolicy(context);
            GoogleChromeAPIWrapper.revertIncognitoRestriction();
            GoogleChromeAPIWrapper.revertPopUpsDefaultSettings();
            GoogleChromeAPIWrapper.revertPopupsAllowedUrls();
            GoogleChromeAPIWrapper.revertPopupsBlockedUrls();
            GoogleChromeAPIWrapper.revertSaveBrowserHistoryPolicy();
            GoogleChromeAPIWrapper.revertDeleteBrowserHistoryPolicy();
            GoogleChromeAPIWrapper.revertHomePageSettings();
            GoogleChromeAPIWrapper.revertRestoreOnStartUpUrls();
            GoogleChromeAPIWrapper.revertSafeBrowsingPolicy();
            GoogleChromeAPIWrapper.revertDisableSafeModeAndProceedAnyway();
            GoogleChromeAPIWrapper.revertEditBookmarkPolicy();
            GoogleChromeAPIWrapper.revertBookMarksBarEnabledPolicy();
            GoogleChromeAPIWrapper.revertRestoreOnStartUpUrls();
            GoogleChromeAPIWrapper.updateUserPolicy(context);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in revertBrowserRestrictions", ex);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(ex, false);
            payloadResponse.setErrorCode(errorJSON.optInt("errorCode", 70010));
            payloadResponse.setErrorMsg(errorJSON.optString("errorMsg", ex.getMessage()));
        }
        return payloadResponse;
    }
}
