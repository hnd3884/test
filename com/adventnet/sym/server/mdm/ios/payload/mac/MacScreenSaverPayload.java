package com.adventnet.sym.server.mdm.ios.payload.mac;

import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;

public class MacScreenSaverPayload extends IOSPayload
{
    public MacScreenSaverPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.screensaver", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setLoginWindowIdleTime(final Integer time) {
        if (!time.equals(0)) {
            this.getPayloadDict().put("loginWindowIdleTime", (Object)(time * 60));
            this.getPayloadDict().put("idleTime", (Object)(time * 60));
        }
    }
    
    public void setAskForPassword(final Boolean password) {
        this.getPayloadDict().put("askForPassword", (Object)password);
    }
    
    public void setAskForPasswordDelay(final Integer passwordDelay) {
        if (!passwordDelay.equals(0)) {
            this.getPayloadDict().put("askForPasswordDelay", (Object)(passwordDelay * 60));
        }
    }
    
    public void setModulePath(final String modulePath) {
        this.getPayloadDict().put("loginWindowModulePath", (Object)modulePath);
    }
}
