package org.apache.tomcat.websocket;

import javax.websocket.Decoder;
import java.util.List;
import org.apache.tomcat.websocket.pojo.PojoEndpointClient;
import javax.naming.NamingException;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import org.apache.tomcat.InstanceManager;
import javax.websocket.ClientEndpointConfig;
import org.apache.tomcat.util.res.StringManager;

public class PojoHolder implements ClientEndpointHolder
{
    private static final StringManager sm;
    private final Object pojo;
    private final ClientEndpointConfig clientEndpointConfig;
    
    public PojoHolder(final Object pojo, final ClientEndpointConfig clientEndpointConfig) {
        this.pojo = pojo;
        this.clientEndpointConfig = clientEndpointConfig;
    }
    
    @Override
    public String getClassName() {
        return this.pojo.getClass().getName();
    }
    
    @Override
    public Endpoint getInstance(final InstanceManager instanceManager) throws DeploymentException {
        if (instanceManager != null) {
            try {
                instanceManager.newInstance(this.pojo);
            }
            catch (final ReflectiveOperationException | NamingException e) {
                throw new DeploymentException(PojoHolder.sm.getString("clientEndpointHolder.instanceRegistrationFailed"), (Throwable)e);
            }
        }
        return new PojoEndpointClient(this.pojo, this.clientEndpointConfig.getDecoders(), instanceManager);
    }
    
    static {
        sm = StringManager.getManager((Class)PojoHolder.class);
    }
}
