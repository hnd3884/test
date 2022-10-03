package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Policy extends GenericJson
{
    @Key
    private String autoUpdatePolicy;
    @Key
    private String deviceReportPolicy;
    @Key
    private MaintenanceWindow maintenanceWindow;
    @Key
    private String productAvailabilityPolicy;
    @Key
    private List<ProductPolicy> productPolicy;
    
    public String getAutoUpdatePolicy() {
        return this.autoUpdatePolicy;
    }
    
    public Policy setAutoUpdatePolicy(final String autoUpdatePolicy) {
        this.autoUpdatePolicy = autoUpdatePolicy;
        return this;
    }
    
    public String getDeviceReportPolicy() {
        return this.deviceReportPolicy;
    }
    
    public Policy setDeviceReportPolicy(final String deviceReportPolicy) {
        this.deviceReportPolicy = deviceReportPolicy;
        return this;
    }
    
    public MaintenanceWindow getMaintenanceWindow() {
        return this.maintenanceWindow;
    }
    
    public Policy setMaintenanceWindow(final MaintenanceWindow maintenanceWindow) {
        this.maintenanceWindow = maintenanceWindow;
        return this;
    }
    
    public String getProductAvailabilityPolicy() {
        return this.productAvailabilityPolicy;
    }
    
    public Policy setProductAvailabilityPolicy(final String productAvailabilityPolicy) {
        this.productAvailabilityPolicy = productAvailabilityPolicy;
        return this;
    }
    
    public List<ProductPolicy> getProductPolicy() {
        return this.productPolicy;
    }
    
    public Policy setProductPolicy(final List<ProductPolicy> productPolicy) {
        this.productPolicy = productPolicy;
        return this;
    }
    
    public Policy set(final String fieldName, final Object value) {
        return (Policy)super.set(fieldName, value);
    }
    
    public Policy clone() {
        return (Policy)super.clone();
    }
}
