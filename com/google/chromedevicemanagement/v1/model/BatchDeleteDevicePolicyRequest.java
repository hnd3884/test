package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class BatchDeleteDevicePolicyRequest extends GenericJson
{
    @Key
    private List<String> deviceIds;
    
    public List<String> getDeviceIds() {
        return this.deviceIds;
    }
    
    public BatchDeleteDevicePolicyRequest setDeviceIds(final List<String> deviceIds) {
        this.deviceIds = deviceIds;
        return this;
    }
    
    public BatchDeleteDevicePolicyRequest set(final String s, final Object o) {
        return (BatchDeleteDevicePolicyRequest)super.set(s, o);
    }
    
    public BatchDeleteDevicePolicyRequest clone() {
        return (BatchDeleteDevicePolicyRequest)super.clone();
    }
}
