package org.apache.coyote.http2;

import org.apache.tomcat.util.buf.ByteChunk;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.coyote.http11.OutputFilter;
import org.apache.coyote.Response;
import org.apache.coyote.http11.HttpOutputBuffer;

public class Http2OutputBuffer implements HttpOutputBuffer
{
    private final Response coyoteResponse;
    private HttpOutputBuffer next;
    
    public void addFilter(final OutputFilter filter) {
        filter.setBuffer(this.next);
        this.next = filter;
    }
    
    public Http2OutputBuffer(final Response coyoteResponse, final Stream.StreamOutputBuffer streamOutputBuffer) {
        this.coyoteResponse = coyoteResponse;
        this.next = streamOutputBuffer;
    }
    
    @Override
    public int doWrite(final ByteBuffer chunk) throws IOException {
        if (!this.coyoteResponse.isCommitted()) {
            this.coyoteResponse.sendHeaders();
        }
        return this.next.doWrite(chunk);
    }
    
    @Override
    public long getBytesWritten() {
        return this.next.getBytesWritten();
    }
    
    @Override
    public void end() throws IOException {
        this.next.end();
    }
    
    @Override
    public void flush() throws IOException {
        this.next.flush();
    }
    
    @Deprecated
    @Override
    public int doWrite(final ByteChunk chunk) throws IOException {
        return this.next.doWrite(chunk);
    }
}
