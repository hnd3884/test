package org.bouncycastle.mail.smime.handlers;

import org.bouncycastle.mail.smime.SMIMEStreamingProcessor;
import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import java.io.OutputStream;
import java.io.IOException;
import javax.activation.DataSource;
import java.awt.datatransfer.DataFlavor;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;

public class PKCS7ContentHandler implements DataContentHandler
{
    private final ActivationDataFlavor _adf;
    private final DataFlavor[] _dfs;
    
    PKCS7ContentHandler(final ActivationDataFlavor adf, final DataFlavor[] dfs) {
        this._adf = adf;
        this._dfs = dfs;
    }
    
    public Object getContent(final DataSource dataSource) throws IOException {
        return dataSource.getInputStream();
    }
    
    public Object getTransferData(final DataFlavor dataFlavor, final DataSource dataSource) throws IOException {
        if (this._adf.equals(dataFlavor)) {
            return this.getContent(dataSource);
        }
        return null;
    }
    
    public DataFlavor[] getTransferDataFlavors() {
        return this._dfs;
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
        else if (o instanceof InputStream) {
            InputStream inputStream = (InputStream)o;
            if (!(inputStream instanceof BufferedInputStream)) {
                inputStream = new BufferedInputStream(inputStream);
            }
            int read;
            while ((read = inputStream.read()) >= 0) {
                outputStream.write(read);
            }
            inputStream.close();
        }
        else {
            if (!(o instanceof SMIMEStreamingProcessor)) {
                throw new IOException("unknown object in writeTo " + o);
            }
            ((SMIMEStreamingProcessor)o).write(outputStream);
        }
    }
}
