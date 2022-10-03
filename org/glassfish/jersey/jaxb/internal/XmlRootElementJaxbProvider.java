package org.glassfish.jersey.jaxb.internal;

import javax.ws.rs.core.Context;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.xml.bind.JAXBException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.Source;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.InputStream;
import javax.xml.bind.Unmarshaller;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import javax.xml.parsers.SAXParserFactory;
import javax.inject.Provider;

public abstract class XmlRootElementJaxbProvider extends AbstractRootElementJaxbProvider
{
    private final Provider<SAXParserFactory> spf;
    
    XmlRootElementJaxbProvider(final Provider<SAXParserFactory> spf, final Providers ps) {
        super(ps);
        this.spf = spf;
    }
    
    XmlRootElementJaxbProvider(final Provider<SAXParserFactory> spf, final Providers ps, final MediaType mt) {
        super(ps, mt);
        this.spf = spf;
    }
    
    @Override
    protected Object readFrom(final Class<Object> type, final MediaType mediaType, final Unmarshaller u, final InputStream entityStream) throws JAXBException {
        final SAXSource s = AbstractJaxbProvider.getSAXSource((SAXParserFactory)this.spf.get(), entityStream);
        if (type.isAnnotationPresent(XmlRootElement.class)) {
            return u.unmarshal(s);
        }
        return u.unmarshal(s, type).getValue();
    }
    
    @Produces({ "application/xml" })
    @Consumes({ "application/xml" })
    @Singleton
    public static final class App extends XmlRootElementJaxbProvider
    {
        public App(@Context final Provider<SAXParserFactory> spf, @Context final Providers ps) {
            super(spf, ps, MediaType.APPLICATION_XML_TYPE);
        }
    }
    
    @Produces({ "text/xml" })
    @Consumes({ "text/xml" })
    @Singleton
    public static final class Text extends XmlRootElementJaxbProvider
    {
        public Text(@Context final Provider<SAXParserFactory> spf, @Context final Providers ps) {
            super(spf, ps, MediaType.TEXT_XML_TYPE);
        }
    }
    
    @Produces({ "*/*" })
    @Consumes({ "*/*" })
    @Singleton
    public static final class General extends XmlRootElementJaxbProvider
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
