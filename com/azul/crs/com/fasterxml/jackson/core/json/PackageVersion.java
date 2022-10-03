package com.azul.crs.com.fasterxml.jackson.core.json;

import com.azul.crs.com.fasterxml.jackson.core.util.VersionUtil;
import com.azul.crs.com.fasterxml.jackson.core.Version;
import com.azul.crs.com.fasterxml.jackson.core.Versioned;

public final class PackageVersion implements Versioned
{
    public static final Version VERSION;
    
    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    static {
        VERSION = VersionUtil.parseVersion("2.12.0", "com.azul.crs.com.fasterxml.jackson.core", "jackson-core");
    }
}
