package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DeviceEnrollmentPermissions extends GenericJson
{
    @Key
    private Boolean enrollNewDeviceDisallowed;
    @Key
    private Boolean reenrollExistingDeviceDisallowed;
    
    public Boolean getEnrollNewDeviceDisallowed() {
        return this.enrollNewDeviceDisallowed;
    }
    
    public DeviceEnrollmentPermissions setEnrollNewDeviceDisallowed(final Boolean enrollNewDeviceDisallowed) {
        this.enrollNewDeviceDisallowed = enrollNewDeviceDisallowed;
        return this;
    }
    
    public Boolean getReenrollExistingDeviceDisallowed() {
        return this.reenrollExistingDeviceDisallowed;
    }
    
    public DeviceEnrollmentPermissions setReenrollExistingDeviceDisallowed(final Boolean reenrollExistingDeviceDisallowed) {
        this.reenrollExistingDeviceDisallowed = reenrollExistingDeviceDisallowed;
        return this;
    }
    
    public DeviceEnrollmentPermissions set(final String s, final Object o) {
        return (DeviceEnrollmentPermissions)super.set(s, o);
    }
    
    public DeviceEnrollmentPermissions clone() {
        return (DeviceEnrollmentPermissions)super.clone();
    }
}
