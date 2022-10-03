package org.glassfish.jersey.inject.hk2;

import javax.inject.Singleton;
import org.glassfish.jersey.process.internal.RequestScope;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class Hk2BootstrapBinder extends AbstractBinder
{
    private final ServiceLocator serviceLocator;
    
    Hk2BootstrapBinder(final ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
    }
    
    protected void configure() {
        this.install(new Binder[] { (Binder)new JerseyClassAnalyzer.Binder(this.serviceLocator), (Binder)new RequestContext.Binder(), (Binder)new ContextInjectionResolverImpl.Binder(), (Binder)new JerseyErrorService.Binder() });
        this.bind((Class)Hk2RequestScope.class).to((Class)RequestScope.class).in((Class)Singleton.class);
    }
}
