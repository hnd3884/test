package com.sun.xml.internal.ws.api.databinding;

import com.sun.xml.internal.ws.db.DatabindingFactoryImpl;
import java.util.Map;
import com.oracle.webservices.internal.api.databinding.Databinding;

public abstract class DatabindingFactory extends com.oracle.webservices.internal.api.databinding.DatabindingFactory
{
    static final String ImplClass;
    
    public abstract Databinding createRuntime(final DatabindingConfig p0);
    
    @Override
    public abstract Map<String, Object> properties();
    
    public static DatabindingFactory newInstance() {
        try {
            final Class<?> cls = Class.forName(DatabindingFactory.ImplClass);
            return (DatabindingFactory)cls.newInstance();
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    static {
        ImplClass = DatabindingFactoryImpl.class.getName();
    }
}
