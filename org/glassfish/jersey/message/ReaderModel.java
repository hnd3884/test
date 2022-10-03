package org.glassfish.jersey.message;

import org.glassfish.jersey.message.internal.MessageBodyFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import java.util.List;
import javax.ws.rs.ext.MessageBodyReader;

public final class ReaderModel extends AbstractEntityProviderModel<MessageBodyReader>
{
    public ReaderModel(final MessageBodyReader provider, final List<MediaType> types, final Boolean custom) {
        super(provider, types, custom, MessageBodyReader.class);
    }
    
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return MessageBodyFactory.isReadable((MessageBodyReader<?>)super.provider(), type, genericType, annotations, mediaType);
    }
}
