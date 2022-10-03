package com.oracle.webservices.internal.api.databinding;

import java.util.Map;

public abstract class DatabindingFactory
{
    static final String ImplClass = "com.sun.xml.internal.ws.db.DatabindingFactoryImpl";
    
    public abstract Databinding.Builder createBuilder(final Class<?> p0, final Class<?> p1);
    
    public abstract Map<String, Object> properties();
    
    public static DatabindingFactory newInstance() {
        try {
            final Class<?> cls = Class.forName("com.sun.xml.internal.ws.db.DatabindingFactoryImpl");
            return convertIfNecessary(cls);
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static DatabindingFactory convertIfNecessary(final Class<?> cls) throws InstantiationException, IllegalAccessException {
        return (DatabindingFactory)cls.newInstance();
    }
}
