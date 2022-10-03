package org.glassfish.jersey.client;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.PropertiesDelegate;
import javax.inject.Inject;
import org.glassfish.jersey.message.MessageBodyWorkers;
import javax.inject.Provider;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.ext.MessageBodyReader;

@ConstrainedTo(RuntimeType.CLIENT)
class ChunkedInputReader implements MessageBodyReader<ChunkedInput>
{
    @Inject
    private Provider<MessageBodyWorkers> messageBodyWorkers;
    @Inject
    private Provider<PropertiesDelegate> propertiesDelegateProvider;
    
    public boolean isReadable(final Class<?> aClass, final Type type, final Annotation[] annotations, final MediaType mediaType) {
        return aClass.equals(ChunkedInput.class);
    }
    
    public ChunkedInput readFrom(final Class<ChunkedInput> chunkedInputClass, final Type type, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> headers, final InputStream inputStream) throws IOException, WebApplicationException {
        final Type chunkType = ReflectionHelper.getTypeArgument(type, 0);
        return new ChunkedInput(chunkType, inputStream, annotations, mediaType, headers, (MessageBodyWorkers)this.messageBodyWorkers.get(), (PropertiesDelegate)this.propertiesDelegateProvider.get());
    }
}
