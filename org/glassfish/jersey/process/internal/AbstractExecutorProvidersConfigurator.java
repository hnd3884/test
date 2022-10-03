package org.glassfish.jersey.process.internal;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.List;
import org.glassfish.jersey.model.internal.ComponentBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.spi.ScheduledExecutorServiceProvider;
import org.glassfish.jersey.spi.ExecutorServiceProvider;
import java.util.function.Function;
import org.glassfish.jersey.internal.BootstrapConfigurator;

public abstract class AbstractExecutorProvidersConfigurator implements BootstrapConfigurator
{
    private static final Function<Object, ExecutorServiceProvider> CAST_TO_EXECUTOR_PROVIDER;
    private static final Function<Object, ScheduledExecutorServiceProvider> CAST_TO_SCHEDULED_EXECUTOR_PROVIDER;
    
    protected void registerExecutors(final InjectionManager injectionManager, final ComponentBag componentBag, final ExecutorServiceProvider defaultAsyncExecutorProvider, final ScheduledExecutorServiceProvider defaultScheduledExecutorProvider) {
        final List<ExecutorServiceProvider> customExecutors = Stream.concat(componentBag.getClasses(ComponentBag.EXECUTOR_SERVICE_PROVIDER_ONLY).stream().map((Function<? super Object, ?>)injectionManager::createAndInitialize), componentBag.getInstances(ComponentBag.EXECUTOR_SERVICE_PROVIDER_ONLY).stream()).map((Function<? super Object, ?>)AbstractExecutorProvidersConfigurator.CAST_TO_EXECUTOR_PROVIDER).collect((Collector<? super Object, ?, List<ExecutorServiceProvider>>)Collectors.toList());
        customExecutors.add(defaultAsyncExecutorProvider);
        final List<ScheduledExecutorServiceProvider> customScheduledExecutors = Stream.concat(componentBag.getClasses(ComponentBag.SCHEDULED_EXECUTOR_SERVICE_PROVIDER_ONLY).stream().map((Function<? super Object, ?>)injectionManager::createAndInitialize), componentBag.getInstances(ComponentBag.SCHEDULED_EXECUTOR_SERVICE_PROVIDER_ONLY).stream()).map((Function<? super Object, ?>)AbstractExecutorProvidersConfigurator.CAST_TO_SCHEDULED_EXECUTOR_PROVIDER).collect((Collector<? super Object, ?, List<ScheduledExecutorServiceProvider>>)Collectors.toList());
        customScheduledExecutors.add(defaultScheduledExecutorProvider);
        ExecutorProviders.registerExecutorBindings(injectionManager, customExecutors, customScheduledExecutors);
    }
    
    static {
        CAST_TO_EXECUTOR_PROVIDER = ExecutorServiceProvider.class::cast;
        CAST_TO_SCHEDULED_EXECUTOR_PROVIDER = ScheduledExecutorServiceProvider.class::cast;
    }
}
