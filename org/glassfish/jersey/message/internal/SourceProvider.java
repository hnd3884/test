package org.glassfish.jersey.message.internal;

import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import javax.xml.transform.TransformerFactory;
import javax.ws.rs.ext.MessageBodyWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import javax.ws.rs.InternalServerErrorException;
import org.xml.sax.SAXParseException;
import javax.ws.rs.BadRequestException;
import org.xml.sax.InputSource;
import javax.ws.rs.core.Context;
import javax.xml.parsers.SAXParserFactory;
import javax.inject.Provider;
import javax.xml.transform.sax.SAXSource;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.transform.Source;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.xml.transform.stream.StreamSource;
import javax.ws.rs.ext.MessageBodyReader;
import javax.inject.Singleton;

@Singleton
public final class SourceProvider
{
    @Produces({ "application/xml", "text/xml", "*/*" })
    @Consumes({ "application/xml", "text/xml", "*/*" })
    @Singleton
    public static final class StreamSourceReader implements MessageBodyReader<StreamSource>
    {
        public boolean isReadable(final Class<?> t, final Type gt, final Annotation[] as, final MediaType mediaType) {
            return StreamSource.class == t || Source.class == t;
        }
        
        public StreamSource readFrom(final Class<StreamSource> t, final Type gt, final Annotation[] as, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
            return new StreamSource(entityStream);
        }
    }
    
    @Produces({ "application/xml", "text/xml", "*/*" })
    @Consumes({ "application/xml", "text/xml", "*/*" })
    @Singleton
    public static final class SaxSourceReader implements MessageBodyReader<SAXSource>
    {
        private final Provider<SAXParserFactory> spf;
        
        public SaxSourceReader(@Context final Provider<SAXParserFactory> spf) {
            this.spf = spf;
        }
        
        public boolean isReadable(final Class<?> t, final Type gt, final Annotation[] as, final MediaType mediaType) {
            return SAXSource.class == t;
        }
        
        public SAXSource readFrom(final Class<SAXSource> t, final Type gt, final Annotation[] as, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
            try {
                return new SAXSource(((SAXParserFactory)this.spf.get()).newSAXParser().getXMLReader(), new InputSource(entityStream));
            }
            catch (final SAXParseException ex) {
                throw new BadRequestException((Throwable)ex);
            }
            catch (final SAXException ex2) {
                throw new InternalServerErrorException((Throwable)ex2);
            }
            catch (final ParserConfigurationException ex3) {
                throw new InternalServerErrorException((Throwable)ex3);
            }
        }
    }
    
    @Produces({ "application/xml", "text/xml", "*/*" })
    @Consumes({ "application/xml", "text/xml", "*/*" })
    @Singleton
    public static final class DomSourceReader implements MessageBodyReader<DOMSource>
    {
        private final Provider<DocumentBuilderFactory> dbf;
        
        public DomSourceReader(@Context final Provider<DocumentBuilderFactory> dbf) {
            this.dbf = dbf;
        }
        
        public boolean isReadable(final Class<?> t, final Type gt, final Annotation[] as, final MediaType mediaType) {
            return DOMSource.class == t;
        }
        
        public DOMSource readFrom(final Class<DOMSource> t, final Type gt, final Annotation[] as, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
            try {
                final Document d = ((DocumentBuilderFactory)this.dbf.get()).newDocumentBuilder().parse(entityStream);
                return new DOMSource(d);
            }
            catch (final SAXParseException ex) {
                throw new BadRequestException((Throwable)ex);
            }
            catch (final SAXException ex2) {
                throw new InternalServerErrorException((Throwable)ex2);
            }
            catch (final ParserConfigurationException ex3) {
                throw new InternalServerErrorException((Throwable)ex3);
            }
        }
    }
    
    @Produces({ "application/xml", "text/xml", "*/*" })
    @Consumes({ "application/xml", "text/xml", "*/*" })
    @Singleton
    public static final class SourceWriter implements MessageBodyWriter<Source>
    {
        private final Provider<SAXParserFactory> saxParserFactory;
        private final Provider<TransformerFactory> transformerFactory;
        
        public SourceWriter(@Context final Provider<SAXParserFactory> spf, @Context final Provider<TransformerFactory> tf) {
            this.saxParserFactory = spf;
            this.transformerFactory = tf;
        }
        
        public boolean isWriteable(final Class<?> t, final Type gt, final Annotation[] as, final MediaType mediaType) {
            return Source.class.isAssignableFrom(t);
        }
        
        public long getSize(final Source o, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
            return -1L;
        }
        
        public void writeTo(Source source, final Class<?> t, final Type gt, final Annotation[] as, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
            try {
                if (source instanceof StreamSource) {
                    final StreamSource stream = (StreamSource)source;
                    final InputSource inputStream = new InputSource(stream.getInputStream());
                    inputStream.setCharacterStream(inputStream.getCharacterStream());
                    inputStream.setPublicId(stream.getPublicId());
                    inputStream.setSystemId(source.getSystemId());
                    source = new SAXSource(((SAXParserFactory)this.saxParserFactory.get()).newSAXParser().getXMLReader(), inputStream);
                }
                final StreamResult sr = new StreamResult(entityStream);
                ((TransformerFactory)this.transformerFactory.get()).newTransformer().transform(source, sr);
            }
            catch (final SAXException ex) {
                throw new InternalServerErrorException((Throwable)ex);
            }
            catch (final ParserConfigurationException ex2) {
                throw new InternalServerErrorException((Throwable)ex2);
            }
            catch (final TransformerException ex3) {
                throw new InternalServerErrorException((Throwable)ex3);
            }
        }
    }
}
