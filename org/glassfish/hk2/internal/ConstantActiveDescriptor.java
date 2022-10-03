package org.glassfish.hk2.internal;

import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.DescriptorType;
import java.util.List;
import java.util.Map;
import org.glassfish.hk2.api.DescriptorVisibility;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;

public class ConstantActiveDescriptor<T> extends AbstractActiveDescriptor<T>
{
    private static final long serialVersionUID = -9196390718074767455L;
    private final T theOne;
    
    public ConstantActiveDescriptor() {
        this.theOne = null;
    }
    
    public ConstantActiveDescriptor(final T theOne, final Set<Type> advertisedContracts, final Class<? extends Annotation> scope, final String name, final Set<Annotation> qualifiers, final DescriptorVisibility descriptorVisibility, final Boolean proxy, final Boolean proxyForSameScope, final String classAnalysisName, final Map<String, List<String>> metadata, final int rank) {
        super(advertisedContracts, scope, name, qualifiers, DescriptorType.CLASS, descriptorVisibility, rank, proxy, proxyForSameScope, classAnalysisName, metadata);
        if (theOne == null) {
            throw new IllegalArgumentException();
        }
        this.theOne = theOne;
    }
    
    @Override
    public String getImplementation() {
        return this.theOne.getClass().getName();
    }
    
    @Override
    public T getCache() {
        return this.theOne;
    }
    
    @Override
    public boolean isCacheSet() {
        return true;
    }
    
    @Override
    public Class<?> getImplementationClass() {
        return this.theOne.getClass();
    }
    
    @Override
    public Type getImplementationType() {
        return this.theOne.getClass();
    }
    
    @Override
    public T create(final ServiceHandle<?> root) {
        return this.theOne;
    }
}
