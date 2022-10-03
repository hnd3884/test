package com.sun.xml.internal.ws.api.message;

import java.util.UUID;
import com.sun.xml.internal.ws.message.StringHeader;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.bind.api.Bridge;
import javax.xml.bind.Unmarshaller;
import java.util.Iterator;
import javax.xml.soap.MimeHeaders;
import java.util.List;
import java.util.Map;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;

public abstract class Message
{
    protected AttachmentSet attachmentSet;
    private WSDLBoundOperation operation;
    private WSDLOperationMapping wsdlOperationMapping;
    private MessageMetadata messageMetadata;
    private Boolean isOneWay;
    
    public Message() {
        this.operation = null;
        this.wsdlOperationMapping = null;
        this.messageMetadata = null;
    }
    
    public abstract boolean hasHeaders();
    
    @NotNull
    public abstract MessageHeaders getHeaders();
    
    @NotNull
    public AttachmentSet getAttachments() {
        if (this.attachmentSet == null) {
            this.attachmentSet = new AttachmentSetImpl();
        }
        return this.attachmentSet;
    }
    
    protected boolean hasAttachments() {
        return this.attachmentSet != null;
    }
    
    public void setMessageMedadata(final MessageMetadata metadata) {
        this.messageMetadata = metadata;
    }
    
    @Deprecated
    @Nullable
    public final WSDLBoundOperation getOperation(@NotNull final WSDLBoundPortType boundPortType) {
        if (this.operation == null && this.messageMetadata != null) {
            if (this.wsdlOperationMapping == null) {
                this.wsdlOperationMapping = this.messageMetadata.getWSDLOperationMapping();
            }
            if (this.wsdlOperationMapping != null) {
                this.operation = this.wsdlOperationMapping.getWSDLBoundOperation();
            }
        }
        if (this.operation == null) {
            this.operation = boundPortType.getOperation(this.getPayloadNamespaceURI(), this.getPayloadLocalPart());
        }
        return this.operation;
    }
    
    @Deprecated
    @Nullable
    public final WSDLBoundOperation getOperation(@NotNull final WSDLPort port) {
        return this.getOperation(port.getBinding());
    }
    
    @Deprecated
    @Nullable
    public final JavaMethod getMethod(@NotNull final SEIModel seiModel) {
        if (this.wsdlOperationMapping == null && this.messageMetadata != null) {
            this.wsdlOperationMapping = this.messageMetadata.getWSDLOperationMapping();
        }
        if (this.wsdlOperationMapping != null) {
            return this.wsdlOperationMapping.getJavaMethod();
        }
        String localPart = this.getPayloadLocalPart();
        String nsUri;
        if (localPart == null) {
            localPart = "";
            nsUri = "";
        }
        else {
            nsUri = this.getPayloadNamespaceURI();
        }
        final QName name = new QName(nsUri, localPart);
        return seiModel.getJavaMethod(name);
    }
    
    public boolean isOneWay(@NotNull final WSDLPort port) {
        if (this.isOneWay == null) {
            final WSDLBoundOperation op = this.getOperation(port);
            if (op != null) {
                this.isOneWay = op.getOperation().isOneWay();
            }
            else {
                this.isOneWay = false;
            }
        }
        return this.isOneWay;
    }
    
    public final void assertOneWay(final boolean value) {
        assert this.isOneWay == value;
        this.isOneWay = value;
    }
    
    @Nullable
    public abstract String getPayloadLocalPart();
    
    public abstract String getPayloadNamespaceURI();
    
    public abstract boolean hasPayload();
    
    public boolean isFault() {
        final String localPart = this.getPayloadLocalPart();
        if (localPart == null || !localPart.equals("Fault")) {
            return false;
        }
        final String nsUri = this.getPayloadNamespaceURI();
        return nsUri.equals(SOAPVersion.SOAP_11.nsUri) || nsUri.equals(SOAPVersion.SOAP_12.nsUri);
    }
    
    @Nullable
    public QName getFirstDetailEntryName() {
        assert this.isFault();
        final Message msg = this.copy();
        try {
            final SOAPFaultBuilder fault = SOAPFaultBuilder.create(msg);
            return fault.getFirstDetailEntryName();
        }
        catch (final JAXBException e) {
            throw new WebServiceException(e);
        }
    }
    
    public abstract Source readEnvelopeAsSource();
    
    public abstract Source readPayloadAsSource();
    
    public abstract SOAPMessage readAsSOAPMessage() throws SOAPException;
    
    public SOAPMessage readAsSOAPMessage(final Packet packet, final boolean inbound) throws SOAPException {
        return this.readAsSOAPMessage();
    }
    
    public static Map<String, List<String>> getTransportHeaders(final Packet packet) {
        return getTransportHeaders(packet, packet.getState().isInbound());
    }
    
    public static Map<String, List<String>> getTransportHeaders(final Packet packet, final boolean inbound) {
        Map<String, List<String>> headers = null;
        final String key = inbound ? "com.sun.xml.internal.ws.api.message.packet.inbound.transport.headers" : "com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers";
        if (packet.supports(key)) {
            headers = (Map)packet.get(key);
        }
        return headers;
    }
    
    public static void addSOAPMimeHeaders(final MimeHeaders mh, final Map<String, List<String>> headers) {
        for (final Map.Entry<String, List<String>> e : headers.entrySet()) {
            if (!e.getKey().equalsIgnoreCase("Content-Type")) {
                for (final String value : e.getValue()) {
                    mh.addHeader(e.getKey(), value);
                }
            }
        }
    }
    
    public abstract <T> T readPayloadAsJAXB(final Unmarshaller p0) throws JAXBException;
    
    @Deprecated
    public abstract <T> T readPayloadAsJAXB(final Bridge<T> p0) throws JAXBException;
    
    public abstract <T> T readPayloadAsJAXB(final XMLBridge<T> p0) throws JAXBException;
    
    public abstract XMLStreamReader readPayload() throws XMLStreamException;
    
    public void consume() {
    }
    
    public abstract void writePayloadTo(final XMLStreamWriter p0) throws XMLStreamException;
    
    public abstract void writeTo(final XMLStreamWriter p0) throws XMLStreamException;
    
    public abstract void writeTo(final ContentHandler p0, final ErrorHandler p1) throws SAXException;
    
    public abstract Message copy();
    
    @NotNull
    @Deprecated
    public String getID(@NotNull final WSBinding binding) {
        return this.getID(binding.getAddressingVersion(), binding.getSOAPVersion());
    }
    
    @NotNull
    @Deprecated
    public String getID(final AddressingVersion av, final SOAPVersion sv) {
        String uuid = null;
        if (av != null) {
            uuid = AddressingUtils.getMessageID(this.getHeaders(), av, sv);
        }
        if (uuid == null) {
            uuid = generateMessageID();
            this.getHeaders().add(new StringHeader(av.messageIDTag, uuid));
        }
        return uuid;
    }
    
    public static String generateMessageID() {
        return "uuid:" + UUID.randomUUID().toString();
    }
    
    public SOAPVersion getSOAPVersion() {
        return null;
    }
}
