package org.glassfish.jersey.server;

import org.glassfish.jersey.server.model.Resource;
import java.util.Set;
import org.glassfish.jersey.ExtendedConfig;

public interface ServerConfig extends ExtendedConfig
{
    Set<Resource> getResources();
}
