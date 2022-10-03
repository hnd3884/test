package org.glassfish.jersey.inject.hk2;

import org.glassfish.jersey.internal.inject.PerThread;
import org.glassfish.jersey.internal.inject.PerLookup;
import org.glassfish.hk2.utilities.AliasDescriptor;
import org.glassfish.hk2.utilities.ActiveDescriptorBuilder;
import org.glassfish.hk2.utilities.binding.ServiceBindingBuilder;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.Descriptor;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.jersey.internal.inject.DisposableSupplier;
import org.glassfish.hk2.utilities.reflection.ParameterizedTypeImpl;
import org.glassfish.jersey.internal.inject.InjectionResolver;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.internal.inject.SupplierInstanceBinding;
import org.glassfish.jersey.internal.inject.SupplierClassBinding;
import org.glassfish.jersey.internal.inject.InjectionResolverBinding;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import org.glassfish.jersey.internal.inject.AliasBinding;
import java.util.Set;
import org.glassfish.jersey.internal.inject.ClassBinding;
import java.lang.annotation.Annotation;
import org.glassfish.hk2.api.DynamicConfigurationService;
import java.util.Iterator;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.internal.inject.Binding;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.Binder;

class Hk2Helper
{
    static void bind(final AbstractHk2InjectionManager injectionManager, final Binder jerseyBinder) {
        bind(injectionManager.getServiceLocator(), Bindings.getBindings((InjectionManager)injectionManager, jerseyBinder));
    }
    
    static void bind(final ServiceLocator locator, final Binding binding) {
        bindBinding(locator, (Binding<?, ?>)binding);
    }
    
    static void bind(final ServiceLocator locator, final Iterable<Binding> descriptors) {
        final DynamicConfiguration dc = getDynamicConfiguration(locator);
        for (final Binding binding : descriptors) {
            bindBinding(locator, dc, (Binding<?, ?>)binding);
        }
        dc.commit();
    }
    
    private static DynamicConfiguration getDynamicConfiguration(final ServiceLocator locator) {
        final DynamicConfigurationService dcs = (DynamicConfigurationService)locator.getService((Class)DynamicConfigurationService.class, new Annotation[0]);
        return dcs.createDynamicConfiguration();
    }
    
    private static void bindBinding(final ServiceLocator locator, final Binding<?, ?> binding) {
        final DynamicConfiguration dc = getDynamicConfiguration(locator);
        bindBinding(locator, dc, binding);
        dc.commit();
    }
    
    private static void bindBinding(final ServiceLocator locator, final DynamicConfiguration dc, final Binding<?, ?> binding) {
        if (ClassBinding.class.isAssignableFrom(binding.getClass())) {
            final ActiveDescriptor<?> activeDescriptor = translateToActiveDescriptor((ClassBinding<?>)binding);
            bindBinding(locator, dc, activeDescriptor, binding.getAliases());
        }
        else if (InstanceBinding.class.isAssignableFrom(binding.getClass())) {
            final ActiveDescriptor<?> activeDescriptor = translateToActiveDescriptor((InstanceBinding<?>)binding, new Type[0]);
            bindBinding(locator, dc, activeDescriptor, binding.getAliases());
        }
        else if (InjectionResolverBinding.class.isAssignableFrom(binding.getClass())) {
            final InjectionResolverBinding resolverDescriptor = (InjectionResolverBinding)binding;
            bindBinding(locator, dc, wrapInjectionResolver(resolverDescriptor), binding.getAliases());
            bindBinding(locator, dc, translateToActiveDescriptor((InjectionResolverBinding<?>)resolverDescriptor), binding.getAliases());
        }
        else if (SupplierClassBinding.class.isAssignableFrom(binding.getClass())) {
            bindSupplierClassBinding(locator, (SupplierClassBinding<?>)binding);
        }
        else {
            if (!SupplierInstanceBinding.class.isAssignableFrom(binding.getClass())) {
                throw new RuntimeException(LocalizationMessages.UNKNOWN_DESCRIPTOR_TYPE((Object)binding.getClass().getSimpleName()));
            }
            bindSupplierInstanceBinding(locator, (SupplierInstanceBinding<?>)binding);
        }
    }
    
    private static ActiveDescriptor<?> wrapInjectionResolver(final InjectionResolverBinding resolverDescriptor) {
        final InjectionResolverWrapper<?> wrappedResolver = new InjectionResolverWrapper<Object>((org.glassfish.jersey.internal.inject.InjectionResolver<?>)resolverDescriptor.getResolver());
        return translateToActiveDescriptor((InstanceBinding<?>)Bindings.service((Object)wrappedResolver), (Type)new ParameterizedTypeImpl((Type)org.glassfish.hk2.api.InjectionResolver.class, new Type[] { resolverDescriptor.getResolver().getAnnotation() }));
    }
    
    private static void bindSupplierInstanceBinding(final ServiceLocator locator, final SupplierInstanceBinding<?> binding) {
        final Consumer<AbstractBinder> bindConsumer = binder -> {
            final Supplier<?> supplier = binding.getSupplier();
            final boolean disposable = DisposableSupplier.class.isAssignableFrom(supplier.getClass());
            final AbstractActiveDescriptor<? extends Supplier<?>> supplierBuilder = (AbstractActiveDescriptor<? extends Supplier<?>>)BuilderHelper.createConstantDescriptor((Object)supplier);
            binding.getContracts().forEach(contract -> {
                new ParameterizedTypeImpl((Type)Supplier.class, new Type[] { contract });
                final ParameterizedTypeImpl parameterizedTypeImpl;
                supplierBuilder.addContractType((Type)parameterizedTypeImpl);
                if (disposable) {
                    new ParameterizedTypeImpl((Type)DisposableSupplier.class, new Type[] { contract });
                    final ParameterizedTypeImpl parameterizedTypeImpl2;
                    supplierBuilder.addContractType((Type)parameterizedTypeImpl2);
                }
                return;
            });
            supplierBuilder.setName(binding.getName());
            binding.getQualifiers().forEach(supplierBuilder::addQualifierAnnotation);
            binder.bind((Descriptor)supplierBuilder);
            final ServiceBindingBuilder<?> builder = (ServiceBindingBuilder<?>)binder.bindFactory((Factory)new InstanceSupplierFactoryBridge((Supplier<Object>)supplier, disposable));
            setupSupplierFactoryBridge((Binding<?, ?>)binding, builder);
            return;
        };
        ServiceLocatorUtilities.bind(locator, new org.glassfish.hk2.utilities.Binder[] { createBinder(bindConsumer) });
    }
    
    private static void bindSupplierClassBinding(final ServiceLocator locator, final SupplierClassBinding<?> binding) {
        final Consumer<AbstractBinder> bindConsumer = binder -> {
            final boolean disposable = DisposableSupplier.class.isAssignableFrom(binding.getSupplierClass());
            final ServiceBindingBuilder<?> supplierBuilder = (ServiceBindingBuilder<?>)binder.bind(binding.getSupplierClass());
            Type contract = null;
            binding.getContracts().forEach(contract -> {
                new ParameterizedTypeImpl((Type)Supplier.class, new Type[] { contract });
                final ParameterizedTypeImpl parameterizedTypeImpl;
                supplierBuilder.to((Type)parameterizedTypeImpl);
                if (disposable) {
                    new ParameterizedTypeImpl((Type)DisposableSupplier.class, new Type[] { contract });
                    final ParameterizedTypeImpl parameterizedTypeImpl2;
                    supplierBuilder.to((Type)parameterizedTypeImpl2);
                }
                return;
            });
            binding.getQualifiers().forEach(supplierBuilder::qualifiedBy);
            supplierBuilder.named(binding.getName());
            supplierBuilder.in((Class)transformScope(binding.getSupplierScope()));
            binder.bind((Object)supplierBuilder);
            contract = null;
            if (binding.getContracts().iterator().hasNext()) {
                contract = binding.getContracts().iterator().next();
            }
            final ServiceBindingBuilder<?> builder = (ServiceBindingBuilder<?>)binder.bindFactory((Factory)new SupplierFactoryBridge(locator, contract, binding.getName(), disposable));
            setupSupplierFactoryBridge((Binding<?, ?>)binding, builder);
            if (binding.getImplementationType() != null) {
                builder.asType((Type)binding.getImplementationType());
            }
            return;
        };
        ServiceLocatorUtilities.bind(locator, new org.glassfish.hk2.utilities.Binder[] { createBinder(bindConsumer) });
    }
    
    private static void setupSupplierFactoryBridge(final Binding<?, ?> binding, final ServiceBindingBuilder<?> builder) {
        builder.named(binding.getName());
        binding.getContracts().forEach(builder::to);
        binding.getQualifiers().forEach(builder::qualifiedBy);
        builder.in((Class)transformScope(binding.getScope()));
        if (binding.getRank() != null) {
            builder.ranked((int)binding.getRank());
        }
        if (binding.isProxiable() != null) {
            builder.proxy((boolean)binding.isProxiable());
        }
        if (binding.isProxiedForSameScope() != null) {
            builder.proxyForSameScope((boolean)binding.isProxiedForSameScope());
        }
    }
    
    static ActiveDescriptor<?> translateToActiveDescriptor(final ClassBinding<?> desc) {
        final ActiveDescriptorBuilder binding = BuilderHelper.activeLink(desc.getService()).named(desc.getName()).analyzeWith(desc.getAnalyzer());
        if (desc.getScope() != null) {
            binding.in((Class)transformScope(desc.getScope()));
        }
        if (desc.getRank() != null) {
            binding.ofRank((int)desc.getRank());
        }
        for (final Annotation annotation : desc.getQualifiers()) {
            binding.qualifiedBy(annotation);
        }
        for (final Type contract : desc.getContracts()) {
            binding.to(contract);
        }
        if (desc.isProxiable() != null) {
            binding.proxy((boolean)desc.isProxiable());
        }
        if (desc.isProxiedForSameScope() != null) {
            binding.proxyForSameScope((boolean)desc.isProxiedForSameScope());
        }
        if (desc.getImplementationType() != null) {
            binding.asType((Type)desc.getImplementationType());
        }
        return (ActiveDescriptor<?>)binding.build();
    }
    
    private static void bindBinding(final ServiceLocator locator, final DynamicConfiguration dc, final ActiveDescriptor<?> activeDescriptor, final Set<AliasBinding> aliases) {
        final ActiveDescriptor<Object> boundDescriptor = (ActiveDescriptor<Object>)dc.bind((Descriptor)activeDescriptor);
        for (final AliasBinding alias : aliases) {
            dc.bind((Descriptor)createAlias(locator, boundDescriptor, alias));
        }
    }
    
    static ActiveDescriptor<?> translateToActiveDescriptor(final InstanceBinding<?> desc, final Type... contracts) {
        AbstractActiveDescriptor<?> binding;
        if (contracts.length == 0) {
            binding = (AbstractActiveDescriptor<?>)BuilderHelper.createConstantDescriptor(desc.getService());
        }
        else {
            binding = (AbstractActiveDescriptor<?>)BuilderHelper.createConstantDescriptor(desc.getService(), (String)null, contracts);
        }
        binding.setName(desc.getName());
        binding.setClassAnalysisName(desc.getAnalyzer());
        if (desc.getScope() != null) {
            binding.setScope(desc.getScope().getName());
        }
        if (desc.getRank() != null) {
            binding.setRanking((int)desc.getRank());
        }
        for (final Annotation annotation : desc.getQualifiers()) {
            binding.addQualifierAnnotation(annotation);
        }
        for (final Type contract : desc.getContracts()) {
            binding.addContractType(contract);
        }
        if (desc.isProxiable() != null) {
            binding.setProxiable(desc.isProxiable());
        }
        if (desc.isProxiedForSameScope() != null) {
            binding.setProxyForSameScope(desc.isProxiedForSameScope());
        }
        return (ActiveDescriptor<?>)binding;
    }
    
    private static ActiveDescriptor<?> translateToActiveDescriptor(final InjectionResolverBinding<?> desc) {
        final ParameterizedTypeImpl parameterizedType = new ParameterizedTypeImpl((Type)InjectionResolver.class, new Type[] { desc.getResolver().getAnnotation() });
        return (ActiveDescriptor<?>)BuilderHelper.createConstantDescriptor((Object)desc.getResolver(), (String)null, new Type[] { (Type)parameterizedType });
    }
    
    private static AliasDescriptor<?> createAlias(final ServiceLocator locator, final ActiveDescriptor<?> descriptor, final AliasBinding alias) {
        final AliasDescriptor<?> hk2Alias = (AliasDescriptor<?>)new AliasDescriptor(locator, (ActiveDescriptor)descriptor, alias.getContract().getName(), (String)null);
        alias.getQualifiers().forEach(hk2Alias::addQualifierAnnotation);
        alias.getScope().ifPresent(hk2Alias::setScope);
        alias.getRank().ifPresent(hk2Alias::setRanking);
        return hk2Alias;
    }
    
    private static org.glassfish.hk2.utilities.Binder createBinder(final Consumer<AbstractBinder> bindConsumer) {
        return (org.glassfish.hk2.utilities.Binder)new AbstractBinder() {
            protected void configure() {
                bindConsumer.accept(this);
            }
        };
    }
    
    private static Class<? extends Annotation> transformScope(final Class<? extends Annotation> scope) {
        if (scope == PerLookup.class) {
            return (Class<? extends Annotation>)org.glassfish.hk2.api.PerLookup.class;
        }
        if (scope == PerThread.class) {
            return (Class<? extends Annotation>)org.glassfish.hk2.api.PerThread.class;
        }
        return scope;
    }
}
