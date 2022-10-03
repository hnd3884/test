package org.glassfish.jersey.server.internal.inject;

import org.glassfish.jersey.internal.inject.ExtractorException;
import javax.ws.rs.WebApplicationException;
import java.lang.annotation.Annotation;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.internal.util.collection.UnsafeValue;
import javax.ws.rs.ext.ParamConverter;

abstract class AbstractParamValueExtractor<T>
{
    private final ParamConverter<T> paramConverter;
    private final String parameterName;
    private final String defaultValueString;
    private final UnsafeValue<T, RuntimeException> convertedDefaultValue;
    
    protected AbstractParamValueExtractor(final ParamConverter<T> converter, final String parameterName, final String defaultValueString) {
        this.paramConverter = converter;
        this.parameterName = parameterName;
        this.defaultValueString = defaultValueString;
        if (defaultValueString != null) {
            this.convertedDefaultValue = (UnsafeValue<T, RuntimeException>)Values.lazy((UnsafeValue)new UnsafeValue<T, RuntimeException>() {
                public T get() throws RuntimeException {
                    return (T)AbstractParamValueExtractor.this.convert(defaultValueString);
                }
            });
            if (!converter.getClass().isAnnotationPresent((Class<? extends Annotation>)ParamConverter.Lazy.class)) {
                this.convertedDefaultValue.get();
            }
        }
        else {
            this.convertedDefaultValue = null;
        }
    }
    
    public String getName() {
        return this.parameterName;
    }
    
    public String getDefaultValueString() {
        return this.defaultValueString;
    }
    
    protected final T fromString(final String value) {
        final T result = this.convert(value);
        if (result == null) {
            return this.defaultValue();
        }
        return result;
    }
    
    private T convert(final String value) {
        try {
            return (T)this.paramConverter.fromString(value);
        }
        catch (final WebApplicationException wae) {
            throw wae;
        }
        catch (final IllegalArgumentException iae) {
            throw iae;
        }
        catch (final Exception ex) {
            throw new ExtractorException((Throwable)ex);
        }
    }
    
    protected final boolean isDefaultValueRegistered() {
        return this.defaultValueString != null;
    }
    
    protected final T defaultValue() {
        if (!this.isDefaultValueRegistered()) {
            return null;
        }
        return (T)this.convertedDefaultValue.get();
    }
}
