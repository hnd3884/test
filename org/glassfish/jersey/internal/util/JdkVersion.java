package org.glassfish.jersey.internal.util;

public class JdkVersion implements Comparable<JdkVersion>
{
    private static final boolean IS_UNSAFE_SUPPORTED;
    private static final JdkVersion UNKNOWN_VERSION;
    private static final JdkVersion JDK_VERSION;
    private final int major;
    private final int minor;
    private final int maintenance;
    private final int update;
    
    private JdkVersion(final int major, final int minor, final int maintenance, final int update) {
        this.major = major;
        this.minor = minor;
        this.maintenance = maintenance;
        this.update = update;
    }
    
    public static JdkVersion parseVersion(String versionString) {
        try {
            final int dashIdx = versionString.indexOf(45);
            if (dashIdx != -1) {
                versionString = versionString.substring(0, dashIdx);
            }
            final String[] parts = versionString.split("\\.|_");
            if (parts.length == 3) {
                return new JdkVersion(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), 0);
            }
            return new JdkVersion(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        }
        catch (final Exception e) {
            return JdkVersion.UNKNOWN_VERSION;
        }
    }
    
    public static JdkVersion getJdkVersion() {
        return JdkVersion.JDK_VERSION;
    }
    
    public int getMajor() {
        return this.major;
    }
    
    public int getMinor() {
        return this.minor;
    }
    
    public int getMaintenance() {
        return this.maintenance;
    }
    
    public int getUpdate() {
        return this.update;
    }
    
    public boolean isUnsafeSupported() {
        return JdkVersion.IS_UNSAFE_SUPPORTED;
    }
    
    @Override
    public String toString() {
        return "JdkVersion{major=" + this.major + ", minor=" + this.minor + ", maintenance=" + this.maintenance + ", update=" + this.update + '}';
    }
    
    public int compareTo(final String versionString) {
        return this.compareTo(parseVersion(versionString));
    }
    
    @Override
    public int compareTo(final JdkVersion otherVersion) {
        if (this.major < otherVersion.major) {
            return -1;
        }
        if (this.major > otherVersion.major) {
            return 1;
        }
        if (this.minor < otherVersion.minor) {
            return -1;
        }
        if (this.minor > otherVersion.minor) {
            return 1;
        }
        if (this.maintenance < otherVersion.maintenance) {
            return -1;
        }
        if (this.maintenance > otherVersion.maintenance) {
            return 1;
        }
        if (this.update < otherVersion.update) {
            return -1;
        }
        if (this.update > otherVersion.update) {
            return 1;
        }
        return 0;
    }
    
    static {
        boolean unsafeSupported;
        try {
            unsafeSupported = (Class.forName("sun.misc.Unsafe") != null);
            unsafeSupported &= (System.getProperty("com.google.appengine.runtime.environment") == null);
        }
        catch (final Throwable t) {
            unsafeSupported = false;
        }
        IS_UNSAFE_SUPPORTED = unsafeSupported;
        UNKNOWN_VERSION = new JdkVersion(-1, -1, -1, -1);
        JDK_VERSION = parseVersion(System.getProperty("java.version"));
    }
}
