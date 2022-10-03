package javax.ws.rs.sse;

import java.net.URL;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.WebTarget;
import java.util.function.Consumer;

public interface SseEventSource extends AutoCloseable
{
    void register(final Consumer<InboundSseEvent> p0);
    
    void register(final Consumer<InboundSseEvent> p0, final Consumer<Throwable> p1);
    
    void register(final Consumer<InboundSseEvent> p0, final Consumer<Throwable> p1, final Runnable p2);
    
    default Builder target(final WebTarget endpoint) {
        return Builder.newBuilder().target(endpoint);
    }
    
    void open();
    
    boolean isOpen();
    
    default void close() {
        this.close(5L, TimeUnit.SECONDS);
    }
    
    boolean close(final long p0, final TimeUnit p1);
    
    public abstract static class Builder
    {
        public static final String JAXRS_DEFAULT_SSE_BUILDER_PROPERTY = "javax.ws.rs.sse.SseEventSource.Builder";
        private static final String JAXRS_DEFAULT_SSE_BUILDER = "org.glassfish.jersey.media.sse.internal.JerseySseEventSource$Builder";
        
        protected Builder() {
        }
        
        static Builder newBuilder() {
            try {
                final Object delegate = FactoryFinder.find("javax.ws.rs.sse.SseEventSource.Builder", "org.glassfish.jersey.media.sse.internal.JerseySseEventSource$Builder", Builder.class);
                if (!(delegate instanceof Builder)) {
                    final Class pClass = Builder.class;
                    final String classnameAsResource = pClass.getName().replace('.', '/') + ".class";
                    ClassLoader loader = pClass.getClassLoader();
                    if (loader == null) {
                        loader = ClassLoader.getSystemClassLoader();
                    }
                    final URL targetTypeURL = loader.getResource(classnameAsResource);
                    throw new LinkageError("ClassCastException: attempting to cast" + delegate.getClass().getClassLoader().getResource(classnameAsResource) + " to " + targetTypeURL);
                }
                return (Builder)delegate;
            }
            catch (final Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        
        protected abstract Builder target(final WebTarget p0);
        
        public abstract Builder reconnectingEvery(final long p0, final TimeUnit p1);
        
        public abstract SseEventSource build();
    }
}
