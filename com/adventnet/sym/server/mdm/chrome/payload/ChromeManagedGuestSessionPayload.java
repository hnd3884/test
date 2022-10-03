package com.adventnet.sym.server.mdm.chrome.payload;

import org.json.JSONException;

public class ChromeManagedGuestSessionPayload extends ChromePayload
{
    ChromeUserRestrictionPayload chromeUserRestrictionPayload;
    ChromeBrowserSettingsPayload chromeBrowserSettingsPayload;
    ChromeWebContentFilterPayload chromeWebContentFilterPayload;
    ChromeBookmarksPayload chromeBookmarksPayload;
    
    public ChromeManagedGuestSessionPayload() {
    }
    
    public ChromeManagedGuestSessionPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "ManagedGuestSession", payloadIdentifier, payloadDisplayName);
    }
    
    public void setManagedGuestSession(final boolean enableManagedGuestSession) throws JSONException {
        this.getPayloadJSON().put("publicSessionEnabled", enableManagedGuestSession);
    }
    
    public void setSessionName(final String sessionName) throws JSONException {
        this.getPayloadJSON().put("sessionDisplayName", (Object)sessionName);
    }
    
    public void setSessionLength(final int sessionLength) throws JSONException {
        this.getPayloadJSON().put("sessionLengthLimit", sessionLength);
    }
    
    public void initializeRestrictionPayload() throws JSONException {
        this.chromeUserRestrictionPayload = new ChromeUserRestrictionPayload();
    }
    
    public ChromeUserRestrictionPayload getChromeUserRestrictionPayload() {
        return this.chromeUserRestrictionPayload;
    }
    
    public void setRestrictionJSON() throws JSONException {
        this.getPayloadJSON().put("restrictions", (Object)this.getChromeUserRestrictionPayload().getPayloadJSON());
    }
    
    public void initializeBrowserRestrictionPayload() throws JSONException {
        this.chromeBrowserSettingsPayload = new ChromeBrowserSettingsPayload();
    }
    
    public ChromeBrowserSettingsPayload getChromeBrowserSettingsPayload() {
        return this.chromeBrowserSettingsPayload;
    }
    
    public void setBrowserRestrictionsJSON() throws JSONException {
        this.getPayloadJSON().put("browserRestrictions", (Object)this.getChromeBrowserSettingsPayload().getPayloadJSON());
    }
    
    public void initializeWebContentPayload() throws JSONException {
        this.chromeWebContentFilterPayload = new ChromeWebContentFilterPayload();
    }
    
    public ChromeWebContentFilterPayload getChromeWebContentFilterPayload() {
        return this.chromeWebContentFilterPayload;
    }
    
    public void setWebContentFilterJSON() throws JSONException {
        this.getPayloadJSON().put("webContentFilter", (Object)this.getChromeWebContentFilterPayload().getPayloadJSON());
    }
    
    public void initializeBookmarksPayload() throws JSONException {
        this.chromeBookmarksPayload = new ChromeBookmarksPayload();
    }
    
    public ChromeBookmarksPayload getChromeBookmarksPayload() {
        return this.chromeBookmarksPayload;
    }
    
    public void setBookmarksJSON() throws JSONException {
        this.getPayloadJSON().put("ManagedBookmarks", (Object)this.getChromeBookmarksPayload().getPayloadJSON());
    }
}
