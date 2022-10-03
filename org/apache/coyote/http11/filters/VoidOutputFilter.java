package org.apache.coyote.http11.filters;

import org.apache.coyote.Response;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.coyote.http11.HttpOutputBuffer;
import org.apache.coyote.http11.OutputFilter;

public class VoidOutputFilter implements OutputFilter
{
    private HttpOutputBuffer buffer;
    
    public VoidOutputFilter() {
        this.buffer = null;
    }
    
    @Deprecated
    @Override
    public int doWrite(final ByteChunk chunk) throws IOException {
        return chunk.getLength();
    }
    
    @Override
    public int doWrite(final ByteBuffer chunk) throws IOException {
        return chunk.remaining();
    }
    
    @Override
    public long getBytesWritten() {
        return 0L;
    }
    
    @Override
    public void setResponse(final Response response) {
    }
    
    @Override
    public void setBuffer(final HttpOutputBuffer buffer) {
        this.buffer = buffer;
    }
    
    @Override
    public void flush() throws IOException {
        this.buffer.flush();
    }
    
    @Override
    public void recycle() {
        this.buffer = null;
    }
    
    @Override
    public void end() throws IOException {
        this.buffer.end();
    }
}
