package com.sun.xml.internal.messaging.saaj.soap;

import java.io.InputStream;
import javax.xml.transform.Source;
import java.io.OutputStream;
import java.io.IOException;
import javax.activation.DataSource;
import javax.activation.ActivationDataFlavor;
import com.sun.xml.internal.messaging.saaj.util.FastInfosetReflection;
import java.awt.datatransfer.DataFlavor;
import javax.activation.DataContentHandler;

public class FastInfosetDataContentHandler implements DataContentHandler
{
    public static final String STR_SRC = "com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource";
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        final DataFlavor[] flavors = { new ActivationDataFlavor(FastInfosetReflection.getFastInfosetSource_class(), "application/fastinfoset", "Fast Infoset") };
        return flavors;
    }
    
    @Override
    public Object getTransferData(final DataFlavor flavor, final DataSource dataSource) throws IOException {
        if (flavor.getMimeType().startsWith("application/fastinfoset")) {
            try {
                if (flavor.getRepresentationClass().getName().equals("com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource")) {
                    return FastInfosetReflection.FastInfosetSource_new(dataSource.getInputStream());
                }
            }
            catch (final Exception e) {
                throw new IOException(e.getMessage());
            }
        }
        return null;
    }
    
    @Override
    public Object getContent(final DataSource dataSource) throws IOException {
        try {
            return FastInfosetReflection.FastInfosetSource_new(dataSource.getInputStream());
        }
        catch (final Exception e) {
            throw new IOException(e.getMessage());
        }
    }
    
    @Override
    public void writeTo(final Object obj, final String mimeType, final OutputStream os) throws IOException {
        if (!mimeType.equals("application/fastinfoset")) {
            throw new IOException("Invalid content type \"" + mimeType + "\" for FastInfosetDCH");
        }
        try {
            final InputStream is = FastInfosetReflection.FastInfosetSource_getInputStream((Source)obj);
            final byte[] buffer = new byte[4096];
            int n;
            while ((n = is.read(buffer)) != -1) {
                os.write(buffer, 0, n);
            }
        }
        catch (final Exception ex) {
            throw new IOException("Error copying FI source to output stream " + ex.getMessage());
        }
    }
}
