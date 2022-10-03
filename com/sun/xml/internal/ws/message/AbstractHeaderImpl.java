package com.sun.xml.internal.ws.message;

import javax.xml.namespace.QName;
import java.util.Set;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import javax.xml.stream.XMLStreamException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import com.sun.xml.internal.bind.api.BridgeContext;
import com.sun.xml.internal.bind.api.Bridge;
import org.xml.sax.helpers.AttributesImpl;
import com.sun.xml.internal.ws.api.message.Header;

public abstract class AbstractHeaderImpl implements Header
{
    protected static final AttributesImpl EMPTY_ATTS;
    
    protected AbstractHeaderImpl() {
    }
    
    @Deprecated
    public final <T> T readAsJAXB(final Bridge<T> bridge, final BridgeContext context) throws JAXBException {
        return this.readAsJAXB(bridge);
    }
    
    @Override
    public <T> T readAsJAXB(final Unmarshaller unmarshaller) throws JAXBException {
        try {
            return (T)unmarshaller.unmarshal(this.readHeader());
        }
        catch (final Exception e) {
            throw new JAXBException(e);
        }
    }
    
    @Override
    @Deprecated
    public <T> T readAsJAXB(final Bridge<T> bridge) throws JAXBException {
        try {
            return bridge.unmarshal(this.readHeader());
        }
        catch (final XMLStreamException e) {
            throw new JAXBException(e);
        }
    }
    
    @Override
    public <T> T readAsJAXB(final XMLBridge<T> bridge) throws JAXBException {
        try {
            return bridge.unmarshal(this.readHeader(), null);
        }
        catch (final XMLStreamException e) {
            throw new JAXBException(e);
        }
    }
    
    @Override
    public WSEndpointReference readAsEPR(final AddressingVersion expected) throws XMLStreamException {
        final XMLStreamReader xsr = this.readHeader();
        final WSEndpointReference epr = new WSEndpointReference(xsr, expected);
        XMLStreamReaderFactory.recycle(xsr);
        return epr;
    }
    
    @Override
    public boolean isIgnorable(@NotNull final SOAPVersion soapVersion, @NotNull final Set<String> roles) {
        final String v = this.getAttribute(soapVersion.nsUri, "mustUnderstand");
        return v == null || !this.parseBool(v) || roles == null || !roles.contains(this.getRole(soapVersion));
    }
    
    @NotNull
    @Override
    public String getRole(@NotNull final SOAPVersion soapVersion) {
        String v = this.getAttribute(soapVersion.nsUri, soapVersion.roleAttributeName);
        if (v == null) {
            v = soapVersion.implicitRole;
        }
        return v;
    }
    
    @Override
    public boolean isRelay() {
        final String v = this.getAttribute(SOAPVersion.SOAP_12.nsUri, "relay");
        return v != null && this.parseBool(v);
    }
    
    @Override
    public String getAttribute(final QName name) {
        return this.getAttribute(name.getNamespaceURI(), name.getLocalPart());
    }
    
    protected final boolean parseBool(final String value) {
        if (value.length() == 0) {
            return false;
        }
        final char ch = value.charAt(0);
        return ch == 't' || ch == '1';
    }
    
    @Override
    public String getStringContent() {
        try {
            final XMLStreamReader xsr = this.readHeader();
            xsr.nextTag();
            return xsr.getElementText();
        }
        catch (final XMLStreamException e) {
            return null;
        }
    }
    
    static {
        EMPTY_ATTS = new AttributesImpl();
    }
}
