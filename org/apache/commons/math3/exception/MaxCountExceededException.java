package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class MaxCountExceededException extends MathIllegalStateException
{
    private static final long serialVersionUID = 4330003017885151975L;
    private final Number max;
    
    public MaxCountExceededException(final Number max) {
        this(LocalizedFormats.MAX_COUNT_EXCEEDED, max, new Object[0]);
    }
    
    public MaxCountExceededException(final Localizable specific, final Number max, final Object... args) {
        this.getContext().addMessage(specific, max, args);
        this.max = max;
    }
    
    public Number getMax() {
        return this.max;
    }
}
