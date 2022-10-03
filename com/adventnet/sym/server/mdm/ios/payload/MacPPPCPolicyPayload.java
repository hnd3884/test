package com.adventnet.sym.server.mdm.ios.payload;

import com.me.mdm.server.apps.MacAppPermissionHandler;
import com.dd.plist.NSArray;
import com.dd.plist.NSObject;
import java.util.logging.Logger;
import com.dd.plist.NSDictionary;
import java.util.Map;

public class MacPPPCPolicyPayload extends IOSPayload
{
    private static final Map<String, String> PERMISSION_GROUP;
    NSDictionary serviceDict;
    private static Logger logger;
    
    public MacPPPCPolicyPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.TCC.configuration-profile-policy", payloadOrganization, payloadIdentifier, payloadDisplayName, Boolean.TRUE);
        this.serviceDict = new NSDictionary();
    }
    
    public void setPermissionForApp(final String key, final NSDictionary identity) {
        this.getPayloadDict().put(key, (NSObject)identity);
    }
    
    public void addToServiceDict(final String key, final NSArray apps) {
        this.serviceDict.put((String)MacPPPCPolicyPayload.PERMISSION_GROUP.get(key), (NSObject)apps);
    }
    
    public NSDictionary getServiceDict() {
        return this.serviceDict;
    }
    
    static {
        PERMISSION_GROUP = MacAppPermissionHandler.generateApplePermissionMap();
        MacPPPCPolicyPayload.logger = Logger.getLogger("MDMConfigLogger");
    }
}
