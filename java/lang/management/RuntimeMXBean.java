package java.lang.management;

import java.util.Map;
import java.util.List;

public interface RuntimeMXBean extends PlatformManagedObject
{
    String getName();
    
    String getVmName();
    
    String getVmVendor();
    
    String getVmVersion();
    
    String getSpecName();
    
    String getSpecVendor();
    
    String getSpecVersion();
    
    String getManagementSpecVersion();
    
    String getClassPath();
    
    String getLibraryPath();
    
    boolean isBootClassPathSupported();
    
    String getBootClassPath();
    
    List<String> getInputArguments();
    
    long getUptime();
    
    long getStartTime();
    
    Map<String, String> getSystemProperties();
}
