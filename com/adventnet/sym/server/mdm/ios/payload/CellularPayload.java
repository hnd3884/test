package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSArray;
import com.dd.plist.NSObject;
import com.dd.plist.NSDictionary;
import java.util.logging.Logger;

public class CellularPayload extends IOSPayload
{
    public static Logger logger;
    
    public CellularPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.cellular", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void initializeAPNsDict() {
        final NSDictionary apnDict = new NSDictionary();
        this.getPayloadDict().put("APNs", (NSObject)apnDict);
    }
    
    protected NSDictionary getAPNsDict() {
        return (NSDictionary)this.getPayloadDict().objectForKey("APNs");
    }
    
    public void setCellularDict() {
        final NSArray aPNsArray = new NSArray(1);
        aPNsArray.setValue(0, (Object)this.getAPNsDict());
        this.getPayloadDict().put("APNs", (NSObject)aPNsArray);
    }
    
    public void setAccessPointName(final String accessPointName) {
        this.getAPNsDict().put("Name", (Object)accessPointName);
    }
    
    public void setAccessPointUserName(final String accessPointUserName) {
        this.getAPNsDict().put("Username", (Object)accessPointUserName);
    }
    
    public void setAccessPointPassword(final String accessPointPassword) {
        this.getAPNsDict().put("Password", (Object)accessPointPassword);
    }
    
    public void setAccessPointProxyHostName(final String proxyHostName) {
        this.getAPNsDict().put("ProxyServer", (Object)proxyHostName);
    }
    
    public void setAccessPointProxyPort(final Integer proxyPort) {
        this.getAPNsDict().put("ProxyPort", (Object)proxyPort);
    }
    
    static {
        CellularPayload.logger = Logger.getLogger("MDMConfigLogger");
    }
}
