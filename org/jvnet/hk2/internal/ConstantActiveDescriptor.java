package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.ServiceHandle;
import java.util.List;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.PerLookup;
import java.util.HashSet;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;

public class ConstantActiveDescriptor<T> extends AbstractActiveDescriptor<T>
{
    private static final long serialVersionUID = 3663054975929743877L;
    private T theOne;
    private Long locatorId;
    
    public ConstantActiveDescriptor() {
    }
    
    public ConstantActiveDescriptor(final T theOne, final ServiceLocatorImpl locator) {
        super((Set)new HashSet(), (Class)PerLookup.class, (String)null, (Set)new HashSet(), DescriptorType.CLASS, DescriptorVisibility.NORMAL, 0, (Boolean)null, (Boolean)null, locator.getPerLocatorUtilities().getAutoAnalyzerName(theOne.getClass()), (Map)null);
        this.theOne = theOne;
        this.locatorId = locator.getLocatorId();
    }
    
    public ConstantActiveDescriptor(final T theOne, final Set<Type> advertisedContracts, final Class<? extends Annotation> scope, final String name, final Set<Annotation> qualifiers, final DescriptorVisibility visibility, final int ranking, final Boolean proxy, final Boolean proxyForSameScope, final String analyzerName, final long locatorId, final Map<String, List<String>> metadata) {
        super((Set)advertisedContracts, (Class)scope, name, (Set)qualifiers, DescriptorType.CLASS, visibility, ranking, proxy, proxyForSameScope, analyzerName, (Map)metadata);
        if (theOne == null) {
            throw new IllegalArgumentException();
        }
        this.theOne = theOne;
        this.locatorId = locatorId;
    }
    
    public String getImplementation() {
        return this.theOne.getClass().getName();
    }
    
    public Long getLocatorId() {
        return this.locatorId;
    }
    
    public T getCache() {
        return this.theOne;
    }
    
    public boolean isCacheSet() {
        return true;
    }
    
    public Class<?> getImplementationClass() {
        return this.theOne.getClass();
    }
    
    public Type getImplementationType() {
        return this.theOne.getClass();
    }
    
    public void setImplementationType(final Type t) {
        throw new AssertionError((Object)"Can not set type of a constant descriptor");
    }
    
    public T create(final ServiceHandle<?> root) {
        return this.theOne;
    }
    
    public void dispose(final T instance) {
    }
}
