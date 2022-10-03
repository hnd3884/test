package com.adventnet.sym.server.mdm.ios.payload;

public class MacVPNPolicyPayload extends MacVPNPayload
{
    public MacVPNPolicyPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.vpn.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
}
