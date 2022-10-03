package org.glassfish.jersey.server;

import java.util.Iterator;
import java.lang.annotation.Annotation;
import javax.inject.Singleton;
import java.util.Collections;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.server.spi.ComponentProvider;
import java.util.Collection;
import org.glassfish.jersey.internal.util.collection.Value;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.internal.BootstrapConfigurator;

class ApplicationConfigurator implements BootstrapConfigurator
{
    private Application application;
    private Class<? extends Application> applicationClass;
    
    ApplicationConfigurator(final Application application) {
        this.application = application;
    }
    
    ApplicationConfigurator(final Class<? extends Application> applicationClass) {
        this.applicationClass = applicationClass;
    }
    
    public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        final ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
        Application resultApplication;
        if (this.application != null) {
            if (this.application instanceof ResourceConfig) {
                final ResourceConfig rc = (ResourceConfig)this.application;
                if (rc.getApplicationClass() != null) {
                    rc.setApplication(createApplication(injectionManager, rc.getApplicationClass(), (Value<Collection<ComponentProvider>>)serverBag.getComponentProviders()));
                }
            }
            resultApplication = this.application;
        }
        else {
            resultApplication = createApplication(injectionManager, this.applicationClass, (Value<Collection<ComponentProvider>>)serverBag.getComponentProviders());
        }
        serverBag.setApplication(resultApplication);
        injectionManager.register(Bindings.service((Object)resultApplication).to((Class)Application.class));
    }
    
    private static Application createApplication(final InjectionManager injectionManager, final Class<? extends Application> applicationClass, final Value<Collection<ComponentProvider>> componentProvidersValue) {
        if (applicationClass == ResourceConfig.class) {
            return new ResourceConfig();
        }
        if (applicationClass == Application.class) {
            return new Application();
        }
        final Collection<ComponentProvider> componentProviders = (Collection<ComponentProvider>)componentProvidersValue.get();
        boolean appClassBound = false;
        for (final ComponentProvider cp : componentProviders) {
            if (cp.bind(applicationClass, Collections.emptySet())) {
                appClassBound = true;
                break;
            }
        }
        if (!appClassBound && applicationClass.isAnnotationPresent((Class<? extends Annotation>)Singleton.class)) {
            injectionManager.register(Bindings.serviceAsContract((Class)applicationClass).in((Class)Singleton.class));
            appClassBound = true;
        }
        final Application app = (Application)(appClassBound ? injectionManager.getInstance((Class)applicationClass) : ((Application)injectionManager.createAndInitialize((Class)applicationClass)));
        if (app instanceof ResourceConfig) {
            final ResourceConfig _rc = (ResourceConfig)app;
            final Class<? extends Application> innerAppClass = _rc.getApplicationClass();
            if (innerAppClass != null) {
                final Application innerApp = createApplication(injectionManager, innerAppClass, componentProvidersValue);
                _rc.setApplication(innerApp);
            }
        }
        return app;
    }
}
