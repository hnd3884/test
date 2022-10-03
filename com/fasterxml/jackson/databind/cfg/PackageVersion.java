package com.fasterxml.jackson.databind.cfg;

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
        VERSION = VersionUtil.parseVersion("2.11.1", "com.fasterxml.jackson.core", "jackson-databind");
    }
}
