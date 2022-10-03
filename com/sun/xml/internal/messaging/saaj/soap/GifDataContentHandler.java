package com.sun.xml.internal.messaging.saaj.soap;

import java.awt.Image;
import java.io.OutputStream;
import java.io.InputStream;
import java.awt.Toolkit;
import java.io.IOException;
import javax.activation.DataSource;
import java.awt.datatransfer.DataFlavor;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import java.awt.Component;

public class GifDataContentHandler extends Component implements DataContentHandler
{
    private static ActivationDataFlavor myDF;
    
    protected ActivationDataFlavor getDF() {
        return GifDataContentHandler.myDF;
    }
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { this.getDF() };
    }
    
    @Override
    public Object getTransferData(final DataFlavor df, final DataSource ds) throws IOException {
        if (this.getDF().equals(df)) {
            return this.getContent(ds);
        }
        return null;
    }
    
    @Override
    public Object getContent(final DataSource ds) throws IOException {
        InputStream is;
        int pos;
        byte[] buf;
        int count;
        int size;
        byte[] tbuf = null;
        for (is = ds.getInputStream(), pos = 0, buf = new byte[1024]; (count = is.read(buf, pos, buf.length - pos)) != -1; buf = tbuf) {
            pos += count;
            if (pos >= buf.length) {
                size = buf.length;
                if (size < 262144) {
                    size += size;
                }
                else {
                    size += 262144;
                }
                tbuf = new byte[size];
                System.arraycopy(buf, 0, tbuf, 0, pos);
            }
        }
        final Toolkit tk = Toolkit.getDefaultToolkit();
        return tk.createImage(buf, 0, pos);
    }
    
    @Override
    public void writeTo(final Object obj, final String type, final OutputStream os) throws IOException {
        if (obj != null && !(obj instanceof Image)) {
            throw new IOException("\"" + this.getDF().getMimeType() + "\" DataContentHandler requires Image object, was given object of type " + obj.getClass().toString());
        }
        throw new IOException(this.getDF().getMimeType() + " encoding not supported");
    }
    
    static {
        GifDataContentHandler.myDF = new ActivationDataFlavor(Image.class, "image/gif", "GIF Image");
    }
}
