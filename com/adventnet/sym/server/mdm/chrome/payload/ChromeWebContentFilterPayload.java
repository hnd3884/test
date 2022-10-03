package com.adventnet.sym.server.mdm.chrome.payload;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class ChromeWebContentFilterPayload extends ChromePayload
{
    public ChromeWebContentFilterPayload() throws JSONException {
    }
    
    public ChromeWebContentFilterPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "WebContent", payloadIdentifier, payloadDisplayName);
    }
    
    public void setWhitelistURLs(final JSONArray whitelistUrls) throws JSONException {
        this.getPayloadJSON().put("WhitelistedURLs", (Object)whitelistUrls);
    }
    
    public void setBlacklistURLs(final JSONArray blacklistUrls) throws JSONException {
        this.getPayloadJSON().put("BlacklistedURLs", (Object)blacklistUrls);
    }
    
    public JSONObject getUrlJSON(final String url, final String bookmarkName) throws JSONException {
        final JSONObject UrlJson = new JSONObject();
        UrlJson.put("URL", (Object)url);
        UrlJson.put("Title", (Object)bookmarkName);
        return UrlJson;
    }
    
    public void setEnableBookmarks(final Boolean enableBookmarks) throws JSONException {
        this.getPayloadJSON().put("EnableBookmarks", (Object)enableBookmarks);
    }
}
