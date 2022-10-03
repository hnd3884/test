package com.fasterxml.jackson.dataformat.xml.util;

import java.util.Collection;
import com.fasterxml.jackson.databind.JavaType;

public class TypeUtil
{
    public static boolean isIndexedType(final JavaType type) {
        if (type.isContainerType()) {
            final Class<?> cls = type.getRawClass();
            return cls != byte[].class && cls != char[].class && !type.isMapLikeType();
        }
        return false;
    }
    
    public static boolean isIndexedType(final Class<?> cls) {
        return (cls.isArray() && cls != byte[].class && cls != char[].class) || Collection.class.isAssignableFrom(cls);
    }
}
