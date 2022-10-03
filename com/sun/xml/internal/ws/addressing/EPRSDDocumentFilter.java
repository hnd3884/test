package com.sun.xml.internal.ws.addressing;

import java.io.IOException;
import javax.xml.namespace.NamespaceContext;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import java.util.Collection;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import java.util.Iterator;
import java.util.Collections;
import com.sun.xml.internal.ws.api.server.Module;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.BoundEndpoint;
import java.util.List;
import com.sun.xml.internal.ws.server.WSEndpointImpl;
import com.sun.xml.internal.ws.api.server.SDDocumentFilter;

public class EPRSDDocumentFilter implements SDDocumentFilter
{
    private final WSEndpointImpl<?> endpoint;
    List<BoundEndpoint> beList;
    
    public EPRSDDocumentFilter(@NotNull final WSEndpointImpl<?> endpoint) {
        this.endpoint = endpoint;
    }
    
    @Nullable
    private WSEndpointImpl<?> getEndpoint(final String serviceName, final String portName) {
        if (serviceName == null || portName == null) {
            return null;
        }
        if (this.endpoint.getServiceName().getLocalPart().equals(serviceName) && this.endpoint.getPortName().getLocalPart().equals(portName)) {
            return this.endpoint;
        }
        if (this.beList == null) {
            final Module module = this.endpoint.getContainer().getSPI(Module.class);
            if (module != null) {
                this.beList = module.getBoundEndpoints();
            }
            else {
                this.beList = Collections.emptyList();
            }
        }
        for (final BoundEndpoint be : this.beList) {
            final WSEndpoint wse = be.getEndpoint();
            if (wse.getServiceName().getLocalPart().equals(serviceName) && wse.getPortName().getLocalPart().equals(portName)) {
                return (WSEndpointImpl)wse;
            }
        }
        return null;
    }
    
    @Override
    public XMLStreamWriter filter(final SDDocument doc, final XMLStreamWriter w) throws XMLStreamException, IOException {
        if (!doc.isWSDL()) {
            return w;
        }
        return new XMLStreamWriterFilter(w) {
            private boolean eprExtnFilterON = false;
            private boolean portHasEPR = false;
            private int eprDepth = -1;
            private String serviceName = null;
            private boolean onService = false;
            private int serviceDepth = -1;
            private String portName = null;
            private boolean onPort = false;
            private int portDepth = -1;
            private String portAddress;
            private boolean onPortAddress = false;
            
            private void handleStartElement(final String localName, final String namespaceURI) throws XMLStreamException {
                this.resetOnElementFlags();
                if (this.serviceDepth >= 0) {
                    ++this.serviceDepth;
                }
                if (this.portDepth >= 0) {
                    ++this.portDepth;
                }
                if (this.eprDepth >= 0) {
                    ++this.eprDepth;
                }
                if (namespaceURI.equals(WSDLConstants.QNAME_SERVICE.getNamespaceURI()) && localName.equals(WSDLConstants.QNAME_SERVICE.getLocalPart())) {
                    this.onService = true;
                    this.serviceDepth = 0;
                }
                else if (namespaceURI.equals(WSDLConstants.QNAME_PORT.getNamespaceURI()) && localName.equals(WSDLConstants.QNAME_PORT.getLocalPart())) {
                    if (this.serviceDepth >= 1) {
                        this.onPort = true;
                        this.portDepth = 0;
                    }
                }
                else if (namespaceURI.equals("http://www.w3.org/2005/08/addressing") && localName.equals("EndpointReference")) {
                    if (this.serviceDepth >= 1 && this.portDepth >= 1) {
                        this.portHasEPR = true;
                        this.eprDepth = 0;
                    }
                }
                else if ((namespaceURI.equals(WSDLConstants.NS_SOAP_BINDING_ADDRESS.getNamespaceURI()) || namespaceURI.equals(WSDLConstants.NS_SOAP12_BINDING_ADDRESS.getNamespaceURI())) && localName.equals("address") && this.portDepth == 1) {
                    this.onPortAddress = true;
                }
                final WSEndpoint endpoint = EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName);
                if (endpoint != null && this.eprDepth == 1 && !namespaceURI.equals("http://www.w3.org/2005/08/addressing")) {
                    this.eprExtnFilterON = true;
                }
            }
            
            private void resetOnElementFlags() {
                if (this.onService) {
                    this.onService = false;
                }
                if (this.onPort) {
                    this.onPort = false;
                }
                if (this.onPortAddress) {
                    this.onPortAddress = false;
                }
            }
            
            private void writeEPRExtensions(final Collection<WSEndpointReference.EPRExtension> eprExtns) throws XMLStreamException {
                if (eprExtns != null) {
                    for (final WSEndpointReference.EPRExtension e : eprExtns) {
                        final XMLStreamReaderToXMLStreamWriter c = new XMLStreamReaderToXMLStreamWriter();
                        final XMLStreamReader r = e.readAsXMLStreamReader();
                        c.bridge(r, this.writer);
                        XMLStreamReaderFactory.recycle(r);
                    }
                }
            }
            
            @Override
            public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
                this.handleStartElement(localName, namespaceURI);
                if (!this.eprExtnFilterON) {
                    super.writeStartElement(prefix, localName, namespaceURI);
                }
            }
            
            @Override
            public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
                this.handleStartElement(localName, namespaceURI);
                if (!this.eprExtnFilterON) {
                    super.writeStartElement(namespaceURI, localName);
                }
            }
            
            @Override
            public void writeStartElement(final String localName) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeStartElement(localName);
                }
            }
            
            private void handleEndElement() throws XMLStreamException {
                this.resetOnElementFlags();
                if (this.portDepth == 0 && !this.portHasEPR && EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName) != null) {
                    this.writer.writeStartElement(AddressingVersion.W3C.getPrefix(), "EndpointReference", AddressingVersion.W3C.nsUri);
                    this.writer.writeNamespace(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.nsUri);
                    this.writer.writeStartElement(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.eprType.address, AddressingVersion.W3C.nsUri);
                    this.writer.writeCharacters(this.portAddress);
                    this.writer.writeEndElement();
                    this.writeEPRExtensions(EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName).getEndpointReferenceExtensions());
                    this.writer.writeEndElement();
                }
                if (this.eprDepth == 0) {
                    if (this.portHasEPR && EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName) != null) {
                        this.writeEPRExtensions(EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName).getEndpointReferenceExtensions());
                    }
                    this.eprExtnFilterON = false;
                }
                if (this.serviceDepth >= 0) {
                    --this.serviceDepth;
                }
                if (this.portDepth >= 0) {
                    --this.portDepth;
                }
                if (this.eprDepth >= 0) {
                    --this.eprDepth;
                }
                if (this.serviceDepth == -1) {
                    this.serviceName = null;
                }
                if (this.portDepth == -1) {
                    this.portHasEPR = false;
                    this.portAddress = null;
                    this.portName = null;
                }
            }
            
            @Override
            public void writeEndElement() throws XMLStreamException {
                this.handleEndElement();
                if (!this.eprExtnFilterON) {
                    super.writeEndElement();
                }
            }
            
            private void handleAttribute(final String localName, final String value) {
                if (localName.equals("name")) {
                    if (this.onService) {
                        this.serviceName = value;
                        this.onService = false;
                    }
                    else if (this.onPort) {
                        this.portName = value;
                        this.onPort = false;
                    }
                }
                if (localName.equals("location") && this.onPortAddress) {
                    this.portAddress = value;
                }
            }
            
            @Override
            public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException {
                this.handleAttribute(localName, value);
                if (!this.eprExtnFilterON) {
                    super.writeAttribute(prefix, namespaceURI, localName, value);
                }
            }
            
            @Override
            public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException {
                this.handleAttribute(localName, value);
                if (!this.eprExtnFilterON) {
                    super.writeAttribute(namespaceURI, localName, value);
                }
            }
            
            @Override
            public void writeAttribute(final String localName, final String value) throws XMLStreamException {
                this.handleAttribute(localName, value);
                if (!this.eprExtnFilterON) {
                    super.writeAttribute(localName, value);
                }
            }
            
            @Override
            public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeEmptyElement(namespaceURI, localName);
                }
            }
            
            @Override
            public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeNamespace(prefix, namespaceURI);
                }
            }
            
            @Override
            public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.setNamespaceContext(context);
                }
            }
            
            @Override
            public void setDefaultNamespace(final String uri) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.setDefaultNamespace(uri);
                }
            }
            
            @Override
            public void setPrefix(final String prefix, final String uri) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.setPrefix(prefix, uri);
                }
            }
            
            @Override
            public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeProcessingInstruction(target, data);
                }
            }
            
            @Override
            public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeEmptyElement(prefix, localName, namespaceURI);
                }
            }
            
            @Override
            public void writeCData(final String data) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeCData(data);
                }
            }
            
            @Override
            public void writeCharacters(final String text) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeCharacters(text);
                }
            }
            
            @Override
            public void writeComment(final String data) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeComment(data);
                }
            }
            
            @Override
            public void writeDTD(final String dtd) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeDTD(dtd);
                }
            }
            
            @Override
            public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeDefaultNamespace(namespaceURI);
                }
            }
            
            @Override
            public void writeEmptyElement(final String localName) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeEmptyElement(localName);
                }
            }
            
            @Override
            public void writeEntityRef(final String name) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeEntityRef(name);
                }
            }
            
            @Override
            public void writeProcessingInstruction(final String target) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeProcessingInstruction(target);
                }
            }
            
            @Override
            public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
                if (!this.eprExtnFilterON) {
                    super.writeCharacters(text, start, len);
                }
            }
        };
    }
}
