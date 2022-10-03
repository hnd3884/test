package org.glassfish.jersey.server.internal.inject;

import java.text.ParseException;
import org.glassfish.jersey.message.internal.HttpDateFormat;
import java.util.Date;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import java.security.AccessController;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import java.lang.reflect.Constructor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ProcessingException;
import java.lang.reflect.InvocationTargetException;
import org.glassfish.jersey.internal.inject.ExtractorException;
import javax.ws.rs.WebApplicationException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import javax.ws.rs.ext.ParamConverter;
import javax.inject.Singleton;

@Singleton
class ParamConverters
{
    private abstract static class AbstractStringReader<T> implements ParamConverter<T>
    {
        public T fromString(final String value) {
            if (value == null) {
                throw new IllegalArgumentException(LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL("value"));
            }
            try {
                return this._fromString(value);
            }
            catch (final InvocationTargetException ex) {
                if (value.isEmpty()) {
                    return null;
                }
                final Throwable cause = ex.getCause();
                if (cause instanceof WebApplicationException) {
                    throw (WebApplicationException)cause;
                }
                throw new ExtractorException(cause);
            }
            catch (final Exception ex2) {
                throw new ProcessingException((Throwable)ex2);
            }
        }
        
        protected abstract T _fromString(final String p0) throws Exception;
        
        public String toString(final T value) throws IllegalArgumentException {
            if (value == null) {
                throw new IllegalArgumentException(LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL("value"));
            }
            return value.toString();
        }
    }
    
    @Singleton
    public static class StringConstructor implements ParamConverterProvider
    {
        public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType, final Annotation[] annotations) {
            final Constructor constructor = AccessController.doPrivileged((PrivilegedAction<Constructor>)ReflectionHelper.getStringConstructorPA((Class)rawType));
            return (ParamConverter<T>)((constructor == null) ? null : new AbstractStringReader<T>() {
                @Override
                protected T _fromString(final String value) throws Exception {
                    return rawType.cast(constructor.newInstance(value));
                }
            });
        }
    }
    
    @Singleton
    public static class TypeValueOf implements ParamConverterProvider
    {
        public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType, final Annotation[] annotations) {
            final Method valueOf = AccessController.doPrivileged((PrivilegedAction<Method>)ReflectionHelper.getValueOfStringMethodPA((Class)rawType));
            return (ParamConverter<T>)((valueOf == null) ? null : new AbstractStringReader<T>() {
                public T _fromString(final String value) throws Exception {
                    return rawType.cast(valueOf.invoke(null, value));
                }
            });
        }
    }
    
    @Singleton
    public static class TypeFromString implements ParamConverterProvider
    {
        public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType, final Annotation[] annotations) {
            final Method fromStringMethod = AccessController.doPrivileged((PrivilegedAction<Method>)ReflectionHelper.getFromStringStringMethodPA((Class)rawType));
            return (ParamConverter<T>)((fromStringMethod == null) ? null : new AbstractStringReader<T>() {
                public T _fromString(final String value) throws Exception {
                    return rawType.cast(fromStringMethod.invoke(null, value));
                }
            });
        }
    }
    
    @Singleton
    public static class TypeFromStringEnum extends TypeFromString
    {
        @Override
        public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType, final Annotation[] annotations) {
            return Enum.class.isAssignableFrom(rawType) ? super.getConverter(rawType, genericType, annotations) : null;
        }
    }
    
    @Singleton
    public static class CharacterProvider implements ParamConverterProvider
    {
        public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType, final Annotation[] annotations) {
            if (rawType.equals(Character.class)) {
                return (ParamConverter<T>)new ParamConverter<T>() {
                    public T fromString(final String value) {
                        if (value == null || value.isEmpty()) {
                            return null;
                        }
                        if (value.length() == 1) {
                            return rawType.cast(value.charAt(0));
                        }
                        throw new ExtractorException(LocalizationMessages.ERROR_PARAMETER_INVALID_CHAR_VALUE(value));
                    }
                    
                    public String toString(final T value) {
                        if (value == null) {
                            throw new IllegalArgumentException(LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL("value"));
                        }
                        return value.toString();
                    }
                };
            }
            return null;
        }
    }
    
    @Singleton
    public static class DateProvider implements ParamConverterProvider
    {
        public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType, final Annotation[] annotations) {
            return (ParamConverter<T>)((rawType != Date.class) ? null : new ParamConverter<T>() {
                public T fromString(final String value) {
                    if (value == null) {
                        throw new IllegalArgumentException(LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL("value"));
                    }
                    try {
                        return rawType.cast(HttpDateFormat.readDate(value));
                    }
                    catch (final ParseException ex) {
                        throw new ExtractorException((Throwable)ex);
                    }
                }
                
                public String toString(final T value) throws IllegalArgumentException {
                    if (value == null) {
                        throw new IllegalArgumentException(LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL("value"));
                    }
                    return value.toString();
                }
            });
        }
    }
    
    @Singleton
    public static class AggregatedProvider implements ParamConverterProvider
    {
        private final ParamConverterProvider[] providers;
        
        public AggregatedProvider() {
            this.providers = new ParamConverterProvider[] { (ParamConverterProvider)new DateProvider(), (ParamConverterProvider)new TypeFromStringEnum(), (ParamConverterProvider)new TypeValueOf(), (ParamConverterProvider)new CharacterProvider(), (ParamConverterProvider)new TypeFromString(), (ParamConverterProvider)new StringConstructor() };
        }
        
        public <T> ParamConverter<T> getConverter(final Class<T> rawType, final Type genericType, final Annotation[] annotations) {
            for (final ParamConverterProvider p : this.providers) {
                final ParamConverter<T> reader = (ParamConverter<T>)p.getConverter((Class)rawType, genericType, annotations);
                if (reader != null) {
                    return reader;
                }
            }
            return null;
        }
    }
}
