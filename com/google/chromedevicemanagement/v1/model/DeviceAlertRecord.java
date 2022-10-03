package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DeviceAlertRecord extends GenericJson
{
    @Key
    private String alertCause;
    @Key
    private String alertDeliveryMode;
    @Key
    private String alertDeliveryTime;
    
    public String getAlertCause() {
        return this.alertCause;
    }
    
    public DeviceAlertRecord setAlertCause(final String alertCause) {
        this.alertCause = alertCause;
        return this;
    }
    
    public String getAlertDeliveryMode() {
        return this.alertDeliveryMode;
    }
    
    public DeviceAlertRecord setAlertDeliveryMode(final String alertDeliveryMode) {
        this.alertDeliveryMode = alertDeliveryMode;
        return this;
    }
    
    public String getAlertDeliveryTime() {
        return this.alertDeliveryTime;
    }
    
    public DeviceAlertRecord setAlertDeliveryTime(final String alertDeliveryTime) {
        this.alertDeliveryTime = alertDeliveryTime;
        return this;
    }
    
    public DeviceAlertRecord set(final String s, final Object o) {
        return (DeviceAlertRecord)super.set(s, o);
    }
    
    public DeviceAlertRecord clone() {
        return (DeviceAlertRecord)super.clone();
    }
}
