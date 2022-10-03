package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class BatchUpdateDevicePolicyResponse extends GenericJson
{
    @Key
    private List<UpdateFailureInfo> failedDevices;
    @Key
    private List<String> successDeviceIds;
    
    public List<UpdateFailureInfo> getFailedDevices() {
        return this.failedDevices;
    }
    
    public BatchUpdateDevicePolicyResponse setFailedDevices(final List<UpdateFailureInfo> failedDevices) {
        this.failedDevices = failedDevices;
        return this;
    }
    
    public List<String> getSuccessDeviceIds() {
        return this.successDeviceIds;
    }
    
    public BatchUpdateDevicePolicyResponse setSuccessDeviceIds(final List<String> successDeviceIds) {
        this.successDeviceIds = successDeviceIds;
        return this;
    }
    
    public BatchUpdateDevicePolicyResponse set(final String s, final Object o) {
        return (BatchUpdateDevicePolicyResponse)super.set(s, o);
    }
    
    public BatchUpdateDevicePolicyResponse clone() {
        return (BatchUpdateDevicePolicyResponse)super.clone();
    }
}
