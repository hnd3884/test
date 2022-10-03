package org.glassfish.jersey.message.internal;

import javax.ws.rs.WebApplicationException;
import java.io.OutputStream;
import javax.ws.rs.Encoded;
import java.io.IOException;
import org.glassfish.jersey.internal.util.collection.NullableMultivaluedHashMap;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Form;

@Produces({ "application/x-www-form-urlencoded", "*/*" })
@Consumes({ "application/x-www-form-urlencoded", "*/*" })
@Singleton
public final class FormProvider extends AbstractFormProvider<Form>
{
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type == Form.class;
    }
    
    public Form readFrom(final Class<Form> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        return new Form(this.readFrom((MultivaluedMap)new NullableMultivaluedHashMap(), mediaType, this.decode(annotations), entityStream));
    }
    
    private boolean decode(final Annotation[] annotations) {
        for (final Annotation annotation : annotations) {
            if (annotation.annotationType().equals(Encoded.class)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type == Form.class;
    }
    
    public void writeTo(final Form t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        this.writeTo(t.asMap(), mediaType, entityStream);
    }
}
