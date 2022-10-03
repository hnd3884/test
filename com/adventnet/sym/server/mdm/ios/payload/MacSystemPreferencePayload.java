package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSObject;
import com.dd.plist.NSArray;

public class MacSystemPreferencePayload extends IOSPayload
{
    public MacSystemPreferencePayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName, final String payloadType) {
        super(payloadVersion, payloadType, payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setPreferencePaneArray(final String keyName, final NSArray value) {
        this.getPayloadDict().put(keyName, (NSObject)value);
    }
}
