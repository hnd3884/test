package com.sun.xml.internal.messaging.saaj.util.transform;

import java.io.Writer;
import java.io.Reader;
import java.io.OutputStream;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import javax.xml.transform.dom.DOMResult;
import com.sun.xml.internal.messaging.saaj.util.FastInfosetReflection;
import java.io.OutputStreamWriter;
import com.sun.xml.internal.messaging.saaj.util.XMLDeclarationParser;
import java.io.PushbackReader;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.IOException;
import java.net.URISyntaxException;
import java.io.FileInputStream;
import java.io.File;
import java.net.URI;
import java.io.InputStream;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.URIResolver;
import java.util.Properties;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;

public class EfficientStreamingTransformer extends Transformer
{
    private final TransformerFactory transformerFactory;
    private Transformer m_realTransformer;
    private Object m_fiDOMDocumentParser;
    private Object m_fiDOMDocumentSerializer;
    
    private EfficientStreamingTransformer() {
        this.transformerFactory = TransformerFactory.newInstance();
        this.m_realTransformer = null;
        this.m_fiDOMDocumentParser = null;
        this.m_fiDOMDocumentSerializer = null;
    }
    
    private void materialize() throws TransformerException {
        if (this.m_realTransformer == null) {
            this.m_realTransformer = this.transformerFactory.newTransformer();
        }
    }
    
    @Override
    public void clearParameters() {
        if (this.m_realTransformer != null) {
            this.m_realTransformer.clearParameters();
        }
    }
    
    @Override
    public ErrorListener getErrorListener() {
        try {
            this.materialize();
            return this.m_realTransformer.getErrorListener();
        }
        catch (final TransformerException ex) {
            return null;
        }
    }
    
    @Override
    public Properties getOutputProperties() {
        try {
            this.materialize();
            return this.m_realTransformer.getOutputProperties();
        }
        catch (final TransformerException ex) {
            return null;
        }
    }
    
    @Override
    public String getOutputProperty(final String str) throws IllegalArgumentException {
        try {
            this.materialize();
            return this.m_realTransformer.getOutputProperty(str);
        }
        catch (final TransformerException ex) {
            return null;
        }
    }
    
    @Override
    public Object getParameter(final String str) {
        try {
            this.materialize();
            return this.m_realTransformer.getParameter(str);
        }
        catch (final TransformerException ex) {
            return null;
        }
    }
    
    @Override
    public URIResolver getURIResolver() {
        try {
            this.materialize();
            return this.m_realTransformer.getURIResolver();
        }
        catch (final TransformerException ex) {
            return null;
        }
    }
    
    @Override
    public void setErrorListener(final ErrorListener errorListener) throws IllegalArgumentException {
        try {
            this.materialize();
            this.m_realTransformer.setErrorListener(errorListener);
        }
        catch (final TransformerException ex) {}
    }
    
    @Override
    public void setOutputProperties(final Properties properties) throws IllegalArgumentException {
        try {
            this.materialize();
            this.m_realTransformer.setOutputProperties(properties);
        }
        catch (final TransformerException ex) {}
    }
    
    @Override
    public void setOutputProperty(final String str, final String str1) throws IllegalArgumentException {
        try {
            this.materialize();
            this.m_realTransformer.setOutputProperty(str, str1);
        }
        catch (final TransformerException ex) {}
    }
    
    @Override
    public void setParameter(final String str, final Object obj) {
        try {
            this.materialize();
            this.m_realTransformer.setParameter(str, obj);
        }
        catch (final TransformerException ex) {}
    }
    
    @Override
    public void setURIResolver(final URIResolver uRIResolver) {
        try {
            this.materialize();
            this.m_realTransformer.setURIResolver(uRIResolver);
        }
        catch (final TransformerException ex) {}
    }
    
    private InputStream getInputStreamFromSource(final StreamSource s) throws TransformerException {
        final InputStream stream = s.getInputStream();
        if (stream != null) {
            return stream;
        }
        if (s.getReader() != null) {
            return null;
        }
        final String systemId = s.getSystemId();
        if (systemId != null) {
            try {
                String fileURL = systemId;
                if (systemId.startsWith("file:///")) {
                    final String absolutePath = systemId.substring(7);
                    final boolean hasDriveDesignator = absolutePath.indexOf(":") > 0;
                    if (hasDriveDesignator) {
                        final String driveDesignatedPath = fileURL = absolutePath.substring(1);
                    }
                    else {
                        fileURL = absolutePath;
                    }
                }
                try {
                    return new FileInputStream(new File(new URI(fileURL)));
                }
                catch (final URISyntaxException ex) {
                    throw new TransformerException(ex);
                }
            }
            catch (final IOException e) {
                throw new TransformerException(e.toString());
            }
        }
        throw new TransformerException("Unexpected StreamSource object");
    }
    
    @Override
    public void transform(final Source source, final Result result) throws TransformerException {
        if (source instanceof StreamSource && result instanceof StreamResult) {
            try {
                final StreamSource streamSource = (StreamSource)source;
                final InputStream is = this.getInputStreamFromSource(streamSource);
                final OutputStream os = ((StreamResult)result).getOutputStream();
                if (os == null) {
                    throw new TransformerException("Unexpected StreamResult object contains null OutputStream");
                }
                if (is != null) {
                    if (is.markSupported()) {
                        is.mark(Integer.MAX_VALUE);
                    }
                    final byte[] b = new byte[8192];
                    int num;
                    while ((num = is.read(b)) != -1) {
                        os.write(b, 0, num);
                    }
                    if (is.markSupported()) {
                        is.reset();
                    }
                    return;
                }
                final Reader reader = streamSource.getReader();
                if (reader != null) {
                    if (reader.markSupported()) {
                        reader.mark(Integer.MAX_VALUE);
                    }
                    final PushbackReader pushbackReader = new PushbackReader(reader, 4096);
                    final XMLDeclarationParser ev = new XMLDeclarationParser(pushbackReader);
                    try {
                        ev.parse();
                    }
                    catch (final Exception ex) {
                        throw new TransformerException("Unable to run the JAXP transformer on a stream " + ex.getMessage());
                    }
                    final Writer writer = new OutputStreamWriter(os);
                    ev.writeTo(writer);
                    final char[] ac = new char[8192];
                    int num2;
                    while ((num2 = pushbackReader.read(ac)) != -1) {
                        writer.write(ac, 0, num2);
                    }
                    writer.flush();
                    if (reader.markSupported()) {
                        reader.reset();
                    }
                    return;
                }
            }
            catch (final IOException e) {
                e.printStackTrace();
                throw new TransformerException(e.toString());
            }
            throw new TransformerException("Unexpected StreamSource object");
        }
        if (FastInfosetReflection.isFastInfosetSource(source) && result instanceof DOMResult) {
            try {
                if (this.m_fiDOMDocumentParser == null) {
                    this.m_fiDOMDocumentParser = FastInfosetReflection.DOMDocumentParser_new();
                }
                FastInfosetReflection.DOMDocumentParser_parse(this.m_fiDOMDocumentParser, (Document)((DOMResult)result).getNode(), FastInfosetReflection.FastInfosetSource_getInputStream(source));
                return;
            }
            catch (final Exception e2) {
                throw new TransformerException(e2);
            }
        }
        if (source instanceof DOMSource && FastInfosetReflection.isFastInfosetResult(result)) {
            try {
                if (this.m_fiDOMDocumentSerializer == null) {
                    this.m_fiDOMDocumentSerializer = FastInfosetReflection.DOMDocumentSerializer_new();
                }
                FastInfosetReflection.DOMDocumentSerializer_setOutputStream(this.m_fiDOMDocumentSerializer, FastInfosetReflection.FastInfosetResult_getOutputStream(result));
                FastInfosetReflection.DOMDocumentSerializer_serialize(this.m_fiDOMDocumentSerializer, ((DOMSource)source).getNode());
                return;
            }
            catch (final Exception e2) {
                throw new TransformerException(e2);
            }
        }
        this.materialize();
        this.m_realTransformer.transform(source, result);
    }
    
    public static Transformer newTransformer() {
        return new EfficientStreamingTransformer();
    }
}
