package org.glassfish.jersey.server.internal.inject;

import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InjecteeImpl;
import org.glassfish.jersey.internal.inject.Injectee;
import org.glassfish.jersey.server.ContainerRequest;
import java.lang.reflect.ParameterizedType;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.internal.util.collection.Cache;
import org.glassfish.jersey.internal.inject.ForeignDescriptor;
import org.glassfish.jersey.internal.inject.Binding;
import java.util.function.Function;
import org.glassfish.jersey.internal.inject.ContextInjectionResolver;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import javax.inject.Singleton;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

@Singleton
class DelegatedInjectionValueParamProvider implements ValueParamProvider
{
    private final LazyValue<ContextInjectionResolver> resolver;
    private final Function<Binding, ForeignDescriptor> foreignDescriptorFactory;
    private final Cache<Parameter, ForeignDescriptor> descriptorCache;
    
    public DelegatedInjectionValueParamProvider(final LazyValue<ContextInjectionResolver> resolver, final Function<Binding, ForeignDescriptor> foreignDescriptorFactory) {
        this.descriptorCache = (Cache<Parameter, ForeignDescriptor>)new Cache(parameter -> {
            final Class<?> rawType = parameter.getRawType();
            if (rawType.isInterface() && !(parameter.getType() instanceof ParameterizedType)) {
                return this.createDescriptor(rawType);
            }
            else {
                return null;
            }
        });
        this.resolver = resolver;
        this.foreignDescriptorFactory = foreignDescriptorFactory;
    }
    
    @Override
    public Function<ContainerRequest, ?> getValueProvider(final Parameter parameter) {
        final Parameter.Source paramSource = parameter.getSource();
        if (paramSource == Parameter.Source.CONTEXT) {
            return (Function<ContainerRequest, ?>)(containerRequest -> ((ContextInjectionResolver)this.resolver.get()).resolve(this.getInjectee(parameter)));
        }
        return null;
    }
    
    @Override
    public PriorityType getPriority() {
        return Priority.LOW;
    }
    
    private Injectee getInjectee(final Parameter parameter) {
        final InjecteeImpl injectee = new InjecteeImpl();
        injectee.setRequiredType(parameter.getType());
        injectee.setInjecteeClass((Class)parameter.getRawType());
        final ForeignDescriptor proxyDescriptor = (ForeignDescriptor)this.descriptorCache.apply((Object)parameter);
        if (proxyDescriptor != null) {
            injectee.setInjecteeDescriptor(proxyDescriptor);
        }
        return (Injectee)injectee;
    }
    
    private ForeignDescriptor createDescriptor(final Class<?> clazz) {
        return this.foreignDescriptorFactory.apply(Bindings.serviceAsContract((Class)clazz).in((Class)RequestScoped.class));
    }
}
