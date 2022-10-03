package org.bouncycastle.cms;

public class CMSAttributeTableGenerationException extends CMSRuntimeException
{
    Exception e;
    
    public CMSAttributeTableGenerationException(final String s) {
        super(s);
    }
    
    public CMSAttributeTableGenerationException(final String s, final Exception e) {
        super(s);
        this.e = e;
    }
    
    @Override
    public Exception getUnderlyingException() {
        return this.e;
    }
    
    @Override
    public Throwable getCause() {
        return this.e;
    }
}
