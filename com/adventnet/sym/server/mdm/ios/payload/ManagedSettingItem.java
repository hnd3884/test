package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSDictionary;
import java.util.logging.Level;
import com.dd.plist.NSObject;
import com.dd.plist.NSData;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;

public class ManagedSettingItem extends IOSPayload
{
    public static Logger logger;
    
    public ManagedSettingItem(final String itemName) {
        this.getPayloadDict().put("Item", (Object)itemName);
    }
    
    public void setImage(final String filePath) {
        try {
            final byte[] bytes = ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(filePath);
            final NSData data = new NSData(bytes);
            this.getPayloadDict().put("Image", (NSObject)data);
        }
        catch (final Exception ex) {
            ManagedSettingItem.logger.log(Level.SEVERE, "Exception while creating image in wallpaper", ex);
        }
    }
    
    public void setImage(final NSData data) {
        this.getPayloadDict().put("Image", (NSObject)data);
    }
    
    public void setWhere(final int type) {
        this.getPayloadDict().put("Where", (Object)type);
    }
    
    public void setIdentifier(final String appIdentifier) {
        this.getPayloadDict().put("Identifier", (Object)appIdentifier);
    }
    
    public void setAttibutes(final String vpnUUID) {
        final NSDictionary appAttibutes = new NSDictionary();
        appAttibutes.put("VPNUUID", (Object)vpnUUID);
        this.getPayloadDict().put("Attributes", (NSObject)appAttibutes);
    }
    
    public void setSettingEnabled(final boolean value) {
        this.getPayloadDict().put("Enabled", (Object)value);
    }
    
    public void setQuotaSize(final int quotaSize) {
        this.getPayloadDict().put("QuotaSize", (Object)quotaSize);
    }
    
    public void setResidentUsers(final int noOFresidentUsers) {
        this.getPayloadDict().put("ResidentUsers", (Object)noOFresidentUsers);
    }
    
    public void setTimeZone(final String value) {
        this.getPayloadDict().put("TimeZone", (Object)value);
    }
    
    public void setUserSessionTimeout(final Integer value) {
        if (value != -1) {
            this.getPayloadDict().put("UserSessionTimeout", (Object)value);
        }
    }
    
    public void setGuestUserSessionTimeout(final Integer value) {
        if (value != -1) {
            this.getPayloadDict().put("TemporarySessionTimeout", (Object)value);
        }
    }
    
    public void setTemporarySession(final boolean value) {
        this.getPayloadDict().put("TemporarySessionOnly", (Object)value);
    }
    
    public void setPasscodeGracePeriod(final Integer value) {
        this.getPayloadDict().put("PasscodeLockGracePeriod", (Object)value);
    }
    
    public void setAppAnalytics(final boolean value) {
        this.getPayloadDict().put("Enabled", (Object)value);
    }
    
    public void setDiagnosticSubmission(final boolean value) {
        this.getPayloadDict().put("Enabled", (Object)value);
    }
    
    static {
        ManagedSettingItem.logger = Logger.getLogger("MDMConfigLogger");
    }
}
