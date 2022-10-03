package org.bouncycastle.cms;

import java.io.IOException;

public class CMSStreamException extends IOException
{
    private final Throwable underlying;
    
    CMSStreamException(final String s) {
        super(s);
        this.underlying = null;
    }
    
    CMSStreamException(final String s, final Throwable underlying) {
        super(s);
        this.underlying = underlying;
    }
    
    @Override
    public Throwable getCause() {
        return this.underlying;
    }
}
