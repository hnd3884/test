package com.adventnet.sym.server.mdm.ios.payload;

public class MacVPNPayload extends VPNPayLoadType
{
    public MacVPNPayload(final int payloadVersion, final String payLoadType, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, payLoadType, payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    @Override
    protected void addVPNSubType(final int connectionType) {
        switch (connectionType) {
            case 6: {
                this.getPayloadDict().put("VPNSubType", (Object)"com.f5.access.macos");
                this.setProviderBundleIdentifier("com.f5.access.macos.PacketTunnel");
                break;
            }
            case 11: {
                this.getPayloadDict().put("VPNSubType", (Object)"com.sonicwall.SonicWALL-Mobile-Connect");
                break;
            }
        }
    }
}
