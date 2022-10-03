package org.apache.commons.httpclient;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.httpclient.util.ExceptionUtil;
import org.apache.commons.httpclient.util.EncodingUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.logging.Log;
import java.io.InputStream;

public class ChunkedInputStream extends InputStream
{
    private InputStream in;
    private int chunkSize;
    private int pos;
    private boolean bof;
    private boolean eof;
    private boolean closed;
    private HttpMethod method;
    private static final Log LOG;
    
    public ChunkedInputStream(final InputStream in, final HttpMethod method) throws IOException {
        this.bof = true;
        this.eof = false;
        this.closed = false;
        this.method = null;
        if (in == null) {
            throw new IllegalArgumentException("InputStream parameter may not be null");
        }
        this.in = in;
        this.method = method;
        this.pos = 0;
    }
    
    public ChunkedInputStream(final InputStream in) throws IOException {
        this(in, null);
    }
    
    public int read() throws IOException {
        if (this.closed) {
            throw new IOException("Attempted read from closed stream.");
        }
        if (this.eof) {
            return -1;
        }
        if (this.pos >= this.chunkSize) {
            this.nextChunk();
            if (this.eof) {
                return -1;
            }
        }
        ++this.pos;
        return this.in.read();
    }
    
    public int read(final byte[] b, final int off, int len) throws IOException {
        if (this.closed) {
            throw new IOException("Attempted read from closed stream.");
        }
        if (this.eof) {
            return -1;
        }
        if (this.pos >= this.chunkSize) {
            this.nextChunk();
            if (this.eof) {
                return -1;
            }
        }
        len = Math.min(len, this.chunkSize - this.pos);
        final int count = this.in.read(b, off, len);
        this.pos += count;
        return count;
    }
    
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    private void readCRLF() throws IOException {
        final int cr = this.in.read();
        final int lf = this.in.read();
        if (cr != 13 || lf != 10) {
            throw new IOException("CRLF expected at end of chunk: " + cr + "/" + lf);
        }
    }
    
    private void nextChunk() throws IOException {
        if (!this.bof) {
            this.readCRLF();
        }
        this.chunkSize = getChunkSizeFromInputStream(this.in);
        this.bof = false;
        this.pos = 0;
        if (this.chunkSize == 0) {
            this.eof = true;
            this.parseTrailerHeaders();
        }
    }
    
    private static int getChunkSizeFromInputStream(final InputStream in) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int state = 0;
        while (state != -1) {
            int b = in.read();
            if (b == -1) {
                throw new IOException("chunked stream ended unexpectedly");
            }
            switch (state) {
                case 0: {
                    switch (b) {
                        case 13: {
                            state = 1;
                            continue;
                        }
                        case 34: {
                            state = 2;
                            break;
                        }
                    }
                    baos.write(b);
                    continue;
                }
                case 1: {
                    if (b == 10) {
                        state = -1;
                        continue;
                    }
                    throw new IOException("Protocol violation: Unexpected single newline character in chunk size");
                }
                case 2: {
                    switch (b) {
                        case 92: {
                            b = in.read();
                            baos.write(b);
                            continue;
                        }
                        case 34: {
                            state = 0;
                            break;
                        }
                    }
                    baos.write(b);
                    continue;
                }
                default: {
                    throw new RuntimeException("assertion failed");
                }
            }
        }
        String dataString = EncodingUtil.getAsciiString(baos.toByteArray());
        final int separator = dataString.indexOf(59);
        dataString = ((separator > 0) ? dataString.substring(0, separator).trim() : dataString.trim());
        int result;
        try {
            result = Integer.parseInt(dataString.trim(), 16);
        }
        catch (final NumberFormatException e) {
            throw new IOException("Bad chunk size: " + dataString);
        }
        return result;
    }
    
    private void parseTrailerHeaders() throws IOException {
        Header[] footers = null;
        try {
            String charset = "US-ASCII";
            if (this.method != null) {
                charset = this.method.getParams().getHttpElementCharset();
            }
            footers = HttpParser.parseHeaders(this.in, charset);
        }
        catch (final HttpException e) {
            ChunkedInputStream.LOG.error((Object)"Error parsing trailer headers", (Throwable)e);
            final IOException ioe = new IOException(e.getMessage());
            ExceptionUtil.initCause(ioe, e);
            throw ioe;
        }
        if (this.method != null) {
            for (int i = 0; i < footers.length; ++i) {
                this.method.addResponseFooter(footers[i]);
            }
        }
    }
    
    public void close() throws IOException {
        if (!this.closed) {
            try {
                if (!this.eof) {
                    exhaustInputStream(this);
                }
            }
            finally {
                this.eof = true;
                this.closed = true;
            }
        }
    }
    
    static void exhaustInputStream(final InputStream inStream) throws IOException {
        final byte[] buffer = new byte[1024];
        while (inStream.read(buffer) >= 0) {}
    }
    
    static {
        LOG = LogFactory.getLog(ChunkedInputStream.class);
    }
}
