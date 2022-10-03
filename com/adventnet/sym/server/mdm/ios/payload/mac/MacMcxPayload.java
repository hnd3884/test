package com.adventnet.sym.server.mdm.ios.payload.mac;

import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;

public class MacMcxPayload extends IOSPayload
{
    public MacMcxPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.MCX", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setGuestAccountSetting(final boolean isEnabled) {
        if (isEnabled) {
            this.getPayloadDict().put("EnableGuestAccount", (Object)true);
            this.getPayloadDict().put("DisableGuestAccount", (Object)false);
        }
        else {
            this.getPayloadDict().put("EnableGuestAccount", (Object)false);
            this.getPayloadDict().put("DisableGuestAccount", (Object)true);
        }
    }
}
