package com.adventnet.sym.server.mdm.ios.payload.mac;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.NSArray;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;

public class MacLoginWindowItemPayload extends IOSPayload
{
    NSArray loginWindowArray;
    
    public MacLoginWindowItemPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.loginitems.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setLoginWindowArray(final int size) {
        final NSArray windowArray = new NSArray(size);
        this.getPayloadDict().put("AutoLaunchedApplicationDictionary-managed", (NSObject)windowArray);
        this.loginWindowArray = windowArray;
    }
    
    public void setLoginWindowItem(final int position, final String path, final boolean hidden) {
        final NSDictionary windowItemDict = new NSDictionary();
        windowItemDict.put("Path", (Object)path);
        windowItemDict.put("Hide", (Object)hidden);
        this.loginWindowArray.setValue(position, (Object)windowItemDict);
    }
}
