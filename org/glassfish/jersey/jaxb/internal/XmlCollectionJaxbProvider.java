package org.glassfish.jersey.jaxb.internal;

import javax.ws.rs.core.Context;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import java.util.Iterator;
import javax.xml.bind.PropertyException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.OutputStream;
import javax.xml.bind.Marshaller;
import java.nio.charset.Charset;
import java.util.Collection;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import javax.xml.bind.Unmarshaller;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;
import javax.xml.stream.XMLInputFactory;
import javax.inject.Provider;

public abstract class XmlCollectionJaxbProvider extends AbstractCollectionJaxbProvider
{
    private final Provider<XMLInputFactory> xif;
    
    XmlCollectionJaxbProvider(final Provider<XMLInputFactory> xif, final Providers ps) {
        super(ps);
        this.xif = xif;
    }
    
    XmlCollectionJaxbProvider(final Provider<XMLInputFactory> xif, final Providers ps, final MediaType mt) {
        super(ps, mt);
        this.xif = xif;
    }
    
    @Override
    protected final XMLStreamReader getXMLStreamReader(final Class<?> elementType, final MediaType mediaType, final Unmarshaller u, final InputStream entityStream) throws XMLStreamException {
        return ((XMLInputFactory)this.xif.get()).createXMLStreamReader(entityStream);
    }
    
    @Override
    public final void writeCollection(final Class<?> elementType, final Collection<?> t, final MediaType mediaType, final Charset c, final Marshaller m, final OutputStream entityStream) throws JAXBException, IOException {
        final String rootElement = this.getRootElementName(elementType);
        final String cName = c.name();
        entityStream.write(String.format("<?xml version=\"1.0\" encoding=\"%s\" standalone=\"yes\"?>", cName).getBytes(cName));
        String property = "com.sun.xml.bind.xmlHeaders";
        String header;
        try {
            header = (String)m.getProperty(property);
        }
        catch (final PropertyException e) {
            property = "com.sun.xml.internal.bind.xmlHeaders";
            try {
                header = (String)m.getProperty(property);
            }
            catch (final PropertyException ex) {
                header = null;
                Logger.getLogger(XmlCollectionJaxbProvider.class.getName()).log(Level.WARNING, "@XmlHeader annotation is not supported with this JAXB implementation. Please use JAXB RI if you need this feature.");
            }
        }
        if (header != null) {
            m.setProperty(property, "");
            entityStream.write(header.getBytes(cName));
        }
        entityStream.write(String.format("<%s>", rootElement).getBytes(cName));
        for (final Object o : t) {
            m.marshal(o, entityStream);
        }
        entityStream.write(String.format("</%s>", rootElement).getBytes(cName));
    }
    
    @Produces({ "application/xml" })
    @Consumes({ "application/xml" })
    @Singleton
    public static final class App extends XmlCollectionJaxbProvider
    {
        public App(@Context final Provider<XMLInputFactory> xif, @Context final Providers ps) {
            super(xif, ps, MediaType.APPLICATION_XML_TYPE);
        }
    }
    
    @Produces({ "text/xml" })
    @Consumes({ "text/xml" })
    @Singleton
    public static final class Text extends XmlCollectionJaxbProvider
    {
        public Text(@Context final Provider<XMLInputFactory> xif, @Context final Providers ps) {
            super(xif, ps, MediaType.TEXT_XML_TYPE);
        }
    }
    
    @Produces({ "*/*" })
    @Consumes({ "*/*" })
    @Singleton
    public static final class General extends XmlCollectionJaxbProvider
    {
        public General(@Context final Provider<XMLInputFactory> xif, @Context final Providers ps) {
            super(xif, ps);
        }
        
        @Override
        protected boolean isSupported(final MediaType m) {
            return m.getSubtype().endsWith("+xml");
        }
    }
}
