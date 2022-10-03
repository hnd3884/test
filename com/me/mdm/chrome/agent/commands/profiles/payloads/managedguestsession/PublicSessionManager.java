package com.me.mdm.chrome.agent.commands.profiles.payloads.managedguestsession;

import com.me.mdm.chrome.agent.commands.profiles.payloads.urlfilter.URLFilterManager;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.chrome.agent.commands.profiles.payloads.userrestriction.ChromeBrowserRestrictionManager;
import com.me.mdm.chrome.agent.commands.profiles.payloads.userrestriction.UserRestrictionManager;
import com.me.mdm.chrome.agent.GoogleChromeApiErrorHandler;
import com.me.mdm.chrome.agent.GoogleChromeAPIWrapper;
import java.util.logging.Level;
import com.me.mdm.chrome.agent.commands.profiles.PayloadResponse;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.Context;
import java.util.logging.Logger;

public class PublicSessionManager
{
    public Logger logger;
    public static final String PUBLIC_SESSION_ENABLED = "publicSessionEnabled";
    public static final String SESSION_DISPLAY_NAME = "sessionDisplayName";
    public static final String SESSION_LENGTH_LIMIT = "sessionLengthLimit";
    public static final String MANAGED_GUEST_SESSION_RESTRICTIONS = "restrictions";
    public static final String MANAGED_GUEST_SESSION_BROWSER_RESTRICTIONS = "browserRestrictions";
    public static final String MANAGED_GUEST_SESSION_WEBCONTENT_FILTER = "webContentFilter";
    public static final String MANAGED_GUEST_SESSION_BOOKMARKS = "ManagedBookmarks";
    
    public PublicSessionManager() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    public PayloadResponse setManagedGuestSession(final Context context, final JSONObject payload, final PayloadResponse payloadResponse) {
        try {
            this.logger.log(Level.INFO, "Going to set Managed Guest Session Settings : {0}", payload);
            final boolean managedGuestSession = payload.optBoolean("publicSessionEnabled");
            if (managedGuestSession) {
                GoogleChromeAPIWrapper.initiateUserPolicy();
                final String sessionName = payload.optString("sessionDisplayName");
                GoogleChromeAPIWrapper.setSessionName(sessionName);
                final int sessionLength = payload.optInt("sessionLengthLimit", -1);
                if (sessionLength != -1) {
                    final int sessionLengthInSec = sessionLength * 60;
                    final String sessionLengthStr = String.valueOf(sessionLengthInSec).concat("s");
                    GoogleChromeAPIWrapper.setSessionLength(sessionLengthStr);
                }
                if (payload.has("restrictions")) {
                    final JSONObject restrictionsJSON = payload.optJSONObject("restrictions");
                    this.handleRestrictions(restrictionsJSON);
                }
                if (payload.has("browserRestrictions")) {
                    final JSONObject browserRestrictionsJSON = payload.optJSONObject("browserRestrictions");
                    this.handleBrowserRestrictions(browserRestrictionsJSON);
                }
                if (payload.has("webContentFilter")) {
                    final JSONObject webContentFilterJSON = payload.optJSONObject("webContentFilter");
                    this.handleWebContentFilter(webContentFilterJSON);
                }
                if (payload.has("ManagedBookmarks")) {
                    final JSONObject managedBookmarksJSON = payload.optJSONObject("ManagedBookmarks");
                    this.handleManagedBookmarks(managedBookmarksJSON);
                }
                GoogleChromeAPIWrapper.initiateDevicePolicy();
                GoogleChromeAPIWrapper.setPublicSession();
                GoogleChromeAPIWrapper.updateDevicePolicy(context);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception : ", ex);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(ex, true);
            payloadResponse.setErrorCode(errorJSON.optInt("errorCode", 70010));
            payloadResponse.setErrorMsg(errorJSON.optString("errorMsg", ex.getMessage()));
        }
        return payloadResponse;
    }
    
    public PayloadResponse revertManagedGuestSession(final Context context, final PayloadResponse payloadResponse) {
        try {
            GoogleChromeAPIWrapper.getDevicePolicy(context);
            GoogleChromeAPIWrapper.revertPublicSession();
            GoogleChromeAPIWrapper.updateDevicePolicy(context);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception : ", ex);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(ex, false);
            payloadResponse.setErrorCode(errorJSON.optInt("errorCode", 70010));
            payloadResponse.setErrorMsg(errorJSON.optString("errorMsg", ex.getMessage()));
        }
        return payloadResponse;
    }
    
    private void handleRestrictions(final JSONObject jsonObject) {
        final int showHomeButton = jsonObject.optInt("showHomeButtonMode", 0);
        final int isExternalStorageAllowed = jsonObject.optInt("accessMode", 0);
        new UserRestrictionManager().setHomeButtonPolicy(showHomeButton);
        new UserRestrictionManager().setExternalStorageAccessibleMode(isExternalStorageAllowed);
    }
    
    private void handleBrowserRestrictions(final JSONObject jsonObject) {
        final JSONObject popUpSettings = jsonObject.optJSONObject("PopUpSettings");
        final int safeBrowsingMode = jsonObject.optInt("SafeBrowsingMode", 0);
        final boolean isDisableSafeBrowsing = jsonObject.optBoolean("DisableSafeBrowsingandProceedAnyWay", false);
        final JSONArray restoreOnStartUpUrls = jsonObject.optJSONArray("RestoreOnStartUpUrls");
        final JSONObject homePageSettings = jsonObject.optJSONObject("HomePageSettings");
        final boolean saveHistory = jsonObject.optBoolean("isSavingHistoryAllowed", false);
        final boolean deleteHistory = jsonObject.optBoolean("isDeletingHistoryAllowed", false);
        final int allowIncognito = jsonObject.optInt("AllowIncognito", 0);
        final int isBookmarkBarEnabled = jsonObject.optInt("BookmarksBarEnabled", 0);
        final boolean isEditBookmarksAllowed = jsonObject.optBoolean("IsEditBookmarksAllowed", false);
        new ChromeBrowserRestrictionManager().setChromePopupSettings(popUpSettings);
        new ChromeBrowserRestrictionManager().setSafeBrowsingModeEnabled(safeBrowsingMode);
        new ChromeBrowserRestrictionManager().setHomePageSettings(homePageSettings);
        new ChromeBrowserRestrictionManager().setBookMarksEnabled(isBookmarkBarEnabled, isEditBookmarksAllowed);
        if (restoreOnStartUpUrls.length() > 0) {
            final List<String> restoreURLS = JSONUtil.getInstance().convertJSONArrayTOList(restoreOnStartUpUrls);
            GoogleChromeAPIWrapper.setRestoreOnStartUpUrls(restoreURLS);
        }
        GoogleChromeAPIWrapper.setDisableSafeModeandProceedAnyway(isDisableSafeBrowsing);
        GoogleChromeAPIWrapper.setSaveBrowserHistoryPolicy(saveHistory);
        GoogleChromeAPIWrapper.setDeleteBrowserHistoryPolicy(deleteHistory);
        new ChromeBrowserRestrictionManager().setIncongitoMode(allowIncognito);
    }
    
    private void handleWebContentFilter(final JSONObject jsonObject) {
        List<String> whitelistURL = new ArrayList<String>();
        List<String> blacklistURL = new ArrayList<String>();
        if (jsonObject.has("WhitelistedURLs")) {
            final JSONArray jsonarray = new JSONArray();
            final JSONArray whiteListedUrlsJSON = jsonObject.optJSONArray("WhitelistedURLs");
            for (int i = 0; i < whiteListedUrlsJSON.length(); ++i) {
                jsonarray.put((Object)String.valueOf(whiteListedUrlsJSON.getJSONObject(i).get("URL")));
            }
            whitelistURL = JSONUtil.getInstance().convertJSONArrayTOList(jsonarray);
        }
        else if (jsonObject.has("BlacklistedURLs")) {
            final JSONArray blackListedUrlsJSON = jsonObject.optJSONArray("BlacklistedURLs");
            if (blackListedUrlsJSON.length() > 0) {
                blacklistURL = JSONUtil.getInstance().convertJSONArrayTOList(blackListedUrlsJSON);
            }
        }
        new URLFilterManager().setURLFilterPolicy(whitelistURL, blacklistURL);
    }
    
    private void handleManagedBookmarks(final JSONObject jsonObject) {
        final int isBookmarkBarEnabled = jsonObject.optInt("BookmarksBarEnabledMode", 0);
        final boolean isEditBookmarksAllowed = jsonObject.optBoolean("IsEditBookmarksAllowed", false);
        new ChromeBrowserRestrictionManager().setBookMarksEnabled(isBookmarkBarEnabled, isEditBookmarksAllowed);
        final JSONArray bookmarks = jsonObject.optJSONArray("Bookmarks");
        GoogleChromeAPIWrapper.setManagedBookmarks(bookmarks.toString());
    }
}
