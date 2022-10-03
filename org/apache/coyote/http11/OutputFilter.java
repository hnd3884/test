package org.apache.coyote.http11;

import org.apache.coyote.Response;

public interface OutputFilter extends HttpOutputBuffer
{
    void setResponse(final Response p0);
    
    void recycle();
    
    void setBuffer(final HttpOutputBuffer p0);
}
