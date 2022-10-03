package com.adventnet.sym.server.mdm.chrome.payload;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class ChromeBookmarksPayload extends ChromePayload
{
    public ChromeBookmarksPayload() throws JSONException {
    }
    
    public ChromeBookmarksPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "ManagedBookMarks", payloadIdentifier, payloadDisplayName);
    }
    
    public JSONObject getUrlJSON(final String url, final String title) throws JSONException {
        final JSONObject json = new JSONObject();
        json.put("name", (Object)title);
        json.put("url", (Object)url);
        return json;
    }
    
    public JSONObject getFolderNameElem(final String topName) throws JSONException {
        if (topName != null && !topName.isEmpty()) {
            return new JSONObject().put("toplevel_name", (Object)topName);
        }
        return null;
    }
    
    public void setURLs(final JSONArray bookmarkArray) throws JSONException {
        this.getPayloadJSON().put("Bookmarks", (Object)bookmarkArray);
    }
    
    public void setEditable(final boolean editable) throws JSONException {
        this.getPayloadJSON().put("editBookmarksDisabled", !editable);
    }
    
    public void setBookmarkBar(final int bookmarkBar) throws JSONException {
        this.getPayloadJSON().put("BookmarksBarEnabledMode", bookmarkBar);
    }
}
