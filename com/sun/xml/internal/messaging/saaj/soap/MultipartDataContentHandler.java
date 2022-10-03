package com.sun.xml.internal.messaging.saaj.soap;

import java.io.IOException;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import java.io.OutputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import javax.activation.DataSource;
import java.awt.datatransfer.DataFlavor;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;

public class MultipartDataContentHandler implements DataContentHandler
{
    private ActivationDataFlavor myDF;
    
    public MultipartDataContentHandler() {
        this.myDF = new ActivationDataFlavor(MimeMultipart.class, "multipart/mixed", "Multipart");
    }
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { this.myDF };
    }
    
    @Override
    public Object getTransferData(final DataFlavor df, final DataSource ds) {
        if (this.myDF.equals(df)) {
            return this.getContent(ds);
        }
        return null;
    }
    
    @Override
    public Object getContent(final DataSource ds) {
        try {
            return new MimeMultipart(ds, new ContentType(ds.getContentType()));
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    @Override
    public void writeTo(final Object obj, final String mimeType, final OutputStream os) throws IOException {
        if (obj instanceof MimeMultipart) {
            try {
                ByteOutputStream baos = null;
                if (!(os instanceof ByteOutputStream)) {
                    throw new IOException("Input Stream expected to be a com.sun.xml.internal.messaging.saaj.util.ByteOutputStream, but found " + os.getClass().getName());
                }
                baos = (ByteOutputStream)os;
                ((MimeMultipart)obj).writeTo(baos);
            }
            catch (final Exception e) {
                throw new IOException(e.toString());
            }
        }
    }
}
