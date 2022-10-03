package org.owasp.validator.html;

public class ScanException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public ScanException(final Exception e) {
        super(e);
    }
    
    public ScanException(final String s) {
        super(s);
    }
}
