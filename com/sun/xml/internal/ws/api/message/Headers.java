package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.message.StringHeader;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.ws.message.stream.StreamHeader12;
import com.sun.xml.internal.ws.message.stream.StreamHeader11;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.message.DOMHeader;
import org.w3c.dom.Element;
import com.sun.xml.internal.ws.message.saaj.SAAJHeader;
import javax.xml.soap.SOAPHeaderElement;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.db.glassfish.BridgeWrapper;
import com.sun.xml.internal.bind.api.Bridge;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import javax.xml.bind.JAXBContext;
import com.sun.xml.internal.ws.message.jaxb.JAXBHeader;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import javax.xml.bind.Marshaller;
import com.sun.xml.internal.ws.api.SOAPVersion;

public abstract class Headers
{
    private Headers() {
    }
    
    @Deprecated
    public static Header create(final SOAPVersion soapVersion, final Marshaller m, final Object o) {
        return new JAXBHeader(BindingContextFactory.getBindingContext(m), o);
    }
    
    public static Header create(final JAXBContext context, final Object o) {
        return new JAXBHeader(BindingContextFactory.create(context), o);
    }
    
    public static Header create(final BindingContext context, final Object o) {
        return new JAXBHeader(context, o);
    }
    
    public static Header create(final SOAPVersion soapVersion, final Marshaller m, final QName tagName, final Object o) {
        return create(soapVersion, m, new JAXBElement(tagName, (Class<Object>)o.getClass(), o));
    }
    
    @Deprecated
    public static Header create(final Bridge bridge, final Object jaxbObject) {
        return new JAXBHeader(new BridgeWrapper(null, bridge), jaxbObject);
    }
    
    public static Header create(final XMLBridge bridge, final Object jaxbObject) {
        return new JAXBHeader(bridge, jaxbObject);
    }
    
    public static Header create(final SOAPHeaderElement header) {
        return new SAAJHeader(header);
    }
    
    public static Header create(final Element node) {
        return new DOMHeader<Object>(node);
    }
    
    @Deprecated
    public static Header create(final SOAPVersion soapVersion, final Element node) {
        return create(node);
    }
    
    public static Header create(final SOAPVersion soapVersion, final XMLStreamReader reader) throws XMLStreamException {
        switch (soapVersion) {
            case SOAP_11: {
                return new StreamHeader11(reader);
            }
            case SOAP_12: {
                return new StreamHeader12(reader);
            }
            default: {
                throw new AssertionError();
            }
        }
    }
    
    public static Header create(final QName name, final String value) {
        return new StringHeader(name, value);
    }
    
    public static Header createMustUnderstand(@NotNull final SOAPVersion soapVersion, @NotNull final QName name, @NotNull final String value) {
        return new StringHeader(name, value, soapVersion, true);
    }
}
