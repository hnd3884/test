package com.sun.xml.internal.ws.api.addressing;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import javax.xml.soap.SOAPHeader;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferException;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.xml.soap.SOAPMessage;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.namespace.QName;
import javax.xml.stream.util.StreamReaderDelegate;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;

final class OutboundReferenceParameterHeader extends AbstractHeaderImpl
{
    private final XMLStreamBuffer infoset;
    private final String nsUri;
    private final String localName;
    private FinalArrayList<Attribute> attributes;
    private static final String TRUE_VALUE = "1";
    private static final String IS_REFERENCE_PARAMETER = "IsReferenceParameter";
    
    OutboundReferenceParameterHeader(final XMLStreamBuffer infoset, final String nsUri, final String localName) {
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
            reader.nextTag();
            this.attributes = new FinalArrayList<Attribute>();
            boolean refParamAttrWritten = false;
            for (int i = 0; i < reader.getAttributeCount(); ++i) {
                final String attrLocalName = reader.getAttributeLocalName(i);
                final String namespaceURI = reader.getAttributeNamespace(i);
                final String value = reader.getAttributeValue(i);
                if (namespaceURI.equals(AddressingVersion.W3C.nsUri) && attrLocalName.equals("IS_REFERENCE_PARAMETER")) {
                    refParamAttrWritten = true;
                }
                this.attributes.add(new Attribute(namespaceURI, attrLocalName, value));
            }
            if (!refParamAttrWritten) {
                this.attributes.add(new Attribute(AddressingVersion.W3C.nsUri, "IsReferenceParameter", "1"));
            }
        }
        catch (final XMLStreamException e) {
            throw new WebServiceException("Unable to read the attributes for {" + this.nsUri + "}" + this.localName + " header", e);
        }
    }
    
    @Override
    public XMLStreamReader readHeader() throws XMLStreamException {
        return new StreamReaderDelegate(this.infoset.readAsXMLStreamReader()) {
            int state = 0;
            
            @Override
            public int next() throws XMLStreamException {
                return this.check(super.next());
            }
            
            @Override
            public int nextTag() throws XMLStreamException {
                return this.check(super.nextTag());
            }
            
            private int check(final int type) {
                switch (this.state) {
                    case 0: {
                        if (type == 1) {
                            this.state = 1;
                            break;
                        }
                        break;
                    }
                    case 1: {
                        this.state = 2;
                        break;
                    }
                }
                return type;
            }
            
            @Override
            public int getAttributeCount() {
                if (this.state == 1) {
                    return super.getAttributeCount() + 1;
                }
                return super.getAttributeCount();
            }
            
            @Override
            public String getAttributeLocalName(final int index) {
                if (this.state == 1 && index == super.getAttributeCount()) {
                    return "IsReferenceParameter";
                }
                return super.getAttributeLocalName(index);
            }
            
            @Override
            public String getAttributeNamespace(final int index) {
                if (this.state == 1 && index == super.getAttributeCount()) {
                    return AddressingVersion.W3C.nsUri;
                }
                return super.getAttributeNamespace(index);
            }
            
            @Override
            public String getAttributePrefix(final int index) {
                if (this.state == 1 && index == super.getAttributeCount()) {
                    return "wsa";
                }
                return super.getAttributePrefix(index);
            }
            
            @Override
            public String getAttributeType(final int index) {
                if (this.state == 1 && index == super.getAttributeCount()) {
                    return "CDATA";
                }
                return super.getAttributeType(index);
            }
            
            @Override
            public String getAttributeValue(final int index) {
                if (this.state == 1 && index == super.getAttributeCount()) {
                    return "1";
                }
                return super.getAttributeValue(index);
            }
            
            @Override
            public QName getAttributeName(final int index) {
                if (this.state == 1 && index == super.getAttributeCount()) {
                    return new QName(AddressingVersion.W3C.nsUri, "IsReferenceParameter", "wsa");
                }
                return super.getAttributeName(index);
            }
            
            @Override
            public String getAttributeValue(final String namespaceUri, final String localName) {
                if (this.state == 1 && localName.equals("IsReferenceParameter") && namespaceUri.equals(AddressingVersion.W3C.nsUri)) {
                    return "1";
                }
                return super.getAttributeValue(namespaceUri, localName);
            }
        };
    }
    
    @Override
    public void writeTo(final XMLStreamWriter w) throws XMLStreamException {
        this.infoset.writeToXMLStreamWriter(new XMLStreamWriterFilter(w) {
            private boolean root = true;
            private boolean onRootEl = true;
            
            @Override
            public void writeStartElement(final String localName) throws XMLStreamException {
                super.writeStartElement(localName);
                this.writeAddedAttribute();
            }
            
            private void writeAddedAttribute() throws XMLStreamException {
                if (!this.root) {
                    this.onRootEl = false;
                    return;
                }
                this.root = false;
                this.writeNamespace("wsa", AddressingVersion.W3C.nsUri);
                super.writeAttribute("wsa", AddressingVersion.W3C.nsUri, "IsReferenceParameter", "1");
            }
            
            @Override
            public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
                super.writeStartElement(namespaceURI, localName);
                this.writeAddedAttribute();
            }
            
            @Override
            public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
                final boolean prefixDeclared = this.isPrefixDeclared(prefix, namespaceURI);
                super.writeStartElement(prefix, localName, namespaceURI);
                if (!prefixDeclared && !prefix.equals("")) {
                    super.writeNamespace(prefix, namespaceURI);
                }
                this.writeAddedAttribute();
            }
            
            @Override
            public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException {
                if (!this.isPrefixDeclared(prefix, namespaceURI)) {
                    super.writeNamespace(prefix, namespaceURI);
                }
            }
            
            @Override
            public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException {
                if (this.onRootEl && namespaceURI.equals(AddressingVersion.W3C.nsUri) && localName.equals("IsReferenceParameter")) {
                    return;
                }
                this.writer.writeAttribute(prefix, namespaceURI, localName, value);
            }
            
            @Override
            public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException {
                this.writer.writeAttribute(namespaceURI, localName, value);
            }
            
            private boolean isPrefixDeclared(final String prefix, final String namespaceURI) {
                return namespaceURI.equals(this.getNamespaceContext().getNamespaceURI(prefix));
            }
        }, true);
    }
    
    @Override
    public void writeTo(final SOAPMessage saaj) throws SOAPException {
        try {
            SOAPHeader header = saaj.getSOAPHeader();
            if (header == null) {
                header = saaj.getSOAPPart().getEnvelope().addHeader();
            }
            final Element node = (Element)this.infoset.writeTo(header);
            node.setAttributeNS(AddressingVersion.W3C.nsUri, AddressingVersion.W3C.getPrefix() + ":" + "IsReferenceParameter", "1");
        }
        catch (final XMLStreamBufferException e) {
            throw new SOAPException(e);
        }
    }
    
    @Override
    public void writeTo(final ContentHandler contentHandler, final ErrorHandler errorHandler) throws SAXException {
        class Filter extends XMLFilterImpl
        {
            private int depth;
            
            Filter() {
                this.depth = 0;
                this.setContentHandler(ch);
            }
            
            @Override
            public void startElement(final String uri, final String localName, final String qName, Attributes atts) throws SAXException {
                if (this.depth++ == 0) {
                    super.startPrefixMapping("wsa", AddressingVersion.W3C.nsUri);
                    if (atts.getIndex(AddressingVersion.W3C.nsUri, "IsReferenceParameter") == -1) {
                        final AttributesImpl atts2 = new AttributesImpl(atts);
                        atts2.addAttribute(AddressingVersion.W3C.nsUri, "IsReferenceParameter", "wsa:IsReferenceParameter", "CDATA", "1");
                        atts = atts2;
                    }
                }
                super.startElement(uri, localName, qName, atts);
            }
            
            @Override
            public void endElement(final String uri, final String localName, final String qName) throws SAXException {
                super.endElement(uri, localName, qName);
                final int depth = this.depth - 1;
                this.depth = depth;
                if (depth == 0) {
                    super.endPrefixMapping("wsa");
                }
            }
        }
        this.infoset.writeTo(new Filter(contentHandler), errorHandler);
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
            return (s == null) ? "" : s;
        }
    }
}
