package com.sun.xml.internal.ws.message;

import javax.xml.soap.SOAPException;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Source;
import javax.activation.DataSource;
import com.sun.xml.internal.ws.encoding.DataSourceStreamingDataHandler;
import com.sun.xml.internal.ws.util.ByteArrayDataSource;
import javax.activation.DataHandler;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Attachment;

public final class ByteArrayAttachment implements Attachment
{
    private final String contentId;
    private byte[] data;
    private int start;
    private final int len;
    private final String mimeType;
    
    public ByteArrayAttachment(@NotNull final String contentId, final byte[] data, final int start, final int len, final String mimeType) {
        this.contentId = contentId;
        this.data = data;
        this.start = start;
        this.len = len;
        this.mimeType = mimeType;
    }
    
    public ByteArrayAttachment(@NotNull final String contentId, final byte[] data, final String mimeType) {
        this(contentId, data, 0, data.length, mimeType);
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
        if (this.start != 0 || this.len != this.data.length) {
            final byte[] exact = new byte[this.len];
            System.arraycopy(this.data, this.start, exact, 0, this.len);
            this.start = 0;
            this.data = exact;
        }
        return this.data;
    }
    
    @Override
    public DataHandler asDataHandler() {
        return new DataSourceStreamingDataHandler(new ByteArrayDataSource(this.data, this.start, this.len, this.getContentType()));
    }
    
    @Override
    public Source asSource() {
        return new StreamSource(this.asInputStream());
    }
    
    @Override
    public InputStream asInputStream() {
        return new ByteArrayInputStream(this.data, this.start, this.len);
    }
    
    @Override
    public void writeTo(final OutputStream os) throws IOException {
        os.write(this.asByteArray());
    }
    
    @Override
    public void writeTo(final SOAPMessage saaj) throws SOAPException {
        final AttachmentPart part = saaj.createAttachmentPart();
        part.setDataHandler(this.asDataHandler());
        part.setContentId(this.contentId);
        saaj.addAttachmentPart(part);
    }
}
