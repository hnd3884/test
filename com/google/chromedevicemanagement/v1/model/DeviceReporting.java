package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DeviceReporting extends GenericJson
{
    @Key
    private Boolean deviceStateReportingEnabled;
    @Key
    private Boolean recentUsersReportingEnabled;
    
    public Boolean getDeviceStateReportingEnabled() {
        return this.deviceStateReportingEnabled;
    }
    
    public DeviceReporting setDeviceStateReportingEnabled(final Boolean deviceStateReportingEnabled) {
        this.deviceStateReportingEnabled = deviceStateReportingEnabled;
        return this;
    }
    
    public Boolean getRecentUsersReportingEnabled() {
        return this.recentUsersReportingEnabled;
    }
    
    public DeviceReporting setRecentUsersReportingEnabled(final Boolean recentUsersReportingEnabled) {
        this.recentUsersReportingEnabled = recentUsersReportingEnabled;
        return this;
    }
    
    public DeviceReporting set(final String s, final Object o) {
        return (DeviceReporting)super.set(s, o);
    }
    
    public DeviceReporting clone() {
        return (DeviceReporting)super.clone();
    }
}
