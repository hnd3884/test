package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ApprovalUrlInfo extends GenericJson
{
    @Key
    private String approvalUrl;
    
    public String getApprovalUrl() {
        return this.approvalUrl;
    }
    
    public ApprovalUrlInfo setApprovalUrl(final String approvalUrl) {
        this.approvalUrl = approvalUrl;
        return this;
    }
    
    public ApprovalUrlInfo set(final String fieldName, final Object value) {
        return (ApprovalUrlInfo)super.set(fieldName, value);
    }
    
    public ApprovalUrlInfo clone() {
        return (ApprovalUrlInfo)super.clone();
    }
}
