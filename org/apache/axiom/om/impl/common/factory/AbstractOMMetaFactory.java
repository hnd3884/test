package org.apache.axiom.om.impl.common.factory;

import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.soap.impl.intf.AxiomSOAPMessage;
import org.apache.axiom.soap.impl.builder.MTOMStAXSOAPModelBuilder;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.om.impl.builder.XOPAwareStAXOMBuilder;
import org.apache.axiom.util.stax.xop.MimePartProvider;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.util.stax.XMLEventUtils;
import org.apache.axiom.util.stax.XMLFragmentStreamReader;
import java.io.Reader;
import java.io.Closeable;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.impl.builder.Detachable;
import java.io.InputStream;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMException;
import java.net.URL;
import org.apache.axiom.om.util.StAXUtils;
import org.xml.sax.InputSource;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.soap.impl.builder.OMMetaFactoryEx;

public abstract class AbstractOMMetaFactory implements OMMetaFactoryEx
{
    private static SourceInfo createXMLStreamReader(final StAXParserConfiguration configuration, final InputSource is, final boolean makeDetachable) {
        Detachable detachable;
        XMLStreamReader reader;
        Closeable closeable;
        try {
            if (is.getByteStream() != null) {
                final String systemId = is.getSystemId();
                final String encoding = is.getEncoding();
                InputStream in = is.getByteStream();
                if (makeDetachable) {
                    in = (InputStream)(detachable = (Detachable)new DetachableInputStream(in, false));
                }
                else {
                    detachable = null;
                }
                if (systemId != null) {
                    if (encoding != null) {
                        throw new UnsupportedOperationException();
                    }
                    reader = StAXUtils.createXMLStreamReader(configuration, systemId, in);
                }
                else if (encoding == null) {
                    reader = StAXUtils.createXMLStreamReader(configuration, in);
                }
                else {
                    reader = StAXUtils.createXMLStreamReader(configuration, in, encoding);
                }
                closeable = null;
            }
            else if (is.getCharacterStream() != null) {
                Reader in2 = is.getCharacterStream();
                if (makeDetachable) {
                    in2 = (Reader)(detachable = (Detachable)new DetachableReader(in2));
                }
                else {
                    detachable = null;
                }
                reader = StAXUtils.createXMLStreamReader(configuration, in2);
                closeable = null;
            }
            else {
                final String systemId = is.getSystemId();
                InputStream in3 = new URL(systemId).openConnection().getInputStream();
                if (makeDetachable) {
                    in3 = (InputStream)(detachable = (Detachable)new DetachableInputStream(in3, true));
                }
                else {
                    detachable = null;
                }
                reader = StAXUtils.createXMLStreamReader(configuration, systemId, in3);
                closeable = in3;
            }
        }
        catch (final XMLStreamException ex) {
            throw new OMException((Throwable)ex);
        }
        catch (final IOException ex2) {
            throw new OMException((Throwable)ex2);
        }
        return new SourceInfo(reader, detachable, closeable);
    }
    
    private static XMLStreamReader getXMLStreamReader(final XMLStreamReader originalReader) {
        final int eventType = originalReader.getEventType();
        switch (eventType) {
            case 7: {
                return originalReader;
            }
            case 1: {
                return (XMLStreamReader)new XMLFragmentStreamReader(originalReader);
            }
            default: {
                throw new OMException("The supplied XMLStreamReader is in an unexpected state (" + XMLEventUtils.getEventTypeString(eventType) + ")");
            }
        }
    }
    
    public OMXMLParserWrapper createStAXOMBuilder(final OMFactory omFactory, final XMLStreamReader parser) {
        return (OMXMLParserWrapper)new StAXOMBuilder(omFactory, getXMLStreamReader(parser), (Detachable)null, (Closeable)null);
    }
    
    public OMXMLParserWrapper createOMBuilder(final OMFactory omFactory, final StAXParserConfiguration configuration, final InputSource is) {
        final SourceInfo sourceInfo = createXMLStreamReader(configuration, is, true);
        final StAXOMBuilder builder = new StAXOMBuilder(omFactory, sourceInfo.getReader(), sourceInfo.getDetachable(), sourceInfo.getCloseable());
        builder.setAutoClose(true);
        return (OMXMLParserWrapper)builder;
    }
    
    private static InputSource toInputSource(final StreamSource source) {
        final InputSource is = new InputSource();
        is.setByteStream(source.getInputStream());
        is.setCharacterStream(source.getReader());
        is.setPublicId(source.getPublicId());
        is.setSystemId(source.getSystemId());
        return is;
    }
    
    public OMXMLParserWrapper createOMBuilder(final OMFactory omFactory, final Source source) {
        if (source instanceof SAXSource) {
            return this.createOMBuilder(omFactory, (SAXSource)source, true);
        }
        if (source instanceof DOMSource) {
            return this.createOMBuilder(omFactory, ((DOMSource)source).getNode(), true);
        }
        if (source instanceof StreamSource) {
            return this.createOMBuilder(omFactory, StAXParserConfiguration.DEFAULT, toInputSource((StreamSource)source));
        }
        try {
            return (OMXMLParserWrapper)new StAXOMBuilder(omFactory, StAXUtils.getXMLInputFactory().createXMLStreamReader(source), (Detachable)null, (Closeable)null);
        }
        catch (final XMLStreamException ex) {
            throw new OMException((Throwable)ex);
        }
    }
    
    public OMXMLParserWrapper createOMBuilder(final OMFactory omFactory, final Node node, final boolean expandEntityReferences) {
        return (OMXMLParserWrapper)new StAXOMBuilder(omFactory, (XMLStreamReader)new DOMXMLStreamReader(node, expandEntityReferences), (Detachable)null, (Closeable)null);
    }
    
    public OMXMLParserWrapper createOMBuilder(final OMFactory omFactory, final SAXSource source, final boolean expandEntityReferences) {
        return (OMXMLParserWrapper)new SAXOMBuilder(omFactory, source, expandEntityReferences);
    }
    
    public OMXMLParserWrapper createOMBuilder(final StAXParserConfiguration configuration, final OMFactory omFactory, final InputSource rootPart, final MimePartProvider mimePartProvider) {
        final SourceInfo sourceInfo = createXMLStreamReader(configuration, rootPart, false);
        final XOPAwareStAXOMBuilder builder = new XOPAwareStAXOMBuilder(omFactory, sourceInfo.getReader(), mimePartProvider, (Detachable)((mimePartProvider instanceof Detachable) ? mimePartProvider : null), sourceInfo.getCloseable());
        builder.setAutoClose(true);
        return (OMXMLParserWrapper)builder;
    }
    
    public SOAPModelBuilder createStAXSOAPModelBuilder(final XMLStreamReader parser) {
        return (SOAPModelBuilder)new StAXSOAPModelBuilder((OMMetaFactory)this, getXMLStreamReader(parser), (Detachable)null, (Closeable)null);
    }
    
    public SOAPModelBuilder createSOAPModelBuilder(final StAXParserConfiguration configuration, final InputSource is) {
        final SourceInfo sourceInfo = createXMLStreamReader(configuration, is, true);
        final StAXSOAPModelBuilder builder = new StAXSOAPModelBuilder((OMMetaFactory)this, sourceInfo.getReader(), sourceInfo.getDetachable(), sourceInfo.getCloseable());
        builder.setAutoClose(true);
        return (SOAPModelBuilder)builder;
    }
    
    public SOAPModelBuilder createSOAPModelBuilder(final Source source) {
        if (source instanceof SAXSource) {
            throw new UnsupportedOperationException();
        }
        if (source instanceof DOMSource) {
            return (SOAPModelBuilder)new StAXSOAPModelBuilder((OMMetaFactory)this, (XMLStreamReader)new DOMXMLStreamReader(((DOMSource)source).getNode(), true), (Detachable)null, (Closeable)null);
        }
        if (source instanceof StreamSource) {
            return this.createSOAPModelBuilder(StAXParserConfiguration.SOAP, toInputSource((StreamSource)source));
        }
        try {
            return (SOAPModelBuilder)new StAXSOAPModelBuilder((OMMetaFactory)this, StAXUtils.getXMLInputFactory().createXMLStreamReader(source), (Detachable)null, (Closeable)null);
        }
        catch (final XMLStreamException ex) {
            throw new OMException((Throwable)ex);
        }
    }
    
    public SOAPModelBuilder createSOAPModelBuilder(final StAXParserConfiguration configuration, final SOAPFactory soapFactory, final InputSource rootPart, final MimePartProvider mimePartProvider) {
        final SourceInfo sourceInfo = createXMLStreamReader(configuration, rootPart, false);
        final MTOMStAXSOAPModelBuilder builder = new MTOMStAXSOAPModelBuilder(soapFactory, sourceInfo.getReader(), mimePartProvider, (Detachable)((mimePartProvider instanceof Detachable) ? mimePartProvider : null), sourceInfo.getCloseable());
        builder.setAutoClose(true);
        return (SOAPModelBuilder)builder;
    }
    
    public abstract AxiomSOAPMessage createSOAPMessage();
    
    public final SOAPMessage createSOAPMessage(final OMXMLParserWrapper builder) {
        final AxiomSOAPMessage message = this.createSOAPMessage();
        message.coreSetBuilder(builder);
        return (SOAPMessage)message;
    }
    
    private static final class SourceInfo
    {
        private final XMLStreamReader reader;
        private final Detachable detachable;
        private final Closeable closeable;
        
        SourceInfo(final XMLStreamReader reader, final Detachable detachable, final Closeable closeable) {
            this.reader = reader;
            this.detachable = detachable;
            this.closeable = closeable;
        }
        
        XMLStreamReader getReader() {
            return this.reader;
        }
        
        Detachable getDetachable() {
            return this.detachable;
        }
        
        Closeable getCloseable() {
            return this.closeable;
        }
    }
}
