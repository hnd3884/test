package com.sun.xml.internal.ws.api.addressing;

import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import org.w3c.dom.Node;
import javax.xml.soap.SOAPHeader;
import javax.xml.transform.Transformer;
import javax.xml.soap.SOAPException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import javax.xml.stream.XMLOutputFactory;
import java.io.ByteArrayOutputStream;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamWriter;
import com.sun.istack.internal.Nullable;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;

final class EPRHeader extends AbstractHeaderImpl
{
    private final String nsUri;
    private final String localName;
    private final WSEndpointReference epr;
    
    EPRHeader(final QName tagName, final WSEndpointReference epr) {
        this.nsUri = tagName.getNamespaceURI();
        this.localName = tagName.getLocalPart();
        this.epr = epr;
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
    
    @Nullable
    @Override
    public String getAttribute(@NotNull final String nsUri, @NotNull final String localName) {
        try {
            final XMLStreamReader sr = this.epr.read("EndpointReference");
            while (sr.getEventType() != 1) {
                sr.next();
            }
            return sr.getAttributeValue(nsUri, localName);
        }
        catch (final XMLStreamException e) {
            throw new AssertionError((Object)e);
        }
    }
    
    @Override
    public XMLStreamReader readHeader() throws XMLStreamException {
        return this.epr.read(this.localName);
    }
    
    @Override
    public void writeTo(final XMLStreamWriter w) throws XMLStreamException {
        this.epr.writeTo(this.localName, w);
    }
    
    @Override
    public void writeTo(final SOAPMessage saaj) throws SOAPException {
        try {
            final Transformer t = XmlUtil.newTransformer();
            SOAPHeader header = saaj.getSOAPHeader();
            if (header == null) {
                header = saaj.getSOAPPart().getEnvelope().addHeader();
            }
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final XMLStreamWriter w = XMLOutputFactory.newFactory().createXMLStreamWriter(baos);
            this.epr.writeTo(this.localName, w);
            w.flush();
            final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            final DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            fac.setNamespaceAware(true);
            final Node eprNode = fac.newDocumentBuilder().parse(bais).getDocumentElement();
            final Node eprNodeToAdd = header.getOwnerDocument().importNode(eprNode, true);
            header.appendChild(eprNodeToAdd);
        }
        catch (final Exception e) {
            throw new SOAPException(e);
        }
    }
    
    @Override
    public void writeTo(final ContentHandler contentHandler, final ErrorHandler errorHandler) throws SAXException {
        this.epr.writeTo(this.localName, contentHandler, errorHandler, true);
    }
}
