package org.glassfish.hk2.utilities.binding;

import org.glassfish.hk2.api.FactoryDescriptors;
import org.glassfish.hk2.utilities.FactoryDescriptorsImpl;
import org.glassfish.hk2.utilities.reflection.ParameterizedTypeImpl;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.ActiveDescriptorBuilder;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import org.glassfish.hk2.api.DynamicConfiguration;
import javax.inject.Named;
import java.util.Iterator;
import java.util.List;
import org.glassfish.hk2.api.TypeLiteral;
import java.util.HashSet;
import java.lang.annotation.Annotation;
import org.jvnet.hk2.component.MultiMap;
import org.glassfish.hk2.api.HK2Loader;
import java.lang.reflect.Type;
import java.util.Set;

abstract class AbstractBindingBuilder<T> implements ServiceBindingBuilder<T>, NamedBindingBuilder<T>, ScopedBindingBuilder<T>, ScopedNamedBindingBuilder<T>
{
    Set<Type> contracts;
    HK2Loader loader;
    final MultiMap<String, String> metadata;
    Set<Annotation> qualifiers;
    Annotation scopeAnnotation;
    Class<? extends Annotation> scope;
    Integer ranked;
    String name;
    Boolean proxiable;
    Boolean proxyForSameScope;
    Type implementationType;
    String analyzer;
    
    AbstractBindingBuilder() {
        this.contracts = new HashSet<Type>();
        this.loader = null;
        this.metadata = (MultiMap<String, String>)new MultiMap();
        this.qualifiers = new HashSet<Annotation>();
        this.scopeAnnotation = null;
        this.scope = null;
        this.ranked = null;
        this.name = null;
        this.proxiable = null;
        this.proxyForSameScope = null;
        this.implementationType = null;
        this.analyzer = null;
    }
    
    @Override
    public AbstractBindingBuilder<T> proxy(final boolean proxiable) {
        this.proxiable = proxiable;
        return this;
    }
    
    @Override
    public AbstractBindingBuilder<T> proxyForSameScope(final boolean proxyForSameScope) {
        this.proxyForSameScope = proxyForSameScope;
        return this;
    }
    
    @Override
    public AbstractBindingBuilder<T> analyzeWith(final String analyzer) {
        this.analyzer = analyzer;
        return this;
    }
    
    @Override
    public AbstractBindingBuilder<T> to(final Class<? super T> contract) {
        this.contracts.add(contract);
        return this;
    }
    
    @Override
    public AbstractBindingBuilder<T> to(final TypeLiteral<?> contract) {
        this.contracts.add(contract.getType());
        return this;
    }
    
    @Override
    public AbstractBindingBuilder<T> to(final Type contract) {
        this.contracts.add(contract);
        return this;
    }
    
    @Override
    public AbstractBindingBuilder<T> loadedBy(final HK2Loader loader) {
        this.loader = loader;
        return this;
    }
    
    @Override
    public AbstractBindingBuilder<T> withMetadata(final String key, final String value) {
        this.metadata.add((Object)key, (Object)value);
        return this;
    }
    
    @Override
    public AbstractBindingBuilder<T> withMetadata(final String key, final List<String> values) {
        for (final String value : values) {
            this.metadata.add((Object)key, (Object)value);
        }
        return this;
    }
    
    @Override
    public AbstractBindingBuilder<T> qualifiedBy(final Annotation annotation) {
        if (Named.class.equals(annotation.annotationType())) {
            this.name = ((Named)annotation).value();
        }
        this.qualifiers.add(annotation);
        return this;
    }
    
    @Override
    public AbstractBindingBuilder<T> in(final Annotation scopeAnnotation) {
        this.scopeAnnotation = scopeAnnotation;
        return this;
    }
    
    @Override
    public AbstractBindingBuilder<T> in(final Class<? extends Annotation> scopeAnnotation) {
        this.scope = scopeAnnotation;
        return this;
    }
    
    @Override
    public AbstractBindingBuilder<T> named(final String name) {
        this.name = name;
        return this;
    }
    
    @Override
    public void ranked(final int rank) {
        this.ranked = rank;
    }
    
    @Override
    public AbstractBindingBuilder<T> asType(final Type t) {
        this.implementationType = t;
        return this;
    }
    
    abstract void complete(final DynamicConfiguration p0, final HK2Loader p1);
    
    static <T> AbstractBindingBuilder<T> create(final Class<T> serviceType, final boolean bindAsContract) {
        return new ClassBasedBindingBuilder<T>(serviceType, bindAsContract ? serviceType : null);
    }
    
    static <T> AbstractBindingBuilder<T> create(final Type serviceType, final boolean bindAsContract) {
        return new ClassBasedBindingBuilder<T>(ReflectionHelper.getRawClass(serviceType), bindAsContract ? serviceType : null).asType(serviceType);
    }
    
    static <T> AbstractBindingBuilder<T> create(final TypeLiteral<T> serviceType, final boolean bindAsContract) {
        final Type type = serviceType.getType();
        return new ClassBasedBindingBuilder<T>(serviceType.getRawType(), bindAsContract ? serviceType.getType() : null).asType(type);
    }
    
    static <T> AbstractBindingBuilder<T> create(final T service) {
        return new InstanceBasedBindingBuilder<T>(service);
    }
    
    static <T> AbstractBindingBuilder<T> createFactoryBinder(final Factory<T> factory) {
        return new FactoryInstanceBasedBindingBuilder<T>(factory);
    }
    
    static <T> AbstractBindingBuilder<T> createFactoryBinder(final Class<? extends Factory<T>> factoryType, final Class<? extends Annotation> factoryScope) {
        return new FactoryTypeBasedBindingBuilder<T>(factoryType, factoryScope);
    }
    
    private static class ClassBasedBindingBuilder<T> extends AbstractBindingBuilder<T>
    {
        private final Class<T> service;
        
        public ClassBasedBindingBuilder(final Class<T> service, final Type serviceContractType) {
            this.service = service;
            if (serviceContractType != null) {
                super.contracts.add(serviceContractType);
            }
        }
        
        @Override
        void complete(final DynamicConfiguration configuration, final HK2Loader defaultLoader) {
            if (this.loader == null) {
                this.loader = defaultLoader;
            }
            final ActiveDescriptorBuilder builder = BuilderHelper.activeLink(this.service).named(this.name).andLoadWith(this.loader).analyzeWith(this.analyzer);
            if (this.scopeAnnotation != null) {
                builder.in(this.scopeAnnotation);
            }
            if (this.scope != null) {
                builder.in(this.scope);
            }
            if (this.ranked != null) {
                builder.ofRank(this.ranked);
            }
            for (final String key : this.metadata.keySet()) {
                for (final String value : this.metadata.get((Object)key)) {
                    builder.has(key, value);
                }
            }
            for (final Annotation annotation : this.qualifiers) {
                builder.qualifiedBy(annotation);
            }
            for (final Type contract : this.contracts) {
                builder.to(contract);
            }
            if (this.proxiable != null) {
                builder.proxy(this.proxiable);
            }
            if (this.proxyForSameScope != null) {
                builder.proxyForSameScope(this.proxyForSameScope);
            }
            if (this.implementationType != null) {
                builder.asType(this.implementationType);
            }
            configuration.bind(builder.build(), false);
        }
    }
    
    private static class InstanceBasedBindingBuilder<T> extends AbstractBindingBuilder<T>
    {
        private final T service;
        
        public InstanceBasedBindingBuilder(final T service) {
            if (service == null) {
                throw new IllegalArgumentException();
            }
            this.service = service;
        }
        
        @Override
        void complete(final DynamicConfiguration configuration, final HK2Loader defaultLoader) {
            if (this.loader == null) {
                this.loader = defaultLoader;
            }
            final AbstractActiveDescriptor<?> descriptor = BuilderHelper.createConstantDescriptor((Object)this.service);
            descriptor.setName(this.name);
            descriptor.setLoader(this.loader);
            descriptor.setClassAnalysisName(this.analyzer);
            if (this.scope != null) {
                descriptor.setScope(this.scope.getName());
            }
            if (this.ranked != null) {
                descriptor.setRanking(this.ranked);
            }
            for (final String key : this.metadata.keySet()) {
                for (final String value : this.metadata.get((Object)key)) {
                    descriptor.addMetadata(key, value);
                }
            }
            for (final Annotation annotation : this.qualifiers) {
                descriptor.addQualifierAnnotation(annotation);
            }
            for (final Type contract : this.contracts) {
                descriptor.addContractType(contract);
            }
            if (this.proxiable != null) {
                descriptor.setProxiable(this.proxiable);
            }
            if (this.proxyForSameScope != null) {
                descriptor.setProxyForSameScope(this.proxyForSameScope);
            }
            configuration.bind(descriptor, false);
        }
    }
    
    private static class FactoryInstanceBasedBindingBuilder<T> extends AbstractBindingBuilder<T>
    {
        private final Factory<T> factory;
        
        public FactoryInstanceBasedBindingBuilder(final Factory<T> factory) {
            this.factory = factory;
        }
        
        @Override
        void complete(final DynamicConfiguration configuration, final HK2Loader defaultLoader) {
            if (this.loader == null) {
                this.loader = defaultLoader;
            }
            final AbstractActiveDescriptor<?> factoryContractDescriptor = BuilderHelper.createConstantDescriptor((Object)this.factory);
            factoryContractDescriptor.addContractType(this.factory.getClass());
            factoryContractDescriptor.setLoader(this.loader);
            final ActiveDescriptorBuilder descriptorBuilder = BuilderHelper.activeLink(this.factory.getClass()).named(this.name).andLoadWith(this.loader).analyzeWith(this.analyzer);
            if (this.scope != null) {
                descriptorBuilder.in(this.scope);
            }
            if (this.ranked != null) {
                descriptorBuilder.ofRank(this.ranked);
            }
            for (final Annotation qualifier : this.qualifiers) {
                factoryContractDescriptor.addQualifierAnnotation(qualifier);
                descriptorBuilder.qualifiedBy(qualifier);
            }
            for (final Type contract : this.contracts) {
                factoryContractDescriptor.addContractType((Type)new ParameterizedTypeImpl((Type)Factory.class, new Type[] { contract }));
                descriptorBuilder.to(contract);
            }
            final Set<String> keys = this.metadata.keySet();
            for (final String key : keys) {
                final List<String> values = this.metadata.get((Object)key);
                for (final String value : values) {
                    factoryContractDescriptor.addMetadata(key, value);
                    descriptorBuilder.has(key, value);
                }
            }
            if (this.proxiable != null) {
                descriptorBuilder.proxy(this.proxiable);
            }
            if (this.proxyForSameScope != null) {
                descriptorBuilder.proxyForSameScope(this.proxyForSameScope);
            }
            configuration.bind(new FactoryDescriptorsImpl(factoryContractDescriptor, descriptorBuilder.buildProvideMethod()));
        }
    }
    
    private static class FactoryTypeBasedBindingBuilder<T> extends AbstractBindingBuilder<T>
    {
        private final Class<? extends Factory<T>> factoryClass;
        private final Class<? extends Annotation> factoryScope;
        
        public FactoryTypeBasedBindingBuilder(final Class<? extends Factory<T>> factoryClass, final Class<? extends Annotation> factoryScope) {
            this.factoryClass = factoryClass;
            this.factoryScope = factoryScope;
        }
        
        @Override
        void complete(final DynamicConfiguration configuration, final HK2Loader defaultLoader) {
            if (this.loader == null) {
                this.loader = defaultLoader;
            }
            final ActiveDescriptorBuilder factoryDescriptorBuilder = BuilderHelper.activeLink(this.factoryClass).named(this.name).andLoadWith(this.loader).analyzeWith(this.analyzer);
            if (this.factoryScope != null) {
                factoryDescriptorBuilder.in(this.factoryScope);
            }
            final ActiveDescriptorBuilder descriptorBuilder = BuilderHelper.activeLink(this.factoryClass).named(this.name).andLoadWith(this.loader).analyzeWith(this.analyzer);
            if (this.scope != null) {
                descriptorBuilder.in(this.scope);
            }
            if (this.ranked != null) {
                descriptorBuilder.ofRank(this.ranked);
            }
            for (final Annotation qualifier : this.qualifiers) {
                factoryDescriptorBuilder.qualifiedBy(qualifier);
                descriptorBuilder.qualifiedBy(qualifier);
            }
            for (final Type contract : this.contracts) {
                factoryDescriptorBuilder.to((Type)new ParameterizedTypeImpl((Type)Factory.class, new Type[] { contract }));
                descriptorBuilder.to(contract);
            }
            final Set<String> keys = this.metadata.keySet();
            for (final String key : keys) {
                final List<String> values = this.metadata.get((Object)key);
                for (final String value : values) {
                    factoryDescriptorBuilder.has(key, value);
                    descriptorBuilder.has(key, value);
                }
            }
            if (this.proxiable != null) {
                descriptorBuilder.proxy(this.proxiable);
            }
            if (this.proxyForSameScope != null) {
                descriptorBuilder.proxyForSameScope(this.proxyForSameScope);
            }
            configuration.bind(new FactoryDescriptorsImpl(factoryDescriptorBuilder.build(), descriptorBuilder.buildProvideMethod()));
        }
    }
}
