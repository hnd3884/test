package com.sun.xml.internal.ws.message;

import javax.xml.soap.SOAPException;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPMessage;
import java.io.InputStream;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Source;
import java.io.IOException;
import javax.xml.ws.WebServiceException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import com.sun.istack.internal.NotNull;
import javax.activation.DataHandler;
import com.sun.xml.internal.ws.api.message.Attachment;

public final class DataHandlerAttachment implements Attachment
{
    private final DataHandler dh;
    private final String contentId;
    String contentIdNoAngleBracket;
    
    public DataHandlerAttachment(@NotNull final String contentId, @NotNull final DataHandler dh) {
        this.dh = dh;
        this.contentId = contentId;
    }
    
    @Override
    public String getContentId() {
        if (this.contentIdNoAngleBracket == null) {
            this.contentIdNoAngleBracket = this.contentId;
            if (this.contentIdNoAngleBracket != null && this.contentIdNoAngleBracket.charAt(0) == '<') {
                this.contentIdNoAngleBracket = this.contentIdNoAngleBracket.substring(1, this.contentIdNoAngleBracket.length() - 1);
            }
        }
        return this.contentIdNoAngleBracket;
    }
    
    @Override
    public String getContentType() {
        return this.dh.getContentType();
    }
    
    @Override
    public byte[] asByteArray() {
        try {
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            this.dh.writeTo(os);
            return os.toByteArray();
        }
        catch (final IOException e) {
            throw new WebServiceException(e);
        }
    }
    
    @Override
    public DataHandler asDataHandler() {
        return this.dh;
    }
    
    @Override
    public Source asSource() {
        try {
            return new StreamSource(this.dh.getInputStream());
        }
        catch (final IOException e) {
            throw new WebServiceException(e);
        }
    }
    
    @Override
    public InputStream asInputStream() {
        try {
            return this.dh.getInputStream();
        }
        catch (final IOException e) {
            throw new WebServiceException(e);
        }
    }
    
    @Override
    public void writeTo(final OutputStream os) throws IOException {
        this.dh.writeTo(os);
    }
    
    @Override
    public void writeTo(final SOAPMessage saaj) throws SOAPException {
        final AttachmentPart part = saaj.createAttachmentPart();
        part.setDataHandler(this.dh);
        part.setContentId(this.contentId);
        saaj.addAttachmentPart(part);
    }
}
