package com.sun.xml.internal.ws.message.jaxb;

import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Node;
import javax.xml.soap.SOAPMessage;
import java.io.OutputStream;
import com.sun.istack.internal.XMLStreamException2;
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.transform.Source;
import com.sun.xml.internal.bind.api.Bridge;
import org.xml.sax.SAXException;
import javax.xml.transform.Result;
import javax.xml.bind.util.JAXBResult;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import javax.xml.stream.XMLStreamReader;
import com.sun.istack.internal.NotNull;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import org.xml.sax.ContentHandler;
import com.sun.xml.internal.ws.message.RootElementSniffer;
import javax.xml.namespace.QName;
import javax.xml.bind.JAXBElement;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import org.xml.sax.Attributes;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;

public final class JAXBHeader extends AbstractHeaderImpl
{
    private final Object jaxbObject;
    private final XMLBridge bridge;
    private String nsUri;
    private String localName;
    private Attributes atts;
    private XMLStreamBuffer infoset;
    
    public JAXBHeader(final BindingContext context, final Object jaxbObject) {
        this.jaxbObject = jaxbObject;
        this.bridge = context.createFragmentBridge();
        if (jaxbObject instanceof JAXBElement) {
            final JAXBElement e = (JAXBElement)jaxbObject;
            this.nsUri = e.getName().getNamespaceURI();
            this.localName = e.getName().getLocalPart();
        }
    }
    
    public JAXBHeader(final XMLBridge bridge, final Object jaxbObject) {
        this.jaxbObject = jaxbObject;
        this.bridge = bridge;
        final QName tagName = bridge.getTypeInfo().tagName;
        this.nsUri = tagName.getNamespaceURI();
        this.localName = tagName.getLocalPart();
    }
    
    private void parse() {
        final RootElementSniffer sniffer = new RootElementSniffer();
        try {
            this.bridge.marshal(this.jaxbObject, sniffer, null);
        }
        catch (final JAXBException e) {
            this.nsUri = sniffer.getNsUri();
            this.localName = sniffer.getLocalName();
            this.atts = sniffer.getAttributes();
        }
    }
    
    @NotNull
    @Override
    public String getNamespaceURI() {
        if (this.nsUri == null) {
            this.parse();
        }
        return this.nsUri;
    }
    
    @NotNull
    @Override
    public String getLocalPart() {
        if (this.localName == null) {
            this.parse();
        }
        return this.localName;
    }
    
    @Override
    public String getAttribute(final String nsUri, final String localName) {
        if (this.atts == null) {
            this.parse();
        }
        return this.atts.getValue(nsUri, localName);
    }
    
    @Override
    public XMLStreamReader readHeader() throws XMLStreamException {
        if (this.infoset == null) {
            final MutableXMLStreamBuffer buffer = new MutableXMLStreamBuffer();
            this.writeTo(buffer.createFromXMLStreamWriter());
            this.infoset = buffer;
        }
        return this.infoset.readAsXMLStreamReader();
    }
    
    @Override
    public <T> T readAsJAXB(final Unmarshaller unmarshaller) throws JAXBException {
        try {
            final JAXBResult r = new JAXBResult(unmarshaller);
            r.getHandler().startDocument();
            this.bridge.marshal(this.jaxbObject, r);
            r.getHandler().endDocument();
            return (T)r.getResult();
        }
        catch (final SAXException e) {
            throw new JAXBException(e);
        }
    }
    
    @Override
    @Deprecated
    public <T> T readAsJAXB(final Bridge<T> bridge) throws JAXBException {
        return bridge.unmarshal(new JAXBBridgeSource(this.bridge, this.jaxbObject));
    }
    
    @Override
    public <T> T readAsJAXB(final XMLBridge<T> bond) throws JAXBException {
        return bond.unmarshal(new JAXBBridgeSource(this.bridge, this.jaxbObject), null);
    }
    
    @Override
    public void writeTo(final XMLStreamWriter sw) throws XMLStreamException {
        try {
            final String encoding = XMLStreamWriterUtil.getEncoding(sw);
            final OutputStream os = this.bridge.supportOutputStream() ? XMLStreamWriterUtil.getOutputStream(sw) : null;
            if (os != null && encoding != null && encoding.equalsIgnoreCase("utf-8")) {
                this.bridge.marshal(this.jaxbObject, os, sw.getNamespaceContext(), null);
            }
            else {
                this.bridge.marshal(this.jaxbObject, sw, null);
            }
        }
        catch (final JAXBException e) {
            throw new XMLStreamException2(e);
        }
    }
    
    @Override
    public void writeTo(final SOAPMessage saaj) throws SOAPException {
        try {
            SOAPHeader header = saaj.getSOAPHeader();
            if (header == null) {
                header = saaj.getSOAPPart().getEnvelope().addHeader();
            }
            this.bridge.marshal(this.jaxbObject, header);
        }
        catch (final JAXBException e) {
            throw new SOAPException(e);
        }
    }
    
    @Override
    public void writeTo(final ContentHandler contentHandler, final ErrorHandler errorHandler) throws SAXException {
        try {
            this.bridge.marshal(this.jaxbObject, contentHandler, null);
        }
        catch (final JAXBException e) {
            final SAXParseException x = new SAXParseException(e.getMessage(), null, null, -1, -1, e);
            errorHandler.fatalError(x);
            throw x;
        }
    }
}
