package org.glassfish.hk2.api;

import java.lang.annotation.Annotation;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface Context<T>
{
    Class<? extends Annotation> getScope();
    
     <U> U findOrCreate(final ActiveDescriptor<U> p0, final ServiceHandle<?> p1);
    
    boolean containsKey(final ActiveDescriptor<?> p0);
    
    void destroyOne(final ActiveDescriptor<?> p0);
    
    boolean supportsNullCreation();
    
    boolean isActive();
    
    void shutdown();
}
