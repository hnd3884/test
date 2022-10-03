package com.me.mdm.server.device.resource;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Security
{
    @SerializedName("device_rooted")
    private Boolean deviceRooted;
    @SerializedName("external_storage_encryption")
    private Integer externalStorageEncryption;
    @SerializedName("hardware_encryption_caps")
    private String hardwareEncryptionCaps;
    @SerializedName("passcode_complaint")
    private Boolean passcodeComplaint;
    @SerializedName("passcode_complaint_profiles")
    private Boolean passcodeComplaintProfiles;
    @SerializedName("passcode_present")
    private Boolean passcodePresent;
    @SerializedName(value = "device_id", alternate = { "resource_id" })
    private String deviceID;
    @SerializedName("storage_encryption")
    private Boolean storageEncryption;
    @SerializedName("efrp_status")
    private Integer efrpStatus;
    @SerializedName("efrp_account_details")
    private List<EFRPAccountDetails> efrpAccountDetails;
    @SerializedName("play_protect")
    private Boolean playProtect;
    @SerializedName("safetynet_cts")
    private Boolean safetyNetCts;
    @SerializedName("safetynet_basic_integrity")
    private Boolean safetyNetBasicInteg;
    @SerializedName("safetynet_advice")
    private String safetyNetAdvice;
    @SerializedName("safetynet_availabiity")
    private Boolean safetyNetAvailability;
    @SerializedName("safetynet_error_code")
    private Integer safetyNetErrorCode;
    @SerializedName("safetynet_error_reason")
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
    
    public List<EFRPAccountDetails> getEfrpAccountDetails() {
        return this.efrpAccountDetails;
    }
    
    public void setEfrpAccountDetails(final List<EFRPAccountDetails> efrpAccountDetails) {
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
