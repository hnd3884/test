package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSArray;
import com.dd.plist.NSObject;
import com.dd.plist.NSDictionary;
import java.util.logging.Logger;

public class AirPrintPayload extends IOSPayload
{
    public static Logger logger;
    
    public AirPrintPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.airprint", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void initializeDict() {
        final NSDictionary dict = new NSDictionary();
        this.getPayloadDict().put("AirPrint", (NSObject)dict);
    }
    
    protected NSDictionary getAirPrintdict() {
        return (NSDictionary)this.getPayloadDict().objectForKey("AirPrint");
    }
    
    public void setHostAddress(final String hostAddress) {
        this.getAirPrintdict().put("IPAddress", (Object)hostAddress);
    }
    
    public void setResourcePath(final String resourcePath) {
        this.getAirPrintdict().put("ResourcePath", (Object)resourcePath);
    }
    
    public void setPort(final int port) {
        this.getAirPrintdict().put("Port", (Object)port);
    }
    
    public void setForceTLS(final Boolean value) {
        this.getAirPrintdict().put("ForceTLS", (Object)value);
    }
    
    public void setAirPrintPayload() {
        final NSArray airPrintArray = new NSArray(1);
        airPrintArray.setValue(0, (Object)this.getAirPrintdict());
        this.getPayloadDict().put("AirPrint", (NSObject)airPrintArray);
    }
    
    static {
        AirPrintPayload.logger = Logger.getLogger("MDMConfigLogger");
    }
}
