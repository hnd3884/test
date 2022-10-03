package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.DescriptorVisibility;
import java.util.List;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface ActiveDescriptorBuilder
{
    ActiveDescriptorBuilder named(final String p0) throws IllegalArgumentException;
    
    ActiveDescriptorBuilder to(final Type p0) throws IllegalArgumentException;
    
    ActiveDescriptorBuilder in(final Annotation p0) throws IllegalArgumentException;
    
    ActiveDescriptorBuilder in(final Class<? extends Annotation> p0) throws IllegalArgumentException;
    
    ActiveDescriptorBuilder qualifiedBy(final Annotation p0) throws IllegalArgumentException;
    
    ActiveDescriptorBuilder has(final String p0, final String p1) throws IllegalArgumentException;
    
    ActiveDescriptorBuilder has(final String p0, final List<String> p1) throws IllegalArgumentException;
    
    ActiveDescriptorBuilder ofRank(final int p0);
    
    ActiveDescriptorBuilder localOnly();
    
    ActiveDescriptorBuilder visibility(final DescriptorVisibility p0);
    
    ActiveDescriptorBuilder proxy();
    
    ActiveDescriptorBuilder proxy(final boolean p0);
    
    ActiveDescriptorBuilder proxyForSameScope();
    
    ActiveDescriptorBuilder proxyForSameScope(final boolean p0);
    
    ActiveDescriptorBuilder andLoadWith(final HK2Loader p0) throws IllegalArgumentException;
    
    ActiveDescriptorBuilder analyzeWith(final String p0);
    
    ActiveDescriptorBuilder asType(final Type p0);
    
     <T> AbstractActiveDescriptor<T> build() throws IllegalArgumentException;
    
    @Deprecated
     <T> AbstractActiveDescriptor<T> buildFactory() throws IllegalArgumentException;
    
     <T> AbstractActiveDescriptor<T> buildProvideMethod() throws IllegalArgumentException;
}
