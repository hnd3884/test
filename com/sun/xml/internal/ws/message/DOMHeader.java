package com.sun.xml.internal.ws.message;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import com.sun.xml.internal.ws.util.DOMUtil;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.bind.api.Bridge;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import org.w3c.dom.Node;
import com.sun.xml.internal.ws.streaming.DOMStreamReader;
import javax.xml.stream.XMLStreamReader;
import org.w3c.dom.Element;

public class DOMHeader<N extends Element> extends AbstractHeaderImpl
{
    protected final N node;
    private final String nsUri;
    private final String localName;
    
    public DOMHeader(final N node) {
        assert node != null;
        this.node = node;
        this.nsUri = fixNull(node.getNamespaceURI());
        this.localName = node.getLocalName();
    }
    
    @Override
    public String getNamespaceURI() {
        return this.nsUri;
    }
    
    @Override
    public String getLocalPart() {
        return this.localName;
    }
    
    @Override
    public XMLStreamReader readHeader() throws XMLStreamException {
        final DOMStreamReader r = new DOMStreamReader(this.node);
        r.nextTag();
        return r;
    }
    
    @Override
    public <T> T readAsJAXB(final Unmarshaller unmarshaller) throws JAXBException {
        return (T)unmarshaller.unmarshal(this.node);
    }
    
    @Override
    @Deprecated
    public <T> T readAsJAXB(final Bridge<T> bridge) throws JAXBException {
        return bridge.unmarshal(this.node);
    }
    
    @Override
    public void writeTo(final XMLStreamWriter w) throws XMLStreamException {
        DOMUtil.serializeNode(this.node, w);
    }
    
    private static String fixNull(final String s) {
        if (s != null) {
            return s;
        }
        return "";
    }
    
    @Override
    public void writeTo(final ContentHandler contentHandler, final ErrorHandler errorHandler) throws SAXException {
        final DOMScanner ds = new DOMScanner();
        ds.setContentHandler(contentHandler);
        ds.scan(this.node);
    }
    
    @Override
    public String getAttribute(String nsUri, final String localName) {
        if (nsUri.length() == 0) {
            nsUri = null;
        }
        return this.node.getAttributeNS(nsUri, localName);
    }
    
    @Override
    public void writeTo(final SOAPMessage saaj) throws SOAPException {
        SOAPHeader header = saaj.getSOAPHeader();
        if (header == null) {
            header = saaj.getSOAPPart().getEnvelope().addHeader();
        }
        final Node clone = header.getOwnerDocument().importNode(this.node, true);
        header.appendChild(clone);
    }
    
    @Override
    public String getStringContent() {
        return this.node.getTextContent();
    }
    
    public N getWrappedNode() {
        return this.node;
    }
    
    @Override
    public int hashCode() {
        return this.getWrappedNode().hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof DOMHeader && this.getWrappedNode().equals(((DOMHeader)obj).getWrappedNode());
    }
}
