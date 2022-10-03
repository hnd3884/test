package com.me.mdm.server.device.api.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityModel
{
    @JsonProperty("device_rooted")
    private Boolean deviceRooted;
    @JsonProperty("external_storage_encryption")
    private Integer externalStorageEncryption;
    @JsonProperty("hardware_encryption_caps")
    private String hardwareEncryptionCaps;
    @JsonProperty("passcode_complaint")
    private Boolean passcodeComplaint;
    @JsonProperty("passcode_complaint_profiles")
    private Boolean passcodeComplaintProfiles;
    @JsonProperty("passcode_present")
    private Boolean passcodePresent;
    @JsonAlias({ "resource_id" })
    @JsonProperty("device_id")
    private String deviceID;
    @JsonProperty("storage_encryption")
    private Boolean storageEncryption;
    @JsonProperty("efrp_status")
    private Integer efrpStatus;
    @JsonProperty("efrp_account_details")
    private List<EFRPAccountDetailModel> efrpAccountDetails;
    @JsonProperty("play_protect")
    private Boolean playProtect;
    @JsonProperty("safetynet_cts")
    private Boolean safetyNetCts;
    @JsonProperty("safetynet_basic_integrity")
    private Boolean safetyNetBasicInteg;
    @JsonProperty("safetynet_advice")
    private String safetyNetAdvice;
    @JsonProperty("safetynet_availabiity")
    private Boolean safetyNetAvailability;
    @JsonProperty("safetynet_error_code")
    private Integer safetyNetErrorCode;
    @JsonProperty("safetynet_error_reason")
    private String safetyNetErrorReason;
    
    public String getSafetyNetAdvice() {
        return this.safetyNetAdvice;
    }
    
    public void setSafetyNetAdvice(final String safetyNetAdvice) {
        this.safetyNetAdvice = safetyNetAdvice;
    }
    
    public Boolean getSafetyNetAvailability() {
        return this.safetyNetAvailability;
    }
    
    public void setSafetyNetAvailability(final Boolean safetyNetAvailability) {
        this.safetyNetAvailability = safetyNetAvailability;
    }
    
    public Integer getSafetyNetErrorCode() {
        return this.safetyNetErrorCode;
    }
    
    public void setSafetyNetErrorCode(final Integer safetyNetErrorCode) {
        this.safetyNetErrorCode = safetyNetErrorCode;
    }
    
    public String getSafetyNetErrorReason() {
        return this.safetyNetErrorReason;
    }
    
    public void setSafetyNetErrorReason(final String safetyNetErrorReason) {
        this.safetyNetErrorReason = safetyNetErrorReason;
    }
    
    public Boolean isSafetyNetCts() {
        return this.safetyNetCts;
    }
    
    public void setSafetyNetCts(final Boolean safetyNetCts) {
        this.safetyNetCts = safetyNetCts;
    }
    
    public Boolean isSafetyNetBasicInteg() {
        return this.safetyNetBasicInteg;
    }
    
    public void setSafetyNetBasicInteg(final Boolean safetyNetBasicInteg) {
        this.safetyNetBasicInteg = safetyNetBasicInteg;
    }
    
    public Boolean getDeviceRooted() {
        return this.deviceRooted;
    }
    
    public void setDeviceRooted(final Boolean deviceRooted) {
        this.deviceRooted = deviceRooted;
    }
    
    public Integer getExternalStorageEncryption() {
        return this.externalStorageEncryption;
    }
    
    public void setExternalStorageEncryption(final Integer externalStorageEncryption) {
        this.externalStorageEncryption = externalStorageEncryption;
    }
    
    public String getHardwareEncryptionCaps() {
        return this.hardwareEncryptionCaps;
    }
    
    public void setHardwareEncryptionCaps(final String hardwareEncryptionCaps) {
        this.hardwareEncryptionCaps = hardwareEncryptionCaps;
    }
    
    public Boolean getPasscodeComplaint() {
        return this.passcodeComplaint;
    }
    
    public void setPasscodeComplaint(final Boolean passcodeComplaint) {
        this.passcodeComplaint = passcodeComplaint;
    }
    
    public Boolean getPasscodeComplaintProfiles() {
        return this.passcodeComplaintProfiles;
    }
    
    public void setPasscodeComplaintProfiles(final Boolean passcodeComplaintProfiles) {
        this.passcodeComplaintProfiles = passcodeComplaintProfiles;
    }
    
    public Boolean getPasscodePresent() {
        return this.passcodePresent;
    }
    
    public void setPasscodePresent(final Boolean passcodePresent) {
        this.passcodePresent = passcodePresent;
    }
    
    public String getDeviceID() {
        return this.deviceID;
    }
    
    public void setDeviceID(final String deviceID) {
        this.deviceID = deviceID;
    }
    
    public Boolean getStorageEncryption() {
        return this.storageEncryption;
    }
    
    public void setStorageEncryption(final Boolean storageEncryption) {
        this.storageEncryption = storageEncryption;
    }
    
    public List<EFRPAccountDetailModel> getEfrpAccountDetails() {
        return this.efrpAccountDetails;
    }
    
    public void setEfrpAccountDetails(final List<EFRPAccountDetailModel> efrpAccountDetails) {
        this.efrpAccountDetails = efrpAccountDetails;
    }
    
    public Integer getEfrpStatus() {
        return this.efrpStatus;
    }
    
    public void setEfrpStatus(final Integer efrpStatus) {
        this.efrpStatus = efrpStatus;
    }
    
    public Boolean getPlayProtect() {
        return this.playProtect;
    }
    
    public void setPlayProtect(final Boolean playProtect) {
        this.playProtect = playProtect;
    }
}
