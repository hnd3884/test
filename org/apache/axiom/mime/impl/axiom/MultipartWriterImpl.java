package org.apache.axiom.mime.impl.axiom;

import javax.activation.DataHandler;
import java.util.Iterator;
import org.apache.axiom.mime.Header;
import org.apache.axiom.util.base64.Base64EncodingOutputStream;
import java.util.List;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.axiom.mime.MultipartWriter;

class MultipartWriterImpl implements MultipartWriter
{
    private final OutputStream out;
    private final String boundary;
    private final byte[] buffer;
    
    public MultipartWriterImpl(final OutputStream out, final String boundary) {
        this.buffer = new byte[256];
        this.out = out;
        this.boundary = boundary;
    }
    
    void writeAscii(final String s) throws IOException {
        int count = 0;
        for (int i = 0, len = s.length(); i < len; ++i) {
            final char c = s.charAt(i);
            if (c >= '\u0080') {
                throw new IOException("Illegal character '" + c + "'");
            }
            this.buffer[count++] = (byte)c;
            if (count == this.buffer.length) {
                this.out.write(this.buffer);
                count = 0;
            }
        }
        if (count > 0) {
            this.out.write(this.buffer, 0, count);
        }
    }
    
    public OutputStream writePart(final String contentType, String contentTransferEncoding, final String contentID, final List extraHeaders) throws IOException {
        OutputStream transferEncoder;
        if (contentTransferEncoding.equals("8bit") || contentTransferEncoding.equals("binary")) {
            transferEncoder = this.out;
        }
        else {
            transferEncoder = new Base64EncodingOutputStream(this.out);
            contentTransferEncoding = "base64";
        }
        this.writeAscii("--");
        this.writeAscii(this.boundary);
        if (contentType != null) {
            this.writeAscii("\r\nContent-Type: ");
            this.writeAscii(contentType);
        }
        this.writeAscii("\r\nContent-Transfer-Encoding: ");
        this.writeAscii(contentTransferEncoding);
        if (contentID != null) {
            this.writeAscii("\r\nContent-ID: <");
            this.writeAscii(contentID);
            this.out.write(62);
        }
        if (extraHeaders != null) {
            for (final Header header : extraHeaders) {
                this.writeAscii("\r\n");
                this.writeAscii(header.getName());
                this.writeAscii(": ");
                this.writeAscii(header.getValue());
            }
        }
        this.writeAscii("\r\n\r\n");
        return new PartOutputStream(transferEncoder);
    }
    
    public OutputStream writePart(final String contentType, final String contentTransferEncoding, final String contentID) throws IOException {
        return this.writePart(contentType, contentTransferEncoding, contentID, null);
    }
    
    public void writePart(final DataHandler dataHandler, final String contentTransferEncoding, final String contentID, final List extraHeaders) throws IOException {
        final OutputStream partOutputStream = this.writePart(dataHandler.getContentType(), contentTransferEncoding, contentID, extraHeaders);
        dataHandler.writeTo(partOutputStream);
        partOutputStream.close();
    }
    
    public void writePart(final DataHandler dataHandler, final String contentTransferEncoding, final String contentID) throws IOException {
        this.writePart(dataHandler, contentTransferEncoding, contentID, null);
    }
    
    public void complete() throws IOException {
        this.writeAscii("--");
        this.writeAscii(this.boundary);
        this.writeAscii("--\r\n");
    }
    
    class PartOutputStream extends OutputStream
    {
        private final OutputStream parent;
        
        public PartOutputStream(final OutputStream parent) {
            this.parent = parent;
        }
        
        @Override
        public void write(final int b) throws IOException {
            this.parent.write(b);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            this.parent.write(b, off, len);
        }
        
        @Override
        public void write(final byte[] b) throws IOException {
            this.parent.write(b);
        }
        
        @Override
        public void close() throws IOException {
            if (this.parent instanceof Base64EncodingOutputStream) {
                ((Base64EncodingOutputStream)this.parent).complete();
            }
            MultipartWriterImpl.this.writeAscii("\r\n");
        }
    }
}
