package org.apache.tomcat.websocket;

import javax.naming.NamingException;
import javax.websocket.DeploymentException;
import org.apache.tomcat.InstanceManager;
import javax.websocket.Endpoint;
import org.apache.tomcat.util.res.StringManager;

public class EndpointClassHolder implements ClientEndpointHolder
{
    private static final StringManager sm;
    private final Class<? extends Endpoint> clazz;
    
    public EndpointClassHolder(final Class<? extends Endpoint> clazz) {
        this.clazz = clazz;
    }
    
    @Override
    public String getClassName() {
        return this.clazz.getName();
    }
    
    @Override
    public Endpoint getInstance(final InstanceManager instanceManager) throws DeploymentException {
        try {
            if (instanceManager == null) {
                return (Endpoint)this.clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            }
            return (Endpoint)instanceManager.newInstance((Class)this.clazz);
        }
        catch (final ReflectiveOperationException | NamingException e) {
            throw new DeploymentException(EndpointClassHolder.sm.getString("clientEndpointHolder.instanceCreationFailed"), (Throwable)e);
        }
    }
    
    static {
        sm = StringManager.getManager((Class)EndpointClassHolder.class);
    }
}
