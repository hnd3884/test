package com.sun.xml.internal.ws.message;

import javax.xml.soap.SOAPException;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPMessage;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.namespace.NamespaceContext;
import java.io.InputStream;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Source;
import com.sun.xml.internal.ws.encoding.DataSourceStreamingDataHandler;
import javax.activation.DataHandler;
import java.io.IOException;
import javax.xml.ws.WebServiceException;
import java.io.OutputStream;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import javax.activation.DataSource;
import com.sun.xml.internal.ws.api.message.Attachment;

public final class JAXBAttachment implements Attachment, DataSource
{
    private final String contentId;
    private final String mimeType;
    private final Object jaxbObject;
    private final XMLBridge bridge;
    
    public JAXBAttachment(@NotNull final String contentId, final Object jaxbObject, final XMLBridge bridge, final String mimeType) {
        this.contentId = contentId;
        this.jaxbObject = jaxbObject;
        this.bridge = bridge;
        this.mimeType = mimeType;
    }
    
    @Override
    public String getContentId() {
        return this.contentId;
    }
    
    @Override
    public String getContentType() {
        return this.mimeType;
    }
    
    @Override
    public byte[] asByteArray() {
        final ByteArrayBuffer bab = new ByteArrayBuffer();
        try {
            this.writeTo(bab);
        }
        catch (final IOException e) {
            throw new WebServiceException(e);
        }
        return bab.getRawData();
    }
    
    @Override
    public DataHandler asDataHandler() {
        return new DataSourceStreamingDataHandler(this);
    }
    
    @Override
    public Source asSource() {
        return new StreamSource(this.asInputStream());
    }
    
    @Override
    public InputStream asInputStream() {
        final ByteArrayBuffer bab = new ByteArrayBuffer();
        try {
            this.writeTo(bab);
        }
        catch (final IOException e) {
            throw new WebServiceException(e);
        }
        return bab.newInputStream();
    }
    
    @Override
    public void writeTo(final OutputStream os) throws IOException {
        try {
            this.bridge.marshal(this.jaxbObject, os, null, null);
        }
        catch (final JAXBException e) {
            throw new WebServiceException(e);
        }
    }
    
    @Override
    public void writeTo(final SOAPMessage saaj) throws SOAPException {
        final AttachmentPart part = saaj.createAttachmentPart();
        part.setDataHandler(this.asDataHandler());
        part.setContentId(this.contentId);
        saaj.addAttachmentPart(part);
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        return this.asInputStream();
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getName() {
        return null;
    }
}
