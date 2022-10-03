package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class NumberIsTooSmallException extends MathIllegalNumberException
{
    private static final long serialVersionUID = -6100997100383932834L;
    private final Number min;
    private final boolean boundIsAllowed;
    
    public NumberIsTooSmallException(final Number wrong, final Number min, final boolean boundIsAllowed) {
        this(boundIsAllowed ? LocalizedFormats.NUMBER_TOO_SMALL : LocalizedFormats.NUMBER_TOO_SMALL_BOUND_EXCLUDED, wrong, min, boundIsAllowed);
    }
    
    public NumberIsTooSmallException(final Localizable specific, final Number wrong, final Number min, final boolean boundIsAllowed) {
        super(specific, wrong, new Object[] { min });
        this.min = min;
        this.boundIsAllowed = boundIsAllowed;
    }
    
    public boolean getBoundIsAllowed() {
        return this.boundIsAllowed;
    }
    
    public Number getMin() {
        return this.min;
    }
}
