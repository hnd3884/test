package com.adventnet.sym.server.mdm.ios.payload.mac;

import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;

public class MacGlobalPreferencesPayload extends IOSPayload
{
    public MacGlobalPreferencesPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, ".GlobalPreferences", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setMultipleSessionEnabled(final Boolean isEnabled) {
        this.getPayloadDict().put("MultipleSessionEnabled", (Object)isEnabled);
    }
    
    public void setAutoLogout(final Integer minutes) {
        this.getPayloadDict().put("com.apple.autologout.AutoLogOutDelay", (Object)(minutes * 60));
    }
}
