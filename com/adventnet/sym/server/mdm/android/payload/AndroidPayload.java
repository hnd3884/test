package com.adventnet.sym.server.mdm.android.payload;

import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class AndroidPayload
{
    private JSONObject json;
    
    public AndroidPayload() {
        this.json = null;
        this.json = new JSONObject();
    }
    
    public Object getPayload() {
        return this.json;
    }
    
    public JSONObject getPayloadJSON() {
        return this.json;
    }
    
    @Override
    public String toString() {
        return this.getPayloadJSON().toString();
    }
    
    public void setPayloadUUID(final Long payloadId) throws JSONException {
        this.getPayloadJSON().put("PayloadUUID", (Object)("Payload:" + payloadId));
    }
    
    public void setPayloadID(final String payloadUUID) throws JSONException {
        this.getPayloadJSON().put("ScepPayloadUUID", (Object)payloadUUID);
    }
    
    public void setPayloadIdentifier(final String payloadIdentifier) throws JSONException {
        this.getPayloadJSON().put("PayloadIdentifier", (Object)payloadIdentifier);
    }
    
    public AndroidPayload(final String payloadVersion, final String payloadType, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        this();
        final String payloadUUID = UUID.randomUUID().toString();
        this.json.put("PayloadVersion", (Object)payloadVersion);
        this.json.put("PayloadUUID", (Object)payloadUUID);
        this.json.put("PayloadType", (Object)payloadType);
        this.json.put("PayloadIdentifier", (Object)payloadIdentifier);
        this.json.put("PayloadDisplayName", (Object)payloadDisplayName);
    }
}
