package com.sun.xml.internal.ws.api.message;

import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.bind.api.Bridge;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.namespace.QName;
import com.sun.istack.internal.Nullable;
import java.util.Set;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.SOAPVersion;

public interface Header
{
    boolean isIgnorable(@NotNull final SOAPVersion p0, @NotNull final Set<String> p1);
    
    @NotNull
    String getRole(@NotNull final SOAPVersion p0);
    
    boolean isRelay();
    
    @NotNull
    String getNamespaceURI();
    
    @NotNull
    String getLocalPart();
    
    @Nullable
    String getAttribute(@NotNull final String p0, @NotNull final String p1);
    
    @Nullable
    String getAttribute(@NotNull final QName p0);
    
    XMLStreamReader readHeader() throws XMLStreamException;
    
     <T> T readAsJAXB(final Unmarshaller p0) throws JAXBException;
    
    @Deprecated
     <T> T readAsJAXB(final Bridge<T> p0) throws JAXBException;
    
     <T> T readAsJAXB(final XMLBridge<T> p0) throws JAXBException;
    
    @NotNull
    WSEndpointReference readAsEPR(final AddressingVersion p0) throws XMLStreamException;
    
    void writeTo(final XMLStreamWriter p0) throws XMLStreamException;
    
    void writeTo(final SOAPMessage p0) throws SOAPException;
    
    void writeTo(final ContentHandler p0, final ErrorHandler p1) throws SAXException;
    
    @NotNull
    String getStringContent();
}
