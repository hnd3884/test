package org.glassfish.jersey.jaxb.internal;

import javax.ws.rs.core.Context;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.io.OutputStream;
import javax.xml.bind.Marshaller;
import java.nio.charset.Charset;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.bind.JAXBElement;
import java.io.InputStream;
import javax.xml.bind.Unmarshaller;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import javax.xml.parsers.SAXParserFactory;
import javax.inject.Provider;

public abstract class XmlJaxbElementProvider extends AbstractJaxbElementProvider
{
    private final Provider<SAXParserFactory> spf;
    
    public XmlJaxbElementProvider(final Provider<SAXParserFactory> spf, final Providers ps) {
        super(ps);
        this.spf = spf;
    }
    
    public XmlJaxbElementProvider(final Provider<SAXParserFactory> spf, final Providers ps, final MediaType mt) {
        super(ps, mt);
        this.spf = spf;
    }
    
    @Override
    protected final JAXBElement<?> readFrom(final Class<?> type, final MediaType mediaType, final Unmarshaller unmarshaller, final InputStream entityStream) throws JAXBException {
        return unmarshaller.unmarshal(AbstractJaxbProvider.getSAXSource((SAXParserFactory)this.spf.get(), entityStream), type);
    }
    
    @Override
    protected final void writeTo(final JAXBElement<?> t, final MediaType mediaType, final Charset c, final Marshaller m, final OutputStream entityStream) throws JAXBException {
        m.marshal(t, entityStream);
    }
    
    @Produces({ "application/xml" })
    @Consumes({ "application/xml" })
    @Singleton
    public static final class App extends XmlJaxbElementProvider
    {
        public App(@Context final Provider<SAXParserFactory> spf, @Context final Providers ps) {
            super(spf, ps, MediaType.APPLICATION_XML_TYPE);
        }
    }
    
    @Produces({ "text/xml" })
    @Consumes({ "text/xml" })
    @Singleton
    public static final class Text extends XmlJaxbElementProvider
    {
        public Text(@Context final Provider<SAXParserFactory> spf, @Context final Providers ps) {
            super(spf, ps, MediaType.TEXT_XML_TYPE);
        }
    }
    
    @Produces({ "*/*,*/*+xml" })
    @Consumes({ "*/*,*/*+xml" })
    @Singleton
    public static final class General extends XmlJaxbElementProvider
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
