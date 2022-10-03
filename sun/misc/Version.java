package sun.misc;

import java.io.PrintStream;

public class Version
{
    private static final String launcher_name = "openjdk";
    private static final String java_version = "1.8.0_282";
    private static final String java_runtime_name = "OpenJDK Runtime Environment";
    private static final String java_profile_name = "";
    private static final String java_runtime_version = "1.8.0_282-b08";
    private static final String java_vendor_version = "Zulu 8.52.0.23-CA-win64";
    private static final String azul_runtime_name = "Zulu 8.52.0.23-CA-win64";
    private static final String azul_runtime_suffix = "";
    private static final String azul_version_suffix = "";
    private static boolean versionsInitialized;
    private static int jvm_major_version;
    private static int jvm_minor_version;
    private static int jvm_micro_version;
    private static int jvm_update_version;
    private static int jvm_build_number;
    private static String jvm_special_version;
    private static int jdk_major_version;
    private static int jdk_minor_version;
    private static int jdk_micro_version;
    private static int jdk_update_version;
    private static int jdk_build_number;
    private static String jdk_special_version;
    private static boolean jvmVersionInfoAvailable;
    
    public static void init() {
        System.setProperty("java.version", "1.8.0_282");
        System.setProperty("java.runtime.version", "1.8.0_282-b08");
        System.setProperty("java.runtime.name", "OpenJDK Runtime Environment");
        System.setProperty("jdk.vendor.version", "Zulu 8.52.0.23-CA-win64");
    }
    
    public static void print() {
        print(System.err);
    }
    
    public static void println() {
        print(System.err);
        System.err.println();
    }
    
    public static void print(final PrintStream printStream) {
        boolean b = false;
        final String property = System.getProperty("java.awt.headless");
        if (property != null && property.equalsIgnoreCase("true")) {
            b = true;
        }
        printStream.println("openjdk version \"1.8.0_282\"");
        printStream.print("OpenJDK Runtime Environment (Zulu 8.52.0.23-CA-win64)");
        if ("".length() > 0) {
            printStream.print("-");
        }
        printStream.print(" (build 1.8.0_282-b08");
        if ("".length() > 0) {
            printStream.print(", profile ");
        }
        if ("OpenJDK Runtime Environment".indexOf("Embedded") != -1 && b) {
            printStream.print(", headless");
        }
        if ("".length() > 0) {
            printStream.println(") ");
        }
        else {
            printStream.println(')');
        }
        final String property2 = System.getProperty("java.vm.name");
        final String property3 = System.getProperty("java.vm.version");
        final String property4 = System.getProperty("java.vm.info");
        printStream.print(property2 + " (" + "Zulu 8.52.0.23-CA-win64" + ")");
        if ("".length() > 0) {
            printStream.print("-");
        }
        printStream.print(" (build " + property3 + ", " + property4);
        if ("".length() > 0) {
            printStream.println(") ");
        }
        else {
            printStream.println(')');
        }
    }
    
    public static synchronized int jvmMajorVersion() {
        if (!Version.versionsInitialized) {
            initVersions();
        }
        return Version.jvm_major_version;
    }
    
    public static synchronized int jvmMinorVersion() {
        if (!Version.versionsInitialized) {
            initVersions();
        }
        return Version.jvm_minor_version;
    }
    
    public static synchronized int jvmMicroVersion() {
        if (!Version.versionsInitialized) {
            initVersions();
        }
        return Version.jvm_micro_version;
    }
    
    public static synchronized int jvmUpdateVersion() {
        if (!Version.versionsInitialized) {
            initVersions();
        }
        return Version.jvm_update_version;
    }
    
    public static synchronized String jvmSpecialVersion() {
        if (!Version.versionsInitialized) {
            initVersions();
        }
        if (Version.jvm_special_version == null) {
            Version.jvm_special_version = getJvmSpecialVersion();
        }
        return Version.jvm_special_version;
    }
    
    public static native String getJvmSpecialVersion();
    
    public static synchronized int jvmBuildNumber() {
        if (!Version.versionsInitialized) {
            initVersions();
        }
        return Version.jvm_build_number;
    }
    
    public static synchronized int jdkMajorVersion() {
        if (!Version.versionsInitialized) {
            initVersions();
        }
        return Version.jdk_major_version;
    }
    
    public static synchronized int jdkMinorVersion() {
        if (!Version.versionsInitialized) {
            initVersions();
        }
        return Version.jdk_minor_version;
    }
    
    public static synchronized int jdkMicroVersion() {
        if (!Version.versionsInitialized) {
            initVersions();
        }
        return Version.jdk_micro_version;
    }
    
    public static synchronized int jdkUpdateVersion() {
        if (!Version.versionsInitialized) {
            initVersions();
        }
        return Version.jdk_update_version;
    }
    
    public static synchronized String jdkSpecialVersion() {
        if (!Version.versionsInitialized) {
            initVersions();
        }
        if (Version.jdk_special_version == null) {
            Version.jdk_special_version = getJdkSpecialVersion();
        }
        return Version.jdk_special_version;
    }
    
    public static native String getJdkSpecialVersion();
    
    public static synchronized int jdkBuildNumber() {
        if (!Version.versionsInitialized) {
            initVersions();
        }
        return Version.jdk_build_number;
    }
    
    private static synchronized void initVersions() {
        if (Version.versionsInitialized) {
            return;
        }
        if (!(Version.jvmVersionInfoAvailable = getJvmVersionInfo())) {
            final String property = System.getProperty("java.vm.version");
            if (property.length() >= 5 && Character.isDigit(property.charAt(0)) && property.charAt(1) == '.' && Character.isDigit(property.charAt(2)) && property.charAt(3) == '.' && Character.isDigit(property.charAt(4))) {
                Version.jvm_major_version = Character.digit(property.charAt(0), 10);
                Version.jvm_minor_version = Character.digit(property.charAt(2), 10);
                Version.jvm_micro_version = Character.digit(property.charAt(4), 10);
                CharSequence charSequence = property.subSequence(5, property.length());
                if (charSequence.charAt(0) == '_' && charSequence.length() >= 3) {
                    int n = 0;
                    if (Character.isDigit(charSequence.charAt(1)) && Character.isDigit(charSequence.charAt(2)) && Character.isDigit(charSequence.charAt(3))) {
                        n = 4;
                    }
                    else if (Character.isDigit(charSequence.charAt(1)) && Character.isDigit(charSequence.charAt(2))) {
                        n = 3;
                    }
                    try {
                        Version.jvm_update_version = Integer.valueOf(charSequence.subSequence(1, n).toString());
                        if (charSequence.length() >= n + 1) {
                            final char char1 = charSequence.charAt(n);
                            if (char1 >= 'a' && char1 <= 'z') {
                                Version.jvm_special_version = Character.toString(char1);
                                ++n;
                            }
                        }
                    }
                    catch (final NumberFormatException ex) {
                        return;
                    }
                    charSequence = charSequence.subSequence(n, charSequence.length());
                }
                if (charSequence.charAt(0) == '-') {
                    for (final String s : charSequence.subSequence(1, charSequence.length()).toString().split("-")) {
                        if (s.charAt(0) == 'b' && s.length() == 3 && Character.isDigit(s.charAt(1)) && Character.isDigit(s.charAt(2))) {
                            Version.jvm_build_number = Integer.valueOf(s.substring(1, 3));
                            break;
                        }
                    }
                }
            }
        }
        getJdkVersionInfo();
        Version.versionsInitialized = true;
    }
    
    private static native boolean getJvmVersionInfo();
    
    private static native void getJdkVersionInfo();
    
    static {
        init();
        Version.versionsInitialized = false;
        Version.jvm_major_version = 0;
        Version.jvm_minor_version = 0;
        Version.jvm_micro_version = 0;
        Version.jvm_update_version = 0;
        Version.jvm_build_number = 0;
        Version.jvm_special_version = null;
        Version.jdk_major_version = 0;
        Version.jdk_minor_version = 0;
        Version.jdk_micro_version = 0;
        Version.jdk_update_version = 0;
        Version.jdk_build_number = 0;
        Version.jdk_special_version = null;
    }
}
