package com.sun.xml.internal.ws.message.stream;

import javax.xml.soap.SOAPException;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import java.io.OutputStream;
import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import java.io.InputStream;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import javax.xml.transform.Source;
import javax.activation.DataSource;
import com.sun.xml.internal.ws.encoding.DataSourceStreamingDataHandler;
import com.sun.xml.internal.ws.util.ByteArrayDataSource;
import javax.activation.DataHandler;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.xml.internal.ws.api.message.Attachment;

public class StreamAttachment implements Attachment
{
    private final String contentId;
    private final String contentType;
    private final ByteArrayBuffer byteArrayBuffer;
    private final byte[] data;
    private final int len;
    
    public StreamAttachment(final ByteArrayBuffer buffer, final String contentId, final String contentType) {
        this.contentId = contentId;
        this.contentType = contentType;
        this.byteArrayBuffer = buffer;
        this.data = this.byteArrayBuffer.getRawData();
        this.len = this.byteArrayBuffer.size();
    }
    
    @Override
    public String getContentId() {
        return this.contentId;
    }
    
    @Override
    public String getContentType() {
        return this.contentType;
    }
    
    @Override
    public byte[] asByteArray() {
        return this.byteArrayBuffer.toByteArray();
    }
    
    @Override
    public DataHandler asDataHandler() {
        return new DataSourceStreamingDataHandler(new ByteArrayDataSource(this.data, 0, this.len, this.getContentType()));
    }
    
    @Override
    public Source asSource() {
        return new StreamSource(new ByteArrayInputStream(this.data, 0, this.len));
    }
    
    @Override
    public InputStream asInputStream() {
        return this.byteArrayBuffer.newInputStream();
    }
    
    public Base64Data asBase64Data() {
        final Base64Data base64Data = new Base64Data();
        base64Data.set(this.data, this.len, this.contentType);
        return base64Data;
    }
    
    @Override
    public void writeTo(final OutputStream os) throws IOException {
        this.byteArrayBuffer.writeTo(os);
    }
    
    @Override
    public void writeTo(final SOAPMessage saaj) throws SOAPException {
        final AttachmentPart part = saaj.createAttachmentPart();
        part.setRawContentBytes(this.data, 0, this.len, this.getContentType());
        part.setContentId(this.contentId);
        saaj.addAttachmentPart(part);
    }
}
