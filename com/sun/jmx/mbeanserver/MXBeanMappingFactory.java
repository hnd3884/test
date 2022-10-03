package com.sun.jmx.mbeanserver;

import javax.management.openmbean.OpenDataException;
import java.lang.reflect.Type;

public abstract class MXBeanMappingFactory
{
    public static final MXBeanMappingFactory DEFAULT;
    
    protected MXBeanMappingFactory() {
    }
    
    public abstract MXBeanMapping mappingForType(final Type p0, final MXBeanMappingFactory p1) throws OpenDataException;
    
    static {
        DEFAULT = new DefaultMXBeanMappingFactory();
    }
}
