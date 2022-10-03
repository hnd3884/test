package com.azul.crs.com.fasterxml.jackson.databind.cfg;

import com.azul.crs.com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.azul.crs.com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter;
import com.azul.crs.com.fasterxml.jackson.databind.util.Converter;
import com.azul.crs.com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.azul.crs.com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.azul.crs.com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.azul.crs.com.fasterxml.jackson.databind.JsonSerializer;
import com.azul.crs.com.fasterxml.jackson.databind.SerializationConfig;
import com.azul.crs.com.fasterxml.jackson.databind.KeyDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.Annotated;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;

public abstract class HandlerInstantiator
{
    public abstract JsonDeserializer<?> deserializerInstance(final DeserializationConfig p0, final Annotated p1, final Class<?> p2);
    
    public abstract KeyDeserializer keyDeserializerInstance(final DeserializationConfig p0, final Annotated p1, final Class<?> p2);
    
    public abstract JsonSerializer<?> serializerInstance(final SerializationConfig p0, final Annotated p1, final Class<?> p2);
    
    public abstract TypeResolverBuilder<?> typeResolverBuilderInstance(final MapperConfig<?> p0, final Annotated p1, final Class<?> p2);
    
    public abstract TypeIdResolver typeIdResolverInstance(final MapperConfig<?> p0, final Annotated p1, final Class<?> p2);
    
    public ValueInstantiator valueInstantiatorInstance(final MapperConfig<?> config, final Annotated annotated, final Class<?> resolverClass) {
        return null;
    }
    
    public ObjectIdGenerator<?> objectIdGeneratorInstance(final MapperConfig<?> config, final Annotated annotated, final Class<?> implClass) {
        return null;
    }
    
    public ObjectIdResolver resolverIdGeneratorInstance(final MapperConfig<?> config, final Annotated annotated, final Class<?> implClass) {
        return null;
    }
    
    public PropertyNamingStrategy namingStrategyInstance(final MapperConfig<?> config, final Annotated annotated, final Class<?> implClass) {
        return null;
    }
    
    public Converter<?, ?> converterInstance(final MapperConfig<?> config, final Annotated annotated, final Class<?> implClass) {
        return null;
    }
    
    public VirtualBeanPropertyWriter virtualPropertyWriterInstance(final MapperConfig<?> config, final Class<?> implClass) {
        return null;
    }
    
    public Object includeFilterInstance(final SerializationConfig config, final BeanPropertyDefinition forProperty, final Class<?> filterClass) {
        return null;
    }
}
