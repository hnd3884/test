package org.owasp.validator.css;

import org.owasp.validator.html.ScanException;

public class UnknownSelectorException extends ScanException
{
    private final String selectorName;
    
    public UnknownSelectorException(final String selectorName) {
        super("Unknown selector " + selectorName);
        this.selectorName = selectorName;
    }
    
    public String getSelectorName() {
        return this.selectorName;
    }
}
