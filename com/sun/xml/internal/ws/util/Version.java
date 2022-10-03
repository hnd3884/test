package com.sun.xml.internal.ws.util;

import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;

public final class Version
{
    public final String BUILD_ID;
    public final String BUILD_VERSION;
    public final String MAJOR_VERSION;
    public final String SVN_REVISION;
    public static final Version RUNTIME_VERSION;
    
    private Version(final String buildId, final String buildVersion, final String majorVersion, final String svnRev) {
        this.BUILD_ID = this.fixNull(buildId);
        this.BUILD_VERSION = this.fixNull(buildVersion);
        this.MAJOR_VERSION = this.fixNull(majorVersion);
        this.SVN_REVISION = this.fixNull(svnRev);
    }
    
    public static Version create(final InputStream is) {
        final Properties props = new Properties();
        try {
            props.load(is);
        }
        catch (final IOException ex) {}
        catch (final Exception ex2) {}
        return new Version(props.getProperty("build-id"), props.getProperty("build-version"), props.getProperty("major-version"), props.getProperty("svn-revision"));
    }
    
    private String fixNull(final String v) {
        if (v == null) {
            return "unknown";
        }
        return v;
    }
    
    @Override
    public String toString() {
        return this.BUILD_VERSION + " svn-revision#" + this.SVN_REVISION;
    }
    
    static {
        RUNTIME_VERSION = create(Version.class.getResourceAsStream("version.properties"));
    }
}
