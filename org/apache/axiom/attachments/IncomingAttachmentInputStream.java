package org.apache.axiom.attachments;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;

public class IncomingAttachmentInputStream extends InputStream
{
    private HashMap _headers;
    private HashMap _headersLowerCase;
    private InputStream _stream;
    private IncomingAttachmentStreams parentContainer;
    public static final String HEADER_CONTENT_DESCRIPTION = "content-description";
    public static final String HEADER_CONTENT_TYPE = "content-type";
    public static final String HEADER_CONTENT_TRANSFER_ENCODING = "content-transfer-encoding";
    public static final String HEADER_CONTENT_LENGTH = "content-length";
    public static final String HEADER_CONTENT_LOCATION = "content-location";
    public static final String HEADER_CONTENT_ID = "content-id";
    
    public IncomingAttachmentInputStream(final InputStream in, final IncomingAttachmentStreams parentContainer) {
        this._headers = null;
        this._headersLowerCase = null;
        this._stream = null;
        this._stream = in;
        this.parentContainer = parentContainer;
    }
    
    public Map getHeaders() {
        return this._headers;
    }
    
    public void addHeader(final String name, final String value) {
        if (this._headers == null) {
            this._headers = new HashMap();
            this._headersLowerCase = new HashMap();
        }
        this._headers.put(name, value);
        this._headersLowerCase.put(name.toLowerCase(), value);
    }
    
    public String getHeader(final String name) {
        Object header = null;
        if (this._headersLowerCase == null || (header = this._headersLowerCase.get(name.toLowerCase())) == null) {
            return null;
        }
        return header.toString();
    }
    
    public String getContentId() {
        return this.getHeader("content-id");
    }
    
    public String getContentLocation() {
        return this.getHeader("content-location");
    }
    
    public String getContentType() {
        return this.getHeader("content-type");
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public void reset() throws IOException {
        throw new IOException("markNotSupported");
    }
    
    @Override
    public void mark(final int readLimit) {
    }
    
    @Override
    public int read() throws IOException {
        final int retval = this._stream.read();
        this.parentContainer.setReadyToGetNextStream(retval == -1);
        return retval;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        final int retval = this._stream.read(b);
        this.parentContainer.setReadyToGetNextStream(retval == -1);
        return retval;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int retval = this._stream.read(b, off, len);
        this.parentContainer.setReadyToGetNextStream(retval == -1);
        return retval;
    }
}
