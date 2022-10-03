package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class NumberIsTooLargeException extends MathIllegalNumberException
{
    private static final long serialVersionUID = 4330003017885151975L;
    private final Number max;
    private final boolean boundIsAllowed;
    
    public NumberIsTooLargeException(final Number wrong, final Number max, final boolean boundIsAllowed) {
        this(boundIsAllowed ? LocalizedFormats.NUMBER_TOO_LARGE : LocalizedFormats.NUMBER_TOO_LARGE_BOUND_EXCLUDED, wrong, max, boundIsAllowed);
    }
    
    public NumberIsTooLargeException(final Localizable specific, final Number wrong, final Number max, final boolean boundIsAllowed) {
        super(specific, wrong, new Object[] { max });
        this.max = max;
        this.boundIsAllowed = boundIsAllowed;
    }
    
    public boolean getBoundIsAllowed() {
        return this.boundIsAllowed;
    }
    
    public Number getMax() {
        return this.max;
    }
}
