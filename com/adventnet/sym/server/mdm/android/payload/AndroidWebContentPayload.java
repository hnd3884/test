package com.adventnet.sym.server.mdm.android.payload;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class AndroidWebContentPayload extends AndroidPayload
{
    public AndroidWebContentPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "WebContentFilter", payloadIdentifier, payloadDisplayName);
    }
    
    public void setWhitelistURLs(final JSONArray whitelistUrls) throws JSONException {
        this.getPayloadJSON().put("WhitelistedBookmarks", (Object)whitelistUrls);
    }
    
    public void setBlacklistURLs(final JSONArray blacklistUrls) throws JSONException {
        this.getPayloadJSON().put("BlacklistedURLs", (Object)blacklistUrls);
    }
    
    public void setMaliciousContentFilter(final Boolean maliciousContentFilter) throws JSONException {
        this.getPayloadJSON().put("MaliciousContentFilterEnabled", (Object)maliciousContentFilter);
    }
    
    public JSONObject getUrlJSON(final String url, final String bookmarkName, final String bookmarkPath) throws JSONException {
        final JSONObject UrlJson = new JSONObject();
        UrlJson.put("URL", (Object)url);
        UrlJson.put("Title", (Object)bookmarkName);
        UrlJson.put("BookmarkPath", (Object)bookmarkPath);
        return UrlJson;
    }
}
