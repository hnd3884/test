package com.adventnet.sym.server.mdm.ios.payload;

public class GlobalHttpProxyPayload extends IOSPayload
{
    public GlobalHttpProxyPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.proxy.http.global", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setManualProxy(final String sProxyServer, final int sProxyServerPort, final String sProxyUserName, final String sProxyPassword) {
        this.getPayloadDict().put("ProxyType", (Object)"Manual");
        this.getPayloadDict().put("ProxyServer", (Object)sProxyServer);
        this.getPayloadDict().put("ProxyServerPort", (Object)sProxyServerPort);
        if (sProxyUserName != null) {
            this.getPayloadDict().put("ProxyUserName", (Object)sProxyUserName);
        }
        if (sProxyPassword != null) {
            this.getPayloadDict().put("ProxyPassword", (Object)sProxyPassword);
        }
    }
    
    public void setAutomaticProxy(final String sProxyPACURL) {
        this.getPayloadDict().put("ProxyType", (Object)"Auto");
        this.getPayloadDict().put("ProxyPACURL", (Object)sProxyPACURL);
    }
}
