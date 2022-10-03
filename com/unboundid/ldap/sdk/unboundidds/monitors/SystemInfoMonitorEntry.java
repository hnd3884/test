package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Entry;
import java.util.Map;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SystemInfoMonitorEntry extends MonitorEntry
{
    static final String SYSTEM_INFO_MONITOR_OC = "ds-system-info-monitor-entry";
    private static final String ATTR_AVAILABLE_CPUS = "availableCPUs";
    private static final String ATTR_CLASSPATH = "classPath";
    private static final String ATTR_ENVIRONMENT_VARIABLE = "environmentVariable";
    private static final String ATTR_FREE_MEMORY = "freeUsedMemory";
    private static final String ATTR_HOSTNAME = "systemName";
    private static final String ATTR_INSTANCE_ROOT = "instanceRoot";
    private static final String ATTR_JAVA_HOME = "javaHome";
    private static final String ATTR_JAVA_VENDOR = "javaVendor";
    private static final String ATTR_JAVA_VERSION = "javaVersion";
    private static final String ATTR_JVM_ARCHITECTURE = "jvmArchitecture";
    private static final String ATTR_JVM_ARGUMENTS = "jvmArguments";
    private static final String ATTR_JVM_PID = "jvmPID";
    private static final String ATTR_JVM_VENDOR = "jvmVendor";
    private static final String ATTR_JVM_VERSION = "jvmVersion";
    private static final String ATTR_MAX_MEMORY = "maxMemory";
    private static final String ATTR_OPERATING_SYSTEM = "operatingSystem";
    private static final String ATTR_SSL_CONTEXT_PROTOCOL = "sslContextProtocol";
    private static final String ATTR_SYSTEM_PROPERTY = "systemProperty";
    private static final String ATTR_USED_MEMORY = "usedMemory";
    private static final String ATTR_USER_NAME = "userName";
    private static final String ATTR_WORKING_DIRECTORY = "workingDirectory";
    private static final long serialVersionUID = 2709857663883498069L;
    private final Long availableCPUs;
    private final Long freeMemory;
    private final Long jvmPID;
    private final Long maxMemory;
    private final Long usedMemory;
    private final Map<String, String> environmentVariables;
    private final Map<String, String> systemProperties;
    private final String classpath;
    private final String hostname;
    private final String instanceRoot;
    private final String javaHome;
    private final String javaVendor;
    private final String javaVersion;
    private final String jvmArchitecture;
    private final String jvmArguments;
    private final String jvmVendor;
    private final String jvmVersion;
    private final String operatingSystem;
    private final String sslContextProtocol;
    private final String userName;
    private final String workingDirectory;
    
    public SystemInfoMonitorEntry(final Entry entry) {
        super(entry);
        this.availableCPUs = this.getLong("availableCPUs");
        this.classpath = this.getString("classPath");
        this.freeMemory = this.getLong("freeUsedMemory");
        this.hostname = this.getString("systemName");
        this.instanceRoot = this.getString("instanceRoot");
        this.javaHome = this.getString("javaHome");
        this.javaVendor = this.getString("javaVendor");
        this.javaVersion = this.getString("javaVersion");
        this.jvmArchitecture = this.getString("jvmArchitecture");
        this.jvmArguments = this.getString("jvmArguments");
        this.jvmPID = this.getLong("jvmPID");
        this.jvmVendor = this.getString("jvmVendor");
        this.jvmVersion = this.getString("jvmVersion");
        this.maxMemory = this.getLong("maxMemory");
        this.operatingSystem = this.getString("operatingSystem");
        this.sslContextProtocol = this.getString("sslContextProtocol");
        this.usedMemory = this.getLong("usedMemory");
        this.userName = this.getString("userName");
        this.workingDirectory = this.getString("workingDirectory");
        final List<String> envValues = this.getStrings("environmentVariable");
        final LinkedHashMap<String, String> envMap = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(envValues.size()));
        for (final String s : envValues) {
            final int eqPos = s.indexOf("='");
            if (eqPos > 0) {
                final String name = s.substring(0, eqPos);
                if (eqPos == s.length() - 2) {
                    continue;
                }
                envMap.put(name, s.substring(eqPos + 2, s.length() - 1));
            }
        }
        this.environmentVariables = Collections.unmodifiableMap((Map<? extends String, ? extends String>)envMap);
        final List<String> propValues = this.getStrings("systemProperty");
        final LinkedHashMap<String, String> propMap = new LinkedHashMap<String, String>(StaticUtils.computeMapCapacity(propValues.size()));
        for (final String s2 : propValues) {
            final int eqPos2 = s2.indexOf("='");
            if (eqPos2 > 0) {
                final String name2 = s2.substring(0, eqPos2);
                if (eqPos2 == s2.length() - 2) {
                    continue;
                }
                propMap.put(name2, s2.substring(eqPos2 + 2, s2.length() - 1));
            }
        }
        this.systemProperties = Collections.unmodifiableMap((Map<? extends String, ? extends String>)propMap);
    }
    
    public Long getAvailableCPUs() {
        return this.availableCPUs;
    }
    
    public String getClassPath() {
        return this.classpath;
    }
    
    public Map<String, String> getEnvironmentVariables() {
        return this.environmentVariables;
    }
    
    public Long getFreeMemory() {
        return this.freeMemory;
    }
    
    public String getHostname() {
        return this.hostname;
    }
    
    public String getInstanceRoot() {
        return this.instanceRoot;
    }
    
    public String getJavaHome() {
        return this.javaHome;
    }
    
    public String getJavaVendor() {
        return this.javaVendor;
    }
    
    public String getJavaVersion() {
        return this.javaVersion;
    }
    
    public String getJVMArchitectureDataModel() {
        return this.jvmArchitecture;
    }
    
    public String getJVMArguments() {
        return this.jvmArguments;
    }
    
    public Long getJVMPID() {
        return this.jvmPID;
    }
    
    public String getJVMVendor() {
        return this.jvmVendor;
    }
    
    public String getJVMVersion() {
        return this.jvmVersion;
    }
    
    public Long getMaxMemory() {
        return this.maxMemory;
    }
    
    public String getOperatingSystem() {
        return this.operatingSystem;
    }
    
    public String getSSLContextProtocol() {
        return this.sslContextProtocol;
    }
    
    public Map<String, String> getSystemProperties() {
        return this.systemProperties;
    }
    
    public Long getUsedMemory() {
        return this.usedMemory;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public String getWorkingDirectory() {
        return this.workingDirectory;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_SYSTEM_INFO_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_SYSTEM_INFO_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(30));
        if (this.hostname != null) {
            MonitorEntry.addMonitorAttribute(attrs, "systemName", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_HOSTNAME.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_HOSTNAME.get(), this.hostname);
        }
        if (this.operatingSystem != null) {
            MonitorEntry.addMonitorAttribute(attrs, "operatingSystem", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_OPERATING_SYSTEM.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_OPERATING_SYSTEM.get(), this.operatingSystem);
        }
        if (this.jvmArchitecture != null) {
            MonitorEntry.addMonitorAttribute(attrs, "jvmArchitecture", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_JVM_ARCHITECTURE.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_JVM_ARCHITECTURE.get(), this.jvmArchitecture);
        }
        if (this.javaHome != null) {
            MonitorEntry.addMonitorAttribute(attrs, "javaHome", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_JAVA_HOME.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_JAVA_HOME.get(), this.javaHome);
        }
        if (this.javaVersion != null) {
            MonitorEntry.addMonitorAttribute(attrs, "javaVersion", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_JAVA_VERSION.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_JAVA_VERSION.get(), this.javaVersion);
        }
        if (this.javaVendor != null) {
            MonitorEntry.addMonitorAttribute(attrs, "javaVendor", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_JAVA_VENDOR.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_JAVA_VENDOR.get(), this.javaVendor);
        }
        if (this.jvmVersion != null) {
            MonitorEntry.addMonitorAttribute(attrs, "jvmVersion", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_JVM_VERSION.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_JVM_VERSION.get(), this.jvmVersion);
        }
        if (this.jvmVendor != null) {
            MonitorEntry.addMonitorAttribute(attrs, "jvmVendor", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_JVM_VENDOR.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_JVM_VENDOR.get(), this.jvmVendor);
        }
        if (this.jvmArguments != null) {
            MonitorEntry.addMonitorAttribute(attrs, "jvmArguments", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_JVM_ARGUMENTS.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_JVM_ARGUMENTS.get(), this.jvmArguments);
        }
        if (this.jvmPID != null) {
            MonitorEntry.addMonitorAttribute(attrs, "jvmPID", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_JVM_PID.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_JVM_PID.get(), this.jvmPID);
        }
        if (this.sslContextProtocol != null) {
            MonitorEntry.addMonitorAttribute(attrs, "sslContextProtocol", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_SSL_CONTEXT_PROTOCOL.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_SSL_CONTEXT_PROTOCOL.get(), this.sslContextProtocol);
        }
        if (this.classpath != null) {
            MonitorEntry.addMonitorAttribute(attrs, "classPath", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_CLASSPATH.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_CLASSPATH.get(), this.classpath);
        }
        if (this.instanceRoot != null) {
            MonitorEntry.addMonitorAttribute(attrs, "instanceRoot", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_INSTANCE_ROOT.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_INSTANCE_ROOT.get(), this.instanceRoot);
        }
        if (this.workingDirectory != null) {
            MonitorEntry.addMonitorAttribute(attrs, "workingDirectory", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_WORKING_DIRECTORY.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_WORKING_DIRECTORY.get(), this.workingDirectory);
        }
        if (this.availableCPUs != null) {
            MonitorEntry.addMonitorAttribute(attrs, "availableCPUs", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_AVAILABLE_CPUS.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_AVAILABLE_CPUS.get(), this.availableCPUs);
        }
        if (this.usedMemory != null) {
            MonitorEntry.addMonitorAttribute(attrs, "usedMemory", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_USED_MEMORY.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_USED_MEMORY.get(), this.usedMemory);
        }
        if (this.maxMemory != null) {
            MonitorEntry.addMonitorAttribute(attrs, "maxMemory", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_MAX_MEMORY.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_MAX_MEMORY.get(), this.maxMemory);
        }
        if (this.freeMemory != null) {
            MonitorEntry.addMonitorAttribute(attrs, "freeUsedMemory", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_FREE_MEMORY.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_FREE_MEMORY.get(), this.freeMemory);
        }
        if (this.userName != null) {
            MonitorEntry.addMonitorAttribute(attrs, "userName", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_USER_NAME.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_USER_NAME.get(), this.userName);
        }
        if (!this.environmentVariables.isEmpty()) {
            final ArrayList<String> envList = new ArrayList<String>(this.environmentVariables.size());
            for (final Map.Entry<String, String> e : this.environmentVariables.entrySet()) {
                envList.add(e.getKey() + "='" + e.getValue() + '\'');
            }
            MonitorEntry.addMonitorAttribute(attrs, "environmentVariable", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_ENV_VAR.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_ENV_VAR.get(), envList);
        }
        if (!this.systemProperties.isEmpty()) {
            final ArrayList<String> propList = new ArrayList<String>(this.systemProperties.size());
            for (final Map.Entry<String, String> e : this.systemProperties.entrySet()) {
                propList.add(e.getKey() + "='" + e.getValue() + '\'');
            }
            MonitorEntry.addMonitorAttribute(attrs, "systemProperty", MonitorMessages.INFO_SYSTEM_INFO_DISPNAME_SYSTEM_PROP.get(), MonitorMessages.INFO_SYSTEM_INFO_DESC_SYSTEM_PROP.get(), propList);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
