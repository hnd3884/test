package com.sun.xml.internal.ws.message;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import javax.xml.stream.XMLStreamReader;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.namespace.QName;

public class StringHeader extends AbstractHeaderImpl
{
    protected final QName name;
    protected final String value;
    protected boolean mustUnderstand;
    protected SOAPVersion soapVersion;
    protected static final String MUST_UNDERSTAND = "mustUnderstand";
    protected static final String S12_MUST_UNDERSTAND_TRUE = "true";
    protected static final String S11_MUST_UNDERSTAND_TRUE = "1";
    
    public StringHeader(@NotNull final QName name, @NotNull final String value) {
        this.mustUnderstand = false;
        assert name != null;
        assert value != null;
        this.name = name;
        this.value = value;
    }
    
    public StringHeader(@NotNull final QName name, @NotNull final String value, @NotNull final SOAPVersion soapVersion, final boolean mustUnderstand) {
        this.mustUnderstand = false;
        this.name = name;
        this.value = value;
        this.soapVersion = soapVersion;
        this.mustUnderstand = mustUnderstand;
    }
    
    @NotNull
    @Override
    public String getNamespaceURI() {
        return this.name.getNamespaceURI();
    }
    
    @NotNull
    @Override
    public String getLocalPart() {
        return this.name.getLocalPart();
    }
    
    @Nullable
    @Override
    public String getAttribute(@NotNull final String nsUri, @NotNull final String localName) {
        if (this.mustUnderstand && this.soapVersion.nsUri.equals(nsUri) && "mustUnderstand".equals(localName)) {
            return getMustUnderstandLiteral(this.soapVersion);
        }
        return null;
    }
    
    @Override
    public XMLStreamReader readHeader() throws XMLStreamException {
        final MutableXMLStreamBuffer buf = new MutableXMLStreamBuffer();
        final XMLStreamWriter w = buf.createFromXMLStreamWriter();
        this.writeTo(w);
        return buf.readAsXMLStreamReader();
    }
    
    @Override
    public void writeTo(final XMLStreamWriter w) throws XMLStreamException {
        w.writeStartElement("", this.name.getLocalPart(), this.name.getNamespaceURI());
        w.writeDefaultNamespace(this.name.getNamespaceURI());
        if (this.mustUnderstand) {
            w.writeNamespace("S", this.soapVersion.nsUri);
            w.writeAttribute("S", this.soapVersion.nsUri, "mustUnderstand", getMustUnderstandLiteral(this.soapVersion));
        }
        w.writeCharacters(this.value);
        w.writeEndElement();
    }
    
    @Override
    public void writeTo(final SOAPMessage saaj) throws SOAPException {
        SOAPHeader header = saaj.getSOAPHeader();
        if (header == null) {
            header = saaj.getSOAPPart().getEnvelope().addHeader();
        }
        final SOAPHeaderElement she = header.addHeaderElement(this.name);
        if (this.mustUnderstand) {
            she.setMustUnderstand(true);
        }
        she.addTextNode(this.value);
    }
    
    @Override
    public void writeTo(final ContentHandler h, final ErrorHandler errorHandler) throws SAXException {
        final String nsUri = this.name.getNamespaceURI();
        final String ln = this.name.getLocalPart();
        h.startPrefixMapping("", nsUri);
        if (this.mustUnderstand) {
            final AttributesImpl attributes = new AttributesImpl();
            attributes.addAttribute(this.soapVersion.nsUri, "mustUnderstand", "S:mustUnderstand", "CDATA", getMustUnderstandLiteral(this.soapVersion));
            h.startElement(nsUri, ln, ln, attributes);
        }
        else {
            h.startElement(nsUri, ln, ln, StringHeader.EMPTY_ATTS);
        }
        h.characters(this.value.toCharArray(), 0, this.value.length());
        h.endElement(nsUri, ln, ln);
    }
    
    private static String getMustUnderstandLiteral(final SOAPVersion sv) {
        if (sv == SOAPVersion.SOAP_12) {
            return "true";
        }
        return "1";
    }
}
