package com.sun.xml.internal.ws.message.source;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.streaming.SourceReaderFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import org.w3c.dom.Node;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.ws.WebServiceException;
import javax.xml.transform.Result;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import org.xml.sax.ContentHandler;
import javax.xml.transform.sax.SAXResult;
import org.w3c.dom.Document;
import com.sun.xml.internal.ws.message.RootElementSniffer;
import javax.xml.namespace.QName;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Source;

final class SourceUtils
{
    int srcType;
    private static final int domSource = 1;
    private static final int streamSource = 2;
    private static final int saxSource = 4;
    
    public SourceUtils(final Source src) {
        if (src instanceof StreamSource) {
            this.srcType = 2;
        }
        else if (src instanceof DOMSource) {
            this.srcType = 1;
        }
        else if (src instanceof SAXSource) {
            this.srcType = 4;
        }
    }
    
    public boolean isDOMSource() {
        return (this.srcType & 0x1) == 0x1;
    }
    
    public boolean isStreamSource() {
        return (this.srcType & 0x2) == 0x2;
    }
    
    public boolean isSaxSource() {
        return (this.srcType & 0x4) == 0x4;
    }
    
    public QName sniff(final Source src) {
        return this.sniff(src, new RootElementSniffer());
    }
    
    public QName sniff(final Source src, final RootElementSniffer sniffer) {
        String localName = null;
        String namespaceUri = null;
        if (this.isDOMSource()) {
            final DOMSource domSrc = (DOMSource)src;
            Node n = domSrc.getNode();
            if (n.getNodeType() == 9) {
                n = ((Document)n).getDocumentElement();
            }
            localName = n.getLocalName();
            namespaceUri = n.getNamespaceURI();
        }
        else if (this.isSaxSource()) {
            final SAXSource saxSrc = (SAXSource)src;
            final SAXResult saxResult = new SAXResult(sniffer);
            try {
                final Transformer tr = XmlUtil.newTransformer();
                tr.transform(saxSrc, saxResult);
            }
            catch (final TransformerConfigurationException e) {
                throw new WebServiceException(e);
            }
            catch (final TransformerException e2) {
                localName = sniffer.getLocalName();
                namespaceUri = sniffer.getNsUri();
            }
        }
        return new QName(namespaceUri, localName);
    }
    
    public static void serializeSource(final Source src, final XMLStreamWriter writer) throws XMLStreamException {
        final XMLStreamReader reader = SourceReaderFactory.createSourceReader(src, true);
        int state;
        do {
            state = reader.next();
            switch (state) {
                default: {
                    continue;
                }
                case 1: {
                    final String uri = reader.getNamespaceURI();
                    final String prefix = reader.getPrefix();
                    final String localName = reader.getLocalName();
                    if (prefix == null) {
                        if (uri == null) {
                            writer.writeStartElement(localName);
                        }
                        else {
                            writer.writeStartElement(uri, localName);
                        }
                    }
                    else if (prefix.length() > 0) {
                        String writerURI = null;
                        if (writer.getNamespaceContext() != null) {
                            writerURI = writer.getNamespaceContext().getNamespaceURI(prefix);
                        }
                        final String writerPrefix = writer.getPrefix(uri);
                        if (declarePrefix(prefix, uri, writerPrefix, writerURI)) {
                            writer.writeStartElement(prefix, localName, uri);
                            writer.setPrefix(prefix, (uri != null) ? uri : "");
                            writer.writeNamespace(prefix, uri);
                        }
                        else {
                            writer.writeStartElement(prefix, localName, uri);
                        }
                    }
                    else {
                        writer.writeStartElement(prefix, localName, uri);
                    }
                    for (int n = reader.getNamespaceCount(), i = 0; i < n; ++i) {
                        String nsPrefix = reader.getNamespacePrefix(i);
                        if (nsPrefix == null) {
                            nsPrefix = "";
                        }
                        String writerURI2 = null;
                        if (writer.getNamespaceContext() != null) {
                            writerURI2 = writer.getNamespaceContext().getNamespaceURI(nsPrefix);
                        }
                        final String readerURI = reader.getNamespaceURI(i);
                        if (writerURI2 == null || nsPrefix.length() == 0 || prefix.length() == 0 || (!nsPrefix.equals(prefix) && !writerURI2.equals(readerURI))) {
                            writer.setPrefix(nsPrefix, (readerURI != null) ? readerURI : "");
                            writer.writeNamespace(nsPrefix, (readerURI != null) ? readerURI : "");
                        }
                    }
                    for (int n = reader.getAttributeCount(), i = 0; i < n; ++i) {
                        final String attrPrefix = reader.getAttributePrefix(i);
                        final String attrURI = reader.getAttributeNamespace(i);
                        writer.writeAttribute((attrPrefix != null) ? attrPrefix : "", (attrURI != null) ? attrURI : "", reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                        setUndeclaredPrefix(attrPrefix, attrURI, writer);
                    }
                    continue;
                }
                case 2: {
                    writer.writeEndElement();
                    continue;
                }
                case 4: {
                    writer.writeCharacters(reader.getText());
                    continue;
                }
            }
        } while (state != 8);
        reader.close();
    }
    
    private static void setUndeclaredPrefix(final String prefix, final String readerURI, final XMLStreamWriter writer) throws XMLStreamException {
        String writerURI = null;
        if (writer.getNamespaceContext() != null) {
            writerURI = writer.getNamespaceContext().getNamespaceURI(prefix);
        }
        if (writerURI == null) {
            writer.setPrefix(prefix, (readerURI != null) ? readerURI : "");
            writer.writeNamespace(prefix, (readerURI != null) ? readerURI : "");
        }
    }
    
    private static boolean declarePrefix(final String rPrefix, final String rUri, final String wPrefix, final String wUri) {
        return wUri == null || (wPrefix != null && !rPrefix.equals(wPrefix)) || (rUri != null && !wUri.equals(rUri));
    }
}
