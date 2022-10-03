package com.sun.xml.internal.org.jvnet.staxex;

import java.io.FileOutputStream;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.activation.DataSource;
import java.util.logging.Logger;
import javax.activation.DataHandler;

public class Base64Data implements CharSequence, Cloneable
{
    private DataHandler dataHandler;
    private byte[] data;
    private int dataLen;
    private boolean dataCloneByRef;
    private String mimeType;
    private static final Logger logger;
    private static final int CHUNK_SIZE;
    
    public Base64Data() {
    }
    
    public Base64Data(final Base64Data that) {
        that.get();
        if (that.dataCloneByRef) {
            this.data = that.data;
        }
        else {
            this.data = new byte[that.dataLen];
            System.arraycopy(that.data, 0, this.data, 0, that.dataLen);
        }
        this.dataCloneByRef = true;
        this.dataLen = that.dataLen;
        this.dataHandler = null;
        this.mimeType = that.mimeType;
    }
    
    public void set(final byte[] data, final int len, final String mimeType, final boolean cloneByRef) {
        this.data = data;
        this.dataLen = len;
        this.dataCloneByRef = cloneByRef;
        this.dataHandler = null;
        this.mimeType = mimeType;
    }
    
    public void set(final byte[] data, final int len, final String mimeType) {
        this.set(data, len, mimeType, false);
    }
    
    public void set(final byte[] data, final String mimeType) {
        this.set(data, data.length, mimeType, false);
    }
    
    public void set(final DataHandler data) {
        assert data != null;
        this.dataHandler = data;
        this.data = null;
    }
    
    public DataHandler getDataHandler() {
        if (this.dataHandler == null) {
            this.dataHandler = new Base64StreamingDataHandler(new Base64DataSource());
        }
        else if (!(this.dataHandler instanceof StreamingDataHandler)) {
            this.dataHandler = new FilterDataHandler(this.dataHandler);
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
                this.dataCloneByRef = true;
            }
            catch (final IOException e) {
                this.dataLen = 0;
            }
        }
        return this.data;
    }
    
    public int getDataLen() {
        this.get();
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
                return Base64Encoder.encode(this.data[base] >> 2);
            }
            case 1: {
                byte b1;
                if (base + 1 < this.dataLen) {
                    b1 = this.data[base + 1];
                }
                else {
                    b1 = 0;
                }
                return Base64Encoder.encode((this.data[base] & 0x3) << 4 | (b1 >> 4 & 0xF));
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
                    return Base64Encoder.encode((b1 & 0xF) << 2 | (b2 >> 6 & 0x3));
                }
                return '=';
            }
            case 3: {
                if (base + 2 < this.dataLen) {
                    return Base64Encoder.encode(this.data[base + 2] & 0x3F);
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
        return Base64Encoder.print(this.data, 0, this.dataLen);
    }
    
    public void writeTo(final char[] buf, final int start) {
        this.get();
        Base64Encoder.print(this.data, 0, this.dataLen, buf, start);
    }
    
    public void writeTo(final XMLStreamWriter output) throws IOException, XMLStreamException {
        if (this.data == null) {
            try {
                final InputStream is = this.dataHandler.getDataSource().getInputStream();
                final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                final Base64EncoderStream encWriter = new Base64EncoderStream(output, outStream);
                final byte[] buffer = new byte[Base64Data.CHUNK_SIZE];
                int b;
                while ((b = is.read(buffer)) != -1) {
                    encWriter.write(buffer, 0, b);
                }
                outStream.close();
                encWriter.close();
                return;
            }
            catch (final IOException e) {
                this.dataLen = 0;
                throw e;
            }
        }
        final String s = Base64Encoder.print(this.data, 0, this.dataLen);
        output.writeCharacters(s);
    }
    
    public Base64Data clone() {
        try {
            final Base64Data clone = (Base64Data)super.clone();
            clone.get();
            if (clone.dataCloneByRef) {
                this.data = clone.data;
            }
            else {
                this.data = new byte[clone.dataLen];
                System.arraycopy(clone.data, 0, this.data, 0, clone.dataLen);
            }
            this.dataCloneByRef = true;
            this.dataLen = clone.dataLen;
            this.dataHandler = null;
            this.mimeType = clone.mimeType;
            return clone;
        }
        catch (final CloneNotSupportedException ex) {
            Logger.getLogger(Base64Data.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    static String getProperty(final String propName) {
        if (System.getSecurityManager() == null) {
            return System.getProperty(propName);
        }
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            @Override
            public Object run() {
                return System.getProperty(propName);
            }
        });
    }
    
    static {
        logger = Logger.getLogger(Base64Data.class.getName());
        int bufSize = 1024;
        try {
            final String bufSizeStr = getProperty("com.sun.xml.internal.org.jvnet.staxex.Base64DataStreamWriteBufferSize");
            if (bufSizeStr != null) {
                bufSize = Integer.parseInt(bufSizeStr);
            }
        }
        catch (final Exception e) {
            Base64Data.logger.log(Level.INFO, "Error reading com.sun.xml.internal.org.jvnet.staxex.Base64DataStreamWriteBufferSize property", e);
        }
        CHUNK_SIZE = bufSize;
    }
    
    private final class Base64DataSource implements DataSource
    {
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
    }
    
    private final class Base64StreamingDataHandler extends StreamingDataHandler
    {
        Base64StreamingDataHandler(final DataSource source) {
            super(source);
        }
        
        @Override
        public InputStream readOnce() throws IOException {
            return this.getDataSource().getInputStream();
        }
        
        @Override
        public void moveTo(final File dst) throws IOException {
            final FileOutputStream fout = new FileOutputStream(dst);
            try {
                fout.write(Base64Data.this.data, 0, Base64Data.this.dataLen);
            }
            finally {
                fout.close();
            }
        }
        
        @Override
        public void close() throws IOException {
        }
    }
    
    private static final class FilterDataHandler extends StreamingDataHandler
    {
        FilterDataHandler(final DataHandler dh) {
            super(dh.getDataSource());
        }
        
        @Override
        public InputStream readOnce() throws IOException {
            return this.getDataSource().getInputStream();
        }
        
        @Override
        public void moveTo(final File dst) throws IOException {
            final byte[] buf = new byte[8192];
            InputStream in = null;
            OutputStream out = null;
            try {
                in = this.getDataSource().getInputStream();
                out = new FileOutputStream(dst);
                while (true) {
                    final int amountRead = in.read(buf);
                    if (amountRead == -1) {
                        break;
                    }
                    out.write(buf, 0, amountRead);
                }
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (final IOException ex) {}
                }
                if (out != null) {
                    try {
                        out.close();
                    }
                    catch (final IOException ex2) {}
                }
            }
        }
        
        @Override
        public void close() throws IOException {
        }
    }
}
