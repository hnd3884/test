package org.apache.tomcat.websocket;

import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import org.apache.tomcat.InstanceManager;

public interface ClientEndpointHolder
{
    String getClassName();
    
    Endpoint getInstance(final InstanceManager p0) throws DeploymentException;
}
