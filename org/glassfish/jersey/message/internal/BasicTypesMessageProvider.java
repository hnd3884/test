package org.glassfish.jersey.message.internal;

import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.security.AccessController;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import java.lang.reflect.Constructor;
import javax.ws.rs.core.NoContentException;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

@Produces({ "text/plain" })
@Consumes({ "text/plain" })
@Singleton
final class BasicTypesMessageProvider extends AbstractMessageReaderWriterProvider<Object>
{
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return this.canProcess(type);
    }
    
    public Object readFrom(final Class<Object> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException, WebApplicationException {
        final String entityString = AbstractMessageReaderWriterProvider.readFromAsString(entityStream, mediaType);
        if (entityString.isEmpty()) {
            throw new NoContentException(LocalizationMessages.ERROR_READING_ENTITY_MISSING());
        }
        final PrimitiveTypes primitiveType = PrimitiveTypes.forType(type);
        if (primitiveType != null) {
            return primitiveType.convert(entityString);
        }
        final Constructor constructor = AccessController.doPrivileged(ReflectionHelper.getStringConstructorPA(type));
        if (constructor != null) {
            try {
                return type.cast(constructor.newInstance(entityString));
            }
            catch (final Exception e) {
                throw new MessageBodyProcessingException(LocalizationMessages.ERROR_ENTITY_PROVIDER_BASICTYPES_CONSTRUCTOR(type));
            }
        }
        if (AtomicInteger.class.isAssignableFrom(type)) {
            return new AtomicInteger((int)PrimitiveTypes.INTEGER.convert(entityString));
        }
        if (AtomicLong.class.isAssignableFrom(type)) {
            return new AtomicLong((long)PrimitiveTypes.LONG.convert(entityString));
        }
        throw new MessageBodyProcessingException(LocalizationMessages.ERROR_ENTITY_PROVIDER_BASICTYPES_UNKWNOWN(type));
    }
    
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return this.canProcess(type);
    }
    
    private boolean canProcess(final Class<?> type) {
        if (PrimitiveTypes.forType(type) != null) {
            return true;
        }
        if (Number.class.isAssignableFrom(type)) {
            final Constructor constructor = AccessController.doPrivileged(ReflectionHelper.getStringConstructorPA(type));
            if (constructor != null) {
                return true;
            }
            if (AtomicInteger.class.isAssignableFrom(type) || AtomicLong.class.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public long getSize(final Object t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return t.toString().length();
    }
    
    public void writeTo(final Object o, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException, WebApplicationException {
        AbstractMessageReaderWriterProvider.writeToAsString(o.toString(), entityStream, mediaType);
    }
    
    private enum PrimitiveTypes
    {
        BYTE((Class)Byte.class, (Class)Byte.TYPE) {
            @Override
            public Object convert(final String s) {
                return Byte.valueOf(s);
            }
        }, 
        SHORT((Class)Short.class, (Class)Short.TYPE) {
            @Override
            public Object convert(final String s) {
                return Short.valueOf(s);
            }
        }, 
        INTEGER((Class)Integer.class, (Class)Integer.TYPE) {
            @Override
            public Object convert(final String s) {
                return Integer.valueOf(s);
            }
        }, 
        LONG((Class)Long.class, (Class)Long.TYPE) {
            @Override
            public Object convert(final String s) {
                return Long.valueOf(s);
            }
        }, 
        FLOAT((Class)Float.class, (Class)Float.TYPE) {
            @Override
            public Object convert(final String s) {
                return Float.valueOf(s);
            }
        }, 
        DOUBLE((Class)Double.class, (Class)Double.TYPE) {
            @Override
            public Object convert(final String s) {
                return Double.valueOf(s);
            }
        }, 
        BOOLEAN((Class)Boolean.class, (Class)Boolean.TYPE) {
            @Override
            public Object convert(final String s) {
                return Boolean.valueOf(s);
            }
        }, 
        CHAR((Class)Character.class, (Class)Character.TYPE) {
            @Override
            public Object convert(final String s) {
                if (s.length() != 1) {
                    throw new MessageBodyProcessingException(LocalizationMessages.ERROR_ENTITY_PROVIDER_BASICTYPES_CHARACTER_MORECHARS());
                }
                return s.charAt(0);
            }
        };
        
        private final Class<?> wrapper;
        private final Class<?> primitive;
        
        public static PrimitiveTypes forType(final Class<?> type) {
            for (final PrimitiveTypes primitive : values()) {
                if (primitive.supports(type)) {
                    return primitive;
                }
            }
            return null;
        }
        
        private PrimitiveTypes(final Class<?> wrapper, final Class<?> primitive) {
            this.wrapper = wrapper;
            this.primitive = primitive;
        }
        
        public abstract Object convert(final String p0);
        
        public boolean supports(final Class<?> type) {
            return type == this.wrapper || type == this.primitive;
        }
    }
}
