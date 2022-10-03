package org.apache.http.impl.io;

import org.apache.http.io.HttpTransportMetrics;
import java.nio.charset.CoderResult;
import java.nio.ByteBuffer;
import org.apache.http.MessageConstraintException;
import org.apache.http.util.CharArrayBuffer;
import java.io.IOException;
import org.apache.http.util.Asserts;
import org.apache.http.util.Args;
import java.nio.CharBuffer;
import java.io.InputStream;
import java.nio.charset.CharsetDecoder;
import org.apache.http.config.MessageConstraints;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.io.BufferInfo;
import org.apache.http.io.SessionInputBuffer;

public class SessionInputBufferImpl implements SessionInputBuffer, BufferInfo
{
    private final HttpTransportMetricsImpl metrics;
    private final byte[] buffer;
    private final ByteArrayBuffer lineBuffer;
    private final int minChunkLimit;
    private final MessageConstraints constraints;
    private final CharsetDecoder decoder;
    private InputStream inStream;
    private int bufferPos;
    private int bufferLen;
    private CharBuffer cbuf;
    
    public SessionInputBufferImpl(final HttpTransportMetricsImpl metrics, final int bufferSize, final int minChunkLimit, final MessageConstraints constraints, final CharsetDecoder charDecoder) {
        Args.notNull(metrics, "HTTP transport metrcis");
        Args.positive(bufferSize, "Buffer size");
        this.metrics = metrics;
        this.buffer = new byte[bufferSize];
        this.bufferPos = 0;
        this.bufferLen = 0;
        this.minChunkLimit = ((minChunkLimit >= 0) ? minChunkLimit : 512);
        this.constraints = ((constraints != null) ? constraints : MessageConstraints.DEFAULT);
        this.lineBuffer = new ByteArrayBuffer(bufferSize);
        this.decoder = charDecoder;
    }
    
    public SessionInputBufferImpl(final HttpTransportMetricsImpl metrics, final int bufferSize) {
        this(metrics, bufferSize, bufferSize, null, null);
    }
    
    public void bind(final InputStream inputStream) {
        this.inStream = inputStream;
    }
    
    public boolean isBound() {
        return this.inStream != null;
    }
    
    @Override
    public int capacity() {
        return this.buffer.length;
    }
    
    @Override
    public int length() {
        return this.bufferLen - this.bufferPos;
    }
    
    @Override
    public int available() {
        return this.capacity() - this.length();
    }
    
    private int streamRead(final byte[] b, final int off, final int len) throws IOException {
        Asserts.notNull(this.inStream, "Input stream");
        return this.inStream.read(b, off, len);
    }
    
    public int fillBuffer() throws IOException {
        if (this.bufferPos > 0) {
            final int len = this.bufferLen - this.bufferPos;
            if (len > 0) {
                System.arraycopy(this.buffer, this.bufferPos, this.buffer, 0, len);
            }
            this.bufferPos = 0;
            this.bufferLen = len;
        }
        final int off = this.bufferLen;
        final int len2 = this.buffer.length - off;
        final int readLen = this.streamRead(this.buffer, off, len2);
        if (readLen == -1) {
            return -1;
        }
        this.bufferLen = off + readLen;
        this.metrics.incrementBytesTransferred(readLen);
        return readLen;
    }
    
    public boolean hasBufferedData() {
        return this.bufferPos < this.bufferLen;
    }
    
    public void clear() {
        this.bufferPos = 0;
        this.bufferLen = 0;
    }
    
    @Override
    public int read() throws IOException {
        while (!this.hasBufferedData()) {
            final int noRead = this.fillBuffer();
            if (noRead == -1) {
                return -1;
            }
        }
        return this.buffer[this.bufferPos++] & 0xFF;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (b == null) {
            return 0;
        }
        if (this.hasBufferedData()) {
            final int chunk = Math.min(len, this.bufferLen - this.bufferPos);
            System.arraycopy(this.buffer, this.bufferPos, b, off, chunk);
            this.bufferPos += chunk;
            return chunk;
        }
        if (len > this.minChunkLimit) {
            final int readLen = this.streamRead(b, off, len);
            if (readLen > 0) {
                this.metrics.incrementBytesTransferred(readLen);
            }
            return readLen;
        }
        while (!this.hasBufferedData()) {
            final int noRead = this.fillBuffer();
            if (noRead == -1) {
                return -1;
            }
        }
        final int chunk = Math.min(len, this.bufferLen - this.bufferPos);
        System.arraycopy(this.buffer, this.bufferPos, b, off, chunk);
        this.bufferPos += chunk;
        return chunk;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        if (b == null) {
            return 0;
        }
        return this.read(b, 0, b.length);
    }
    
    @Override
    public int readLine(final CharArrayBuffer charbuffer) throws IOException {
        Args.notNull(charbuffer, "Char array buffer");
        final int maxLineLen = this.constraints.getMaxLineLength();
        int noRead = 0;
        boolean retry = true;
        while (retry) {
            int pos = -1;
            for (int i = this.bufferPos; i < this.bufferLen; ++i) {
                if (this.buffer[i] == 10) {
                    pos = i;
                    break;
                }
            }
            if (maxLineLen > 0) {
                final int currentLen = this.lineBuffer.length() + ((pos >= 0) ? pos : this.bufferLen) - this.bufferPos;
                if (currentLen >= maxLineLen) {
                    throw new MessageConstraintException("Maximum line length limit exceeded");
                }
            }
            if (pos != -1) {
                if (this.lineBuffer.isEmpty()) {
                    return this.lineFromReadBuffer(charbuffer, pos);
                }
                retry = false;
                final int len = pos + 1 - this.bufferPos;
                this.lineBuffer.append(this.buffer, this.bufferPos, len);
                this.bufferPos = pos + 1;
            }
            else {
                if (this.hasBufferedData()) {
                    final int len = this.bufferLen - this.bufferPos;
                    this.lineBuffer.append(this.buffer, this.bufferPos, len);
                    this.bufferPos = this.bufferLen;
                }
                noRead = this.fillBuffer();
                if (noRead != -1) {
                    continue;
                }
                retry = false;
            }
        }
        if (noRead == -1 && this.lineBuffer.isEmpty()) {
            return -1;
        }
        return this.lineFromLineBuffer(charbuffer);
    }
    
    private int lineFromLineBuffer(final CharArrayBuffer charbuffer) throws IOException {
        int len = this.lineBuffer.length();
        if (len > 0) {
            if (this.lineBuffer.byteAt(len - 1) == 10) {
                --len;
            }
            if (len > 0 && this.lineBuffer.byteAt(len - 1) == 13) {
                --len;
            }
        }
        if (this.decoder == null) {
            charbuffer.append(this.lineBuffer, 0, len);
        }
        else {
            final ByteBuffer bbuf = ByteBuffer.wrap(this.lineBuffer.buffer(), 0, len);
            len = this.appendDecoded(charbuffer, bbuf);
        }
        this.lineBuffer.clear();
        return len;
    }
    
    private int lineFromReadBuffer(final CharArrayBuffer charbuffer, final int position) throws IOException {
        int pos = position;
        final int off = this.bufferPos;
        this.bufferPos = pos + 1;
        if (pos > off && this.buffer[pos - 1] == 13) {
            --pos;
        }
        int len = pos - off;
        if (this.decoder == null) {
            charbuffer.append(this.buffer, off, len);
        }
        else {
            final ByteBuffer bbuf = ByteBuffer.wrap(this.buffer, off, len);
            len = this.appendDecoded(charbuffer, bbuf);
        }
        return len;
    }
    
    private int appendDecoded(final CharArrayBuffer charbuffer, final ByteBuffer bbuf) throws IOException {
        if (!bbuf.hasRemaining()) {
            return 0;
        }
        if (this.cbuf == null) {
            this.cbuf = CharBuffer.allocate(1024);
        }
        this.decoder.reset();
        int len = 0;
        while (bbuf.hasRemaining()) {
            final CoderResult result = this.decoder.decode(bbuf, this.cbuf, true);
            len += this.handleDecodingResult(result, charbuffer, bbuf);
        }
        final CoderResult result = this.decoder.flush(this.cbuf);
        len += this.handleDecodingResult(result, charbuffer, bbuf);
        this.cbuf.clear();
        return len;
    }
    
    private int handleDecodingResult(final CoderResult result, final CharArrayBuffer charbuffer, final ByteBuffer bbuf) throws IOException {
        if (result.isError()) {
            result.throwException();
        }
        this.cbuf.flip();
        final int len = this.cbuf.remaining();
        while (this.cbuf.hasRemaining()) {
            charbuffer.append(this.cbuf.get());
        }
        this.cbuf.compact();
        return len;
    }
    
    @Override
    public String readLine() throws IOException {
        final CharArrayBuffer charbuffer = new CharArrayBuffer(64);
        final int readLen = this.readLine(charbuffer);
        return (readLen != -1) ? charbuffer.toString() : null;
    }
    
    @Override
    public boolean isDataAvailable(final int timeout) throws IOException {
        return this.hasBufferedData();
    }
    
    @Override
    public HttpTransportMetrics getMetrics() {
        return this.metrics;
    }
}
