package com.me.mdm.server.device.api.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivacySettingModel
{
    @JsonProperty("fetch_location")
    private Integer fetchLocation;
    @JsonProperty("view_privacy_settings")
    private Boolean viewPrivacySettings;
    @JsonProperty("disable_wipe")
    private Integer disableWipe;
    @JsonProperty("device_state_report")
    private Integer deviceStateReport;
    @JsonProperty("fetch_device_name")
    private Integer fetchDeviceName;
    @JsonProperty("fetch_installed_app")
    private Integer fetchInstalledApp;
    @JsonProperty("disable_bug_report")
    private Integer disableBugReport;
    @JsonProperty("recent_users_report")
    private Integer recentUsersReport;
    @JsonProperty("applicable_for")
    private List<Integer> applicableFor;
    @JsonProperty("disable_remote_control")
    private Integer disableRemoteControl;
    @JsonProperty("fetch_phone_number")
    private Integer fetchPhoneNumber;
    
    public PrivacySettingModel() {
        this.applicableFor = new ArrayList<Integer>();
    }
    
    public Integer getFetchLocation() {
        return this.fetchLocation;
    }
    
    public void setFetchLocation(final Integer fetchLocation) {
        this.fetchLocation = fetchLocation;
    }
    
    public Boolean getViewPrivacySettings() {
        return this.viewPrivacySettings;
    }
    
    public void setViewPrivacySettings(final Boolean viewPrivacySettings) {
        this.viewPrivacySettings = viewPrivacySettings;
    }
    
    public Integer getDisableWipe() {
        return this.disableWipe;
    }
    
    public void setDisableWipe(final Integer disableWipe) {
        this.disableWipe = disableWipe;
    }
    
    public Integer getDeviceStateReport() {
        return this.deviceStateReport;
    }
    
    public void setDeviceStateReport(final Integer deviceStateReport) {
        this.deviceStateReport = deviceStateReport;
    }
    
    public Integer getFetchDeviceName() {
        return this.fetchDeviceName;
    }
    
    public void setFetchDeviceName(final Integer fetchDeviceName) {
        this.fetchDeviceName = fetchDeviceName;
    }
    
    public Integer getFetchInstalledApp() {
        return this.fetchInstalledApp;
    }
    
    public void setFetchInstalledApp(final Integer fetchInstalledApp) {
        this.fetchInstalledApp = fetchInstalledApp;
    }
    
    public Integer getDisableBugReport() {
        return this.disableBugReport;
    }
    
    public void setDisableBugReport(final Integer disableBugReport) {
        this.disableBugReport = disableBugReport;
    }
    
    public Integer getRecentUsersReport() {
        return this.recentUsersReport;
    }
    
    public void setRecentUsersReport(final Integer recentUsersReport) {
        this.recentUsersReport = recentUsersReport;
    }
    
    public List<Integer> getApplicableFor() {
        return this.applicableFor;
    }
    
    public void setApplicableFor(final List<Integer> applicableFor) {
        this.applicableFor = applicableFor;
    }
    
    public Integer getDisableRemoteControl() {
        return this.disableRemoteControl;
    }
    
    public void setDisableRemoteControl(final Integer disableRemoteControl) {
        this.disableRemoteControl = disableRemoteControl;
    }
    
    public Integer getFetchPhoneNumber() {
        return this.fetchPhoneNumber;
    }
    
    public void setFetchPhoneNumber(final Integer fetchPhoneNumber) {
        this.fetchPhoneNumber = fetchPhoneNumber;
    }
}
