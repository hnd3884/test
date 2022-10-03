package org.glassfish.jersey.server.internal.inject;

import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.ForeignDescriptor;
import org.glassfish.jersey.internal.util.collection.Cache;
import org.glassfish.jersey.server.ContainerRequest;
import java.util.function.Function;
import org.glassfish.jersey.server.model.Parameter;
import javax.inject.Provider;
import org.glassfish.jersey.internal.inject.InjectionManager;
import javax.inject.Singleton;

@Singleton
final class BeanParamValueParamProvider extends AbstractValueParamProvider
{
    private final InjectionManager injectionManager;
    
    public BeanParamValueParamProvider(final Provider<MultivaluedParameterExtractorProvider> mpep, final InjectionManager injectionManager) {
        super(mpep, new Parameter.Source[] { Parameter.Source.BEAN_PARAM });
        this.injectionManager = injectionManager;
    }
    
    public Function<ContainerRequest, ?> createValueProvider(final Parameter parameter) {
        return new BeanParamValueProvider(this.injectionManager, parameter);
    }
    
    private static final class BeanParamValueProvider implements Function<ContainerRequest, Object>
    {
        private final Parameter parameter;
        private final InjectionManager injectionManager;
        private final Cache<Class<?>, ForeignDescriptor> descriptorCache;
        
        private BeanParamValueProvider(final InjectionManager injectionManager, final Parameter parameter) {
            this.descriptorCache = (Cache<Class<?>, ForeignDescriptor>)new Cache((Function)new Function<Class<?>, ForeignDescriptor>() {
                @Override
                public ForeignDescriptor apply(final Class<?> key) {
                    return BeanParamValueProvider.this.injectionManager.createForeignDescriptor(Bindings.serviceAsContract((Class)key).in((Class)RequestScoped.class));
                }
            });
            this.injectionManager = injectionManager;
            this.parameter = parameter;
        }
        
        @Override
        public Object apply(final ContainerRequest request) {
            final Class<?> rawType = this.parameter.getRawType();
            final Object fromHk2 = this.injectionManager.getInstance((Class)rawType);
            if (fromHk2 != null) {
                return fromHk2;
            }
            final ForeignDescriptor foreignDescriptor = (ForeignDescriptor)this.descriptorCache.apply((Object)rawType);
            return this.injectionManager.getInstance(foreignDescriptor);
        }
    }
}
