package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.me.mdm.api.model.BaseAPIModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceFilevaultDetailsModel extends BaseAPIModel
{
    @JsonProperty("is_filevault_managed")
    private boolean filevaultManaged;
    @JsonProperty("is_filevault_managed_personal")
    private boolean filevaultPersonalManaged;
    @JsonProperty("is_filevault_managed_institutional")
    private boolean filevaultInstitutionalManaged;
    @JsonProperty("is_filevault_enabled")
    private boolean filevaultEnabled;
    @JsonProperty("is_filevault_enabled_personal")
    private boolean filevaultPersonalEnabled;
    @JsonProperty("is_filevault_enabled_institutional")
    private boolean filevaultInstitutionalEnabled;
    @JsonProperty("is_filevault_personal_key_available")
    private boolean filevaultPersonalRecoveryKeyAvailable;
    @JsonProperty("personal_recovery_key_updated_time")
    private Long personalRecoveryKeyUpdatedTime;
    @JsonProperty("personal_recovery_key_updated_time_string")
    private String personalRecoveryKeyUpdatedTimeString;
    @JsonProperty("is_filevault_previous_personal_key_available")
    private boolean filevaultPreviousPersonalRecoveryKeyAvailable;
    @JsonProperty("is_filevault_institutional_cert_available")
    private boolean filevaultInstitutionalRecoveryKeyAvailable;
    @JsonProperty("device_id")
    private Long resourceID;
    
    public boolean isFilevaultManaged() {
        return this.filevaultManaged;
    }
    
    public void setFilevaultManaged(final boolean filevaultManaged) {
        this.filevaultManaged = filevaultManaged;
    }
    
    public boolean isFilevaultPersonalManaged() {
        return this.filevaultPersonalManaged;
    }
    
    public void setFilevaultPersonalManaged(final boolean filevaultPersonalManaged) {
        this.filevaultPersonalManaged = filevaultPersonalManaged;
    }
    
    public boolean isFilevaultInstitutionalManaged() {
        return this.filevaultInstitutionalManaged;
    }
    
    public void setFilevaultInstitutionalManaged(final boolean filevaultInstitutionalManaged) {
        this.filevaultInstitutionalManaged = filevaultInstitutionalManaged;
    }
    
    public boolean isFilevaultEnabled() {
        return this.filevaultEnabled;
    }
    
    public void setFilevaultEnabled(final boolean filevaultEnabled) {
        this.filevaultEnabled = filevaultEnabled;
    }
    
    public boolean isFilevaultPersonalEnabled() {
        return this.filevaultPersonalEnabled;
    }
    
    public void setFilevaultPersonalEnabled(final boolean filevaultPersonalEnabled) {
        this.filevaultPersonalEnabled = filevaultPersonalEnabled;
    }
    
    public boolean isFilevaultInstitutionalEnabled() {
        return this.filevaultInstitutionalEnabled;
    }
    
    public void setFilevaultInstitutionalEnabled(final boolean filevaultInstitutionalEnabled) {
        this.filevaultInstitutionalEnabled = filevaultInstitutionalEnabled;
    }
    
    public boolean isFilevaultPersonalRecoveryKeyAvailable() {
        return this.filevaultPersonalRecoveryKeyAvailable;
    }
    
    public void setFilevaultPersonalRecoveryKeyAvailable(final boolean filevaultPersonalRecoveryKeyAvailable) {
        this.filevaultPersonalRecoveryKeyAvailable = filevaultPersonalRecoveryKeyAvailable;
    }
    
    public boolean isFilevaultPreviousPersonalRecoveryKeyAvailable() {
        return this.filevaultPreviousPersonalRecoveryKeyAvailable;
    }
    
    public void setFilevaultPreviousPersonalRecoveryKeyAvailable(final boolean filevaultPreviousPersonalRecoveryKeyAvailable) {
        this.filevaultPreviousPersonalRecoveryKeyAvailable = filevaultPreviousPersonalRecoveryKeyAvailable;
    }
    
    public boolean isFilevaultInstitutionalRecoveryKeyAvailable() {
        return this.filevaultInstitutionalRecoveryKeyAvailable;
    }
    
    public void setFilevaultInstitutionalRecoveryKeyAvailable(final boolean filevaultInstitutionalRecoveryKeyAvailable) {
        this.filevaultInstitutionalRecoveryKeyAvailable = filevaultInstitutionalRecoveryKeyAvailable;
    }
    
    public String getPersonalRecoveryKeyUpdatedTimeString() {
        return this.personalRecoveryKeyUpdatedTimeString;
    }
    
    public void setPersonalRecoveryKeyUpdatedTimeString(final String personalRecoveryKeyUpdatedTimeString) {
        this.personalRecoveryKeyUpdatedTimeString = personalRecoveryKeyUpdatedTimeString;
    }
    
    public Long getPersonalRecoveryKeyUpdatedTime() {
        return this.personalRecoveryKeyUpdatedTime;
    }
    
    public void setPersonalRecoveryKeyUpdatedTime(final Long personalRecoveryKeyUpdatedTime) {
        this.personalRecoveryKeyUpdatedTime = personalRecoveryKeyUpdatedTime;
    }
    
    public Long getResourceID() {
        return this.resourceID;
    }
    
    public void setResourceID(final Long resourceID) {
        this.resourceID = resourceID;
    }
}
