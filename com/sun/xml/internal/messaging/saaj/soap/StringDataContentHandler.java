package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.activation.DataSource;
import java.awt.datatransfer.DataFlavor;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;

public class StringDataContentHandler implements DataContentHandler
{
    private static ActivationDataFlavor myDF;
    
    protected ActivationDataFlavor getDF() {
        return StringDataContentHandler.myDF;
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
        String enc = null;
        InputStreamReader is = null;
        try {
            enc = this.getCharset(ds.getContentType());
            is = new InputStreamReader(ds.getInputStream(), enc);
        }
        catch (final IllegalArgumentException iex) {
            throw new UnsupportedEncodingException(enc);
        }
        try {
            int pos;
            char[] buf;
            int count;
            int size;
            char[] tbuf = null;
            for (pos = 0, buf = new char[1024]; (count = is.read(buf, pos, buf.length - pos)) != -1; buf = tbuf) {
                pos += count;
                if (pos >= buf.length) {
                    size = buf.length;
                    if (size < 262144) {
                        size += size;
                    }
                    else {
                        size += 262144;
                    }
                    tbuf = new char[size];
                    System.arraycopy(buf, 0, tbuf, 0, pos);
                }
            }
            return new String(buf, 0, pos);
        }
        finally {
            try {
                is.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    @Override
    public void writeTo(final Object obj, final String type, final OutputStream os) throws IOException {
        if (!(obj instanceof String)) {
            throw new IOException("\"" + this.getDF().getMimeType() + "\" DataContentHandler requires String object, was given object of type " + obj.getClass().toString());
        }
        String enc = null;
        OutputStreamWriter osw = null;
        try {
            enc = this.getCharset(type);
            osw = new OutputStreamWriter(os, enc);
        }
        catch (final IllegalArgumentException iex) {
            throw new UnsupportedEncodingException(enc);
        }
        final String s = (String)obj;
        osw.write(s, 0, s.length());
        osw.flush();
    }
    
    private String getCharset(final String type) {
        try {
            final ContentType ct = new ContentType(type);
            String charset = ct.getParameter("charset");
            if (charset == null) {
                charset = "us-ascii";
            }
            return MimeUtility.javaCharset(charset);
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    static {
        StringDataContentHandler.myDF = new ActivationDataFlavor(String.class, "text/plain", "Text String");
    }
}
