package org.bouncycastle.openssl;

import java.io.IOException;

public class PEMException extends IOException
{
    Exception underlying;
    
    public PEMException(final String s) {
        super(s);
    }
    
    public PEMException(final String s, final Exception underlying) {
        super(s);
        this.underlying = underlying;
    }
    
    public Exception getUnderlyingException() {
        return this.underlying;
    }
    
    @Override
    public Throwable getCause() {
        return this.underlying;
    }
}
