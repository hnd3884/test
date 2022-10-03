package org.apache.tomcat.websocket.pojo;

import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.util.Map;

public class PojoEndpointServer extends PojoEndpointBase
{
    public PojoEndpointServer(final Map<String, String> pathParameters, final Object pojo) {
        super(pathParameters);
        this.setPojo(pojo);
    }
    
    public void onOpen(final Session session, final EndpointConfig endpointConfig) {
        final ServerEndpointConfig sec = (ServerEndpointConfig)endpointConfig;
        final PojoMethodMapping methodMapping = sec.getUserProperties().get("org.apache.tomcat.websocket.pojo.PojoEndpoint.methodMapping");
        this.setMethodMapping(methodMapping);
        this.doOnOpen(session, endpointConfig);
    }
}
