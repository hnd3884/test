package com.sun.mail.handlers;

import java.awt.Image;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Toolkit;
import javax.activation.DataSource;
import javax.activation.ActivationDataFlavor;

public class image_gif extends handler_base
{
    private static ActivationDataFlavor[] myDF;
    
    @Override
    protected ActivationDataFlavor[] getDataFlavors() {
        return image_gif.myDF;
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
        if (!(obj instanceof Image)) {
            throw new IOException("\"" + this.getDataFlavors()[0].getMimeType() + "\" DataContentHandler requires Image object, was given object of type " + obj.getClass().toString());
        }
        throw new IOException(this.getDataFlavors()[0].getMimeType() + " encoding not supported");
    }
    
    static {
        image_gif.myDF = new ActivationDataFlavor[] { new ActivationDataFlavor(Image.class, "image/gif", "GIF Image") };
    }
}
