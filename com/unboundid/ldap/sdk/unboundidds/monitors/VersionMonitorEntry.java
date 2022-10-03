package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.Collections;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class VersionMonitorEntry extends MonitorEntry
{
    protected static final String VERSION_MONITOR_OC = "ds-version-monitor-entry";
    private static final String ATTR_BUILD_ID = "buildID";
    private static final String ATTR_BUILD_NUMBER = "buildNumber";
    private static final String ATTR_COMPACT_VERSION = "compactVersion";
    private static final String ATTR_FIX_IDS = "fixIDs";
    private static final String ATTR_FULL_VERSION = "fullVersion";
    private static final String ATTR_GROOVY_VERSION = "groovyVersion";
    private static final String ATTR_JE_VERSION = "jeVersion";
    private static final String ATTR_JZLIB_VERSION = "jzlibVersion";
    private static final String ATTR_LDAP_SDK_VERSION = "ldapSDKVersion";
    private static final String ATTR_MAJOR_VERSION = "majorVersion";
    private static final String ATTR_MINOR_VERSION = "minorVersion";
    private static final String ATTR_POINT_VERSION = "pointVersion";
    private static final String ATTR_PRODUCT_NAME = "productName";
    private static final String ATTR_REVISION_NUMBER = "revisionNumber";
    private static final String ATTR_SERVER_SDK_VERSION = "serverSDKVersion";
    private static final String ATTR_SHORT_NAME = "shortName";
    private static final String ATTR_SNMP4J_VERSION = "snmp4jVersion";
    private static final String ATTR_SNMP4J_AGENT_VERSION = "snmp4jAgentVersion";
    private static final String ATTR_SNMP4J_AGENTX_VERSION = "snmp4jAgentXVersion";
    private static final String ATTR_VERSION_QUALIFIER = "versionQualifier";
    private static final long serialVersionUID = -8501846678698542926L;
    private final Long buildNumber;
    private final Long majorVersion;
    private final Long minorVersion;
    private final Long pointVersion;
    private final Long revisionNumber;
    private final String buildID;
    private final String compactVersion;
    private final String fixIDs;
    private final String groovyVersion;
    private final String fullVersion;
    private final String jeVersion;
    private final String jzlibVersion;
    private final String ldapSDKVersion;
    private final String productName;
    private final String serverSDKVersion;
    private final String shortName;
    private final String snmp4jVersion;
    private final String snmp4jAgentVersion;
    private final String snmp4jAgentXVersion;
    private final String versionQualifier;
    
    public VersionMonitorEntry(final Entry entry) {
        super(entry);
        this.buildNumber = this.getLong("buildNumber");
        this.majorVersion = this.getLong("majorVersion");
        this.minorVersion = this.getLong("minorVersion");
        this.pointVersion = this.getLong("pointVersion");
        this.revisionNumber = this.getLong("revisionNumber");
        this.buildID = this.getString("buildID");
        this.compactVersion = this.getString("compactVersion");
        this.fixIDs = this.getString("fixIDs");
        this.groovyVersion = this.getString("groovyVersion");
        this.fullVersion = this.getString("fullVersion");
        this.jeVersion = this.getString("jeVersion");
        this.jzlibVersion = this.getString("jzlibVersion");
        this.ldapSDKVersion = this.getString("ldapSDKVersion");
        this.productName = this.getString("productName");
        this.serverSDKVersion = this.getString("serverSDKVersion");
        this.shortName = this.getString("shortName");
        this.snmp4jVersion = this.getString("snmp4jVersion");
        this.snmp4jAgentVersion = this.getString("snmp4jAgentVersion");
        this.snmp4jAgentXVersion = this.getString("snmp4jAgentXVersion");
        this.versionQualifier = this.getString("versionQualifier");
    }
    
    public String getBuildID() {
        return this.buildID;
    }
    
    public Long getBuildNumber() {
        return this.buildNumber;
    }
    
    public String getCompactVersion() {
        return this.compactVersion;
    }
    
    public String getFixIDs() {
        return this.fixIDs;
    }
    
    public String getFullVersion() {
        return this.fullVersion;
    }
    
    public String getGroovyVersion() {
        return this.groovyVersion;
    }
    
    public String getBerkeleyDBJEVersion() {
        return this.jeVersion;
    }
    
    public String getJZLibVersion() {
        return this.jzlibVersion;
    }
    
    public String getLDAPSDKVersion() {
        return this.ldapSDKVersion;
    }
    
    public Long getMajorVersion() {
        return this.majorVersion;
    }
    
    public Long getMinorVersion() {
        return this.minorVersion;
    }
    
    public Long getPointVersion() {
        return this.pointVersion;
    }
    
    public String getProductName() {
        return this.productName;
    }
    
    public Long getRevisionNumber() {
        return this.revisionNumber;
    }
    
    public String getServerSDKVersion() {
        return this.serverSDKVersion;
    }
    
    public String getShortProductName() {
        return this.shortName;
    }
    
    public String getSNMP4JVersion() {
        return this.snmp4jVersion;
    }
    
    public String getSNMP4JAgentVersion() {
        return this.snmp4jAgentVersion;
    }
    
    public String getSNMP4JAgentXVersion() {
        return this.snmp4jAgentXVersion;
    }
    
    public String getVersionQualifier() {
        return this.versionQualifier;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_VERSION_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_VERSION_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(20));
        if (this.productName != null) {
            MonitorEntry.addMonitorAttribute(attrs, "productName", MonitorMessages.INFO_VERSION_DISPNAME_PRODUCT_NAME.get(), MonitorMessages.INFO_VERSION_DESC_PRODUCT_NAME.get(), this.productName);
        }
        if (this.shortName != null) {
            MonitorEntry.addMonitorAttribute(attrs, "shortName", MonitorMessages.INFO_VERSION_DISPNAME_SHORT_NAME.get(), MonitorMessages.INFO_VERSION_DESC_SHORT_NAME.get(), this.shortName);
        }
        if (this.fullVersion != null) {
            MonitorEntry.addMonitorAttribute(attrs, "fullVersion", MonitorMessages.INFO_VERSION_DISPNAME_FULL_VERSION.get(), MonitorMessages.INFO_VERSION_DESC_FULL_VERSION.get(), this.fullVersion);
        }
        if (this.compactVersion != null) {
            MonitorEntry.addMonitorAttribute(attrs, "compactVersion", MonitorMessages.INFO_VERSION_DISPNAME_COMPACT_VERSION.get(), MonitorMessages.INFO_VERSION_DESC_COMPACT_VERSION.get(), this.compactVersion);
        }
        if (this.buildID != null) {
            MonitorEntry.addMonitorAttribute(attrs, "buildID", MonitorMessages.INFO_VERSION_DISPNAME_BUILD_ID.get(), MonitorMessages.INFO_VERSION_DESC_BUILD_ID.get(), this.buildID);
        }
        if (this.majorVersion != null) {
            MonitorEntry.addMonitorAttribute(attrs, "majorVersion", MonitorMessages.INFO_VERSION_DISPNAME_MAJOR_VERSION.get(), MonitorMessages.INFO_VERSION_DESC_MAJOR_VERSION.get(), this.majorVersion);
        }
        if (this.minorVersion != null) {
            MonitorEntry.addMonitorAttribute(attrs, "minorVersion", MonitorMessages.INFO_VERSION_DISPNAME_MINOR_VERSION.get(), MonitorMessages.INFO_VERSION_DESC_MINOR_VERSION.get(), this.minorVersion);
        }
        if (this.pointVersion != null) {
            MonitorEntry.addMonitorAttribute(attrs, "pointVersion", MonitorMessages.INFO_VERSION_DISPNAME_POINT_VERSION.get(), MonitorMessages.INFO_VERSION_DESC_POINT_VERSION.get(), this.pointVersion);
        }
        if (this.buildNumber != null) {
            MonitorEntry.addMonitorAttribute(attrs, "buildNumber", MonitorMessages.INFO_VERSION_DISPNAME_BUILD_NUMBER.get(), MonitorMessages.INFO_VERSION_DESC_BUILD_NUMBER.get(), this.buildNumber);
        }
        if (this.versionQualifier != null) {
            MonitorEntry.addMonitorAttribute(attrs, "versionQualifier", MonitorMessages.INFO_VERSION_DISPNAME_VERSION_QUALIFIER.get(), MonitorMessages.INFO_VERSION_DESC_VERSION_QUALIFIER.get(), this.versionQualifier);
        }
        if (this.revisionNumber != null) {
            MonitorEntry.addMonitorAttribute(attrs, "revisionNumber", MonitorMessages.INFO_VERSION_DISPNAME_REVISION_NUMBER.get(), MonitorMessages.INFO_VERSION_DESC_REVISION_NUMBER.get(), this.revisionNumber);
        }
        if (this.fixIDs != null) {
            MonitorEntry.addMonitorAttribute(attrs, "fixIDs", MonitorMessages.INFO_VERSION_DISPNAME_FIX_IDS.get(), MonitorMessages.INFO_VERSION_DESC_FIX_IDS.get(), this.fixIDs);
        }
        if (this.groovyVersion != null) {
            MonitorEntry.addMonitorAttribute(attrs, "groovyVersion", MonitorMessages.INFO_VERSION_DISPNAME_GROOVY_VERSION.get(), MonitorMessages.INFO_VERSION_DESC_GROOVY_VERSION.get(), this.groovyVersion);
        }
        if (this.jeVersion != null) {
            MonitorEntry.addMonitorAttribute(attrs, "jeVersion", MonitorMessages.INFO_VERSION_DISPNAME_JE_VERSION.get(), MonitorMessages.INFO_VERSION_DESC_JE_VERSION.get(), this.jeVersion);
        }
        if (this.jzlibVersion != null) {
            MonitorEntry.addMonitorAttribute(attrs, "jzlibVersion", MonitorMessages.INFO_VERSION_DISPNAME_JZLIB_VERSION.get(), MonitorMessages.INFO_VERSION_DESC_JZLIB_VERSION.get(), this.jzlibVersion);
        }
        if (this.ldapSDKVersion != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ldapSDKVersion", MonitorMessages.INFO_VERSION_DISPNAME_LDAP_SDK_VERSION.get(), MonitorMessages.INFO_VERSION_DESC_LDAP_SDK_VERSION.get(), this.ldapSDKVersion);
        }
        if (this.serverSDKVersion != null) {
            MonitorEntry.addMonitorAttribute(attrs, "serverSDKVersion", MonitorMessages.INFO_VERSION_DISPNAME_SERVER_SDK_VERSION.get(), MonitorMessages.INFO_VERSION_DESC_SERVER_SDK_VERSION.get(), this.serverSDKVersion);
        }
        if (this.snmp4jVersion != null) {
            MonitorEntry.addMonitorAttribute(attrs, "snmp4jVersion", MonitorMessages.INFO_VERSION_DISPNAME_SNMP4J_VERSION.get(), MonitorMessages.INFO_VERSION_DESC_SNMP4J_VERSION.get(), this.snmp4jVersion);
        }
        if (this.snmp4jAgentVersion != null) {
            MonitorEntry.addMonitorAttribute(attrs, "snmp4jAgentVersion", MonitorMessages.INFO_VERSION_DISPNAME_SNMP4J_AGENT_VERSION.get(), MonitorMessages.INFO_VERSION_DESC_SNMP4J_AGENT_VERSION.get(), this.snmp4jAgentVersion);
        }
        if (this.snmp4jAgentXVersion != null) {
            MonitorEntry.addMonitorAttribute(attrs, "snmp4jAgentXVersion", MonitorMessages.INFO_VERSION_DISPNAME_SNMP4J_AGENTX_VERSION.get(), MonitorMessages.INFO_VERSION_DESC_SNMP4J_AGENTX_VERSION.get(), this.snmp4jAgentXVersion);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
