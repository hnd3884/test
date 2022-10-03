package org.glassfish.jersey.client;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.security.PrivilegedAction;
import java.security.AccessController;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.model.internal.ManagedObjectsFinalizer;
import org.glassfish.jersey.spi.ScheduledExecutorServiceProvider;
import org.glassfish.jersey.internal.util.collection.Value;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.spi.ExecutorServiceProvider;
import org.glassfish.jersey.internal.inject.Binding;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import java.util.Map;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import java.util.concurrent.ScheduledExecutorService;
import org.glassfish.jersey.model.internal.ComponentBag;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import org.glassfish.jersey.process.internal.AbstractExecutorProvidersConfigurator;

class ClientExecutorProvidersConfigurator extends AbstractExecutorProvidersConfigurator
{
    private static final Logger LOGGER;
    private static final ExecutorService MANAGED_EXECUTOR_SERVICE;
    private final ComponentBag componentBag;
    private final JerseyClient client;
    private final ExecutorService customExecutorService;
    private final ScheduledExecutorService customScheduledExecutorService;
    
    ClientExecutorProvidersConfigurator(final ComponentBag componentBag, final JerseyClient client, final ExecutorService customExecutorService, final ScheduledExecutorService customScheduledExecutorService) {
        this.componentBag = componentBag;
        this.client = client;
        this.customExecutorService = customExecutorService;
        this.customScheduledExecutorService = customScheduledExecutorService;
    }
    
    public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        final Map<String, Object> runtimeProperties = bootstrapBag.getConfiguration().getProperties();
        final ManagedObjectsFinalizer finalizer = bootstrapBag.getManagedObjectsFinalizer();
        final ExecutorService clientExecutorService = (this.client.getExecutorService() == null) ? this.customExecutorService : this.client.getExecutorService();
        ExecutorServiceProvider defaultAsyncExecutorProvider;
        if (clientExecutorService != null) {
            defaultAsyncExecutorProvider = (ExecutorServiceProvider)new ClientExecutorServiceProvider(clientExecutorService);
        }
        else {
            Integer asyncThreadPoolSize = ClientProperties.getValue((Map<String, ?>)runtimeProperties, "jersey.config.client.async.threadPoolSize", Integer.class);
            if (asyncThreadPoolSize != null) {
                asyncThreadPoolSize = ((asyncThreadPoolSize < 0) ? 0 : asyncThreadPoolSize);
                final InstanceBinding<Integer> asyncThreadPoolSizeBinding = (InstanceBinding<Integer>)Bindings.service((Object)asyncThreadPoolSize).named("ClientAsyncThreadPoolSize");
                injectionManager.register((Binding)asyncThreadPoolSizeBinding);
                defaultAsyncExecutorProvider = (ExecutorServiceProvider)new DefaultClientAsyncExecutorProvider(asyncThreadPoolSize);
            }
            else if (ClientExecutorProvidersConfigurator.MANAGED_EXECUTOR_SERVICE != null) {
                defaultAsyncExecutorProvider = (ExecutorServiceProvider)new ClientExecutorServiceProvider(ClientExecutorProvidersConfigurator.MANAGED_EXECUTOR_SERVICE);
            }
            else {
                defaultAsyncExecutorProvider = (ExecutorServiceProvider)new DefaultClientAsyncExecutorProvider(0);
            }
        }
        final InstanceBinding<ExecutorServiceProvider> executorBinding = (InstanceBinding<ExecutorServiceProvider>)Bindings.service((Object)defaultAsyncExecutorProvider).to((Class)ExecutorServiceProvider.class);
        injectionManager.register((Binding)executorBinding);
        finalizer.registerForPreDestroyCall((Object)defaultAsyncExecutorProvider);
        final ScheduledExecutorService clientScheduledExecutorService = (this.client.getScheduledExecutorService() == null) ? this.customScheduledExecutorService : this.client.getScheduledExecutorService();
        ScheduledExecutorServiceProvider defaultScheduledExecutorProvider;
        if (clientScheduledExecutorService != null) {
            defaultScheduledExecutorProvider = (ScheduledExecutorServiceProvider)new ClientScheduledExecutorServiceProvider((Value<ScheduledExecutorService>)Values.of((Object)clientScheduledExecutorService));
        }
        else {
            final ScheduledExecutorService scheduledExecutorService = this.lookupManagedScheduledExecutorService();
            defaultScheduledExecutorProvider = (ScheduledExecutorServiceProvider)((scheduledExecutorService == null) ? new DefaultClientBackgroundSchedulerProvider() : new ClientScheduledExecutorServiceProvider((Value<ScheduledExecutorService>)Values.of((Object)scheduledExecutorService)));
        }
        final InstanceBinding<ScheduledExecutorServiceProvider> schedulerBinding = (InstanceBinding<ScheduledExecutorServiceProvider>)Bindings.service((Object)defaultScheduledExecutorProvider).to((Class)ScheduledExecutorServiceProvider.class);
        injectionManager.register((Binding)schedulerBinding);
        finalizer.registerForPreDestroyCall((Object)defaultScheduledExecutorProvider);
        this.registerExecutors(injectionManager, this.componentBag, defaultAsyncExecutorProvider, defaultScheduledExecutorProvider);
    }
    
    private static ExecutorService lookupManagedExecutorService() {
        try {
            final Class<?> aClass = AccessController.doPrivileged((PrivilegedAction<Class<?>>)ReflectionHelper.classForNamePA("javax.naming.InitialContext"));
            final Object initialContext = aClass.newInstance();
            final Method lookupMethod = aClass.getMethod("lookup", String.class);
            return (ExecutorService)lookupMethod.invoke(initialContext, "java:comp/DefaultManagedExecutorService");
        }
        catch (final Exception e) {
            if (ClientExecutorProvidersConfigurator.LOGGER.isLoggable(Level.FINE)) {
                ClientExecutorProvidersConfigurator.LOGGER.log(Level.FINE, e.getMessage(), e);
            }
        }
        catch (final LinkageError linkageError) {}
        return null;
    }
    
    private ScheduledExecutorService lookupManagedScheduledExecutorService() {
        try {
            final Class<?> aClass = AccessController.doPrivileged((PrivilegedAction<Class<?>>)ReflectionHelper.classForNamePA("javax.naming.InitialContext"));
            final Object initialContext = aClass.newInstance();
            final Method lookupMethod = aClass.getMethod("lookup", String.class);
            return (ScheduledExecutorService)lookupMethod.invoke(initialContext, "java:comp/DefaultManagedScheduledExecutorService");
        }
        catch (final Exception e) {
            if (ClientExecutorProvidersConfigurator.LOGGER.isLoggable(Level.FINE)) {
                ClientExecutorProvidersConfigurator.LOGGER.log(Level.FINE, e.getMessage(), e);
            }
        }
        catch (final LinkageError linkageError) {}
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(ClientExecutorProvidersConfigurator.class.getName());
        MANAGED_EXECUTOR_SERVICE = lookupManagedExecutorService();
    }
    
    @ClientAsyncExecutor
    public static class ClientExecutorServiceProvider implements ExecutorServiceProvider
    {
        private final ExecutorService executorService;
        
        ClientExecutorServiceProvider(final ExecutorService executorService) {
            this.executorService = executorService;
        }
        
        public ExecutorService getExecutorService() {
            return this.executorService;
        }
        
        public void dispose(final ExecutorService executorService) {
        }
    }
    
    @ClientBackgroundScheduler
    public static class ClientScheduledExecutorServiceProvider implements ScheduledExecutorServiceProvider
    {
        private final Value<ScheduledExecutorService> executorService;
        
        ClientScheduledExecutorServiceProvider(final Value<ScheduledExecutorService> executorService) {
            this.executorService = executorService;
        }
        
        public ScheduledExecutorService getExecutorService() {
            return (ScheduledExecutorService)this.executorService.get();
        }
        
        public void dispose(final ExecutorService executorService) {
        }
    }
}
