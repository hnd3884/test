package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSArray;
import com.dd.plist.NSObject;
import java.util.List;

public class ManagedDomainPayload extends IOSPayload
{
    public ManagedDomainPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.domains", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setManagedDomainURL(final List urls) {
        final NSArray urlArray = this.getURLStringArray(urls);
        this.getPayloadDict().put("WebDomains", (NSObject)urlArray);
    }
    
    public NSArray getURLStringArray(final List urls) {
        final NSArray urlarray = new NSArray(urls.size());
        for (int i = 0; i < urls.size(); ++i) {
            urlarray.setValue(i, urls.get(i));
        }
        return urlarray;
    }
}
