package com.adventnet.sym.server.mdm.ios.payload;

import java.util.UUID;
import com.dd.plist.NSDictionary;

public abstract class IOSPayload
{
    private NSDictionary dict;
    
    public IOSPayload() {
        this.dict = null;
        this.dict = new NSDictionary();
    }
    
    public NSDictionary getPayloadDict() {
        return this.dict;
    }
    
    public IOSPayload(final int payloadVersion, final String payloadType, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName, final boolean isSetGivenPayloadIdentifier) {
        this();
        final String payloadUUID = this.getPayloadUUID();
        final NSDictionary payload = this.dict;
        payload.put("PayloadVersion", (Object)payloadVersion);
        payload.put("PayloadUUID", (Object)payloadUUID);
        payload.put("PayloadType", (Object)payloadType);
        payload.put("PayloadOrganization", (Object)payloadOrganization);
        payload.put("PayloadIdentifier", (Object)(isSetGivenPayloadIdentifier ? payloadIdentifier : payloadUUID));
        payload.put("PayloadDisplayName", (Object)payloadDisplayName);
    }
    
    public IOSPayload(final int payloadVersion, final String payloadType, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        this();
        final String payloadUUID = this.getPayloadUUID();
        final NSDictionary payload = this.dict;
        payload.put("PayloadVersion", (Object)payloadVersion);
        payload.put("PayloadUUID", (Object)payloadUUID);
        payload.put("PayloadType", (Object)payloadType);
        payload.put("PayloadOrganization", (Object)payloadOrganization);
        payload.put("PayloadIdentifier", (Object)payloadUUID);
        payload.put("PayloadDisplayName", (Object)payloadDisplayName);
    }
    
    protected String getPayloadUUID() {
        return UUID.randomUUID().toString();
    }
    
    @Override
    public String toString() {
        return this.getPayloadDict().toXMLPropertyList();
    }
}
