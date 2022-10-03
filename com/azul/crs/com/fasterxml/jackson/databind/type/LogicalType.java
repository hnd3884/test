package com.azul.crs.com.fasterxml.jackson.databind.type;

import java.util.Map;
import java.util.Collection;

public enum LogicalType
{
    Array, 
    Collection, 
    Map, 
    POJO, 
    Untyped, 
    Integer, 
    Float, 
    Boolean, 
    Enum, 
    Textual, 
    Binary, 
    DateTime, 
    OtherScalar;
    
    public static LogicalType fromClass(final Class<?> raw, final LogicalType defaultIfNotRecognized) {
        if (raw.isEnum()) {
            return LogicalType.Enum;
        }
        if (raw.isArray()) {
            if (raw == byte[].class) {
                return LogicalType.Binary;
            }
            return LogicalType.Array;
        }
        else {
            if (Collection.class.isAssignableFrom(raw)) {
                return LogicalType.Collection;
            }
            if (Map.class.isAssignableFrom(raw)) {
                return LogicalType.Map;
            }
            if (raw == String.class) {
                return LogicalType.Textual;
            }
            return defaultIfNotRecognized;
        }
    }
}
