package com.me.mdm.server.device.resource;

import com.google.gson.annotations.SerializedName;

public class Os
{
    @SerializedName("os_version")
    private String osVersion;
    @SerializedName("os_name")
    private String osName;
    @SerializedName("platform_type")
    private Integer platformType;
    @SerializedName("build_version")
    private String buildVersion;
    @SerializedName(value = "device_id", alternate = { "resource_id" })
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
