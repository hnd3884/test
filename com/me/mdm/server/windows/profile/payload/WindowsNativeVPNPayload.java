package com.me.mdm.server.windows.profile.payload;

public class WindowsNativeVPNPayload extends WindowsVPNpayload
{
    String nativeURI;
    
    public WindowsNativeVPNPayload(final String profileIdentifier, final int vpnType) {
        super(profileIdentifier, vpnType);
        this.nativeURI = null;
        this.nativeURI = this.baseURI + "/NativeProfile";
    }
    
    public void addVPNServers(final String serverURL) {
        final String keyName = this.nativeURI + "/Servers";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, serverURL, "chr"));
    }
    
    public void addRoutingPolicyType(final String routingType) {
        final String keyName = this.nativeURI + "/RoutingPolicyType";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, routingType, "chr"));
    }
    
    public void addProtocolType(final String protocolType) {
        final String keyName = this.nativeURI + "/NativeProtocolType";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, protocolType, "chr"));
    }
    
    public void setAuthType(final String authType) {
        final String keyName = this.nativeURI + "/Authentication/UserMethod";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, authType, "chr"));
    }
    
    public void setEAPConfiguration(final String eapXML) {
        final String keyName = this.nativeURI + "/Authentication/EAP/Configuration";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, eapXML, "chr"));
    }
    
    public void setL2TPSharedSecret(final String secret) {
        final String keyName = this.nativeURI + "/L2tpPsk";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, secret, "chr"));
    }
}
