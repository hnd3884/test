package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.bind.v2.runtime.output.UTF8XmlOutput;
import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.v2.util.ByteArrayOutputStreamEx;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.activation.DataSource;
import com.sun.istack.internal.Nullable;
import javax.activation.DataHandler;
import com.sun.xml.internal.bind.v2.runtime.output.Pcdata;

public final class Base64Data extends Pcdata
{
    private DataHandler dataHandler;
    private byte[] data;
    private int dataLen;
    @Nullable
    private String mimeType;
    
    public void set(final byte[] data, final int len, @Nullable final String mimeType) {
        this.data = data;
        this.dataLen = len;
        this.dataHandler = null;
        this.mimeType = mimeType;
    }
    
    public void set(final byte[] data, @Nullable final String mimeType) {
        this.set(data, data.length, mimeType);
    }
    
    public void set(final DataHandler data) {
        assert data != null;
        this.dataHandler = data;
        this.data = null;
    }
    
    public DataHandler getDataHandler() {
        if (this.dataHandler == null) {
            this.dataHandler = new DataHandler(new DataSource() {
                @Override
                public String getContentType() {
                    return Base64Data.this.getMimeType();
                }
                
                @Override
                public InputStream getInputStream() {
                    return new ByteArrayInputStream(Base64Data.this.data, 0, Base64Data.this.dataLen);
                }
                
                @Override
                public String getName() {
                    return null;
                }
                
                @Override
                public OutputStream getOutputStream() {
                    throw new UnsupportedOperationException();
                }
            });
        }
        return this.dataHandler;
    }
    
    public byte[] getExact() {
        this.get();
        if (this.dataLen != this.data.length) {
            final byte[] buf = new byte[this.dataLen];
            System.arraycopy(this.data, 0, buf, 0, this.dataLen);
            this.data = buf;
        }
        return this.data;
    }
    
    public InputStream getInputStream() throws IOException {
        if (this.dataHandler != null) {
            return this.dataHandler.getInputStream();
        }
        return new ByteArrayInputStream(this.data, 0, this.dataLen);
    }
    
    public boolean hasData() {
        return this.data != null;
    }
    
    public byte[] get() {
        if (this.data == null) {
            try {
                final ByteArrayOutputStreamEx baos = new ByteArrayOutputStreamEx(1024);
                final InputStream is = this.dataHandler.getDataSource().getInputStream();
                baos.readFrom(is);
                is.close();
                this.data = baos.getBuffer();
                this.dataLen = baos.size();
            }
            catch (final IOException e) {
                this.dataLen = 0;
            }
        }
        return this.data;
    }
    
    public int getDataLen() {
        return this.dataLen;
    }
    
    public String getMimeType() {
        if (this.mimeType == null) {
            return "application/octet-stream";
        }
        return this.mimeType;
    }
    
    @Override
    public int length() {
        this.get();
        return (this.dataLen + 2) / 3 * 4;
    }
    
    @Override
    public char charAt(final int index) {
        final int offset = index % 4;
        final int base = index / 4 * 3;
        switch (offset) {
            case 0: {
                return DatatypeConverterImpl.encode(this.data[base] >> 2);
            }
            case 1: {
                byte b1;
                if (base + 1 < this.dataLen) {
                    b1 = this.data[base + 1];
                }
                else {
                    b1 = 0;
                }
                return DatatypeConverterImpl.encode((this.data[base] & 0x3) << 4 | (b1 >> 4 & 0xF));
            }
            case 2: {
                if (base + 1 < this.dataLen) {
                    final byte b1 = this.data[base + 1];
                    byte b2;
                    if (base + 2 < this.dataLen) {
                        b2 = this.data[base + 2];
                    }
                    else {
                        b2 = 0;
                    }
                    return DatatypeConverterImpl.encode((b1 & 0xF) << 2 | (b2 >> 6 & 0x3));
                }
                return '=';
            }
            case 3: {
                if (base + 2 < this.dataLen) {
                    return DatatypeConverterImpl.encode(this.data[base + 2] & 0x3F);
                }
                return '=';
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public CharSequence subSequence(final int start, final int end) {
        final StringBuilder buf = new StringBuilder();
        this.get();
        for (int i = start; i < end; ++i) {
            buf.append(this.charAt(i));
        }
        return buf;
    }
    
    @Override
    public String toString() {
        this.get();
        return DatatypeConverterImpl._printBase64Binary(this.data, 0, this.dataLen);
    }
    
    @Override
    public void writeTo(final char[] buf, final int start) {
        this.get();
        DatatypeConverterImpl._printBase64Binary(this.data, 0, this.dataLen, buf, start);
    }
    
    @Override
    public void writeTo(final UTF8XmlOutput output) throws IOException {
        this.get();
        output.text(this.data, this.dataLen);
    }
    
    public void writeTo(final XMLStreamWriter output) throws IOException, XMLStreamException {
        this.get();
        DatatypeConverterImpl._printBase64Binary(this.data, 0, this.dataLen, output);
    }
}
