package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Notification extends GenericJson
{
    @Key
    private AppRestrictionsSchemaChangeEvent appRestrictionsSchemaChangeEvent;
    @Key
    private AppUpdateEvent appUpdateEvent;
    @Key
    private DeviceReportUpdateEvent deviceReportUpdateEvent;
    @Key
    private String enterpriseId;
    @Key
    private InstallFailureEvent installFailureEvent;
    @Key
    private NewDeviceEvent newDeviceEvent;
    @Key
    private NewPermissionsEvent newPermissionsEvent;
    @Key
    private String notificationType;
    @Key
    private ProductApprovalEvent productApprovalEvent;
    @Key
    private ProductAvailabilityChangeEvent productAvailabilityChangeEvent;
    @Key
    @JsonString
    private Long timestampMillis;
    
    public AppRestrictionsSchemaChangeEvent getAppRestrictionsSchemaChangeEvent() {
        return this.appRestrictionsSchemaChangeEvent;
    }
    
    public Notification setAppRestrictionsSchemaChangeEvent(final AppRestrictionsSchemaChangeEvent appRestrictionsSchemaChangeEvent) {
        this.appRestrictionsSchemaChangeEvent = appRestrictionsSchemaChangeEvent;
        return this;
    }
    
    public AppUpdateEvent getAppUpdateEvent() {
        return this.appUpdateEvent;
    }
    
    public Notification setAppUpdateEvent(final AppUpdateEvent appUpdateEvent) {
        this.appUpdateEvent = appUpdateEvent;
        return this;
    }
    
    public DeviceReportUpdateEvent getDeviceReportUpdateEvent() {
        return this.deviceReportUpdateEvent;
    }
    
    public Notification setDeviceReportUpdateEvent(final DeviceReportUpdateEvent deviceReportUpdateEvent) {
        this.deviceReportUpdateEvent = deviceReportUpdateEvent;
        return this;
    }
    
    public String getEnterpriseId() {
        return this.enterpriseId;
    }
    
    public Notification setEnterpriseId(final String enterpriseId) {
        this.enterpriseId = enterpriseId;
        return this;
    }
    
    public InstallFailureEvent getInstallFailureEvent() {
        return this.installFailureEvent;
    }
    
    public Notification setInstallFailureEvent(final InstallFailureEvent installFailureEvent) {
        this.installFailureEvent = installFailureEvent;
        return this;
    }
    
    public NewDeviceEvent getNewDeviceEvent() {
        return this.newDeviceEvent;
    }
    
    public Notification setNewDeviceEvent(final NewDeviceEvent newDeviceEvent) {
        this.newDeviceEvent = newDeviceEvent;
        return this;
    }
    
    public NewPermissionsEvent getNewPermissionsEvent() {
        return this.newPermissionsEvent;
    }
    
    public Notification setNewPermissionsEvent(final NewPermissionsEvent newPermissionsEvent) {
        this.newPermissionsEvent = newPermissionsEvent;
        return this;
    }
    
    public String getNotificationType() {
        return this.notificationType;
    }
    
    public Notification setNotificationType(final String notificationType) {
        this.notificationType = notificationType;
        return this;
    }
    
    public ProductApprovalEvent getProductApprovalEvent() {
        return this.productApprovalEvent;
    }
    
    public Notification setProductApprovalEvent(final ProductApprovalEvent productApprovalEvent) {
        this.productApprovalEvent = productApprovalEvent;
        return this;
    }
    
    public ProductAvailabilityChangeEvent getProductAvailabilityChangeEvent() {
        return this.productAvailabilityChangeEvent;
    }
    
    public Notification setProductAvailabilityChangeEvent(final ProductAvailabilityChangeEvent productAvailabilityChangeEvent) {
        this.productAvailabilityChangeEvent = productAvailabilityChangeEvent;
        return this;
    }
    
    public Long getTimestampMillis() {
        return this.timestampMillis;
    }
    
    public Notification setTimestampMillis(final Long timestampMillis) {
        this.timestampMillis = timestampMillis;
        return this;
    }
    
    public Notification set(final String fieldName, final Object value) {
        return (Notification)super.set(fieldName, value);
    }
    
    public Notification clone() {
        return (Notification)super.clone();
    }
}
