package javax.websocket.server;

import javax.websocket.Endpoint;
import java.util.Set;

public interface ServerApplicationConfig
{
    Set<ServerEndpointConfig> getEndpointConfigs(final Set<Class<? extends Endpoint>> p0);
    
    Set<Class<?>> getAnnotatedEndpointClasses(final Set<Class<?>> p0);
}
