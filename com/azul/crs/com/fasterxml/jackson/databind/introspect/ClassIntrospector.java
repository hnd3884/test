package com.azul.crs.com.fasterxml.jackson.databind.introspect;

import com.azul.crs.com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;
import com.azul.crs.com.fasterxml.jackson.databind.BeanDescription;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.SerializationConfig;

public abstract class ClassIntrospector
{
    protected ClassIntrospector() {
    }
    
    public abstract ClassIntrospector copy();
    
    public abstract BeanDescription forSerialization(final SerializationConfig p0, final JavaType p1, final MixInResolver p2);
    
    public abstract BeanDescription forDeserialization(final DeserializationConfig p0, final JavaType p1, final MixInResolver p2);
    
    public abstract BeanDescription forDeserializationWithBuilder(final DeserializationConfig p0, final JavaType p1, final MixInResolver p2, final BeanDescription p3);
    
    @Deprecated
    public abstract BeanDescription forDeserializationWithBuilder(final DeserializationConfig p0, final JavaType p1, final MixInResolver p2);
    
    public abstract BeanDescription forCreation(final DeserializationConfig p0, final JavaType p1, final MixInResolver p2);
    
    public abstract BeanDescription forClassAnnotations(final MapperConfig<?> p0, final JavaType p1, final MixInResolver p2);
    
    public abstract BeanDescription forDirectClassAnnotations(final MapperConfig<?> p0, final JavaType p1, final MixInResolver p2);
    
    public interface MixInResolver
    {
        Class<?> findMixInClassFor(final Class<?> p0);
        
        MixInResolver copy();
    }
}
