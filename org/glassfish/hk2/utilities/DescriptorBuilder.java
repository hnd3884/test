package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.FactoryDescriptors;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.DescriptorVisibility;
import java.util.List;
import java.lang.annotation.Annotation;

public interface DescriptorBuilder
{
    DescriptorBuilder named(final String p0) throws IllegalArgumentException;
    
    DescriptorBuilder to(final Class<?> p0) throws IllegalArgumentException;
    
    DescriptorBuilder to(final String p0) throws IllegalArgumentException;
    
    DescriptorBuilder in(final Class<? extends Annotation> p0) throws IllegalArgumentException;
    
    DescriptorBuilder in(final String p0) throws IllegalArgumentException;
    
    DescriptorBuilder qualifiedBy(final Annotation p0) throws IllegalArgumentException;
    
    DescriptorBuilder qualifiedBy(final String p0) throws IllegalArgumentException;
    
    DescriptorBuilder has(final String p0, final String p1) throws IllegalArgumentException;
    
    DescriptorBuilder has(final String p0, final List<String> p1) throws IllegalArgumentException;
    
    DescriptorBuilder ofRank(final int p0);
    
    DescriptorBuilder proxy();
    
    DescriptorBuilder proxy(final boolean p0);
    
    DescriptorBuilder proxyForSameScope();
    
    DescriptorBuilder proxyForSameScope(final boolean p0);
    
    DescriptorBuilder localOnly();
    
    DescriptorBuilder visibility(final DescriptorVisibility p0);
    
    DescriptorBuilder andLoadWith(final HK2Loader p0) throws IllegalArgumentException;
    
    DescriptorBuilder analyzeWith(final String p0);
    
    DescriptorImpl build() throws IllegalArgumentException;
    
    FactoryDescriptors buildFactory() throws IllegalArgumentException;
    
    FactoryDescriptors buildFactory(final String p0) throws IllegalArgumentException;
    
    FactoryDescriptors buildFactory(final Class<? extends Annotation> p0) throws IllegalArgumentException;
}
