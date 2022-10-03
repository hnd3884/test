package org.glassfish.jersey.internal.inject;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.List;
import java.util.function.Consumer;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.Set;
import javax.ws.rs.RuntimeType;
import java.util.Collections;
import org.glassfish.jersey.model.internal.ComponentBag;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.lang.annotation.Annotation;
import java.util.Collection;
import org.glassfish.jersey.model.ContractProvider;

public class ProviderBinder
{
    private final InjectionManager injectionManager;
    
    public ProviderBinder(final InjectionManager injectionManager) {
        this.injectionManager = injectionManager;
    }
    
    public static void bindProvider(final Class<?> providerClass, final ContractProvider model, final InjectionManager injectionManager) {
        injectionManager.register(CompositeBinder.wrap(createProviderBinders(providerClass, model)));
    }
    
    private static Collection<Binder> createProviderBinders(final Class<?> providerClass, final ContractProvider model) {
        final Function<Class, Binder> binderFunction = (Function<Class, Binder>)(contract -> new AbstractBinder() {
            final /* synthetic */ Class val$providerClass;
            final /* synthetic */ ContractProvider val$model;
            final /* synthetic */ Class val$contract;
            
            @Override
            protected void configure() {
                final ClassBinding builder = (ClassBinding)this.bind((Class<Object>)this.val$providerClass).in(this.val$model.getScope()).qualifiedBy(CustomAnnotationLiteral.INSTANCE).to(this.val$contract);
                final int priority = this.val$model.getPriority(this.val$contract);
                if (priority > -1) {
                    builder.ranked(priority);
                }
            }
        });
        return model.getContracts().stream().map((Function<? super Object, ?>)binderFunction).collect((Collector<? super Object, ?, Collection<Binder>>)Collectors.toList());
    }
    
    public static void bindProvider(final Object providerInstance, final ContractProvider model, final InjectionManager injectionManager) {
        injectionManager.register(CompositeBinder.wrap(createProviderBinders(providerInstance, model)));
    }
    
    private static Collection<Binder> createProviderBinders(final Object providerInstance, final ContractProvider model) {
        final Function<Class, Binder> binderFunction = (Function<Class, Binder>)(contract -> new AbstractBinder() {
            final /* synthetic */ Object val$providerInstance;
            final /* synthetic */ Class val$contract;
            final /* synthetic */ ContractProvider val$model;
            
            @Override
            protected void configure() {
                final InstanceBinding builder = (InstanceBinding)this.bind(this.val$providerInstance).qualifiedBy(CustomAnnotationLiteral.INSTANCE).to(this.val$contract);
                final int priority = this.val$model.getPriority(this.val$contract);
                if (priority > -1) {
                    builder.ranked(priority);
                }
            }
        });
        return model.getContracts().stream().map((Function<? super Object, ?>)binderFunction).collect((Collector<? super Object, ?, Collection<Binder>>)Collectors.toList());
    }
    
    public static void bindProviders(final ComponentBag componentBag, final InjectionManager injectionManager) {
        bindProviders(componentBag, null, Collections.emptySet(), injectionManager);
    }
    
    public static void bindProviders(final ComponentBag componentBag, final RuntimeType constrainedTo, final Set<Class<?>> registeredClasses, final InjectionManager injectionManager) {
        final Predicate<ContractProvider> filter = ComponentBag.EXCLUDE_EMPTY.and(ComponentBag.excludeMetaProviders(injectionManager));
        final Predicate<Class<?>> correctlyConfigured = componentClass -> Providers.checkProviderRuntime(componentClass, componentBag.getModel(componentClass), constrainedTo, registeredClasses == null || !registeredClasses.contains(componentClass), false);
        final Collection<Binder> binderToRegister = new ArrayList<Binder>();
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>(componentBag.getClasses(filter));
        if (constrainedTo != null) {
            classes = classes.stream().filter((Predicate<? super Object>)correctlyConfigured).collect((Collector<? super Object, ?, Set<Class<?>>>)Collectors.toSet());
        }
        for (final Class<?> providerClass : classes) {
            final ContractProvider model = componentBag.getModel(providerClass);
            binderToRegister.addAll(createProviderBinders(providerClass, model));
        }
        Set<Object> instances = componentBag.getInstances(filter);
        if (constrainedTo != null) {
            instances = instances.stream().filter(component -> correctlyConfigured.test(component.getClass())).collect((Collector<? super Object, ?, Set<Object>>)Collectors.toSet());
        }
        for (final Object provider : instances) {
            final ContractProvider model2 = componentBag.getModel(provider.getClass());
            binderToRegister.addAll(createProviderBinders(provider, model2));
        }
        injectionManager.register(CompositeBinder.wrap(binderToRegister));
    }
    
    private static <T> Collection<Binder> createInstanceBinders(final T instance) {
        final Function<Class, Binder> binderFunction = (Function<Class, Binder>)(contract -> new AbstractBinder() {
            final /* synthetic */ Object val$instance;
            final /* synthetic */ Class val$contract;
            
            @Override
            protected void configure() {
                this.bind(this.val$instance).to(this.val$contract).qualifiedBy(CustomAnnotationLiteral.INSTANCE);
            }
        });
        return Providers.getProviderContracts(instance.getClass()).stream().map((Function<? super Object, ?>)binderFunction).collect((Collector<? super Object, ?, Collection<Binder>>)Collectors.toList());
    }
    
    public void bindInstances(final Iterable<Object> instances) {
        final List<Object> instancesList = new ArrayList<Object>();
        instances.forEach(instancesList::add);
        this.bindInstances(instancesList);
    }
    
    public void bindInstances(final Collection<Object> instances) {
        final List<Binder> binders = instances.stream().map((Function<? super Object, ?>)ProviderBinder::createInstanceBinders).flatMap((Function<? super Object, ? extends Stream<?>>)Collection::stream).collect((Collector<? super Object, ?, List<Binder>>)Collectors.toList());
        this.injectionManager.register(CompositeBinder.wrap(binders));
    }
    
    public void bindClasses(final Class<?>... classes) {
        this.bindClasses(Arrays.asList(classes), false);
    }
    
    public void bindClasses(final Iterable<Class<?>> classes) {
        final List<Class<?>> classesList = new ArrayList<Class<?>>();
        classes.forEach(classesList::add);
        this.bindClasses(classesList, false);
    }
    
    public void bindClasses(final Collection<Class<?>> classes) {
        this.bindClasses(classes, false);
    }
    
    public void bindClasses(final Collection<Class<?>> classes, final boolean bindResources) {
        final List<Binder> binders = classes.stream().map(clazz -> this.createClassBinders((Class<Object>)clazz, bindResources)).collect((Collector<? super Object, ?, List<Binder>>)Collectors.toList());
        this.injectionManager.register(CompositeBinder.wrap(binders));
    }
    
    private <T> Binder createClassBinders(final Class<T> clazz, final boolean isResource) {
        final Class<? extends Annotation> scope = this.getProviderScope(clazz);
        if (isResource) {
            return new AbstractBinder() {
                @Override
                protected void configure() {
                    final ClassBinding<T> descriptor = (ClassBinding<T>)this.bindAsContract((Class<Object>)clazz).in(scope);
                    for (final Class contract : Providers.getProviderContracts(clazz)) {
                        descriptor.addAlias(contract).in(scope.getName()).qualifiedBy(CustomAnnotationLiteral.INSTANCE);
                    }
                }
            };
        }
        return new AbstractBinder() {
            @Override
            protected void configure() {
                final ClassBinding<T> builder = (ClassBinding<T>)this.bind((Class<Object>)clazz).in(scope).qualifiedBy(CustomAnnotationLiteral.INSTANCE);
                Providers.getProviderContracts(clazz).forEach(contract -> {
                    final ClassBinding classBinding = (ClassBinding)builder.to(contract);
                });
            }
        };
    }
    
    private Class<? extends Annotation> getProviderScope(final Class<?> clazz) {
        Class<? extends Annotation> scope = (Class<? extends Annotation>)Singleton.class;
        if (clazz.isAnnotationPresent(PerLookup.class)) {
            scope = PerLookup.class;
        }
        return scope;
    }
}
