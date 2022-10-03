package org.glassfish.jersey.process.internal;

import org.glassfish.jersey.internal.inject.DisposableSupplier;
import java.util.logging.Logger;
import java.lang.reflect.AnnotatedElement;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import javax.inject.Qualifier;
import java.util.HashMap;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.util.logging.Level;
import java.util.Iterator;
import java.util.concurrent.ScheduledExecutorService;
import org.glassfish.jersey.internal.inject.Binding;
import javax.inject.Named;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;
import javax.inject.Singleton;
import java.util.function.Supplier;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.SupplierInstanceBinding;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.LinkedList;
import java.util.Collection;
import org.glassfish.jersey.internal.inject.Providers;
import java.util.List;
import org.glassfish.jersey.spi.ScheduledExecutorServiceProvider;
import org.glassfish.jersey.spi.ExecutorServiceProvider;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.util.ExtendedLogger;

public final class ExecutorProviders
{
    private static final ExtendedLogger LOGGER;
    
    private ExecutorProviders() {
        throw new AssertionError((Object)"Instantiation not allowed.");
    }
    
    public static void registerExecutorBindings(final InjectionManager injectionManager) {
        final List<ExecutorServiceProvider> executorProviders = getExecutorProviders(injectionManager, ExecutorServiceProvider.class);
        final List<ScheduledExecutorServiceProvider> scheduledProviders = getExecutorProviders(injectionManager, ScheduledExecutorServiceProvider.class);
        registerExecutorBindings(injectionManager, executorProviders, scheduledProviders);
    }
    
    private static <T> List<T> getExecutorProviders(final InjectionManager injectionManager, final Class<T> providerClass) {
        final Set<T> customProviders = Providers.getCustomProviders(injectionManager, providerClass);
        final Set<T> defaultProviders = Providers.getProviders(injectionManager, providerClass);
        defaultProviders.removeAll(customProviders);
        final List<T> executorProviders = new LinkedList<T>((Collection<? extends T>)customProviders);
        executorProviders.addAll((Collection<? extends T>)defaultProviders);
        return executorProviders;
    }
    
    public static void registerExecutorBindings(final InjectionManager injectionManager, final List<ExecutorServiceProvider> executorProviders, final List<ScheduledExecutorServiceProvider> scheduledProviders) {
        final Map<Class<? extends Annotation>, List<ExecutorServiceProvider>> executorProviderMap = getQualifierToProviderMap(executorProviders);
        for (final Map.Entry<Class<? extends Annotation>, List<ExecutorServiceProvider>> qualifierToProviders : executorProviderMap.entrySet()) {
            final Class<? extends Annotation> qualifierAnnotationClass = qualifierToProviders.getKey();
            final Iterator<ExecutorServiceProvider> bucketProviderIterator = qualifierToProviders.getValue().iterator();
            final ExecutorServiceProvider executorProvider = bucketProviderIterator.next();
            logExecutorServiceProvider(qualifierAnnotationClass, bucketProviderIterator, executorProvider);
            final SupplierInstanceBinding<ExecutorService> descriptor = (SupplierInstanceBinding<ExecutorService>)Bindings.supplier((Supplier<Object>)new ExecutorServiceSupplier(executorProvider)).in((Class<? extends Annotation>)Singleton.class).to((Type)ExecutorService.class);
            final Annotation qualifier = executorProvider.getClass().getAnnotation(qualifierAnnotationClass);
            if (qualifier instanceof Named) {
                descriptor.named(((Named)qualifier).value());
            }
            else {
                descriptor.qualifiedBy(qualifier);
            }
            injectionManager.register(descriptor);
        }
        final Map<Class<? extends Annotation>, List<ScheduledExecutorServiceProvider>> schedulerProviderMap = getQualifierToProviderMap(scheduledProviders);
        for (final Map.Entry<Class<? extends Annotation>, List<ScheduledExecutorServiceProvider>> qualifierToProviders2 : schedulerProviderMap.entrySet()) {
            final Class<? extends Annotation> qualifierAnnotationClass2 = qualifierToProviders2.getKey();
            final Iterator<ScheduledExecutorServiceProvider> bucketProviderIterator2 = qualifierToProviders2.getValue().iterator();
            final ScheduledExecutorServiceProvider executorProvider2 = bucketProviderIterator2.next();
            logScheduledExecutorProvider(qualifierAnnotationClass2, bucketProviderIterator2, executorProvider2);
            final SupplierInstanceBinding<ScheduledExecutorService> descriptor2 = (SupplierInstanceBinding<ScheduledExecutorService>)Bindings.supplier((Supplier<Object>)new ScheduledExecutorServiceSupplier(executorProvider2)).in((Class<? extends Annotation>)Singleton.class).to((Type)ScheduledExecutorService.class);
            if (!executorProviderMap.containsKey(qualifierAnnotationClass2)) {
                descriptor2.to(ExecutorService.class);
            }
            final Annotation qualifier2 = executorProvider2.getClass().getAnnotation(qualifierAnnotationClass2);
            if (qualifier2 instanceof Named) {
                descriptor2.named(((Named)qualifier2).value());
            }
            else {
                descriptor2.qualifiedBy(qualifier2);
            }
            injectionManager.register(descriptor2);
        }
    }
    
    private static void logScheduledExecutorProvider(final Class<? extends Annotation> qualifierAnnotationClass, final Iterator<ScheduledExecutorServiceProvider> bucketProviderIterator, final ScheduledExecutorServiceProvider executorProvider) {
        if (ExecutorProviders.LOGGER.isLoggable(Level.CONFIG)) {
            ExecutorProviders.LOGGER.config(LocalizationMessages.USING_SCHEDULER_PROVIDER(executorProvider.getClass().getName(), qualifierAnnotationClass.getName()));
            if (bucketProviderIterator.hasNext()) {
                final StringBuilder msg = new StringBuilder(bucketProviderIterator.next().getClass().getName());
                while (bucketProviderIterator.hasNext()) {
                    msg.append(", ").append(bucketProviderIterator.next().getClass().getName());
                }
                ExecutorProviders.LOGGER.config(LocalizationMessages.IGNORED_SCHEDULER_PROVIDERS(msg.toString(), qualifierAnnotationClass.getName()));
            }
        }
    }
    
    private static void logExecutorServiceProvider(final Class<? extends Annotation> qualifierAnnotationClass, final Iterator<ExecutorServiceProvider> bucketProviderIterator, final ExecutorServiceProvider executorProvider) {
        if (ExecutorProviders.LOGGER.isLoggable(Level.CONFIG)) {
            ExecutorProviders.LOGGER.config(LocalizationMessages.USING_EXECUTOR_PROVIDER(executorProvider.getClass().getName(), qualifierAnnotationClass.getName()));
            if (bucketProviderIterator.hasNext()) {
                final StringBuilder msg = new StringBuilder(bucketProviderIterator.next().getClass().getName());
                while (bucketProviderIterator.hasNext()) {
                    msg.append(", ").append(bucketProviderIterator.next().getClass().getName());
                }
                ExecutorProviders.LOGGER.config(LocalizationMessages.IGNORED_EXECUTOR_PROVIDERS(msg.toString(), qualifierAnnotationClass.getName()));
            }
        }
    }
    
    private static <T extends ExecutorServiceProvider> Map<Class<? extends Annotation>, List<T>> getQualifierToProviderMap(final List<T> executorProviders) {
        final Map<Class<? extends Annotation>, List<T>> executorProviderMap = new HashMap<Class<? extends Annotation>, List<T>>();
        for (final T provider : executorProviders) {
            for (final Class<? extends Annotation> qualifier : ReflectionHelper.getAnnotationTypes(provider.getClass(), (Class<? extends Annotation>)Qualifier.class)) {
                List<T> providersForQualifier;
                if (!executorProviderMap.containsKey(qualifier)) {
                    providersForQualifier = new LinkedList<T>();
                    executorProviderMap.put(qualifier, providersForQualifier);
                }
                else {
                    providersForQualifier = executorProviderMap.get(qualifier);
                }
                providersForQualifier.add(provider);
            }
        }
        return executorProviderMap;
    }
    
    static {
        LOGGER = new ExtendedLogger(Logger.getLogger(ExecutorProviders.class.getName()), Level.FINEST);
    }
    
    private static class ExecutorServiceSupplier implements DisposableSupplier<ExecutorService>
    {
        private final ExecutorServiceProvider executorProvider;
        
        private ExecutorServiceSupplier(final ExecutorServiceProvider executorServiceProvider) {
            this.executorProvider = executorServiceProvider;
        }
        
        @Override
        public ExecutorService get() {
            return this.executorProvider.getExecutorService();
        }
        
        @Override
        public void dispose(final ExecutorService instance) {
            this.executorProvider.dispose(instance);
        }
    }
    
    private static class ScheduledExecutorServiceSupplier implements DisposableSupplier<ScheduledExecutorService>
    {
        private final ScheduledExecutorServiceProvider executorProvider;
        
        private ScheduledExecutorServiceSupplier(final ScheduledExecutorServiceProvider executorServiceProvider) {
            this.executorProvider = executorServiceProvider;
        }
        
        @Override
        public ScheduledExecutorService get() {
            return this.executorProvider.getExecutorService();
        }
        
        @Override
        public void dispose(final ScheduledExecutorService instance) {
            this.executorProvider.dispose(instance);
        }
    }
}
