package javax.websocket.server;

import javax.websocket.HandshakeResponse;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import java.util.ServiceLoader;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.util.Collections;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Extension;
import java.util.List;
import javax.websocket.EndpointConfig;

public interface ServerEndpointConfig extends EndpointConfig
{
    Class<?> getEndpointClass();
    
    String getPath();
    
    List<String> getSubprotocols();
    
    List<Extension> getExtensions();
    
    Configurator getConfigurator();
    
    public static final class Builder
    {
        private final Class<?> endpointClass;
        private final String path;
        private List<Class<? extends Encoder>> encoders;
        private List<Class<? extends Decoder>> decoders;
        private List<String> subprotocols;
        private List<Extension> extensions;
        private Configurator configurator;
        
        public static Builder create(final Class<?> endpointClass, final String path) {
            return new Builder(endpointClass, path);
        }
        
        private Builder(final Class<?> endpointClass, final String path) {
            this.encoders = Collections.emptyList();
            this.decoders = Collections.emptyList();
            this.subprotocols = Collections.emptyList();
            this.extensions = Collections.emptyList();
            this.configurator = Configurator.fetchContainerDefaultConfigurator();
            if (endpointClass == null) {
                throw new IllegalArgumentException("Endpoint class may not be null");
            }
            if (path == null) {
                throw new IllegalArgumentException("Path may not be null");
            }
            if (path.isEmpty()) {
                throw new IllegalArgumentException("Path may not be empty");
            }
            if (path.charAt(0) != '/') {
                throw new IllegalArgumentException("Path must start with '/'");
            }
            this.endpointClass = endpointClass;
            this.path = path;
        }
        
        public ServerEndpointConfig build() {
            return new DefaultServerEndpointConfig(this.endpointClass, this.path, this.subprotocols, this.extensions, this.encoders, this.decoders, this.configurator);
        }
        
        public Builder encoders(final List<Class<? extends Encoder>> encoders) {
            if (encoders == null || encoders.size() == 0) {
                this.encoders = Collections.emptyList();
            }
            else {
                this.encoders = Collections.unmodifiableList((List<? extends Class<? extends Encoder>>)encoders);
            }
            return this;
        }
        
        public Builder decoders(final List<Class<? extends Decoder>> decoders) {
            if (decoders == null || decoders.size() == 0) {
                this.decoders = Collections.emptyList();
            }
            else {
                this.decoders = Collections.unmodifiableList((List<? extends Class<? extends Decoder>>)decoders);
            }
            return this;
        }
        
        public Builder subprotocols(final List<String> subprotocols) {
            if (subprotocols == null || subprotocols.size() == 0) {
                this.subprotocols = Collections.emptyList();
            }
            else {
                this.subprotocols = Collections.unmodifiableList((List<? extends String>)subprotocols);
            }
            return this;
        }
        
        public Builder extensions(final List<Extension> extensions) {
            if (extensions == null || extensions.size() == 0) {
                this.extensions = Collections.emptyList();
            }
            else {
                this.extensions = Collections.unmodifiableList((List<? extends Extension>)extensions);
            }
            return this;
        }
        
        public Builder configurator(final Configurator serverEndpointConfigurator) {
            if (serverEndpointConfigurator == null) {
                this.configurator = Configurator.fetchContainerDefaultConfigurator();
            }
            else {
                this.configurator = serverEndpointConfigurator;
            }
            return this;
        }
    }
    
    public static class Configurator
    {
        private static volatile Configurator defaultImpl;
        private static final Object defaultImplLock;
        private static final String DEFAULT_IMPL_CLASSNAME = "org.apache.tomcat.websocket.server.DefaultServerEndpointConfigurator";
        
        static Configurator fetchContainerDefaultConfigurator() {
            if (Configurator.defaultImpl == null) {
                synchronized (Configurator.defaultImplLock) {
                    if (Configurator.defaultImpl == null) {
                        if (System.getSecurityManager() == null) {
                            Configurator.defaultImpl = loadDefault();
                        }
                        else {
                            Configurator.defaultImpl = AccessController.doPrivileged((PrivilegedAction<Configurator>)new PrivilegedLoadDefault());
                        }
                    }
                }
            }
            return Configurator.defaultImpl;
        }
        
        private static Configurator loadDefault() {
            Configurator result = null;
            final ServiceLoader<Configurator> serviceLoader = ServiceLoader.load(Configurator.class);
            for (Iterator<Configurator> iter = serviceLoader.iterator(); result == null && iter.hasNext(); result = iter.next()) {}
            if (result == null) {
                try {
                    final Class<Configurator> clazz = (Class<Configurator>)Class.forName("org.apache.tomcat.websocket.server.DefaultServerEndpointConfigurator");
                    result = clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                }
                catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {}
            }
            return result;
        }
        
        public String getNegotiatedSubprotocol(final List<String> supported, final List<String> requested) {
            return fetchContainerDefaultConfigurator().getNegotiatedSubprotocol(supported, requested);
        }
        
        public List<Extension> getNegotiatedExtensions(final List<Extension> installed, final List<Extension> requested) {
            return fetchContainerDefaultConfigurator().getNegotiatedExtensions(installed, requested);
        }
        
        public boolean checkOrigin(final String originHeaderValue) {
            return fetchContainerDefaultConfigurator().checkOrigin(originHeaderValue);
        }
        
        public void modifyHandshake(final ServerEndpointConfig sec, final HandshakeRequest request, final HandshakeResponse response) {
            fetchContainerDefaultConfigurator().modifyHandshake(sec, request, response);
        }
        
        public <T> T getEndpointInstance(final Class<T> clazz) throws InstantiationException {
            return (T)fetchContainerDefaultConfigurator().getEndpointInstance((Class<Object>)clazz);
        }
        
        static {
            Configurator.defaultImpl = null;
            defaultImplLock = new Object();
        }
        
        private static class PrivilegedLoadDefault implements PrivilegedAction<Configurator>
        {
            @Override
            public Configurator run() {
                return loadDefault();
            }
        }
    }
}
