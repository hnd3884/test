package org.glassfish.jersey.server.internal.inject;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ProcessingException;
import java.lang.reflect.InvocationTargetException;
import org.glassfish.jersey.internal.inject.ExtractorException;
import javax.ws.rs.WebApplicationException;
import java.lang.reflect.Method;

final class PrimitiveValueOfExtractor implements MultivaluedParameterExtractor<Object>
{
    private final Method valueOf;
    private final String parameter;
    private final String defaultStringValue;
    private final Object defaultValue;
    private final Object defaultPrimitiveTypeValue;
    
    public PrimitiveValueOfExtractor(final Method valueOf, final String parameter, final String defaultStringValue, final Object defaultPrimitiveTypeValue) {
        this.valueOf = valueOf;
        this.parameter = parameter;
        this.defaultStringValue = defaultStringValue;
        this.defaultValue = ((defaultStringValue != null) ? this.getValue(defaultStringValue) : null);
        this.defaultPrimitiveTypeValue = defaultPrimitiveTypeValue;
    }
    
    @Override
    public String getName() {
        return this.parameter;
    }
    
    @Override
    public String getDefaultValueString() {
        return this.defaultStringValue;
    }
    
    private Object getValue(final String v) {
        try {
            return this.valueOf.invoke(null, v);
        }
        catch (final InvocationTargetException ex) {
            final Throwable target = ex.getTargetException();
            if (target instanceof WebApplicationException) {
                throw (WebApplicationException)target;
            }
            throw new ExtractorException(target);
        }
        catch (final Exception ex2) {
            throw new ProcessingException((Throwable)ex2);
        }
    }
    
    @Override
    public Object extract(final MultivaluedMap<String, String> parameters) {
        final String v = (String)parameters.getFirst((Object)this.parameter);
        if (v != null && !v.trim().isEmpty()) {
            return this.getValue(v);
        }
        if (this.defaultValue != null) {
            return this.defaultValue;
        }
        return this.defaultPrimitiveTypeValue;
    }
}
