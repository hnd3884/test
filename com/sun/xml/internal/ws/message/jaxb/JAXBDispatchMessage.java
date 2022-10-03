package com.sun.xml.internal.ws.message.jaxb;

import java.io.OutputStream;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil;
import com.sun.xml.internal.ws.streaming.MtomStreamWriter;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import com.sun.xml.internal.ws.message.PayloadElementSniffer;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.namespace.QName;
import javax.xml.bind.JAXBContext;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.message.AbstractMessageImpl;

public class JAXBDispatchMessage extends AbstractMessageImpl
{
    private final Object jaxbObject;
    private final XMLBridge bridge;
    private final JAXBContext rawContext;
    private QName payloadQName;
    
    private JAXBDispatchMessage(final JAXBDispatchMessage that) {
        super(that);
        this.jaxbObject = that.jaxbObject;
        this.rawContext = that.rawContext;
        this.bridge = that.bridge;
    }
    
    public JAXBDispatchMessage(final JAXBContext rawContext, final Object jaxbObject, final SOAPVersion soapVersion) {
        super(soapVersion);
        this.bridge = null;
        this.rawContext = rawContext;
        this.jaxbObject = jaxbObject;
    }
    
    public JAXBDispatchMessage(final BindingContext context, final Object jaxbObject, final SOAPVersion soapVersion) {
        super(soapVersion);
        this.bridge = context.createFragmentBridge();
        this.rawContext = null;
        this.jaxbObject = jaxbObject;
    }
    
    @Override
    protected void writePayloadTo(final ContentHandler contentHandler, final ErrorHandler errorHandler, final boolean fragment) throws SAXException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean hasHeaders() {
        return false;
    }
    
    @Override
    public MessageHeaders getHeaders() {
        return null;
    }
    
    @Override
    public String getPayloadLocalPart() {
        if (this.payloadQName == null) {
            this.readPayloadElement();
        }
        return this.payloadQName.getLocalPart();
    }
    
    @Override
    public String getPayloadNamespaceURI() {
        if (this.payloadQName == null) {
            this.readPayloadElement();
        }
        return this.payloadQName.getNamespaceURI();
    }
    
    private void readPayloadElement() {
        final PayloadElementSniffer sniffer = new PayloadElementSniffer();
        try {
            if (this.rawContext != null) {
                final Marshaller m = this.rawContext.createMarshaller();
                m.setProperty("jaxb.fragment", Boolean.FALSE);
                m.marshal(this.jaxbObject, sniffer);
            }
            else {
                this.bridge.marshal(this.jaxbObject, sniffer, null);
            }
        }
        catch (final JAXBException e) {
            this.payloadQName = sniffer.getPayloadQName();
        }
    }
    
    @Override
    public boolean hasPayload() {
        return true;
    }
    
    @Override
    public Source readPayloadAsSource() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public XMLStreamReader readPayload() throws XMLStreamException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void writePayloadTo(final XMLStreamWriter sw) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Message copy() {
        return new JAXBDispatchMessage(this);
    }
    
    @Override
    public void writeTo(final XMLStreamWriter sw) throws XMLStreamException {
        try {
            final AttachmentMarshaller am = (sw instanceof MtomStreamWriter) ? ((MtomStreamWriter)sw).getAttachmentMarshaller() : new AttachmentMarshallerImpl(this.attachmentSet);
            final String encoding = XMLStreamWriterUtil.getEncoding(sw);
            final OutputStream os = this.bridge.supportOutputStream() ? XMLStreamWriterUtil.getOutputStream(sw) : null;
            if (this.rawContext != null) {
                final Marshaller m = this.rawContext.createMarshaller();
                m.setProperty("jaxb.fragment", Boolean.FALSE);
                m.setAttachmentMarshaller(am);
                if (os != null) {
                    m.marshal(this.jaxbObject, os);
                }
                else {
                    m.marshal(this.jaxbObject, sw);
                }
            }
            else if (os != null && encoding != null && encoding.equalsIgnoreCase("utf-8")) {
                this.bridge.marshal(this.jaxbObject, os, sw.getNamespaceContext(), am);
            }
            else {
                this.bridge.marshal(this.jaxbObject, sw, am);
            }
        }
        catch (final JAXBException e) {
            throw new WebServiceException(e);
        }
    }
}
