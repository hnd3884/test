package org.glassfish.hk2.utilities.binding;

import java.lang.annotation.Annotation;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.DynamicConfiguration;

public class BindingBuilderFactory
{
    public static void addBinding(final BindingBuilder<?> builder, final DynamicConfiguration configuration) {
        if (builder instanceof AbstractBindingBuilder) {
            ((AbstractBindingBuilder)builder).complete(configuration, null);
            return;
        }
        throw new IllegalArgumentException("Unknown binding builder type: " + builder.getClass().getName());
    }
    
    public static void addBinding(final BindingBuilder<?> builder, final DynamicConfiguration configuration, final HK2Loader defaultLoader) {
        if (builder instanceof AbstractBindingBuilder) {
            ((AbstractBindingBuilder)builder).complete(configuration, defaultLoader);
            return;
        }
        throw new IllegalArgumentException("Unknown binding builder type: " + builder.getClass().getName());
    }
    
    public static <T> ServiceBindingBuilder<T> newFactoryBinder(final Class<? extends Factory<T>> factoryType, final Class<? extends Annotation> factoryScope) {
        return (ServiceBindingBuilder<T>)AbstractBindingBuilder.createFactoryBinder((Class<? extends Factory<Object>>)factoryType, factoryScope);
    }
    
    public static <T> ServiceBindingBuilder<T> newFactoryBinder(final Class<? extends Factory<T>> factoryType) {
        return (ServiceBindingBuilder<T>)AbstractBindingBuilder.createFactoryBinder((Class<? extends Factory<Object>>)factoryType, (Class<? extends Annotation>)null);
    }
    
    public static <T> ServiceBindingBuilder<T> newFactoryBinder(final Factory<T> factory) {
        return AbstractBindingBuilder.createFactoryBinder(factory);
    }
    
    public static <T> ServiceBindingBuilder<T> newBinder(final Class<T> serviceType) {
        return AbstractBindingBuilder.create(serviceType, false);
    }
    
    public static <T> ScopedBindingBuilder<T> newBinder(final T service) {
        return AbstractBindingBuilder.create(service);
    }
}
