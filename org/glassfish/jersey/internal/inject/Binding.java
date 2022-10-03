package org.glassfish.jersey.internal.inject;

import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import java.util.Collection;
import java.util.HashSet;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

public abstract class Binding<T, D extends Binding>
{
    private final Set<Type> contracts;
    private final Set<Annotation> qualifiers;
    private final Set<AliasBinding> aliases;
    private Class<? extends Annotation> scope;
    private String name;
    private Class<T> implementationType;
    private String analyzer;
    private Boolean proxiable;
    private Boolean proxyForSameScope;
    private Integer ranked;
    
    public Binding() {
        this.contracts = new HashSet<Type>();
        this.qualifiers = new HashSet<Annotation>();
        this.aliases = new HashSet<AliasBinding>();
        this.scope = null;
        this.name = null;
        this.implementationType = null;
        this.analyzer = null;
        this.proxiable = null;
        this.proxyForSameScope = null;
        this.ranked = null;
    }
    
    public Boolean isProxiable() {
        return this.proxiable;
    }
    
    public Boolean isProxiedForSameScope() {
        return this.proxyForSameScope;
    }
    
    public Integer getRank() {
        return this.ranked;
    }
    
    public Set<Type> getContracts() {
        return this.contracts;
    }
    
    public Set<Annotation> getQualifiers() {
        return this.qualifiers;
    }
    
    public Class<? extends Annotation> getScope() {
        return this.scope;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Class<T> getImplementationType() {
        return this.implementationType;
    }
    
    public String getAnalyzer() {
        return this.analyzer;
    }
    
    public Set<AliasBinding> getAliases() {
        return this.aliases;
    }
    
    public D analyzeWith(final String analyzer) {
        this.analyzer = analyzer;
        return (D)this;
    }
    
    public D to(final Collection<Class<? super T>> contracts) {
        if (contracts != null) {
            this.contracts.addAll(contracts);
        }
        return (D)this;
    }
    
    public D to(final Class<? super T> contract) {
        this.contracts.add(contract);
        return (D)this;
    }
    
    public D to(final GenericType<?> contract) {
        this.contracts.add(contract.getType());
        return (D)this;
    }
    
    public D to(final Type contract) {
        this.contracts.add(contract);
        return (D)this;
    }
    
    public D qualifiedBy(final Annotation annotation) {
        if (Named.class.equals(annotation.annotationType())) {
            this.name = ((Named)annotation).value();
        }
        this.qualifiers.add(annotation);
        return (D)this;
    }
    
    public D in(final Class<? extends Annotation> scopeAnnotation) {
        this.scope = scopeAnnotation;
        return (D)this;
    }
    
    public D named(final String name) {
        this.name = name;
        return (D)this;
    }
    
    public AliasBinding addAlias(final Class<?> contract) {
        final AliasBinding alias = new AliasBinding(contract);
        this.aliases.add(alias);
        return alias;
    }
    
    public D proxy(final boolean proxiable) {
        this.proxiable = proxiable;
        return (D)this;
    }
    
    public D proxyForSameScope(final boolean proxyForSameScope) {
        this.proxyForSameScope = proxyForSameScope;
        return (D)this;
    }
    
    public void ranked(final int rank) {
        this.ranked = rank;
    }
    
    D asType(final Class type) {
        this.implementationType = type;
        return (D)this;
    }
}
