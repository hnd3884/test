package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class GroupLicense extends GenericJson
{
    @Key
    private String acquisitionKind;
    @Key
    private String approval;
    @Key
    private Integer numProvisioned;
    @Key
    private Integer numPurchased;
    @Key
    private String permissions;
    @Key
    private String productId;
    
    public String getAcquisitionKind() {
        return this.acquisitionKind;
    }
    
    public GroupLicense setAcquisitionKind(final String acquisitionKind) {
        this.acquisitionKind = acquisitionKind;
        return this;
    }
    
    public String getApproval() {
        return this.approval;
    }
    
    public GroupLicense setApproval(final String approval) {
        this.approval = approval;
        return this;
    }
    
    public Integer getNumProvisioned() {
        return this.numProvisioned;
    }
    
    public GroupLicense setNumProvisioned(final Integer numProvisioned) {
        this.numProvisioned = numProvisioned;
        return this;
    }
    
    public Integer getNumPurchased() {
        return this.numPurchased;
    }
    
    public GroupLicense setNumPurchased(final Integer numPurchased) {
        this.numPurchased = numPurchased;
        return this;
    }
    
    public String getPermissions() {
        return this.permissions;
    }
    
    public GroupLicense setPermissions(final String permissions) {
        this.permissions = permissions;
        return this;
    }
    
    public String getProductId() {
        return this.productId;
    }
    
    public GroupLicense setProductId(final String productId) {
        this.productId = productId;
        return this;
    }
    
    public GroupLicense set(final String fieldName, final Object value) {
        return (GroupLicense)super.set(fieldName, value);
    }
    
    public GroupLicense clone() {
        return (GroupLicense)super.clone();
    }
}
