package com.adventnet.sym.server.mdm.ios.payload.mac;

import java.util.logging.Level;
import com.dd.plist.NSObject;
import com.dd.plist.NSData;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;

public class FontPayload extends IOSPayload
{
    public static Logger logger;
    
    public FontPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.font", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setFontData(final String filePath) {
        try {
            final byte[] bytes = ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(filePath);
            final NSData data = new NSData(bytes);
            this.getPayloadDict().put("Font", (NSObject)data);
        }
        catch (final Exception e) {
            FontPayload.logger.log(Level.SEVERE, "Exception while creating image in wallpaper", e);
        }
    }
    
    public void setFontName(final String name) {
        this.getPayloadDict().put("Name", (Object)name);
    }
    
    static {
        FontPayload.logger = Logger.getLogger("MDMConfigLogger");
    }
}
