package sun.awt;

import java.util.HashMap;
import java.security.PrivilegedAction;
import java.util.Map;

public class OSInfo
{
    public static final WindowsVersion WINDOWS_UNKNOWN;
    public static final WindowsVersion WINDOWS_95;
    public static final WindowsVersion WINDOWS_98;
    public static final WindowsVersion WINDOWS_ME;
    public static final WindowsVersion WINDOWS_2000;
    public static final WindowsVersion WINDOWS_XP;
    public static final WindowsVersion WINDOWS_2003;
    public static final WindowsVersion WINDOWS_VISTA;
    private static final String OS_NAME = "os.name";
    private static final String OS_VERSION = "os.version";
    private static final Map<String, WindowsVersion> windowsVersionMap;
    private static final PrivilegedAction<OSType> osTypeAction;
    
    private OSInfo() {
    }
    
    public static OSType getOSType() throws SecurityException {
        final String property = System.getProperty("os.name");
        if (property != null) {
            if (property.contains("Windows")) {
                return OSType.WINDOWS;
            }
            if (property.contains("Linux")) {
                return OSType.LINUX;
            }
            if (property.contains("Solaris") || property.contains("SunOS")) {
                return OSType.SOLARIS;
            }
            if (property.contains("OS X")) {
                return OSType.MACOSX;
            }
        }
        return OSType.UNKNOWN;
    }
    
    public static PrivilegedAction<OSType> getOSTypeAction() {
        return OSInfo.osTypeAction;
    }
    
    public static WindowsVersion getWindowsVersion() throws SecurityException {
        final String property = System.getProperty("os.version");
        if (property == null) {
            return OSInfo.WINDOWS_UNKNOWN;
        }
        synchronized (OSInfo.windowsVersionMap) {
            WindowsVersion windowsVersion = OSInfo.windowsVersionMap.get(property);
            if (windowsVersion == null) {
                final String[] split = property.split("\\.");
                if (split.length == 2) {
                    Label_0088: {
                        try {
                            windowsVersion = new WindowsVersion(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                            break Label_0088;
                        }
                        catch (final NumberFormatException ex) {
                            return OSInfo.WINDOWS_UNKNOWN;
                        }
                        return OSInfo.WINDOWS_UNKNOWN;
                    }
                    OSInfo.windowsVersionMap.put(property, windowsVersion);
                    return windowsVersion;
                }
                return OSInfo.WINDOWS_UNKNOWN;
            }
            return windowsVersion;
        }
    }
    
    static {
        WINDOWS_UNKNOWN = new WindowsVersion(-1, -1);
        WINDOWS_95 = new WindowsVersion(4, 0);
        WINDOWS_98 = new WindowsVersion(4, 10);
        WINDOWS_ME = new WindowsVersion(4, 90);
        WINDOWS_2000 = new WindowsVersion(5, 0);
        WINDOWS_XP = new WindowsVersion(5, 1);
        WINDOWS_2003 = new WindowsVersion(5, 2);
        WINDOWS_VISTA = new WindowsVersion(6, 0);
        (windowsVersionMap = new HashMap<String, WindowsVersion>()).put(OSInfo.WINDOWS_95.toString(), OSInfo.WINDOWS_95);
        OSInfo.windowsVersionMap.put(OSInfo.WINDOWS_98.toString(), OSInfo.WINDOWS_98);
        OSInfo.windowsVersionMap.put(OSInfo.WINDOWS_ME.toString(), OSInfo.WINDOWS_ME);
        OSInfo.windowsVersionMap.put(OSInfo.WINDOWS_2000.toString(), OSInfo.WINDOWS_2000);
        OSInfo.windowsVersionMap.put(OSInfo.WINDOWS_XP.toString(), OSInfo.WINDOWS_XP);
        OSInfo.windowsVersionMap.put(OSInfo.WINDOWS_2003.toString(), OSInfo.WINDOWS_2003);
        OSInfo.windowsVersionMap.put(OSInfo.WINDOWS_VISTA.toString(), OSInfo.WINDOWS_VISTA);
        osTypeAction = new PrivilegedAction<OSType>() {
            @Override
            public OSType run() {
                return OSInfo.getOSType();
            }
        };
    }
    
    public enum OSType
    {
        WINDOWS, 
        LINUX, 
        SOLARIS, 
        MACOSX, 
        UNKNOWN;
    }
    
    public static class WindowsVersion implements Comparable<WindowsVersion>
    {
        private final int major;
        private final int minor;
        
        private WindowsVersion(final int major, final int minor) {
            this.major = major;
            this.minor = minor;
        }
        
        public int getMajor() {
            return this.major;
        }
        
        public int getMinor() {
            return this.minor;
        }
        
        @Override
        public int compareTo(final WindowsVersion windowsVersion) {
            int n = this.major - windowsVersion.getMajor();
            if (n == 0) {
                n = this.minor - windowsVersion.getMinor();
            }
            return n;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof WindowsVersion && this.compareTo((WindowsVersion)o) == 0;
        }
        
        @Override
        public int hashCode() {
            return 31 * this.major + this.minor;
        }
        
        @Override
        public String toString() {
            return this.major + "." + this.minor;
        }
    }
}
