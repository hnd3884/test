package com.adventnet.sym.server.mdm.ios.payload;

public class MacPerAppVPNPolicyPayload extends MacVPNPayload
{
    public MacPerAppVPNPolicyPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.vpn.managed.applayer", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
}
