package org.bouncycastle.dvcs;

public class DVCSConstructionException extends DVCSException
{
    private static final long serialVersionUID = 660035299653583980L;
    
    public DVCSConstructionException(final String s) {
        super(s);
    }
    
    public DVCSConstructionException(final String s, final Throwable t) {
        super(s, t);
    }
}
