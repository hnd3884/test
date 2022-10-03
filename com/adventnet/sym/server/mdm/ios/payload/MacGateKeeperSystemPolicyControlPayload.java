package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSDictionary;

public class MacGateKeeperSystemPolicyControlPayload extends IOSPayload
{
    NSDictionary macGKSPControlDict;
    
    public MacGateKeeperSystemPolicyControlPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.systempolicy.control", payloadOrganization, payloadIdentifier, payloadDisplayName);
        this.macGKSPControlDict = new NSDictionary();
        this.macGKSPControlDict = this.getPayloadDict();
    }
    
    public void setEnabledAssesment(final boolean enabledAssesment) {
        this.macGKSPControlDict.put("EnableAssessment", (Object)enabledAssesment);
    }
    
    public void setAllowIdentifiedDevelopers(final boolean allowIdentifiedDevelopers) {
        this.macGKSPControlDict.put("AllowIdentifiedDevelopers", (Object)allowIdentifiedDevelopers);
    }
}
