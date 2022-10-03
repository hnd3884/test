package org.glassfish.jersey.model;

import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Singleton;
import java.util.Iterator;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.HashMap;
import java.util.Collection;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.Map;

public final class ContractProvider implements Scoped, NameBound
{
    public static final int NO_PRIORITY = -1;
    private final Class<?> implementationClass;
    private final Map<Class<?>, Integer> contracts;
    private final int defaultPriority;
    private final Set<Class<? extends Annotation>> nameBindings;
    private final Class<? extends Annotation> scope;
    
    public static Builder builder(final Class<?> implementationClass) {
        return new Builder((Class)implementationClass);
    }
    
    public static Builder builder(final ContractProvider original) {
        return new Builder(original);
    }
    
    private ContractProvider(final Class<?> implementationClass, final Class<? extends Annotation> scope, final Map<Class<?>, Integer> contracts, final int defaultPriority, final Set<Class<? extends Annotation>> nameBindings) {
        this.implementationClass = implementationClass;
        this.scope = scope;
        this.contracts = contracts;
        this.defaultPriority = defaultPriority;
        this.nameBindings = nameBindings;
    }
    
    @Override
    public Class<? extends Annotation> getScope() {
        return this.scope;
    }
    
    public Class<?> getImplementationClass() {
        return this.implementationClass;
    }
    
    public Set<Class<?>> getContracts() {
        return this.contracts.keySet();
    }
    
    public Map<Class<?>, Integer> getContractMap() {
        return this.contracts;
    }
    
    @Override
    public boolean isNameBound() {
        return !this.nameBindings.isEmpty();
    }
    
    public int getPriority(final Class<?> contract) {
        if (this.contracts.containsKey(contract)) {
            return this.contracts.get(contract);
        }
        return this.defaultPriority;
    }
    
    @Override
    public Set<Class<? extends Annotation>> getNameBindings() {
        return this.nameBindings;
    }
    
    public static final class Builder
    {
        private static final ContractProvider EMPTY_MODEL;
        private Class<?> implementationClass;
        private Class<? extends Annotation> scope;
        private Map<Class<?>, Integer> contracts;
        private int defaultPriority;
        private Set<Class<? extends Annotation>> nameBindings;
        
        private Builder(final Class<?> implementationClass) {
            this.implementationClass = null;
            this.scope = null;
            this.contracts = new HashMap<Class<?>, Integer>();
            this.defaultPriority = -1;
            this.nameBindings = Collections.newSetFromMap(new IdentityHashMap<Class<? extends Annotation>, Boolean>());
            this.implementationClass = implementationClass;
        }
        
        private Builder(final ContractProvider original) {
            this.implementationClass = null;
            this.scope = null;
            this.contracts = new HashMap<Class<?>, Integer>();
            this.defaultPriority = -1;
            this.nameBindings = Collections.newSetFromMap(new IdentityHashMap<Class<? extends Annotation>, Boolean>());
            this.implementationClass = original.implementationClass;
            this.scope = original.scope;
            this.contracts.putAll(original.contracts);
            this.defaultPriority = original.defaultPriority;
            this.nameBindings.addAll(original.nameBindings);
        }
        
        public Builder scope(final Class<? extends Annotation> scope) {
            this.scope = scope;
            return this;
        }
        
        public Builder addContract(final Class<?> contract) {
            return this.addContract(contract, this.defaultPriority);
        }
        
        public Builder addContract(final Class<?> contract, final int priority) {
            this.contracts.put(contract, priority);
            return this;
        }
        
        public Builder addContracts(final Map<Class<?>, Integer> contracts) {
            this.contracts.putAll(contracts);
            return this;
        }
        
        public Builder addContracts(final Collection<Class<?>> contracts) {
            for (final Class<?> contract : contracts) {
                this.addContract(contract, this.defaultPriority);
            }
            return this;
        }
        
        public Builder defaultPriority(final int defaultPriority) {
            this.defaultPriority = defaultPriority;
            return this;
        }
        
        public Builder addNameBinding(final Class<? extends Annotation> binding) {
            this.nameBindings.add(binding);
            return this;
        }
        
        public Class<? extends Annotation> getScope() {
            return this.scope;
        }
        
        public Map<Class<?>, Integer> getContracts() {
            return this.contracts;
        }
        
        public int getDefaultPriority() {
            return this.defaultPriority;
        }
        
        public Set<Class<? extends Annotation>> getNameBindings() {
            return this.nameBindings;
        }
        
        public ContractProvider build() {
            if (this.scope == null) {
                this.scope = (Class<? extends Annotation>)Singleton.class;
            }
            final Map<Class<?>, Integer> _contracts = (Map<Class<?>, Integer>)(this.contracts.isEmpty() ? Collections.emptyMap() : this.contracts.entrySet().stream().collect(Collectors.toMap((Function<? super Object, ?>)Map.Entry::getKey, classIntegerEntry -> {
                final Integer priority = classIntegerEntry.getValue();
                return Integer.valueOf((priority != -1) ? priority : this.defaultPriority);
            })));
            final Set<Class<? extends Annotation>> bindings = this.nameBindings.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet((Set<? extends Class<? extends Annotation>>)this.nameBindings);
            if (this.implementationClass == null && this.scope == Singleton.class && _contracts.isEmpty() && this.defaultPriority == -1 && bindings.isEmpty()) {
                return Builder.EMPTY_MODEL;
            }
            return new ContractProvider(this.implementationClass, this.scope, _contracts, this.defaultPriority, bindings, null);
        }
        
        static {
            EMPTY_MODEL = new ContractProvider(null, Singleton.class, Collections.emptyMap(), -1, Collections.emptySet(), null);
        }
    }
}
