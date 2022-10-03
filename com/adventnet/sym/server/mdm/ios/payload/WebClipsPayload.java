package com.adventnet.sym.server.mdm.ios.payload;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.dd.plist.NSObject;
import com.dd.plist.NSData;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;

public class WebClipsPayload extends IOSPayload
{
    public WebClipsPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.webClip.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setLabel(final String label) {
        this.getPayloadDict().put("Label", (Object)label);
    }
    
    public void setURL(final String url) {
        this.getPayloadDict().put("URL", (Object)url);
    }
    
    public void setIcon(final String fileName) {
        try {
            final byte[] b = ApiFactoryProvider.getFileAccessAPI().readImageContentAsArray(fileName);
            final NSData data = new NSData(b);
            this.getPayloadDict().put("Icon", (NSObject)data);
        }
        catch (final Exception exp) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in webclip", exp);
        }
    }
    
    public void setIsRemovable(final boolean value) {
        this.getPayloadDict().put("IsRemovable", (Object)value);
    }
    
    public void setIsFullScreen(final boolean value) {
        this.getPayloadDict().put("FullScreen", (Object)value);
    }
    
    public void setIsPrecomposed(final boolean value) {
        this.getPayloadDict().put("Precomposed", (Object)value);
    }
    
    public void setIgnoreManifestScope(final boolean value) {
        this.getPayloadDict().put("IgnoreManifestScope", (Object)value);
    }
}
