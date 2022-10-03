package org.glassfish.hk2.internal;

import org.glassfish.hk2.api.ServiceHandle;
import java.util.Map;
import java.util.Set;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import java.util.Collections;
import javax.inject.Named;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.HK2Loader;
import java.util.List;
import java.util.HashMap;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import org.glassfish.hk2.utilities.ActiveDescriptorBuilder;

public class ActiveDescriptorBuilderImpl implements ActiveDescriptorBuilder
{
    private String name;
    private final HashSet<Type> contracts;
    private Annotation scopeAnnotation;
    private Class<? extends Annotation> scope;
    private final HashSet<Annotation> qualifiers;
    private final HashMap<String, List<String>> metadatas;
    private final Class<?> implementation;
    private HK2Loader loader;
    private int rank;
    private Boolean proxy;
    private Boolean proxyForSameScope;
    private DescriptorVisibility visibility;
    private String classAnalysisName;
    private Type implementationType;
    
    public ActiveDescriptorBuilderImpl(final Class<?> implementation) {
        this.contracts = new HashSet<Type>();
        this.scopeAnnotation = null;
        this.scope = PerLookup.class;
        this.qualifiers = new HashSet<Annotation>();
        this.metadatas = new HashMap<String, List<String>>();
        this.loader = null;
        this.rank = 0;
        this.proxy = null;
        this.proxyForSameScope = null;
        this.visibility = DescriptorVisibility.NORMAL;
        this.classAnalysisName = null;
        this.implementation = implementation;
    }
    
    @Override
    public ActiveDescriptorBuilder named(final String name) throws IllegalArgumentException {
        this.name = name;
        return this;
    }
    
    @Override
    public ActiveDescriptorBuilder to(final Type contract) throws IllegalArgumentException {
        if (contract != null) {
            this.contracts.add(contract);
        }
        return this;
    }
    
    @Override
    public ActiveDescriptorBuilder in(final Annotation scopeAnnotation) throws IllegalArgumentException {
        if (scopeAnnotation == null) {
            throw new IllegalArgumentException();
        }
        this.scopeAnnotation = scopeAnnotation;
        this.scope = scopeAnnotation.annotationType();
        return this;
    }
    
    @Override
    public ActiveDescriptorBuilder in(final Class<? extends Annotation> scope) throws IllegalArgumentException {
        this.scope = scope;
        if (scope == null) {
            this.scopeAnnotation = null;
        }
        else if (this.scopeAnnotation != null && !scope.equals(this.scopeAnnotation.annotationType())) {
            throw new IllegalArgumentException("Scope set to different class (" + scope.getName() + ") from the scope annotation (" + this.scopeAnnotation.annotationType().getName());
        }
        return this;
    }
    
    @Override
    public ActiveDescriptorBuilder qualifiedBy(final Annotation annotation) throws IllegalArgumentException {
        if (annotation != null) {
            if (Named.class.equals(annotation.annotationType())) {
                this.name = ((Named)annotation).value();
            }
            this.qualifiers.add(annotation);
        }
        return this;
    }
    
    @Override
    public ActiveDescriptorBuilder has(final String key, final String value) throws IllegalArgumentException {
        return this.has(key, Collections.singletonList(value));
    }
    
    @Override
    public ActiveDescriptorBuilder has(final String key, final List<String> values) throws IllegalArgumentException {
        if (key == null || values == null || values.size() <= 0) {
            throw new IllegalArgumentException();
        }
        this.metadatas.put(key, values);
        return this;
    }
    
    @Override
    public ActiveDescriptorBuilder ofRank(final int rank) {
        this.rank = rank;
        return this;
    }
    
    @Override
    public ActiveDescriptorBuilder proxy() {
        return this.proxy(true);
    }
    
    @Override
    public ActiveDescriptorBuilder proxy(final boolean forceProxy) {
        if (forceProxy) {
            this.proxy = Boolean.TRUE;
        }
        else {
            this.proxy = Boolean.FALSE;
        }
        return this;
    }
    
    @Override
    public ActiveDescriptorBuilder proxyForSameScope() {
        return this.proxy(true);
    }
    
    @Override
    public ActiveDescriptorBuilder proxyForSameScope(final boolean forceProxyForSameScope) {
        if (forceProxyForSameScope) {
            this.proxyForSameScope = Boolean.TRUE;
        }
        else {
            this.proxyForSameScope = Boolean.FALSE;
        }
        return this;
    }
    
    @Override
    public ActiveDescriptorBuilder andLoadWith(final HK2Loader loader) throws IllegalArgumentException {
        this.loader = loader;
        return this;
    }
    
    @Override
    public ActiveDescriptorBuilder analyzeWith(final String serviceName) {
        this.classAnalysisName = serviceName;
        return this;
    }
    
    @Override
    public ActiveDescriptorBuilder localOnly() {
        this.visibility = DescriptorVisibility.LOCAL;
        return this;
    }
    
    @Override
    public ActiveDescriptorBuilder visibility(final DescriptorVisibility visibility) {
        if (visibility == null) {
            throw new IllegalArgumentException();
        }
        this.visibility = visibility;
        return this;
    }
    
    @Override
    public ActiveDescriptorBuilder asType(final Type t) {
        if (t == null) {
            throw new IllegalArgumentException();
        }
        this.implementationType = t;
        return this;
    }
    
    @Override
    public <T> AbstractActiveDescriptor<T> build() throws IllegalArgumentException {
        return new BuiltActiveDescriptor<T>((Class)this.implementation, (Set)this.contracts, this.scopeAnnotation, (Class)this.scope, this.name, (Set)this.qualifiers, DescriptorType.CLASS, this.visibility, this.rank, this.proxy, this.proxyForSameScope, this.classAnalysisName, (Map)this.metadatas, this.loader, this.implementationType);
    }
    
    @Deprecated
    @Override
    public <T> AbstractActiveDescriptor<T> buildFactory() throws IllegalArgumentException {
        return (AbstractActiveDescriptor<T>)this.buildProvideMethod();
    }
    
    @Override
    public <T> AbstractActiveDescriptor<T> buildProvideMethod() throws IllegalArgumentException {
        return new BuiltActiveDescriptor<T>((Class)this.implementation, (Set)this.contracts, this.scopeAnnotation, (Class)this.scope, this.name, (Set)this.qualifiers, DescriptorType.PROVIDE_METHOD, this.visibility, this.rank, this.proxy, this.proxyForSameScope, this.classAnalysisName, (Map)this.metadatas, this.loader, this.implementationType);
    }
    
    private static class BuiltActiveDescriptor<T> extends AbstractActiveDescriptor<T>
    {
        private static final long serialVersionUID = 2434137639270026082L;
        private Class<?> implementationClass;
        private Type implementationType;
        
        public BuiltActiveDescriptor() {
        }
        
        private BuiltActiveDescriptor(final Class<?> implementationClass, final Set<Type> advertisedContracts, final Annotation scopeAnnotation, final Class<? extends Annotation> scope, final String name, final Set<Annotation> qualifiers, final DescriptorType descriptorType, final DescriptorVisibility descriptorVisibility, final int ranking, final Boolean proxy, final Boolean proxyForSameScope, final String classAnalysisName, final Map<String, List<String>> metadata, final HK2Loader loader, Type implementationType) {
            super(advertisedContracts, scope, name, qualifiers, descriptorType, descriptorVisibility, ranking, proxy, proxyForSameScope, classAnalysisName, metadata);
            super.setReified(false);
            super.setLoader(loader);
            super.setScopeAsAnnotation(scopeAnnotation);
            this.implementationClass = implementationClass;
            super.setImplementation(implementationClass.getName());
            if (implementationType == null) {
                implementationType = implementationClass;
            }
            this.implementationType = implementationType;
        }
        
        @Override
        public Class<?> getImplementationClass() {
            return this.implementationClass;
        }
        
        @Override
        public Type getImplementationType() {
            return this.implementationType;
        }
        
        @Override
        public T create(final ServiceHandle<?> root) {
            throw new AssertionError((Object)"Should not be called directly");
        }
        
        @Override
        public void setImplementationType(final Type t) {
            this.implementationType = t;
        }
    }
}
