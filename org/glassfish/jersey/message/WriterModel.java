package org.glassfish.jersey.message;

import org.glassfish.jersey.message.internal.MessageBodyFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import java.util.List;
import javax.ws.rs.ext.MessageBodyWriter;

public final class WriterModel extends AbstractEntityProviderModel<MessageBodyWriter>
{
    public WriterModel(final MessageBodyWriter provider, final List<MediaType> types, final Boolean custom) {
        super(provider, types, custom, MessageBodyWriter.class);
    }
    
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return MessageBodyFactory.isWriteable((MessageBodyWriter<?>)super.provider(), type, genericType, annotations, mediaType);
    }
}
