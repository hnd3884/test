package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.util.Localizable;

public class DimensionMismatchException extends MathIllegalNumberException
{
    private static final long serialVersionUID = -8415396756375798143L;
    private final int dimension;
    
    public DimensionMismatchException(final Localizable specific, final int wrong, final int expected) {
        super(specific, wrong, new Object[] { expected });
        this.dimension = expected;
    }
    
    public DimensionMismatchException(final int wrong, final int expected) {
        this(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, wrong, expected);
    }
    
    public int getDimension() {
        return this.dimension;
    }
}
