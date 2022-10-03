package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import java.net.UnknownServiceException;
import java.io.OutputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.DataSource;

public final class MimePartDataSource implements DataSource
{
    private final MimeBodyPart part;
    
    public MimePartDataSource(final MimeBodyPart part) {
        this.part = part;
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        try {
            final InputStream is = this.part.getContentStream();
            final String encoding = this.part.getEncoding();
            if (encoding != null) {
                return MimeUtility.decode(is, encoding);
            }
            return is;
        }
        catch (final MessagingException mex) {
            throw new IOException(mex.getMessage());
        }
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnknownServiceException();
    }
    
    @Override
    public String getContentType() {
        return this.part.getContentType();
    }
    
    @Override
    public String getName() {
        try {
            return this.part.getFileName();
        }
        catch (final MessagingException mex) {
            return "";
        }
    }
}
