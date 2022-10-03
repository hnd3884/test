package org.bouncycastle.dvcs;

public class DVCSParsingException extends DVCSException
{
    private static final long serialVersionUID = -7895880961377691266L;
    
    public DVCSParsingException(final String s) {
        super(s);
    }
    
    public DVCSParsingException(final String s, final Throwable t) {
        super(s, t);
    }
}
