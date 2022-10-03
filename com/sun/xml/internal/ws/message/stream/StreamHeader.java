package com.sun.xml.internal.ws.message.stream;

import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import javax.xml.soap.SOAPHeader;
import org.w3c.dom.Node;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferSource;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamWriter;
import java.util.Set;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import com.sun.istack.internal.FinalArrayList;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;

public abstract class StreamHeader extends AbstractHeaderImpl
{
    protected final XMLStreamBuffer _mark;
    protected boolean _isMustUnderstand;
    @NotNull
    protected String _role;
    protected boolean _isRelay;
    protected String _localName;
    protected String _namespaceURI;
    private final FinalArrayList<Attribute> attributes;
    
    protected StreamHeader(final XMLStreamReader reader, final XMLStreamBuffer mark) {
        assert reader != null && mark != null;
        this._mark = mark;
        this._localName = reader.getLocalName();
        this._namespaceURI = reader.getNamespaceURI();
        this.attributes = this.processHeaderAttributes(reader);
    }
    
    protected StreamHeader(final XMLStreamReader reader) throws XMLStreamException {
        this._localName = reader.getLocalName();
        this._namespaceURI = reader.getNamespaceURI();
        this.attributes = this.processHeaderAttributes(reader);
        this._mark = XMLStreamBuffer.createNewBufferFromXMLStreamReader(reader);
    }
    
    @Override
    public final boolean isIgnorable(@NotNull final SOAPVersion soapVersion, @NotNull final Set<String> roles) {
        return !this._isMustUnderstand || roles == null || !roles.contains(this._role);
    }
    
    @NotNull
    @Override
    public String getRole(@NotNull final SOAPVersion soapVersion) {
        assert this._role != null;
        return this._role;
    }
    
    @Override
    public boolean isRelay() {
        return this._isRelay;
    }
    
    @NotNull
    @Override
    public String getNamespaceURI() {
        return this._namespaceURI;
    }
    
    @NotNull
    @Override
    public String getLocalPart() {
        return this._localName;
    }
    
    @Override
    public String getAttribute(final String nsUri, final String localName) {
        if (this.attributes != null) {
            for (int i = this.attributes.size() - 1; i >= 0; --i) {
                final Attribute a = this.attributes.get(i);
                if (a.localName.equals(localName) && a.nsUri.equals(nsUri)) {
                    return a.value;
                }
            }
        }
        return null;
    }
    
    @Override
    public XMLStreamReader readHeader() throws XMLStreamException {
        return this._mark.readAsXMLStreamReader();
    }
    
    @Override
    public void writeTo(final XMLStreamWriter w) throws XMLStreamException {
        if (this._mark.getInscopeNamespaces().size() > 0) {
            this._mark.writeToXMLStreamWriter(w, true);
        }
        else {
            this._mark.writeToXMLStreamWriter(w);
        }
    }
    
    @Override
    public void writeTo(final SOAPMessage saaj) throws SOAPException {
        try {
            final TransformerFactory tf = XmlUtil.newTransformerFactory();
            final Transformer t = tf.newTransformer();
            final XMLStreamBufferSource source = new XMLStreamBufferSource(this._mark);
            final DOMResult result = new DOMResult();
            t.transform(source, result);
            Node d = result.getNode();
            if (d.getNodeType() == 9) {
                d = d.getFirstChild();
            }
            SOAPHeader header = saaj.getSOAPHeader();
            if (header == null) {
                header = saaj.getSOAPPart().getEnvelope().addHeader();
            }
            final Node node = header.getOwnerDocument().importNode(d, true);
            header.appendChild(node);
        }
        catch (final Exception e) {
            throw new SOAPException(e);
        }
    }
    
    @Override
    public void writeTo(final ContentHandler contentHandler, final ErrorHandler errorHandler) throws SAXException {
        this._mark.writeTo(contentHandler);
    }
    
    @NotNull
    @Override
    public WSEndpointReference readAsEPR(final AddressingVersion expected) throws XMLStreamException {
        return new WSEndpointReference(this._mark, expected);
    }
    
    protected abstract FinalArrayList<Attribute> processHeaderAttributes(final XMLStreamReader p0);
    
    private static String fixNull(final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
    
    protected static final class Attribute
    {
        final String nsUri;
        final String localName;
        final String value;
        
        public Attribute(final String nsUri, final String localName, final String value) {
            this.nsUri = fixNull(nsUri);
            this.localName = localName;
            this.value = value;
        }
    }
}
