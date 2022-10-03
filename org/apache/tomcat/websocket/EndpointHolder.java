package org.apache.tomcat.websocket;

import javax.naming.NamingException;
import javax.websocket.DeploymentException;
import org.apache.tomcat.InstanceManager;
import javax.websocket.Endpoint;
import org.apache.tomcat.util.res.StringManager;

public class EndpointHolder implements ClientEndpointHolder
{
    private static final StringManager sm;
    private final Endpoint endpoint;
    
    public EndpointHolder(final Endpoint endpoint) {
        this.endpoint = endpoint;
    }
    
    @Override
    public String getClassName() {
        return this.endpoint.getClass().getName();
    }
    
    @Override
    public Endpoint getInstance(final InstanceManager instanceManager) throws DeploymentException {
        if (instanceManager != null) {
            try {
                instanceManager.newInstance((Object)this.endpoint);
            }
            catch (final ReflectiveOperationException | NamingException e) {
                throw new DeploymentException(EndpointHolder.sm.getString("clientEndpointHolder.instanceRegistrationFailed"), (Throwable)e);
            }
        }
        return this.endpoint;
    }
    
    static {
        sm = StringManager.getManager((Class)EndpointHolder.class);
    }
}
