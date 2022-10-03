package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.Injectee;
import java.util.Collections;
import javax.inject.Named;
import java.util.Iterator;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.Descriptor;
import java.util.LinkedHashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import java.util.Set;
import org.glassfish.hk2.api.ActiveDescriptor;

public abstract class AbstractActiveDescriptor<T> extends DescriptorImpl implements ActiveDescriptor<T>
{
    private static final long serialVersionUID = 7080312303893604939L;
    private static final Set<Annotation> EMPTY_QUALIFIER_SET;
    private Set<Type> advertisedContracts;
    private Annotation scopeAnnotation;
    private Class<? extends Annotation> scope;
    private Set<Annotation> qualifiers;
    private Long factoryServiceId;
    private Long factoryLocatorId;
    private boolean isReified;
    private transient boolean cacheSet;
    private transient T cachedValue;
    private final ReentrantReadWriteLock rwLock;
    private final Lock rLock;
    private final Lock wLock;
    
    public AbstractActiveDescriptor() {
        this.advertisedContracts = new LinkedHashSet<Type>();
        this.isReified = true;
        this.cacheSet = false;
        this.rwLock = new ReentrantReadWriteLock();
        this.rLock = this.rwLock.readLock();
        this.wLock = this.rwLock.writeLock();
        this.scope = null;
    }
    
    protected AbstractActiveDescriptor(final Descriptor baseDescriptor) {
        super(baseDescriptor);
        this.advertisedContracts = new LinkedHashSet<Type>();
        this.isReified = true;
        this.cacheSet = false;
        this.rwLock = new ReentrantReadWriteLock();
        this.rLock = this.rwLock.readLock();
        this.wLock = this.rwLock.writeLock();
        this.isReified = false;
        this.scope = null;
    }
    
    protected AbstractActiveDescriptor(final Set<Type> advertisedContracts, final Class<? extends Annotation> scope, final String name, final Set<Annotation> qualifiers, final DescriptorType descriptorType, final DescriptorVisibility descriptorVisibility, final int ranking, final Boolean proxy, final Boolean proxyForSameScope, final String analyzerName, final Map<String, List<String>> metadata) {
        this.advertisedContracts = new LinkedHashSet<Type>();
        this.isReified = true;
        this.cacheSet = false;
        this.rwLock = new ReentrantReadWriteLock();
        this.rLock = this.rwLock.readLock();
        this.wLock = this.rwLock.writeLock();
        this.scope = scope;
        this.advertisedContracts.addAll(advertisedContracts);
        if (qualifiers != null && !qualifiers.isEmpty()) {
            (this.qualifiers = new LinkedHashSet<Annotation>()).addAll(qualifiers);
        }
        this.setRanking(ranking);
        this.setDescriptorType(descriptorType);
        this.setDescriptorVisibility(descriptorVisibility);
        this.setName(name);
        this.setProxiable(proxy);
        this.setProxyForSameScope(proxyForSameScope);
        if (scope != null) {
            this.setScope(scope.getName());
        }
        for (final Type t : advertisedContracts) {
            final Class<?> raw = ReflectionHelper.getRawClass(t);
            if (raw == null) {
                continue;
            }
            this.addAdvertisedContract(raw.getName());
        }
        if (qualifiers != null) {
            for (final Annotation q : qualifiers) {
                this.addQualifier(q.annotationType().getName());
            }
        }
        this.setClassAnalysisName(analyzerName);
        if (metadata == null) {
            return;
        }
        for (final Map.Entry<String, List<String>> entry : metadata.entrySet()) {
            final String key = entry.getKey();
            final List<String> values = entry.getValue();
            for (final String value : values) {
                this.addMetadata(key, value);
            }
        }
    }
    
    private void removeNamedQualifier() {
        try {
            this.wLock.lock();
            if (this.qualifiers == null) {
                return;
            }
            for (final Annotation qualifier : this.qualifiers) {
                if (qualifier.annotationType().equals(Named.class)) {
                    this.removeQualifierAnnotation(qualifier);
                }
            }
        }
        finally {
            this.wLock.unlock();
        }
    }
    
    public void setImplementationType(final Type t) {
        throw new AssertionError((Object)("Can not set type of " + this.getClass().getName() + " descriptor"));
    }
    
    @Override
    public void setName(final String name) {
        try {
            this.wLock.lock();
            super.setName(name);
            this.removeNamedQualifier();
            if (name == null) {
                return;
            }
            this.addQualifierAnnotation(new NamedImpl(name));
        }
        finally {
            this.wLock.unlock();
        }
    }
    
    @Override
    public T getCache() {
        try {
            this.rLock.lock();
            return this.cachedValue;
        }
        finally {
            this.rLock.unlock();
        }
    }
    
    @Override
    public boolean isCacheSet() {
        try {
            this.rLock.lock();
            return this.cacheSet;
        }
        finally {
            this.rLock.unlock();
        }
    }
    
    @Override
    public void setCache(final T cacheMe) {
        try {
            this.wLock.lock();
            this.cachedValue = cacheMe;
            this.cacheSet = true;
        }
        finally {
            this.wLock.unlock();
        }
    }
    
    @Override
    public void releaseCache() {
        try {
            this.wLock.lock();
            this.cacheSet = false;
            this.cachedValue = null;
        }
        finally {
            this.wLock.unlock();
        }
    }
    
    @Override
    public boolean isReified() {
        try {
            this.rLock.lock();
            return this.isReified;
        }
        finally {
            this.rLock.unlock();
        }
    }
    
    public void setReified(final boolean reified) {
        try {
            this.wLock.lock();
            this.isReified = reified;
        }
        finally {
            this.wLock.unlock();
        }
    }
    
    @Override
    public Set<Type> getContractTypes() {
        try {
            this.rLock.lock();
            return Collections.unmodifiableSet((Set<? extends Type>)this.advertisedContracts);
        }
        finally {
            this.rLock.unlock();
        }
    }
    
    public void addContractType(final Type addMe) {
        try {
            this.wLock.lock();
            if (addMe == null) {
                return;
            }
            this.advertisedContracts.add(addMe);
            final Class<?> rawClass = ReflectionHelper.getRawClass(addMe);
            if (rawClass == null) {
                return;
            }
            this.addAdvertisedContract(rawClass.getName());
        }
        finally {
            this.wLock.unlock();
        }
    }
    
    public boolean removeContractType(final Type removeMe) {
        try {
            this.wLock.lock();
            if (removeMe == null) {
                return false;
            }
            final boolean retVal = this.advertisedContracts.remove(removeMe);
            final Class<?> rawClass = ReflectionHelper.getRawClass(removeMe);
            if (rawClass == null) {
                return retVal;
            }
            return this.removeAdvertisedContract(rawClass.getName());
        }
        finally {
            this.wLock.unlock();
        }
    }
    
    @Override
    public Annotation getScopeAsAnnotation() {
        return this.scopeAnnotation;
    }
    
    public void setScopeAsAnnotation(final Annotation scopeAnnotation) {
        this.scopeAnnotation = scopeAnnotation;
        if (scopeAnnotation != null) {
            this.setScopeAnnotation(scopeAnnotation.annotationType());
        }
    }
    
    @Override
    public Class<? extends Annotation> getScopeAnnotation() {
        return this.scope;
    }
    
    public void setScopeAnnotation(final Class<? extends Annotation> scopeAnnotation) {
        this.scope = scopeAnnotation;
        this.setScope(this.scope.getName());
    }
    
    @Override
    public Set<Annotation> getQualifierAnnotations() {
        try {
            this.rLock.lock();
            if (this.qualifiers == null) {
                return AbstractActiveDescriptor.EMPTY_QUALIFIER_SET;
            }
            return Collections.unmodifiableSet((Set<? extends Annotation>)this.qualifiers);
        }
        finally {
            this.rLock.unlock();
        }
    }
    
    public void addQualifierAnnotation(final Annotation addMe) {
        try {
            this.wLock.lock();
            if (addMe == null) {
                return;
            }
            if (this.qualifiers == null) {
                this.qualifiers = new LinkedHashSet<Annotation>();
            }
            this.qualifiers.add(addMe);
            this.addQualifier(addMe.annotationType().getName());
        }
        finally {
            this.wLock.unlock();
        }
    }
    
    public boolean removeQualifierAnnotation(final Annotation removeMe) {
        try {
            this.wLock.lock();
            if (removeMe == null) {
                return false;
            }
            if (this.qualifiers == null) {
                return false;
            }
            final boolean retVal = this.qualifiers.remove(removeMe);
            this.removeQualifier(removeMe.annotationType().getName());
            return retVal;
        }
        finally {
            this.wLock.unlock();
        }
    }
    
    @Override
    public Long getFactoryServiceId() {
        return this.factoryServiceId;
    }
    
    @Override
    public Long getFactoryLocatorId() {
        return this.factoryLocatorId;
    }
    
    public void setFactoryId(final Long locatorId, final Long serviceId) {
        if (!this.getDescriptorType().equals(DescriptorType.PROVIDE_METHOD)) {
            throw new IllegalStateException("The descriptor type must be PROVIDE_METHOD");
        }
        this.factoryServiceId = serviceId;
        this.factoryLocatorId = locatorId;
    }
    
    @Override
    public List<Injectee> getInjectees() {
        return Collections.emptyList();
    }
    
    @Override
    public void dispose(final T instance) {
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }
    
    static {
        EMPTY_QUALIFIER_SET = Collections.emptySet();
    }
}
