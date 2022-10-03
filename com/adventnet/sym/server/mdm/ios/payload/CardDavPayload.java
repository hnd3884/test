package com.adventnet.sym.server.mdm.ios.payload;

public class CardDavPayload extends IOSPayload
{
    public CardDavPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.carddav.account", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setCardDAVAccountDescription(final String description) {
        this.getPayloadDict().put("CardDAVAccountDescription", (Object)description);
    }
    
    public void setCardDAVHostName(final String hostName) {
        this.getPayloadDict().put("CardDAVHostName", (Object)hostName);
    }
    
    public void setCardDAVPassword(final String passwd) {
        this.getPayloadDict().put("CardDAVPassword", (Object)passwd);
    }
    
    public void setCardDAVPort(final int port) {
        this.getPayloadDict().put("CardDAVPort", (Object)port);
    }
    
    public void setCardDAVPrincipalURL(final String url) {
        this.getPayloadDict().put("CardDAVPrincipalURL", (Object)url);
    }
    
    public void setCardDAVUseSSL(final boolean useSSL) {
        this.getPayloadDict().put("CardDAVUseSSL", (Object)useSSL);
    }
    
    public void setCardDAVUsername(final String userName) {
        this.getPayloadDict().put("CardDAVUsername", (Object)userName);
    }
}
