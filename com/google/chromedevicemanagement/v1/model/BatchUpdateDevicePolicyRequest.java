package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class BatchUpdateDevicePolicyRequest extends GenericJson
{
    @Key
    private List<String> deviceIds;
    @Key
    private DevicePolicy devicePolicy;
    @Key
    private String updateMask;
    
    public List<String> getDeviceIds() {
        return this.deviceIds;
    }
    
    public BatchUpdateDevicePolicyRequest setDeviceIds(final List<String> deviceIds) {
        this.deviceIds = deviceIds;
        return this;
    }
    
    public DevicePolicy getDevicePolicy() {
        return this.devicePolicy;
    }
    
    public BatchUpdateDevicePolicyRequest setDevicePolicy(final DevicePolicy devicePolicy) {
        this.devicePolicy = devicePolicy;
        return this;
    }
    
    public String getUpdateMask() {
        return this.updateMask;
    }
    
    public BatchUpdateDevicePolicyRequest setUpdateMask(final String updateMask) {
        this.updateMask = updateMask;
        return this;
    }
    
    public BatchUpdateDevicePolicyRequest set(final String s, final Object o) {
        return (BatchUpdateDevicePolicyRequest)super.set(s, o);
    }
    
    public BatchUpdateDevicePolicyRequest clone() {
        return (BatchUpdateDevicePolicyRequest)super.clone();
    }
}
