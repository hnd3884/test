package com.adventnet.sym.server.mdm.android.payload;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import org.json.JSONException;

public class AndroidWallpaperPayload extends AndroidPayload
{
    public AndroidWallpaperPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "Wallpaper", payloadIdentifier, payloadDisplayName);
    }
    
    public void setWallpaperURL_1920(String url) throws JSONException {
        final HashMap hm = new HashMap();
        hm.put("path", url);
        hm.put("IS_SERVER", false);
        hm.put("IS_AUTHTOKEN", false);
        url = ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
        this.getPayloadJSON().put("WallpaperURL1920", (Object)url);
    }
    
    public void setWallpaperURL_800(String url) throws JSONException {
        final HashMap hm = new HashMap();
        hm.put("path", url);
        hm.put("IS_SERVER", false);
        hm.put("IS_AUTHTOKEN", false);
        url = ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
        this.getPayloadJSON().put("WallpaperURL800", (Object)url);
    }
    
    public void setAllowWallpaperChange(final boolean status) throws JSONException {
        this.getPayloadJSON().put("AllowWallpaperChange", status);
    }
}
