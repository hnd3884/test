package com.adventnet.sym.server.mdm.ios.payload;

public class CalDavPayload extends IOSPayload
{
    public CalDavPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.caldav.account", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setCalDAVAccountDescription(final String description) {
        this.getPayloadDict().put("CalDAVAccountDescription", (Object)description);
    }
    
    public void setCalDAVHostName(final String hostName) {
        this.getPayloadDict().put("CalDAVHostName", (Object)hostName);
    }
    
    public void setCalDAVPassword(final String passwd) {
        this.getPayloadDict().put("CalDAVPassword", (Object)passwd);
    }
    
    public void setCalDAVPort(final int port) {
        this.getPayloadDict().put("CalDAVPort", (Object)port);
    }
    
    public void setCalDAVPrincipalURL(final String url) {
        this.getPayloadDict().put("CalDAVPrincipalURL", (Object)url);
    }
    
    public void setCalDAVUseSSL(final boolean useSSL) {
        this.getPayloadDict().put("CalDAVUseSSL", (Object)useSSL);
    }
    
    public void setCalDAVUsername(final String userName) {
        this.getPayloadDict().put("CalDAVUsername", (Object)userName);
    }
}
