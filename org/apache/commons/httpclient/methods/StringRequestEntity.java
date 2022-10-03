package org.apache.commons.httpclient.methods;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.HeaderElement;

public class StringRequestEntity implements RequestEntity
{
    private byte[] content;
    private String charset;
    private String contentType;
    
    public StringRequestEntity(final String content) {
        if (content == null) {
            throw new IllegalArgumentException("The content cannot be null");
        }
        this.contentType = null;
        this.charset = null;
        this.content = content.getBytes();
    }
    
    public StringRequestEntity(final String content, final String contentType, final String charset) throws UnsupportedEncodingException {
        if (content == null) {
            throw new IllegalArgumentException("The content cannot be null");
        }
        this.contentType = contentType;
        this.charset = charset;
        if (contentType != null) {
            final HeaderElement[] values = HeaderElement.parseElements(contentType);
            NameValuePair charsetPair = null;
            for (int i = 0; i < values.length && (charsetPair = values[i].getParameterByName("charset")) == null; ++i) {}
            if (charset == null && charsetPair != null) {
                this.charset = charsetPair.getValue();
            }
            else if (charset != null && charsetPair == null) {
                this.contentType = contentType + "; charset=" + charset;
            }
        }
        if (this.charset != null) {
            this.content = content.getBytes(this.charset);
        }
        else {
            this.content = content.getBytes();
        }
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public boolean isRepeatable() {
        return true;
    }
    
    public void writeRequest(final OutputStream out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        out.write(this.content);
        out.flush();
    }
    
    public long getContentLength() {
        return this.content.length;
    }
    
    public String getContent() {
        if (this.charset != null) {
            try {
                return new String(this.content, this.charset);
            }
            catch (final UnsupportedEncodingException e) {
                return new String(this.content);
            }
        }
        return new String(this.content);
    }
    
    public String getCharset() {
        return this.charset;
    }
}
