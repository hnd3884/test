package com.adventnet.sym.server.mdm.android.payload;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

public class AndroidWebClipsPayload extends AndroidPayload
{
    JSONObject webClipSettings;
    
    public AndroidWebClipsPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "webclips", payloadIdentifier, payloadDisplayName);
        this.webClipSettings = new JSONObject();
    }
    
    public void setLabel(final String label) throws JSONException {
        this.getPayloadJSON().put("Label", (Object)label);
    }
    
    public void setURL(final String url) throws JSONException {
        this.getPayloadJSON().put("URL", (Object)url);
    }
    
    public void setIcon(String fileName) throws JSONException {
        try {
            final HashMap hm = new HashMap();
            hm.put("path", fileName);
            hm.put("IS_SERVER", false);
            hm.put("IS_AUTHTOKEN", false);
            fileName = ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
            this.getPayloadJSON().put("Icon", (Object)fileName);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    public void setIsFullScreen(final boolean value) throws JSONException {
        this.getPayloadJSON().put("FullScreen", value);
    }
    
    public void setRefreshMode(final int value) throws JSONException {
        this.getPayloadJSON().put("refreshOption", value);
    }
    
    public void createHomescreenShortcut(final boolean value) throws JSONException {
        this.getPayloadJSON().put("CreateShortcut", value);
    }
    
    public void allowClearCookie(final boolean value) throws JSONException {
        this.webClipSettings.put("AllowClearCookie", value);
    }
    
    public void setWebClipSettings() throws JSONException {
        this.getPayloadJSON().put("WebViewSettings", (Object)this.webClipSettings);
    }
    
    public void setScreenOrientationOption(final int value) throws JSONException {
        this.getPayloadJSON().put("ScreenOrientationOption", value);
    }
    
    public void setIsSitePermissionAllowed(final boolean isSitePermissionAllowed) throws JSONException {
        this.getPayloadJSON().put("IsPermissionAllowed", isSitePermissionAllowed);
    }
}
