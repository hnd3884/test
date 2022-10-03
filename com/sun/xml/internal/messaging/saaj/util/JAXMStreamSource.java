package com.sun.xml.internal.messaging.saaj.util;

import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import javax.xml.transform.stream.StreamSource;

public class JAXMStreamSource extends StreamSource
{
    InputStream in;
    Reader reader;
    private static final boolean lazyContentLength;
    
    public JAXMStreamSource(final InputStream is) throws IOException {
        if (JAXMStreamSource.lazyContentLength) {
            this.in = is;
        }
        else if (is instanceof ByteInputStream) {
            this.in = is;
        }
        else {
            final ByteOutputStream bout = new ByteOutputStream();
            bout.write(is);
            this.in = bout.newInputStream();
        }
    }
    
    public JAXMStreamSource(final Reader rdr) throws IOException {
        if (JAXMStreamSource.lazyContentLength) {
            this.reader = rdr;
            return;
        }
        final CharWriter cout = new CharWriter();
        final char[] temp = new char[1024];
        int len;
        while (-1 != (len = rdr.read(temp))) {
            cout.write(temp, 0, len);
        }
        this.reader = new CharReader(cout.getChars(), cout.getCount());
    }
    
    @Override
    public InputStream getInputStream() {
        return this.in;
    }
    
    @Override
    public Reader getReader() {
        return this.reader;
    }
    
    public void reset() throws IOException {
        if (this.in != null) {
            this.in.reset();
        }
        if (this.reader != null) {
            this.reader.reset();
        }
    }
    
    static {
        lazyContentLength = SAAJUtil.getSystemBoolean("saaj.lazy.contentlength");
    }
}
