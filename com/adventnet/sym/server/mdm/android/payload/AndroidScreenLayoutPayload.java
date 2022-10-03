package com.adventnet.sym.server.mdm.android.payload;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class AndroidScreenLayoutPayload extends AndroidPayload
{
    private JSONArray pageArray;
    private JSONArray pageLayoutArray;
    private JSONArray folderLayoutArray;
    private JSONObject folderObject;
    
    public AndroidScreenLayoutPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "HomeScreenCustomization", payloadIdentifier, payloadDisplayName);
    }
    
    private void setPageArray(final String key) {
        final JSONArray jsonArray = new JSONArray();
        this.getPayloadJSON().put(key, (Object)jsonArray);
        this.pageArray = jsonArray;
    }
    
    public void setScreenPageArray() {
        this.setPageArray("Pages");
    }
    
    public void setDockPageArray() {
        this.setPageArray("DockItems");
        this.pageLayoutArray = this.pageArray;
    }
    
    public void setPageLayoutArray(final int pageNo, final boolean isFolder) {
        final JSONArray pageLayoutArray = new JSONArray();
        final JSONObject pageObject = new JSONObject();
        pageObject.put("PageNumber", pageNo);
        pageObject.put("PageLayout", (Object)pageLayoutArray);
        if (!isFolder) {
            this.pageArray.put(pageNo - 1, (Object)pageObject);
            this.pageLayoutArray = pageLayoutArray;
        }
    }
    
    public void setFolderLayoutPages() {
        final JSONArray folderPages = new JSONArray();
        this.folderObject.put("FolderLayout", (Object)folderPages);
        this.folderLayoutArray = folderPages;
    }
    
    public void setAppDetails(final int position, final String bundleIdentifier, final boolean isFolder) {
        final JSONArray layoutArray = this.getPageLayoutArray(isFolder);
        final JSONObject appObject = new JSONObject();
        appObject.put("PackageName", (Object)bundleIdentifier);
        appObject.put("ItemType", 1);
        layoutArray.put(position, (Object)appObject);
    }
    
    public void setWebclipDetails(final int position, final String payloadIdentifier, final boolean isFolder) {
        final JSONArray layoutArray = this.getPageLayoutArray(isFolder);
        final JSONObject webClipObject = new JSONObject();
        webClipObject.put("WebShortcut", (Object)payloadIdentifier);
        webClipObject.put("ItemType", 2);
        layoutArray.put(position, (Object)webClipObject);
    }
    
    public void setFolderDetails(final int position, final String folderName) {
        final JSONObject folderObject = new JSONObject();
        folderObject.put("ItemType", 3);
        folderObject.put("FolderName", (Object)folderName);
        this.folderObject = folderObject;
        this.pageLayoutArray.put(position, (Object)folderObject);
    }
    
    private JSONArray getPageLayoutArray(final boolean isFolder) {
        if (isFolder) {
            return this.folderLayoutArray;
        }
        return this.pageLayoutArray;
    }
    
    public void setOrientationType(final int type) {
        this.getPayloadJSON().put("OrientationType", type);
    }
    
    public void setIconSize(final int size) {
        this.getPayloadJSON().put("IconSize", size);
    }
    
    public void setUserToChangePosition(final boolean changePosition) {
        this.getPayloadJSON().put("AllowUserToChangeAppPosition", changePosition);
    }
    
    public void setTextColour(final String colourCode) {
        this.getPayloadJSON().put("TextColour", (Object)colourCode);
    }
    
    public void setTextSize(final int size) {
        this.getPayloadJSON().put("TextSize", size);
    }
    
    public void setPlaceSubsequentAppPage(final int pageNo) {
        final JSONObject subsequentObject = new JSONObject();
        this.getPayloadJSON().put("PlaceSubsequentApps", (Object)subsequentObject);
        subsequentObject.put("PageNumber", pageNo);
    }
    
    public void setPlaceSubsequentAppFolder(final String folderName) {
        final JSONObject subsequentObject = new JSONObject();
        this.getPayloadJSON().put("PlaceSubsequentApps", (Object)subsequentObject);
        subsequentObject.put("FolderName", (Object)folderName);
    }
}
