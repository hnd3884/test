package org.glassfish.jersey.jaxb.internal;

import javax.ws.rs.core.Context;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import java.io.OutputStream;
import java.io.IOException;
import javax.ws.rs.InternalServerErrorException;
import javax.xml.bind.UnmarshalException;
import javax.ws.rs.BadRequestException;
import javax.xml.transform.Source;
import javax.ws.rs.core.NoContentException;
import org.glassfish.jersey.message.internal.EntityInputStream;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import javax.xml.parsers.SAXParserFactory;
import javax.inject.Provider;

public abstract class XmlRootObjectJaxbProvider extends AbstractJaxbProvider<Object>
{
    private final Provider<SAXParserFactory> spf;
    
    XmlRootObjectJaxbProvider(final Provider<SAXParserFactory> spf, final Providers ps) {
        super(ps);
        this.spf = spf;
    }
    
    XmlRootObjectJaxbProvider(final Provider<SAXParserFactory> spf, final Providers ps, final MediaType mt) {
        super(ps, mt);
        this.spf = spf;
    }
    
    @Override
    protected JAXBContext getStoredJaxbContext(final Class type) throws JAXBException {
        return null;
    }
    
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        try {
            return Object.class == type && this.isSupported(mediaType) && this.getUnmarshaller(type, mediaType) != null;
        }
        catch (final JAXBException cause) {
            throw new RuntimeException(LocalizationMessages.ERROR_UNMARSHALLING_JAXB(type), cause);
        }
    }
    
    public final Object readFrom(final Class<Object> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream inputStream) throws IOException {
        final EntityInputStream entityStream = EntityInputStream.create(inputStream);
        if (entityStream.isEmpty()) {
            throw new NoContentException(LocalizationMessages.ERROR_READING_ENTITY_MISSING());
        }
        try {
            return this.getUnmarshaller(type, mediaType).unmarshal(AbstractJaxbProvider.getSAXSource((SAXParserFactory)this.spf.get(), (InputStream)entityStream));
        }
        catch (final UnmarshalException ex) {
            throw new BadRequestException((Throwable)ex);
        }
        catch (final JAXBException ex2) {
            throw new InternalServerErrorException((Throwable)ex2);
        }
    }
    
    public boolean isWriteable(final Class<?> arg0, final Type arg1, final Annotation[] arg2, final MediaType mediaType) {
        return false;
    }
    
    public void writeTo(final Object arg0, final Class<?> arg1, final Type arg2, final Annotation[] arg3, final MediaType arg4, final MultivaluedMap<String, Object> arg5, final OutputStream arg6) throws IOException, WebApplicationException {
        throw new IllegalArgumentException();
    }
    
    @Produces({ "application/xml" })
    @Consumes({ "application/xml" })
    @Singleton
    public static final class App extends XmlRootObjectJaxbProvider
    {
        public App(@Context final Provider<SAXParserFactory> spf, @Context final Providers ps) {
            super(spf, ps, MediaType.APPLICATION_XML_TYPE);
        }
    }
    
    @Produces({ "text/xml" })
    @Consumes({ "text/xml" })
    @Singleton
    public static final class Text extends XmlRootObjectJaxbProvider
    {
        public Text(@Context final Provider<SAXParserFactory> spf, @Context final Providers ps) {
            super(spf, ps, MediaType.TEXT_XML_TYPE);
        }
    }
    
    @Produces({ "*/*" })
    @Consumes({ "*/*" })
    @Singleton
    public static final class General extends XmlRootObjectJaxbProvider
    {
        public General(@Context final Provider<SAXParserFactory> spf, @Context final Providers ps) {
            super(spf, ps);
        }
        
        @Override
        protected boolean isSupported(final MediaType m) {
            return m.getSubtype().endsWith("+xml");
        }
    }
}
