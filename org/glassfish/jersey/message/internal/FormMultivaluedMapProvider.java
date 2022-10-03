package org.glassfish.jersey.message.internal;

import javax.ws.rs.WebApplicationException;
import java.io.OutputStream;
import java.io.IOException;
import org.glassfish.jersey.internal.util.collection.NullableMultivaluedHashMap;
import java.io.InputStream;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;

@Produces({ "application/x-www-form-urlencoded" })
@Consumes({ "application/x-www-form-urlencoded" })
@Singleton
public final class FormMultivaluedMapProvider extends AbstractFormProvider<MultivaluedMap<String, String>>
{
    private final Type mapType;
    
    public FormMultivaluedMapProvider() {
        final ParameterizedType iface = (ParameterizedType)this.getClass().getGenericSuperclass();
        this.mapType = iface.getActualTypeArguments()[0];
    }
    
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type == MultivaluedMap.class && (type == genericType || this.mapType.equals(genericType));
    }
    
    public MultivaluedMap<String, String> readFrom(final Class<MultivaluedMap<String, String>> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        return this.readFrom((MultivaluedMap<String, String>)new NullableMultivaluedHashMap(), mediaType, true, entityStream);
    }
    
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return MultivaluedMap.class.isAssignableFrom(type);
    }
    
    public void writeTo(final MultivaluedMap<String, String> t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        this.writeTo(t, mediaType, entityStream);
    }
}
