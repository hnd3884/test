package org.apache.commons.httpclient.methods.multipart;

import org.apache.commons.logging.LogFactory;
import java.io.ByteArrayOutputStream;
import org.apache.commons.httpclient.util.EncodingUtil;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.logging.Log;

public abstract class Part
{
    private static final Log LOG;
    protected static final String BOUNDARY = "----------------314159265358979323846";
    protected static final byte[] BOUNDARY_BYTES;
    private static final byte[] DEFAULT_BOUNDARY_BYTES;
    protected static final String CRLF = "\r\n";
    protected static final byte[] CRLF_BYTES;
    protected static final String QUOTE = "\"";
    protected static final byte[] QUOTE_BYTES;
    protected static final String EXTRA = "--";
    protected static final byte[] EXTRA_BYTES;
    protected static final String CONTENT_DISPOSITION = "Content-Disposition: form-data; name=";
    protected static final byte[] CONTENT_DISPOSITION_BYTES;
    protected static final String CONTENT_TYPE = "Content-Type: ";
    protected static final byte[] CONTENT_TYPE_BYTES;
    protected static final String CHARSET = "; charset=";
    protected static final byte[] CHARSET_BYTES;
    protected static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding: ";
    protected static final byte[] CONTENT_TRANSFER_ENCODING_BYTES;
    private byte[] boundaryBytes;
    
    public static String getBoundary() {
        return "----------------314159265358979323846";
    }
    
    public abstract String getName();
    
    public abstract String getContentType();
    
    public abstract String getCharSet();
    
    public abstract String getTransferEncoding();
    
    protected byte[] getPartBoundary() {
        if (this.boundaryBytes == null) {
            return Part.DEFAULT_BOUNDARY_BYTES;
        }
        return this.boundaryBytes;
    }
    
    void setPartBoundary(final byte[] boundaryBytes) {
        this.boundaryBytes = boundaryBytes;
    }
    
    public boolean isRepeatable() {
        return true;
    }
    
    protected void sendStart(final OutputStream out) throws IOException {
        Part.LOG.trace((Object)"enter sendStart(OutputStream out)");
        out.write(Part.EXTRA_BYTES);
        out.write(this.getPartBoundary());
        out.write(Part.CRLF_BYTES);
    }
    
    protected void sendDispositionHeader(final OutputStream out) throws IOException {
        Part.LOG.trace((Object)"enter sendDispositionHeader(OutputStream out)");
        out.write(Part.CONTENT_DISPOSITION_BYTES);
        out.write(Part.QUOTE_BYTES);
        out.write(EncodingUtil.getAsciiBytes(this.getName()));
        out.write(Part.QUOTE_BYTES);
    }
    
    protected void sendContentTypeHeader(final OutputStream out) throws IOException {
        Part.LOG.trace((Object)"enter sendContentTypeHeader(OutputStream out)");
        final String contentType = this.getContentType();
        if (contentType != null) {
            out.write(Part.CRLF_BYTES);
            out.write(Part.CONTENT_TYPE_BYTES);
            out.write(EncodingUtil.getAsciiBytes(contentType));
            final String charSet = this.getCharSet();
            if (charSet != null) {
                out.write(Part.CHARSET_BYTES);
                out.write(EncodingUtil.getAsciiBytes(charSet));
            }
        }
    }
    
    protected void sendTransferEncodingHeader(final OutputStream out) throws IOException {
        Part.LOG.trace((Object)"enter sendTransferEncodingHeader(OutputStream out)");
        final String transferEncoding = this.getTransferEncoding();
        if (transferEncoding != null) {
            out.write(Part.CRLF_BYTES);
            out.write(Part.CONTENT_TRANSFER_ENCODING_BYTES);
            out.write(EncodingUtil.getAsciiBytes(transferEncoding));
        }
    }
    
    protected void sendEndOfHeader(final OutputStream out) throws IOException {
        Part.LOG.trace((Object)"enter sendEndOfHeader(OutputStream out)");
        out.write(Part.CRLF_BYTES);
        out.write(Part.CRLF_BYTES);
    }
    
    protected abstract void sendData(final OutputStream p0) throws IOException;
    
    protected abstract long lengthOfData() throws IOException;
    
    protected void sendEnd(final OutputStream out) throws IOException {
        Part.LOG.trace((Object)"enter sendEnd(OutputStream out)");
        out.write(Part.CRLF_BYTES);
    }
    
    public void send(final OutputStream out) throws IOException {
        Part.LOG.trace((Object)"enter send(OutputStream out)");
        this.sendStart(out);
        this.sendDispositionHeader(out);
        this.sendContentTypeHeader(out);
        this.sendTransferEncodingHeader(out);
        this.sendEndOfHeader(out);
        this.sendData(out);
        this.sendEnd(out);
    }
    
    public long length() throws IOException {
        Part.LOG.trace((Object)"enter length()");
        if (this.lengthOfData() < 0L) {
            return -1L;
        }
        final ByteArrayOutputStream overhead = new ByteArrayOutputStream();
        this.sendStart(overhead);
        this.sendDispositionHeader(overhead);
        this.sendContentTypeHeader(overhead);
        this.sendTransferEncodingHeader(overhead);
        this.sendEndOfHeader(overhead);
        this.sendEnd(overhead);
        return overhead.size() + this.lengthOfData();
    }
    
    public String toString() {
        return this.getName();
    }
    
    public static void sendParts(final OutputStream out, final Part[] parts) throws IOException {
        sendParts(out, parts, Part.DEFAULT_BOUNDARY_BYTES);
    }
    
    public static void sendParts(final OutputStream out, final Part[] parts, final byte[] partBoundary) throws IOException {
        if (parts == null) {
            throw new IllegalArgumentException("Parts may not be null");
        }
        if (partBoundary == null || partBoundary.length == 0) {
            throw new IllegalArgumentException("partBoundary may not be empty");
        }
        for (int i = 0; i < parts.length; ++i) {
            parts[i].setPartBoundary(partBoundary);
            parts[i].send(out);
        }
        out.write(Part.EXTRA_BYTES);
        out.write(partBoundary);
        out.write(Part.EXTRA_BYTES);
        out.write(Part.CRLF_BYTES);
    }
    
    public static long getLengthOfParts(final Part[] parts) throws IOException {
        return getLengthOfParts(parts, Part.DEFAULT_BOUNDARY_BYTES);
    }
    
    public static long getLengthOfParts(final Part[] parts, final byte[] partBoundary) throws IOException {
        Part.LOG.trace((Object)"getLengthOfParts(Parts[])");
        if (parts == null) {
            throw new IllegalArgumentException("Parts may not be null");
        }
        long total = 0L;
        for (int i = 0; i < parts.length; ++i) {
            parts[i].setPartBoundary(partBoundary);
            final long l = parts[i].length();
            if (l < 0L) {
                return -1L;
            }
            total += l;
        }
        total += Part.EXTRA_BYTES.length;
        total += partBoundary.length;
        total += Part.EXTRA_BYTES.length;
        total += Part.CRLF_BYTES.length;
        return total;
    }
    
    static {
        LOG = LogFactory.getLog(Part.class);
        BOUNDARY_BYTES = EncodingUtil.getAsciiBytes("----------------314159265358979323846");
        DEFAULT_BOUNDARY_BYTES = Part.BOUNDARY_BYTES;
        CRLF_BYTES = EncodingUtil.getAsciiBytes("\r\n");
        QUOTE_BYTES = EncodingUtil.getAsciiBytes("\"");
        EXTRA_BYTES = EncodingUtil.getAsciiBytes("--");
        CONTENT_DISPOSITION_BYTES = EncodingUtil.getAsciiBytes("Content-Disposition: form-data; name=");
        CONTENT_TYPE_BYTES = EncodingUtil.getAsciiBytes("Content-Type: ");
        CHARSET_BYTES = EncodingUtil.getAsciiBytes("; charset=");
        CONTENT_TRANSFER_ENCODING_BYTES = EncodingUtil.getAsciiBytes("Content-Transfer-Encoding: ");
    }
}
