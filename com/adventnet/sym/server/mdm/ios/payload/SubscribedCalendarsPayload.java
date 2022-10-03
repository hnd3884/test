package com.adventnet.sym.server.mdm.ios.payload;

public class SubscribedCalendarsPayload extends IOSPayload
{
    public SubscribedCalendarsPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.subscribedcalendar.account", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setSubCalAccountDescription(final String description) {
        this.getPayloadDict().put("SubCalAccountDescription", (Object)description);
    }
    
    public void setSubCalAccountHostName(final String hostName) {
        this.getPayloadDict().put("SubCalAccountHostName", (Object)hostName);
    }
    
    public void setSubCalAccountPassword(final String passwd) {
        this.getPayloadDict().put("SubCalAccountPassword", (Object)passwd);
    }
    
    public void setSubCalAccountUsername(final String userName) {
        this.getPayloadDict().put("SubCalAccountUsername", (Object)userName);
    }
    
    public void setSubCalAccountUseSSL(final boolean useSSL) {
        this.getPayloadDict().put("SubCalAccountUseSSL", (Object)useSSL);
    }
}
