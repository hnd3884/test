package sun.management;

import javax.management.ObjectName;
import java.util.Iterator;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.lang.management.RuntimeMXBean;

class RuntimeImpl implements RuntimeMXBean
{
    private final VMManagement jvm;
    private final long vmStartupTime;
    
    RuntimeImpl(final VMManagement jvm) {
        this.jvm = jvm;
        this.vmStartupTime = this.jvm.getStartupTime();
    }
    
    @Override
    public String getName() {
        return this.jvm.getVmId();
    }
    
    @Override
    public String getManagementSpecVersion() {
        return this.jvm.getManagementVersion();
    }
    
    @Override
    public String getVmName() {
        return this.jvm.getVmName();
    }
    
    @Override
    public String getVmVendor() {
        return this.jvm.getVmVendor();
    }
    
    @Override
    public String getVmVersion() {
        return this.jvm.getVmVersion();
    }
    
    @Override
    public String getSpecName() {
        return this.jvm.getVmSpecName();
    }
    
    @Override
    public String getSpecVendor() {
        return this.jvm.getVmSpecVendor();
    }
    
    @Override
    public String getSpecVersion() {
        return this.jvm.getVmSpecVersion();
    }
    
    @Override
    public String getClassPath() {
        return this.jvm.getClassPath();
    }
    
    @Override
    public String getLibraryPath() {
        return this.jvm.getLibraryPath();
    }
    
    @Override
    public String getBootClassPath() {
        if (!this.isBootClassPathSupported()) {
            throw new UnsupportedOperationException("Boot class path mechanism is not supported");
        }
        Util.checkMonitorAccess();
        return this.jvm.getBootClassPath();
    }
    
    @Override
    public List<String> getInputArguments() {
        Util.checkMonitorAccess();
        return this.jvm.getVmArguments();
    }
    
    @Override
    public long getUptime() {
        return this.jvm.getUptime();
    }
    
    @Override
    public long getStartTime() {
        return this.vmStartupTime;
    }
    
    @Override
    public boolean isBootClassPathSupported() {
        return this.jvm.isBootClassPathSupported();
    }
    
    @Override
    public Map<String, String> getSystemProperties() {
        final Properties properties = System.getProperties();
        final HashMap hashMap = new HashMap();
        for (final String s : properties.stringPropertyNames()) {
            hashMap.put(s, properties.getProperty(s));
        }
        return hashMap;
    }
    
    @Override
    public ObjectName getObjectName() {
        return Util.newObjectName("java.lang:type=Runtime");
    }
}
