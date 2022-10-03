package org.glassfish.jersey.server;

import org.glassfish.jersey.spi.ThreadPoolExecutorProvider;
import org.glassfish.jersey.spi.ScheduledThreadPoolExecutorProvider;
import org.glassfish.jersey.model.internal.ManagedObjectsFinalizer;
import org.glassfish.jersey.model.internal.ComponentBag;
import org.glassfish.jersey.spi.ExecutorServiceProvider;
import org.glassfish.jersey.internal.inject.Binding;
import java.lang.annotation.Annotation;
import org.glassfish.jersey.spi.ScheduledExecutorServiceProvider;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.process.internal.AbstractExecutorProvidersConfigurator;

class ServerExecutorProvidersConfigurator extends AbstractExecutorProvidersConfigurator
{
    public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        final ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
        final ResourceConfig runtimeConfig = serverBag.getRuntimeConfig();
        final ComponentBag componentBag = runtimeConfig.getComponentBag();
        final ManagedObjectsFinalizer finalizer = serverBag.getManagedObjectsFinalizer();
        final ScheduledExecutorServiceProvider defaultScheduledExecutorProvider = (ScheduledExecutorServiceProvider)new DefaultBackgroundSchedulerProvider();
        final InstanceBinding<ScheduledExecutorServiceProvider> schedulerBinding = (InstanceBinding<ScheduledExecutorServiceProvider>)((InstanceBinding)Bindings.service((Object)defaultScheduledExecutorProvider).to((Class)ScheduledExecutorServiceProvider.class)).qualifiedBy((Annotation)BackgroundSchedulerLiteral.INSTANCE);
        injectionManager.register((Binding)schedulerBinding);
        finalizer.registerForPreDestroyCall((Object)defaultScheduledExecutorProvider);
        final ExecutorServiceProvider defaultAsyncExecutorProvider = (ExecutorServiceProvider)new DefaultManagedAsyncExecutorProvider();
        final InstanceBinding<ExecutorServiceProvider> executorBinding = (InstanceBinding<ExecutorServiceProvider>)Bindings.service((Object)defaultAsyncExecutorProvider).to((Class)ExecutorServiceProvider.class);
        injectionManager.register((Binding)executorBinding);
        finalizer.registerForPreDestroyCall((Object)defaultAsyncExecutorProvider);
        this.registerExecutors(injectionManager, componentBag, defaultAsyncExecutorProvider, defaultScheduledExecutorProvider);
    }
    
    @BackgroundScheduler
    private static class DefaultBackgroundSchedulerProvider extends ScheduledThreadPoolExecutorProvider
    {
        public DefaultBackgroundSchedulerProvider() {
            super("jersey-background-task-scheduler");
        }
        
        protected int getCorePoolSize() {
            return 1;
        }
    }
    
    @ManagedAsyncExecutor
    private static class DefaultManagedAsyncExecutorProvider extends ThreadPoolExecutorProvider
    {
        public DefaultManagedAsyncExecutorProvider() {
            super("jersey-server-managed-async-executor");
        }
    }
}
