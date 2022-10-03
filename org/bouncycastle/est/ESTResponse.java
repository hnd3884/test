package org.bouncycastle.est;

import java.io.EOFException;
import org.bouncycastle.util.Strings;
import java.io.IOException;
import java.util.Set;
import org.bouncycastle.util.Properties;
import java.io.InputStream;

public class ESTResponse
{
    private final ESTRequest originalRequest;
    private final HttpUtil.Headers headers;
    private final byte[] lineBuffer;
    private final Source source;
    private String HttpVersion;
    private int statusCode;
    private String statusMessage;
    private InputStream inputStream;
    private Long contentLength;
    private long read;
    private Long absoluteReadLimit;
    private static final Long ZERO;
    
    public ESTResponse(final ESTRequest originalRequest, final Source source) throws IOException {
        this.read = 0L;
        this.originalRequest = originalRequest;
        this.source = source;
        if (source instanceof LimitedSource) {
            this.absoluteReadLimit = ((LimitedSource)source).getAbsoluteReadLimit();
        }
        final Set keySet = Properties.asKeySet("org.bouncycastle.debug.est");
        if (keySet.contains("input") || keySet.contains("all")) {
            this.inputStream = new PrintingInputStream(source.getInputStream());
        }
        else {
            this.inputStream = source.getInputStream();
        }
        this.headers = new HttpUtil.Headers();
        this.lineBuffer = new byte[1024];
        this.process();
    }
    
    private void process() throws IOException {
        this.HttpVersion = this.readStringIncluding(' ');
        this.statusCode = Integer.parseInt(this.readStringIncluding(' '));
        this.statusMessage = this.readStringIncluding('\n');
        for (String s = this.readStringIncluding('\n'); s.length() > 0; s = this.readStringIncluding('\n')) {
            final int index = s.indexOf(58);
            if (index > -1) {
                this.headers.add(Strings.toLowerCase(s.substring(0, index).trim()), s.substring(index + 1).trim());
            }
        }
        this.contentLength = this.getContentLength();
        if (this.statusCode == 204 || this.statusCode == 202) {
            if (this.contentLength == null) {
                this.contentLength = 0L;
            }
            else if (this.statusCode == 204 && this.contentLength > 0L) {
                throw new IOException("Got HTTP status 204 but Content-length > 0.");
            }
        }
        if (this.contentLength == null) {
            throw new IOException("No Content-length header.");
        }
        if (this.contentLength.equals(ESTResponse.ZERO)) {
            this.inputStream = new InputStream() {
                @Override
                public int read() throws IOException {
                    return -1;
                }
            };
        }
        if (this.contentLength != null) {
            if (this.contentLength < 0L) {
                throw new IOException("Server returned negative content length: " + this.absoluteReadLimit);
            }
            if (this.absoluteReadLimit != null && this.contentLength >= this.absoluteReadLimit) {
                throw new IOException("Content length longer than absolute read limit: " + this.absoluteReadLimit + " Content-Length: " + this.contentLength);
            }
        }
        this.inputStream = this.wrapWithCounter(this.inputStream, this.absoluteReadLimit);
        if ("base64".equalsIgnoreCase(this.getHeader("content-transfer-encoding"))) {
            this.inputStream = new CTEBase64InputStream(this.inputStream, this.getContentLength());
        }
    }
    
    public String getHeader(final String s) {
        return this.headers.getFirstValue(s);
    }
    
    protected InputStream wrapWithCounter(final InputStream inputStream, final Long n) {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                final int read = inputStream.read();
                if (read > -1) {
                    ESTResponse.this.read++;
                    if (n != null && ESTResponse.this.read >= n) {
                        throw new IOException("Absolute Read Limit exceeded: " + n);
                    }
                }
                return read;
            }
            
            @Override
            public void close() throws IOException {
                if (ESTResponse.this.contentLength != null && ESTResponse.this.contentLength - 1L > ESTResponse.this.read) {
                    throw new IOException("Stream closed before limit fully read, Read: " + ESTResponse.this.read + " ContentLength: " + ESTResponse.this.contentLength);
                }
                if (inputStream.available() > 0) {
                    throw new IOException("Stream closed with extra content in pipe that exceeds content length.");
                }
                inputStream.close();
            }
        };
    }
    
    protected String readStringIncluding(final char c) throws IOException {
        int n = 0;
        int read;
        do {
            read = this.inputStream.read();
            this.lineBuffer[n++] = (byte)read;
            if (n >= this.lineBuffer.length) {
                throw new IOException("Server sent line > " + this.lineBuffer.length);
            }
        } while (read != c && read > -1);
        if (read == -1) {
            throw new EOFException();
        }
        return new String(this.lineBuffer, 0, n).trim();
    }
    
    public ESTRequest getOriginalRequest() {
        return this.originalRequest;
    }
    
    public HttpUtil.Headers getHeaders() {
        return this.headers;
    }
    
    public String getHttpVersion() {
        return this.HttpVersion;
    }
    
    public int getStatusCode() {
        return this.statusCode;
    }
    
    public String getStatusMessage() {
        return this.statusMessage;
    }
    
    public InputStream getInputStream() {
        return this.inputStream;
    }
    
    public Source getSource() {
        return this.source;
    }
    
    public Long getContentLength() {
        final String firstValue = this.headers.getFirstValue("Content-Length");
        if (firstValue == null) {
            return null;
        }
        try {
            return Long.parseLong(firstValue);
        }
        catch (final RuntimeException ex) {
            throw new RuntimeException("Content Length: '" + firstValue + "' invalid. " + ex.getMessage());
        }
    }
    
    public void close() throws IOException {
        if (this.inputStream != null) {
            this.inputStream.close();
        }
        this.source.close();
    }
    
    static {
        ZERO = 0L;
    }
    
    private class PrintingInputStream extends InputStream
    {
        private final InputStream src;
        
        private PrintingInputStream(final InputStream src) {
            this.src = src;
        }
        
        @Override
        public int read() throws IOException {
            final int read = this.src.read();
            System.out.print(String.valueOf((char)read));
            return read;
        }
        
        @Override
        public int available() throws IOException {
            return this.src.available();
        }
        
        @Override
        public void close() throws IOException {
            this.src.close();
        }
    }
}
