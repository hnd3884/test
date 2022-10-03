package org.apache.tomcat.websocket.pojo;

import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import org.apache.tomcat.InstanceManager;
import javax.websocket.DeploymentException;
import java.util.Collections;
import javax.websocket.Decoder;
import java.util.List;

public class PojoEndpointClient extends PojoEndpointBase
{
    @Deprecated
    public PojoEndpointClient(final Object pojo, final List<Class<? extends Decoder>> decoders) throws DeploymentException {
        super(Collections.emptyMap());
        this.setPojo(pojo);
        this.setMethodMapping(new PojoMethodMapping(pojo.getClass(), decoders, null));
    }
    
    public PojoEndpointClient(final Object pojo, final List<Class<? extends Decoder>> decoders, final InstanceManager instanceManager) throws DeploymentException {
        super(Collections.emptyMap());
        this.setPojo(pojo);
        this.setMethodMapping(new PojoMethodMapping(pojo.getClass(), decoders, null, instanceManager));
    }
    
    public void onOpen(final Session session, final EndpointConfig config) {
        this.doOnOpen(session, config);
    }
}
