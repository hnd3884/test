package org.glassfish.jersey.jackson.internal.jackson.jaxrs.json;

import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;

public final class PackageVersion implements Versioned
{
    public static final Version VERSION;
    
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    static {
        VERSION = VersionUtil.parseVersion("2.8.4", "com.fasterxml.jackson.jaxrs", "jackson-jaxrs-json-provider");
    }
}
