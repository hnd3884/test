package com.oracle.webservices.internal.api.message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Inherited;
import java.util.Map;

public interface PropertySet
{
    boolean containsKey(final Object p0);
    
    Object get(final Object p0);
    
    Object put(final String p0, final Object p1);
    
    boolean supports(final Object p0);
    
    Object remove(final Object p0);
    
    @Deprecated
    Map<String, Object> createMapView();
    
    Map<String, Object> asMap();
    
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD })
    public @interface Property {
        String[] value();
    }
}
