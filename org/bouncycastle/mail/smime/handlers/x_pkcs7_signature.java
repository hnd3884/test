package org.bouncycastle.mail.smime.handlers;

import java.io.InputStream;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import java.io.OutputStream;
import java.io.IOException;
import javax.activation.DataSource;
import java.awt.datatransfer.DataFlavor;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;

public class x_pkcs7_signature implements DataContentHandler
{
    private static final ActivationDataFlavor ADF;
    private static final DataFlavor[] ADFs;
    
    public Object getContent(final DataSource dataSource) throws IOException {
        return dataSource.getInputStream();
    }
    
    public Object getTransferData(final DataFlavor dataFlavor, final DataSource dataSource) throws IOException {
        if (x_pkcs7_signature.ADF.equals(dataFlavor)) {
            return this.getContent(dataSource);
        }
        return null;
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        return x_pkcs7_signature.ADFs;
    }
    
    public void writeTo(final Object o, final String s, final OutputStream outputStream) throws IOException {
        if (o instanceof MimeBodyPart) {
            try {
                ((MimeBodyPart)o).writeTo(outputStream);
                return;
            }
            catch (final MessagingException ex) {
                throw new IOException(ex.getMessage());
            }
        }
        if (o instanceof byte[]) {
            outputStream.write((byte[])o);
        }
        else {
            if (!(o instanceof InputStream)) {
                throw new IOException("unknown object in writeTo " + o);
            }
            int read;
            while ((read = ((InputStream)o).read()) >= 0) {
                outputStream.write(read);
            }
        }
    }
    
    static {
        ADF = new ActivationDataFlavor(MimeBodyPart.class, "application/x-pkcs7-signature", "Signature");
        ADFs = new DataFlavor[] { x_pkcs7_signature.ADF };
    }
}
