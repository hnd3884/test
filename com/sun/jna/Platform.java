package com.sun.jna;

public final class Platform
{
    public static final int UNSPECIFIED = -1;
    public static final int MAC = 0;
    public static final int LINUX = 1;
    public static final int WINDOWS = 2;
    public static final int SOLARIS = 3;
    public static final int FREEBSD = 4;
    public static final int OPENBSD = 5;
    public static final int WINDOWSCE = 6;
    private static final int osType;
    
    private Platform() {
    }
    
    public static final int getOSType() {
        return Platform.osType;
    }
    
    public static final boolean isMac() {
        return Platform.osType == 0;
    }
    
    public static final boolean isLinux() {
        return Platform.osType == 1;
    }
    
    public static final boolean isWindowsCE() {
        return Platform.osType == 6;
    }
    
    public static final boolean isWindows() {
        return Platform.osType == 2 || Platform.osType == 6;
    }
    
    public static final boolean isSolaris() {
        return Platform.osType == 3;
    }
    
    public static final boolean isFreeBSD() {
        return Platform.osType == 4;
    }
    
    public static final boolean isOpenBSD() {
        return Platform.osType == 5;
    }
    
    public static final boolean isX11() {
        return !isWindows() && !isMac();
    }
    
    public static final boolean deleteNativeLibraryAfterVMExit() {
        return Platform.osType == 2;
    }
    
    public static final boolean hasRuntimeExec() {
        return !isWindowsCE() || !"J9".equals(System.getProperty("java.vm.name"));
    }
    
    public static final boolean is64Bit() {
        final String model = System.getProperty("sun.arch.data.model");
        if (model != null) {
            return "64".equals(model);
        }
        final String arch = System.getProperty("os.arch").toLowerCase();
        return "x86_64".equals(arch) || "ppc64".equals(arch) || "sparcv9".equals(arch) || "amd64".equals(arch) || Native.POINTER_SIZE == 8;
    }
    
    static {
        final String osName = System.getProperty("os.name");
        if (osName.startsWith("Linux")) {
            osType = 1;
        }
        else if (osName.startsWith("Mac") || osName.startsWith("Darwin")) {
            osType = 0;
        }
        else if (osName.startsWith("Windows CE")) {
            osType = 6;
        }
        else if (osName.startsWith("Windows")) {
            osType = 2;
        }
        else if (osName.startsWith("Solaris") || osName.startsWith("SunOS")) {
            osType = 3;
        }
        else if (osName.startsWith("FreeBSD")) {
            osType = 4;
        }
        else if (osName.startsWith("OpenBSD")) {
            osType = 5;
        }
        else {
            osType = -1;
        }
    }
}
