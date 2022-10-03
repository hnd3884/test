package org.glassfish.jersey.server;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.spi.ExternalRequestContext;
import java.util.Iterator;
import org.glassfish.jersey.internal.inject.Binder;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.logging.Level;
import java.util.Set;
import java.util.Collections;
import org.glassfish.jersey.server.spi.ComponentProvider;
import java.util.Collection;
import org.glassfish.jersey.internal.ServiceFinder;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.spi.ExternalRequestScope;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.BootstrapConfigurator;

class ExternalRequestScopeConfigurator implements BootstrapConfigurator
{
    private static final Logger LOGGER;
    private static final ExternalRequestScope<Object> NOOP_EXTERNAL_REQ_SCOPE;
    
    public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        final ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
        final Class<ExternalRequestScope>[] extScopes = ServiceFinder.find((Class)ExternalRequestScope.class, true).toClassArray();
        boolean extScopeBound = false;
        if (extScopes.length == 1) {
            for (final ComponentProvider p : (Collection)serverBag.getComponentProviders().get()) {
                if (p.bind(extScopes[0], (Set<Class<?>>)Collections.singleton(ExternalRequestScope.class))) {
                    extScopeBound = true;
                    break;
                }
            }
        }
        else if (extScopes.length > 1 && ExternalRequestScopeConfigurator.LOGGER.isLoggable(Level.WARNING)) {
            final StringBuilder scopeList = new StringBuilder("\n");
            for (final Class<ExternalRequestScope> ers : extScopes) {
                scopeList.append("   ").append(ers.getTypeParameters()[0]).append('\n');
            }
            ExternalRequestScopeConfigurator.LOGGER.warning(LocalizationMessages.WARNING_TOO_MANY_EXTERNAL_REQ_SCOPES(scopeList.toString()));
        }
        if (!extScopeBound) {
            injectionManager.register((Binder)new NoopExternalRequestScopeBinder());
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ExternalRequestScopeConfigurator.class.getName());
        NOOP_EXTERNAL_REQ_SCOPE = new ExternalRequestScope<Object>() {
            @Override
            public ExternalRequestContext<Object> open(final InjectionManager injectionManager) {
                return null;
            }
            
            @Override
            public void close() {
            }
            
            @Override
            public void suspend(final ExternalRequestContext<Object> o, final InjectionManager injectionManager) {
            }
            
            @Override
            public void resume(final ExternalRequestContext<Object> o, final InjectionManager injectionManager) {
            }
        };
    }
    
    private static class NoopExternalRequestScopeBinder extends AbstractBinder
    {
        protected void configure() {
            this.bind((Object)ExternalRequestScopeConfigurator.NOOP_EXTERNAL_REQ_SCOPE).to((Class)ExternalRequestScope.class);
        }
    }
}
