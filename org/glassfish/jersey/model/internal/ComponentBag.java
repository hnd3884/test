package org.glassfish.jersey.model.internal;

import javax.ws.rs.core.Feature;
import org.glassfish.jersey.spi.ExecutorServiceProvider;
import org.glassfish.jersey.spi.ScheduledExecutorServiceProvider;
import java.util.stream.Stream;
import java.util.Collection;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import javax.inject.Scope;
import javax.ws.rs.NameBinding;
import javax.annotation.Priority;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.Severity;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import org.glassfish.jersey.internal.inject.ClassBinding;
import java.util.List;
import org.glassfish.jersey.internal.inject.Binding;
import java.util.Map;
import java.util.Set;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.internal.inject.InjectionManager;
import java.util.function.BiPredicate;
import org.glassfish.jersey.internal.inject.Binder;
import java.util.function.Function;
import org.glassfish.jersey.model.ContractProvider;
import java.util.function.Predicate;

public class ComponentBag
{
    private static final Predicate<ContractProvider> EXCLUDE_META_PROVIDERS;
    private static final Function<Object, Binder> CAST_TO_BINDER;
    public static final BiPredicate<ContractProvider, InjectionManager> EXTERNAL_ONLY;
    public static final Predicate<ContractProvider> BINDERS_ONLY;
    public static final Predicate<ContractProvider> EXECUTOR_SERVICE_PROVIDER_ONLY;
    public static final Predicate<ContractProvider> SCHEDULED_EXECUTOR_SERVICE_PROVIDER_ONLY;
    public static final Predicate<ContractProvider> EXCLUDE_EMPTY;
    public static final Predicate<ContractProvider> INCLUDE_ALL;
    static final Inflector<ContractProvider.Builder, ContractProvider> AS_IS;
    private final Predicate<ContractProvider> registrationStrategy;
    private final Set<Class<?>> classes;
    private final Set<Class<?>> classesView;
    private final Set<Object> instances;
    private final Set<Object> instancesView;
    private final Map<Class<?>, ContractProvider> models;
    private final Set<Class<?>> modelKeysView;
    
    public static Predicate<ContractProvider> excludeMetaProviders(final InjectionManager injectionManager) {
        return ComponentBag.EXCLUDE_META_PROVIDERS.and(model -> !injectionManager.isRegistrable(model.getImplementationClass()));
    }
    
    public static ComponentBag newInstance(final Predicate<ContractProvider> registrationStrategy) {
        return new ComponentBag(registrationStrategy);
    }
    
    public static <T> List<T> getFromBinders(final InjectionManager injectionManager, final ComponentBag componentBag, final Function<Object, T> cast, final Predicate<Binding> filter) {
        final Function<Binding, Object> bindingToObject = (Function<Binding, Object>)(binding -> {
            if (binding instanceof ClassBinding) {
                final ClassBinding classBinding = (ClassBinding)binding;
                return injectionManager.createAndInitialize(classBinding.getService());
            }
            else {
                final InstanceBinding instanceBinding = (InstanceBinding)binding;
                return instanceBinding.getService();
            }
        });
        return componentBag.getInstances(ComponentBag.BINDERS_ONLY).stream().map((Function<? super Object, ?>)ComponentBag.CAST_TO_BINDER).flatMap(binder -> Bindings.getBindings(injectionManager, binder).stream()).filter((Predicate<? super Object>)filter).map((Function<? super Object, ?>)bindingToObject).map((Function<? super Object, ?>)cast).collect((Collector<? super Object, ?, List<T>>)Collectors.toList());
    }
    
    private ComponentBag(final Predicate<ContractProvider> registrationStrategy) {
        this.registrationStrategy = registrationStrategy;
        this.classes = new LinkedHashSet<Class<?>>();
        this.instances = new LinkedHashSet<Object>();
        this.models = new IdentityHashMap<Class<?>, ContractProvider>();
        this.classesView = Collections.unmodifiableSet((Set<? extends Class<?>>)this.classes);
        this.instancesView = Collections.unmodifiableSet((Set<?>)this.instances);
        this.modelKeysView = Collections.unmodifiableSet((Set<? extends Class<?>>)this.models.keySet());
    }
    
    private ComponentBag(final Predicate<ContractProvider> registrationStrategy, final Set<Class<?>> classes, final Set<Object> instances, final Map<Class<?>, ContractProvider> models) {
        this.registrationStrategy = registrationStrategy;
        this.classes = classes;
        this.instances = instances;
        this.models = models;
        this.classesView = Collections.unmodifiableSet((Set<? extends Class<?>>)classes);
        this.instancesView = Collections.unmodifiableSet((Set<?>)instances);
        this.modelKeysView = Collections.unmodifiableSet((Set<? extends Class<?>>)models.keySet());
    }
    
    public boolean register(final Class<?> componentClass, final Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
        final boolean result = this.registerModel(componentClass, -1, null, modelEnhancer);
        if (result) {
            this.classes.add(componentClass);
        }
        return result;
    }
    
    public boolean register(final Class<?> componentClass, final int priority, final Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
        final boolean result = this.registerModel(componentClass, priority, null, modelEnhancer);
        if (result) {
            this.classes.add(componentClass);
        }
        return result;
    }
    
    public boolean register(final Class<?> componentClass, final Set<Class<?>> contracts, final Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
        final boolean result = this.registerModel(componentClass, -1, asMap(contracts), modelEnhancer);
        if (result) {
            this.classes.add(componentClass);
        }
        return result;
    }
    
    public boolean register(final Class<?> componentClass, final Map<Class<?>, Integer> contracts, final Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
        final boolean result = this.registerModel(componentClass, -1, contracts, modelEnhancer);
        if (result) {
            this.classes.add(componentClass);
        }
        return result;
    }
    
    public boolean register(final Object component, final Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
        final Class<?> componentClass = component.getClass();
        final boolean result = this.registerModel(componentClass, -1, null, modelEnhancer);
        if (result) {
            this.instances.add(component);
        }
        return result;
    }
    
    public boolean register(final Object component, final int priority, final Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
        final Class<?> componentClass = component.getClass();
        final boolean result = this.registerModel(componentClass, priority, null, modelEnhancer);
        if (result) {
            this.instances.add(component);
        }
        return result;
    }
    
    public boolean register(final Object component, final Set<Class<?>> contracts, final Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
        final Class<?> componentClass = component.getClass();
        final boolean result = this.registerModel(componentClass, -1, asMap(contracts), modelEnhancer);
        if (result) {
            this.instances.add(component);
        }
        return result;
    }
    
    public boolean register(final Object component, final Map<Class<?>, Integer> contracts, final Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
        final Class<?> componentClass = component.getClass();
        final boolean result = this.registerModel(componentClass, -1, contracts, modelEnhancer);
        if (result) {
            this.instances.add(component);
        }
        return result;
    }
    
    private boolean registerModel(final Class<?> componentClass, final int defaultPriority, final Map<Class<?>, Integer> contractMap, final Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
        return Errors.process(() -> {
            if (this.models.containsKey(componentClass)) {
                Errors.error(LocalizationMessages.COMPONENT_TYPE_ALREADY_REGISTERED(componentClass), Severity.HINT);
                return false;
            }
            else {
                final ContractProvider model = modelFor(componentClass, defaultPriority, contractMap, modelEnhancer);
                if (!this.registrationStrategy.test(model)) {
                    return false;
                }
                else {
                    this.models.put(componentClass, model);
                    return true;
                }
            }
        });
    }
    
    public static ContractProvider modelFor(final Class<?> componentClass) {
        return modelFor(componentClass, -1, null, ComponentBag.AS_IS);
    }
    
    private static ContractProvider modelFor(final Class<?> componentClass, final int defaultPriority, final Map<Class<?>, Integer> contractMap, final Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
        Map<Class<?>, Integer> contracts = contractMap;
        if (contracts == null) {
            contracts = asMap(Providers.getProviderContracts(componentClass));
        }
        else {
            final Iterator<Class<?>> it = contracts.keySet().iterator();
            while (it.hasNext()) {
                final Class<?> contract = it.next();
                if (contract == null) {
                    it.remove();
                }
                else {
                    boolean failed = false;
                    if (!Providers.isSupportedContract(contract)) {
                        Errors.error(LocalizationMessages.CONTRACT_NOT_SUPPORTED(contract, componentClass), Severity.WARNING);
                        failed = true;
                    }
                    if (!contract.isAssignableFrom(componentClass)) {
                        Errors.error(LocalizationMessages.CONTRACT_NOT_ASSIGNABLE(contract, componentClass), Severity.WARNING);
                        failed = true;
                    }
                    if (!failed) {
                        continue;
                    }
                    it.remove();
                }
            }
        }
        final ContractProvider.Builder builder = ContractProvider.builder(componentClass).addContracts(contracts).defaultPriority(defaultPriority);
        final boolean useAnnotationPriority = defaultPriority == -1;
        for (final Annotation annotation : componentClass.getAnnotations()) {
            if (annotation instanceof Priority) {
                if (useAnnotationPriority) {
                    builder.defaultPriority(((Priority)annotation).value());
                }
            }
            else {
                for (final Annotation metaAnnotation : annotation.annotationType().getAnnotations()) {
                    if (metaAnnotation instanceof NameBinding) {
                        builder.addNameBinding(annotation.annotationType());
                    }
                    if (metaAnnotation instanceof Scope) {
                        builder.scope(annotation.annotationType());
                    }
                }
            }
        }
        return modelEnhancer.apply(builder);
    }
    
    private static Map<Class<?>, Integer> asMap(final Set<Class<?>> contractSet) {
        final Map<Class<?>, Integer> contracts = new IdentityHashMap<Class<?>, Integer>();
        for (final Class<?> contract : contractSet) {
            contracts.put(contract, -1);
        }
        return contracts;
    }
    
    public Set<Class<?>> getClasses() {
        return this.classesView;
    }
    
    public Set<Object> getInstances() {
        return this.instancesView;
    }
    
    public Set<Class<?>> getClasses(final Predicate<ContractProvider> filter) {
        return this.classesView.stream().filter(input -> {
            final ContractProvider model = this.getModel(input);
            return filter.test(model);
        }).collect((Collector<? super Object, ?, Set<Class<?>>>)Collectors.toSet());
    }
    
    public Set<Object> getInstances(final Predicate<ContractProvider> filter) {
        return this.instancesView.stream().filter(input -> {
            final ContractProvider model = this.getModel(input.getClass());
            return filter.test(model);
        }).collect((Collector<? super Object, ?, Set<Object>>)Collectors.toSet());
    }
    
    public Set<Class<?>> getRegistrations() {
        return this.modelKeysView;
    }
    
    public ContractProvider getModel(final Class<?> componentClass) {
        return this.models.get(componentClass);
    }
    
    public ComponentBag copy() {
        return new ComponentBag(this.registrationStrategy, new LinkedHashSet<Class<?>>(this.classes), new LinkedHashSet<Object>(this.instances), new IdentityHashMap<Class<?>, ContractProvider>(this.models));
    }
    
    public ComponentBag immutableCopy() {
        return new ImmutableComponentBag(this);
    }
    
    public void clear() {
        this.classes.clear();
        this.instances.clear();
        this.models.clear();
    }
    
    void loadFrom(final ComponentBag bag) {
        this.clear();
        this.classes.addAll(bag.classes);
        this.instances.addAll(bag.instances);
        this.models.putAll(bag.models);
    }
    
    static {
        EXCLUDE_META_PROVIDERS = (model -> {
            final Set<Class<?>> contracts = model.getContracts();
            if (contracts.isEmpty()) {
                return true;
            }
            else {
                byte count = 0;
                if (contracts.contains(Feature.class)) {
                    ++count;
                }
                if (contracts.contains(Binder.class)) {
                    ++count;
                }
                return contracts.size() > count;
            }
        });
        CAST_TO_BINDER = Binder.class::cast;
        EXTERNAL_ONLY = ((model, injectionManager) -> model.getImplementationClass() != null && injectionManager.isRegistrable(model.getImplementationClass()));
        BINDERS_ONLY = (model -> model.getContracts().contains(Binder.class));
        EXECUTOR_SERVICE_PROVIDER_ONLY = (model -> model.getContracts().contains(ExecutorServiceProvider.class) && !model.getContracts().contains(ScheduledExecutorServiceProvider.class));
        SCHEDULED_EXECUTOR_SERVICE_PROVIDER_ONLY = (model -> model.getContracts().contains(ScheduledExecutorServiceProvider.class));
        EXCLUDE_EMPTY = (model -> !model.getContracts().isEmpty());
        INCLUDE_ALL = (contractProvider -> true);
        AS_IS = ContractProvider.Builder::build;
    }
    
    private static class ImmutableComponentBag extends ComponentBag
    {
        ImmutableComponentBag(final ComponentBag original) {
            super(original.registrationStrategy, new LinkedHashSet(original.classes), new LinkedHashSet(original.instances), new IdentityHashMap(original.models), null);
        }
        
        @Override
        public boolean register(final Class<?> componentClass, final Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
            throw new IllegalStateException("This instance is read-only.");
        }
        
        @Override
        public boolean register(final Class<?> componentClass, final int priority, final Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
            throw new IllegalStateException("This instance is read-only.");
        }
        
        @Override
        public boolean register(final Class<?> componentClass, final Set<Class<?>> contracts, final Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
            throw new IllegalStateException("This instance is read-only.");
        }
        
        @Override
        public boolean register(final Class<?> componentClass, final Map<Class<?>, Integer> contracts, final Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
            throw new IllegalStateException("This instance is read-only.");
        }
        
        @Override
        public boolean register(final Object component, final Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
            throw new IllegalStateException("This instance is read-only.");
        }
        
        @Override
        public boolean register(final Object component, final int priority, final Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
            throw new IllegalStateException("This instance is read-only.");
        }
        
        @Override
        public boolean register(final Object component, final Set<Class<?>> contracts, final Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
            throw new IllegalStateException("This instance is read-only.");
        }
        
        @Override
        public boolean register(final Object component, final Map<Class<?>, Integer> contracts, final Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
            throw new IllegalStateException("This instance is read-only.");
        }
        
        @Override
        public ComponentBag copy() {
            return this;
        }
        
        @Override
        public ComponentBag immutableCopy() {
            return this;
        }
        
        @Override
        public void clear() {
            throw new IllegalStateException("This instance is read-only.");
        }
    }
}
