package org.apache.tomcat.websocket;

import javax.naming.NamingException;
import javax.websocket.DeploymentException;
import javax.websocket.Decoder;
import java.util.List;
import org.apache.tomcat.websocket.pojo.PojoEndpointClient;
import javax.websocket.Endpoint;
import org.apache.tomcat.InstanceManager;
import javax.websocket.ClientEndpointConfig;
import org.apache.tomcat.util.res.StringManager;

public class PojoClassHolder implements ClientEndpointHolder
{
    private static final StringManager sm;
    private final Class<?> pojoClazz;
    private final ClientEndpointConfig clientEndpointConfig;
    
    public PojoClassHolder(final Class<?> pojoClazz, final ClientEndpointConfig clientEndpointConfig) {
        this.pojoClazz = pojoClazz;
        this.clientEndpointConfig = clientEndpointConfig;
    }
    
    @Override
    public String getClassName() {
        return this.pojoClazz.getName();
    }
    
    @Override
    public Endpoint getInstance(final InstanceManager instanceManager) throws DeploymentException {
        try {
            Object pojo;
            if (instanceManager == null) {
                pojo = this.pojoClazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            }
            else {
                pojo = instanceManager.newInstance((Class)this.pojoClazz);
            }
            return new PojoEndpointClient(pojo, this.clientEndpointConfig.getDecoders(), instanceManager);
        }
        catch (final ReflectiveOperationException | SecurityException | NamingException e) {
            throw new DeploymentException(PojoClassHolder.sm.getString("clientEndpointHolder.instanceCreationFailed"), (Throwable)e);
        }
    }
    
    static {
        sm = StringManager.getManager((Class)PojoClassHolder.class);
    }
}
