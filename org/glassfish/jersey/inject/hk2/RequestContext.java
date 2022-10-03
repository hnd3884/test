package org.glassfish.jersey.inject.hk2;

import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.internal.inject.ForeignDescriptor;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ActiveDescriptor;
import java.lang.annotation.Annotation;
import javax.inject.Inject;
import org.glassfish.jersey.process.internal.RequestScope;
import javax.inject.Singleton;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.hk2.api.Context;

@Singleton
public class RequestContext implements Context<RequestScoped>
{
    private final RequestScope requestScope;
    
    @Inject
    public RequestContext(final RequestScope requestScope) {
        this.requestScope = requestScope;
    }
    
    public Class<? extends Annotation> getScope() {
        return (Class<? extends Annotation>)RequestScoped.class;
    }
    
    public <U> U findOrCreate(final ActiveDescriptor<U> activeDescriptor, final ServiceHandle<?> root) {
        final Hk2RequestScope.Instance instance = (Hk2RequestScope.Instance)this.requestScope.current();
        U retVal = instance.get(ForeignDescriptor.wrap((Object)activeDescriptor));
        if (retVal == null) {
            retVal = (U)activeDescriptor.create((ServiceHandle)root);
            instance.put(ForeignDescriptor.wrap((Object)activeDescriptor, obj -> activeDescriptor.dispose(obj)), retVal);
        }
        return retVal;
    }
    
    public boolean containsKey(final ActiveDescriptor<?> descriptor) {
        final Hk2RequestScope.Instance instance = (Hk2RequestScope.Instance)this.requestScope.current();
        return instance.contains(ForeignDescriptor.wrap((Object)descriptor));
    }
    
    public boolean supportsNullCreation() {
        return true;
    }
    
    public boolean isActive() {
        return this.requestScope.isActive();
    }
    
    public void destroyOne(final ActiveDescriptor<?> descriptor) {
        final Hk2RequestScope.Instance instance = (Hk2RequestScope.Instance)this.requestScope.current();
        instance.remove(ForeignDescriptor.wrap((Object)descriptor));
    }
    
    public void shutdown() {
        this.requestScope.shutdown();
    }
    
    public static class Binder extends AbstractBinder
    {
        protected void configure() {
            this.bindAsContract((Class)RequestContext.class).to(new TypeLiteral<Context<RequestScoped>>() {}.getType()).in((Class)Singleton.class);
        }
    }
}
