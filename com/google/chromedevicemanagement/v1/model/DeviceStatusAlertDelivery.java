package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class DeviceStatusAlertDelivery extends GenericJson
{
    @Key
    private List<String> deviceOfflineAlerts;
    
    public List<String> getDeviceOfflineAlerts() {
        return this.deviceOfflineAlerts;
    }
    
    public DeviceStatusAlertDelivery setDeviceOfflineAlerts(final List<String> deviceOfflineAlerts) {
        this.deviceOfflineAlerts = deviceOfflineAlerts;
        return this;
    }
    
    public DeviceStatusAlertDelivery set(final String s, final Object o) {
        return (DeviceStatusAlertDelivery)super.set(s, o);
    }
    
    public DeviceStatusAlertDelivery clone() {
        return (DeviceStatusAlertDelivery)super.clone();
    }
}
