package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ProductsApproveRequest extends GenericJson
{
    @Key
    private ApprovalUrlInfo approvalUrlInfo;
    @Key
    private String approvedPermissions;
    
    public ApprovalUrlInfo getApprovalUrlInfo() {
        return this.approvalUrlInfo;
    }
    
    public ProductsApproveRequest setApprovalUrlInfo(final ApprovalUrlInfo approvalUrlInfo) {
        this.approvalUrlInfo = approvalUrlInfo;
        return this;
    }
    
    public String getApprovedPermissions() {
        return this.approvedPermissions;
    }
    
    public ProductsApproveRequest setApprovedPermissions(final String approvedPermissions) {
        this.approvedPermissions = approvedPermissions;
        return this;
    }
    
    public ProductsApproveRequest set(final String fieldName, final Object value) {
        return (ProductsApproveRequest)super.set(fieldName, value);
    }
    
    public ProductsApproveRequest clone() {
        return (ProductsApproveRequest)super.clone();
    }
}
