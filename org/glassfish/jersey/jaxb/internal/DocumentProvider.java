package org.glassfish.jersey.jaxb.internal;

import javax.ws.rs.WebApplicationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.ws.rs.InternalServerErrorException;
import org.xml.sax.SAXException;
import javax.ws.rs.BadRequestException;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.xml.transform.TransformerFactory;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import org.w3c.dom.Document;
import org.glassfish.jersey.message.internal.AbstractMessageReaderWriterProvider;

@Produces({ "application/xml", "text/xml", "*/*" })
@Consumes({ "application/xml", "text/xml", "*/*" })
@Singleton
public final class DocumentProvider extends AbstractMessageReaderWriterProvider<Document>
{
    @Inject
    private Provider<DocumentBuilderFactory> dbf;
    @Inject
    private Provider<TransformerFactory> tf;
    
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return Document.class == type;
    }
    
    public Document readFrom(final Class<Document> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        try {
            return ((DocumentBuilderFactory)this.dbf.get()).newDocumentBuilder().parse(entityStream);
        }
        catch (final SAXException ex) {
            throw new BadRequestException((Throwable)ex);
        }
        catch (final ParserConfigurationException ex2) {
            throw new InternalServerErrorException((Throwable)ex2);
        }
    }
    
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return Document.class.isAssignableFrom(type);
    }
    
    public void writeTo(final Document t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        try {
            final StreamResult sr = new StreamResult(entityStream);
            ((TransformerFactory)this.tf.get()).newTransformer().transform(new DOMSource(t), sr);
        }
        catch (final TransformerException ex) {
            throw new InternalServerErrorException((Throwable)ex);
        }
    }
}
