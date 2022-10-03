package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class NoBracketingException extends MathIllegalArgumentException
{
    private static final long serialVersionUID = -3629324471511904459L;
    private final double lo;
    private final double hi;
    private final double fLo;
    private final double fHi;
    
    public NoBracketingException(final double lo, final double hi, final double fLo, final double fHi) {
        this(LocalizedFormats.SAME_SIGN_AT_ENDPOINTS, lo, hi, fLo, fHi, new Object[0]);
    }
    
    public NoBracketingException(final Localizable specific, final double lo, final double hi, final double fLo, final double fHi, final Object... args) {
        super(specific, new Object[] { lo, hi, fLo, fHi, args });
        this.lo = lo;
        this.hi = hi;
        this.fLo = fLo;
        this.fHi = fHi;
    }
    
    public double getLo() {
        return this.lo;
    }
    
    public double getHi() {
        return this.hi;
    }
    
    public double getFLo() {
        return this.fLo;
    }
    
    public double getFHi() {
        return this.fHi;
    }
}
