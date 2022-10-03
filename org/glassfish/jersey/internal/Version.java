package org.glassfish.jersey.internal;

import java.io.InputStream;
import java.util.Properties;

public final class Version
{
    private static String buildId;
    private static String version;
    
    private Version() {
        throw new AssertionError((Object)"Instantiation not allowed.");
    }
    
    private static void _initiateProperties() {
        final InputStream in = getIntputStream();
        if (in != null) {
            try {
                final Properties p = new Properties();
                p.load(in);
                final String timestamp = p.getProperty("Build-Timestamp");
                Version.version = p.getProperty("Build-Version");
                Version.buildId = String.format("Jersey: %s %s", Version.version, timestamp);
            }
            catch (final Exception e) {
                Version.buildId = "Jersey";
            }
            finally {
                close(in);
            }
        }
    }
    
    private static void close(final InputStream in) {
        try {
            in.close();
        }
        catch (final Exception ex) {}
    }
    
    private static InputStream getIntputStream() {
        try {
            return Version.class.getResourceAsStream("build.properties");
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public static String getBuildId() {
        return Version.buildId;
    }
    
    public static String getVersion() {
        return Version.version;
    }
    
    static {
        Version.version = null;
        _initiateProperties();
    }
}
