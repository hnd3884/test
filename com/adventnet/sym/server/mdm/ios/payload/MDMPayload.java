package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSObject;
import com.dd.plist.NSArray;

public class MDMPayload extends IOSPayload
{
    public MDMPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.mdm", payloadOrganization, payloadIdentifier, payloadDisplayName, true);
    }
    
    public void setAccessRights(final int rights) {
        this.getPayloadDict().put("AccessRights", (Object)rights);
    }
    
    public void setServerCapabilities(final NSArray capabilities) {
        this.getPayloadDict().put("ServerCapabilities", (NSObject)capabilities);
    }
    
    public void setCheckInURL(final String checkinurl) {
        this.getPayloadDict().put("CheckInURL", (Object)checkinurl);
    }
    
    public void setCheckOutWhenRemoved(final boolean coRemoved) {
        this.getPayloadDict().put("CheckOutWhenRemoved", (Object)coRemoved);
    }
    
    public void setServerURL(final String serverURL) {
        this.getPayloadDict().put("ServerURL", (Object)serverURL);
    }
    
    public void setIdentifyCertificateUUID(final String keyUUID) {
        this.getPayloadDict().put("IdentityCertificateUUID", (Object)keyUUID);
    }
    
    public void setSignMessage(final boolean signMessage) {
        this.getPayloadDict().put("SignMessage", (Object)signMessage);
    }
    
    public void setTopic(final String topicName) {
        this.getPayloadDict().put("Topic", (Object)topicName);
    }
    
    public void setUseDevelopmentAPNS(final boolean apns) {
        this.getPayloadDict().put("UseDevelopmentAPNS", (Object)apns);
    }
}
