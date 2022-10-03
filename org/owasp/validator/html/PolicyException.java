package org.owasp.validator.html;

public class PolicyException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public PolicyException(final Exception e) {
        super(e);
    }
    
    public PolicyException(final String string) {
        super(string);
    }
}
