package org.glassfish.hk2.api;

public interface DynamicConfiguration
{
     <T> ActiveDescriptor<T> bind(final Descriptor p0);
    
     <T> ActiveDescriptor<T> bind(final Descriptor p0, final boolean p1);
    
    FactoryDescriptors bind(final FactoryDescriptors p0);
    
    FactoryDescriptors bind(final FactoryDescriptors p0, final boolean p1);
    
     <T> ActiveDescriptor<T> addActiveDescriptor(final ActiveDescriptor<T> p0) throws IllegalArgumentException;
    
     <T> ActiveDescriptor<T> addActiveDescriptor(final ActiveDescriptor<T> p0, final boolean p1) throws IllegalArgumentException;
    
     <T> ActiveDescriptor<T> addActiveDescriptor(final Class<T> p0) throws MultiException, IllegalArgumentException;
    
     <T> FactoryDescriptors addActiveFactoryDescriptor(final Class<? extends Factory<T>> p0) throws MultiException, IllegalArgumentException;
    
    void addUnbindFilter(final Filter p0) throws IllegalArgumentException;
    
    void addIdempotentFilter(final Filter... p0) throws IllegalArgumentException;
    
    void registerTwoPhaseResources(final TwoPhaseResource... p0);
    
    void commit() throws MultiException;
}
