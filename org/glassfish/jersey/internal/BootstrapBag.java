package org.glassfish.jersey.internal;

import java.util.Objects;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;
import java.util.List;
import org.glassfish.jersey.model.internal.ManagedObjectsFinalizer;
import org.glassfish.jersey.spi.ContextResolvers;
import org.glassfish.jersey.spi.ExceptionMappers;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.process.internal.RequestScope;
import javax.ws.rs.core.Configuration;

public class BootstrapBag
{
    private Configuration configuration;
    private RequestScope requestScope;
    private MessageBodyWorkers messageBodyWorkers;
    private ExceptionMappers exceptionMappers;
    private ContextResolvers contextResolvers;
    private ManagedObjectsFinalizer managedObjectsFinalizer;
    private List<AutoDiscoverable> autoDiscoverables;
    
    public List<AutoDiscoverable> getAutoDiscoverables() {
        return this.autoDiscoverables;
    }
    
    public void setAutoDiscoverables(final List<AutoDiscoverable> autoDiscoverables) {
        this.autoDiscoverables = autoDiscoverables;
    }
    
    public ManagedObjectsFinalizer getManagedObjectsFinalizer() {
        return this.managedObjectsFinalizer;
    }
    
    public void setManagedObjectsFinalizer(final ManagedObjectsFinalizer managedObjectsFinalizer) {
        this.managedObjectsFinalizer = managedObjectsFinalizer;
    }
    
    public RequestScope getRequestScope() {
        requireNonNull(this.requestScope, RequestScope.class);
        return this.requestScope;
    }
    
    public void setRequestScope(final RequestScope requestScope) {
        this.requestScope = requestScope;
    }
    
    public MessageBodyWorkers getMessageBodyWorkers() {
        requireNonNull(this.messageBodyWorkers, MessageBodyWorkers.class);
        return this.messageBodyWorkers;
    }
    
    public void setMessageBodyWorkers(final MessageBodyWorkers messageBodyWorkers) {
        this.messageBodyWorkers = messageBodyWorkers;
    }
    
    public Configuration getConfiguration() {
        requireNonNull(this.configuration, Configuration.class);
        return this.configuration;
    }
    
    public void setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
    }
    
    public ExceptionMappers getExceptionMappers() {
        requireNonNull(this.exceptionMappers, ExceptionMappers.class);
        return this.exceptionMappers;
    }
    
    public void setExceptionMappers(final ExceptionMappers exceptionMappers) {
        this.exceptionMappers = exceptionMappers;
    }
    
    public ContextResolvers getContextResolvers() {
        requireNonNull(this.contextResolvers, ContextResolvers.class);
        return this.contextResolvers;
    }
    
    public void setContextResolvers(final ContextResolvers contextResolvers) {
        this.contextResolvers = contextResolvers;
    }
    
    protected static void requireNonNull(final Object object, final Type type) {
        Objects.requireNonNull(object, type + " has not been added into BootstrapBag yet");
    }
}
