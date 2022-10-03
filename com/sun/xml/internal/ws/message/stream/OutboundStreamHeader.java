package com.sun.xml.internal.ws.message.stream;

import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import javax.xml.soap.SOAPHeader;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferException;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Node;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;

public final class OutboundStreamHeader extends AbstractHeaderImpl
{
    private final XMLStreamBuffer infoset;
    private final String nsUri;
    private final String localName;
    private FinalArrayList<Attribute> attributes;
    private static final String TRUE_VALUE = "1";
    private static final String IS_REFERENCE_PARAMETER = "IsReferenceParameter";
    
    public OutboundStreamHeader(final XMLStreamBuffer infoset, final String nsUri, final String localName) {
        this.infoset = infoset;
        this.nsUri = nsUri;
        this.localName = localName;
    }
    
    @NotNull
    @Override
    public String getNamespaceURI() {
        return this.nsUri;
    }
    
    @NotNull
    @Override
    public String getLocalPart() {
        return this.localName;
    }
    
    @Override
    public String getAttribute(final String nsUri, final String localName) {
        if (this.attributes == null) {
            this.parseAttributes();
        }
        for (int i = this.attributes.size() - 1; i >= 0; --i) {
            final Attribute a = this.attributes.get(i);
            if (a.localName.equals(localName) && a.nsUri.equals(nsUri)) {
                return a.value;
            }
        }
        return null;
    }
    
    private void parseAttributes() {
        try {
            final XMLStreamReader reader = this.readHeader();
            this.attributes = new FinalArrayList<Attribute>();
            for (int i = 0; i < reader.getAttributeCount(); ++i) {
                final String localName = reader.getAttributeLocalName(i);
                final String namespaceURI = reader.getAttributeNamespace(i);
                final String value = reader.getAttributeValue(i);
                this.attributes.add(new Attribute(namespaceURI, localName, value));
            }
        }
        catch (final XMLStreamException e) {
            throw new WebServiceException("Unable to read the attributes for {" + this.nsUri + "}" + this.localName + " header", e);
        }
    }
    
    @Override
    public XMLStreamReader readHeader() throws XMLStreamException {
        return this.infoset.readAsXMLStreamReader();
    }
    
    @Override
    public void writeTo(final XMLStreamWriter w) throws XMLStreamException {
        this.infoset.writeToXMLStreamWriter(w, true);
    }
    
    @Override
    public void writeTo(final SOAPMessage saaj) throws SOAPException {
        try {
            SOAPHeader header = saaj.getSOAPHeader();
            if (header == null) {
                header = saaj.getSOAPPart().getEnvelope().addHeader();
            }
            this.infoset.writeTo(header);
        }
        catch (final XMLStreamBufferException e) {
            throw new SOAPException(e);
        }
    }
    
    @Override
    public void writeTo(final ContentHandler contentHandler, final ErrorHandler errorHandler) throws SAXException {
        this.infoset.writeTo(contentHandler, errorHandler);
    }
    
    static final class Attribute
    {
        final String nsUri;
        final String localName;
        final String value;
        
        public Attribute(final String nsUri, final String localName, final String value) {
            this.nsUri = fixNull(nsUri);
            this.localName = localName;
            this.value = value;
        }
        
        private static String fixNull(final String s) {
            if (s == null) {
                return "";
            }
            return s;
        }
    }
}
