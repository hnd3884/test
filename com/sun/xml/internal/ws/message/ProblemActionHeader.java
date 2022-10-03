package com.sun.xml.internal.ws.message;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import javax.xml.stream.XMLStreamReader;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.istack.internal.NotNull;

public class ProblemActionHeader extends AbstractHeaderImpl
{
    @NotNull
    protected String action;
    protected String soapAction;
    @NotNull
    protected AddressingVersion av;
    private static final String actionLocalName = "Action";
    private static final String soapActionLocalName = "SoapAction";
    
    public ProblemActionHeader(@NotNull final String action, @NotNull final AddressingVersion av) {
        this(action, null, av);
    }
    
    public ProblemActionHeader(@NotNull final String action, final String soapAction, @NotNull final AddressingVersion av) {
        assert action != null;
        assert av != null;
        this.action = action;
        this.soapAction = soapAction;
        this.av = av;
    }
    
    @NotNull
    @Override
    public String getNamespaceURI() {
        return this.av.nsUri;
    }
    
    @NotNull
    @Override
    public String getLocalPart() {
        return "ProblemAction";
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
        w.writeStartElement("", this.getLocalPart(), this.getNamespaceURI());
        w.writeDefaultNamespace(this.getNamespaceURI());
        w.writeStartElement("Action");
        w.writeCharacters(this.action);
        w.writeEndElement();
        if (this.soapAction != null) {
            w.writeStartElement("SoapAction");
            w.writeCharacters(this.soapAction);
            w.writeEndElement();
        }
        w.writeEndElement();
    }
    
    @Override
    public void writeTo(final SOAPMessage saaj) throws SOAPException {
        SOAPHeader header = saaj.getSOAPHeader();
        if (header == null) {
            header = saaj.getSOAPPart().getEnvelope().addHeader();
        }
        final SOAPHeaderElement she = header.addHeaderElement(new QName(this.getNamespaceURI(), this.getLocalPart()));
        she.addChildElement("Action");
        she.addTextNode(this.action);
        if (this.soapAction != null) {
            she.addChildElement("SoapAction");
            she.addTextNode(this.soapAction);
        }
    }
    
    @Override
    public void writeTo(final ContentHandler h, final ErrorHandler errorHandler) throws SAXException {
        final String nsUri = this.getNamespaceURI();
        final String ln = this.getLocalPart();
        h.startPrefixMapping("", nsUri);
        h.startElement(nsUri, ln, ln, ProblemActionHeader.EMPTY_ATTS);
        h.startElement(nsUri, "Action", "Action", ProblemActionHeader.EMPTY_ATTS);
        h.characters(this.action.toCharArray(), 0, this.action.length());
        h.endElement(nsUri, "Action", "Action");
        if (this.soapAction != null) {
            h.startElement(nsUri, "SoapAction", "SoapAction", ProblemActionHeader.EMPTY_ATTS);
            h.characters(this.soapAction.toCharArray(), 0, this.soapAction.length());
            h.endElement(nsUri, "SoapAction", "SoapAction");
        }
        h.endElement(nsUri, ln, ln);
    }
}
