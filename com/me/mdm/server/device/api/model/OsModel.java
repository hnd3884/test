package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OsModel
{
    @JsonProperty("os_version")
    private String osVersion;
    @JsonProperty(value = "os_name", defaultValue = "--")
    private String osName;
    @JsonProperty("platform_type")
    private Integer platformType;
    @JsonProperty("build_version")
    private String buildVersion;
    @JsonAlias({ "resource_id" })
    @JsonProperty("device_id")
    private String deviceID;
    
    public String getOsVersion() {
        return this.osVersion;
    }
    
    public void setOsVersion(final String osVersion) {
        this.osVersion = osVersion;
    }
    
    public String getOsName() {
        return this.osName;
    }
    
    public void setOsName(final String osName) {
        this.osName = osName;
    }
    
    public Integer getPlatformType() {
        return this.platformType;
    }
    
    public void setPlatformType(final Integer platformType) {
        this.platformType = platformType;
    }
    
    public String getBuildVersion() {
        return this.buildVersion;
    }
    
    public void setBuildVersion(final String buildVersion) {
        this.buildVersion = buildVersion;
    }
    
    public String getDeviceID() {
        return this.deviceID;
    }
    
    public void setDeviceID(final String deviceID) {
        this.deviceID = deviceID;
    }
}
