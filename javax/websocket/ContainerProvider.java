package javax.websocket;

import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import java.util.ServiceLoader;

public abstract class ContainerProvider
{
    private static final String DEFAULT_PROVIDER_CLASS_NAME = "org.apache.tomcat.websocket.WsWebSocketContainer";
    
    public static WebSocketContainer getWebSocketContainer() {
        WebSocketContainer result = null;
        final ServiceLoader<ContainerProvider> serviceLoader = ServiceLoader.load(ContainerProvider.class);
        for (Iterator<ContainerProvider> iter = serviceLoader.iterator(); result == null && iter.hasNext(); result = iter.next().getContainer()) {}
        if (result == null) {
            try {
                final Class<WebSocketContainer> clazz = (Class<WebSocketContainer>)Class.forName("org.apache.tomcat.websocket.WsWebSocketContainer");
                result = clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            }
            catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {}
        }
        return result;
    }
    
    protected abstract WebSocketContainer getContainer();
}
