package org.glassfish.hk2.internal;

import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.utilities.FactoryDescriptorsImpl;
import java.util.Collection;
import java.util.Collections;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.FactoryDescriptors;
import java.util.Map;
import java.util.Set;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.utilities.DescriptorImpl;
import java.util.LinkedList;
import java.lang.annotation.Annotation;
import javax.inject.Named;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.HK2Loader;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import org.glassfish.hk2.utilities.DescriptorBuilder;

public class DescriptorBuilderImpl implements DescriptorBuilder
{
    private String name;
    private final HashSet<String> contracts;
    private String scope;
    private final HashSet<String> qualifiers;
    private final HashMap<String, List<String>> metadatas;
    private String implementation;
    private HK2Loader loader;
    private int rank;
    private Boolean proxy;
    private Boolean proxyForSameScope;
    private DescriptorVisibility visibility;
    private String analysisName;
    
    public DescriptorBuilderImpl() {
        this.contracts = new HashSet<String>();
        this.qualifiers = new HashSet<String>();
        this.metadatas = new HashMap<String, List<String>>();
        this.loader = null;
        this.rank = 0;
        this.proxy = null;
        this.proxyForSameScope = null;
        this.visibility = DescriptorVisibility.NORMAL;
        this.analysisName = null;
    }
    
    public DescriptorBuilderImpl(final String implementation, final boolean addToContracts) {
        this.contracts = new HashSet<String>();
        this.qualifiers = new HashSet<String>();
        this.metadatas = new HashMap<String, List<String>>();
        this.loader = null;
        this.rank = 0;
        this.proxy = null;
        this.proxyForSameScope = null;
        this.visibility = DescriptorVisibility.NORMAL;
        this.analysisName = null;
        this.implementation = implementation;
        if (addToContracts) {
            this.contracts.add(implementation);
        }
    }
    
    @Override
    public DescriptorBuilder named(final String name) throws IllegalArgumentException {
        if (this.name != null) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.qualifiers.add(Named.class.getName());
        return this;
    }
    
    @Override
    public DescriptorBuilder to(final Class<?> contract) throws IllegalArgumentException {
        if (contract == null) {
            throw new IllegalArgumentException();
        }
        return this.to(contract.getName());
    }
    
    @Override
    public DescriptorBuilder to(final String contract) throws IllegalArgumentException {
        if (contract == null) {
            throw new IllegalArgumentException();
        }
        this.contracts.add(contract);
        return this;
    }
    
    @Override
    public DescriptorBuilder in(final Class<? extends Annotation> scope) throws IllegalArgumentException {
        if (scope == null) {
            throw new IllegalArgumentException();
        }
        return this.in(scope.getName());
    }
    
    @Override
    public DescriptorBuilder in(final String scope) throws IllegalArgumentException {
        if (scope == null) {
            throw new IllegalArgumentException();
        }
        this.scope = scope;
        return this;
    }
    
    @Override
    public DescriptorBuilder qualifiedBy(final Annotation annotation) throws IllegalArgumentException {
        if (annotation == null) {
            throw new IllegalArgumentException();
        }
        if (Named.class.equals(annotation.annotationType())) {
            this.name = ((Named)annotation).value();
        }
        return this.qualifiedBy(annotation.annotationType().getName());
    }
    
    @Override
    public DescriptorBuilder qualifiedBy(final String annotation) throws IllegalArgumentException {
        if (annotation == null) {
            throw new IllegalArgumentException();
        }
        this.qualifiers.add(annotation);
        return this;
    }
    
    @Override
    public DescriptorBuilder has(final String key, final String value) throws IllegalArgumentException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        final LinkedList<String> values = new LinkedList<String>();
        values.add(value);
        return this.has(key, values);
    }
    
    @Override
    public DescriptorBuilder has(final String key, final List<String> values) throws IllegalArgumentException {
        if (key == null || values == null || values.size() <= 0) {
            throw new IllegalArgumentException();
        }
        this.metadatas.put(key, values);
        return this;
    }
    
    @Override
    public DescriptorBuilder ofRank(final int rank) {
        this.rank = rank;
        return this;
    }
    
    @Override
    public DescriptorBuilder proxy() {
        return this.proxy(true);
    }
    
    @Override
    public DescriptorBuilder proxy(final boolean forceProxy) {
        if (forceProxy) {
            this.proxy = Boolean.TRUE;
        }
        else {
            this.proxy = Boolean.FALSE;
        }
        return this;
    }
    
    @Override
    public DescriptorBuilder proxyForSameScope() {
        return this.proxyForSameScope(true);
    }
    
    @Override
    public DescriptorBuilder proxyForSameScope(final boolean proxyForSameScope) {
        if (proxyForSameScope) {
            this.proxyForSameScope = Boolean.TRUE;
        }
        else {
            this.proxyForSameScope = Boolean.FALSE;
        }
        return this;
    }
    
    @Override
    public DescriptorBuilder localOnly() {
        this.visibility = DescriptorVisibility.LOCAL;
        return this;
    }
    
    @Override
    public DescriptorBuilder visibility(final DescriptorVisibility visibility) {
        if (visibility == null) {
            throw new IllegalArgumentException();
        }
        this.visibility = visibility;
        return this;
    }
    
    @Override
    public DescriptorBuilder andLoadWith(final HK2Loader loader) throws IllegalArgumentException {
        if (this.loader != null) {
            throw new IllegalArgumentException();
        }
        this.loader = loader;
        return this;
    }
    
    @Override
    public DescriptorBuilder analyzeWith(final String serviceName) {
        this.analysisName = serviceName;
        return this;
    }
    
    @Override
    public DescriptorImpl build() throws IllegalArgumentException {
        return new DescriptorImpl(this.contracts, this.name, this.scope, this.implementation, this.metadatas, this.qualifiers, DescriptorType.CLASS, this.visibility, this.loader, this.rank, this.proxy, this.proxyForSameScope, this.analysisName, null, null);
    }
    
    @Override
    public FactoryDescriptors buildFactory(final String factoryScope) throws IllegalArgumentException {
        final Set<String> factoryContracts = new HashSet<String>();
        factoryContracts.add(this.implementation);
        factoryContracts.add(Factory.class.getName());
        final Set<String> factoryQualifiers = Collections.emptySet();
        final Map<String, List<String>> factoryMetadata = Collections.emptyMap();
        final DescriptorImpl asService = new DescriptorImpl(factoryContracts, null, factoryScope, this.implementation, factoryMetadata, factoryQualifiers, DescriptorType.CLASS, DescriptorVisibility.NORMAL, this.loader, this.rank, null, null, this.analysisName, null, null);
        final Set<String> serviceContracts = new HashSet<String>(this.contracts);
        if (this.implementation != null) {
            serviceContracts.remove(this.implementation);
        }
        final DescriptorImpl asFactory = new DescriptorImpl(serviceContracts, this.name, this.scope, this.implementation, this.metadatas, this.qualifiers, DescriptorType.PROVIDE_METHOD, this.visibility, this.loader, this.rank, this.proxy, this.proxyForSameScope, null, null, null);
        return new FactoryDescriptorsImpl(asService, asFactory);
    }
    
    @Override
    public FactoryDescriptors buildFactory() throws IllegalArgumentException {
        return this.buildFactory(PerLookup.class.getName());
    }
    
    @Override
    public FactoryDescriptors buildFactory(Class<? extends Annotation> factoryScope) throws IllegalArgumentException {
        if (factoryScope == null) {
            factoryScope = PerLookup.class;
        }
        return this.buildFactory(factoryScope.getName());
    }
}
