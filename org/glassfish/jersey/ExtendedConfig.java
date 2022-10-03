package org.glassfish.jersey;

import javax.ws.rs.core.Configuration;

public interface ExtendedConfig extends Configuration
{
    boolean isProperty(final String p0);
}
