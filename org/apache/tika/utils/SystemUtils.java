package org.apache.tika.utils;

public class SystemUtils
{
    public static final String OS_NAME;
    public static final String OS_VERSION;
    public static final boolean IS_OS_AIX;
    public static final boolean IS_OS_HP_UX;
    public static final boolean IS_OS_IRIX;
    public static final boolean IS_OS_LINUX;
    public static final boolean IS_OS_MAC;
    public static final boolean IS_OS_MAC_OSX;
    public static final boolean IS_OS_OS2;
    public static final boolean IS_OS_SOLARIS;
    public static final boolean IS_OS_SUN_OS;
    public static final boolean IS_OS_UNIX;
    public static final boolean IS_OS_WINDOWS;
    private static final String OS_NAME_WINDOWS_PREFIX = "Windows";
    
    private static String getSystemProperty(final String property) {
        try {
            return System.getProperty(property);
        }
        catch (final SecurityException var2) {
            return null;
        }
    }
    
    private static boolean getOSMatchesName(final String osNamePrefix) {
        return isOSNameMatch(SystemUtils.OS_NAME, osNamePrefix);
    }
    
    static boolean isOSNameMatch(final String osName, final String osNamePrefix) {
        return osName != null && osName.startsWith(osNamePrefix);
    }
    
    static {
        OS_NAME = getSystemProperty("os.name");
        OS_VERSION = getSystemProperty("os.version");
        IS_OS_AIX = getOSMatchesName("AIX");
        IS_OS_HP_UX = getOSMatchesName("HP-UX");
        IS_OS_IRIX = getOSMatchesName("Irix");
        IS_OS_LINUX = (getOSMatchesName("Linux") || getOSMatchesName("LINUX"));
        IS_OS_MAC = getOSMatchesName("Mac");
        IS_OS_MAC_OSX = getOSMatchesName("Mac OS X");
        IS_OS_OS2 = getOSMatchesName("OS/2");
        IS_OS_SOLARIS = getOSMatchesName("Solaris");
        IS_OS_SUN_OS = getOSMatchesName("SunOS");
        IS_OS_UNIX = (SystemUtils.IS_OS_AIX || SystemUtils.IS_OS_HP_UX || SystemUtils.IS_OS_IRIX || SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC_OSX || SystemUtils.IS_OS_SOLARIS || SystemUtils.IS_OS_SUN_OS);
        IS_OS_WINDOWS = getOSMatchesName("Windows");
    }
}
