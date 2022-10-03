package com.me.mdm.chrome.agent.commands.profiles;

import org.json.JSONObject;

public class PayloadRequest
{
    public String payloadType;
    public JSONObject payloadData;
    public String payloadIdentifier;
    private String payloadName;
    public int payloadStaus;
    public ONCPayload existingONCPayload;
    
    public PayloadRequest() {
        this.payloadType = null;
        this.payloadData = null;
    }
    
    public void setPayloadData(final JSONObject payloadData) {
        this.payloadData = payloadData;
    }
    
    public JSONObject getPayloadData() {
        return this.payloadData;
    }
    
    public void setPayloadType(final String payloadType) {
        this.payloadType = payloadType;
    }
    
    public String getPayloadType() {
        return this.payloadType;
    }
    
    public void setPayloadName(final String payloadName) {
        this.payloadName = payloadName;
    }
    
    public void setPayloadIdentifier(final String payloadIdentifier) {
        this.payloadIdentifier = payloadIdentifier;
    }
    
    public String getPayloadIdentifier() {
        return this.payloadIdentifier;
    }
    
    public String getPayloadName() {
        return this.payloadName;
    }
    
    public void setPayloadStatus(final int payloadStatus) {
        this.payloadStaus = payloadStatus;
    }
    
    public int getPayloadStatus() {
        return this.payloadStaus;
    }
    
    public void setExistingONCPayload(final ONCPayload oNCPayload) {
        this.existingONCPayload = oNCPayload;
    }
    
    public ONCPayload getExistingONCPayload() {
        return this.existingONCPayload;
    }
}
