package com.me.mdm.server.device.api.model;

import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceInstalledAppModel
{
    @JsonProperty("app_name")
    private String appName;
    @JsonProperty("identifier")
    private String identifier;
    private String platformType;
    @JsonProperty("app_version")
    private String appVersion;
    @JsonAlias({ "app_name_short_version" })
    @JsonProperty("app_version_code")
    private String appVersionCode;
    @JsonProperty("app_id")
    private String appId;
    
    public String getAppName() {
        return this.appName;
    }
    
    public void setAppName(final String appName) {
        this.appName = appName;
    }
    
    public String getIdentifier() {
        return this.identifier;
    }
    
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }
    
    public String getPlatformType() {
        return this.platformType;
    }
    
    @JsonProperty("platform_type")
    public void setPlatformType(final int platformTypeId) {
        this.platformType = MDMEnrollmentUtil.getPlatformString(platformTypeId);
    }
    
    public String getAppVersion() {
        return this.appVersion;
    }
    
    public void setAppVersion(final String appVersion) {
        this.appVersion = appVersion;
    }
    
    public String getAppVersionCode() {
        return this.appVersionCode;
    }
    
    public void setAppVersionCode(final String appVersionCode) {
        this.appVersionCode = appVersionCode;
    }
    
    public String getAppId() {
        return this.appId;
    }
    
    public void setAppId(final String appId) {
        this.appId = appId;
    }
}
