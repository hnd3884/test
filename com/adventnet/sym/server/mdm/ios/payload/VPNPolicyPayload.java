package com.adventnet.sym.server.mdm.ios.payload;

public class VPNPolicyPayload extends VPNPayLoadType
{
    public VPNPolicyPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.vpn.managed", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
}
