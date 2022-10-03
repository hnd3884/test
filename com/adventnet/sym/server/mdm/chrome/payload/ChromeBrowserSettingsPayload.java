package com.adventnet.sym.server.mdm.chrome.payload;

import org.json.JSONObject;
import java.util.List;
import java.util.Collection;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONException;

public class ChromeBrowserSettingsPayload extends ChromePayload
{
    public ChromeBrowserSettingsPayload() throws JSONException {
    }
    
    public ChromeBrowserSettingsPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "ChromeBrowserRestriction", payloadIdentifier, payloadDisplayName);
    }
    
    public void setIncognitoMode(final int incognitoMode) throws JSONException {
        this.getPayloadJSON().put("AllowIncognito", incognitoMode);
    }
    
    public void setPopupSettings(final int popUpSettings) throws JSONException {
        this.getPopupSettings().put("isPopupAllowed", popUpSettings);
    }
    
    public void setPopupAllowURL(final String popupAllowURL) throws JSONException {
        final List urlList = MDMUtil.getInstance().getStringList(popupAllowURL, ",");
        this.getPopupSettings().put("AllowedURLs", (Collection)urlList);
    }
    
    public void setPopupBlockURL(final String popupBlockURL) throws JSONException {
        final List urlList = MDMUtil.getInstance().getStringList(popupBlockURL, ",");
        this.getPopupSettings().put("BlockedURLs", (Collection)urlList);
    }
    
    public void setSafeBrowsing(final int safeBrowsing) throws JSONException {
        this.getPayloadJSON().put("SafeBrowsingMode", safeBrowsing);
    }
    
    public void setMaliciousSites(final boolean maliciousSites) throws JSONException {
        this.getPayloadJSON().put("DisableSafeBrowsingandProceedAnyWay", maliciousSites);
    }
    
    public void setHomePageSettings(final int homePageSettings) throws JSONException {
        this.getHomePageSettings().put("TabMode", homePageSettings);
    }
    
    public void setHomePageURL(final String url) throws JSONException {
        this.getHomePageSettings().put("HomePageURL", (Object)url);
    }
    
    public void setStartupURLs(final String startupURLs) throws JSONException {
        final List urlList = MDMUtil.getInstance().getStringList(startupURLs, ",");
        this.getPayloadJSON().put("RestoreOnStartUpUrls", (Collection)urlList);
    }
    
    public void setSavingHistoryAllowed(final Boolean savingHistory) throws JSONException {
        this.getPayloadJSON().put("isSavingHistoryAllowed", (Object)savingHistory);
    }
    
    public void setDeletingHistoryAllowed(final Boolean isDeletingHistoryAllowed) throws JSONException {
        this.getPayloadJSON().put("isDeletingHistoryAllowed", (Object)isDeletingHistoryAllowed);
    }
    
    public void setBookmarksBarEnabled(final int bookmarksBarEnabled) throws JSONException {
        this.getPayloadJSON().put("BookmarksBarEnabled", bookmarksBarEnabled);
    }
    
    public void setIsEditBookmarksAllowed(final Boolean isEditBookmarksAllowed) throws JSONException {
        this.getPayloadJSON().put("IsEditBookmarksAllowed", (Object)isEditBookmarksAllowed);
    }
    
    public void initiatePopupSettings() throws JSONException {
        this.getPayloadJSON().put("PopUpSettings", (Object)new JSONObject());
    }
    
    public JSONObject getPopupSettings() throws JSONException {
        return this.getPayloadJSON().getJSONObject("PopUpSettings");
    }
    
    public void initiateHomePageSettings() throws JSONException {
        this.getPayloadJSON().put("HomePageSettings", (Object)new JSONObject());
    }
    
    public JSONObject getHomePageSettings() throws JSONException {
        return this.getPayloadJSON().getJSONObject("HomePageSettings");
    }
}
