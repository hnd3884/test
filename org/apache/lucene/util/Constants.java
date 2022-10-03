package org.apache.lucene.util;

import java.util.StringTokenizer;

public final class Constants
{
    public static final String JVM_VENDOR;
    public static final String JVM_VERSION;
    public static final String JVM_NAME;
    public static final String JVM_SPEC_VERSION;
    public static final String JAVA_VERSION;
    public static final String OS_NAME;
    public static final boolean LINUX;
    public static final boolean WINDOWS;
    public static final boolean SUN_OS;
    public static final boolean MAC_OS_X;
    public static final boolean FREE_BSD;
    public static final String OS_ARCH;
    public static final String OS_VERSION;
    public static final String JAVA_VENDOR;
    private static final int JVM_MAJOR_VERSION;
    private static final int JVM_MINOR_VERSION;
    public static final boolean JRE_IS_64BIT;
    public static final boolean JRE_IS_MINIMUM_JAVA8;
    public static final boolean JRE_IS_MINIMUM_JAVA9;
    @Deprecated
    public static final String LUCENE_MAIN_VERSION;
    @Deprecated
    public static final String LUCENE_VERSION;
    
    private Constants() {
    }
    
    static {
        JVM_VENDOR = System.getProperty("java.vm.vendor");
        JVM_VERSION = System.getProperty("java.vm.version");
        JVM_NAME = System.getProperty("java.vm.name");
        JVM_SPEC_VERSION = System.getProperty("java.specification.version");
        JAVA_VERSION = System.getProperty("java.version");
        OS_NAME = System.getProperty("os.name");
        LINUX = Constants.OS_NAME.startsWith("Linux");
        WINDOWS = Constants.OS_NAME.startsWith("Windows");
        SUN_OS = Constants.OS_NAME.startsWith("SunOS");
        MAC_OS_X = Constants.OS_NAME.startsWith("Mac OS X");
        FREE_BSD = Constants.OS_NAME.startsWith("FreeBSD");
        OS_ARCH = System.getProperty("os.arch");
        OS_VERSION = System.getProperty("os.version");
        JAVA_VENDOR = System.getProperty("java.vendor");
        final StringTokenizer st = new StringTokenizer(Constants.JVM_SPEC_VERSION, ".");
        JVM_MAJOR_VERSION = Integer.parseInt(st.nextToken());
        if (st.hasMoreTokens()) {
            JVM_MINOR_VERSION = Integer.parseInt(st.nextToken());
        }
        else {
            JVM_MINOR_VERSION = 0;
        }
        boolean is64Bit = false;
        final String x = System.getProperty("sun.arch.data.model");
        if (x != null) {
            is64Bit = x.contains("64");
        }
        else {
            is64Bit = (Constants.OS_ARCH != null && Constants.OS_ARCH.contains("64"));
        }
        JRE_IS_64BIT = is64Bit;
        JRE_IS_MINIMUM_JAVA8 = (Constants.JVM_MAJOR_VERSION > 1 || (Constants.JVM_MAJOR_VERSION == 1 && Constants.JVM_MINOR_VERSION >= 8));
        JRE_IS_MINIMUM_JAVA9 = (Constants.JVM_MAJOR_VERSION > 1 || (Constants.JVM_MAJOR_VERSION == 1 && Constants.JVM_MINOR_VERSION >= 9));
        LUCENE_MAIN_VERSION = Version.LATEST.toString();
        LUCENE_VERSION = Version.LATEST.toString();
    }
}
