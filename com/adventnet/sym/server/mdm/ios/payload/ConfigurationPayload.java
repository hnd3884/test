package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSObject;
import com.dd.plist.NSArray;

public class ConfigurationPayload extends IOSPayload
{
    public ConfigurationPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "Configuration", payloadOrganization, payloadIdentifier, payloadDisplayName, true);
    }
    
    public void setPayloadDescription(final String description) {
        this.getPayloadDict().put("PayloadDescription", (Object)description);
    }
    
    public void setPayloadScope(final String scope) {
        this.getPayloadDict().put("PayloadScope", (Object)scope);
    }
    
    public void setPayloadRemovalDisallowed(final Integer securityType) {
        if (securityType != null) {
            if (securityType == 1) {
                this.getPayloadDict().put("PayloadRemovalDisallowed", (Object)false);
            }
            else if (securityType == 2) {
                this.getPayloadDict().put("PayloadRemovalDisallowed", (Object)true);
            }
        }
    }
    
    public void setPayloadContent(final NSArray arrayDict) {
        this.getPayloadDict().put("PayloadContent", (NSObject)arrayDict);
    }
    
    public void setDurationForRemoval(final Float removal) {
        this.getPayloadDict().put("DurationUntilRemoval", (Object)removal);
    }
}
