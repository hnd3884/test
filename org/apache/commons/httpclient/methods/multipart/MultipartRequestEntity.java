package org.apache.commons.httpclient.methods.multipart;

import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.httpclient.util.EncodingUtil;
import java.util.Random;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.httpclient.methods.RequestEntity;

public class MultipartRequestEntity implements RequestEntity
{
    private static final Log log;
    private static final String MULTIPART_FORM_CONTENT_TYPE = "multipart/form-data";
    private static byte[] MULTIPART_CHARS;
    protected Part[] parts;
    private byte[] multipartBoundary;
    private HttpMethodParams params;
    
    private static byte[] generateMultipartBoundary() {
        final Random rand = new Random();
        final byte[] bytes = new byte[rand.nextInt(11) + 30];
        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] = MultipartRequestEntity.MULTIPART_CHARS[rand.nextInt(MultipartRequestEntity.MULTIPART_CHARS.length)];
        }
        return bytes;
    }
    
    public MultipartRequestEntity(final Part[] parts, final HttpMethodParams params) {
        if (parts == null) {
            throw new IllegalArgumentException("parts cannot be null");
        }
        if (params == null) {
            throw new IllegalArgumentException("params cannot be null");
        }
        this.parts = parts;
        this.params = params;
    }
    
    protected byte[] getMultipartBoundary() {
        if (this.multipartBoundary == null) {
            final String temp = (String)this.params.getParameter("http.method.multipart.boundary");
            if (temp != null) {
                this.multipartBoundary = EncodingUtil.getAsciiBytes(temp);
            }
            else {
                this.multipartBoundary = generateMultipartBoundary();
            }
        }
        return this.multipartBoundary;
    }
    
    public boolean isRepeatable() {
        for (int i = 0; i < this.parts.length; ++i) {
            if (!this.parts[i].isRepeatable()) {
                return false;
            }
        }
        return true;
    }
    
    public void writeRequest(final OutputStream out) throws IOException {
        Part.sendParts(out, this.parts, this.getMultipartBoundary());
    }
    
    public long getContentLength() {
        try {
            return Part.getLengthOfParts(this.parts, this.getMultipartBoundary());
        }
        catch (final Exception e) {
            MultipartRequestEntity.log.error((Object)"An exception occurred while getting the length of the parts", (Throwable)e);
            return 0L;
        }
    }
    
    public String getContentType() {
        final StringBuffer buffer = new StringBuffer("multipart/form-data");
        buffer.append("; boundary=");
        buffer.append(EncodingUtil.getAsciiString(this.getMultipartBoundary()));
        return buffer.toString();
    }
    
    static {
        log = LogFactory.getLog(MultipartRequestEntity.class);
        MultipartRequestEntity.MULTIPART_CHARS = EncodingUtil.getAsciiBytes("-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }
}
