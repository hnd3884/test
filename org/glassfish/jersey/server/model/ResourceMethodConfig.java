package org.glassfish.jersey.server.model;

import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ContainerRequestFilter;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.model.ContractProvider;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.model.internal.ComponentBag;
import javax.ws.rs.RuntimeType;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.glassfish.jersey.model.internal.CommonConfig;

class ResourceMethodConfig extends CommonConfig
{
    private static final Logger LOGGER;
    private static final Set<Class<?>> allowedContracts;
    
    ResourceMethodConfig(final Map<String, Object> properties) {
        super(RuntimeType.SERVER, ComponentBag.EXCLUDE_EMPTY);
        this.setProperties((Map)properties);
    }
    
    protected Inflector<ContractProvider.Builder, ContractProvider> getModelEnhancer(final Class<?> providerClass) {
        return (Inflector<ContractProvider.Builder, ContractProvider>)new Inflector<ContractProvider.Builder, ContractProvider>() {
            public ContractProvider apply(final ContractProvider.Builder builder) {
                final Iterator<Class<?>> it = builder.getContracts().keySet().iterator();
                while (it.hasNext()) {
                    final Class<?> contract = it.next();
                    if (!ResourceMethodConfig.allowedContracts.contains(contract)) {
                        ResourceMethodConfig.LOGGER.warning(LocalizationMessages.CONTRACT_CANNOT_BE_BOUND_TO_RESOURCE_METHOD(contract, providerClass));
                        it.remove();
                    }
                }
                return builder.build();
            }
        };
    }
    
    static {
        LOGGER = Logger.getLogger(ResourceMethodConfig.class.getName());
        final Set<Class<?>> tempSet = Collections.newSetFromMap(new IdentityHashMap<Class<?>, Boolean>());
        tempSet.add(ContainerRequestFilter.class);
        tempSet.add(ContainerResponseFilter.class);
        tempSet.add(ReaderInterceptor.class);
        tempSet.add(WriterInterceptor.class);
        allowedContracts = Collections.unmodifiableSet((Set<? extends Class<?>>)tempSet);
    }
}
