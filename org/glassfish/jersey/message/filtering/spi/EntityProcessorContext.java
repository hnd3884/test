package org.glassfish.jersey.message.filtering.spi;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

public interface EntityProcessorContext
{
    Type getType();
    
    Class<?> getEntityClass();
    
    Field getField();
    
    Method getMethod();
    
    EntityGraph getEntityGraph();
    
    public enum Type
    {
        CLASS_READER, 
        CLASS_WRITER, 
        PROPERTY_READER, 
        PROPERTY_WRITER, 
        METHOD_READER, 
        METHOD_WRITER;
    }
}
