package org.apache.commons.lang;

public class SystemUtils
{
    public static final String FILE_ENCODING;
    public static final String FILE_SEPARATOR;
    public static final String JAVA_CLASS_PATH;
    public static final String JAVA_CLASS_VERSION;
    public static final String JAVA_COMPILER;
    public static final String JAVA_EXT_DIRS;
    public static final String JAVA_HOME;
    public static final String JAVA_IO_TMPDIR;
    public static final String JAVA_LIBRARY_PATH;
    public static final String JAVA_RUNTIME_NAME;
    public static final String JAVA_RUNTIME_VERSION;
    public static final String JAVA_SPECIFICATION_NAME;
    public static final String JAVA_SPECIFICATION_VENDOR;
    public static final String JAVA_SPECIFICATION_VERSION;
    public static final String JAVA_VENDOR;
    public static final String JAVA_VENDOR_URL;
    public static final String JAVA_VERSION;
    public static final String JAVA_VM_INFO;
    public static final String JAVA_VM_NAME;
    public static final String JAVA_VM_SPECIFICATION_NAME;
    public static final String JAVA_VM_SPECIFICATION_VENDOR;
    public static final String JAVA_VM_SPECIFICATION_VERSION;
    public static final String JAVA_VM_VENDOR;
    public static final String JAVA_VM_VERSION;
    public static final String LINE_SEPARATOR;
    public static final String OS_ARCH;
    public static final String OS_NAME;
    public static final String OS_VERSION;
    public static final String PATH_SEPARATOR;
    public static final String USER_COUNTRY;
    public static final String USER_DIR;
    public static final String USER_HOME;
    public static final String USER_LANGUAGE;
    public static final String USER_NAME;
    public static final float JAVA_VERSION_FLOAT;
    public static final int JAVA_VERSION_INT;
    public static final boolean IS_JAVA_1_1;
    public static final boolean IS_JAVA_1_2;
    public static final boolean IS_JAVA_1_3;
    public static final boolean IS_JAVA_1_4;
    public static final boolean IS_JAVA_1_5;
    public static final boolean IS_OS_AIX;
    public static final boolean IS_OS_HP_UX;
    public static final boolean IS_OS_IRIX;
    public static final boolean IS_OS_LINUX;
    public static final boolean IS_OS_MAC;
    public static final boolean IS_OS_MAC_OSX;
    public static final boolean IS_OS_OS2;
    public static final boolean IS_OS_SOLARIS;
    public static final boolean IS_OS_SUN_OS;
    public static final boolean IS_OS_WINDOWS;
    public static final boolean IS_OS_WINDOWS_2000;
    public static final boolean IS_OS_WINDOWS_95;
    public static final boolean IS_OS_WINDOWS_98;
    public static final boolean IS_OS_WINDOWS_ME;
    public static final boolean IS_OS_WINDOWS_NT;
    public static final boolean IS_OS_WINDOWS_XP;
    
    public static float getJavaVersion() {
        return SystemUtils.JAVA_VERSION_FLOAT;
    }
    
    private static float getJavaVersionAsFloat() {
        if (SystemUtils.JAVA_VERSION == null) {
            return 0.0f;
        }
        String str = SystemUtils.JAVA_VERSION.substring(0, 3);
        if (SystemUtils.JAVA_VERSION.length() >= 5) {
            str += SystemUtils.JAVA_VERSION.substring(4, 5);
        }
        return Float.parseFloat(str);
    }
    
    private static int getJavaVersionAsInt() {
        if (SystemUtils.JAVA_VERSION == null) {
            return 0;
        }
        String str = SystemUtils.JAVA_VERSION.substring(0, 1);
        str += SystemUtils.JAVA_VERSION.substring(2, 3);
        if (SystemUtils.JAVA_VERSION.length() >= 5) {
            str += SystemUtils.JAVA_VERSION.substring(4, 5);
        }
        else {
            str += "0";
        }
        return Integer.parseInt(str);
    }
    
    private static boolean getJavaVersionMatches(final String versionPrefix) {
        return SystemUtils.JAVA_VERSION != null && SystemUtils.JAVA_VERSION.startsWith(versionPrefix);
    }
    
    private static boolean getOSMatches(final String osNamePrefix) {
        return SystemUtils.OS_NAME != null && SystemUtils.OS_NAME.startsWith(osNamePrefix);
    }
    
    private static boolean getOSMatches(final String osNamePrefix, final String osVersionPrefix) {
        return SystemUtils.OS_NAME != null && SystemUtils.OS_VERSION != null && SystemUtils.OS_NAME.startsWith(osNamePrefix) && SystemUtils.OS_VERSION.startsWith(osVersionPrefix);
    }
    
    private static String getSystemProperty(final String property) {
        try {
            return System.getProperty(property);
        }
        catch (final SecurityException ex) {
            System.err.println("Caught a SecurityException reading the system property '" + property + "'; the SystemUtils property value will default to null.");
            return null;
        }
    }
    
    public static boolean isJavaVersionAtLeast(final float requiredVersion) {
        return SystemUtils.JAVA_VERSION_FLOAT >= requiredVersion;
    }
    
    public static boolean isJavaVersionAtLeast(final int requiredVersion) {
        return SystemUtils.JAVA_VERSION_INT >= requiredVersion;
    }
    
    static {
        FILE_ENCODING = getSystemProperty("file.encoding");
        FILE_SEPARATOR = getSystemProperty("file.separator");
        JAVA_CLASS_PATH = getSystemProperty("java.class.path");
        JAVA_CLASS_VERSION = getSystemProperty("java.class.version");
        JAVA_COMPILER = getSystemProperty("java.compiler");
        JAVA_EXT_DIRS = getSystemProperty("java.ext.dirs");
        JAVA_HOME = getSystemProperty("java.home");
        JAVA_IO_TMPDIR = getSystemProperty("java.io.tmpdir");
        JAVA_LIBRARY_PATH = getSystemProperty("java.library.path");
        JAVA_RUNTIME_NAME = getSystemProperty("java.runtime.name");
        JAVA_RUNTIME_VERSION = getSystemProperty("java.runtime.version");
        JAVA_SPECIFICATION_NAME = getSystemProperty("java.specification.name");
        JAVA_SPECIFICATION_VENDOR = getSystemProperty("java.specification.vendor");
        JAVA_SPECIFICATION_VERSION = getSystemProperty("java.specification.version");
        JAVA_VENDOR = getSystemProperty("java.vendor");
        JAVA_VENDOR_URL = getSystemProperty("java.vendor.url");
        JAVA_VERSION = getSystemProperty("java.version");
        JAVA_VM_INFO = getSystemProperty("java.vm.info");
        JAVA_VM_NAME = getSystemProperty("java.vm.name");
        JAVA_VM_SPECIFICATION_NAME = getSystemProperty("java.vm.specification.name");
        JAVA_VM_SPECIFICATION_VENDOR = getSystemProperty("java.vm.specification.vendor");
        JAVA_VM_SPECIFICATION_VERSION = getSystemProperty("java.vm.specification.version");
        JAVA_VM_VENDOR = getSystemProperty("java.vm.vendor");
        JAVA_VM_VERSION = getSystemProperty("java.vm.version");
        LINE_SEPARATOR = getSystemProperty("line.separator");
        OS_ARCH = getSystemProperty("os.arch");
        OS_NAME = getSystemProperty("os.name");
        OS_VERSION = getSystemProperty("os.version");
        PATH_SEPARATOR = getSystemProperty("path.separator");
        USER_COUNTRY = ((getSystemProperty("user.country") == null) ? getSystemProperty("user.region") : getSystemProperty("user.country"));
        USER_DIR = getSystemProperty("user.dir");
        USER_HOME = getSystemProperty("user.home");
        USER_LANGUAGE = getSystemProperty("user.language");
        USER_NAME = getSystemProperty("user.name");
        JAVA_VERSION_FLOAT = getJavaVersionAsFloat();
        JAVA_VERSION_INT = getJavaVersionAsInt();
        IS_JAVA_1_1 = getJavaVersionMatches("1.1");
        IS_JAVA_1_2 = getJavaVersionMatches("1.2");
        IS_JAVA_1_3 = getJavaVersionMatches("1.3");
        IS_JAVA_1_4 = getJavaVersionMatches("1.4");
        IS_JAVA_1_5 = getJavaVersionMatches("1.5");
        IS_OS_AIX = getOSMatches("AIX");
        IS_OS_HP_UX = getOSMatches("HP-UX");
        IS_OS_IRIX = getOSMatches("Irix");
        IS_OS_LINUX = (getOSMatches("Linux") || getOSMatches("LINUX"));
        IS_OS_MAC = getOSMatches("Mac");
        IS_OS_MAC_OSX = getOSMatches("Mac OS X");
        IS_OS_OS2 = getOSMatches("OS/2");
        IS_OS_SOLARIS = getOSMatches("Solaris");
        IS_OS_SUN_OS = getOSMatches("SunOS");
        IS_OS_WINDOWS = getOSMatches("Windows");
        IS_OS_WINDOWS_2000 = getOSMatches("Windows", "5.0");
        IS_OS_WINDOWS_95 = getOSMatches("Windows 9", "4.0");
        IS_OS_WINDOWS_98 = getOSMatches("Windows 9", "4.1");
        IS_OS_WINDOWS_ME = getOSMatches("Windows", "4.9");
        IS_OS_WINDOWS_NT = getOSMatches("Windows NT");
        IS_OS_WINDOWS_XP = getOSMatches("Windows", "5.1");
    }
}
