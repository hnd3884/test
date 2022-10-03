package com.sun.mail.handlers;

import javax.mail.Multipart;
import java.io.OutputStream;
import javax.mail.MessagingException;
import java.io.IOException;
import javax.mail.internet.MimeMultipart;
import javax.activation.DataSource;
import javax.activation.ActivationDataFlavor;

public class multipart_mixed extends handler_base
{
    private static ActivationDataFlavor[] myDF;
    
    @Override
    protected ActivationDataFlavor[] getDataFlavors() {
        return multipart_mixed.myDF;
    }
    
    @Override
    public Object getContent(final DataSource ds) throws IOException {
        try {
            return new MimeMultipart(ds);
        }
        catch (final MessagingException e) {
            final IOException ioex = new IOException("Exception while constructing MimeMultipart");
            ioex.initCause(e);
            throw ioex;
        }
    }
    
    @Override
    public void writeTo(final Object obj, final String mimeType, final OutputStream os) throws IOException {
        if (obj instanceof Multipart) {
            try {
                ((Multipart)obj).writeTo(os);
            }
            catch (final MessagingException e) {
                final IOException ioex = new IOException("Exception writing Multipart");
                ioex.initCause(e);
                throw ioex;
            }
        }
    }
    
    static {
        multipart_mixed.myDF = new ActivationDataFlavor[] { new ActivationDataFlavor(Multipart.class, "multipart/mixed", "Multipart") };
    }
}
