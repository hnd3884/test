package com.sun.xml.internal.ws.message;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
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
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;

public class FaultDetailHeader extends AbstractHeaderImpl
{
    private AddressingVersion av;
    private String wrapper;
    private String problemValue;
    
    public FaultDetailHeader(final AddressingVersion av, final String wrapper, final QName problemHeader) {
        this.problemValue = null;
        this.av = av;
        this.wrapper = wrapper;
        this.problemValue = problemHeader.toString();
    }
    
    public FaultDetailHeader(final AddressingVersion av, final String wrapper, final String problemValue) {
        this.problemValue = null;
        this.av = av;
        this.wrapper = wrapper;
        this.problemValue = problemValue;
    }
    
    @NotNull
    @Override
    public String getNamespaceURI() {
        return this.av.nsUri;
    }
    
    @NotNull
    @Override
    public String getLocalPart() {
        return this.av.faultDetailTag.getLocalPart();
    }
    
    @Nullable
    @Override
    public String getAttribute(@NotNull final String nsUri, @NotNull final String localName) {
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
        w.writeStartElement("", this.av.faultDetailTag.getLocalPart(), this.av.faultDetailTag.getNamespaceURI());
        w.writeDefaultNamespace(this.av.nsUri);
        w.writeStartElement("", this.wrapper, this.av.nsUri);
        w.writeCharacters(this.problemValue);
        w.writeEndElement();
        w.writeEndElement();
    }
    
    @Override
    public void writeTo(final SOAPMessage saaj) throws SOAPException {
        SOAPHeader header = saaj.getSOAPHeader();
        if (header == null) {
            header = saaj.getSOAPPart().getEnvelope().addHeader();
        }
        SOAPHeaderElement she = header.addHeaderElement(this.av.faultDetailTag);
        she = header.addHeaderElement(new QName(this.av.nsUri, this.wrapper));
        she.addTextNode(this.problemValue);
    }
    
    @Override
    public void writeTo(final ContentHandler h, final ErrorHandler errorHandler) throws SAXException {
        final String nsUri = this.av.nsUri;
        final String ln = this.av.faultDetailTag.getLocalPart();
        h.startPrefixMapping("", nsUri);
        h.startElement(nsUri, ln, ln, FaultDetailHeader.EMPTY_ATTS);
        h.startElement(nsUri, this.wrapper, this.wrapper, FaultDetailHeader.EMPTY_ATTS);
        h.characters(this.problemValue.toCharArray(), 0, this.problemValue.length());
        h.endElement(nsUri, this.wrapper, this.wrapper);
        h.endElement(nsUri, ln, ln);
    }
}
