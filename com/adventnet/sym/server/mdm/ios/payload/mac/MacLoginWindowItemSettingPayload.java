package com.adventnet.sym.server.mdm.ios.payload.mac;

import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;

public class MacLoginWindowItemSettingPayload extends IOSPayload
{
    public MacLoginWindowItemSettingPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "loginwindow", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setLoginItemSuppression(final boolean suppression) {
        this.getPayloadDict().put("DisableLoginItemsSuppression", (Object)suppression);
    }
}
