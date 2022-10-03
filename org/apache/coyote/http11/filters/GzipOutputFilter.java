package org.apache.coyote.http11.filters;

import org.apache.juli.logging.LogFactory;
import org.apache.coyote.Response;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.tomcat.util.buf.ByteChunk;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.coyote.http11.HttpOutputBuffer;
import org.apache.juli.logging.Log;
import org.apache.coyote.http11.OutputFilter;

public class GzipOutputFilter implements OutputFilter
{
    protected static final Log log;
    protected HttpOutputBuffer buffer;
    protected GZIPOutputStream compressionStream;
    protected final OutputStream fakeOutputStream;
    
    public GzipOutputFilter() {
        this.compressionStream = null;
        this.fakeOutputStream = new FakeOutputStream();
    }
    
    @Deprecated
    @Override
    public int doWrite(final ByteChunk chunk) throws IOException {
        if (this.compressionStream == null) {
            this.compressionStream = new GZIPOutputStream(this.fakeOutputStream, true);
        }
        this.compressionStream.write(chunk.getBytes(), chunk.getStart(), chunk.getLength());
        return chunk.getLength();
    }
    
    @Override
    public int doWrite(final ByteBuffer chunk) throws IOException {
        if (this.compressionStream == null) {
            this.compressionStream = new GZIPOutputStream(this.fakeOutputStream, true);
        }
        final int len = chunk.remaining();
        if (chunk.hasArray()) {
            this.compressionStream.write(chunk.array(), chunk.arrayOffset() + chunk.position(), len);
            chunk.position(chunk.position() + len);
        }
        else {
            final byte[] bytes = new byte[len];
            chunk.put(bytes);
            this.compressionStream.write(bytes, 0, len);
        }
        return len;
    }
    
    @Override
    public long getBytesWritten() {
        return this.buffer.getBytesWritten();
    }
    
    @Override
    public void flush() throws IOException {
        if (this.compressionStream != null) {
            try {
                if (GzipOutputFilter.log.isDebugEnabled()) {
                    GzipOutputFilter.log.debug((Object)"Flushing the compression stream!");
                }
                this.compressionStream.flush();
            }
            catch (final IOException e) {
                if (GzipOutputFilter.log.isDebugEnabled()) {
                    GzipOutputFilter.log.debug((Object)"Ignored exception while flushing gzip filter", (Throwable)e);
                }
            }
        }
        this.buffer.flush();
    }
    
    @Override
    public void setResponse(final Response response) {
    }
    
    @Override
    public void setBuffer(final HttpOutputBuffer buffer) {
        this.buffer = buffer;
    }
    
    @Override
    public void end() throws IOException {
        if (this.compressionStream == null) {
            this.compressionStream = new GZIPOutputStream(this.fakeOutputStream, true);
        }
        this.compressionStream.finish();
        this.compressionStream.close();
        this.buffer.end();
    }
    
    @Override
    public void recycle() {
        this.compressionStream = null;
    }
    
    static {
        log = LogFactory.getLog((Class)GzipOutputFilter.class);
    }
    
    protected class FakeOutputStream extends OutputStream
    {
        protected final ByteBuffer outputChunk;
        
        protected FakeOutputStream() {
            this.outputChunk = ByteBuffer.allocate(1);
        }
        
        @Override
        public void write(final int b) throws IOException {
            this.outputChunk.put(0, (byte)(b & 0xFF));
            GzipOutputFilter.this.buffer.doWrite(this.outputChunk);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            GzipOutputFilter.this.buffer.doWrite(ByteBuffer.wrap(b, off, len));
        }
        
        @Override
        public void flush() throws IOException {
        }
        
        @Override
        public void close() throws IOException {
        }
    }
}
