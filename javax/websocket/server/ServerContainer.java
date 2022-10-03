package javax.websocket.server;

import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;

public interface ServerContainer extends WebSocketContainer
{
    void addEndpoint(final Class<?> p0) throws DeploymentException;
    
    void addEndpoint(final ServerEndpointConfig p0) throws DeploymentException;
}
