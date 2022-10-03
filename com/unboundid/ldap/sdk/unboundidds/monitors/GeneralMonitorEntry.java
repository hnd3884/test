package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.Collections;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.ldap.sdk.Entry;
import java.util.List;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GeneralMonitorEntry extends MonitorEntry
{
    static final String GENERAL_MONITOR_OC = "ds-general-monitor-entry";
    private static final String ATTR_CURRENT_CONNECTIONS = "currentConnections";
    private static final String ATTR_CURRENT_TIME = "currentTime";
    private static final String ATTR_DEGRADED_ALERT_TYPE = "degraded-alert-type";
    private static final String ATTR_INSTANCE_NAME = "instanceName";
    private static final String ATTR_MAX_CONNECTIONS = "maxConnections";
    private static final String ATTR_PRODUCT_NAME = "productName";
    private static final String ATTR_START_TIME = "startTime";
    private static final String ATTR_STARTUP_ID = "startupID";
    private static final String ATTR_STARTUP_UUID = "startupUUID";
    private static final String ATTR_THIRD_PARTY_EXTENSION_DN = "thirdPartyExtensionDN";
    private static final String ATTR_TOTAL_CONNECTIONS = "totalConnections";
    private static final String ATTR_UP_TIME = "upTime";
    private static final String ATTR_VENDOR_NAME = "productVendor";
    private static final String ATTR_VERSION = "productVersion";
    private static final String ATTR_UNAVAILABLE_ALERT_TYPE = "unavailable-alert-type";
    private static final long serialVersionUID = 4262569940859462743L;
    private final Date currentTime;
    private final Date startTime;
    private final List<String> degradedAlertTypes;
    private final List<String> thirdPartyExtensionDNs;
    private final List<String> unavailableAlertTypes;
    private final Long currentConnections;
    private final Long maxConnections;
    private final Long totalConnections;
    private final String instanceName;
    private final String productName;
    private final String startupID;
    private final String startupUUID;
    private final String uptime;
    private final String vendorName;
    private final String versionString;
    
    public GeneralMonitorEntry(final Entry entry) {
        super(entry);
        this.currentConnections = this.getLong("currentConnections");
        this.currentTime = this.getDate("currentTime");
        this.maxConnections = this.getLong("maxConnections");
        this.productName = this.getString("productName");
        this.startTime = this.getDate("startTime");
        this.instanceName = this.getString("instanceName");
        this.startupID = this.getString("startupID");
        this.startupUUID = this.getString("startupUUID");
        this.totalConnections = this.getLong("totalConnections");
        this.uptime = this.getString("upTime");
        this.vendorName = this.getString("productVendor");
        this.versionString = this.getString("productVersion");
        this.degradedAlertTypes = this.getStrings("degraded-alert-type");
        this.unavailableAlertTypes = this.getStrings("unavailable-alert-type");
        this.thirdPartyExtensionDNs = this.getStrings("thirdPartyExtensionDN");
    }
    
    public Long getCurrentConnections() {
        return this.currentConnections;
    }
    
    public Long getMaxConnections() {
        return this.maxConnections;
    }
    
    public Long getTotalConnections() {
        return this.totalConnections;
    }
    
    public Date getCurrentTime() {
        return this.currentTime;
    }
    
    public Date getStartTime() {
        return this.startTime;
    }
    
    public String getInstanceName() {
        return this.instanceName;
    }
    
    public String getStartupID() {
        return this.startupID;
    }
    
    public String getStartupUUID() {
        return this.startupUUID;
    }
    
    public Long getUptimeMillis() {
        if (this.currentTime == null || this.startTime == null) {
            return null;
        }
        return this.currentTime.getTime() - this.startTime.getTime();
    }
    
    public String getUptimeString() {
        return this.uptime;
    }
    
    public String getProductName() {
        return this.productName;
    }
    
    public String getVendorName() {
        return this.vendorName;
    }
    
    public String getVersionString() {
        return this.versionString;
    }
    
    public List<String> getDegradedAlertTypes() {
        return this.degradedAlertTypes;
    }
    
    public List<String> getUnavailableAlertTypes() {
        return this.unavailableAlertTypes;
    }
    
    public List<String> getThirdPartyExtensionDNs() {
        return this.thirdPartyExtensionDNs;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_GENERAL_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_GENERAL_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(30));
        if (this.productName != null) {
            MonitorEntry.addMonitorAttribute(attrs, "productName", MonitorMessages.INFO_GENERAL_DISPNAME_PRODUCT_NAME.get(), MonitorMessages.INFO_GENERAL_DESC_PRODUCT_NAME.get(), this.productName);
        }
        if (this.vendorName != null) {
            MonitorEntry.addMonitorAttribute(attrs, "productVendor", MonitorMessages.INFO_GENERAL_DISPNAME_VENDOR_NAME.get(), MonitorMessages.INFO_GENERAL_DESC_VENDOR_NAME.get(), this.vendorName);
        }
        if (this.versionString != null) {
            MonitorEntry.addMonitorAttribute(attrs, "productVersion", MonitorMessages.INFO_GENERAL_DISPNAME_VERSION.get(), MonitorMessages.INFO_GENERAL_DESC_VERSION.get(), this.versionString);
        }
        if (this.instanceName != null) {
            MonitorEntry.addMonitorAttribute(attrs, "instanceName", MonitorMessages.INFO_GENERAL_DISPNAME_INSTANCE_NAME.get(), MonitorMessages.INFO_GENERAL_DESC_INSTANCE_NAME.get(), this.instanceName);
        }
        if (this.startTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "startTime", MonitorMessages.INFO_GENERAL_DISPNAME_START_TIME.get(), MonitorMessages.INFO_GENERAL_DESC_START_TIME.get(), this.startTime);
        }
        if (this.startupID != null) {
            MonitorEntry.addMonitorAttribute(attrs, "startupID", MonitorMessages.INFO_GENERAL_DISPNAME_STARTUP_ID.get(), MonitorMessages.INFO_GENERAL_DESC_STARTUP_ID.get(), this.startupID);
        }
        if (this.startupUUID != null) {
            MonitorEntry.addMonitorAttribute(attrs, "startupUUID", MonitorMessages.INFO_GENERAL_DISPNAME_STARTUP_UUID.get(), MonitorMessages.INFO_GENERAL_DESC_STARTUP_UUID.get(), this.startupUUID);
        }
        if (this.currentTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "currentTime", MonitorMessages.INFO_GENERAL_DISPNAME_CURRENT_TIME.get(), MonitorMessages.INFO_GENERAL_DESC_CURRENT_TIME.get(), this.currentTime);
        }
        if (this.uptime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "upTime", MonitorMessages.INFO_GENERAL_DISPNAME_UPTIME.get(), MonitorMessages.INFO_GENERAL_DESC_UPTIME.get(), this.uptime);
        }
        if (this.startTime != null && this.currentTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "upTimeMillis", MonitorMessages.INFO_GENERAL_DISPNAME_UPTIME_MILLIS.get(), MonitorMessages.INFO_GENERAL_DESC_UPTIME_MILLIS.get(), this.currentTime.getTime() - this.startTime.getTime());
        }
        if (this.currentConnections != null) {
            MonitorEntry.addMonitorAttribute(attrs, "currentConnections", MonitorMessages.INFO_GENERAL_DISPNAME_CURRENT_CONNECTIONS.get(), MonitorMessages.INFO_GENERAL_DESC_CURRENT_CONNECTIONS.get(), this.currentConnections);
        }
        if (this.maxConnections != null) {
            MonitorEntry.addMonitorAttribute(attrs, "maxConnections", MonitorMessages.INFO_GENERAL_DISPNAME_MAX_CONNECTIONS.get(), MonitorMessages.INFO_GENERAL_DESC_MAX_CONNECTIONS.get(), this.maxConnections);
        }
        if (this.totalConnections != null) {
            MonitorEntry.addMonitorAttribute(attrs, "totalConnections", MonitorMessages.INFO_GENERAL_DISPNAME_TOTAL_CONNECTIONS.get(), MonitorMessages.INFO_GENERAL_DESC_TOTAL_CONNECTIONS.get(), this.totalConnections);
        }
        if (!this.degradedAlertTypes.isEmpty()) {
            MonitorEntry.addMonitorAttribute(attrs, "degraded-alert-type", MonitorMessages.INFO_GENERAL_DISPNAME_DEGRADED_ALERT_TYPE.get(), MonitorMessages.INFO_GENERAL_DESC_DEGRADED_ALERT_TYPE.get(), this.degradedAlertTypes);
        }
        if (!this.unavailableAlertTypes.isEmpty()) {
            MonitorEntry.addMonitorAttribute(attrs, "unavailable-alert-type", MonitorMessages.INFO_GENERAL_DISPNAME_UNAVAILABLE_ALERT_TYPE.get(), MonitorMessages.INFO_GENERAL_DESC_UNAVAILABLE_ALERT_TYPE.get(), this.unavailableAlertTypes);
        }
        if (!this.thirdPartyExtensionDNs.isEmpty()) {
            MonitorEntry.addMonitorAttribute(attrs, "thirdPartyExtensionDN", MonitorMessages.INFO_GENERAL_DISPNAME_THIRD_PARTY_EXTENSION_DN.get(), MonitorMessages.INFO_GENERAL_DESC_THIRD_PARTY_EXTENSION_DN.get(), this.thirdPartyExtensionDNs);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
