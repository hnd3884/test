package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSDictionary;

public class MacGateKeeperSystemPolicyManagedPayload extends IOSPayload
{
    NSDictionary macGKSPManagedDict;
    
    public MacGateKeeperSystemPolicyManagedPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.systempolicy.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
        this.macGKSPManagedDict = new NSDictionary();
        this.macGKSPManagedDict = this.getPayloadDict();
    }
    
    public void setDisabledOveride(final boolean disableOverride) {
        this.macGKSPManagedDict.put("DisableOverride", (Object)disableOverride);
    }
}
