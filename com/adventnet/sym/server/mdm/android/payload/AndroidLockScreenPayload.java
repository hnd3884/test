package com.adventnet.sym.server.mdm.android.payload;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;

public class AndroidLockScreenPayload extends AndroidPayload
{
    public AndroidLockScreenPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "Lockscreen", payloadIdentifier, payloadDisplayName);
    }
    
    public void setMessages(final JSONArray messageArray) throws Exception {
        this.getPayloadJSON().put("messages", (Object)messageArray);
    }
    
    public void setMessagesWithOrder(final JSONArray messageArray) throws Exception {
        this.getPayloadJSON().put("MessagesOrder", (Object)messageArray);
    }
    
    public void setTextColour(final String textColour) throws Exception {
        this.getPayloadJSON().put("textColour", (Object)textColour);
    }
    
    public void setBackgroundColour(final String bgColour) throws Exception {
        this.getPayloadJSON().put("backGroundColour", (Object)bgColour);
    }
    
    public void setWallpaperPath(String url) throws JSONException {
        final HashMap hm = new HashMap();
        hm.put("path", url);
        hm.put("IS_SERVER", false);
        hm.put("IS_AUTHTOKEN", false);
        url = ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
        this.getPayloadJSON().put("lockScreenPath", (Object)url);
    }
    
    public void setWallpaperType(final int wallpaperType) throws Exception {
        this.getPayloadJSON().put("wallpaperType", wallpaperType);
    }
    
    public void setOrientation(final int orientation) throws Exception {
        this.getPayloadJSON().put("orientation", orientation);
    }
}
