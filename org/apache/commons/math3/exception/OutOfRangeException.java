package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class OutOfRangeException extends MathIllegalNumberException
{
    private static final long serialVersionUID = 111601815794403609L;
    private final Number lo;
    private final Number hi;
    
    public OutOfRangeException(final Number wrong, final Number lo, final Number hi) {
        this(LocalizedFormats.OUT_OF_RANGE_SIMPLE, wrong, lo, hi);
    }
    
    public OutOfRangeException(final Localizable specific, final Number wrong, final Number lo, final Number hi) {
        super(specific, wrong, new Object[] { lo, hi });
        this.lo = lo;
        this.hi = hi;
    }
    
    public Number getLo() {
        return this.lo;
    }
    
    public Number getHi() {
        return this.hi;
    }
}
