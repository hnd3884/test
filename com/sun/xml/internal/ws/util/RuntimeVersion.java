package com.sun.xml.internal.ws.util;

import java.io.InputStream;
import java.io.IOException;

public final class RuntimeVersion
{
    public static final Version VERSION;
    
    public String getVersion() {
        return RuntimeVersion.VERSION.toString();
    }
    
    static {
        Version version = null;
        final InputStream in = RuntimeVersion.class.getResourceAsStream("version.properties");
        try {
            version = Version.create(in);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (final IOException ex) {}
            }
        }
        VERSION = ((version == null) ? Version.create(null) : version);
    }
}
